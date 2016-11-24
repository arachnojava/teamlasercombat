package tlc.ui.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGame;
import mhframework.MHRenderable;
import mhframework.MHRuntimeMetrics;
import mhframework.MHScreen;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIComponent;
import mhframework.media.MHFont;
import mhframework.media.MHImageFont;
import mhframework.media.MHStarField;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.ui.TLCUI;

public class TLCMainMenuScreen extends TLCScreenBase
{
    private Background background;
    private CreditsDisplay credits;

    private final MHGUIButton btnExit, btnSinglePlayer, btnHostLAN, btnJoinLAN, btnSpectator, btnOptions;
    private final int buttonHeight = TLCUI.Images.BUTTON_LARGE.getHeight(null);
    private final int buttonWidth = TLCUI.Images.BUTTON_LARGE.getWidth(null);
    private final ArrayList<MHGUIButton> buttons = new ArrayList<MHGUIButton>();
    private final ArrayList<MHGUIButton> helpButtons = new ArrayList<MHGUIButton>();
    
    MHFont font;

    public TLCMainMenuScreen()
    {
        setDisposable(false);

        btnSinglePlayer = TLCUI.createLargeButton("Single Player");
        buttons.add(btnSinglePlayer);

        btnHostLAN = TLCUI.createLargeButton("Host LAN Game");
        buttons.add(btnHostLAN);

        btnJoinLAN = TLCUI.createLargeButton("Join LAN Game");
        buttons.add(btnJoinLAN);

        btnSpectator = TLCUI.createLargeButton("Spectator Mode");
        buttons.add(btnSpectator);

        btnOptions = TLCUI.createLargeButton("Options");
        buttons.add(btnOptions);

        btnExit = TLCUI.createLargeButton("Exit");
        buttons.add(btnExit);

        for (final MHGUIButton b : buttons)
        {
            b.addActionListener(this);
            add(b);
            MHGUIButton h = TLCUI.createHelpButton(getHelpText(b), b.getCaptionText(), this);
            helpButtons.add(h);
            add(h);
        }

    }

    @Override
    public void render(final Graphics2D g)
    {
        background.render(g);

        final int logoX = (int) (MHDisplayModeChooser.getCenterX() - TLCUI.Images.GAME_LOGO.getWidth(null)/2);
        final int logoY = 80;
        g.drawImage(TLCUI.Images.GAME_LOGO, logoX, logoY, null);

        credits.render(g);
        
        super.render(g);
        
        drawStatusBar("Version " + TLCDataFacade.VERSION_NUMBER + " -- " + TLCDataFacade.BUILD_DATE, g);
    }


    @Override
    public void load()
    {
        setCursor(TLCUI.Images.MOUSE_NORMAL);

        setFinished(false);

        if (background == null)
            background = new Background();
        
        if (credits == null)
            credits = new CreditsDisplay(100, MHDisplayModeChooser.getHeight() - 150);
        
        if (font == null)
            font = new MHFont(MHImageFont.EngineFont.TAHOMA_BLUE);
    }


    @Override
    public void unload()
    {
    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        final TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
        if (e.getSource() == btnSinglePlayer)
        {
            data.setPlayerMode(TLCPlayerMode.SINGLE_PLAYER);
            setNextScreen(new TLCLoginScreen());
            setFinished(true);
        }
        else if (e.getSource() == btnSpectator)
        {
            data.setPlayerMode(TLCPlayerMode.SPECTATOR);
            setNextScreen(new TLCLoginScreen());
            setFinished(true);
        }
        else if (e.getSource() == btnHostLAN)
        {
            data.setPlayerMode(TLCPlayerMode.HOST_LAN);
            setNextScreen(new TLCLoginScreen());
            setFinished(true);
        }
        else if (e.getSource() == btnJoinLAN)
        {
            data.setPlayerMode(TLCPlayerMode.JOIN_LAN);
            setNextScreen(new TLCLoginScreen());
            setFinished(true);
        }
        else if (e.getSource() == btnOptions)
        {
            setNextScreen(new TLCOptionsScreen());
            setFinished(true);
        }
        else if (e.getSource() == btnExit)
            MHGame.setProgramOver(true);
    }




