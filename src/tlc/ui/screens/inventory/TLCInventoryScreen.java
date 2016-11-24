package tlc.ui.screens.inventory;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import tlc.TLCMain;
import tlc.data.TLCCombatTokenStack;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPowerTokenSet;
import tlc.data.TLCToken;
import tlc.data.TLCTokenData;
import tlc.data.TLCTokenInventory;
import tlc.data.TLCTokenType;
import tlc.data.characters.TLCCharacterType;
import tlc.ui.TLCCombatTokenButton;
import tlc.ui.TLCPowerTokenButton;
import tlc.ui.TLCUI;
import tlc.ui.screens.TLCGameScreen;
import tlc.ui.screens.TLCScreenBase;

public class TLCInventoryScreen extends TLCScreenBase
{
    private enum Mode
    {
        VIEW_INVENTORY, ATTACK, DEFEND;
    }
    private static TLCInventoryScreen INSTANCE;
    private static final int DIALOG_WIDTH = 700;
    private static final int DIALOG_HEIGHT = 500;
    public static TLCToken selectedToken;
    Mode mode;
    
    private TLCScreenBase parent;
    private Color bgColor = new Color(0, 0, 0, 150);
    private int dialogX, dialogY;
    private boolean sortByAttack = true;
    private boolean stateChanged = true;
    private TLCCharacterType selectedType;
    private ArrayList<MHGUIButton> tokenButtons;
    private MHGUIButton btnHealToken, btnGrenadeToken;
    private MHGUIButton btnAT, btnDF, btnClose;
    private MHGUIButton btnCaptain, btnOfficer, btnTrooper;
    private TLCHelpTextArea helpTextArea;
    private String screenTitle;
    
