package tlc.data.characters;

public class TLCEnduranceTraining extends TLCUpgradeItem
{
    private static final long serialVersionUID = -3682777405860176752L;
    public static final int COST = 2;
    public static final int SELL_VALUE = 1;
    public static final int EFFECT = 1;

    public TLCEnduranceTraining()
    {
        super("Endurance Training", COST, SELL_VALUE);
    }


    @Override
    public void applyUpgrade(final TLCCharacter character)
    {
        character.setMaxHealth(character.getMaxHealth() + EFFECT);
        character.setHealth(character.getMaxHealth());
    }


    @Override
    public String getDescription()
    {
        return "HP + " + EFFECT;
    }


    @Override
    public String getHelpText()
    {
        return "Endurance training enables you to take more of a " +
               "beating before you pass out. It adds one point to " +
               "the character's maximum health (HP), making it harder for an " +
               "opponent to eliminate him or her.  (20 HP maximum per character. " +
               "Training cannot be sold.)";
    }


    @Override
    public void undoUpgrade(TLCCharacter character)
    {
        character.setMaxHealth(character.getMaxHealth() - EFFECT);
        character.setHealth(character.getMaxHealth());
    }


    @Override
    public double getEffect()
    {
        return EFFECT;
    }
}
