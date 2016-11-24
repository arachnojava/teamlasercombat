package tlc.net.client;

import java.awt.Color;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import mhframework.MHRandom;
import mhframework.gui.MHGUIChatClient;
import mhframework.io.MHLogFile;
import mhframework.io.MHTextFile;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.MHSerializableClientList;
import mhframework.io.net.client.MHAbstractClient;
import mhframework.io.net.client.MHLocalClient;
import mhframework.io.net.client.MHNetworkClient;
import mhframework.io.net.client.MHObservableClient;
import mhframework.io.net.event.MHGameMessageListener;
import mhframework.io.net.event.MHSystemMessageListener;
import mhframework.io.net.server.MHAbstractServer;
import mhframework.io.net.server.MHServerModule;
import mhframework.tilemap.MHMapCell;
import mhframework.tilemap.MHMapCellAddress;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.TLCGameOptions;
import tlc.data.TLCPlayerMode;
import tlc.data.TLCTeam;
import tlc.data.TLCTeamColor;
import tlc.data.TLCToken;
import tlc.data.TLCTokenData;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterType;
import tlc.net.TLCCombatInteractionMessage;
import tlc.net.TLCEventLogMessage;
import tlc.net.TLCMessageType;
import tlc.net.server.TLCAttackNotification;
import tlc.net.server.TLCGameServer;
import tlc.net.server.TLCPlayerDescriptor;
import tlc.net.server.ai.TLCAIManager;
import tlc.ui.hud.TLCEventLogDisplay;
import tlc.ui.hud.actions.TLCActionsMenu;
import tlc.ui.screens.TLCGameScreen;

public class TLCGameClient implements MHGameMessageListener, MHSystemMessageListener
{
    // Thipi Thai: 630-469-9001
    private static final MHTextFile logFile = new MHLogFile("TLCGameClientLog.txt");
    private boolean isGameState = false;
    private static int instanceCount = 0;
    
    
    private MHObservableClient clientModule;

    private Thread messageThread;
    
    private static ConcurrentLinkedQueue<MHNetworkMessage> overflow;

    private static String mapFileName = null;
    
    private static MHAbstractServer server;

    long dataID;
    private static String lastEventMessage = "";
    
    public TLCGameClient(long dataID)
    {
        this.dataID = dataID;
        instanceCount++;
//        System.out.println("TLCGameClient " + instanceCount + ": " + dataID);
//        try { throw new Exception("Just for debugging..."); }
//        catch (Exception ex) { ex.printStackTrace(); }
//        messageThread = new Thread(new TLCMessageThread(this, dataID));
    }

    public void connect()
    {
        TLCDataFacade data = TLCDataFacade.getInstance(dataID);
        
        if (data.getPlayerMode() == TLCPlayerMode.SINGLE_PLAYER ||
                data.getPlayerMode() == TLCPlayerMode.HOST_LAN ||
                        data.getPlayerMode() == TLCPlayerMode.STANDALONE_HOST)
        {
            // Launch the server.
            launchServer();
        } // if player mode
        
        if (getClient() == null)  System.err.println("Client module is null.");
        
        // Register player name.
        data.registerPlayerName();
       
        // Register user type.
        data.registerUserType();
        
        // Set the game options.
        if (data.getPlayerMode() == TLCPlayerMode.SINGLE_PLAYER ||
                data.getPlayerMode() == TLCPlayerMode.HOST_LAN)
            sendGameOptions(TLCDataFacade.getInstance(TLCMain.DATA_ID).getGameOptions());
    }

    
    private void launchServer()
    {
        if (server == null)
        {
            server = MHServerModule.getInstance(MHServerModule.RANDOM_PORT);
            server.setGameServer(new TLCGameServer());

            String ip = MHAbstractServer.getIPAddress();
            if (ip == null || ip.length() == 0)
                ip = "127.0.0.1";
            TLCDataFacade.getInstance(dataID).setServerIP(ip);

            // Set color options in server.
            final Color[] colors = new Color[TLCTeamColor.values().length];
            for (int i = 0; i < colors.length; i++)
                colors[i] = TLCTeamColor.values()[i].getColorValue();
            server.setColorOptions(colors);
        }
    }
    
    
    static ConcurrentLinkedQueue<MHNetworkMessage> getOverflowQueue()
    {
        if (overflow == null)
            overflow = new ConcurrentLinkedQueue<MHNetworkMessage>();

        return overflow;
    }



