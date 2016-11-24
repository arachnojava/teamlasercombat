package tlc.data.characters;

public class TLCWeaponUpgrade extends TLCUpgradeItem
{
    private static final long serialVersionUID = -4737183424928798418L;
    public static final int COST = 2;
    public static final int SELL_VALUE = 1;
    public static final double EFFECT = 0.1;

    public TLCWeaponUpgrade()
    {
        super("Laser Gun", COST, SELL_VALUE);
    }


    @Override
    public void applyUpgrade(final TLCCharacter character)
    {
        character.setAttackValue(character.getAttackValue() + EFFECT);
    }


    @Override
    public String getDescription()
    {
        return "Attack + " + (int)(EFFECT*100) + "%";
    }


    @Override
    public String getHelpText()
    {
        return "A better weapon increases your chances of doing extra damage when you attack an opponent." +
               " The current value shows the probability of an extra attack point (AT) being added" +
               " when you attack an opponent.";
    }

    
    @Override
    public void undoUpgrade(TLCCharacter character)
    {
        if (character.getAttackValue() >= EFFECT)
            character.setAttackValue(character.getAttackValue() - EFFECT);
    }


    @Override
    public double getEffect()
    {
        return EFFECT;
    }

}
