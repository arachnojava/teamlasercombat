package tlc.data.characters;

public class TLCCombatTraining extends TLCUpgradeItem
{
    private static final long serialVersionUID = 7949503497968796478L;
    public static final int COST = 1;
    public static final int SELL_VALUE = 1;
    public static final double EFFECT = 0.05;

    public TLCCombatTraining()
    {
        super("Combat Training", COST, SELL_VALUE);
    }


    @Override
    public void applyUpgrade(final TLCCharacter character)
    {
        character.setTrainingLevel(character.getTrainingLevel() + EFFECT);
    }


    @Override
    public String getDescription()
    {
        return "Luck + " + (int)(EFFECT*100) + "%";
    }

    @Override
    public String getHelpText()
    {
        return "Combat training makes it more likely that you will " +
               "get an additional random point added to attack (AT) " +
               "or defense (DF) during combat.  (Maximum value is 100%. " +
               "Training cannot be sold.)";
    }


    @Override
    public void undoUpgrade(TLCCharacter character)
    {
        character.setTrainingLevel(character.getTrainingLevel() - EFFECT);
    }


    @Override
    public double getEffect()
    {
        return EFFECT;
    }

}