    public static void log(final String data)
    {
        logFile.write(data);
    }

    
    public String getUserType()
    {
        return TLCDataFacade.getInstance(dataID).getPlayerMode().toString();
    }


    public boolean isConnected()
    {
        //if (clientModule == null) return false;
        
        return getClient().getStatus() == MHAbstractClient.STATUS_CONNECTED;
    }


    public boolean isErrorState()
    {
        
        return getClient().isErrorState();
    }


    public String getStatusMessage()
    {
        if (clientModule == null) return "Not connected.";
        return getClient().getStatusMessage();
    }


    public MHGUIChatClient createChatClient(final int x, final int y, final int w, final int h)
    {
        MHObservableClient client = new MHObservableClient(getClient());
        return new MHGUIChatClient(client, x, y, w, h);
    }

    
    private MHAbstractClient getClient()
    {
        if (clientModule == null)
        {
            if (TLCDataFacade.getInstance(dataID).isLocal())// For local clients:
            {
                if (server == null)
                    launchServer();

                clientModule = new MHObservableClient(new MHLocalClient(server));
            }
            else // For network clients:
            {
                String ip = TLCDataFacade.getInstance(dataID).getServerIP();
                clientModule = new MHObservableClient(MHNetworkClient.create(ip));
                messageThread.start();
            }
            
            clientModule.addGameListener(this);
            clientModule.addSystemListener(this);
            clientModule.getClient().connect();
            
            // Give things a chance to get started.
                try 
                {
                    Thread.sleep(2000);
                }  
                catch (final InterruptedException e){}
        }

        return clientModule.getClient();
    }


    public String getPlayerName()
    {
        String name = getClient().getPlayerName();
        if (name == null)
            name = getUserType() + " " + getClient().getClientID();

        return name;
    }


    public int getClientID()
    {
        return getClient().getClientID();
    }


    public void disconnect()
    {
        getClient().disconnect();
        clientModule = null;
    }


    public void send(final String messageType, final Serializable payload)
    {
        send(messageType, payload, getClientID());
    }

    
    public void send(final String messageType, final Serializable payload, int clientID)
    {
        getClient().sendMessage(new MHNetworkMessage(messageType, payload, getClientID()));
        log("TLCGameClient.send("+messageType+")");
    }

    
    public void drawInitialTokens()
    {
        TLCTeam team = TLCDataFacade.getTeam(getClientID());
            boolean hasOfficer = false, 
                    hasTrooper = false;
            int numTokens = 3;
            
            // Determine how many tokens we should draw based on variety
            // of character types on screen.
            
            // If team has officer, add one token.
            if (team.hasCharacterType(TLCCharacterType.OFFICER))
            {
                hasOfficer = true;
                numTokens++;
            }
            
            // If team has trooper, add one token.
            if (team.hasCharacterType(TLCCharacterType.TROOPER))
            {
                hasTrooper = true;
                numTokens++;
            }

            for (int i = 0; i < numTokens; i++)
            {
                // Decide who this token will be for.
                int forWhom = MHRandom.random(0, 2);
                
                // Draw from the appropriate character's generator.
                if (forWhom == 1 && hasOfficer)
                    sendDrawTokenMessage(TLCCharacterType.OFFICER);
                else if (forWhom == 2 && hasTrooper)
                    sendDrawTokenMessage(TLCCharacterType.TROOPER);
                else
                    sendDrawTokenMessage(TLCCharacterType.CAPTAIN);
            }
    }
    
    
    public void sendDrawTokenMessage(int charID)
    {
        send(TLCMessageType.DRAW_TOKEN, charID);
    }

    
    public void sendDrawTokenMessage(TLCCharacterType type)
    {
        send(TLCMessageType.DRAW_TOKEN, type);
    }

    
    private void sendGameOptions(final TLCGameOptions o)
    {
        send(TLCMessageType.GAME_OPTIONS, o.getSerializableVersion());
    }
    
