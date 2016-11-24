package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIChatClient;
import mhframework.media.MHFont;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;
import tlc.ui.TLCPlayerMarquee;
import tlc.ui.TLCUI;

public class TLCTeamConfigScreen extends TLCScreenBase
{
    
    private MHGUIButton btnDone, btnHelp, btnRecruit;
    private MHGUIChatClient chatClient;
    private TLCPlayerMarquee marquee;
    //private long lastRefreshTime;
    private TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
    
    public TLCTeamConfigScreen()
    {
        int scrW = MHDisplayModeChooser.getWidth();
        int scrH = MHDisplayModeChooser.getHeight();
        
        btnDone = TLCUI.createLargeButton("Done");
        btnDone.setPosition(scrW - 200, scrH - 60);
        btnDone.addActionListener(this);

        btnHelp = TLCUI.createLargeButton("Help");
        btnHelp.setPosition(btnDone.getX(), btnDone.getY() - btnDone.getHeight() - 10);
        btnHelp.addActionListener(this);

        btnRecruit = TLCUI.createLargeButton("Recruit");
        btnRecruit.setPosition(20, 300);
        btnRecruit.addActionListener(this);
        
        marquee = new TLCPlayerMarquee();
        marquee.setY(TLCUI.Images.TEAM_TITLE_BANNER.getHeight(null) + 2);
}

    @Override
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);
        TLCTeam team = data.getTeam(data.getClientID());
        TLCCharacterList members = data.getCharacterList();
        final TLCCharacterList teamMembers = members.getTeamMembers(data.getClientID());
        
        int left = MHDisplayModeChooser.getCenterX() - 370;
        int y = 100;
        marquee.render(g);
        int x = left;
        y += 50;
        MHFont font = TLCUI.Fonts.getCustomLabelFont();
        
        int wide = 150;
        int narrow = 80;
        g.setColor(Color.WHITE);
        font.drawString(g, "NAME", x, y);
        x += wide;
        font.drawString(g, "RANK", x, y);
        x += wide;
        font.drawString(g, "SEX", x, y);
        x += narrow;
        font.drawString(g, "HP", x, y);
        x += narrow;
        font.drawString(g, "TL", x, y);
        x += narrow;
        font.drawString(g, "AT", x, y);
        x += narrow;
        font.drawString(g, "DF", x, y);
        x += narrow;
        font.drawString(g, "MV", x, y);

        if (teamMembers.size() <= 0) // This should never, ever happen here.
            centerText(g, "No members recruited.", y+100, Color.RED, false, 0);
        
        super.render(g);
        drawTitle(team.getTeamName(), g);//, team.getColor().getColorValue());

        // TODO:  Replace this with coin display.
        TLCUI.Fonts.getCustomValueFont().drawString(g, team.getCoins()+ "c", MHDisplayModeChooser.getCenterX() + 300, 70);

        drawStatusBar(data.getStatusMessage(), g);
    }
    
    
    @Override
    public void load()
    {
        setFinished(false);
        setNextScreen(null);

        createUI();
    }


    @Override
    public void unload()
    {
    }

    
    protected void drawTitle(String title, Graphics2D g)
    {
        // Draw the title background
        int x = MHDisplayModeChooser.getCenterX() - (TLCUI.Images.TEAM_TITLE_BANNER.getWidth(null)/2); 
        g.drawImage(TLCUI.Images.TEAM_TITLE_BANNER, x, 0, null);
        // Draw title text.
        TLCUI.Fonts.getScreenTitleFont().drawString(g, title.toUpperCase(), x+65, TITLE_Y);
    }
    
        
    private void createUI()
    {
        removeComponents();
        
        TLCTeam team = TLCDataFacade.getTeam(data.getClientID());
        TLCCharacterList members = TLCDataFacade.getCharacterList();
        TLCCharacterList teamMembers = members.getTeamMembers(team.getID());
        int gap = 5;
        int y = 170;
        for (final TLCCharacter c : teamMembers)
        {
            MHGUIButton b = TLCUI.createCharacterStatsButton(c, this);
            centerComponent(b);
            b.setY(y);
            add(b);
            
            y += b.getHeight() + gap;
        }
        
        if (teamMembers.size() < 10)
            btnRecruit.setY(y);
        else
            btnRecruit.setY(-99999);
        
        centerComponent(btnRecruit);
        add(btnRecruit);
        
        add(btnDone);
        add(btnHelp);

        if (!data.getPlayerMode().equals(TLCPlayerMode.SINGLE_PLAYER))
        {
            chatClient = data.createChatClient(MHDisplayModeChooser.getCenterX() - 200, MHDisplayModeChooser.getHeight() - 130, 400, 110);
            add(chatClient);
        }
    }

    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnDone)
        {
            // If single player mode, go to game screen.
//            if (data.getPlayerMode().equals(TLCPlayerMode.SINGLE_PLAYER))
//            {
//                data.sendSignalReadyMessage(true);
//                setNextScreen(new TLCGameLoadingScreen());
//            }
//            else
                setNextScreen(null);
            
            setFinished(true);
            return;
        }
        else if (e.getSource() == btnHelp)
        {
            String txt = "Click on a character to customize or retire him or her.  Click the Recruit button to hire new members onto your team.";
            showDialog(this, txt);
        }
        else if (e.getSource() == btnRecruit)
        {
            setNextScreen(new TLCRecruitScreen());
            setFinished(true);
        }
    }

    
    @Override
    public void advance()
    {
        super.advance();
        marquee.advance();
        // If time elapsed, refresh the UI.
        //if (MHGame.getGameTimerValue() - lastRefreshTime > 5000)
        //{
        //    createUI();
        //    lastRefreshTime = MHGame.getGameTimerValue();
        //}
        
    }
}
