package tlc.data.characters;

public enum TLCCharacterGender
{
    MALE   ("Male",   0.0, 0.1),
    FEMALE ("Female", 0.1, 0.0);

    private final double attackBonus, defenseBonus;
    private final String name;

    private TLCCharacterGender(final String genderName, final double atBonus, final double dfBonus)
    {
        name = genderName;
        attackBonus = atBonus;
        defenseBonus = dfBonus;
    }


    public String getName()
    {
        return name;
    }

    public double getAttackBonus()
    {
        return attackBonus;
    }

    public double getDefenseBonus()
    {
        return defenseBonus;
    }
}

