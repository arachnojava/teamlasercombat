package tlc.data.world;

import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCUpgradeItem;

public class TLCHazard extends TLCUpgradeItem
{

    protected TLCHazard()
    {
        super("Hazard", 0, 0);
    }


    @Override
    public String getDescription()
    {
        return "Hazard";
    }


    @Override
    public String getHelpText()
    {
        return "Got hurt by a hazard.";
    }


    @Override
    public void applyUpgrade(TLCCharacter character)
    {
        character.setHealth(character.getHealth()-1);
    }


    @Override
    public void undoUpgrade(TLCCharacter character)
    {
    }


    @Override
    public double getEffect()
    {
        return -1;
    }

}
