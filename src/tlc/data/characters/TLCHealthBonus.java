package tlc.data.characters;

import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCUpgradeItem;

public class TLCHealthBonus extends TLCUpgradeItem
{
    private static final long serialVersionUID = 4971892391216908142L;

    public TLCHealthBonus()
    {
        super("Snack", 0, 0);
    }


    @Override
    public String getDescription()
    {
        return "Snack";
    }


    @Override
    public String getHelpText()
    {
        return "Found a snack.";
    }


    @Override
    public void applyUpgrade(TLCCharacter character)
    {
        character.setHealth(character.getHealth()+1);
    }


    @Override
    public void undoUpgrade(TLCCharacter character)
    {
    }


    @Override
    public double getEffect()
    {
        return 1;
    }
}