    public void sendRecruitMessage(TLCCharacter c)
    {
        send(TLCMessageType.RECRUIT_CHARACTER, c.getSerializableVersion());
    }
    

    public void sendSignalReadyMessage(boolean ready)
    {
        send(TLCMessageType.SIGNAL_READY, ready);
    }
    

    public void sendCharacterUpdateMessage(TLCCharacter c)
    {
        send(TLCMessageType.UPDATE_CHARACTER, c.getSerializableVersion());
    }
    

    public Color[] getColorList()
    {
        return getClient().getAvailableColors();
    }


//    public boolean isMessageWaiting()
//    {
//        return getClient().isMessageWaiting();
//    }


//    public MHNetworkMessage getMessage()
//    {
//        return getClient().getMessage();
//    }


    public MHSerializableClientList getClientList()
    {
        return getClient().getClientList();
    }


    public void clearErrorState()
    {
        getClient().clearErrorState();
    }

    
    public void setPlayerColor(final Color color)
    {
        getClient().registerPlayerColor(color);
    }


//    public MHNetworkMessage peek()
//    {
//        return getClient().peek();
//    }


    public void sendCharacterUpdateMessage(TLCCharacter character,
            boolean upgradePurchased)
    {
        sendCharacterUpdateMessage(character);
        if (upgradePurchased)
            sendTeamUpdateMessage(TLCDataFacade.getTeam(character.getTeamID()));
    }


    private void sendTeamUpdateMessage(TLCTeam team)
    {
        send(TLCMessageType.UPDATE_TEAM, team.getSerializableVersion());
    }


    public void sendRetireMessage(TLCCharacter c)
    {
        send(TLCMessageType.RETIRE_CHARACTER, c.getSerializableVersion());
    }


    public void setGameState(boolean game)
    {
        isGameState = game;
    }

    
    public boolean isInGameState()
    {
        return isGameState;
    }


    public static String getMapFileName()
    {
        String fName = mapFileName;
        mapFileName = null;
        
        return fName;
    }


    public void setMapFileName(String mapFile)
    {
        mapFileName = mapFile;
    }


    public void queueMessage(MHNetworkMessage message)
    {
        getClient().queueMessage(message);
    }


    public void registerPlayerName(String playerName)
    {
        getClient().registerPlayerName(playerName);
    }


