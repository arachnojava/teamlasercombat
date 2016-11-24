package tlc.net.server;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import mhframework.MHActor;
import mhframework.MHRandom;
import mhframework.io.MHFileFilter;
import mhframework.io.MHLogFile;
import mhframework.io.MHTextFile;
import mhframework.io.net.MHMessageType;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.server.MHAbstractServer;
import mhframework.io.net.server.MHClientInfo;
import mhframework.io.net.server.MHClientList;
import mhframework.io.net.server.MHGameServer;
import mhframework.tilemap.MHMapCell;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHTileMapDirection;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.data.TLCTeam;
import tlc.data.TLCTokenData;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCUpgradeItem;
import tlc.data.world.TLCContainer;
import tlc.data.world.TLCGameWorld;
import tlc.data.world.TLCPickupItem;
import tlc.net.TLCAttackMessage;
import tlc.net.TLCCharacterMoveMessage;
import tlc.net.TLCCombatInteractionMessage;
import tlc.net.TLCEventLogMessage;
import tlc.net.TLCMessageType;
import tlc.net.server.ai.TLCAIManager;
import tlc.net.server.tokens.TLCTokenSource;

public class TLCGameServer implements MHGameServer
{
    //public static final long DATA_ID = TLCDataFacade.generateDataID();
    final static TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);

    private static final int LOBBY_STATE = 0;
    private static final int GAME_STATE = 1;
    private int gameState = LOBBY_STATE;

    private static final int TOKEN_ID = -999;
    
    private static MHTextFile logFile;
    private static ClientLogs clientLogs;
    public static MHAbstractServer server;
    private TLCTokenSource tokenSource;
    private boolean isGameStarted = false;
    private static TLCGameWorld gameWorld;
    private TLCAIManager aiManager;
    private static ArrayList<Integer> playOrder = null;
    private static int currentPlayerIndex;
    private int nextCharacterID = -1;
    private boolean allReady = false;

    private TLCCombatInteractionMessage combatInteraction;

    public TLCGameServer()
    {
        logFile = new MHLogFile("logs/SERVERLOG.TXT");
        clientLogs = new ClientLogs();

        /*
        new Thread()
        {
            public void run()
            {
                while (!TLCDataFacade.isGameOver())
                {
                    placeCharactersOnMap();
                    broadcastCharacterUpdates();
                    try
                    {
                        Thread.sleep(5000);
                    } catch (InterruptedException e)
                    {
                    }
                }
            }
        }.start();
         */
    }


    private static void determinePlayOrder()
    {
        //  List all the player IDs.
        int[] teamIDs = TLCDataFacade.getTeamList().getTeamIDs();

        // Shuffle the players.
        for (int i = 0; i < teamIDs.length; i++)
        {
            int swapWith = MHRandom.random(0, teamIDs.length-1);
            int temp = teamIDs[swapWith];
            teamIDs[swapWith] = teamIDs[i];
            teamIDs[i] = temp;
        }

        playOrder = new ArrayList<Integer>();
        for (int i = 0; i < teamIDs.length; i++)
            playOrder.add(teamIDs[i]);

        // Select who goes first.
        currentPlayerIndex = MHRandom.random(0, playOrder.size()-1);
    }

    
    private static ArrayList<Integer> getPlayOrder()
    {
        if (playOrder == null)
            determinePlayOrder();
        
        return playOrder;
    }

    public static void nextPlayerTurn()
    {
        int playerIndex = (currentPlayerIndex + 1) % getPlayOrder().size();
        TLCDataFacade.setWhoseTurn(getPlayOrder().get(playerIndex));
        
        serverLog("nextPlayerTurn(): " + TLCDataFacade.getWhoseTurn());
        
        broadcastWhoseTurn(playerIndex);
    }


    public static void serverLog(String message)
    {
        logFile.append(message);
    }


    public void clientLog(MHClientInfo client, String message)
    {
        // Log the message only if the client is a player and not a spectator.
        if (data.isPlayer(client))
            clientLogs.log(client.id, message);
    }


    void broadcastUserRoles()
    {
        // for each user...
        for (MHClientInfo client : server.getClientList())
        {
            TLCPlayerDescriptor user = data.getUser(client.id);
            MHNetworkMessage msg = new MHNetworkMessage(TLCMessageType.UPDATE_PLAYER_DATA, user);
            server.sendToAll(msg);
        }
    }

    
    private void drawToken(int clientID, int charID)
    {
        TLCCharacterType type = TLCDataFacade.getCharacterList().get(charID).getType();
        drawToken(clientID, type);
    }

    
    private void drawToken(int clientID, TLCCharacterType type)
    {
        if (tokenSource == null)
            tokenSource = new TLCTokenSource(TLCDataFacade.getTeam(clientID));
        
        TLCTokenData token = tokenSource.drawToken(type);

        MHNetworkMessage tokenMsg = new MHNetworkMessage(TLCMessageType.DRAW_TOKEN, token);
        server.send(clientID, tokenMsg);
    }

    
    // FIXME: This method sometimes makes single player a spectator.
    private TLCPlayerMode validateUserRole(TLCPlayerDescriptor user, TLCPlayerMode type)
    {
        // Does the user want to be a player?
        boolean isPlayer = (type.equals(TLCPlayerMode.HOST_LAN) || type.equals(TLCPlayerMode.JOIN_LAN));

        if (!isPlayer)  // If not a LAN player, then no validation is necessary.
            return type;
        else if (isGameStarted)  // If already in game state, no new players allowed.
            return TLCPlayerMode.SPECTATOR;
        else
        {
            // If we haven't reached the max number of players, then 
            // we can allow another player.
            int currentPlayers = data.countPlayers();
            int totalPlayers = data.getNumHumansSelected();

            System.out.println("Current Players: " + currentPlayers + "\tTotal Players Selected: " + totalPlayers);

            if (currentPlayers < totalPlayers)
                return type;
        }

        return TLCPlayerMode.SPECTATOR;
    }


    /**
     * Handles messages that are processed the same way regardless of server state.
     */
    @Override
    public void receiveMessage(final MHNetworkMessage message,
            final MHAbstractServer server)
    {
        final String msgType = message.getMessageType();
        final int id = message.getSender();
        MHClientInfo sender = server.getClientList().get(id);
        TLCGameServer.server = server;

        String log;
        // DEBUG
        if (sender != null)
        {
        log = "TLCGameServer.receiveMessage(" + sender.name+"["+id+"], " + msgType + ", server)";
        serverLog(log);
        clientLog(sender, log);
        }
        if (msgType.equals(TLCMessageType.ASSIGN_CLIENT_ID))
        {
            data.addUser(sender);
            boolean g = (gameState == GAME_STATE);
            server.sendToAll(new MHNetworkMessage(TLCMessageType.BROADCAST_GAME_STATE, g));
        }
        else if (msgType.equals(TLCMessageType.REGISTER_NAME))
        {
            TLCDataFacade.getUser(sender.id).name = (String) message.getPayload(); 
        }
        else if (msgType.equals(TLCMessageType.REGISTER_USER_TYPE))
        {
            TLCPlayerDescriptor user = TLCDataFacade.getUser(sender.id);
            TLCPlayerMode type = (TLCPlayerMode) message.getPayload();

            // If user is a player, make sure we still have room for
            // them.  Otherwise, make them a spectator.
            user.type = validateUserRole(user, type);

            if (!TLCDataFacade.isHumanPlayer(sender.id))
                user.ready = true;

            log = msgType + ":  " + TLCDataFacade.getUser(sender.id).name + " (ID:" + sender.id + ") is a " + TLCDataFacade.getUser(sender.id).type.toString(); 
            serverLog(log);
            clientLog(sender, log);

            MHNetworkMessage msg = new MHNetworkMessage(TLCMessageType.REGISTER_USER_TYPE, user);
            server.send(sender.id, msg);

            msg = new MHNetworkMessage(TLCMessageType.GAME_OPTIONS, TLCDataFacade.getInstance(TLCMain.DATA_ID).getGameOptions().getSerializableVersion());
            server.send(sender.id, msg);
            
            //broadcastUserRoles();
            //broadcastCharacterUpdates();
        }
        else if (msgType.equals(TLCMessageType.UPDATE_TEAM))
        {
            final TLCTeam newTeam = data.addTeam(message.getPayload(), id);
            server.sendToAll(new MHNetworkMessage(TLCMessageType.UPDATE_TEAM, newTeam.getSerializableVersion()));
            log = msgType + ":  Team " + newTeam.getTeamName() + " updated for player " + TLCDataFacade.getUser(id).name;
            serverLog(log);
            clientLog(sender, log);

            // If all the human players are connected, make sure the
            // AI players are ready to go.
            // BUT...don't init AI until all human players have created their teams.
            if (TLCDataFacade.getTeamList().size() >= data.getNumHumansSelected() && data.countHumanPlayers() >= data.getNumHumansSelected())
                initAI();
        }
        else if (msgType.equals(TLCMessageType.UPDATE_CHARACTER))
        {
            //if (finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(sender.id)))
            if (finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn())))
                nextPlayerTurn();

            final TLCCharacter character = TLCCharacter.deserialize(message.getPayload());
            TLCDataFacade.getCharacterList().add(character);
            MHNetworkMessage m = new MHNetworkMessage(TLCMessageType.UPDATE_CHARACTER, character.getSerializableVersion());
            //server.sendToAll(new MHNetworkMessage(TLCMessageType.UPDATE_CHARACTER, character.getSerializableVersion()));
            sendToHumans(m, server);
            log = msgType + ":" + character.toString();
            serverLog(log);
            clientLog(sender, log);
            
