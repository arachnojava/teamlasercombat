package tlc.data.characters;

public class TLCArmorUpgrade extends TLCUpgradeItem
{
    private static final long serialVersionUID = -6274954035778081420L;
    public static final int COST = 2;
    public static final int SELL_VALUE = 1;
    public static final double EFFECT = 0.1;

    public TLCArmorUpgrade()
    {
        super("Armor", COST, SELL_VALUE);
    }


    @Override
    public void applyUpgrade(final TLCCharacter character)
    {
        character.setDefenseValue(character.getDefenseValue() + EFFECT);
    }


    @Override
    public String getDescription()
    {
        return "Defense + " + (int)(EFFECT*100) + "%";
    }


    @Override
    public String getHelpText()
    {
        return "Better armor can help you take less damage when you are attacked." +
               " The current value shows the probability that you will have an extra" +
               " defense (DF) point when you defend against an attack.";
    }


    @Override
    public void undoUpgrade(TLCCharacter character)
    {
        if (character.getDefenseValue() >= EFFECT)
            character.setDefenseValue(character.getDefenseValue() - EFFECT);
    }


    @Override
    public double getEffect()
    {
        return EFFECT;
    }
}
