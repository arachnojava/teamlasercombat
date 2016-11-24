package tlc.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import mhframework.gui.MHCommand;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUICommandButton;
import mhframework.media.MHFont;
import mhframework.media.MHResourceManager;
import mhframework.media.MHSoundManager;
import tlc.TLCMain;
import tlc.data.TLCCombatTokenStack;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.TLCTokenType;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCUpgradeItem;
import tlc.net.client.TLCGameClient;
import tlc.ui.command.TLCCharacterStatsCmd;
import tlc.ui.command.TLCRecruitCmd;
import tlc.ui.screens.TLCScreenBase;

public class TLCUIFactory
{

    public MHGUIButton createLargeButton(final String caption)
    {
        return new LargeButton(caption);
    }


    public MHGUIButton createSmallButton(final String caption)
    {
        return new SmallButton(caption);
    }


    public TLCCustomComponent createCustomComponent(final String caption, final String value)
    {
        return new TLCCustomComponent(caption, value, 0.75);
    }


    public MHGUIButton createHelpButton(String text, String title, TLCScreenBase screen)
    {
        return new HelpButton(text, title, screen);
    }

    
    public MHGUIButton createBuyButton(TLCUpgradeItem upgrade, TLCCharacter character)
    {
        return new BuyButton(upgrade, character);
    }

    
    public MHGUIButton createSellButton(TLCUpgradeItem upgrade, TLCCharacter character)
    {
        return new SellButton(upgrade, character);
    }

    
    public MHGUIButton createCharacterStatsButton(TLCCharacter c, TLCScreenBase screen)
    {
        MHCommand cmd = new TLCCharacterStatsCmd(c, screen);
        return new CharacterStatsButton(c, cmd);
    }


    public MHGUIButton createRecruitButton(TLCCharacter c, TLCScreenBase tlcRecruitScreen)
    {
        TLCGameClient client = TLCDataFacade.getInstance(TLCMain.DATA_ID).getGameClient();
        int teamID = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeamID();
        TLCTeam team = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(teamID);
        MHCommand cmd = new TLCRecruitCmd(c, tlcRecruitScreen, TLCDataFacade.getInstance(TLCMain.DATA_ID));
        return new CharacterStatsButton(c, cmd);
    }


    public MHGUIButton createPreviousButton()
    {
        return new PreviousButton();
    }


    public MHGUIButton createNextButton()
    {
        return new NextButton();
    }


    public MHGUIButton createCloseButton()
    {
        return new CloseButton();
    }

    
    public MHGUIButton createCombatTokenButton(TLCCombatTokenStack token)
    {
        return new TLCCombatTokenButton(token);
    }


    public MHGUIButton createHealTokenButton(int quantity)
    {
        return new TLCHealTokenButton(quantity);
    }


    public MHGUIButton createGrenadeTokenButton(int quantity)
    {
        return new TLCGrenadeTokenButton(quantity);
    }


    public MHGUIButton createPowerTokenButton(TLCTokenType tokenType)
    {
        return new TLCPowerTokenButton(tokenType);
    }
}


class LargeButton extends MHGUIButton
{
    public LargeButton(final String caption)
    {
        super(TLCUI.Images.BUTTON_LARGE, TLCUI.Images.BUTTON_LARGE, TLCUI.Images.BUTTON_LARGE_OVER);
        setFont(TLCUI.Fonts.BUTTON_12);
        setForeColor(Color.WHITE);
        setText(caption);
        MHSoundManager soundManager = MHResourceManager.getSoundManager();
        setButtonDownSound(soundManager, TLCAudio.BUTTON_CLICK_SOUND);
        setButtonOverSound(soundManager, TLCAudio.BUTTON_OVER_SOUND);
    }

    @Override
    public void renderCaption(final Graphics2D g)
    {
        final Rectangle2D r = new Rectangle2D.Double(getX()+22, getY()+12, 140, 15);
        caption.centerOn(r, g);
        caption.render(g);
    }
}

class SmallButton extends MHGUIButton
{
    public SmallButton(final String caption)
    {
        super(TLCUI.Images.BUTTON_SMALL, TLCUI.Images.BUTTON_SMALL, TLCUI.Images.BUTTON_SMALL_OVER);
        setFont(TLCUI.Fonts.BUTTON_12);
        setForeColor(Color.WHITE);
        setText(caption);
        MHSoundManager soundManager = MHResourceManager.getSoundManager();
        setButtonDownSound(soundManager, TLCAudio.BUTTON_CLICK_SOUND);
        setButtonOverSound(soundManager, TLCAudio.BUTTON_OVER_SOUND);
    }
}

class PreviousButton extends MHGUIButton
{
    public PreviousButton()
    {
        super(TLCUI.Images.BUTTON_PREVIOUS_NORMAL, TLCUI.Images.BUTTON_PREVIOUS_DOWN, TLCUI.Images.BUTTON_PREVIOUS_OVER);
        MHSoundManager soundManager = MHResourceManager.getSoundManager();
        setButtonDownSound(soundManager, TLCAudio.BUTTON_CLICK_SOUND);
        setButtonOverSound(soundManager, TLCAudio.BUTTON_OVER_SOUND);
    }
}


class NextButton extends MHGUIButton
{
    public NextButton()
    {
        super(TLCUI.Images.BUTTON_NEXT_NORMAL, TLCUI.Images.BUTTON_NEXT_DOWN, TLCUI.Images.BUTTON_NEXT_OVER);
        MHSoundManager soundManager = MHResourceManager.getSoundManager();
        setButtonDownSound(soundManager, TLCAudio.BUTTON_CLICK_SOUND);
        setButtonOverSound(soundManager, TLCAudio.BUTTON_OVER_SOUND);
    }
}


