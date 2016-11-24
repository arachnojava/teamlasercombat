package tlc.ui.hud.actions;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHResourceManager;
import mhframework.tilemap.MHTileMapDirection;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCToken;
import tlc.data.TLCTokenData;
import tlc.data.TLCTokenInventory;
import tlc.data.TLCTokenType;
import tlc.ui.TLCUI;
import tlc.ui.screens.TLCGameScreen;
import tlc.ui.screens.inventory.TLCInventoryScreen;

public class TLCAttackActionState implements TLCActionsMenuState
{
    private static TLCAttackActionState INSTANCE;
    private static TLCAttackDirections directions;
    private static TLCActionsMenu menu;
    public static MHTileMapDirection selectedDirection;
    
    private MHGUIButton btnAttackN, btnAttackNE, btnAttackE, btnAttackSE,
                        btnAttackS, btnAttackSW, btnAttackW, btnAttackNW;
    
    private TLCAttackActionState()
    {
        createButtons();  
    }
    
    private void createButtons()
    {
        // Mouse-over images
        Image imgAttackN  = MHResourceManager.loadImage("images/btnAttackN.png");
        Image imgAttackNE = MHResourceManager.loadImage("images/btnAttackNE.png");
        Image imgAttackE  = MHResourceManager.loadImage("images/btnAttackE.png");
        Image imgAttackSE = MHResourceManager.loadImage("images/btnAttackSE.png");
        Image imgAttackS  = MHResourceManager.loadImage("images/btnAttackS.png");
        Image imgAttackSW = MHResourceManager.loadImage("images/btnAttackSW.png");
        Image imgAttackW  = MHResourceManager.loadImage("images/btnAttackW.png");
        Image imgAttackNW = MHResourceManager.loadImage("images/btnAttackNW.png");
        
        // Normal images
        Image imgAttackN_Normal  = MHResourceManager.loadImage("images/btnAttackN0.png");
        Image imgAttackNE_Normal = MHResourceManager.loadImage("images/btnAttackNE0.png");
        Image imgAttackE_Normal  = MHResourceManager.loadImage("images/btnAttackE0.png");
        Image imgAttackSE_Normal = MHResourceManager.loadImage("images/btnAttackSE0.png");
        Image imgAttackS_Normal  = MHResourceManager.loadImage("images/btnAttackS0.png");
        Image imgAttackSW_Normal = MHResourceManager.loadImage("images/btnAttackSW0.png");
        Image imgAttackW_Normal  = MHResourceManager.loadImage("images/btnAttackW0.png");
        Image imgAttackNW_Normal = MHResourceManager.loadImage("images/btnAttackNW0.png");
        
        btnAttackN  = new MHGUIButton(imgAttackN_Normal, imgAttackN, imgAttackN);
        btnAttackNE = new MHGUIButton(imgAttackNE_Normal, imgAttackNE, imgAttackNE);
        btnAttackE  = new MHGUIButton(imgAttackE_Normal, imgAttackE, imgAttackE);
        btnAttackSE = new MHGUIButton(imgAttackSE_Normal, imgAttackSE, imgAttackSE);
        btnAttackS  = new MHGUIButton(imgAttackS_Normal, imgAttackS, imgAttackS);
        btnAttackSW = new MHGUIButton(imgAttackSW_Normal, imgAttackSW, imgAttackSW);
        btnAttackW  = new MHGUIButton(imgAttackW_Normal, imgAttackW, imgAttackW);
        btnAttackNW = new MHGUIButton(imgAttackNW_Normal, imgAttackNW, imgAttackNW);

        btnAttackN.addActionListener(this);
        btnAttackNE.addActionListener(this);
        btnAttackE.addActionListener(this);
        btnAttackSE.addActionListener(this);
        btnAttackS.addActionListener(this);
        btnAttackSW.addActionListener(this);
        btnAttackW.addActionListener(this);
        btnAttackNW.addActionListener(this);
        
        btnAttackN.setPosition(MHDisplayModeChooser.getCenterX() - imgAttackN.getWidth(null)/2, MHDisplayModeChooser.getCenterY()-32-imgAttackN.getHeight(null));
        btnAttackS.setPosition(btnAttackN.getX(), MHDisplayModeChooser.getCenterY() + 32);
        btnAttackW.setPosition(MHDisplayModeChooser.getCenterX() - btnAttackN.getWidth()*2, MHDisplayModeChooser.getCenterY()-btnAttackN.getHeight()/2);
        btnAttackE.setPosition(MHDisplayModeChooser.getCenterX() + btnAttackN.getWidth(), MHDisplayModeChooser.getCenterY()-btnAttackN.getHeight()/2);
        
        btnAttackNE.setPosition((btnAttackN.getX()+btnAttackE.getX())/2, (btnAttackN.getY()+btnAttackE.getY())/2);
        btnAttackNW.setPosition((btnAttackN.getX()+btnAttackW.getX())/2, (btnAttackN.getY()+btnAttackW.getY())/2);
        btnAttackSE.setPosition((btnAttackS.getX()+btnAttackE.getX())/2, (btnAttackS.getY()+btnAttackE.getY())/2);
        btnAttackSW.setPosition((btnAttackS.getX()+btnAttackW.getX())/2, (btnAttackS.getY()+btnAttackW.getY())/2);

        menu.gameScreen.add(btnAttackN);
        menu.gameScreen.add(btnAttackNE);
        menu.gameScreen.add(btnAttackE);
        menu.gameScreen.add(btnAttackSE);
        menu.gameScreen.add(btnAttackS);
        menu.gameScreen.add(btnAttackSW);
        menu.gameScreen.add(btnAttackW);
        menu.gameScreen.add(btnAttackNW);
    }
    
    
    private void validate(MHGUIButton b, MHTileMapDirection d)
    {
        if (directions.getDirection(d) == null)
            b.setVisible(false);
        else
            b.setVisible(true);
    }
    
    
    @Override
    public void advance()
    {
        validate(btnAttackN,  MHTileMapDirection.NORTH);
        validate(btnAttackNE, MHTileMapDirection.NORTHEAST);
        validate(btnAttackE,  MHTileMapDirection.EAST);
        validate(btnAttackSE, MHTileMapDirection.SOUTHEAST);
        validate(btnAttackS,  MHTileMapDirection.SOUTH);
        validate(btnAttackSW, MHTileMapDirection.SOUTHWEST);
        validate(btnAttackW,  MHTileMapDirection.WEST);
        validate(btnAttackNW, MHTileMapDirection.NORTHWEST);
    }


