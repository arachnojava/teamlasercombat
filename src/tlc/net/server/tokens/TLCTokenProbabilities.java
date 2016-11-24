package tlc.net.server.tokens;

public class TLCTokenProbabilities
{
    private double chancePowerToken;
    private double chanceHealToken;
    private double chanceGrenadeToken;
    
    public TLCTokenProbabilities(double pt, double ht, double gt)
    {
        this.chanceGrenadeToken = gt;
        this.chanceHealToken = ht;
        this.chancePowerToken = pt;
    }

    public double getChancePowerToken()
    {
        return chancePowerToken;
    }

    public double getChanceHealToken()
    {
        return chanceHealToken;
    }

    public double getChanceGrenadeToken()
    {
        return chanceGrenadeToken;
    }
}
