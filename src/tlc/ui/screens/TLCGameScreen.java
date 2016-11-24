package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHResourceManager;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCGameOptions;
import tlc.data.TLCToken;
import tlc.data.TLCTokenData;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterType;
import tlc.data.world.TLCGameWorld;
import tlc.data.world.TLCItemManager;
import tlc.net.TLCAttackMessage;
import tlc.net.TLCCombatInteractionMessage;
import tlc.net.TLCMessageType;
import tlc.net.client.TLCGameClient;
import tlc.net.server.TLCAttackNotification;
import tlc.net.server.ai.TLCAIManager;
import tlc.ui.TLCUI;
import tlc.ui.hud.TLCCharacterDisplay;
import tlc.ui.hud.TLCEventLogDisplay;
import tlc.ui.hud.TLCTeamStatusDisplay;
import tlc.ui.hud.TLCWhoseTurnDisplay;
import tlc.ui.hud.actions.TLCActionsMenu;
import tlc.ui.hud.actions.TLCAttackActionState;
import tlc.ui.screens.inventory.TLCInventoryScreen;

public class TLCGameScreen extends TLCScreenBase implements Runnable
{
    // Constants
    private static final int MENU_Y = MHDisplayModeChooser.getHeight() - 50;
    
    // Stuff for the loading thread
    private final int TOTAL_ELEMENTS = 9;  // number of screen elements to load
    private int numElements = 0; // number of elements loaded so far
    private double pctLoaded = 0.0;
    
    private boolean keyScrollUp = false;
    private boolean keyScrollDown = false;
    private boolean keyScrollRight = false;
    private boolean keyScrollLeft = false;

    
    private TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
    private TLCGameWorld gameWorld = TLCDataFacade.getGameWorld();
    private TLCTeamStatusDisplay teamStatus;
    private TLCCharacterDisplay characterInfo;
    private TLCEventLogDisplay eventLog;
    private TLCWhoseTurnDisplay whoseTurn;
    private ArrayList<MHGUIButton> buttonMenu;
    private TLCScreenBase inGameMenu;
    private TLCActionsMenu actionsMenu = null;
    
    private MHGUIButton btnMenu, btnTeamStatus, btnCharInfo, btnTokens;
    private MHGUIButton btnTest;

    public static boolean isAttacking = false;
    public static TLCAttackNotification attackNotification = null;

    private static boolean showSummary;

    private static TLCCombatInteractionMessage combatSummary;
    
