package tlc.data;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import mhframework.gui.MHGUIChatClient;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.MHSerializableClientList;
import mhframework.io.net.server.MHClientInfo;
import mhframework.media.MHImageGroup;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.images.TLCCharacterImageGroups;
import tlc.data.world.TLCGameWorld;
import tlc.net.TLCMessageType;
import tlc.net.client.TLCGameClient;
import tlc.net.server.TLCPlayerDescriptor;
import tlc.net.server.TLCUserList;
import tlc.ui.hud.actions.TLCActionsMenu;


public class TLCDataFacade
{
    public static final String VERSION_NUMBER = "0.16";
    public static final String BUILD_DATE = "05/20/2012";
    public static final int ACTION_POINTS = 2;
    
    public static boolean DEBUG = true;
    
    public static final int MAX_PLAYERS = 4;
    // Keep a separate system of data objects for each user in the
    // same address space.
    private static Map<Long, TLCDataFacade> instances = 
        Collections.synchronizedMap(new HashMap<Long, TLCDataFacade>());


    // System of data objects:
    private static TLCGameOptions options;
    private static final TLCTeamList teams = new TLCTeamList();
    private static TLCCharacterList characters = new TLCCharacterList();
    private static final TLCUserList users = new TLCUserList();
    private static TLCGameWorld gameWorld;
    private TLCGameClient gameClient;
    private TLCTokenInventory tokenInventory;
    private String ip, playerName;
    private static boolean gameOver;
    private static int whoseTurn;
    private static MHImageGroup containerImages;
    private int numHumanPlayers;
    private int numAIPlayers;
    private boolean friendlyFireOn = false;

    private TLCDataFacade(long dataID)
    {
        gameClient = new TLCGameClient(dataID);
    }

    
    public boolean isPlayer(MHClientInfo client)
    {
        TLCPlayerDescriptor player = getUser(client.id);

        if (player == null)
            return false;
            
        TLCPlayerMode m = player.type;
        
        return m == TLCPlayerMode.AI_PLAYER || m == TLCPlayerMode.HOST_LAN || m == TLCPlayerMode.JOIN_LAN || m == TLCPlayerMode.SINGLE_PLAYER;
    }

    
    public static long generateDataID()
    {
        long id = Long.MIN_VALUE;
        Random rand = new Random();
        
        while (id == Long.MIN_VALUE || instances.containsKey(id))
        {
            id = rand.nextLong();
        }
        instances.put(id, new TLCDataFacade(id));
        return id;
        
    }


    public static TLCDataFacade getInstance(long dataID)
    {
        return instances.get(dataID);
    }


    public void setPlayerMode(final TLCPlayerMode mode)
    {
        getGameOptions().setPlayerMode(mode);
    }


    public static TLCGameOptions getGameOptions()
    {
        if (options == null)
            options = new TLCGameOptions();

        return options;
    }


    public static TLCTeamList getTeamList()
    {
        return teams;
    }

    
    public void addUser(MHClientInfo user)
    {
        users.addUser(user);
    }
    

    public void setNumHumanPlayers(final int value)
    {
        this.numHumanPlayers = value;
    }


    public void setNumAIPlayers(final int value)
    {
        this.numAIPlayers = value;
    }


    public TLCTeam addTeam(final Serializable teamData, int id)
    {
        TLCTeam t = (TLCTeam) teamData;
        t.setID(id);
        getTeamList().addTeam(t);

        return t;
    }


    public void setTeamName(final String name, final int teamID)
    {
        getTeamList().get(teamID).setTeamName(name);
    }


    public int getMemberCount(final int teamID)
    {
        return getCharacterList().getTeamMembers(teamID).size();
    }


    public int getNumHumansSelected()
    {
        return numHumanPlayers;
    }


    public int getNumAISelected()
    {
        return numAIPlayers;
    }


    public static TLCTeam getTeam(final int teamID)
    {
        return getTeamList().get(teamID);
    }


    public int getTeamID()
    {
        return gameClient.getClientID();
    }

    
    public TLCPlayerMode getPlayerMode()
    {
        //return getUser(TLCGameClient.getClientID()).type;
        return getGameOptions().getPlayerMode();
    }


    public String getServerIP()
    {
        return ip;
    }


    public void setServerIP(final String serverIP)
    {
        ip = serverIP;
    }


    public void updateTeam(final TLCTeam team)
    {
        getTeamList().addTeam(team);
    }


