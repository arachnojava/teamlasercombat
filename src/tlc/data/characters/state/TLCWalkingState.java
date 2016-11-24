package tlc.data.characters.state;

import mhframework.MHActor;
import mhframework.ai.state.MHState;
import mhframework.io.net.MHNetworkMessage;
import mhframework.tilemap.MHMap;
import mhframework.tilemap.MHMapCell;
import mhframework.tilemap.MHMapCellAddress;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCCharacter;
import tlc.data.world.TLCItemManager;
import tlc.data.world.TLCPickupItem;
import tlc.net.TLCEventLogMessage;
import tlc.net.TLCMessageType;
import tlc.net.server.TLCGameServer;

public class TLCWalkingState implements MHState
{
    private static final TLCWalkingState INSTANCE = new TLCWalkingState();
    private static final int MOVE_DELAY = 5;
    private static int moveDelayCounter = 0;
    
    public static TLCWalkingState getInstance()
    {
        return INSTANCE;
    }
    
    
    @Override
    public void enter(Object subject)
    {
        // DEBUG
        System.out.println("\t\t\t\t"+this.getClass().getName()+".enter()");
        TLCCharacter c = (TLCCharacter) subject;
//        if (TLCDataFacade.getWhoseTurn() != c.getTeamID() || c.getActionPoints() < 1)
//        {
//            c.changeState(TLCStandingState.getInstance());
//            return;
//        }
        c.isTakingTurn = true;
//        MHMapCellAddress destination = c.path.get(c.nextNodeIndex);
//        MHMapCellAddress origin = c.getMapLocation();
//        Point d = TLCDataFacade.getGameWorld().getMap().plotTile(destination.row, destination.column);
//        Point o = TLCDataFacade.getGameWorld().getMap().plotTile(origin.row, origin.column);
//        c.setHorizontalSpeed((d.x-o.x)/2);
//        c.setVerticalSpeed((d.y-o.y)/2);
    }

    
    public void execute(Object subject)
    {
        // DEBUG
        //System.out.println("\t\t\t\t"+this.getClass().getName()+".execute()");
        
        TLCCharacter c = (TLCCharacter) subject;
        
        // DEBUG
        //System.out.println("\t\t" + c.getName() + " at " + c.getMapLocation());
        
        TLCDataFacade.getGameWorld().putObject(null, c.getMapLocation(), MHMapCell.WALL_LAYER);
        MHMapCellAddress nextNode = c.getPath().get(c.nextNodeIndex);
        c.setMapLocation(nextNode);
        TLCDataFacade.getGameWorld().putObject(c, c.getMapLocation(), MHMapCell.WALL_LAYER);
        
        // Check for bonus items in the space we just entered.
        TLCItemManager.getInstance().checkCollision(c);
        
        // DEBUG
        //TLCEventLogDisplay.addMessage(Color.LIGHT_GRAY, c.getName() + " walking to " + nextNode);
        
        if (++moveDelayCounter > MOVE_DELAY)
        {
            moveDelayCounter = 0;
            c.nextNodeIndex++;
        }
        
        if (c.nextNodeIndex >= c.path.size())
        {
            c.nextNodeIndex = 0;
            c.path = null;
            c.changeState(TLCStandingState.getInstance());
        }
    }
    
    
//    @Override
//    public void execute(Object subject)
//    {
//        TLCCharacter c = (TLCCharacter) subject;
//        Point basePt = TLCDataFacade.getGameWorld().getMap().calculateBasePoint(c);
//        
//        // If basePt on new cell, move character reference to new cell.
//        MHMapCellAddress current = TLCDataFacade.getGameWorld().getMap().mapMouse(basePt);
//        if (!c.getMapLocation().equals(current))
//        {
//            TLCDataFacade.getGameWorld().putObject(null, c.getMapLocation(), MHMapCell.WALL_LAYER);
//            TLCDataFacade.getGameWorld().putObject(c, current, MHMapCell.WALL_LAYER);
//            c.setMapLocation(current);
//            c.nextNodeIndex++;
//            c.changeState(TLCWalkingState.getInstance());
//        }
//        // If arrived at goal, revert to standing state.
//        else if (c.getMapLocation().equals(c.path.get(c.path.size()-1)))
//        {
//            c.changeState(TLCWalkingState.getInstance());
//        }
//    }

    @Override
    public void exit(Object subject)
    {
        // DEBUG
        System.out.println("\t\t\t\t"+this.getClass().getName()+".exit()");

        TLCCharacter c = (TLCCharacter) subject;
        c.setHorizontalSpeed(0);
        c.setVerticalSpeed(0);
        
        moveDelayCounter = 0;
        
        c.isTakingTurn = false;

        // Check board space for item.
        MHMap map = TLCDataFacade.getGameWorld().getMap().getMapData();
        MHMapCellAddress location = c.getMapLocation();
        MHMapCell cell = map.getMapCell(location.row, location.column);
        MHActor itemLayer = cell.getLayer(MHMapCell.ITEM_LAYER);
        if (itemLayer != null)
        {
            // DEBUG:
            System.out.println(c.getName() + " picked up an item.");
            
            // Cast itemLayer to an upgrade item and use it.
            TLCPickupItem item = (TLCPickupItem) itemLayer;
            item.apply(c);

            String text = c.getName() + " picked up a(n) " + item.getName();
            TLCEventLogMessage event = new TLCEventLogMessage(c.getCharacterID(), text);
            MHNetworkMessage elm = new MHNetworkMessage(TLCMessageType.EVENT_LOG, event);
            
            TLCGameServer.server.sendToAll(elm);
            
            cell.setLayer(MHMapCell.ITEM_LAYER, null);
        }
        
        // Send updated character to server.
        TLCDataFacade.getInstance(TLCMain.DATA_ID).getGameClient().sendCharacterUpdateMessage(c);
    }
}
