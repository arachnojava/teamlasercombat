    package tlc.ui.hud.actions;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import mhframework.tilemap.MHMapCellAddress;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCCharacter;
import tlc.ui.screens.TLCGameScreen;

public class TLCActionsMenu implements ActionListener
{
    TLCGameScreen gameScreen;
    public static TLCCharacterList characterList;
    private static TLCCharacter selectedCharacter;
    static int charID = 0; 
    private final int clientID = TLCDataFacade.getInstance(TLCMain.DATA_ID).getClientID();
    
    TLCActionsMenuState state;
    
    public TLCActionsMenu(TLCGameScreen screen)
    {
        gameScreen = screen;
        init();
    }
    
    
    public void init()
    {
        characterList = TLCDataFacade.getCharacterList().getTeamMembers(clientID);
        selectedCharacter = characterList.get(charID);
        centerOnCharacter();
        state = new TLCDefaultMenuState(this);
    }
    
    
    public void close()
    {
        gameScreen.closeActionsMenu();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        state.actionPerformed(e);
    }

    
    public void advance()
    {
        state.advance();
    }
    

    public void render(Graphics2D g)
    {
        //centerOnCharacter();
        state.render(g);
    }
    
    void centerOnCharacter()
    {
        if (selectedCharacter != null)
        {
            if (selectedCharacter.getMapLocation() == null)
            {
                // Character's map location is missing.  Fix it.
                MHMapCellAddress cell = TLCDataFacade.getGameWorld().findCharacter(selectedCharacter);
                if (cell != null)
                    selectedCharacter.setMapLocation(cell);
                else return;
            }

            int row = selectedCharacter.getMapLocation().row;
            int column = selectedCharacter.getMapLocation().column;
            TLCDataFacade.getGameWorld().centerOn(row, column);
        }
    }




    public void unload()
    {
        state.unload();
    }


    public static TLCCharacter getSelectedCharacter()
    {
        if (characterList == null)
            return null;
        
        selectedCharacter = characterList.get(charID);
        
        return selectedCharacter;
    }


    public static void setSelectedCharacter(TLCCharacter c)
    {
        selectedCharacter = c;
    }
    
    
}
