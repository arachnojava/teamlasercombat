package tlc.data;

import java.io.Serializable;
import mhframework.io.MHSerializable;
import tlc.data.characters.TLCCharacterType;

public class TLCTokenData implements MHSerializable
{
    private static final long serialVersionUID = -1549744724308225526L;
    private long tokenID;
    private TLCCharacterType characterType;
    private TLCTokenType tokenType;
    private int attackValue;
    private int defenseValue;

    
    @Override
    public Serializable getSerializableVersion()
    {
        return this;
    }


    public long getTokenID()
    {
        return tokenID;
    }


    public void setTokenID(long tokenID)
    {
        this.tokenID = tokenID;
    }


    public TLCCharacterType getCharacterType()
    {
        return characterType;
    }


    public void setCharacterType(TLCCharacterType characterType)
    {
        this.characterType = characterType;
    }


    public TLCTokenType getTokenType()
    {
        return tokenType;
    }


    public void setTokenType(TLCTokenType tokenType)
    {
        this.tokenType = tokenType;
    }


    public int getAttackValue()
    {
        return attackValue;
    }


    public void setAttackValue(int attackValue)
    {
        this.attackValue = attackValue;
    }


    public int getDefenseValue()
    {
        return defenseValue;
    }


    public void setDefenseValue(int defenseValue)
    {
        this.defenseValue = defenseValue;
    }
    
    public String toString()
    {
        String s = getTokenType().getName() + " (" + getAttackValue() + "/" + getDefenseValue() + ")";
        
        return s;
    }
}