    @Override
    public void advance()
    {
        final int buttonTop = MHDisplayModeChooser.getHeight() - 250;
        final int buttonSpacing = 8;
        final int buttonLeft = MHDisplayModeChooser.getWidth() - (int)(buttonWidth*1.5);

        for (int i = 0; i < buttons.size(); i++)
        {
            final MHGUIButton b = buttons.get(i);
            final MHGUIButton h = helpButtons.get(i);
            b.setPosition(buttonLeft-i*17, buttonTop + (buttonHeight+buttonSpacing)*i);
            h.setPosition(b.getX()+b.getWidth()-7, b.getY());

        }

        credits.advance();
        super.advance();
    }

    
    private String getHelpText(MHGUIComponent c)
    {
        
        String s = "";
        
        if (c == btnSinglePlayer)
        {
            s = "Single Player mode involves one human (you) playing against ";
            s += "computer controlled opponents.";
        }
        else if (c == btnHostLAN)
        {
            s = "Host LAN means that you are volunteering to set up a multiplayer ";
            s += "game to which other players can connect over a network by selecting ";
            s += "the Join LAN option.";
        }
        else if (c == btnJoinLAN)
        {
            s = "Join LAN mode allows you to connect to a game on the network to play ";
            s += "against other human players, along with computer controlled players ";
            s += "if you wish.";
        }
        else if (c == btnSpectator)
        {
            s = "Spectator mode allows you to connect to a multiplayer game already ";
            s += "in progress so that you can watch the action without playing a ";
            s += "team of your own.";
        }
        else if (c == btnOptions)
        {
            s = "The Options button lets you configure some game options, such as ";
            s += "sound and music.";
        }
        else if (c == btnExit)
        {
            s = "The Exit button quits the program and leaves Team Laser Combat.";
        }
        
        return s;
    }



    private class Background
    {
        private final MHStarField sky, layer2;
        private double x = 0;
        double x2 = 0;

        public Background()
        {
            sky = new MHStarField((int)MHDisplayModeChooser.getBounds().getWidth(), (int)MHDisplayModeChooser.getBounds().getHeight(), 400);
            layer2 = new MHStarField((int)MHDisplayModeChooser.getBounds().getWidth()/2, (int)MHDisplayModeChooser.getBounds().getHeight()/2, 30, false);
        }

        public void render(final Graphics g)
        {
            x -= 0.5;
            MHScreen.tileImage((Graphics2D) g, sky.getImage(), (int)x, 0);

            x2 -= 0.75;
            MHScreen.tileImage((Graphics2D) g, layer2.getImage(), (int)x2, 0);

        }
    }
    
    

    private class CreditsDisplay implements MHRenderable
    {
        private ArrayList<CreditsRecord> list = new ArrayList<CreditsRecord>();
        private long delay = MHRuntimeMetrics.secToNano(5);
        private long lastChange = MHGame.getGameTimerValue();
        private int currentRecord = 0;
        int x, y;//, width;
        MHFont titleFont, nameFont;
        
        
        public CreditsDisplay(int x, int y)
        {
            this.x = x;
            this.y = y;
            //this.width = width;
            
            list.add(new CreditsRecord("Lead Designer", "Michael", "Henson"));
            list.add(new CreditsRecord("MHFramework Game Engine by", "Michael", "Henson"));
            list.add(new CreditsRecord("Animators", "Shaun", "Hager"));
            list.add(new CreditsRecord("Animators", "Michael", "Henson"));
            list.add(new CreditsRecord("Level Designers", "Justin", "Begalka"));
            list.add(new CreditsRecord("Level Designers", "Michael", "Henson"));
            list.add(new CreditsRecord("3D Modelers", "Michael", "Henson"));
            list.add(new CreditsRecord("Software Engineer", "Michael", "Henson"));
            list.add(new CreditsRecord("Costume Designers", "Kristen", "Miguel"));
            list.add(new CreditsRecord("Costume Designers", "Michael", "Henson"));
            list.add(new CreditsRecord("Costume Designers", "Sarah", "Kemper"));
            list.add(new CreditsRecord("Music Composer", "Justin", "Begalka"));
            list.add(new CreditsRecord("Audio", "Michael", "Henson"));
            list.add(new CreditsRecord("Associate Game Designer", "Justin", "Begalka"));
            list.add(new CreditsRecord("UI Artist", "Kristen", "Miguel"));
        }

        @Override
        public void advance()
        {
            if (MHGame.getGameTimerValue() - lastChange > delay)
            {
                currentRecord = (currentRecord + 1) % list.size();
                this.lastChange = MHGame.getGameTimerValue(); 
            }
        }

        @Override
        public void render(Graphics2D g)
        {
            CreditsRecord c = list.get(currentRecord);
            g.setColor(Color.WHITE);
            getTitleFont().drawString(g, c.credit, x, y);
            getNameFont().drawString(g, c.firstName.toUpperCase(), x+20, y+getNameFont().getHeight()+5);
            getNameFont().drawString(g, c.lastName.toUpperCase(), x+80, y+5+getNameFont().getHeight()*2);
        }
         
        
        private MHFont getTitleFont()
        {
            if (titleFont == null)
                titleFont = new MHFont("SansSerif", Font.BOLD, 16);

            return titleFont;
        }

    
        private MHFont getNameFont()
        {
            if (nameFont == null)
            {
                nameFont = new MHFont(MHImageFont.EngineFont.ANDROID_NATION);
                nameFont.setScale(0.75);
            }

            return nameFont;
        }
}
    
    private class CreditsRecord
    {
        String credit, firstName, lastName;
        
        public CreditsRecord(String credit, String fName, String lName)
        {
            this.credit = credit;
            this.firstName = fName;
            this.lastName = lName;
        }
    }
}