class CloseButton extends MHGUIButton
{
    public CloseButton()
    {
        super(TLCUI.Images.BUTTON_CLOSE_NORMAL, TLCUI.Images.BUTTON_CLOSE_DOWN, TLCUI.Images.BUTTON_CLOSE_OVER);
        MHSoundManager soundManager = MHResourceManager.getSoundManager();
        setButtonDownSound(soundManager, TLCAudio.BUTTON_CLICK_SOUND);
        setButtonOverSound(soundManager, TLCAudio.BUTTON_OVER_SOUND);
    }
}


class HelpButton extends SmallButton implements ActionListener
{
    private TLCScreenBase screen;
    private String helpText, titleText;
    private static MHFont titleFont;
    
    public HelpButton(String text, String title, TLCScreenBase screen)
    {
        super("Help");
        this.screen = screen;
        helpText = text;
        titleText = title;
        addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent e)
    {
        screen.showDialog(screen, helpText, titleText, TLCUI.Fonts.getHelpFont(), TLCUI.Fonts.getDialogTitleFont());
    }
}


class BuyButton extends SmallButton implements ActionListener
{
    private TLCUpgradeItem upgrade;
    private TLCCharacter character;
    
    public BuyButton(TLCUpgradeItem upgrade, TLCCharacter character)
    {
        super("Buy");
        this.upgrade = upgrade;
        this.character = character;
        addActionListener(this);
    }
    
    
    public void actionPerformed(ActionEvent e)
    {
        upgrade.applyUpgrade(character);
        TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(character.getTeamID()).adjustCoins(-upgrade.cost());
    }
}


class SellButton extends SmallButton implements ActionListener
{
    private TLCUpgradeItem upgrade;
    private TLCCharacter character;
    
    public SellButton(TLCUpgradeItem upgrade, TLCCharacter character)
    {
        super("Sell");
        this.upgrade = upgrade;
        this.character = character;
        addActionListener(this);
    }
    
    
    public void actionPerformed(ActionEvent e)
    {
        upgrade.undoUpgrade(character);
        TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(character.getTeamID()).adjustCoins(upgrade.sellValue());
    }
}


class CharacterStatsButton extends MHGUICommandButton
{
    private TLCCharacter character;
    public CharacterStatsButton(TLCCharacter c, MHCommand cmd)
    {
        super(TLCUI.Images.STATS_BUTTON, TLCUI.Images.STATS_BUTTON_OVER, TLCUI.Images.STATS_BUTTON_OVER, cmd);
        character = c;
        
        MHFont font = TLCUI.Fonts.getDataFont().clone();
        font.setScale(0.5);
        setFont(font);
        
        MHSoundManager soundManager = MHResourceManager.getSoundManager();
        setButtonDownSound(soundManager, TLCAudio.BUTTON_CLICK_SOUND);
        setButtonOverSound(soundManager, TLCAudio.BUTTON_OVER_SOUND);
    }
    
    
    public void render(Graphics2D g)
    {
        super.render(g);
        drawStats(g);
    }
    
    
    private void drawStats(Graphics2D g)
    {
        int x = getX();
        int y = getY() + (getImage().getHeight(null) - 2);
        int wide = 150;
        int narrow = 80;
        DecimalFormat df = new DecimalFormat("##0%");
        TLCCharacter c = character;

        getFont().drawString(g, c.getName(), x, y);
        x += wide;
        getFont().drawString(g, c.getType().getTitle(), x, y);
        x += wide;
        getFont().drawString(g, c.getGender().getName(), x, y);
        x += narrow;
        getFont().drawString(g, " "+c.getMaxHealth(), x, y);
        x += narrow;
        getFont().drawString(g, df.format(c.getTrainingLevel()), x, y);
        x += narrow;
        getFont().drawString(g, df.format(c.getAttackValue()), x, y);
        x += narrow;
        getFont().drawString(g, df.format(c.getDefenseValue()), x, y);
        x += narrow;
        getFont().drawString(g, df.format(c.getMovementValue()), x, y);
    }
}


class TLCHealTokenButton extends MHGUIButton
{
    private int count;
    private MHFont font = TLCUI.Fonts.getHelpFont();

    public TLCHealTokenButton(int quantity)
    {
        super(TLCUI.Images.BUTTON_HEAL_TOKEN, TLCUI.Images.BUTTON_HEAL_TOKEN, TLCUI.Images.BUTTON_HEAL_TOKEN);
        count = quantity;
    }

 
    public void render(Graphics2D g)
    {
        super.render(g);

        g.setColor(Color.BLACK);
        font.drawString(g, "x"+count, getX()+44, getY()+64);
        font.drawString(g, "x"+count, getX()+46, getY()+66);
        g.setColor(Color.WHITE);
        font.drawString(g, "x"+count, getX()+45, getY()+65);
    }
}


class TLCGrenadeTokenButton extends MHGUIButton
{
    private int count;
    private MHFont font = TLCUI.Fonts.getHelpFont();

    public TLCGrenadeTokenButton(int quantity)
    {
        super(TLCUI.Images.BUTTON_GRENADE_TOKEN, TLCUI.Images.BUTTON_GRENADE_TOKEN, TLCUI.Images.BUTTON_GRENADE_TOKEN);
        count = quantity;
    }

 
    public void render(Graphics2D g)
    {
        super.render(g);

        g.setColor(Color.BLACK);
        font.drawString(g, "x"+count, getX()+44, getY()+64);
        font.drawString(g, "x"+count, getX()+46, getY()+66);
        g.setColor(Color.WHITE);
        font.drawString(g, "x"+count, getX()+45, getY()+65);
    }
}
