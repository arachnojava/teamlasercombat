package tlc.net;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import mhframework.MHLogFile;
import mhframework.MHTextFile;
import mhframework.gui.MHGUIChatClient;
import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.MHSerializableClientList;
import mhframework.io.net.client.MHAbstractClient;
import mhframework.io.net.client.MHClientModule;
import mhframework.io.net.client.MHObservableClient;
import mhframework.io.net.server.MHServerModule;
import mhframework.media.MHResourceManager;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.data.TLCTeam;
import tlc.data.TLCTeamColor;
import tlc.data.characters.TLCCharacter;
import tlc.net.server.TLCGameServer;
import tlc.net.server.TLCPlayerDescriptor;

public class TLCGameClient
{
    private static final MHTextFile logFile = new MHLogFile("TLCGameClientLog.txt");

    private static MHObservableClient clientModule;
    private static boolean isConnected = false;
    private static String ip, playerName;
    private static Thread messageThread = new Thread(new TLCMessageThread());
    
    private static ConcurrentLinkedQueue<MHNetworkMessage> overflow;


    private TLCGameClient()
    {
    }


    public static void connect()
    {
        if (TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode() == TLCPlayerMode.SINGLE_PLAYER ||
            TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode() == TLCPlayerMode.HOST_LAN ||
            TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode() == TLCPlayerMode.STANDALONE_HOST)
        {
            // Launch the server.

                final MHServerModule server = MHServerModule.getInstance();
                server.setGameServer(new TLCGameServer());

                String ip = MHServerModule.getIPAddress();
                if (ip == null || ip.length() == 0)
                    ip = "127.0.0.1";
                setServerIP(ip);

                // Set color options in server.
                final Color[] colors = new Color[TLCTeamColor.values().length];
                for (int i = 0; i < colors.length; i++)
                    colors[i] = TLCTeamColor.values()[i].getColorValue();
                server.setColorOptions(colors);
            }


        // Register player name.
        if (getClientModule() == null)  System.out.println("Client module is null.");
        if (playerName == null)  System.out.println("Player name is null.");
        getClientModule().registerPlayerName(playerName);
        TLCGameClient.send(TLCMessageType.REGISTER_USER_TYPE, TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode());
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

    public static String getUserType()
    {
        return TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode().toString();
    }


    public static boolean isConnected()
    {
        if (clientModule == null) return false;
        
        return getClientModule().getStatus() == MHClientModule.STATUS_CONNECTED;
    }


    public static boolean isErrorState()
    {
        
        return getClientModule().isErrorState();
    }


    public static String getStatusMessage()
    {
        if (clientModule == null) return "Not connected.";
        return getClientModule().getStatusMessage();
    }


    public static MHGUIChatClient createChatClient(final int x, final int y, final int w, final int h)
    {
        return new MHGUIChatClient(clientModule, x, y, w, h);
    }


    private static MHAbstractClient getClientModule()
    {
        if (clientModule == null || clientModule.getClient().isErrorState())
        {
            clientModule = new MHObservableClient(MHClientModule.create(ip));
            int soundID = MHResourceManager.getSoundManager().addSound("audio/LinkEstablished.wav");
            MHResourceManager.getSoundManager().play(soundID);
        }
        if (clientModule != null && !isConnected && !clientModule.getClient().isErrorState())
        {
            isConnected = true;
            messageThread.start();

            try {Thread.sleep(2000);}  // Give things a chance to get started.
            catch (final InterruptedException e){}
        }

        return clientModule.getClient();
    }


    public static String getPlayerName()
    {
        String name = getClientModule().getPlayerName();
        if (name == null)
            name = getUserType() + " " + getClientModule().getClientID();

        return name;
    }


    public static void setPlayerName(final String name)
    {
        //if (clientModule != null)
        //    getClientModule().registerPlayerName(name);

        playerName = name;
    }


    public static void setServerIP(final String address)
    {
        ip = address;
    }


    public static int getClientID()
    {
        return getClientModule().getClientID();
    }


    public static void disconnect()
    {
        getClientModule().disconnect();
        clientModule = null;
        isConnected = false;
    }


    public static void send(final String messageType, final Serializable payload)
    {
        getClientModule().sendMessage(new MHNetworkMessage(messageType, payload));
        log("TLCGameClient.send("+messageType+")");
    }

    
    public static void sendRecruitMessage(TLCCharacter c)
    {
        send(TLCMessageType.RECRUIT_CHARACTER, c.getSerializableVersion());
    }
    

    public static void sendCharacterUpdateMessage(TLCCharacter c)
    {
        send(TLCMessageType.UPDATE_CHARACTER, c.getSerializableVersion());
    }
    

    public static Color[] getColorList()
    {
        return getClientModule().getAvailableColors();
    }


    public static boolean isMessageWaiting()
    {
        return getClientModule().isMessageWaiting();
    }


    public static MHNetworkMessage getMessage()
    {
        return getClientModule().getMessage();
    }


    public static MHSerializableClientList getClientList()
    {
        return getClientModule().getClientList();
    }


    public static void clearErrorState()
    {
        getClientModule().clearErrorState();
    }

    
    public static void setPlayerColor(final Color color)
    {
        getClientModule().registerPlayerColor(color);
    }


    public static MHNetworkMessage peek()
    {
        return getClientModule().peek();
    }
}


class TLCMessageThread implements Runnable
{
    private String lastMessage;