    public boolean process(MHNetworkMessage message)
    {
        TLCDataFacade data = TLCDataFacade.getInstance(dataID);
            String s = "";

            if (message.getMessageType().equals(TLCMessageType.UPDATE_TEAM))
            {
                final TLCTeam team = (TLCTeam)message.getPayload();
                s = "Team " + team.getID() + " received.";
                //System.out.println(s);
                TLCGameClient.log(s);
                TLCGameClient.log(team.toString());
                
                data.updateTeam(team);
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.WHOSE_TURN))
            {
                System.out.println(message.getMessageType()+": "+message.getPayload());

                int whoseTurn = ((Integer)message.getPayload()).intValue();
                TLCDataFacade.setWhoseTurn(whoseTurn);

                // Reset action points to full.
                if (TLCDataFacade.getWhoseTurn() == this.getClientID())
                    TLCDataFacade.resetActionPoints();

                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.CHARACTER_MOVE))
            {
                // FIXME: This method may have meaning only to the networked players.
//                TLCCharacterMoveMessage moveMsg = (TLCCharacterMoveMessage) message;
//                MHMapCellAddress location = new MHMapCellAddress(moveMsg.getOldRow(), moveMsg.getOldCol());
//                TLCDataFacade.getGameWorld().putObject(null, location, MHMapCell.WALL_LAYER);

//                TLCCharacter target = TLCDataFacade.getCharacterList().get(moveMsg.getCharID());
//                location.row = moveMsg.getNewRow();
//                location.column = moveMsg.getNewCol();
//                target.walkTo(location);
                
//                TLCCharacter c = TLCCharacter.deserialize(message.getPayload());
//                TLCCharacter target = TLCDataFacade.getCharacterList().get(c.getCharacterID());
//                target.walkTo(c.getMapLocation());
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.RECRUIT_CHARACTER) || 
                     message.getMessageType().equals(TLCMessageType.UPDATE_CHARACTER))
            {
                TLCCharacter c = TLCCharacter.deserialize(message.getPayload());
                
                // TODO: Remove character from old location, if exists.
                TLCCharacter oldChar = TLCDataFacade.getCharacterList().get(c.getCharacterID());
                if (oldChar != null)
                {
                    // DEBUG
                    if (c.getName().equalsIgnoreCase("Test"))
                        System.out.println("TEST: oldChar != null");
                    
                    MHMapCellAddress address = oldChar.getMapLocation();
                    if (address != null)
                    {
                        
                        // DEBUG
                        if (c.getName().equalsIgnoreCase("Test"))
                            System.out.println("TEST: oldChar.getMapLocation() != null");
                        
                        System.out.println("REMOVING CHARACTER FROM " + address);
                        TLCDataFacade.getGameWorld().putObject(null, address, MHMapCell.WALL_LAYER);
                        //TLCDataFacade.getCharacterList().remove(c.getCharacterID());
                    }
                }
                
                TLCDataFacade.getCharacterList().add(c);
                
                s = "Character received:" + c.toString();
                //System.out.println(s);
                TLCGameClient.log(s);

                TLCDataFacade.getGameWorld().putObject(c, c.getMapLocation(), MHMapCell.WALL_LAYER);
                //TLCDataFacade.getGameWorld().putFineObject(c);
                
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.RETIRE_CHARACTER))
            {
                TLCCharacter c = TLCCharacter.deserialize(message.getPayload());
                TLCDataFacade.getCharacterList().remove(c);
                
                s = "Character " + c.getCharacterID() + " retired and removed. (" + c.getGender().getName() + " " + c.getType().getTitle() + " on Team " + c.getTeamID() + ".)";
                TLCGameClient.log(s);
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.UPDATE_PLAYER_DATA))
            {
                TLCPlayerDescriptor user = (TLCPlayerDescriptor) message.getPayload();
                data.addUser(user);
                //System.out.println("\t"+user.toString());
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.REGISTER_USER_TYPE))
            {
                TLCPlayerDescriptor user = (TLCPlayerDescriptor) message.getPayload();
                data.addUser(user);
                data.setPlayerMode(user.type);
                String name = user.name;
                String role = user.type.toString();         
                s = "User type received:  " + name + " is a " + role;
                TLCGameClient.log(s);
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.REGISTER_COLOR_ERROR))
            {
                s = (String)message.getPayload();
                System.err.println(s);
                TLCGameClient.log(s);
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.CHAT))
            {
                queueMessage(message);
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.BROADCAST_GAME_STATE))
            {
                boolean game = ((Boolean)(message.getPayload())).booleanValue();
                setGameState(game);
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.BROADCAST_MAP_FILE))
            {
                String mapFile = (String) message.getPayload();
                setMapFileName(mapFile);
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.GAME_OPTIONS))
            {
                TLCDataFacade.getInstance(TLCMain.DATA_ID).setGameOptions((TLCGameOptions)message.getPayload());
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.BROADCAST_CHARACTER_LIST))
            {
                TLCDataFacade.setCharacterList(TLCCharacterList.deserialize(message.getPayload()));
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.REQUEST_MOVE_POINTS))
            {
                int mp = Integer.parseInt(message.getPayload().toString());
                getSelectedCharacter().setMovementPoints(mp);
                
                // DEBUG
                System.out.println("Received " + mp + " movement points.");
                
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.DRAW_TOKEN))
            {
                TLCTokenData token = (TLCTokenData)message.getPayload();
                data.addToken(token);
                
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.EVENT_LOG))
            {
                //String text = (String)message.getPayload();
                TLCEventLogMessage msg = (TLCEventLogMessage)message.getPayload();
                TLCCharacter c = TLCDataFacade.getCharacterList().get(msg.getCharacterID());
                TLCTeam t = TLCDataFacade.getTeam(c.getTeamID());
                
                String msgText = msg.getText();
                if (!lastEventMessage.equals(msgText))
                {
                    lastEventMessage = msgText;
                    TLCEventLogDisplay.addMessage(t.getColor().getColorValue(), msgText);
                }
                
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.ATTACK_NOTIFICATION))
            {
                TLCAttackNotification notify = (TLCAttackNotification) message.getPayload();
                TLCCharacter target = TLCDataFacade.getCharacterList().get(notify.defenderID);

                // If we're the target of the attack, pop up the inventory on the game screen.
                if (target.getTeamID() == TLCDataFacade.getInstance(TLCMain.DATA_ID).getClientID())
                {
                    // If we don't have a defense token, send a null to the server.
                    TLCTokenData token = data.getTokenInventory().selectDefenseToken(target.getType());
                    if (token == null)
                    {
                        send(TLCMessageType.DEFEND, null);
                        return true;
                    }
                    else // Put the token back and pop up the UI.
                    {
                        TLCGameScreen.attackNotification = notify;
                        try
                        {data.getTokenInventory().addToken(target.getType(), new TLCToken(token));} 
                        catch (Exception e){e.printStackTrace();}
                    }
                }
                else if (TLCAIManager.isAIPlayer(target.getTeamID()))
                {
                    // Tell the AI manager to pick a defense token.
                    TLCTokenData defenseToken = TLCAIManager.selectDefenseToken(target.getCharacterID());
                    // Send the token to the server.
                    send(TLCMessageType.DEFEND, defenseToken);
                }
                return true;
            }
            else if (message.getMessageType().equals(TLCMessageType.COMBAT_RESULTS))
            {
                TLCGameScreen.isAttacking = false;
                TLCGameScreen.attackNotification = null;
                // TODO: Show combat results summary.
                TLCCombatInteractionMessage summary = (TLCCombatInteractionMessage) message.getPayload();
                
                TLCGameScreen.showCombatSummary(summary);
                
                // DEBUG:
                System.out.println(summary.toString());
                
                return true;
            }
            else
            {
                s = "ERROR:  Message not handled by Client " + getClientID() + ":  " + message.getMessageType();
                System.err.println(s);
                TLCGameClient.log(s);
                return false;
            }
    }




