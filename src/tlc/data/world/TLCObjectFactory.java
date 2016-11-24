package tlc.data.world;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import mhframework.MHActor;
import mhframework.MHRandom;
import mhframework.media.MHResourceManager;
import mhframework.tilemap.MHMapCell;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHObjectFactory;
import tlc.data.characters.TLCArmorUpgrade;
import tlc.data.characters.TLCCoinBonus;
import tlc.data.characters.TLCHealthBonus;
import tlc.data.characters.TLCMovementUpgrade;
import tlc.data.characters.TLCTokenBonus;
import tlc.data.characters.TLCUpgradeItem;
import tlc.data.characters.TLCWeaponUpgrade;

public class TLCObjectFactory implements MHObjectFactory
{
    static final int SPAWN_POINT_LAYER = MHMapCell.OBSTACLE_LAYER;
    static final int CONTAINER_LAYER = MHMapCell.WALL_LAYER;
    private static final int SPAWN_POINT_ID = 1;
    private static final int NULL_OBSTACLE_ID = 0;
    private static final ArrayList<Integer> CONTAINER_IDS = new ArrayList<Integer>();
    
    private TLCGameWorld gameWorld;
    
    public TLCObjectFactory(TLCGameWorld world)
    {
        this.gameWorld = world;
        CONTAINER_IDS.add(4);
    }
    
    
    @Override
    public MHActor getObject(int layer, int tileID, MHMapCellAddress location)
    {
        switch (layer)
        {
            case MHMapCell.FLOOR_LAYER:
                break;
            case MHMapCell.FLOOR_DETAIL_LAYER:
                break;
            case MHMapCell.ITEM_LAYER:
                break;
            case MHMapCell.OBSTACLE_LAYER:
                switch (tileID)
                {
                    case TLCObjectFactory.SPAWN_POINT_ID:
                        gameWorld.addSpawnPoint(location);
                        break;
                }
                break;
            case MHMapCell.WALL_LAYER:
                if (CONTAINER_IDS.contains(tileID))
                {
                    return new TLCContainer(selectRandomUpgrade());
                    //TLCContainer container = new TLCContainer(selectRandomUpgrade());
                    //gameWorld.getMapCell(location.row, location.column).setLayer(CONTAINER_LAYER, container);
                }
                break;
            case MHMapCell.WALL_DETAIL_LAYER:
                break;
        }
        return null;
    }
    
    
    private TLCUpgradeItem selectRandomUpgrade()
    {
        int itemType = MHRandom.random(0, 5);
        switch (itemType)
        {
            case 0:  return new TLCArmorUpgrade();
            case 1:  return new TLCWeaponUpgrade();
            case 2:  return new TLCHealthBonus();
            case 3:  return new TLCMovementUpgrade();
            case 4:  return new TLCTokenBonus();
            default: return new TLCCoinBonus();
        }
    }
}


class NullActor extends MHActor
{
    public static final MHActor INSTANCE = new NullActor();
    public static final Image img = MHResourceManager.loadImage("images/F0000000.png");
    
    @Override
    public void render(Graphics2D g)
    {
        //g.drawImage(img, 0, 0, null);
    }

    @Override
    public void render(Graphics2D g, int rx, int ry)
    {
        g.drawImage(img, rx, ry, null);
    }
    

    @Override
    public Image getImage()
    {
        return img;
    }

    @Override
    public void advance()
    {
    }  
}