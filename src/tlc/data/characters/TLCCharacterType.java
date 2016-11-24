package tlc.data.characters;

public enum TLCCharacterType
{
    // TODO:  Reference the GDD pages where these values originate.
    CAPTAIN ("Captain", 15, 0, 0.0),
    OFFICER ("Officer", 10, 8, 0.0),
    TROOPER ("Trooper", 5 , 4, 1.0);

    private String title;
    private int hp;
    private int cost;
    private double mv;

    private TLCCharacterType(final String description, final int hitPoints, final int price, double moveBonus)
    {
        title = description;
        hp = hitPoints;
        mv = moveBonus;
        cost = price;        
    }

    public String getTitle()
    {
        return title;
    }

    public int getDefaultHP()
    {
        return hp;
    }

    public int getCost()
    {
        return cost;
    }
    
    public double getDefaultMovementBonus()
    {
        return mv;
    }
}
