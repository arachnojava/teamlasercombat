package tlc.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.MHScreen;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHFont;
import mhframework.media.MHImageFont;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCNames;
import tlc.net.TLCGameClient;

public class TLCRecruitScreen extends TLCScreenBase
{
    private static final int TROOPER_Y = 190;
    private static final int OFFICER_Y = 380;
    
    private MHFont headingFont;
    private Font textFont;
    private MHGUIButton btnMaleTrooper, btnFemaleTrooper;
    private MHGUIButton btnMaleOfficer, btnFemaleOfficer;
    private MHGUIButton btnCancel;
    

    public TLCRecruitScreen()
    {
        headingFont = new MHFont(MHImageFont.EngineFont.TAHOMA_BLUE);
        textFont = new Font("SansSerif", Font.BOLD, 16);
        
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
        c.setTeamID(TLCGameClient.getClientID());
        
        return c;
    }
    
    
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);
        int x = 30;
        
        String message = "These four athletes are available to be drafted.  Click to select.";
        g.setFont(textFont);
        centerText(g, message, 100, Color.WHITE, false, 0);
        //textFont.drawString(g, message, x, y);
        
        int y = TROOPER_Y;
        headingFont.drawString(g, "Troopers (Cost: " + TLCCharacterType.TROOPER.getCost() + " coins)", x, y);
        drawColumnHeadings(g, x, y + 20);
        
        y = OFFICER_Y;
        headingFont.drawString(g, "Officers (Cost: " + TLCCharacterType.OFFICER.getCost() + " coins)", x, y);
        drawColumnHeadings(g, x, y + 20);
        
        
        super.render(g);
        super.drawTitle("Recruit Member", g);
        super.drawStatusBar(TLCGameClient.getStatusMessage(), g);
    }
    
    private void drawColumnHeadings(Graphics2D g, int x, int y)
    {
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
        // TODO Auto-generated method stub

    }

}
