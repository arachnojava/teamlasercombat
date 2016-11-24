package tlc.ui.command;

import mhframework.gui.MHCommand;
import tlc.data.characters.TLCCharacter;
import tlc.ui.screens.TLCCharacterScreen;
import tlc.ui.screens.TLCScreenBase;

public class TLCCharacterStatsCmd implements MHCommand
{
    private TLCCharacter character;
    private TLCScreenBase screen;
    
    public TLCCharacterStatsCmd(TLCCharacter c, TLCScreenBase s)
    {
        character = c;
        screen = s;
    }
    
    @Override
    public void execute()
    {
        TLCCharacterScreen s = new TLCCharacterScreen(character);
        screen.setNextScreen(s);
        screen.setFinished(true);
    }

}
