package tlc.ui.hud;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGame;
import mhframework.MHRuntimeMetrics;
import mhframework.gui.MHGUIButton;
import mhframework.tilemap.MHMapCellAddress;
import tlc.TLCMain;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCCharacter;
import tlc.ui.TLCUI;

public class TLCCharacterDisplay extends TLCHUDElement implements ActionListener
{
    private BufferedImage img;
    private TLCCharacterList characterList;
    private int charID = 0;
    private TLCCharacter selectedCharacter;
    private MHGUIButton btnPrevious;
    private MHGUIButton btnNext;
    private MHGUIButton btnClose;
    private long lastUpdateTime;
    private Paint bgPaint;
    
    public TLCCharacterDisplay()
    {
        super();
        setWidth(150);
        setHeight(150);
        setY(20);
        setHiddenLocation(MHDisplayModeChooser.getWidth(), (int)getY());
        setVisibleLocation(MHDisplayModeChooser.getWidth()-this.getWidth()-10, (int)getY());
        setX(getInvisibleX());
        setState(STATE_COMING);
        characterList = TLCDataFacade.getCharacterList().getTeamMembers(TLCDataFacade.getInstance(TLCMain.DATA_ID).getClientID());
        selectedCharacter = getNextCharacter();
    }

    
    private void update()
    {
        final int TOP = 18;
        int x = 4;
        int y = TOP;
        int spacing = TLCUI.Fonts.BUTTON_12.getHeight() + 6;
        int dataColumn = x + getWidth()/2;
        
        img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        
        if (selectedCharacter == null) return;

        Color teamColor = TLCDataFacade.getTeam(selectedCharacter.getTeamID()).getColor().getColorValue();
        
        g.setPaint(getPaint(getWidth(), getHeight()));
        g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);

        g.setColor(Color.WHITE);
        TLCUI.Fonts.getHelpFont().drawString(g, selectedCharacter.getType().getTitle() + " " + selectedCharacter.getName(), x, y);

        // Draw labels.
        g.setColor(Color.LIGHT_GRAY);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Gender:", x, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Health:", x, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Weapon:", x, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Armor:", x, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Movement:", x, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Training:", x, y);
        y += spacing;
        //TLCUI.Fonts.BUTTON_12.drawString(g, "Location:", x, y);
        
        // Draw data.
        g.setColor(Color.WHITE);
        y = TOP + spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "" +selectedCharacter.getGender().getName(), dataColumn, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "" +selectedCharacter.getHealth()+" / "+selectedCharacter.getMaxHealth(), dataColumn, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "" +(int)(selectedCharacter.getAttackValue()*100)+"%", dataColumn, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "" +(int)(selectedCharacter.getDefenseValue()*100)+"%", dataColumn, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "" +(int)(selectedCharacter.getDefenseValue()*100)+"%", dataColumn, y);
        y += spacing;
        TLCUI.Fonts.BUTTON_12.drawString(g, "" +(int)(selectedCharacter.getTrainingLevel()*100)+"%", dataColumn, y);
        y += spacing;
        //TLCUI.Fonts.BUTTON_12.drawString(g, "" +selectedCharacter.getMapLocation().toString(), dataColumn, y);
        
        g.setPaint(teamColor);
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
        
        lastUpdateTime = MHGame.getGameTimerValue();
    }

    
    public void advance()
    {
        super.advance();
        
        btnClose.setPosition((int)(getX() + getWidth() - btnClose.getWidth()), (int)getY());
        btnPrevious.setPosition((int)getX(), (int)(getY()+getHeight()));
        btnNext.setPosition(btnPrevious.getX()+TLCUI.SMALL_BUTTON_WIDTH, btnPrevious.getY());
        
        // Only update every few seconds.
        if (MHGame.getGameTimerValue() - lastUpdateTime > MHRuntimeMetrics.secToNano(5))
            update();
    }
    
    


    @Override
    public void render(Graphics2D g)
    {
        //int spacing = 2;
        int x = (int)getX();
        int y = (int)getY();

        g.drawImage(img, x, y, null);
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
            selectedCharacter = getPreviousCharacter();
        }
        else if (e.getSource() == btnNext)
        {
            selectedCharacter = getNextCharacter();
        }
        
        update();
    }

    
    private TLCCharacter getNextCharacter()
    {
        charID++;
        if (charID >= characterList.size())
            charID = 0;
        
        return characterList.get(charID);
    }

    
    private TLCCharacter getPreviousCharacter()
    {
        charID--;
        if (charID < 0)
            charID = characterList.size()-1;
        
        return characterList.get(charID);
    }

    
    private void centerOnCharacter()
    {
        if (selectedCharacter != null)
        {
            if (selectedCharacter.getMapLocation() == null)
            {
                // Character's map location is missing.  Fix it.
                MHMapCellAddress cell = TLCDataFacade.getGameWorld().findCharacter(selectedCharacter);
                if (cell != null)
                    selectedCharacter.setMapLocation(cell);
                else return;
            }

            int row = selectedCharacter.getMapLocation().row;
            int column = selectedCharacter.getMapLocation().column;
            TLCDataFacade.getGameWorld().centerOn(row, column);
        }
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




}
