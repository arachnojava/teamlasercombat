package tlc.data;

import mhframework.MHActor;

public class TLCToken extends MHActor
{
    private TLCTokenData data;
    
    public TLCToken(TLCTokenData tokenData) throws Exception
    {
        if (tokenData == null)
            throw new Exception("ERROR: tokenData is null.");
        
        data = tokenData;
    }
    
    
    public int getAttackValue()
    {
        return data.getAttackValue();
    }
    
    
    public int getDefenseValue()
    {
        return data.getDefenseValue();
    }


    public boolean isPowerToken()
    {
        if (data == null) return false;
        
        TLCTokenType type = data.getTokenType();
        
        if (type == null) return false;
        
        boolean isPT = !(type.equals(TLCTokenType.COMBAT_TOKEN) || 
                         type.equals(TLCTokenType.GRENADE_TOKEN) || 
                         type.equals(TLCTokenType.HEAL_TOKEN));
        return isPT;
    }

    
    public boolean isCombatToken()
    {
        if (data == null || data.getTokenType() == null) return true;

        return data.getTokenType().equals(TLCTokenType.COMBAT_TOKEN);
    }


    public boolean isGrenadeToken()
    {
        if (data == null || data.getTokenType() == null) return false;
        
        return data.getTokenType().equals(TLCTokenType.GRENADE_TOKEN);
    }


    public boolean isHealToken()
    {
        if (data == null || data.getTokenType() == null) return false;
        
        return data.getTokenType().equals(TLCTokenType.HEAL_TOKEN);
    }

    
    public TLCTokenType getTokenType()
    {
        return data.getTokenType();
    }


    public TLCTokenData getTokenData()
    {
        return data;
    }
}
