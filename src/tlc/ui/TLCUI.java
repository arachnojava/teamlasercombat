package tlc.ui;

import java.awt.Font;
import java.awt.Image;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHFont;
import mhframework.media.MHImageFont;
import mhframework.media.MHResourceManager;
import tlc.data.TLCCombatTokenStack;
import tlc.data.TLCTokenType;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCUpgradeItem;
import tlc.net.client.TLCGameClient;
import tlc.ui.screens.TLCScreenBase;

public class TLCUI
{
    public static final int SMALL_BUTTON_WIDTH = 65;
    
    // Fonts
    public abstract static class Fonts
    {
        public static final Font TEXT = new Font("Serif", Font.PLAIN, 24);
        public static final MHFont BUTTON_12 = new MHFont("Tahoma", Font.BOLD+Font.ITALIC, 12);

        private static MHFont dataFont, customLabelFont, customValueFont;
        private static MHFont helpFont, screenTitleFont, dlgTitleFont;
        
        public static MHFont getScreenTitleFont()
        {
            if (Fonts.screenTitleFont == null)
                Fonts.screenTitleFont = new MHFont(MHImageFont.EngineFont.ANDROID_NATION);
            
            return Fonts.screenTitleFont;
        }
        
        
        public static MHFont getDialogTitleFont()
        {
            if (Fonts.dlgTitleFont == null)
            {
                Fonts.dlgTitleFont = new MHFont("Tahoma", Font.BOLD+Font.ITALIC, 24);
                //Fonts.dlgTitleFont = new MHFont(MHImageFont.EngineFont.ANDROID_NATION);
                //Fonts.dlgTitleFont.setScale(0.6);
                //Fonts.dlgTitleFont.setAllCaps(true);
            }
            
            return Fonts.dlgTitleFont;
        }

        
        public static MHFont getDataFont()
        {
            if (Fonts.dataFont == null)
                Fonts.dataFont = new MHFont(MHImageFont.EngineFont.OCR_GREEN);
            
            return Fonts.dataFont;
        }
        
        
        public static MHFont getHelpFont()
        {
            if (Fonts.helpFont == null)
                Fonts.helpFont = new MHFont("Tahoma", Font.PLAIN, 20);
            
            return Fonts.helpFont;
        }


        public static MHFont getCustomLabelFont()
        {
            if (Fonts.customLabelFont == null)
            {
                Fonts.customLabelFont = new MHFont(MHImageFont.EngineFont.ANDROID_NATION);
                Fonts.customLabelFont.setScale(0.5);
            }
            
            return Fonts.customLabelFont;
        }


        public static MHFont getCustomValueFont()
        {
            if (Fonts.customValueFont == null)
                Fonts.customValueFont = new MHFont(MHImageFont.EngineFont.OCR_GREEN);
            
            return Fonts.customValueFont;
        }
    }


    // Images
    public abstract static class Images
    {
        public static final String IMAGE_DIR = "images/";

