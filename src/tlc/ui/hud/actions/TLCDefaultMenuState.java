package tlc.ui.hud.actions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import mhframework.MHDisplayModeChooser;
import mhframework.MHRandom;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHResourceManager;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHTileMapDirection;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPowerTokenSet;
import tlc.data.TLCTokenType;
import tlc.data.characters.TLCCharacter;
import tlc.ui.TLCUI;

public class TLCDefaultMenuState implements TLCActionsMenuState
{
    private MHGUIButton btnAttack, btnMove, btnDraw, btnHeal, btnNext, btnPrevious;
    private LaserBorder[] borders = new LaserBorder[6];
    TLCActionsMenu menu;
    
    public TLCDefaultMenuState(TLCActionsMenu menu)
    {
        this.menu = menu;
        init();
    }

    public void init()
    {
        createButtons();
        menu.centerOnCharacter();
    }
    
    @Override
    public void advance()
    {
        // If character cannot do actions, disable buttons.
        if (TLCActionsMenu.getSelectedCharacter().getActionPoints() <= 0)
        {
            this.btnAttack.setVisible(false);
            this.btnDraw.setVisible(false);
            this.btnHeal.setVisible(false);
            this.btnMove.setVisible(false);
        }
        else
        {
            this.btnDraw.setVisible(true);

            if (canAttack())
                this.btnAttack.setVisible(true);
            else
                this.btnAttack.setVisible(false);
            
            if (canHeal())
                this.btnHeal.setVisible(true);
            else 
                this.btnHeal.setVisible(false);
            
            if (canMove())
                this.btnMove.setVisible(true);
            else
                this.btnMove.setVisible(false);
        }
    }


    private boolean canAttack()
    {
        int ct = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory().getCombatTokens(TLCActionsMenu.getSelectedCharacter().getType()).size();
        int gt = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory().getGrenadeTokenCount(TLCActionsMenu.getSelectedCharacter().getType());
        TLCPowerTokenSet pt = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory().getPowerTokens(TLCActionsMenu.getSelectedCharacter().getType());
        boolean hasToken = ct > 0 || gt > 0 
                || pt.hasToken(TLCTokenType.PT_VAMPIRE)
                || pt.hasToken(TLCTokenType.PT_DESPERATION)
                || pt.hasToken(TLCTokenType.PT_BLAZE_OF_GLORY)
                || pt.hasToken(TLCTokenType.PT_LUCKY_SHOT);

        return (hasToken && findAttackDirections() != null);
    }

