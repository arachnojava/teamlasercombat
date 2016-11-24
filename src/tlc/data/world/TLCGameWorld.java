package tlc.data.world;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import mhframework.MHActor;
import mhframework.MHDisplayModeChooser;
import mhframework.MHPoint;
import mhframework.MHRandom;
import mhframework.MHRenderable;
import mhframework.tilemap.MHDiamondMap;
import mhframework.tilemap.MHIsoMouseMap;
import mhframework.tilemap.MHIsometricMap;
import mhframework.tilemap.MHMapCell;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHObjectFactory;
import mhframework.tilemap.MHTileMap;
import mhframework.tilemap.MHTileMapDirection;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCCharacter;
import tlc.ui.hud.actions.TLCAttackDirections;

public class TLCGameWorld implements MHRenderable
{
    private MHIsometricMap map;
    private MHObjectFactory objectFactory;
    private ArrayList<MHMapCellAddress> spawnPoints;
    
    public TLCGameWorld()
    {
        objectFactory = new TLCObjectFactory(this);
        spawnPoints = new ArrayList<MHMapCellAddress>();
    }
    
    
    public void loadMap(String mapFileName)
    {
        map = new MHDiamondMap(mapFileName, objectFactory);
        int width = MHDisplayModeChooser.getWidth();
        int height = MHDisplayModeChooser.getHeight() + MHIsoMouseMap.HEIGHT * 4;
        map.setCursorOn(false);
        map.setScreenSpace(0, 0, width, height);
        int row = map.getMapData().getHeight()/2;
        int column = map.getMapData().getWidth()/2;
        centerOn(row, column);
        
        // Remove the spawn point markers from the map.
        for (MHMapCellAddress a : spawnPoints)
            map.getMapData().getMapCell(a.row, a.column).setLayer(TLCObjectFactory.SPAWN_POINT_LAYER, null);
    }
    
    
    public void centerOn(int row, int column)
    {
        if (map != null)
            map.centerOn(row, column);
    }

    
    public MHTileMap getMap()
    {
        return map;
    }
    
    @Override
    public void render(final Graphics2D g)
    {
        map.render(g);
    }


    @Override
    public void advance()
    {
        map.advance();
       
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
        {
            if (c.getHealth() <= 0 && c.getMapLocation() != null)
            {
                    c.setMapLocation(null);
                    TLCDataFacade.getGameWorld().putObject(null, c.getMapLocation(), MHMapCell.WALL_LAYER);
            }
            else
            {
                TLCDataFacade.getGameWorld().putObject(c, c.getMapLocation(), MHMapCell.WALL_LAYER);
            }
        }
    }


    public void addSpawnPoint(MHMapCellAddress location)
    {
        if (!spawnPoints.contains(location))
            spawnPoints.add(location);
    }
    
    
    public void clearSpawnPoints()
    {
        spawnPoints.clear();
    }


    public MHMapCellAddress chooseSpawnPoint()
    {
        if (spawnPoints.size() < 1) return null;
        
        MHMapCellAddress location;
        int sp = 0;
        
        do
        {
            sp = MHRandom.random(0, spawnPoints.size()-1);
            location = spawnPoints.get(sp);
        }
        while (!canWalkOn(location.row, location.column) && isSpawnPointUsed(location));

        return location;
    }


    public boolean isSpawnPointUsed(MHMapCellAddress location)
    {
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
        {
            if (location != null && c.getMapLocation() != null && c.getMapLocation().equals(location))
                return true;
        }
        return false;
    }


    public void putObject(MHActor c, MHMapCellAddress location, int layer)
    {
        if (location != null && map != null && map.getMapData() != null)
        {
            map.getMapData().getMapCell(location.row, location.column).setLayer(layer, c);
            
            if (c != null)
            {
                Point p = map.plotTile(location.row, location.column);
                p.x = (p.x + MHIsoMouseMap.WIDTH/2) - c.getWidth()/2;
                p.y = (p.y + MHIsoMouseMap.HEIGHT) - c.getHeight();
                c.setLocation(p.x, p.y);
            }
        }
    }
    
    
//    public void putFineObject(TLCCharacter c)
//    {
//        MHMapCellAddress location = c.getMapLocation();
//        if (location == null)
//            return;
//        
//        Point p = map.plotTile(location.row, location.column);
//        p.x = (p.x + MHIsoMouseMap.WIDTH/2) - c.getWidth()/2;
//        p.y = (p.y + MHIsoMouseMap.HEIGHT) - c.getHeight();
//        c.setLocation(p.x, p.y);
//        
//        if (map != null)
//            map.placeFineObject(c);
//    }


    public MHMapCell getMapCell(int r, int c)
    {
        return map.getMapData().getMapCell(r, c);
    }


    public void mouseMoved(MouseEvent e)
    {
        map.mouseMoved(e);
    }