        public static final Image GAME_LOGO = loadImage("TLCLogo600.png");
        public static final Image MOUSE_NORMAL = loadImage("Mouse0.gif");
        public static final Image BUTTON_LARGE = loadImage("Button194x30.png");
        public static final Image BUTTON_LARGE_OVER = loadImage("Button194x30_Over.png");
        public static final Image BUTTON_SMALL = loadImage("Button81x31.png");
        public static final Image BUTTON_SMALL_OVER = loadImage("Button81x31_Over.png");
        public static final Image STATS_BUTTON = loadImage("CharacterConfigButton_Normal.png");
        public static final Image STATS_BUTTON_OVER = loadImage("CharacterConfigButton_Over.png");
        public static final Image BUTTON_PREVIOUS_NORMAL = loadImage("PreviousButton_Normal.png");
        public static final Image BUTTON_PREVIOUS_DOWN = loadImage("PreviousButton_Down.png");
        public static final Image BUTTON_PREVIOUS_OVER = loadImage("PreviousButton_Over.png");
        public static final Image BUTTON_NEXT_NORMAL = loadImage("NextButton_Normal.png");
        public static final Image BUTTON_NEXT_DOWN = loadImage("NextButton_Down.png");
        public static final Image BUTTON_NEXT_OVER = loadImage("NextButton_Over.png");
        public static final Image BUTTON_CLOSE_NORMAL = loadImage("CloseButton_Normal.png");
        public static final Image BUTTON_CLOSE_DOWN = loadImage("CloseButton_Down.png");
        public static final Image BUTTON_CLOSE_OVER = loadImage("CloseButton_Over.png");
        public static final Image COMPONENT_FRAME = loadImage("CustomComponent.png");
        public static final Image BACKGROUND_TEXTURE = loadImage("IsoFloor.jpg");
        public static final Image TEAM_TITLE_BANNER = loadImage("TeamConfigTitleBanner.gif");
        public static final Image BUTTON_COMBAT_TOKEN = loadImage("btnCombatToken.png");
        public static final Image BUTTON_POWER_TOKEN = loadImage("btnPowerToken.png");
        public static final Image BUTTON_HEAL_TOKEN = loadImage("btnHealToken.png");
        public static final Image BUTTON_GRENADE_TOKEN = loadImage("btnGrenadeToken.png");


        private static final Image loadImage(final String filename)
        {
            TLCGameClient.log("TLCImages.loadImage(" + filename + ")");
            return MHResourceManager.loadImage(IMAGE_DIR + filename);
        }
    }


    private static TLCUIFactory uiFactory;
    private static TLCUIFactory getUIFactory()
    {
        if (uiFactory == null)
            uiFactory = new TLCUIFactory();

        return uiFactory;
    }

       
    public static MHGUIButton createLargeButton(final String caption)
    {
        return getUIFactory().createLargeButton(caption);
    }

    
    public static TLCCustomComponent createCustomComponent(final String caption, final String value)
    {
        return getUIFactory().createCustomComponent(caption, value);
    }


    public static MHGUIButton createSmallButton(String caption)
    {
        return getUIFactory().createSmallButton(caption);
    }
    
    
    public static MHGUIButton createCharacterStatsButton(TLCCharacter c, TLCScreenBase screen)
    {
        return getUIFactory().createCharacterStatsButton(c, screen);
    }


    public static MHGUIButton createRecruitButton(TLCCharacter c, TLCScreenBase screen)
    {
        return getUIFactory().createRecruitButton(c, screen);
    }

    
    public static MHGUIButton createHelpButton(String text, String title, TLCScreenBase screen)
    {
        return getUIFactory().createHelpButton(text, title, screen);
    }
    
    
    public static MHGUIButton createBuyButton(TLCUpgradeItem item, TLCCharacter character)
    {
        return getUIFactory().createBuyButton(item, character);
    }

    
    public static MHGUIButton createSellButton(TLCUpgradeItem item, TLCCharacter character)
    {
        return getUIFactory().createSellButton(item, character);
    }


    public static MHGUIButton createPreviousButton()
    {
        return getUIFactory().createPreviousButton();
    }

    
    public static MHGUIButton createNextButton()
    {
        return getUIFactory().createNextButton();
    }

    public static MHGUIButton createCloseButton()
    {
        return getUIFactory().createCloseButton();
    }
    
    public static MHGUIButton createCombatTokenButton(TLCCombatTokenStack token)
    {
        return getUIFactory().createCombatTokenButton(token);
    }

    public static MHGUIButton createHealTokenButton(int quantity)
    {
        return getUIFactory().createHealTokenButton(quantity);
    }


    public static MHGUIButton createGrenadeTokenButton(int quantity)
    {
        return getUIFactory().createGrenadeTokenButton(quantity);
    }


    public static MHGUIButton createPowerTokenButton(TLCTokenType tokenType)
    {
        return getUIFactory().createPowerTokenButton(tokenType);
    }

}
