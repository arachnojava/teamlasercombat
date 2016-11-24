package tlc.net;

import java.io.Serializable;
import mhframework.tilemap.MHTileMapDirection;
import tlc.data.TLCTokenData;

@SuppressWarnings("serial")
public class TLCAttackMessage implements Serializable
{
    private int attackingCharacterID;
    private TLCTokenData attackToken;
    private MHTileMapDirection direction;
    
    public TLCAttackMessage(int attackerID, TLCTokenData attackToken, MHTileMapDirection attackDirection)
    {
        this.attackingCharacterID = attackerID;
        this.attackToken = attackToken;
        this.direction = attackDirection;
    }

    public int getAttackingCharacterID()
    {
        return attackingCharacterID;
    }

    public TLCTokenData getAttackToken()
    {
        return attackToken;
    }

    public MHTileMapDirection getDirection()
    {
        return direction;
    }
}