    @Override
    public void run()
    {
        while (true)
        {
            if (TLCGameClient.isMessageWaiting())
            {
                MHNetworkMessage message = TLCGameClient.peek();
                lastMessage = message.getMessageType();

                if (!lastMessage.equals(message.getMessageType()))
                    TLCGameClient.log("TLCGameClient.TLCMessageThread.run("+message.getMessageType()+")");
                
                if (!process(message)) // If message not processed, put it in the overflow queue.
                    TLCGameClient.getOverflowQueue().add(TLCGameClient.getMessage());
                else  // Else just remove it.
                    TLCGameClient.getMessage();  
            }
            else
            {
                // See if there's anything in the overflow queue we can process.
                for (MHNetworkMessage m : TLCGameClient.getOverflowQueue())
                {
                    if (process(m)) // If message processed, remove it.
                        TLCGameClient.getOverflowQueue().remove(m);
                }
            }
            
            try {Thread.sleep(500);} catch (InterruptedException e){}
        }
    }
    
    
    private boolean process(MHNetworkMessage message)
    {
        final TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
        String s = "";

        if (message.getMessageType().equals(TLCMessageType.UPDATE_TEAM))
        {
            final TLCTeam team = (TLCTeam)message.getPayload();
            s = "Team " + team.getID() + " received.";
            System.out.println(s);
            TLCGameClient.log(s);
            TLCGameClient.log(team.toString());
            
            data.updateTeam(team);
            return true;
        }
        else if (message.getMessageType().equals(TLCMessageType.RECRUIT_CHARACTER) || 
                 message.getMessageType().equals(TLCMessageType.UPDATE_CHARACTER))
        {
            TLCCharacter c = TLCCharacter.deserialize(message.getPayload());
            TLCDataFacade.getInstance(TLCMain.DATA_ID).getCharacterList().add(c);
            
            s = "Character " + c.getCharacterID() + " received. (" + c.getGender().getName() + " " + c.getType().getTitle() + " on Team " + c.getTeamID() + ".)";
            TLCGameClient.log(s);
            return true;
        }
        else if (message.getMessageType().equals(TLCMessageType.REGISTER_USER_TYPE))
        {
            TLCPlayerDescriptor user = (TLCPlayerDescriptor) message.getPayload();
            TLCDataFacade.getInstance(TLCMain.DATA_ID).addUser(user);
            TLCDataFacade.getInstance(TLCMain.DATA_ID).setPlayerMode(user.type);
            s = "User type received:  " + ((TLCPlayerDescriptor)message.getPayload()).type.toString();
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
            MHClientModule.get().queueMessage(message);
            return true;
        }
        else
        {
            s = "ERROR:  Message not handled by Client " + TLCGameClient.getClientID() + ":  " + message.getMessageType();
            System.err.println(s);
            TLCGameClient.log(s);
            return false;
        }
    }
}
