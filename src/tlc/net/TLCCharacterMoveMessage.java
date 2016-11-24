package tlc.net;

import mhframework.io.net.MHNetworkMessage;

public class TLCCharacterMoveMessage extends MHNetworkMessage
{
    private static final long serialVersionUID = 4156000639214852914L;
    private int charID, oldRow, oldCol, newRow, newCol;
    
    public TLCCharacterMoveMessage(int charID, int oldRow, int oldCol, int newRow, int newCol)
    {
        super.setMessageType(TLCMessageType.CHARACTER_MOVE);
        this.charID = charID;
        this.oldRow = oldRow;
        this.oldCol = oldCol;
        this.newRow = newRow;
        this.newCol = newCol;
    }

    public int getCharID()
    {
        return charID;
    }

    public int getOldRow()
    {
        return oldRow;
    }

    public int getOldCol()
    {
        return oldCol;
    }

    public int getNewRow()
    {
        return newRow;
    }

    public int getNewCol()
    {
        return newCol;
    }
}