    public void scrollMap(int dx, int dy)
    {
        map.scrollMap(dx, dy);
    }


    public boolean canWalkOn(int r, int c)
    {
        if (r <= 0 || r >= map.getMapData().getHeight()) return false;
        if (c <= 0 || c >= map.getMapData().getWidth()) return false;
        
        boolean result = true;
        MHMapCell cell = map.getMapData().getMapCell(r, c);
        
        if (cell.getLayer(MHMapCell.FLOOR_LAYER) == null)
            result = false;

        if (cell.getLayer(MHMapCell.OBSTACLE_LAYER) != null)
            result = false;

        if (cell.getLayer(MHMapCell.WALL_LAYER) != null)
            result = false;

//        if (map.isObjectAt(r, c))
//            result = false;
        
        return result;
    }


    public Point worldToScreen(MHPoint location)
    {
        return map.worldToScreen(location);
    }


    public synchronized MHMapCellAddress findCharacter(TLCCharacter selectedCharacter)
    {
        if (map == null || map.getMapData() == null)
            return null;
        
        for (int r = 0; r < map.getMapData().getHeight(); r++)
            for (int c = 0; c < map.getMapData().getWidth(); c++)
            {
                MHMapCell cell = map.getMapData().getMapCell(r, c);
                
                try
                {
                    TLCCharacter character = (TLCCharacter) (cell.getLayer(MHMapCell.WALL_LAYER)); 
                    
                    // DEBUG
                    if (selectedCharacter.getName().equalsIgnoreCase("Test"))
                        System.out.println("TEST: findCharacter() found a character.");
                    
                    if (character != null && character.getCharacterID() == selectedCharacter.getCharacterID())
                    {
                        System.out.println("CHARACTER FOUND AT (" + r + ", " + c + ")");
                        return new MHMapCellAddress(r, c);
                    }
                }
                catch (ClassCastException cce)
                {
                    System.err.println("TLCGameWorld.findCharacter():" + cce.getMessage());
                }
            }
        
        return null;
    }


    public TLCAttackDirections findAttackDirections(TLCCharacter c)
    {
        
        MHMapCellAddress start = c.getMapLocation();
        TLCAttackDirections dir = new TLCAttackDirections();
        
        String str = traceLOS(c.getTeamID(), start, MHTileMapDirection.NORTH);
        if (str != null) dir.setDirection(MHTileMapDirection.NORTH, str);
        
        str = traceLOS(c.getTeamID(), start, MHTileMapDirection.NORTHEAST);
        if (str != null) dir.setDirection(MHTileMapDirection.NORTHEAST, str);
        
        str = traceLOS(c.getTeamID(), start, MHTileMapDirection.EAST);
        if (str != null) dir.setDirection(MHTileMapDirection.EAST, str);
        
        str = traceLOS(c.getTeamID(), start, MHTileMapDirection.SOUTHEAST);
        if (str != null) dir.setDirection(MHTileMapDirection.SOUTHEAST, str);
        
        str = traceLOS(c.getTeamID(), start, MHTileMapDirection.SOUTH);
        if (str != null) dir.setDirection(MHTileMapDirection.SOUTH, str);
        
        str = traceLOS(c.getTeamID(), start, MHTileMapDirection.SOUTHWEST);
        if (str != null) dir.setDirection(MHTileMapDirection.SOUTHWEST, str);
        
        str = traceLOS(c.getTeamID(), start, MHTileMapDirection.WEST);
        if (str != null) dir.setDirection(MHTileMapDirection.WEST, str);
        
        str = traceLOS(c.getTeamID(), start, MHTileMapDirection.NORTHWEST);
        if (str != null) dir.setDirection(MHTileMapDirection.NORTHWEST, str);
        
        return dir;
    }

    
    private String traceLOS(int teamID, MHMapCellAddress start, MHTileMapDirection dir)
    {
        MHMapCellAddress addr = start;
        
        do
        {
            addr = getMap().tileWalk(addr, dir);
            
            if (addr == null) 
                return null;
        }
        while (canWalkOn(addr.row, addr.column));
        // TODO: That should be "canShootOver".
        
        MHActor target = getMap().getMapData().getMapCell(addr.row, addr.column).getLayer(MHMapCell.WALL_LAYER);
        if (target instanceof TLCCharacter)
        {
            TLCCharacter c = (TLCCharacter) target;
            
            // If the character is on our team, it's not a target.
            if (c.getTeamID() == teamID)
                return null;
            
            return c.getName();
        }
        else // Recognize containers as targets.
        {
            if (target instanceof TLCContainer)
            {
                return "Container";
            }
        }
        return null;
    }

//    public void moveFineObject(TLCCharacter character,
//            MHMapCellAddress destination)
//    {
//        map.moveFineObject(character, destination);
//    }
}
