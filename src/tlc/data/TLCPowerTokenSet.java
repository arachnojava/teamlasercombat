package tlc.data;

public class TLCPowerTokenSet
{
    private boolean[] tokenArray;
    
    
    public TLCPowerTokenSet()
    {
        tokenArray = new boolean[TLCTokenType.values().length];
        reset();
    }
    
    
    public boolean isAttackToken(int typeID)
    {
        return  typeID >= 0 && typeID < 4;
    }

    
    public boolean isDefenseToken(int typeID)
    {
        return  typeID >= 4 && typeID < 8;
    }

    
    public boolean hasToken(TLCTokenType token)
    {
        return hasToken(token.getIndex());
    }

    
    public void addToken(TLCTokenType token)
    {
        addToken(token.getIndex());
    }

    
    public void removeToken(TLCTokenType token)
    {
        removeToken(token.getIndex());
    }


    public void reset()
    {
        for (int i = 0; i < tokenArray.length; i++)
            tokenArray[i] = false;
    }


    public boolean hasToken(int tokenTypeIndex)
    {
        return tokenArray[tokenTypeIndex];
    }

    
    public void addToken(int tokenTypeIndex)
    {
        tokenArray[tokenTypeIndex] = true;
    }

    
    public void removeToken(int tokenTypeIndex)
    {
        tokenArray[tokenTypeIndex] = false;
    }


    public int getCount()
    {
        return tokenArray.length;
    }
    
    
    public String toString()
    {
        String s = "";
        
        for (int i = 0; i < tokenArray.length; i++)
        {
            if (tokenArray[i])
                s += TLCTokenType.values()[i].getName() + " -- " + TLCTokenType.values()[i].getDescription() + "\n"; 
        }
        
        return s;
    }
}