    public TLCGameScreen()
    {
        inGameMenu = new TLCInGameMenuScreen(this);
    }

    
    public void advance()    
    {
        if (combatSummary != null && TLCDataFacade.isCombatResultsOn())
        {
            setFinished(true);
            setNextScreen(TLCCombatSummaryScreen.getInstance(combatSummary));
            combatSummary = null;
        }
        
        // If it's our turn, show the actions menu.
        if (isOurTurn())
            showActionsMenu(true);
        else
            showActionsMenu(false);
        
        if (attackNotification != null)
        {
            TLCInventoryScreen inv = TLCInventoryScreen.getInstance(this);
            TLCDataFacade.getInstance(TLCMain.DATA_ID);
            TLCCharacter target = TLCDataFacade.getCharacterList().get(attackNotification.defenderID);
            TLCCharacterType type = target.getType();
            String name = target.getName();
            // If Auto-Defense is on, just choose a token.
            if (TLCDataFacade.getInstance(TLCMain.DATA_ID).isAutoDefenseOn())
            {
                TLCTokenData tokenData = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory().selectDefenseToken(type);
                try
                {
                    TLCInventoryScreen.selectedToken = new TLCToken(tokenData);
                } 
                catch (Exception e){}
            }
            else
            {
                inv.initForDefend(type, name);
                setNextScreen(inv);
                setFinished(true);
            }
        }
        
        if (actionsMenu != null)
            actionsMenu.advance();
        
        int scrollSpeed = 16;
        if (keyScrollRight)
            gameWorld.scrollMap(scrollSpeed, 0);

        if (keyScrollLeft)
            gameWorld.scrollMap(-scrollSpeed, 0);

        if (keyScrollUp)
            gameWorld.scrollMap(0, -scrollSpeed/2);

        if (keyScrollDown)
            gameWorld.scrollMap(0, scrollSpeed/2);

        teamStatus.advance();
        characterInfo.advance();
        eventLog.advance();
        whoseTurn.advance();
        gameWorld.advance();
        TLCAIManager.advance();
        super.advance();
    }
    
    
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);
        
        gameWorld.render(g);
        
        teamStatus.render(g);
        characterInfo.render(g);
        eventLog.render(g);
        whoseTurn.render(g);
        
        if (actionsMenu != null)
            actionsMenu.render(g);
        
        super.render(g);
        drawStatusBar(data.getStatusMessage(), g);
    }
    

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnTeamStatus)
            teamStatus.setVisible(!teamStatus.isVisible());
        else if (e.getSource() == btnCharInfo)
            characterInfo.setVisible(!characterInfo.isVisible());
        else if (e.getSource() == btnTokens)
        {
            showInventoryScreen();
        }
        else if (e.getSource() == btnMenu)
        {
            showInGameMenu();
        }
        // FOR TESTING:
        else if (e.getSource() == btnTest)
        {
            if (actionsMenu == null)
                showActionsMenu(true);
            else
                showActionsMenu(false);
        }
    }

    
    private boolean isOurTurn()
    {
        return TLCDataFacade.getWhoseTurn() == TLCDataFacade.getInstance(TLCMain.DATA_ID).getClientID();
    }
    
    
    private void showActionsMenu(boolean show)
    {
        if (show)
        {
            if (actionsMenu == null)
                actionsMenu = new TLCActionsMenu(this);
        }
        else if (actionsMenu != null)
        {
            actionsMenu.unload();
            actionsMenu = null;
        }
    }

    
    private void showInGameMenu()
    {
        setNextScreen(inGameMenu);
        setFinished(true);
        setDisposable(false);
    }

    
    private void showInventoryScreen()
    {
        TLCInventoryScreen inv = TLCInventoryScreen.getInstance(this);
        inv.initForInventory(TLCCharacterType.CAPTAIN);
        setNextScreen(inv);
        setFinished(true);
        setDisposable(false);
    }
    
    @Override
    public void load()
    {
        setNextScreen(null);
        setFinished(false);
        
        // Position the buttons.
        int width = buttonMenu.size() * TLCUI.SMALL_BUTTON_WIDTH;
        int x = MHDisplayModeChooser.getCenterX() + (width/2);
        int y = MENU_Y;
        for (MHGUIButton btn : buttonMenu)
        {
            x -= TLCUI.SMALL_BUTTON_WIDTH;
            btn.setPosition(x, y);
        }
        
        // If we're returning from the inventory screen, do we need
        // to send an attack message to the server?
        if (isAttacking)
        {
            // When combat results are received, reset isAttacking.
            TLCAttackMessage attackMsg = new TLCAttackMessage(
                    TLCActionsMenu.getSelectedCharacter().getCharacterID(),
                    TLCInventoryScreen.selectedToken.getTokenData(),
                    TLCAttackActionState.selectedDirection);
            TLCDataFacade.getInstance(TLCMain.DATA_ID).send(TLCMessageType.ATTACK, attackMsg);
        }
        else if (attackNotification != null)
        {
            TLCToken token = TLCInventoryScreen.selectedToken;
            
            if (token != null)
                TLCDataFacade.getInstance(TLCMain.DATA_ID).send(TLCMessageType.DEFEND, token.getTokenData());
            else
                TLCDataFacade.getInstance(TLCMain.DATA_ID).send(TLCMessageType.DEFEND, null);
        }
    }


    @Override
    public void unload()
    {
    }


    public void startLoading()
    {
        pctLoaded = 0.0;
        numElements = 0;
        MHResourceManager.getMediaTracker().reset();
        Thread loadingThread = new Thread(this);
        loadingThread.start();
    }


    public double getPercentLoaded()
    {
        pctLoaded = (double)numElements / TOTAL_ELEMENTS;
        double localPct = pctLoaded * 0.5;
        double mediaPct = MHResourceManager.getMediaTracker().getPctLoaded() * 0.5; 
        return localPct + mediaPct;
    }


    /****************************************************************
     * Method called by the loading thread on the game loading screen.
     */
    @Override
    public void run()
    {
        // Create the game world.
        if (gameWorld == null)
            gameWorld = TLCDataFacade.getGameWorld();
        
        numElements++;
        pause();

        // Wait for the server to tell us which map to load.
        String mapFileName = null;
        while (mapFileName == null)
        {
            //System.out.println("Waiting for map file name...");
            mapFileName = TLCGameClient.getMapFileName();
        }
        
        gameWorld.loadMap(mapFileName);
        numElements++;

        // Let the AI players upgrade their teams.
        TLCAIManager.goShopping();
        numElements++;
        pause(); // Pause for network latency.
        
        // Draw some tokens to start off with.
        TLCDataFacade.getInstance(TLCMain.DATA_ID).drawInitialTokens();
        numElements++;
        pause(); // Pause for network latency.
        
        // Create event log
        eventLog = new TLCEventLogDisplay(10, 50);
        numElements++;
        
        // Create "whose turn" display
        whoseTurn = new TLCWhoseTurnDisplay();
        numElements++;

        // Place random bonus items and hazard on the board.
        TLCItemManager.getInstance().init();
        
        // Create button menu
        buttonMenu = new ArrayList<MHGUIButton>();
        
        // FOR TESTING:
//        btnTest = TLCUI.createSmallButton("TEST");
//        btnTest.addActionListener(this);
//        btnTest.setPosition(50, MENU_Y);
//        add(btnTest);
//        buttonMenu.add(btnTest);
        
        // Create buttons in the reverse order that we want to display them.
        btnMenu = TLCUI.createSmallButton("Menu");
        btnMenu.setPosition(50, MENU_Y);
        btnMenu.addActionListener(this);
        add(btnMenu);
        buttonMenu.add(btnMenu);

        btnTokens = TLCUI.createSmallButton("Tokens");
        btnTokens.setPosition(50, MENU_Y);
        btnTokens.addActionListener(this);
        add(btnTokens);
        buttonMenu.add(btnTokens);

        btnCharInfo = TLCUI.createSmallButton("Units");
        btnCharInfo.setPosition(50, MENU_Y);
        btnCharInfo.addActionListener(this);
        add(btnCharInfo);
        buttonMenu.add(btnCharInfo);
        
        btnTeamStatus = TLCUI.createSmallButton("Teams");
        btnTeamStatus.setPosition(50, MENU_Y);
        btnTeamStatus.addActionListener(this);
        add(btnTeamStatus);
        buttonMenu.add(btnTeamStatus);

        numElements++;
        pause();

        // Create character data display
        characterInfo = new TLCCharacterDisplay();
        MHGUIButton previousC = TLCUI.createPreviousButton();
        MHGUIButton nextC = TLCUI.createNextButton();
        MHGUIButton closeC = TLCUI.createCloseButton();
        characterInfo.addButtons(previousC, nextC, closeC);
        add(previousC);
        add(nextC);
        add(closeC);
        numElements++;
        pause();
        
        // Create team status display
        teamStatus = new TLCTeamStatusDisplay();
        MHGUIButton previous = TLCUI.createPreviousButton();
        MHGUIButton next = TLCUI.createNextButton();
        MHGUIButton close = TLCUI.createCloseButton();
        teamStatus.addButtons(previous, next, close);
        add(previous);
        add(next);
        add(close);
        numElements++;
        pause();
    }

    
    private void pause()
    {
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void mouseMoved(MouseEvent e)
    {
        super.mouseMoved(e);
        gameWorld.mouseMoved(e);
    }
    
    
    @Override
    public void keyPressed(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                this.keyScrollRight = true;
                break;
            case KeyEvent.VK_LEFT:
                this.keyScrollLeft = true;
                break;
            case KeyEvent.VK_UP:
                this.keyScrollUp = true;
                break;
            case KeyEvent.VK_DOWN:
                this.keyScrollDown = true;
                break;
        }
    }

    
    @Override
    public void keyReleased(final KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_RIGHT:
                this.keyScrollRight = false;
                break;
            case KeyEvent.VK_LEFT:
                this.keyScrollLeft = false;
                break;
            case KeyEvent.VK_UP:
                this.keyScrollUp = false;
                break;
            case KeyEvent.VK_DOWN:
                this.keyScrollDown = false;
                break;
            case KeyEvent.VK_ESCAPE:
                showInGameMenu();
                break;
            case KeyEvent.VK_F5:
                TLCDataFacade.DEBUG = !TLCDataFacade.DEBUG;
                break;
        }
    }


    public void closeActionsMenu()
    {
        this.showActionsMenu(false);
    }


    public static void showCombatSummary(TLCCombatInteractionMessage summary)
    {
        combatSummary = summary;
    }

    
    
}
