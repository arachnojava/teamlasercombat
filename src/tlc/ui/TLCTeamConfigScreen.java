package tlc.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import mhframework.gui.MHGUIButton;
import mhframework.io.net.client.MHClientModule;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;
import tlc.net.TLCGameClient;

public class TLCTeamConfigScreen extends TLCScreenBase
{
    
    private MHGUIButton btnDone, btnHelp, btnRecruit;
    
    public TLCTeamConfigScreen()
    {
        btnDone = TLCUI.createLargeButton("Done");
        btnDone.setPosition(600, 540);
        btnDone.addActionListener(this);

        btnHelp = TLCUI.createLargeButton("Help");
        btnHelp.setPosition(600, btnDone.getY() - btnDone.getHeight() - 10);
        btnHelp.addActionListener(this);

        btnRecruit = TLCUI.createLargeButton("Recruit");
        btnRecruit.setPosition(20, 300);
        btnRecruit.addActionListener(this);
        
        createCharacterButtons();
}

    @Override
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);
        int id = MHClientModule.get().getClientID();
        TLCTeam team = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(TLCGameClient.getClientID());
        TLCCharacterList members = TLCDataFacade.getInstance(TLCMain.DATA_ID).getCharacterList();
        final TLCCharacterList teamMembers = members.getTeamMembers(TLCGameClient.getClientID());
        
        g.setFont(TLCUI.Fonts.TEXT);
        int left = 30;
        int y = 100;
        String playerName = TLCDataFacade.getInstance(TLCMain.DATA_ID).getUser(id).name;
        centerText(g, "Coach " + playerName +"           Coins: " + team.getCoins(), 100, Color.LIGHT_GRAY, false, 0);

        int x = left;
        y += 50;
        
        int wide = 150;
        int narrow = 80;
        g.setColor(Color.WHITE);
        g.drawString("NAME", x, y);
        x += wide;
        g.drawString("RANK", x, y);
        x += wide;
        g.drawString("GN", x, y);
        x += narrow;
        g.drawString("HP", x, y);
        x += narrow;
        g.drawString("TL", x, y);
        x += narrow;
        g.drawString("AT", x, y);
        x += narrow;
        g.drawString("DF", x, y);
        x += narrow;
        g.drawString("MV", x, y);

        if (teamMembers.size() <= 0) // This should never, ever happen here.
            centerText(g, "No members recruited.", y+100, Color.RED, false, 0);
        
        super.render(g);
        drawTitle(team.getTeamName(), g, team.getColor().getColorValue());
        drawStatusBar(TLCGameClient.getStatusMessage(), g);
    }
    
    
    @Override
    public void load()
    {
        setFinished(false);
        setNextScreen(null);

        createCharacterButtons();
        add(btnDone);
        add(btnHelp);
    }


    @Override
    public void unload()
    {
    }

    
    private void createCharacterButtons()
    {
        removeComponents();
        
        TLCTeam team = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(TLCGameClient.getClientID());
        TLCCharacterList members = TLCDataFacade.getInstance(TLCMain.DATA_ID).getCharacterList();
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
            btnRecruit.setY(99999);
        
        add(btnRecruit);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnDone)
        {
            setFinished(true);
            return;
        }
        else if (e.getSource() == btnHelp)
        {
            String txt = "Help text here will explain how to customize characters and recruit new ones.";
            showDialog(this, txt);
        }
        else if (e.getSource() == btnRecruit)
        {
            setNextScreen(new TLCRecruitScreen());
            setFinished(true);
        }
    }
}
