package tlc.net;

import java.io.Serializable;

public class TLCEventLogMessage implements Serializable
{
    private int characterID;
    private String text;
    
    public TLCEventLogMessage(int characterID, String text)
    {
        this.characterID = characterID;
        this.text = text;
    }
    
    
    public int getCharacterID()
    {
        return characterID;
    }
    
    
    public String getText()
    {
        return text;
    }
    
    
    private static final long serialVersionUID = -1414660342516325141L;
}
