package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIComponent;
import mhframework.gui.MHGUIInputDialogScreen;
import mhframework.media.MHFont;
import mhframework.media.MHResourceManager;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCArmorUpgrade;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCCombatTraining;
import tlc.data.characters.TLCEnduranceTraining;
import tlc.data.characters.TLCMovementUpgrade;
import tlc.data.characters.TLCUpgradeItem;
import tlc.data.characters.TLCWeaponUpgrade;
import tlc.ui.TLCCustomComponent;
import tlc.ui.TLCUI;

public class TLCCharacterScreen extends TLCScreenBase
{
    private static final String SCREEN_TITLE = "EQUIP & TRAIN";
    private TLCCharacter character;
    private MHGUIComponent nameComponent;
    private MHGUIInputDialogScreen scrChangeName;
    private MHGUIButton btnChangeName;
    private MHGUIButton btnCommit;
    private MHGUIButton btnUndo;
    private MHGUIButton btnRetire;
    private UpgradeRow[] upgradeRows;
    private static final int SPACING = 40;
    private Image upgradeHeadings;
    private int startingBudget;
    private TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
    public TLCCharacterScreen(TLCCharacter c)
    {
        removeComponents();
        character = c.clone();
        nameComponent = TLCUI.createCustomComponent("Name", character.getName());
        add(nameComponent);
     
        btnChangeName = TLCUI.createSmallButton("Change");
        btnChangeName.setPosition(580, 200);
        btnChangeName.addActionListener(this);
        add(btnChangeName);
        
        btnRetire = TLCUI.createLargeButton("Retire");
        btnRetire.addActionListener(this);
        add(btnRetire);
        
        btnUndo = TLCUI.createLargeButton("Undo Changes");
        btnUndo.addActionListener(this);
        add(btnUndo);
        
        btnCommit = TLCUI.createLargeButton("Commit Changes");
        btnCommit.addActionListener(this);
        add(btnCommit);
        
        upgradeHeadings = MHResourceManager.loadImage("images/UpgradeHeadings.png");
        
        startingBudget = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(c.getTeamID()).getCoins();
    }
    
    
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);

        int y = 180;
        
        g.drawImage(upgradeHeadings, MHDisplayModeChooser.getCenterX()-400, y, null);
        y += upgradeHeadings.getHeight(null);
        
        for (int row = 0; row < upgradeRows.length; row++)
        {
            upgradeRows[row].render(g, y + row * SPACING);
        }
        
        
        super.render(g);
        drawTitle(SCREEN_TITLE, g);
        
        // TODO:  Replace this with coin display.

        TLCTeam team = data.getTeam(data.getClientID());
        TLCUI.Fonts.getCustomValueFont().drawString(g, team.getCoins()+ "c", MHDisplayModeChooser.getCenterX()+300, 150);

        drawStatusBar(data.getStatusMessage(), g);
    }
    
    
    public void advance()
    {
        getPreviousScreen().advance();
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {

        if (e.getSource() == btnCommit)
        {
            data.sendCharacterUpdateMessage(character, true);
            setFinished(true);
            setNextScreen(null);
        }
//        else if (e.getSource() == btnUndo)
//        {
//            data.getTeam(character.getTeamID()).setCoins(startingBudget);
//            setFinished(true);
//            setNextScreen(null);
//        }
        else if (e.getSource() == btnRetire)
        {
            data.sendRetireMessage(character);
            TLCDataFacade.getCharacterList().remove(character);
            setFinished(true);
            setNextScreen(null);
        }
        else if (e.getSource() == btnChangeName)
        {
            // Open dialog screen for entering player name
            scrChangeName = new TLCInputScreen(nameComponent, "Name", character.getName());
            scrChangeName.setTitle("");
            scrChangeName.setMessage("");
            setNextScreen(scrChangeName);
            setFinished(true);
        }
    }


    @Override
    public void load()
    {
        if (scrChangeName != null)
        {
            final String name = scrChangeName.getInputText();
            ((TLCCustomComponent)nameComponent).setValue(name);
            character.setName(name);
            TLCDataFacade.getInstance(TLCMain.DATA_ID).sendCharacterUpdateMessage(character, false);
            setFinished(false);
            setNextScreen(null);
            scrChangeName = null;
        }
        
        setFinished(false);

        nameComponent.setX(MHDisplayModeChooser.getCenterX()-400);
        nameComponent.setY(TLCUI.Images.TEAM_TITLE_BANNER.getHeight(null) + 10);
        btnChangeName.setPosition(nameComponent.getX()+480, nameComponent.getY()+50);

        upgradeRows = new UpgradeRow[5];
        upgradeRows[0] = new UpgradeRow(new TLCWeaponUpgrade(), character, this);
        upgradeRows[1] = new UpgradeRow(new TLCArmorUpgrade(), character, this);
        upgradeRows[2] = new UpgradeRow(new TLCMovementUpgrade(), character, this);
        upgradeRows[3] = new UpgradeRow(new TLCCombatTraining(), character, this);
        upgradeRows[4] = new UpgradeRow(new TLCEnduranceTraining(), character, this);
        
        btnCommit.setX(MHDisplayModeChooser.getWidth() - btnCommit.getWidth() - 5);
        btnCommit.setY(MHDisplayModeChooser.getHeight() - statusBarHeight - btnCommit.getHeight()-5);

        btnUndo.setX(btnCommit.getX() - btnUndo.getWidth() + 15);
        btnUndo.setY(btnCommit.getY());
        
        // Captains cannot be retired.
        if (!character.getType().equals(TLCCharacterType.CAPTAIN))
        {
            btnRetire.setX(btnUndo.getX() - btnRetire.getWidth() + 15);
            btnRetire.setY(btnUndo.getY());
        }
        else
            btnRetire.setPosition(99999, 99999);
        
    }


    @Override
    public void unload()
    {
    }

    
    private class UpgradeRow
    {
        private TLCScreenBase screen;
        private TLCUpgradeItem upgrade;
        private TLCCharacter character;
        private MHGUIButton btnBuy, btnSell, btnHelp;
        
        public UpgradeRow(TLCUpgradeItem item, TLCCharacter character, TLCScreenBase screen)
        {
            this.screen = screen;
            upgrade = item;
            this.character = character;
        }

        
        public void render(Graphics2D g, int y)
        {
            int cx = MHDisplayModeChooser.getCenterX();
            int[] col = new int[] {cx-395, cx-165, cx+20, cx+60, cx+100, cx+175, cx+404};
            g.setColor(Color.GRAY);
            for (int i = 0; i < col.length; i++)
                g.drawLine(col[i]-5, y-10, col[i]-5, y+20);

            MHFont font = TLCUI.Fonts.getDataFont();
            font.drawString(g, upgrade.getName(), col[0], y + font.getHeight());
            font.drawString(g, upgrade.getDescription(), col[1], y + font.getHeight());
            font.drawString(g, upgrade.cost()+"c", col[2], y + font.getHeight());
            font.drawString(g, getCurrentValue(), col[4], y + font.getHeight());
            
            // Only show sell value for equipment, not training.
            if (!upgrade.getName().toUpperCase().contains("TRAINING"))
                font.drawString(g, upgrade.sellValue()+"c", col[3], y + font.getHeight());

            

            g.drawLine(MHDisplayModeChooser.getCenterX()-400, y+font.getHeight()+5, MHDisplayModeChooser.getCenterX()+400, y+font.getHeight()+5);
         
            // Set button positions.
            MHGUIButton h = getHelpButton();
            h.setPosition(col[5]+140, y-12);
            MHGUIButton s = getSellButton();
            s.setPosition(h.getX()-65, h.getY());
            MHGUIButton b = getBuyButton();
            b.setPosition(s.getX()-65, s.getY());
            
            
            // Check on button validity.
            int budget = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(character.getTeamID()).getCoins();
            if (upgrade.cost() > budget)
                btnBuy.setPosition(-5000, -5000);
            
            if (upgrade.getName().toUpperCase().contains("LASER"))
            {
                if (character.getAttackValue() < upgrade.getEffect())
                    btnSell.setPosition(-5000, -5000);
            }
            else if (upgrade.getName().toUpperCase().contains("ARMOR"))
            {
                if (character.getDefenseValue() < upgrade.getEffect())
                    btnSell.setPosition(-5000, -5000);
            } 
            else if (upgrade.getName().toUpperCase().contains("BOOTS"))
            {
                if (character.getMovementValue() < upgrade.getEffect() ||
                        (character.getType().equals(TLCCharacterType.TROOPER) && character.getMovementValue() - upgrade.getEffect() < 1.0))
                    btnSell.setPosition(-5000, -5000);
            }
            else if (upgrade.getName().toUpperCase().contains("COMBAT"))
            {
                btnSell.setPosition(-5000, -5000);
                if (character.getTrainingLevel() >= 1.0)
                    btnBuy.setPosition(-5000, -5000);
            } 
            else if (upgrade.getName().toUpperCase().contains("ENDURANCE"))
            {
                btnSell.setPosition(-5000, -5000);
                if (character.getMaxHealth() >= 20)
                    btnBuy.setPosition(-5000, -5000);
            } 

        }
        
        private String getCurrentValue()
        {
            DecimalFormat df = new DecimalFormat("##0%");

            if (upgrade.getName().toUpperCase().contains("LASER"))
                return df.format(character.getAttackValue());

            if (upgrade.getName().toUpperCase().contains("ARMOR"))
                return df.format(character.getDefenseValue());

            if (upgrade.getName().toUpperCase().contains("BOOTS"))
                return df.format(character.getMovementValue());

            if (upgrade.getName().toUpperCase().contains("COMBAT"))
                return df.format(character.getTrainingLevel());

            if (upgrade.getName().toUpperCase().contains("ENDURANCE"))
                return character.getMaxHealth() + " HP";

            return "0";
        }
        
        private MHGUIButton getHelpButton()
        {
            if (btnHelp == null)
            {
                String text = upgrade.getHelpText();
                String title = upgrade.getName(); 
                btnHelp = TLCUI.createHelpButton(text, title, screen);
                screen.add(btnHelp);
            }
            
            return btnHelp;
        }
        
        
        private MHGUIButton getBuyButton()
        {
            if (btnBuy == null)
            {
                btnBuy = TLCUI.createBuyButton(upgrade, character);
                screen.add(btnBuy);
            }
            
            return btnBuy;
        }



        public MHGUIButton getSellButton()
        {
            if (btnSell == null)
            {
                btnSell = TLCUI.createSellButton(upgrade, character);
                screen.add(btnSell);
            }
            
            return btnSell;
        }
    }
}
