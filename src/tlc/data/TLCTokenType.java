package tlc.data;

public enum TLCTokenType
{
    PT_VAMPIRE            (0, "Vampire",            "Vampire: (AT:4) If the attack does damage, the attacker heals one hit point per unit of damage dealt."),
    PT_LUCKY_SHOT         (1, "Lucky Shot",         "Lucky Shot: (AT:3) If the attack does damage, the damage amount is doubled."),
    PT_DESPERATION        (2, "Desperation",        "Desperation: (AT:4) The attack value is normally 4, but if the attacker is the only remaining member of the team, then the attack value is 10."),
    PT_BLAZE_OF_GLORY     (3, "Blaze of Glory",     "Blaze of Glory: (AT:5) If the attack does damage, the damage amount is doubled, but if the attack is blocked, the attacker takes 5 points of damage."),
    PT_REFLECTION         (4, "Reflection",         "Reflection: Any attack, regardless of power, is reflected back at the attacker. The defender takes no damage."),
    PT_RESOLVE            (5, "Resolve",            "Resolve: Defense value goes up as the character health goes down.  DF = maxHP - currentHP"),
    PT_LUCKY_BREAK        (6, "Lucky Break",        "Lucky Break: (DF:5) If the attack is blocked, any excess defense points are converted to health points for your unit."),
    PT_SHATTER_AND_SCATTER(7, "Shatter and Scatter","Shatter and Scatter: (DF:4) If the attack is blocked, each member of the attacking team takes two points of damage."),
    COMBAT_TOKEN          (8, "Combat Token",       "Basic token for attacking and defending."),
    HEAL_TOKEN            (9, "Heal Token",         "Restores one point of health to the character who uses it."),
    GRENADE_TOKEN         (10,"Grenade Token",      "Aggressive token for Troopers only. Does radial damage. Cannot be blocked. Has no defensive value.");
    
    private int index;
    private String name;
    private String description;
    
    private TLCTokenType(int i, String n, String d)
    {
        index = i;
        name = n;
        description = d;
    }
    
    
    public static int getCount()
    {
        return values().length;
    }
    
    
    public int getIndex()
    {
        return index;
    }

    
    public String getName()
    {
        return name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    @Override
    public String toString()
    {
        return "Token Type " + getIndex() + ": " + getName();
    }
}
