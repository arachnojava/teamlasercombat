package tlc.ui.hud;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGame;
import mhframework.MHRuntimeMetrics;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIProgressBar;
import mhframework.io.net.MHSerializableClientInfo;
import mhframework.media.MHFont;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;
import tlc.ui.TLCUI;

public class TLCTeamStatusDisplay extends TLCHUDElement implements ActionListener
{
    private Paint bgPaint;
    private ArrayList<Image> statusList;
    private long lastUpdateTime = MHGame.getGameTimerValue();
    private int currentTeamID = 0;
    private MHGUIButton btnNext, btnPrevious, btnClose;
    
    public TLCTeamStatusDisplay()
    {
        super();
        setWidth(150);
        setHeight(300);
        updateStatusList();
        setHiddenLocation(-getWidth(), (int)getY());
        setVisibleLocation(10, (int)getY());
        setX(getInvisibleX());
        setState(STATE_COMING);
    }

    
    @Override
    public void render(Graphics2D g)
    {
        int spacing = 2;
        int x = (int)getX();
        int y = (int)getY();
        synchronized(statusList)
        {
            try{
            for (Image img : statusList)
            {
                g.drawImage(img, x, y, null);
                y += img.getHeight(null) + spacing;
            }
            }
            catch (Exception e) {}
        }
    }

    
    public void advance()
    {
        super.advance();
        
        btnClose.setPosition((int)(getX() + getWidth() - btnClose.getWidth()), (int)getY());
        btnPrevious.setPosition((int)getX(), (int)(getY()+getHeight()));
        btnNext.setPosition(btnPrevious.getX()+TLCUI.SMALL_BUTTON_WIDTH, btnPrevious.getY());
        
        // Only update every few seconds.
        if (MHGame.getGameTimerValue() - lastUpdateTime > MHRuntimeMetrics.secToNano(5))
        {
            updateStatusList();
        }
    }
    
    
    public float getY()
    {
        if (statusList == null)
            updateStatusList();
        
        return MHDisplayModeChooser.getHeight() - 18 - 32 - ((statusList.size()+2) * 34);
    }
    
    
    public int getHeight()
    {
        return statusList.size() * 34 + 34;
    }

    
    private void updateStatusList()
    {
        //TLCDataFacade.getInstance(TLCMain.DATA_ID);
        // Get currently selected team and members.
        TLCTeam team = TLCDataFacade.getTeam(currentTeamID);
        if (team == null) return;
        TLCCharacterList members = TLCDataFacade.getCharacterList().getTeamMembers(currentTeamID);
    
        // Build character status list.
        statusList = new ArrayList<Image>();
        statusList.add(teamIDBox(team));
        for (TLCCharacter c : members)
        {
            statusList.add(characterStatus(c));
        }
        
        setHiddenLocation(-getWidth() - 5, (int)getY());
        setVisibleLocation(10, (int)getY());
        lastUpdateTime = MHGame.getGameTimerValue();
    }
    
    private Image teamIDBox(TLCTeam team)
    {
        int height = 64;
    
        BufferedImage img = new BufferedImage(getWidth(), height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();

        String playerName = "";
        String teamName = team.getTeamName();
        MHSerializableClientInfo client = TLCDataFacade.getInstance(TLCMain.DATA_ID).getClientList().get(team.getID());
        if (client != null)
            playerName = client.name;
        Color teamColor = team.getColor().getColorValue();
        String teamColorName = team.getColor().getName();
        
        g.setPaint(getPaint(img.getWidth(), img.getHeight()));
        g.fillRoundRect(0, 0, getWidth()-1, height-1, 10, 10);

        g.setColor(Color.WHITE);
        MHFont teamNameFont = TLCUI.Fonts.getHelpFont();
        teamNameFont.drawString(g, teamName, 4, teamNameFont.getHeight()+2);
        
        MHFont font = TLCUI.Fonts.BUTTON_12;
        font.drawString(g, "Coach " + playerName, 4, teamNameFont.getHeight()+7+font.getHeight());
        font.drawString(g, "Team Color: " + teamColorName, 4, teamNameFont.getHeight()+12+font.getHeight()*2);
        
        g.setPaint(teamColor);
        g.drawRoundRect(0, 0, getWidth()-1, height-1, 10, 10);
        
        return img;
    }


    private Paint getPaint(int w, int h)
    {
        if (bgPaint == null)
        {
            final Color color1 = new Color(0, 0, 0, 64);
            final Color color2 = new Color(0, 0, 0, 127);
            bgPaint = new GradientPaint((int)(w*0.1), 0, color1, (int)(w*0.9), h, color2);
        }
        
        return bgPaint;
    }


    private Image characterStatus(TLCCharacter c)
    {
        int height = 32;
        BufferedImage img = new BufferedImage(getWidth(), height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        Color teamColor = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeam(c.getTeamID()).getColor().getColorValue();
        
        g.setPaint(getPaint(img.getWidth(), img.getHeight()));
        g.fillRoundRect(0, 0, getWidth()-1, height-1, 10, 10);

        g.setColor(Color.WHITE);
        TLCUI.Fonts.BUTTON_12.drawString(g, c.getType().getTitle() + " " + c.getName(), 4, 14);
        
        MHGUIProgressBar healthBar = new MHGUIProgressBar();
        healthBar.setSize((int)(getWidth() * 0.9), 10);
        healthBar.setPosition((int)(getWidth() * 0.05), 18);
        healthBar.setPaint(new GradientPaint(0, 0, teamColor.brighter(), 0, healthBar.getHeight()-1, teamColor.darker(), true));
        healthBar.setMaxValue(c.getMaxHealth());
        healthBar.update(c.getHealth());
        //healthBar.setText(c.getHealth() + "/" + c.getMaxHealth());
        healthBar.render(g);
        
        return img;
    }
    
    
    public void addButtons(MHGUIButton previous, MHGUIButton next, MHGUIButton close)
    {
        btnPrevious = previous;
        btnPrevious.addActionListener(this);
        
        btnNext = next;
        btnNext.addActionListener(this);
        
        btnClose = close;
        btnClose.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnClose)
        {
            setVisible(false);
        }
        else if (e.getSource() == btnPrevious)
        {
            currentTeamID = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeamList().previous().getID();
        }
        else if (e.getSource() == btnNext)
        {
            currentTeamID = TLCDataFacade.getInstance(TLCMain.DATA_ID).getTeamList().next().getID();
        }
        
        updateStatusList();
    }
}