    private TLCInventoryScreen(TLCScreenBase parent)
    {
        this.parent = parent;
        
        dialogX = MHDisplayModeChooser.getCenterX() - DIALOG_WIDTH/2;
        dialogY = MHDisplayModeChooser.getCenterY() - DIALOG_HEIGHT/2;
        
        helpTextArea = new TLCHelpTextArea(dialogX + 5, dialogY + 455, DIALOG_WIDTH - 10);
        
        selectedType = TLCCharacterType.CAPTAIN;
        sortByAttack = true;
        
        btnAT = TLCUI.createSmallButton("Attack");
        btnAT.setToolTip("Show tokens with offense values.");
        btnAT.addActionListener(this);
        btnAT.addMouseListener(helpTextArea);
        add(btnAT);
        
        btnDF = TLCUI.createSmallButton("Defense");
        btnDF.setToolTip("Show tokens with defense values.");
        btnDF.addActionListener(this);
        btnDF.addMouseListener(helpTextArea);
        add(btnDF);
        
        btnClose = TLCUI.createSmallButton("Close");
        btnClose.setToolTip("Close the inventory screen.");
        btnClose.addActionListener(this);
        btnClose.addMouseListener(helpTextArea);
        add(btnClose);
        
        btnCaptain = TLCUI.createLargeButton("Captain");
        btnCaptain.setToolTip("Show tokens for your captain.");
        btnCaptain.addActionListener(this);
        btnCaptain.addMouseListener(helpTextArea);
        add(btnCaptain);
        
        btnOfficer = TLCUI.createLargeButton("Officers");
        btnOfficer.setToolTip("Show tokens for your officers.");
        btnOfficer.addActionListener(this);
        btnOfficer.addMouseListener(helpTextArea);
        add(btnOfficer);

        btnTrooper = TLCUI.createLargeButton("Troopers");
        btnTrooper.setToolTip("Show tokens for your troopers.");
        btnTrooper.addActionListener(this);
        btnTrooper.addMouseListener(helpTextArea);
        add(btnTrooper);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnAT)
        {
            sortByAttack = true;
            stateChanged = true;
        }
        else if (e.getSource() == btnDF)
        {
            sortByAttack = false;
            stateChanged = true;
        }
        else if (e.getSource() == btnCaptain)
        {
            selectedType = TLCCharacterType.CAPTAIN;
            stateChanged = true;
        }
        else if (e.getSource() == btnOfficer)
        {
            selectedType = TLCCharacterType.OFFICER;
            stateChanged = true;
        }
        else if (e.getSource() == btnTrooper)
        {
            selectedType = TLCCharacterType.TROOPER;
            stateChanged = true;
        }
        else if (e.getSource() == btnClose)
        {
            setFinished(true);
        }
        else // Must have clicked on a token.
        {
            if (mode == Mode.VIEW_INVENTORY)
                return;
            
            // The case for Power Tokens.
            if (e.getSource() instanceof TLCPowerTokenButton)
            {
                TLCPowerTokenButton btn = (TLCPowerTokenButton) e.getSource();
                
                // Remove Power Token from inventory.
                TLCTokenInventory inventory = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory();
                inventory.getPowerTokens(selectedType).removeToken(btn.getTokenType());
                
                // Save the selected token for retrieval later.
                TLCTokenData pt = new TLCTokenData();
                pt.setTokenType(btn.getTokenType());
                try
                {
                    selectedToken = new TLCToken(pt);
                } 
                catch (Exception e1)
                {
                    e1.printStackTrace();
                    try {selectedToken = new TLCToken(pt);} 
                    catch (Exception e2){e2.printStackTrace();}
                }
                setFinished(true);
            }
            
            // The case for Combat Tokens:
            if (e.getSource() instanceof TLCCombatTokenButton)
            {
                // Find the stack for the token that was clicked.
                TLCCombatTokenButton btn = (TLCCombatTokenButton) e.getSource();
                TLCCombatTokenStack tokens = btn.getTokenStack();
                tokens.removeToken();

                // Save the selected token for retrieval later.
                selectedToken = tokens.getToken();
                setFinished(true);
            }

            // The case for Grenade Tokens:
            if (e.getSource().equals(btnGrenadeToken))
            {
                System.out.println(" >>> GRENADE TOKEN SELECTED <<<");
                
                TLCTokenInventory inventory = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory();
                inventory.useGrenadeToken();
                
                TLCTokenData data = new TLCTokenData();
                data.setTokenType(TLCTokenType.GRENADE_TOKEN);
                data.setAttackValue(2);
                TLCToken grenade = null;
                try
                {
                    grenade = new TLCToken(data);
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                }

                // Save the selected token for retrieval later.
                selectedToken = grenade;
                setFinished(true);
            }
        }
    }


    @Override
    public void load()
    {
        // If defending and there's no defense token, set 
        // selectedToken to null and close.
        if (TLCGameScreen.attackNotification != null)
        {
            TLCTokenInventory inventory = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory();
            TLCTokenData token = inventory.selectDefenseToken(selectedType);
            if (token == null)
            {
                selectedToken = null;
                setNextScreen(null);
                setFinished(true);
                return;
            }
            else 
            {
                try
                {
                    inventory.addToken(selectedType, new TLCToken(token));
                } catch (Exception e)
                {
                }
            }
        }
        
        setFinished(false);
        
        // Init token buttons
        initDisplay(TLCCharacterType.CAPTAIN, true);

        int buttonWidth = btnCaptain.getWidth()-15;
        int cw = (buttonWidth)*3;
        int cy = Math.max(TLCUI.Images.TEAM_TITLE_BANNER.getHeight(null), dialogY+5);
        btnCaptain.setPosition(MHDisplayModeChooser.getCenterX()-cw/2, cy);
        btnOfficer.setPosition(btnCaptain.getX()+buttonWidth, btnCaptain.getY());
        btnTrooper.setPosition(btnOfficer.getX()+buttonWidth, btnOfficer.getY());
        
        btnDF.setPosition(dialogX+DIALOG_WIDTH-btnDF.getWidth()-5, btnHealToken.getY()+btnHealToken.getHeight() + 10);
        btnAT.setPosition(btnDF.getX()-TLCUI.SMALL_BUTTON_WIDTH, btnDF.getY());
        
        cy = dialogY+DIALOG_HEIGHT-btnClose.getHeight()/2;
        btnClose.setPosition(dialogX+DIALOG_WIDTH-TLCUI.SMALL_BUTTON_WIDTH, cy);
        stateChanged = true;
    }
    
    
    public void initForAttack(TLCCharacterType type)
    {
        this.screenTitle = "Select Attack";
        mode = Mode.ATTACK;
        selectedType = type;
        sortByAttack = true;
        btnClose.setVisible(false);
        btnCaptain.setVisible(false);
        btnOfficer.setVisible(false);
        btnTrooper.setVisible(false);
        btnAT.setVisible(false);
        btnDF.setVisible(false);
    }

    
    public void initForDefend(TLCCharacterType type, String name)
    {
        this.screenTitle = "Defend " + name;
        mode = Mode.DEFEND;
        selectedType = type;
        sortByAttack = false;
        btnClose.setVisible(false);
        btnCaptain.setVisible(false);
        btnOfficer.setVisible(false);
        btnTrooper.setVisible(false);
        btnAT.setVisible(false);
        btnDF.setVisible(false);
    }

    
    public void initForInventory(TLCCharacterType type)
    {
        this.screenTitle = "Inventory";
        mode = Mode.VIEW_INVENTORY;
        selectedType = type;
        initDisplay(type, true);
        btnClose.setVisible(true);
        btnCaptain.setVisible(true);
        btnOfficer.setVisible(true);
        btnTrooper.setVisible(true);
        btnAT.setVisible(true);
        btnDF.setVisible(true);
    }

    
    private void initDisplay(TLCCharacterType type, boolean sortByAttack)
    {        
        stateChanged = false;
        
        removeOldButtons();
        TLCTokenInventory inventory = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTokenInventory();
        ArrayList<TLCCombatTokenStack> tokens = inventory.getCombatTokens(type);
        
        int ht = inventory.getHealTokenCount(type);
        btnHealToken = TLCUI.createHealTokenButton(ht);
        btnHealToken.setPosition(dialogX + 600, dialogY+60);
        btnHealToken.setToolTip("Heal Token. Choose the Heal action on your turn to restore one hit point to your character.");
        btnHealToken.addMouseListener(helpTextArea);
        add(btnHealToken);

        if (tokens == null)
            return;
        
        if (sortByAttack)
            sortByAT(tokens);
        else
            sortByDF(tokens);
        
        tokenButtons = new ArrayList<MHGUIButton>();
        
        // Add power tokens.
        TLCPowerTokenSet pt = inventory.getPowerTokens(selectedType);
        for (int p = 0; p < pt.getCount(); p++)
        {
            if (pt.hasToken(p))
            {
                if (pt.isAttackToken(p) && sortByAttack || pt.isDefenseToken(p) && !sortByAttack)
                {
                    MHGUIButton b = TLCUI.createPowerTokenButton(TLCTokenType.values()[p]);
                    b.setToolTip(TLCTokenType.values()[p].getDescription());
                    b.addActionListener(this);
                    b.addMouseListener(helpTextArea);
                    add(b);
                    tokenButtons.add(b);
                }
            }
        }
  
        for (int i = 0; i < tokens.size(); i++)
        {
            TLCCombatTokenStack stack = tokens.get(i);
            if (stack.getCount() < 1)
                continue;
            MHGUIButton b = TLCUI.createCombatTokenButton(stack);
            b.setToolTip("Combat Token. Use when attacking or defending. AT:" + tokens.get(i).getAttackValue()+" DF:"+tokens.get(i).getDefenseValue());
            b.addActionListener(this);
            b.addMouseListener(helpTextArea);
            add(b);
            tokenButtons.add(b);
        }

        if (sortByAttack && 
                selectedType.equals(TLCCharacterType.TROOPER) &&
                inventory.getGrenadeTokenCount(TLCCharacterType.TROOPER) > 0)
            {
                int gt = inventory.getGrenadeTokenCount(TLCCharacterType.TROOPER);
                btnGrenadeToken = TLCUI.createGrenadeTokenButton(gt);
                btnGrenadeToken.setToolTip("Grenade Token. Use when attacking. Does radial damage and cannot be blocked.");
                btnGrenadeToken.addMouseListener(helpTextArea);
                btnGrenadeToken.addActionListener(this);
                add(btnGrenadeToken);
                tokenButtons.add(btnGrenadeToken);
            }
    }
    
    
    private void removeOldButtons()
    {
        if (btnHealToken != null)
        {
            remove(btnHealToken);
            btnHealToken = null;
        }
        
        if (tokenButtons != null)
        {
            for (int i=0; i < tokenButtons.size(); i++)
                remove(tokenButtons.get(i));

            tokenButtons = null;
        }
    }
    
    
    private void sortByAT(ArrayList<TLCCombatTokenStack> tokens)
    {
        Collections.sort(tokens, new AttackComparator());
    }

    
    private void sortByDF(ArrayList<TLCCombatTokenStack> tokens)
    {
        Collections.sort(tokens, new DefenseComparator());
    }
    



    @Override
    public void unload()
    {

    }
    
    @Override
    public void advance()
    {
        super.advance();
        parent.advance();
        
        if (stateChanged)
        {
            initDisplay(selectedType, sortByAttack);

            int px = dialogX + 10;
            int py = dialogY + 180;
            
            for (int i = 0; i < tokenButtons.size(); i++)
            {
                tokenButtons.get(i).setPosition(px, py);
                px += tokenButtons.get(i).getWidth() + 10;
                if (px + tokenButtons.get(i).getWidth() > dialogX + DIALOG_WIDTH)
                {
                    px = dialogX + 10;
                    py += tokenButtons.get(i).getHeight() + 5;
                }
            }        
        }
    }

    


    @Override
    public void render(Graphics2D g)
    {
        parent.render(g);
        //super.drawStatusBar(TLCDataFacade.getInstance(TLCMain.DATA_ID).getStatusMessage(), g);
        
        super.fill(g, bgColor);
        
        // Dialog box background
        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(dialogX, dialogY, DIALOG_WIDTH, DIALOG_HEIGHT, true);
        
        // Character type
        g.setColor(Color.BLACK);
        g.fillRect(dialogX+5, btnHealToken.getY(), DIALOG_WIDTH-10, btnHealToken.getHeight()+5);
        g.setColor(Color.LIGHT_GRAY);
        g.draw3DRect(dialogX+5, btnHealToken.getY(), DIALOG_WIDTH-10, btnHealToken.getHeight()+5, false);
        TLCUI.Fonts.getScreenTitleFont().drawString(g, selectedType.name(), dialogX+10, dialogY+115);
        
        // Combat tokens label
        String tt = (this.sortByAttack ? "Offensive Tokens" : "Defensive Tokens");
        g.setColor(Color.BLACK);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, tt, dialogX+6, dialogY+165);
        g.setColor(Color.WHITE);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, tt, dialogX+5, dialogY+164);
        
        // Token area
        g.setColor(Color.BLACK);
        g.fillRect(dialogX+5, dialogY+170, DIALOG_WIDTH-10, (btnHealToken.getHeight()+5)*4);
        g.setColor(Color.LIGHT_GRAY);
        g.draw3DRect(dialogX+5, dialogY+170, DIALOG_WIDTH-10, (btnHealToken.getHeight()+5)*4, false);
        helpTextArea.render(g);
        
        super.render(g);
        super.drawTitle(screenTitle, g);
    }
    
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_ESCAPE:
                setFinished(true);
                break;
        }
    }

    
    
    public static TLCInventoryScreen getInstance(TLCScreenBase parent)
    {
        if (INSTANCE == null)
            INSTANCE = new TLCInventoryScreen(parent);
        
        return INSTANCE;
    }
}

class AttackComparator implements Comparator<TLCCombatTokenStack>
{

    @Override
    public int compare(TLCCombatTokenStack a, TLCCombatTokenStack b)
    {
        if (a.getAttackValue() < b.getAttackValue())
            return 1;
        else if (a.getAttackValue() > b.getAttackValue())
            return -1;
        else
        {
            if (a.getDefenseValue() > b.getDefenseValue())
                return 1;
            else
                return -1;
        }
    }
}


class DefenseComparator implements Comparator<TLCCombatTokenStack>
{

    @Override
    public int compare(TLCCombatTokenStack a, TLCCombatTokenStack b)
    {
        if (a.getDefenseValue() < b.getDefenseValue())
            return 1;
        else if (a.getDefenseValue() > b.getDefenseValue())
            return -1;
        else
        {
            if (a.getAttackValue() > b.getAttackValue())
                return 1;
            else
                return -1;
        }
    }
}
