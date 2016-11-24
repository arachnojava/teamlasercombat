package tlc.data.characters;

import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCUpgradeItem;

public class TLCTokenBonus extends TLCUpgradeItem
{
    private static final long serialVersionUID = 1L;


    public TLCTokenBonus()
    {
        super("Token", 0, 0);
    }


    @Override
    public String getDescription()
    {
        return "Token";
    }


    @Override
    public String getHelpText()
    {
        return "Found a token.";
    }


    @Override
    public void applyUpgrade(TLCCharacter character)
    {
        TLCDataFacade.getInstance(TLCMain.DATA_ID).drawInitialTokens();
    }


    @Override
    public void undoUpgrade(TLCCharacter character)
    {
    }


    @Override
    public double getEffect()
    {
        return 0;
    }

}