private TLCCharacter getSelectedCharacter()
{
    // For human players:
    if (TLCDataFacade.isHumanPlayer(TLCDataFacade.getWhoseTurn()))
        return TLCActionsMenu.getSelectedCharacter();
    
    // For AI players:
    return TLCAIManager.getSelectedCharacter();
}

    @Override
    public void gameMessageReceived(MHNetworkMessage message)
    {
        process(message);
    }

    @Override
    public void systemMessageReceived(MHNetworkMessage message)
    {
        getClient().process(message);
    }
}


//class TLCMessageThread implements Runnable
//{
//    private String lastMessage;
//    private TLCGameClient client;
//    
//    TLCDataFacade data;
//    long dataID;
//
//    public TLCMessageThread(TLCGameClient c, long dataID)
//    {
//        client = c;
//        this.dataID = dataID;
//        data = TLCDataFacade.getInstance(dataID);
//    }
//    
//    @Override
//    public void run()
//    {
//        while (true)
//        {
//            if (client.isMessageWaiting())
//            {
//                MHNetworkMessage message = client.peek();
//                lastMessage = message.getMessageType();
//
//                if (!lastMessage.equals(message.getMessageType()))
//                    TLCGameClient.log("TLCGameClient.TLCMessageThread.run("+message.getMessageType()+")");
//                
//                if (!client.process(message)) // If message not processed, put it in the overflow queue.
//                    TLCGameClient.getOverflowQueue().add(client.getMessage());
//                else  // Else just remove it.
//                    client.getMessage();  
//            }
//            else
//            {
//                // See if there's anything in the overflow queue we can process.
//                for (MHNetworkMessage m : TLCGameClient.getOverflowQueue())
//                {
//                    if (client.process(m)) // If message processed, remove it.
//                        TLCGameClient.getOverflowQueue().remove(m);
//                }
//            }
//            
//            try {Thread.sleep(500);} catch (InterruptedException e){}
//        }
//    }
//    
//
//}
