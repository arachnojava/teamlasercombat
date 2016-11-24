package tlc.net.server;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TLCAttackNotification implements Serializable
{
    public int attackerID;
    public int defenderID;
}