    private TLCAttackDirections findAttackDirections()
    {
        TLCAttackDirections dirs = TLCDataFacade.getGameWorld().findAttackDirections(TLCActionsMenu.getSelectedCharacter());

        if (dirs.isEmpty())
            return null;

        TLCAttackActionState.setDirections(dirs);
        
        return dirs;
    }

    
    private boolean canMove()
    {
        MHMapCellAddress addr = TLCActionsMenu.getSelectedCharacter().getMapLocation();
        if (addr == null) return false;
        
        MHMapCellAddress nw = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.NORTHWEST);
        MHMapCellAddress ne = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.NORTHEAST);
        MHMapCellAddress sw = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.SOUTHWEST);
        MHMapCellAddress se = TLCDataFacade.getGameWorld().getMap().tileWalk(addr, MHTileMapDirection.SOUTHEAST);
        
        return (TLCDataFacade.getGameWorld().canWalkOn(nw.row, nw.column) ||
                TLCDataFacade.getGameWorld().canWalkOn(ne.row, ne.column) || 
                TLCDataFacade.getGameWorld().canWalkOn(sw.row, sw.column) ||
                TLCDataFacade.getGameWorld().canWalkOn(se.row, se.column));
    }

    
    private boolean canHeal()
    {
        TLCCharacter c = TLCActionsMenu.getSelectedCharacter();
        boolean isDamaged = c.getHealth() < c.getMaxHealth();

        int ht = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory().getHealTokenCount(c.getType());
        boolean hasToken = (ht > 0);
        
        return isDamaged && hasToken;
    }

    
    @Override
    public void render(Graphics2D g)
    {
        // Draw laser beam border.
        for (int i = 0; i < borders.length; i++)
            borders[i].render(g);
        
        // Display character name and action points remaining.
        g.setColor(Color.BLACK);
        menu.gameScreen.centerText(g, TLCActionsMenu.getSelectedCharacter().getType().getTitle() + " " + TLCActionsMenu.getSelectedCharacter().getName(), MHDisplayModeChooser.getCenterY()+51, TLCUI.Fonts.getHelpFont());
        menu.gameScreen.centerText(g, TLCActionsMenu.getSelectedCharacter().getActionPoints()+" Actions Remaining", MHDisplayModeChooser.getCenterY()+81, TLCUI.Fonts.getHelpFont());
        g.setColor(Color.WHITE);
        menu.gameScreen.centerText(g, TLCActionsMenu.getSelectedCharacter().getType().getTitle() + " " + TLCActionsMenu.getSelectedCharacter().getName(), MHDisplayModeChooser.getCenterY()+50, TLCUI.Fonts.getHelpFont());
        menu.gameScreen.centerText(g, TLCActionsMenu.getSelectedCharacter().getActionPoints()+" Actions Remaining", MHDisplayModeChooser.getCenterY()+80, TLCUI.Fonts.getHelpFont());
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnPrevious)
        {
            TLCActionsMenu.setSelectedCharacter(getPreviousCharacter());
        }
        else if (e.getSource() == btnNext)
        {
            TLCActionsMenu.setSelectedCharacter(getNextCharacter());
        }
        else if (e.getSource() == btnMove)
        {
            removeButtons();
            // Switch to move action UI.
            menu.state.unload();
            menu.state = new TLCMoveActionState(menu);
        }
        else if (e.getSource() == btnDraw)
        {
            TLCDataFacade.getInstance(TLCMain.DATA_ID).drawToken();
        }
        else if (e.getSource() == btnAttack)
        {
            removeButtons();
            // Switch to attack action UI.
            menu.state.unload();
            menu.state = TLCAttackActionState.getInstance(menu);
            menu.state.load();
        }
        else if (e.getSource() == btnHeal)
        {
            // TODO:  Implement Heal action.
        }
        menu.centerOnCharacter();
    }

    
    private TLCCharacter getNextCharacter()
    {
        TLCCharacter c;
        do
        {
            TLCActionsMenu.charID++;
            
            if (TLCActionsMenu.charID >= TLCActionsMenu.characterList.size())
                TLCActionsMenu.charID = 0;

            c = TLCActionsMenu.characterList.get(TLCActionsMenu.charID);
        }
        while (c.getHealth() < 1);
        
        return c;
    }

    
    private TLCCharacter getPreviousCharacter()
    {
        TLCCharacter c;
        do
        {
            TLCActionsMenu.charID--;
            
            if (TLCActionsMenu.charID < 0)
                TLCActionsMenu.charID = TLCActionsMenu.characterList.size()-1;

            c = TLCActionsMenu.characterList.get(TLCActionsMenu.charID);
        }
        while (c.getHealth() < 1);
        
        return c;
    }


    private void removeButtons()
    {
        menu.gameScreen.remove(btnMove);
        menu.gameScreen.remove(btnAttack);
        menu.gameScreen.remove(btnHeal);
        menu.gameScreen.remove(btnDraw);
        menu.gameScreen.remove(btnPrevious);
        menu.gameScreen.remove(btnNext);
    }
    
    private void createButtons()
    {
        Image imgMoveN = MHResourceManager.loadImage("images/btnMove.png");
        Image imgMoveO = MHResourceManager.loadImage("images/btnMoveOver.png");
        Image imgAttackN = MHResourceManager.loadImage("images/btnAttack.png");
        Image imgAttackO = MHResourceManager.loadImage("images/btnAttackOver.png");
        Image imgDrawN = MHResourceManager.loadImage("images/btnDraw.png");
        Image imgDrawO = MHResourceManager.loadImage("images/btnDrawOver.png");
        Image imgHealN = MHResourceManager.loadImage("images/btnHeal.png");
        Image imgHealO = MHResourceManager.loadImage("images/btnHealOver.png");
        Image imgNextN = MHResourceManager.loadImage("images/btnNext.png");
        Image imgNextO = MHResourceManager.loadImage("images/btnNextOver.png");
        Image imgPrevN = MHResourceManager.loadImage("images/btnPrevious.png");
        Image imgPrevO = MHResourceManager.loadImage("images/btnPreviousOver.png");
        
        btnAttack   = new MHGUIButton(imgAttackN, imgAttackN, imgAttackO);
        btnMove     = new MHGUIButton(imgMoveN,   imgMoveN,   imgMoveO);
        btnDraw     = new MHGUIButton(imgDrawN,   imgDrawN,   imgDrawO);
        btnHeal     = new MHGUIButton(imgHealN,   imgHealN,   imgHealO);
        btnNext     = new MHGUIButton(imgNextN,   imgNextN,   imgNextO);
        btnPrevious = new MHGUIButton(imgPrevN,   imgPrevN,   imgPrevO);
        
        ArrayList<MHGUIButton> buttons = new ArrayList<MHGUIButton>();
        buttons.add(btnAttack);
        buttons.add(btnMove);
        buttons.add(btnDraw);
        buttons.add(btnNext);
        buttons.add(btnPrevious);
        buttons.add(btnHeal);
        
        int centerX = MHDisplayModeChooser.getCenterX();
        int centerY = MHDisplayModeChooser.getCenterY();
        int[][] position = new int[][] 
        {
            {centerX - 115, centerY - 190},// top left
            {centerX + 45,  centerY - 190},// top right
            {centerX + 130, centerY - 35}, // middle right
            {centerX + 45,  centerY + 110},// bottom right
            {centerX - 115, centerY + 110},// bottom left
            {centerX - 200, centerY - 35}  // middle left
        };

        Color teamColor = TLCDataFacade.getTeam(TLCActionsMenu.getSelectedCharacter().getTeamID()).getColor().getColorValue();
        int i = 0;
        for (MHGUIButton b : buttons)
        {
            menu.gameScreen.add(b);
            b.addActionListener(this);
            b.setPosition(position[i][0], position[i][1]);
            if (i < position.length-1)
                borders[i] = new LaserBorder(teamColor, position[i][0], position[i][1], position[i+1][0], position[i+1][1]);
            else
                borders[i] = new LaserBorder(teamColor, position[i][0], position[i][1], position[0][0], position[0][1]);

            i++;
        }
    }

    private class LaserBorder
    {
        private int x1, y1, x2, y2;
        private int r, g, b;
        
        public LaserBorder(Color c, int x1, int y1, int x2, int y2)
        {
            r = c.getRed();
            g = c.getGreen();
            b = c.getBlue();
            
            this.x1 = x1 + 35;
            this.y1 = y1 + 35;
            this.x2 = x2 + 35;
            this.y2 = y2 + 35;
        }
        
        public void render(Graphics2D g2d)
        {
            int baseAlpha = MHRandom.random(0, 40);
            
            g2d.setStroke(new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(r, g, b, 64-baseAlpha));
            g2d.drawLine(x1, y1, x2, y2);

            g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(r, g, b, 100-baseAlpha));
            g2d.drawLine(x1, y1, x2, y2);

            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(255, 255, 255, 192-baseAlpha));
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    @Override
    public void unload()
    {
        menu.gameScreen.remove(this.btnAttack);
        menu.gameScreen.remove(this.btnDraw);
        menu.gameScreen.remove(this.btnHeal);
        menu.gameScreen.remove(this.btnMove);
        menu.gameScreen.remove(this.btnNext);
        menu.gameScreen.remove(this.btnPrevious);
    }

    @Override
    public void load()
    {
        // TODO Auto-generated method stub
        
    }
}
