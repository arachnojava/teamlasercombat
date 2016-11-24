package tlc.net.server.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mhframework.MHRandom;
import mhframework.MHThreadManager;
import mhframework.ai.path.MHNodePath;
import mhframework.ai.path.MHPathFinder;
import mhframework.io.MHLogFile;
import mhframework.io.net.server.MHClientInfo;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHTileMap;
import mhframework.tilemap.MHTileMapDirection;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPowerTokenSet;
import tlc.data.TLCTokenData;
import tlc.data.TLCTokenInventory;
import tlc.data.TLCTokenType;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterType;
import tlc.net.TLCAttackMessage;
import tlc.net.TLCCharacterMoveMessage;
import tlc.net.TLCMessageType;
import tlc.net.client.TLCGameClient;
import tlc.net.server.TLCGameServer;
import tlc.ui.hud.actions.TLCAttackDirections;

public class TLCAIManager //implements Runnable
{
    private static MHLogFile logFile = new MHLogFile("TLCAIManagerLog.txt");
    private static int currentPlayerID;
    private static TLCAIPlayer currentPlayer = null; // Used for determining transitions between players.
    private static int charID = 0;
    private static Map<Integer, TLCAIPlayer> aiPlayers;
    private static long threadID;
    //private static boolean doneShopping = false;
    public static boolean isAttacking; // Flag to indicate when attack in in progress.

    public void replaceHumanPlayer(MHClientInfo sender)
    {
        TLCAIPlayer ai = new TLCAIPlayer(sender);
        aiPlayers.put(ai.getClientID(), ai);
    }


    public void createAIPlayers()
    {
        if (aiPlayers != null) return;

        aiPlayers = Collections.synchronizedMap(new HashMap<Integer, TLCAIPlayer>());

        //int numAI = TLCDataFacade.getInstance(TLCGameServer.DATA_ID).getNumAISelected();
        int numAI = TLCDataFacade.getInstance(TLCMain.DATA_ID).getNumAISelected();
        System.out.println("TLCAIManager.createAIPlayers(): numAI = " + numAI);
        while (aiPlayers.size() < numAI)
        {
            TLCAIPlayer ai = new TLCAIPlayer();
            TLCAIShopper.goShopping(ai.getDataID());
            aiPlayers.put(ai.getClientID(), ai);
            //MHThreadManager.getInstance().createThread(ai, ai.getName());

            System.out.println("TLCAIManager.createAIPlayers(): aiPlayers.size() = " + aiPlayers.size());
        }
    }


    public static void advance()
    {
        if (TLCGameServer.finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn())))
            TLCGameServer.nextPlayerTurn();
        
        if (currentPlayerID == TLCDataFacade.getWhoseTurn())
            return;
        
        // DEBUG
        logFile.append("================  TLCAIManager.advance()");

        currentPlayerID = TLCDataFacade.getWhoseTurn(); 
        
        if (isAIPlayer(currentPlayerID))
        {
            currentPlayer = getPlayer(currentPlayerID);
            logFile.append("Player " + currentPlayerID + " is an AI player.");

            // DEBUG:
            logFile.append("It's " + currentPlayer.getName() + "'s turn. ID="+currentPlayer.getClientID());
            System.out.println("\t================");
            System.out.println("\tIt's " + currentPlayer.getName() + "'s turn. ID="+currentPlayer.getClientID());
            System.out.println("\t================");

            TLCDataFacade data = TLCDataFacade.getInstance(currentPlayer.getDataID()); 

            if (data.isInGameState())
            {
                Runnable thread = new Runnable()
                //Thread thread = new Thread()
                {
                    public void run()
                    {
                        doTurn(getPlayer(TLCDataFacade.getWhoseTurn()));
                        waitForPlayers();
                    }
                };
                //thread.start();

                threadID = MHThreadManager.getInstance().createThread(thread, currentPlayer.getName()+MHRandom.random(0, Integer.MAX_VALUE-10));
                logFile.append("Starting thread " + threadID + " for " + currentPlayer.getName());
                MHThreadManager.getInstance().start(threadID);
                //while (!MHThreadManager.getInstance().isFinished(threadID));
            }
        }

        logFile.append("End of TLCAIManager.advance()  ================");
    } // advance()


    private static void chat()
    {
        //        TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
        //        String[] messages = 
        //            {
        //                "Hi!",
        //                "I have the feeling you're going to lose this round.",
        //                "Random numbers aren't a very good simulation of intelligence."
        //            };
        //        
        //        int rm = MHRandom.random(0, messages.length-1);
        //        TLCAIPlayer player = aiPlayers.get(MHRandom.random(0, aiPlayers.size()-1));
        //        
        //        String message = player.getName() + ": " + messages[rm];
        //        
        //        data.getGameClient().send(MHMessageType.CHAT, message);
    }


    public static void goShopping()
    {
        // For each AI player...
        for (TLCAIPlayer player : aiPlayers.values())
        {
            // Go shopping.
            TLCAIShopper.goShopping(player.getDataID());

            // Draw tokens.
            TLCDataFacade.getInstance(player.getDataID()).drawInitialTokens();

            // Signal ready.
            //TLCDataFacade.getInstance(player.getDataID()).sendSignalReadyMessage(true);
        }
    }


