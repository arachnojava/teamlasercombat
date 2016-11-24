package tlc.data.characters.state;

import mhframework.ai.state.MHState;
import mhframework.tilemap.MHMapCell;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;

public class TLCStandingState implements MHState
{
    private static final TLCStandingState INSTANCE = new TLCStandingState();
    
    
    public static TLCStandingState getInstance()
    {
        return INSTANCE;
    }
    
    
    @Override
    public void enter(Object subject)
    {
        TLCCharacter thisCharacter = (TLCCharacter) subject;
        
        // Sanity check to see if character belongs in its current place.
        TLCCharacter c = TLCDataFacade.getCharacterList().get(thisCharacter.getCharacterID());
        if (!(c.getMapLocation() == null || thisCharacter.getMapLocation() == null))
            if (!c.getMapLocation().equals(thisCharacter.getMapLocation()))
            {
                // Problem!  Remove this from the board.
                System.err.println("ERROR:  Duplicate character on board. Removing.");
                TLCDataFacade.getGameWorld().putObject(null, thisCharacter.getMapLocation(), MHMapCell.WALL_LAYER);
                return;
            }
        
        // TODO Play idle animation.
    }

    @Override
    public void execute(Object subject)
    {
        TLCCharacter thisCharacter = (TLCCharacter) subject;
        
        // TODO Play idle animation.
        
        // Set actor's image group based on type, gender, and color.
        TLCTeam t = TLCDataFacade.getTeam(thisCharacter.getTeamID());
        if (t != null)
            thisCharacter.setImageGroup(TLCDataFacade.getImageGroup(thisCharacter.getType(), thisCharacter.getGender(), t.getColor()));
    }

    @Override
    public void exit(Object subject)
    {
    }

}
