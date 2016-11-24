package tlc.ui.hud;

import java.awt.Color;
import java.awt.Graphics2D;
import mhframework.MHDisplayModeChooser;
import mhframework.media.MHFont;
import tlc.data.TLCDataFacade;
import tlc.ui.TLCUI;

public class TLCWhoseTurnDisplay extends TLCHUDElement
{
    private MHFont font = TLCUI.Fonts.getDialogTitleFont();
    private String text = "";
    
    public TLCWhoseTurnDisplay()
    {
        super();
        setHeight(font.getHeight());
        setY(-getHeight());
        setVisible(true);
        setState(STATE_COMING);
    }
    
    
    public void advance()
    {
        super.advance();
    
        text = "Up Now: " + TLCDataFacade.getTeam(TLCDataFacade.getWhoseTurn()).getTeamName();
        int x = MHDisplayModeChooser.getCenterX() - font.stringWidth(text)/2; 
        setX(x);
        setHiddenLocation((int)getX(), (int)getY());
        setVisibleLocation((int)getX(), getHeight()+10);
    }
    
    
    @Override
    public void render(Graphics2D g)
    {
        // TODO Replace this with a prettier graphical version.
        g.setColor(Color.BLACK);
        font.drawString(g, text, getX()+2, getY()+2);
        g.setColor(Color.WHITE);
        font.drawString(g, text, getX(), getY());
    }

}
