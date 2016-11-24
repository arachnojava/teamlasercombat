package tlc.ui.command;

import mhframework.gui.MHCommand;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;
import tlc.ui.screens.TLCScreenBase;

public class TLCRecruitCmd implements MHCommand
{
    private TLCScreenBase screen;
    private TLCCharacter character;
    private TLCDataFacade data;
    
    public TLCRecruitCmd(TLCCharacter c, TLCScreenBase s, TLCDataFacade d)
    {
        character = c;
        screen = s;
        data = d;
    }
    
    @Override
    public synchronized void execute()
    {
        int cost = character.getType().getCost();
        int teamID = data.getTeamID();
        TLCTeam team = TLCDataFacade.getTeam(teamID);
        int budget = team.getCoins(); 
        if (cost > budget)
        {
            String pronoun = (character.getGender().getName().equalsIgnoreCase("male") ? "He" : "She");
            
            String msg = "You do not have enough coins to hire " + character.getType().getTitle() + " " + character.getName() +". "
            + pronoun + " demands " + cost + " coins, but you only have " + budget + ".";

            if (screen != null)
                screen.showDialog(screen, msg);
        }
        else
        {
            data.sendRecruitMessage(character);
            // For network clients, wait for character to arrive from server.
            if (data.getPlayerMode().equals(TLCPlayerMode.JOIN_LAN))
            {
                while(true)
                {
                    try
                    {
                        for (TLCCharacter c : TLCDataFacade.getCharacterList())
                        {
                            if (c.getType() == character.getType() && c.getTeamID() == team.getID() && c.getName().equals(character.getName()))
                            {
                                if (screen != null)
                                    screen.setFinished(true);
                                return;
                            }
                        }
                    }
                    catch (Exception e)
                    {                    
                    }
                }
            }
            
            if (screen != null)
                screen.setFinished(true);
        }
            
    }

}
