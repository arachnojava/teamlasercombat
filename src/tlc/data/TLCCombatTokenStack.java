package tlc.data;

public class TLCCombatTokenStack
{
    private int attack, defense, count;
    private TLCToken token = null;
    
    public TLCCombatTokenStack(TLCToken token)
    {
        attack = token.getAttackValue();
        defense = token.getDefenseValue();
        count = 0;
    }

    
    public void removeToken()
    {
        count--;
    }


    public int getAttackValue()
    {
        return attack;
    }

    
    public void addToken()
    {
        count++;
    }


    public int getDefenseValue()
    {
        return defense;
    }


    public int getCount()
    {
        return count;
    }
    
    @Override
    public String toString()
    {
        return "Combat Token (" + getAttackValue() + "/" + getDefenseValue() + ") x" + getCount();
    }


    public TLCToken getToken()
    {
        if (token == null)
        {
            TLCTokenData data = new TLCTokenData();
            data.setAttackValue(this.attack);
            data.setDefenseValue(this.defense);
            data.setTokenType(TLCTokenType.COMBAT_TOKEN);
            
            try
            {
                token = new TLCToken(data);
            } catch (Exception e)
            {
                e.printStackTrace();
                
                try
                {
                    token = new TLCToken(data);
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }
        
        return token;
    }
}