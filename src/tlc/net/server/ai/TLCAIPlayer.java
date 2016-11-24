package tlc.net.server.ai;

import java.awt.Color;
import mhframework.MHRandom;
import mhframework.io.net.server.MHClientInfo;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.data.TLCTeam;
import tlc.data.TLCTeamColor;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCNames;
import tlc.net.TLCMessageType;
import tlc.net.client.TLCGameClient;

public class TLCAIPlayer
{
    private long dataID;
    
    public TLCAIPlayer()
    {
        dataID = TLCDataFacade.generateDataID();

        registerPlayer();
    }

    
    public TLCAIPlayer(MHClientInfo sender)
    {
        this();
        // Replace human player's ID with ours.
        TLCDataFacade.getTeam(sender.id).setID(getClientID());
    }

    
    public int getClientID()
    {
        return TLCDataFacade.getInstance(dataID).getClientID();
    }
    

    public String getName()
    {
        return TLCDataFacade.getInstance(dataID).getPlayerName();
    }

    
    private void registerPlayer()
    {
        TLCDataFacade data = TLCDataFacade.getInstance(dataID);

        registerPlayerName();
        data.setPlayerMode(TLCPlayerMode.AI_PLAYER);
        data.registerUserType();
        createTeam();
        recruitCaptain();
    }

    
    private void registerPlayerName()
    {
        String name;
        
        if (MHRandom.rollD20() % 2 == 0) 
            name = TLCNames.boyName();
        else
            name = TLCNames.girlName();
        
        name = "Robo-" + name;// + " " + id + ".0";

        TLCDataFacade data = TLCDataFacade.getInstance(dataID);
        
        data.setPlayerName(name);
        data.registerPlayerName();
    }
   
   private void recruitCaptain()
   {
       System.out.println("DEBUG:TLCAIPlayer.recruitCaptain() -- " + getName());

       // Recruit a captain.
       TLCCharacterGender gender;
       String name;
       if (MHRandom.rollD4() % 2 == 0)
       {
           gender = TLCCharacterGender.MALE;
           name = TLCNames.boyName();
       }
       else
       {
           gender = TLCCharacterGender.FEMALE;
           name = TLCNames.girlName();
       }
       TLCDataFacade data = TLCDataFacade.getInstance(dataID);
       final TLCCharacter captain = new TLCCharacter(TLCCharacterType.CAPTAIN, gender);
       captain.setTeamID(data.getClientID());
       captain.setName(name);

       TLCGameClient.log(getName() + ":  Sending character request.");
       data.send(TLCMessageType.RECRUIT_CHARACTER, captain.getSerializableVersion());
       
       // Wait until captain returns from server.
       // TODO: Does the AI need to be multithreaded?
       int id = data.getClientID();
       while (true)
       {
           for (TLCCharacter c : TLCDataFacade.getCharacterList())
           {
               if (c.getTeamID() == id && c.getType() == TLCCharacterType.CAPTAIN)  //.equals(TLCCharacterType.CAPTAIN))
                   return;
           }
           try { Thread.sleep(100); }
           catch (InterruptedException e) {}
       }
   }

   private void createTeam()
   {
       System.out.println("DEBUG:TLCAIPlayer.createTeam() -- " + getName());

       TLCDataFacade data = TLCDataFacade.getInstance(dataID);

       TLCTeam t = null;
       do
       {
           Color[] colors = data.getColorList();
           int index = MHRandom.random(0, colors.length-1);
           Color teamColor = colors[index];
           data.setPlayerColor(teamColor);

           String colorName = TLCTeamColor.lookupName(teamColor);
           if (colorName != null)
           {
               t = new TLCTeam(TLCTeamColor.valueOf(colorName.toUpperCase()), data.getClientID());
               t.setTeamName(colorName + " Team");
           }
       } while (t == null);

       TLCGameClient.log(getName() + ":  Sending team request.");
       data.send(TLCMessageType.UPDATE_TEAM, t.getSerializableVersion());
   }

   
   public long getDataID()
   {
       return dataID;
   }
}