//    @Override
//    public void run()
//    {
//        // DEBUG
//        MHLogFile log = new MHLogFile("TLCAIManagerLog.txt");
//        log.append("TLCAIManager.run()");
//
//        TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);//TLCGameServer.DATA_ID); 
//
//        // While the game's not over...
//        while (!TLCDataFacade.isGameOver())
//        {
//            // If we're in Lobby State
//            if (!data.isInGameState())
//            {
//                log.append("In lobby...");
//                if (!doneShopping)
//                {
//                    for (TLCAIPlayer player : aiPlayers.values())
//                    {
//                        // Go shopping.
//                        TLCAIShopper.goShopping(player.getDataID());
//
//                        // Signal ready.
//                        TLCDataFacade.getInstance(player.getDataID()).sendSignalReadyMessage(true);
//                    }
//                    doneShopping = true;
//                }
//            }
//            else if (isAIPlayer(TLCDataFacade.getWhoseTurn()))  // else we're in the game, so if it's my turn now...
//            {
//                doneShopping = false;
//                TLCAIPlayer player = getPlayer(TLCDataFacade.getWhoseTurn());
//                doTurn(player);
//            }
//
//            // Wait a few seconds before checking again.
//            try {Thread.sleep(5000);} 
//            catch (InterruptedException e){}
//        } // while not game over...
//    } // run()


//    private static boolean finishedTurn(ArrayList<TLCCharacter> characters)
//    {
//        //for (int i = 0; i < characters.size(); i++)
//        for (TLCCharacter c : characters)
//            if (c.getActionPoints() > 0)
//            {
//                // DEBUG:
//                System.out.print("TLCAIManager.finishedTurn(): ");
//                System.out.print(c.getName() + " is not done.");
//                System.out.println(" Still has " + c.getActionPoints() + " actions.");
//                return false;
//            }
//
//        return true;
//    }


    private static void doTurn(TLCAIPlayer player)
    {
        logFile.append("TLCAIManager.doTurn()");
        
        TLCDataFacade data = TLCDataFacade.getInstance(player.getDataID());
        TLCGameClient client = data.getGameClient();
        //int teamID = data.getClientID();
        boolean hasAttacked = false;
        
        // Get a list of our team members.
        //ArrayList<TLCCharacter> characters = TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn());
        
        // For each character:
        //for (TLCCharacter selectedCharacter : characters)
        //for (charID = 0; charID < characters.size(); charID++)
        while (!TLCGameServer.finishedTurn(TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn())))
        {
            waitForPlayers();

            // Next character's turn.
            while (getSelectedCharacter().getActionPoints() < 1)
            {
                // DEBUG
                System.out.println(getSelectedCharacter().getName() + " is done...");
                charID = (charID + 1) % TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn()).size();
            }

            // While character still has action points:
            //while (selectedCharacter.getActionPoints() > 0)
            // DEBUG:
            String s = getSelectedCharacter().getName() + " has " + getSelectedCharacter().getActionPoints() + " actions left.";
            //logFile.append(s);
            System.out.println(s);
            
