package tlc.ui.hud.actions;

import java.util.HashMap;
import mhframework.tilemap.MHTileMapDirection;

public class TLCAttackDirections
{
    private HashMap<MHTileMapDirection, String> directions;
    
    public void setDirection(MHTileMapDirection dir, String targetName)
    {
        if (directions == null)
            directions = new HashMap<MHTileMapDirection, String>();
        
        directions.put(dir, targetName);
    }
    
    
    public String getDirection(MHTileMapDirection dir)
    {
        return directions.get(dir);
    }
    
    
    public boolean isEmpty()
    {
        if (directions == null)
            return true;
        
        return directions.isEmpty();
    }
}
