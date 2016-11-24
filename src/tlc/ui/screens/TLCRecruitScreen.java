package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.MHScreen;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHFont;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCNames;
import tlc.ui.TLCUI;

public class TLCRecruitScreen extends TLCScreenBase
{
    private static final int TROOPER_Y = 190;
    private static final int OFFICER_Y = 380;
    
    private MHFont headingFont;
    private MHFont textFont;
    private MHGUIButton btnMaleTrooper, btnFemaleTrooper;
    private MHGUIButton btnMaleOfficer, btnFemaleOfficer;
    private MHGUIButton btnCancel;
    
    private TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);

    public TLCRecruitScreen()
    {
        headingFont = TLCUI.Fonts.getScreenTitleFont();
        //headingFont.setScale(0.6);
        textFont = TLCUI.Fonts.getHelpFont();
        
        TLCCharacter c = createCharacter(TLCCharacterType.TROOPER, TLCCharacterGender.MALE);
        c.setName(TLCNames.boyName());
        btnMaleTrooper = TLCUI.createRecruitButton(c, this);
        
        c = createCharacter(TLCCharacterType.TROOPER, TLCCharacterGender.FEMALE);
        c.setName(TLCNames.girlName());
        btnFemaleTrooper = TLCUI.createRecruitButton(c, this);
        
        c = createCharacter(TLCCharacterType.OFFICER, TLCCharacterGender.MALE);
        c.setName(TLCNames.boyName());
        btnMaleOfficer = TLCUI.createRecruitButton(c, this);
        
        c = createCharacter(TLCCharacterType.OFFICER, TLCCharacterGender.FEMALE);
        c.setName(TLCNames.girlName());
        btnFemaleOfficer = TLCUI.createRecruitButton(c, this);
        
        btnCancel = TLCUI.createLargeButton("Cancel");
        btnCancel.addActionListener(this);
        
        add(btnMaleTrooper);
        add(btnFemaleTrooper);
        add(btnMaleOfficer);
        add(btnFemaleOfficer);
        add(btnCancel);
    }

    
    private TLCCharacter createCharacter(TLCCharacterType type, TLCCharacterGender gender)
    {
        TLCCharacter c = new TLCCharacter(type, gender);
        c.setTeamID(data.getClientID());
        
        return c;
    }
    
    
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);
        int x = MHDisplayModeChooser.getCenterX() - 380;
        
        String message = "These four athletes are available to be drafted.  Click to select.";
        g.setColor(Color.WHITE);
        centerText(g, message, 100, textFont);
        
        int y = TROOPER_Y;
        headingFont.drawString(g, "TROOPERS: " + TLCCharacterType.TROOPER.getCost() + " COINS", x, y);
        drawColumnHeadings(g, x, y + 30);
        
        y = OFFICER_Y;
        headingFont.drawString(g, "OFFICERS: " + TLCCharacterType.OFFICER.getCost() + " COINS", x, y);
        drawColumnHeadings(g, x, y + 30);
        
        
        super.render(g);
        super.drawTitle("Recruit Member", g);
        
        // TODO:  Replace this with coin display.
        TLCTeam team = TLCDataFacade.getTeam(data.getClientID());
        TLCUI.Fonts.getCustomValueFont().drawString(g, team.getCoins()+ "c", MHDisplayModeChooser.getCenterX() + 300, 150);

        super.drawStatusBar(data.getStatusMessage(), g);
    }

    
    public void advance()
    {
        getPreviousScreen().advance();
    }
    

    private void drawColumnHeadings(Graphics2D g, int x, int y)
    {
        int wide = 150;
        int narrow = 80;
        g.setColor(Color.WHITE);
        MHFont font = TLCUI.Fonts.getCustomLabelFont();
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

    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnCancel)
        {
            setFinished(true);
            setNextScreen(null);
        }
    }


    @Override
    public void load()
    {
        setFinished(false);
        setNextScreen(null);
        int yOffset = 40;
        
        centerComponent(btnMaleTrooper);
        //btnMaleTrooper.setY(MHDisplayModeChooser.DISPLAY_Y + TROOPER_Y + yOffset);
        btnMaleTrooper.setY(TROOPER_Y + yOffset);

        centerComponent(btnFemaleTrooper);
        btnFemaleTrooper.setY(btnMaleTrooper.getY() + btnFemaleTrooper.getHeight() + 10);
        
        centerComponent(btnMaleOfficer);
        //btnMaleOfficer.setY(MHDisplayModeChooser.DISPLAY_Y + OFFICER_Y + yOffset);
        btnMaleOfficer.setY(OFFICER_Y + yOffset);

        centerComponent(btnFemaleOfficer);
        btnFemaleOfficer.setY(btnMaleOfficer.getY() + btnFemaleOfficer.getHeight() + 10);

        centerComponent(btnCancel);
        //btnCancel.setY(MHDisplayModeChooser.getHeight()+MHDisplayModeChooser.DISPLAY_Y -(MHScreen.statusBarHeight + btnCancel.getHeight()));
        btnCancel.setY(MHDisplayModeChooser.getHeight()-(MHScreen.statusBarHeight + btnCancel.getHeight()));
    }


    @Override
    public void unload()
    {
    }

}