    public synchronized static TLCCharacterList getCharacterList()
    {
        return characters;
    }


    public static TLCPlayerDescriptor getUser(int id)
    {
        return users.getUser(id);
    }


    public int getUserCount()
    {
        return users.size();
    }


    public void addUser(TLCPlayerDescriptor user)
    {
        users.addUser(user);
    }

    
    public void removeUser(int userID)
    {
        users.removeUser(userID);
    }



    public boolean isPlayer()
    {
        return isPlayer(gameClient.getClientID());
    }


    public static boolean isPlayer(int clientID)
    {
        TLCPlayerDescriptor player = getUser(clientID);
        if (player == null) return false;
        
        TLCPlayerMode m = player.type;
 
        return m == TLCPlayerMode.AI_PLAYER || m == TLCPlayerMode.HOST_LAN || m == TLCPlayerMode.JOIN_LAN || m == TLCPlayerMode.SINGLE_PLAYER;
    }


    public int countPlayers()
    {
        return users.countPlayers();
    }

    
    public int countHumanPlayers()
    {
        return users.countHumanPlayers();
    }
    

    public int countReadyPlayers()
    {
        return users.countReadyPlayers();
    }


    public void resetReadyStates()
    {
        users.resetReadyStates();
    }


    public void setSoundOn(boolean soundOn)
    {
        getGameOptions().setSoundOn(soundOn);
    }

    
    public boolean isSoundOn()
    {
        return getGameOptions().isSoundOn();
    }

    
    public void setMusicOn(boolean musicOn)
    {
        getGameOptions().setMusicOn(musicOn);
    }


    public boolean isMusicOn()
    {
        return getGameOptions().isMusicOn();
    }


    public static TLCGameWorld getGameWorld()
    {
        if (gameWorld == null)
            gameWorld = new TLCGameWorld();
        
        return gameWorld;
    }


    public String getPlayerName()
    {
        return playerName;
    }


    public int getClientID()
    {
        return gameClient.getClientID();
    }


    public String getStatusMessage()
    {
        return gameClient.getStatusMessage();
    }


    public void registerUserType()
    {
        gameClient.send(TLCMessageType.REGISTER_USER_TYPE, this.getPlayerMode());
    }


    public void setPlayerName(String nameValue)
    {
        playerName = nameValue;
    }


    public void connect()
    {
        gameClient.connect();
    }


    public void sendCharacterUpdateMessage(TLCCharacter character, boolean upgradePurchased)
    {
        gameClient.sendCharacterUpdateMessage(character, upgradePurchased);
    }


    public void sendRetireMessage(TLCCharacter character)
    {
        gameClient.sendRetireMessage(character);
    }


    public boolean isConnected()
    {
        return gameClient.isConnected();
    }


    public MHGUIChatClient createChatClient(int x, int y, int w, int h)
    {
        return gameClient.createChatClient(x, y, w, h);
    }
    

    public boolean isInGameState()
    {
        return gameClient.isInGameState();
    }


    public MHSerializableClientList getClientList()
    {
        return gameClient.getClientList();
    }


    public void sendSignalReadyMessage(boolean ready)
    {
        gameClient.sendSignalReadyMessage(ready);
    }

    
    public TLCGameClient getGameClient()
    {
        return gameClient;
    }


    public Color[] getColorList()
    {
        return gameClient.getColorList();
    }


    public boolean isErrorState()
    {
        return gameClient.isErrorState();
    }


    public void setPlayerColor(Color colorValue)
    {
        gameClient.setPlayerColor(colorValue);
    }


    public void send(String messageType, Serializable payload)
    {
        gameClient.send(messageType, payload);
    }


    public void clearErrorState()
    {
        gameClient.clearErrorState();
    }


    public void registerPlayerName()
    {
        gameClient.registerPlayerName(getPlayerName());
        
    }


    public void setGameOptions(TLCGameOptions o)
    {
        options = o;
    }


    public static boolean isGameOver()
    {
        return gameOver;
    }


    public void sendRecruitMessage(TLCCharacter character)
    {
        gameClient.sendRecruitMessage(character);
    }


