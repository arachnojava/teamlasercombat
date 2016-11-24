package tlc.data.world;

import java.util.ArrayList;
import mhframework.MHRandom;
import mhframework.io.net.MHNetworkMessage;
import mhframework.tilemap.MHMap;
import mhframework.tilemap.MHMapCellAddress;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCArmorUpgrade;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCoinBonus;
import tlc.data.characters.TLCHealthBonus;
import tlc.data.characters.TLCMovementUpgrade;
import tlc.data.characters.TLCTokenBonus;
import tlc.data.characters.TLCUpgradeItem;
import tlc.data.characters.TLCWeaponUpgrade;
import tlc.net.TLCEventLogMessage;
import tlc.net.TLCMessageType;
import tlc.net.server.TLCGameServer;

public class TLCItemManager
{
    private static TLCItemManager INSTANCE;
    
    private ArrayList<TLCUpgradeItem> hiddenItems;
    private ArrayList<MHMapCellAddress> hiddenItemLocations;

    private TLCItemManager()
    {
    }

    
    public static TLCItemManager getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new TLCItemManager();
        
        return INSTANCE;
    }

    
    public void init()
    {
        // Create list of bonus items.
        hiddenItems = new ArrayList<TLCUpgradeItem>();
        
        hiddenItems.add(new TLCWeaponUpgrade());
        hiddenItems.add(new TLCArmorUpgrade());
        hiddenItems.add(new TLCMovementUpgrade());
        hiddenItems.add(new TLCCoinBonus());
        hiddenItems.add(new TLCTokenBonus());
        hiddenItems.add(new TLCHealthBonus());
        hiddenItems.add(new TLCHazard());
        
        
        hiddenItemLocations = new ArrayList<MHMapCellAddress>();
        for (int i = 0; i < hiddenItems.size(); i++)
        {
            MHMapCellAddress cell = null;
            do
            {
                MHMap map = TLCDataFacade.getGameWorld().getMap().getMapData();
                int row = MHRandom.random(1, map.getHeight()-2);
                int col = MHRandom.random(1, map.getWidth()-2);
                if (map.getMapCell(row, col).canWalkOn())
                    cell = new MHMapCellAddress(row, col);
            }
            while (cell == null);
            
            hiddenItemLocations.add(cell);
        }
    }
    
    
    public void checkCollision(TLCCharacter unit)
    {
        // Get unit's map location.
        MHMapCellAddress unitLocation = unit.getMapLocation();
        
        // Check map location against list of hidden bonus items.
        for (int i = 0; i < hiddenItemLocations.size(); i++)
        {
            // If found...
            if (hiddenItemLocations.get(i).equals(unitLocation))
            {
                // Apply item effect.
                hiddenItems.get(i).applyUpgrade(unit);

                // Send event log message.
                String msgText = unit.getType().getTitle() + " " + unit.getName() + " found a(n) " + hiddenItems.get(i).getName();
                TLCEventLogMessage elm = new TLCEventLogMessage(unit.getCharacterID(), msgText);
                MHNetworkMessage message = new MHNetworkMessage(TLCMessageType.EVENT_LOG, elm);
                TLCGameServer.server.sendToAll(message);
                
                // Remove item from list.
                hiddenItems.remove(i);
                hiddenItemLocations.remove(i);
            }
        }
    }
}
