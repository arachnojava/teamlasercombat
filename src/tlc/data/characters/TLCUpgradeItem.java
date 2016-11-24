package tlc.data.characters;

import java.io.Serializable;
import tlc.data.characters.TLCBuySell;


public abstract class TLCUpgradeItem implements TLCBuySell, Serializable
{
    private static final long serialVersionUID = -8368689707193726102L;
    private final String name;
    private final int cost;
    private final int sellValue;

    protected TLCUpgradeItem(final String name, final int cost, final int sellPrice)
    {
        this.name = name;
        this.cost = cost;
        this.sellValue = sellPrice;
    }


    public String getName()
    {
        return name;
    }


    public int cost()
    {
        return cost;
    }


    public int sellValue()
    {
        return sellValue;
    }

    public abstract String getDescription();
    public abstract String getHelpText();
    public abstract void applyUpgrade(TLCCharacter character);
    public abstract void undoUpgrade(TLCCharacter character);
    public abstract double getEffect();
}
