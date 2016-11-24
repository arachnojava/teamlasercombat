package tlc.net.server.tokens;

import mhframework.MHRandom;
import tlc.data.TLCTokenData;
import tlc.data.TLCTokenType;

public class TLCTokenGenerator
{
    private TLCPowerTokenSelector ptSelector;
    private TLCTokenProbabilities probabilities;
    
    public TLCTokenGenerator(TLCTokenProbabilities probabilities)
    {
        this.probabilities = probabilities;
        ptSelector = new TLCPowerTokenSelector();
    }
    
    
    private TLCTokenType selectTokenType()
    {
        double pt = probabilities.getChancePowerToken();
        double ht = probabilities.getChanceHealToken();
        double gt = probabilities.getChanceGrenadeToken();
        double rand = Math.random();

        if (rand < gt)
            return TLCTokenType.GRENADE_TOKEN;
        else if (rand < gt + ht)
            return TLCTokenType.HEAL_TOKEN;
        else if (rand < gt + ht + pt)
        {
            try
            {
                TLCTokenType powerToken = ptSelector.selectToken();
                return powerToken;
            }
            catch (Exception e)
            {
                return TLCTokenType.COMBAT_TOKEN;
            }
        }
        else
            return TLCTokenType.COMBAT_TOKEN;
    }
    
    
    private TLCTokenData createToken(TLCTokenType tokenID)
    {
        switch (tokenID)
        {
            case COMBAT_TOKEN:  return createCombatToken();
            case HEAL_TOKEN:    return createHealToken();
            case GRENADE_TOKEN: return createGrenadeToken();
        }

        return createPowerToken(tokenID);
    }
    
    
    private TLCTokenData createGrenadeToken()
    {
        TLCTokenData t = new TLCTokenData();
        t.setTokenType(TLCTokenType.GRENADE_TOKEN);
        t.setAttackValue(2);
        t.setDefenseValue(0);
        
        return t;
    }


    private TLCTokenData createHealToken()
    {
        TLCTokenData t = new TLCTokenData();
        t.setTokenType(TLCTokenType.HEAL_TOKEN);
        t.setAttackValue(0);
        t.setDefenseValue(0);
        
        return t;
    }

    
    private TLCTokenData createPowerToken(TLCTokenType type)
    {
        TLCTokenData t = new TLCTokenData();
        t.setTokenType(type);
        t.setAttackValue(0);
        t.setDefenseValue(0);
        
        return t;
    }
    

    public TLCTokenData generateToken()
    {
        TLCTokenType type = selectTokenType();
        return createToken(type);
    }
    
    
    private TLCTokenData createCombatToken()
    {
        TLCTokenData t = new TLCTokenData();
        t.setTokenType(TLCTokenType.COMBAT_TOKEN);
        t.setAttackValue(MHRandom.rollD6());
        t.setDefenseValue(MHRandom.random(1, t.getAttackValue()));
        
        return t;
    }
}