    @Override
    public void render(Graphics2D g)
    {
        // TODO Render lasers here?
        
        if (directions.getDirection(MHTileMapDirection.NORTH) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.NORTH), btnAttackN.getX(), btnAttackN.getY(), TLCUI.Fonts.BUTTON_12);
        if (directions.getDirection(MHTileMapDirection.NORTHEAST) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.NORTHEAST), btnAttackNE.getX(), btnAttackNE.getY(), TLCUI.Fonts.BUTTON_12);
        if (directions.getDirection(MHTileMapDirection.EAST) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.EAST), btnAttackE.getX(), btnAttackE.getY(), TLCUI.Fonts.BUTTON_12);
        if (directions.getDirection(MHTileMapDirection.SOUTHEAST) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.SOUTHEAST), btnAttackSE.getX(), btnAttackSE.getY(), TLCUI.Fonts.BUTTON_12);
        if (directions.getDirection(MHTileMapDirection.SOUTH) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.SOUTH), btnAttackS.getX(), btnAttackS.getY(), TLCUI.Fonts.BUTTON_12);
        if (directions.getDirection(MHTileMapDirection.SOUTHWEST) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.SOUTHWEST), btnAttackSW.getX(), btnAttackSW.getY(), TLCUI.Fonts.BUTTON_12);
        if (directions.getDirection(MHTileMapDirection.WEST) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.WEST), btnAttackW.getX(), btnAttackW.getY(), TLCUI.Fonts.BUTTON_12);
        if (directions.getDirection(MHTileMapDirection.NORTHWEST) != null)
            menu.gameScreen.centerText(g, directions.getDirection(MHTileMapDirection.NORTHWEST), btnAttackNW.getX(), btnAttackNW.getY(), TLCUI.Fonts.BUTTON_12);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == this.btnAttackN)
            selectedDirection = MHTileMapDirection.NORTH;
        else if (e.getSource() == this.btnAttackNE)
            selectedDirection = MHTileMapDirection.NORTHEAST;
        else if (e.getSource() == this.btnAttackE)
            selectedDirection = MHTileMapDirection.EAST;
        else if (e.getSource() == this.btnAttackSE)
            selectedDirection = MHTileMapDirection.SOUTHEAST;
        else if (e.getSource() == this.btnAttackS)
            selectedDirection = MHTileMapDirection.SOUTH;
        else if (e.getSource() == this.btnAttackSW)
            selectedDirection = MHTileMapDirection.SOUTHWEST;
        else if (e.getSource() == this.btnAttackW)
            selectedDirection = MHTileMapDirection.WEST;
        else if (e.getSource() == this.btnAttackNW)
            selectedDirection = MHTileMapDirection.NORTHWEST;
        
        TLCGameScreen.isAttacking = true;

        // Time to draw a token.  If auto attack, the game will do it.
        if (TLCDataFacade.getInstance(TLCMain.DATA_ID).getGameOptions().isAutoAttackOn())
        {
            // Pick a token automatically.
            TLCTokenInventory inventory = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory();
            TLCTokenData token = inventory.selectAttackToken(TLCActionsMenu.getSelectedCharacter().getType());
            // Store the token so we can send it to the server.
            try
            {
                TLCInventoryScreen.selectedToken = new TLCToken(token);
            } 
            catch (Exception ex)
            {
                token.setTokenType(TLCTokenType.COMBAT_TOKEN);
                token.setAttackValue(1);
                token.setDefenseValue(1);
                try {TLCInventoryScreen.selectedToken = new TLCToken(token);}
                catch (Exception ex2){}
            }
        }
        else
        {
            // Show token inventory.
            TLCInventoryScreen inv = TLCInventoryScreen.getInstance(menu.gameScreen);
            inv.initForAttack(TLCActionsMenu.getSelectedCharacter().getType());
            menu.gameScreen.setNextScreen(inv);
            menu.gameScreen.setFinished(true);
        }
        unload();
        menu.close();
    }
    

    public void load()
    {
        menu.gameScreen.add(btnAttackN);
        menu.gameScreen.add(btnAttackNE);
        menu.gameScreen.add(btnAttackE);
        menu.gameScreen.add(btnAttackSE);
        menu.gameScreen.add(btnAttackS);
        menu.gameScreen.add(btnAttackSW);
        menu.gameScreen.add(btnAttackW);
        menu.gameScreen.add(btnAttackNW);
    }
    
    
    @Override
    public void unload()
    {
        menu.gameScreen.remove(btnAttackN);
        menu.gameScreen.remove(btnAttackNE);
        menu.gameScreen.remove(btnAttackE);
        menu.gameScreen.remove(btnAttackSE);
        menu.gameScreen.remove(btnAttackS);
        menu.gameScreen.remove(btnAttackSW);
        menu.gameScreen.remove(btnAttackW);
        menu.gameScreen.remove(btnAttackNW);
    }


    public static TLCAttackActionState getInstance(TLCActionsMenu menu)
    {
        TLCAttackActionState.menu = menu;
        if (INSTANCE == null)
            INSTANCE = new TLCAttackActionState();
        
        return INSTANCE;
    }

    
    public static void setDirections(TLCAttackDirections directions)
    {
        TLCAttackActionState.directions = directions;
    }
}