    public static MHImageGroup getImageGroup(TLCCharacterType type,
            TLCCharacterGender gender, TLCTeamColor teamColor)
    {
        MHImageGroup ig = null;
        
        if (type.equals(TLCCharacterType.CAPTAIN))
        {
            if (gender.equals(TLCCharacterGender.MALE))
                ig = TLCCharacterImageGroups.getMaleCaptain(teamColor);
            else
                ig = TLCCharacterImageGroups.getFemaleCaptain(teamColor);
        }
        else if (type.equals(TLCCharacterType.OFFICER))
        {
            if (gender.equals(TLCCharacterGender.MALE))
                ig = TLCCharacterImageGroups.getMaleOfficer(teamColor);
            else
                ig = TLCCharacterImageGroups.getFemaleOfficer(teamColor);
        }
        else if (type.equals(TLCCharacterType.TROOPER))
        {
            if (gender.equals(TLCCharacterGender.MALE))
                ig = TLCCharacterImageGroups.getMaleTrooper(teamColor);
            else
                ig = TLCCharacterImageGroups.getFemaleTrooper(teamColor);
        }
        
        return ig;
    }


    public static void setWhoseTurn(int whoseTurn)
    {
        TLCDataFacade.whoseTurn = whoseTurn;
    }


    public static int getWhoseTurn()
    {
        return whoseTurn;
    }


    public static boolean isHumanPlayer(int id)
    {
        return isPlayer(id) && getUser(id).type != TLCPlayerMode.AI_PLAYER;
    }


    public int countReadyHumans()
    {
        return users.countReadyHumans();
    }


    public static boolean isCombatResultsOn()
    {
        getGameOptions();
        return TLCGameOptions.isCombatResultsOn();
    }
    
    
    public static void setCombatResultsOn(boolean b)
    {
        getGameOptions();
        TLCGameOptions.setCombatResultsOn(b);
    }

    
    public boolean isAutoAttackOn()
    {
        return getGameOptions().isAutoAttackOn();
    }
    
    
    public boolean isAutoDefenseOn()
    {
        return getGameOptions().isAutoDefenseOn();
    }


    public void setAutoAttackOn(boolean b)
    {
        getGameOptions().setAutoAttackOn(b);
    }

    
    public void setAutoDefenseOn(boolean b)
    {
        getGameOptions().setAutoDefenseOn(b);
    }


    public static void setCharacterList(TLCCharacterList list)
    {
        characters = list;
    }


    public static void resetActionPoints()
    {
        TLCCharacterList characters = getCharacterList().getTeamMembers(getWhoseTurn()); 
        for (TLCCharacter c : characters)
        {
            // DEBUG
            System.out.println("Resetting " + c.getName() + "'s action points.");
            
            c.setActionPoints(ACTION_POINTS);
        }
    }


    public boolean isLocal()
    {
        boolean remote = (getPlayerMode() == TLCPlayerMode.JOIN_LAN ||
                          getPlayerMode() == TLCPlayerMode.SPECTATOR);
                
        return !remote;
    }


    public boolean isFriendlyFireOn()
    {
        return friendlyFireOn;
    }


    public void setFriendlyFireOn(boolean friendlyFireOn)
    {
        this.friendlyFireOn = friendlyFireOn;
    }


    public void drawToken()
    {
        TLCCharacter c = TLCActionsMenu.getSelectedCharacter();
        this.getGameClient().sendDrawTokenMessage(c.getCharacterID());
        //TLCActionsMenu.selectedCharacter.setActionPoints(TLCActionsMenu.selectedCharacter.getActionPoints()-1);
    }


    public void addToken(TLCTokenData tokenData)
    {
        if (tokenData == null || tokenData.getTokenType() == null)
        {
            tokenData = new TLCTokenData();
            tokenData.setTokenType(TLCTokenType.COMBAT_TOKEN);
            tokenData.setAttackValue(1);
            tokenData.setDefenseValue(1);
        }
        
        TLCToken token = null;
        try {token = new TLCToken(tokenData);}
        catch (Exception e) {e.printStackTrace();}
        
        getTokenInventory().addToken(tokenData.getCharacterType(), token);
    }
    
    
    public TLCTokenInventory getTokenInventory()
    {
        if (tokenInventory == null)
            tokenInventory = new TLCTokenInventory();

        return tokenInventory;
    }


    public void drawInitialTokens()
    {
        getGameClient().drawInitialTokens();        
    }


    public static MHImageGroup getContainerImageGroup()
    {
        if (containerImages == null)
        {
            containerImages = new MHImageGroup();
            containerImages.addSequence(0);
            containerImages.addFrame(0, "images/W0100300.png", 1);
        }
        
        return containerImages;
    }


    public void setNumTeams(Integer selectedValue)
    {
        // TODO Auto-generated method stub
        
    }
}