//            if (getSelectedCharacter().getActionPoints() < 1)
//            {
//                // Next character's turn.
//                charID = (charID + 1) % characters.size();
//            }
//            else
            {
                // If I'm low on tokens
                if (TLCDataFacade.getInstance(player.getDataID()).getTokenInventory().countTokens(getSelectedCharacter().getType()) < 2)
                {
                    // DEBUG:
                    logFile.append(getSelectedCharacter().getName()+" is drawing a token.");
                    // Draw a token.
                    client.sendDrawTokenMessage(getSelectedCharacter().getCharacterID());
                    //selectedCharacter.setActionPoints(selectedCharacter.getActionPoints()-1);
                }
                // Else if I need to heal and I have a heal token
//                else if (canHeal(player.getDataID()))
//                {
//                    // DEBUG:
//                    System.out.println(player.getName() + " is healing.");
//                    
//                    // TODO: Use heal token.
//                    //selectedCharacter.setActionPoints(selectedCharacter.getActionPoints()-1);
//                }
                else if (canAttack(player.getDataID()) && !hasAttacked)
                {
                    // Else if I'm in a position to attack and I have an attack token
                    // Figure out which direction I can attack and do it.

                    TLCAttackDirections dirs = TLCAIManager.findAttackDirections();
                    for (MHTileMapDirection d : MHTileMapDirection.values())
                    {
                        hasAttacked = true;
                        boolean dirChosen = false;
                        if (!dirChosen && dirs.getDirection(d) != null)
                        {
                            // DEBUG:
                            System.out.println(getSelectedCharacter().getName() + " is attacking.");
                            
                            //selectedCharacter.setActionPoints(selectedCharacter.getActionPoints()-1);
                            
                            // Choose a token for attack.
                            TLCTokenInventory inventory = TLCDataFacade.getInstance(player.getDataID()).getTokenInventory();
                            TLCTokenData token = inventory.selectAttackToken(getSelectedCharacter().getType());

                            if (token == null)
                            {
                                // DEBUG:
                                System.out.println("Token is null!");
                                token = new TLCTokenData();
                                token.setTokenType(TLCTokenType.COMBAT_TOKEN);
                                token.setAttackValue(1);
                                token.setDefenseValue(1);
                            }
                            else
                            {
                                // DEBUG:
                                System.out.println("Well, the token is NOT null. It's a " + token.getTokenType());
                            }
                            
                            // DEBUG:
                            System.out.println(getSelectedCharacter().getName() + " chose token: " + token);

                            // Send character, token, and direction to server.
                            TLCAttackMessage attackMsg = new TLCAttackMessage(
                                    getSelectedCharacter().getCharacterID(),
                                    token, d);
                            TLCDataFacade.getInstance(player.getDataID()).send(TLCMessageType.ATTACK, attackMsg);
                            
                            
                            if (!dirs.getDirection(d).equalsIgnoreCase("Container"))
                            {
                                getSelectedCharacter().isTakingTurn = true;
                                isAttacking = true;
                                dirChosen = true;
                            

                                // TODO: Wait for combat results.
                                while (isAttacking);
                                getSelectedCharacter().isTakingTurn = false;
                            }

                            break;
                        }
                    }
                }
                else if (canMove(player.getDataID())) // Else let's try to move.
                {
                    client.send(TLCMessageType.REQUEST_MOVE_POINTS, getSelectedCharacter().getSerializableVersion());

                    // Wait for movement points to return from server.
                    while (getSelectedCharacter().getMovementPoints() <= 0);

                    if (getSelectedCharacter().getMapLocation() == null) 
                    {
                        // Fix null map locations.
                        TLCGameServer.placeCharactersOnMap();
                    }

                    // Generate all paths accessible within movement points.
                    int startRow = getSelectedCharacter().getMapLocation().row - getSelectedCharacter().getMovementPoints();
                    int endRow = getSelectedCharacter().getMapLocation().row + getSelectedCharacter().getMovementPoints();
                    int startCol = getSelectedCharacter().getMapLocation().column - getSelectedCharacter().getMovementPoints();
                    int endCol = getSelectedCharacter().getMapLocation().column + getSelectedCharacter().getMovementPoints();

                    // List of places I might go if I decide to move.
                    ArrayList<MHMapCellAddress> destinations = new ArrayList<MHMapCellAddress>();
                    MHMapCellAddress startLoc = getSelectedCharacter().getMapLocation();
                    MHTileMap map = TLCDataFacade.getGameWorld().getMap();
                    MHTileMapDirection[] directions = new MHTileMapDirection[4];
                    directions[0] = MHTileMapDirection.NORTHEAST;
                    directions[1] = MHTileMapDirection.SOUTHEAST;
                    directions[2] = MHTileMapDirection.SOUTHWEST;
                    directions[3] = MHTileMapDirection.NORTHWEST;
                    for (int r = startRow; r <= endRow; r++)
                    {
                        for (int c = startCol; c <= endCol; c++)
                        {
                            MHMapCellAddress goalLoc = new MHMapCellAddress(r, c);

                            // If goalLoc is unobstructed, let's see if I have a path to it.
                            if (TLCDataFacade.getGameWorld().canWalkOn(r, c))
                            {
                                MHNodePath p = MHPathFinder.aStarSearch(startLoc, goalLoc, map, directions);
                                if (p != null && p.size() <= getSelectedCharacter().getMovementPoints())
                                    destinations.add(goalLoc);
                            }
                        }
                    }
                    //         If we can move into attack position.
                    //             Move into attack position.
                    //         Else if we can move at all, just move somewhere.
                    if (destinations.size() > 0)
                    {
                        // Pick a valid destination at random.
                        MHMapCellAddress destination = destinations.get(MHRandom.random(0, destinations.size()-1));

                        // Go to selected destination.
//                        selectedCharacter.setActionPoints(selectedCharacter.getActionPoints()-1);
//                        selectedCharacter.walkTo(destination);
                        
                        TLCCharacterMoveMessage msg = new TLCCharacterMoveMessage(getSelectedCharacter().getCharacterID(), 
                                getSelectedCharacter().getMapLocation().row, getSelectedCharacter().getMapLocation().column, 
                                destination.row, destination.column);
                        
                        data.send(TLCMessageType.CHARACTER_MOVE, msg);
                        
                        //selectedCharacter.setActionPoints(selectedCharacter.getActionPoints()-1);
                    }
                    //     Else just draw a token 'cause we can't do anything else.
                    else
                    {
                        if (getSelectedCharacter().getActionPoints() > 0)
                        {
                            // DEBUG:
                            logFile.append(getSelectedCharacter().getName()+" is drawing a token.");

                            //selectedCharacter.setActionPoints(selectedCharacter.getActionPoints()-1);
                            client.sendDrawTokenMessage(getSelectedCharacter().getCharacterID());
                        }
                    }
                }
                //     Else just draw a token 'cause we can't do anything else.
                else
                {
//                    if (TLCDataFacade.getWhoseTurn() != TLCDataFacade.getInstance(player.getDataID()).getClientID())
//                        return;
                    
                    if (getSelectedCharacter().getActionPoints() > 0)
                    {
                        // DEBUG:
                        logFile.append(getSelectedCharacter().getName()+" is drawing a token.");

                        //selectedCharacter.setActionPoints(selectedCharacter.getActionPoints()-1);
                        client.sendDrawTokenMessage(getSelectedCharacter().getType());
                    }
                }
                // Wait until character is finished moving before going to next one.
                waitForPlayers();

//                if (TLCDataFacade.getWhoseTurn() != TLCDataFacade.getInstance(player.getDataID()).getClientID())
//                    return;
                
//                if (getSelectedCharacter().getActionPoints() < 1)
//                {
//                    charID = (charID + 1) % characters.size();
//                }
            }
        }
    }
    
    
    private static boolean canAttack(long dataID)
    {
//        if (TLCDataFacade.getWhoseTurn() != TLCDataFacade.getInstance(dataID).getClientID())
//            return false;
        
        if (getSelectedCharacter().getActionPoints() < 1)
            return false;
        
        int ct = TLCDataFacade.getInstance(dataID).getTokenInventory().getCombatTokens(getSelectedCharacter().getType()).size();
        int gt = TLCDataFacade.getInstance(dataID).getTokenInventory().getGrenadeTokenCount(getSelectedCharacter().getType());
        TLCPowerTokenSet pt = TLCDataFacade.getInstance(dataID).getTokenInventory().getPowerTokens(getSelectedCharacter().getType());
        boolean hasToken = ct > 0 || gt > 0 
                || pt.hasToken(TLCTokenType.PT_VAMPIRE)
                || pt.hasToken(TLCTokenType.PT_DESPERATION)
                || pt.hasToken(TLCTokenType.PT_BLAZE_OF_GLORY)
                || pt.hasToken(TLCTokenType.PT_LUCKY_SHOT);

        return (hasToken && findAttackDirections() != null);
    }

    private static TLCAttackDirections findAttackDirections()
    {
        TLCAttackDirections dirs = TLCDataFacade.getGameWorld().findAttackDirections(getSelectedCharacter());

        if (dirs.isEmpty())
            return null;

        return dirs;
    }

    
    private static boolean canMove(long dataID)
    {
//        if (TLCDataFacade.getWhoseTurn() != TLCDataFacade.getInstance(dataID).getClientID())
//            return false;
        
        if (getSelectedCharacter().getActionPoints() < 1)
            return false;
        
        MHMapCellAddress addr = getSelectedCharacter().getMapLocation();
        if (addr == null) return false;
        
        MHMapCellAddress nw = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.NORTHWEST);
        MHMapCellAddress ne = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.NORTHEAST);
        MHMapCellAddress sw = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.SOUTHWEST);
        MHMapCellAddress se = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.SOUTHEAST);
        
        return (TLCDataFacade.getGameWorld().canWalkOn(nw.row, nw.column) ||
                TLCDataFacade.getGameWorld().canWalkOn(ne.row, ne.column) || 
                TLCDataFacade.getGameWorld().canWalkOn(sw.row, sw.column) ||
                TLCDataFacade.getGameWorld().canWalkOn(se.row, se.column));
    }

    
    private static boolean canHeal(long dataID)
    {
//        if (TLCDataFacade.getWhoseTurn() != TLCDataFacade.getInstance(dataID).getClientID())
//            return false;
        
        if (getSelectedCharacter().getActionPoints() < 1)
            return false;
        
        TLCCharacter c = getSelectedCharacter();
        boolean isDamaged = c.getHealth() < c.getMaxHealth();

        int ht = TLCDataFacade.getInstance(dataID).getTokenInventory().getHealTokenCount(c.getType());
        boolean hasToken = (ht > 0);
        
        return isDamaged && hasToken;
    }


    public static void waitForPlayers()
    {
        while (playersMoving())
        {
            try
            {
                Thread.sleep(2000);
            } catch (InterruptedException e)
            {
            }
        }
    }

    
    private static boolean playersMoving()
    {
        TLCCharacterList characters = TLCDataFacade.getCharacterList();
        for (TLCCharacter c : characters)
        {
                if (c.isTakingTurn)
                    return true;
        }
        return false;
    }

    
    private static TLCAIPlayer getPlayer(int clientID)
    {
        if (aiPlayers == null)
            return null;
        
        for (TLCAIPlayer player : aiPlayers.values())
        {
            if (player.getClientID() == clientID)
                return player;
        }        

        return null;
    }


    public static boolean isAIPlayer(int clientID)
    {
        TLCAIPlayer player = getPlayer(clientID);
        boolean isAI = (player != null);

        //System.out.println("TLCAIManager.isAIPlayer(" + clientID + ") : " + isAI);

        return isAI;
    }


    public void setThreadID(long threadID)
    {
        TLCAIManager.threadID = threadID;
    }


    public long getThreadID()
    {
        return threadID;
    }


    public static TLCCharacter getSelectedCharacter()
    {
        // Get a list of our team members.
        TLCCharacterList characters = TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getWhoseTurn());
        //TLCCharacter selectedCharacter = characters.get(charID);

        charID %= characters.size();
        return characters.get(charID);
    }

    
    public static TLCTokenData selectDefenseToken(int charID)
    {
        TLCCharacter character = TLCDataFacade.getCharacterList().get(charID);
        TLCAIPlayer player = getPlayer(character.getTeamID());
        TLCTokenInventory inventory = TLCDataFacade.getInstance(player.getDataID()).getTokenInventory();
        TLCTokenData token = inventory.selectDefenseToken(character.getType());
        return token;
    }
}