//            if (TLCDataFacade.getGameWorld() != null && character.getMapLocation() != null)
//                TLCDataFacade.getGameWorld().putObject(character, character.getMapLocation(), MHMapCell.WALL_LAYER);

            //            serverLog("DEBUG(TLCGameServer.receiveMessage->UPDATE_CHARACTER):====    CHARACTERS:    ====");
            //            for (TLCCharacter c : TLCDataFacade.getCharacterList())
            //            {
            //                serverLog(c.toString());
            //                if (TLCDataFacade.getGameWorld() != null && c.getMapLocation() != null)
            //                    TLCDataFacade.getGameWorld().putObject(c, c.getMapLocation(), MHMapCell.WALL_LAYER);
            //            }
        }
        /*=========================================================*/
//        else if (msgType.equals(TLCMessageType.GAME_OPTIONS))
//        {
//            TLCGameOptions o = (TLCGameOptions)message.getPayload();
//            TLCDataFacade.setGameOptions(o);
//        }
        /*=========================================================*/
        else if (msgType.equals(TLCMessageType.RETIRE_CHARACTER))
        {
            final TLCCharacter character = TLCCharacter.deserialize(message.getPayload());
            server.sendToAll(new MHNetworkMessage(TLCMessageType.RETIRE_CHARACTER, character.getSerializableVersion()));

            log = msgType + ":  " + character.getType().getTitle() + " " +  character.getName() + " retired.";
            serverLog(log);
            clientLog(sender, log);

            TLCTeam t = TLCDataFacade.getTeam(id);
            t.retireCharacter(character);

            server.send(id, new MHNetworkMessage(TLCMessageType.UPDATE_TEAM, t.getSerializableVersion()));
        }
        /*=========================================================*/
        else if (msgType.equals(TLCMessageType.RECRUIT_CHARACTER))
        {
            final TLCCharacter character = TLCCharacter.deserialize(message.getPayload());
            character.setTeam(id);
            character.setCharacterID(++nextCharacterID);
            TLCDataFacade.getCharacterList().add(character);
            server.sendToAll(new MHNetworkMessage(TLCMessageType.RECRUIT_CHARACTER, character.getSerializableVersion()));
            log = msgType + ":" + character.toString();
            serverLog(log);
            clientLog(sender, log);
            
            placeCharactersOnMap();

            TLCTeam t = TLCDataFacade.getTeam(id);
            t.adjustCoins(-character.getType().getCost());
            server.send(sender.id, new MHNetworkMessage(TLCMessageType.UPDATE_TEAM, t.getSerializableVersion()));
        }
        /*=========================================================*/
        else if (msgType.equals(TLCMessageType.DEFEND))
        {
            getCombatInteraction().defendToken = (TLCTokenData) message.getPayload();
            
            // Send characters and tokens to combat evaluator.
            TLCCombatProcessor.resolveCombat(getCombatInteraction());
            broadcastCharacterUpdates();
            TLCAIManager.isAttacking = false;
            
            if (finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn())))
                nextPlayerTurn();
        }
        /*=========================================================*/
        else if (msgType.equals(TLCMessageType.ATTACK))
        {
            TLCAttackMessage msg = (TLCAttackMessage) message.getPayload();
            TLCCharacter attacker = TLCDataFacade.getCharacterList().get(msg.getAttackingCharacterID());
            
            attacker.setActionPoints(attacker.getActionPoints()-1);

            TLCAttackNotification notify = new TLCAttackNotification();
            notify.attackerID = msg.getAttackingCharacterID();
            
            int targetID = findTarget(attacker.getTeamID(), attacker.getMapLocation(), msg.getDirection());
            if (targetID == TOKEN_ID)
            {
                String text = attacker.getName() + " destroyed a container.";
                TLCEventLogMessage event = new TLCEventLogMessage(attacker.getCharacterID(), text);
                MHNetworkMessage elm = new MHNetworkMessage(TLCMessageType.EVENT_LOG, event);
                server.sendToAll(elm);
            }
            else
            {
            notify.defenderID = targetID;
            TLCCharacter defender = TLCDataFacade.getCharacterList().get(notify.defenderID);

            // Store combat interaction data.
            getCombatInteraction().attackerID = notify.attackerID;
            getCombatInteraction().defenderID = notify.defenderID;
            getCombatInteraction().attackToken = msg.getAttackToken();
            
            // Send Attack notification.
            MHNetworkMessage attackNotifyMsg = new MHNetworkMessage();
            attackNotifyMsg.setMessageType(TLCMessageType.ATTACK_NOTIFICATION);
            attackNotifyMsg.setPayload(notify);
            server.send(defender.getTeamID(), attackNotifyMsg);

            // Send Event Log update.
            //TLCTeam at = TLCDataFacade.getTeam(attacker.getTeamID());
            //TLCTeam dt = TLCDataFacade.getTeam(defender.getTeamID());
            String text = attacker.getName() + " attacked " 
                        + (defender==null?"someone":defender.getName()+".");
            TLCEventLogMessage event = new TLCEventLogMessage(attacker.getCharacterID(), text);
            MHNetworkMessage elm = new MHNetworkMessage(TLCMessageType.EVENT_LOG, event);
            //sendToHumans(elm, server);
            server.sendToAll(elm);
            }
            
        }
        /*=========================================================*/
        else if (msgType.equals(TLCMessageType.DRAW_TOKEN))
        {
            TLCCharacter character = null;
            
            Serializable payload = message.getPayload();
            // If the payload is a character type, then we're drawing
            // initial tokens before a match.  Otherwise, it's a 
            // character drawing tokens as an action during its turn.
            if (payload instanceof TLCCharacterType)
            {
                drawToken(sender.id, (TLCCharacterType)payload);
            }
            else if (TLCDataFacade.getWhoseTurn() == sender.id)
            {
                int charID = ((Integer)payload).intValue();
                drawToken(sender.id, charID);
                
                character = TLCDataFacade.getCharacterList().get(charID);
                
                if (character != null)
                {
                    character.setActionPoints(character.getActionPoints()-1);

                    TLCTeam t = TLCDataFacade.getTeam(character.getTeamID());
                    String text = t.getTeamName() + " " + character.getType().getTitle() + " " + character.getName() + " drew a token.";
                    TLCEventLogMessage event = new TLCEventLogMessage(character.getCharacterID(), text);
                    MHNetworkMessage elm = new MHNetworkMessage(TLCMessageType.EVENT_LOG, event);
                    //sendToHumans(elm, server);
                    server.sendToAll(elm);
                }

                //if (finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(sender.id)))
                if (finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn())))
                        nextPlayerTurn();
            }
        }
        /*=========================================================*/
        else if (msgType.equals(MHMessageType.DISCONNECT))
        {
            // If the disconnected user was a player, create a new AI for their team.
            aiManager.replaceHumanPlayer(sender);
            data.removeUser(sender.id);
        }
        /*=========================================================*/
        else if (msgType.equals(TLCMessageType.REQUEST_MOVE_POINTS))
        {
            // Make sure it's this player's turn.
            if (TLCDataFacade.getWhoseTurn() != sender.id)
                return;
            
            TLCCharacter c = TLCCharacter.deserialize(message.getPayload());
            int mp = (int)Math.floor(c.getMovementValue());
            double remainder = c.getMovementValue() - mp;
            if (Math.random() > remainder)
                mp += 1;
            
            mp += rollMovementDie();

            serverLog("Assigning " + mp + " movement points to " + c.getName());
            System.out.println("Assigning " + mp + " movement points to " + c.getName());
            
            message.setPayload(new Integer(mp));
            server.send(sender.id, message);
        }
        /*=========================================================*/
        else if (message.getMessageType().equals(TLCMessageType.CHARACTER_MOVE))
        {
            placeCharactersOnMap();
            
            // Make sure it's this player's turn.
            if (TLCDataFacade.getWhoseTurn() != sender.id)
                return;
            
            TLCCharacterMoveMessage msg = (TLCCharacterMoveMessage)message.getPayload();

            // Remove character from board.
            TLCCharacter localGuy = TLCDataFacade.getCharacterList().get(msg.getCharID());
            MHMapCellAddress a = localGuy.getMapLocation();
            TLCDataFacade.getGameWorld().putObject(null, a, MHMapCell.WALL_LAYER);
            
            //TLCDataFacade.getCharacterList().add(c);
            localGuy.walkTo(new MHMapCellAddress(msg.getNewRow(), msg.getNewCol()));
            
//            TLCDataFacade.getGameWorld().putObject(c, c.getMapLocation(), MHMapCell.WALL_LAYER);
//            server.sendToAll(message);
            
            // Deduct one action point from the character who moved.
            localGuy.setActionPoints(localGuy.getActionPoints()-1);
            
//            message.setMessageType(TLCMessageType.UPDATE_CHARACTER);
//            message.setPayload(c.getSerializableVersion());
//            server.send(sender.id, message);

            TLCTeam t = TLCDataFacade.getTeam(localGuy.getTeamID());
            String text = t.getTeamName() + " " + localGuy.getType().getTitle() + " " + localGuy.getName() + " moved.";
            TLCEventLogMessage event = new TLCEventLogMessage(localGuy.getCharacterID(), text);
            MHNetworkMessage elm = new MHNetworkMessage(TLCMessageType.EVENT_LOG, event);
            //sendToHumans(elm, server);
            server.sendToAll(elm);

            //if (finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(sender.id)))
            if (finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn())))
                nextPlayerTurn();
        }
        /*=========================================================*/
        else if (msgType.equals(TLCMessageType.SIGNAL_READY))
        {

            if (!allReady)
            {
                boolean ready = ((Boolean) message.getPayload()).booleanValue();
                TLCDataFacade.getUser(sender.id).ready = ready;
                broadcastUserRoles();
            }
            // If all players are connected and ready, go into the game.
            //if (data.countReadyPlayers() >= data.countHumanPlayers() && data.countHumanPlayers() == data.getNumHumansSelected())
            if (data.countReadyPlayers() == data.getNumAISelected() + data.getNumHumansSelected())
                //if (humansReady() && data.countPlayers() == data.getNumAISelected() + data.getNumHumansSelected())
            {
                allReady = true;
                server.sendToAll(new MHNetworkMessage(TLCMessageType.BROADCAST_GAME_STATE, true));

                String mapFile = selectMap();
                server.sendToAll(new MHNetworkMessage(TLCMessageType.BROADCAST_MAP_FILE, mapFile));
                
                // Load the server's master copy of the map.
                getGameWorld().loadMap(mapFile);
                clearCharacterLocations();
                placeCharactersOnMap();             
                broadcastCharacterUpdates();
                broadcastWhoseTurn(currentPlayerIndex);
            }
        }
    }

    
    private TLCCombatInteractionMessage getCombatInteraction()
    {
        if (combatInteraction == null)
            combatInteraction = new TLCCombatInteractionMessage();
        
        return combatInteraction;
    }
    
    
    private int findTarget(int teamID, MHMapCellAddress start, MHTileMapDirection dir)
    {
        MHMapCellAddress addr = start;
        
        if (addr == null)
            return -1;
        
        do
        {
            addr = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, dir);
        }
        while (TLCDataFacade.getGameWorld().canWalkOn(addr.row, addr.column));
        // TODO: That should be "canShootOver".
        
        MHActor target = TLCDataFacade.getGameWorld().getMap().getMapData().getMapCell(addr.row, addr.column).getLayer(MHMapCell.WALL_LAYER);
        if (target instanceof TLCCharacter)
        {
            TLCCharacter c = (TLCCharacter) target;
            
            // If the character is on our team, it's not a target.
            if (c.getTeamID() == teamID)
                return -1;
            
            return c.getCharacterID();
        }
        else if (target instanceof TLCContainer) // Recognize containers as targets.
        {
            TLCContainer c = (TLCContainer) target;
            TLCUpgradeItem item = c.getItem();
            
            TLCDataFacade.getGameWorld().getMap().getMapData().getMapCell(addr.row, addr.column).setLayer(MHMapCell.WALL_LAYER, null);
            TLCDataFacade.getGameWorld().getMap().getMapData().getMapCell(addr.row, addr.column).setLayer(MHMapCell.ITEM_LAYER, new TLCPickupItem(item));
            
            return TLCGameServer.TOKEN_ID;
        }
        return -1;
    }


    private void sendToHumans(MHNetworkMessage msg, MHAbstractServer server)
    {
        MHClientList clients = server.getClientList();
        for (int i = 0; i < clients.size(); i++)
        {
            MHClientInfo client = clients.get(i);
            if (client != null && TLCDataFacade.isHumanPlayer(client.id) && TLCDataFacade.getUser(client.id).type.equals(TLCPlayerMode.JOIN_LAN))
                client.send(msg);
        }
    }


    private static void broadcastWhoseTurn(int whoseTurnIndex)
    {
//        if (whoseTurnIndex == currentPlayerIndex)
//            return; 
        
        currentPlayerIndex = whoseTurnIndex;
        TLCDataFacade.setWhoseTurn(getPlayOrder().get(currentPlayerIndex));

        // Reset action points to full.
        TLCDataFacade.resetActionPoints();
        
        // TODO: Tell network clients whose turn it is.
        //MHNetworkMessage msg = new MHNetworkMessage();
        //msg.setMessageType(TLCMessageType.WHOSE_TURN);
        //msg.setPayload(TLCDataFacade.getWhoseTurn());
        //server.sendToAll(msg);
    }

       
    public static boolean finishedTurn(TLCCharacterList tlcCharacterList)
    {
        for (TLCCharacter c : tlcCharacterList)
        {
            // DEBUG:
            //System.out.println(c.getName() + " has " + c.getActionPoints() + " actions left.");
            if (c.getActionPoints() > 0)
                return false;
        }
        
        return true;
    }

    
    private int rollMovementDie()
    {
        return MHRandom.rollD4();
    }


    public String selectMap()
    {
        // List all .LIME map files.
        File[] maps = MHFileFilter.listFiles(".", ".lime");
        
        // Pick one at random.
        int rand = MHRandom.random(0, maps.length-1);
        return maps[rand].getName();
    }


    public TLCGameWorld getGameWorld()
    {
        if (gameWorld == null)
            gameWorld = new TLCGameWorld();

        return gameWorld;
    }


    public void initAI()
    {
        if (aiManager == null)
        {
            //new Thread(new Runnable(){
            //    public void run()
            //    {
                    aiManager = new TLCAIManager();
                    aiManager.createAIPlayers();
                    //aiManager.setThreadID(MHThreadManager.getInstance().createThread(aiManager, "TLCAIManager"));
                    //MHThreadManager.getInstance().start(aiManager.getThreadID());
            //    }
            //}).start();
        }
    }


    public static String getServerIP()
    {
        return MHAbstractServer.getIPAddress();
    }


    public synchronized static void placeCaptain(TLCCharacter c)
    {
        if (gameWorld == null) return;

        c.setMapLocation(gameWorld.chooseSpawnPoint());
        gameWorld.putObject(c, c.getMapLocation(), MHMapCell.WALL_LAYER);
//        gameWorld.putFineObject(c);
        serverLog("Placing " + c.getName() + " at " + c.getMapLocation());
    }


    public void broadcastCharacterUpdates()
    {
        MHNetworkMessage msg = new MHNetworkMessage();
        msg.setMessageType(TLCMessageType.BROADCAST_CHARACTER_LIST);
        msg.setPayload(TLCDataFacade.getCharacterList().getSerializableVersion());
        sendToHumans(msg, server);
        
        /*
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
        {
            MHNetworkMessage msg = new MHNetworkMessage();
            msg.setMessageType(TLCMessageType.UPDATE_CHARACTER);
            msg.setPayload(c.getSerializableVersion());
            server.sendToAll(msg);
        }
        */
    }

    public void clearCharacterLocations()
    {
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
            c.setMapLocation(null);
    }

    public static void placeCharactersOnMap()
    {
        // Place captains.
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
        {
            if (c.getMapLocation() == null 
                    && c.getType().equals(TLCCharacterType.CAPTAIN)
                    && c.getHealth() > 0)
                placeCaptain(c);
        }

        // Place minor characters near their captains.
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
        {
            if (c.getMapLocation() == null 
                    && !c.getType().equals(TLCCharacterType.CAPTAIN)
                    && c.getHealth() > 0)
            {
                // Find captain for this team.
                TLCCharacter captain = null;
                for (TLCCharacter capt : TLCDataFacade.getCharacterList())
                    if (capt.getType().equals(TLCCharacterType.CAPTAIN) && capt.getTeamID() == c.getTeamID())
                    {
                        captain = capt;
                        if (captain.getMapLocation() == null)
                            placeCaptain(captain);
                    }

                if (captain == null || captain.getMapLocation() == null)
                {
                    System.err.println("ERROR in placeCharactersOnMap():  Captain misplaced!");
                    continue;
                }

                // Find open spaces near the captain.
                ArrayList<MHMapCellAddress> spawnPoints = new ArrayList<MHMapCellAddress>();
                final int row = captain.getMapLocation().row;
                final int col = captain.getMapLocation().column;
                int radius = 1;
                for (int dr = -radius; dr <= radius; dr++)
                    for (int dc = -radius; dc <= radius; dc++)
                    {
                        MHMapCellAddress address = new MHMapCellAddress(row+dr, col+dc);
                        if (gameWorld.canWalkOn(row + dr, col + dc) && !gameWorld.isSpawnPointUsed(address))
                            spawnPoints.add(address);
                    }

                // Randomly pick one of the open spaces.
                int s = MHRandom.random(0, spawnPoints.size()-1);
                c.setMapLocation(spawnPoints.get(s));
                serverLog("Placing " + c.getName() + " at " + c.getMapLocation());
                gameWorld.putObject(c, c.getMapLocation(), MHMapCell.WALL_LAYER);

//                                System.out.println("\nDEBUG(placeCharactersOnMap):====    SERVER CHARACTERS    ====");
//                                for (TLCCharacter ch : TLCDataFacade.getCharacterList())
//                                {
//                                    System.out.println(ch.toString());
//                                }
            }
        }

    }
}

class ClientLogs
{
    private final Hashtable<Integer, MHLogFile> logs;

    public ClientLogs()
    {
        logs = new Hashtable<Integer, MHLogFile>();
    }


    public void log(final int clientID, final String message)
    {
        if (!logs.containsKey(clientID))
            logs.put(clientID, new MHLogFile("logs/CLIENT"+clientID+"LOG.txt"));

        logs.get(clientID).append(message);
    }
}