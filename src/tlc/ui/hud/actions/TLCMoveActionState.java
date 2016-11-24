package tlc.ui.hud.actions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import mhframework.ai.path.MHNodePath;
import mhframework.ai.path.MHPathFinder;
import mhframework.event.MHMouseListener;
import mhframework.event.MHMouseMotionListener;
import mhframework.tilemap.MHIsoMouseMap;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHTileMap;
import mhframework.tilemap.MHTileMapDirection;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCCharacter;
import tlc.net.TLCCharacterMoveMessage;
import tlc.net.TLCMessageType;
import tlc.net.client.TLCGameClient;
import tlc.net.server.ai.TLCAIManager;

public class TLCMoveActionState implements TLCActionsMenuState
{
    TLCActionsMenu menu;
    ArrayList<MHMapCellAddress> destinations = new ArrayList<MHMapCellAddress>();
    
    public TLCMoveActionState(TLCActionsMenu menu)
    {
        // Clear out the mouse click data.
        MHMouseListener.getClickPoint();
        
        this.menu = menu;
        TLCCharacter character = TLCActionsMenu.getSelectedCharacter();
        TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
        TLCGameClient client = data.getGameClient();
        client.send(TLCMessageType.REQUEST_MOVE_POINTS, character.getSerializableVersion());
        
        // Wait for movement points to return from server.
        while (character.getMovementPoints() <= 0);
        
        // DEBUG
        //System.out.println(character.getName() + " has " + character.getMovementPoints() + " movement points.");
        
        // Generate all paths accessible within movement points.
        int startRow = character.getMapLocation().row - character.getMovementPoints();
        int endRow = character.getMapLocation().row + character.getMovementPoints();
        int startCol = character.getMapLocation().column - character.getMovementPoints();
        int endCol = character.getMapLocation().column + character.getMovementPoints();

        MHMapCellAddress startLoc = character.getMapLocation();
        MHTileMap map = TLCDataFacade.getGameWorld().getMap();
        MHTileMapDirection[] directions = new MHTileMapDirection[4];
        directions[0] = MHTileMapDirection.NORTHEAST;
        directions[1] = MHTileMapDirection.SOUTHEAST;
        directions[2] = MHTileMapDirection.SOUTHWEST;
        directions[3] = MHTileMapDirection.NORTHWEST;
        for (int r = startRow; r <= endRow; r++)
        {
            for (int c = startCol; c <= endCol; c++)
            {
                MHMapCellAddress goalLoc = new MHMapCellAddress(r, c);

                // If goalLoc is unobstructed, let's see if we have a path to it.
                if (TLCDataFacade.getGameWorld().canWalkOn(r, c))
                {
                    MHNodePath p = MHPathFinder.aStarSearch(startLoc, goalLoc, map, directions);
                    if (p == null)
                    {
                        //System.out.println("No path to " + goalLoc);
                    }
                    else if (p.size() > character.getMovementPoints())
                    {
                        //System.out.println(""+goalLoc+" is too far away.");
                    }
                    else
                    {
                        destinations.add(goalLoc);
                        // DEBUG
                        //System.out.println("Success!  Added " + goalLoc);
                    }
                }
                // DEBUG
                //else System.out.println("...Can't walk on " + goalLoc);
            }
        }
    }


    @Override
    public void advance()
    {
    }


    @Override
    public void render(Graphics2D g)
    {
        TLCAIManager.waitForPlayers();
        
        Point mouse = MHMouseMotionListener.getMousePoint();
        Point anchor = TLCDataFacade.getGameWorld().getMap().getScreenAnchor();
        final int s = 32;

        for (int i = 0; i < destinations.size(); i++)
        {
            // Display possible destinations.
            MHMapCellAddress a = destinations.get(i);
            Point p = TLCDataFacade.getGameWorld().getMap().plotTile(a.row, a.column);
            g.setColor(Color.WHITE);
            int x = (p.x + MHIsoMouseMap.WIDTH/2 - s) - anchor.x;
            int y = (p.y + MHIsoMouseMap.HEIGHT/2 - s/2) - anchor.y;
            Rectangle2D rect = new Rectangle2D.Double(x, y, s*2, s);
            g.drawOval((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
            
            // Highlight selected destination.
            if (rect.contains(mouse))
            {
                g.fillOval((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
            
                Point click = MHMouseListener.getClickPoint(); 
                if (click != null && rect.contains(click))
                {
//                    int ap = TLCActionsMenu.selectedCharacter.getActionPoints();
//                    TLCActionsMenu.selectedCharacter.setActionPoints(ap-1);
                    TLCCharacter c = TLCActionsMenu.getSelectedCharacter();
                    TLCCharacterMoveMessage msg = new TLCCharacterMoveMessage(c.getCharacterID(),
                            c.getMapLocation().row, c.getMapLocation().column, a.row, a.column);
                    TLCDataFacade.getInstance(TLCMain.DATA_ID).getGameClient().send(TLCMessageType.CHARACTER_MOVE, msg);

                    // Put the selected character in "path following" mode.
                    //TLCActionsMenu.getSelectedCharacter().walkTo(a);

                    // Close the move action interface.
                    menu.close();
                }
            }
        }
        
//        String text = TLCActionsMenu.selectedCharacter.getType().name() + " " 
//        + TLCActionsMenu.selectedCharacter.getName() + ": " 
//        + TLCActionsMenu.selectedCharacter.getMovementPoints() + " spaces";
//        menu.gameScreen.centerText(g, text, 100, TLCUI.Fonts.getDialogTitleFont());
    }
    

    @Override
    public void unload()
    {
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
    }


    @Override
    public void load()
    {
        // TODO Auto-generated method stub
        
    }
}
