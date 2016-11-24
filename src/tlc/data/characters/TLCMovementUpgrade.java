package tlc.data.characters;

public class TLCMovementUpgrade extends TLCUpgradeItem
{
    private static final long serialVersionUID = -5564573009896219184L;
    public static final int COST = 1;
    public static final int SELL_VALUE = 1;
    public static final double EFFECT = 0.1;

    public TLCMovementUpgrade()
    {
        super("Boots", COST, SELL_VALUE);
    }


    @Override
    public void applyUpgrade(final TLCCharacter character)
    {
        character.setMovementValue(character.getMovementValue() + EFFECT);
    }


    @Override
    public String getDescription()
    {
        return "Movement + " + (int)(EFFECT*100) + "%";
    }


    @Override
    public String getHelpText()
    {
        return "Better boots may help you to move farther on each turn.  This could help you line up shots, get out of an attacker's line of fire, or reach bonus items before your opponents do.";
    }

    
    @Override
    public void undoUpgrade(TLCCharacter character)
    {
        if (character.getMovementValue() >= EFFECT)
            character.setMovementValue(character.getMovementValue() - EFFECT);
    }


    @Override
    public double getEffect()
    {
        return EFFECT;
    }

}
