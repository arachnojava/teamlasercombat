package tlc.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGame;
import mhframework.MHRenderable;
import mhframework.media.MHFont;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;

public class TLCPlayerMarquee implements MHRenderable
{
    private double x = 0.0;
    private double pixelsPerNano = 0.000000025;
    private int y, height, width;
    private long lastUpdateTime;
    private String playerName;
    private Color teamColor;
    private String teamName;
    private MHFont font;
    TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
    public TLCPlayerMarquee()
    {
        font = TLCUI.Fonts.getHelpFont();
        width = MHDisplayModeChooser.getWidth()-1;
        height = font.getHeight();
        playerName = data.getPlayerName();
        
        if (data.isPlayer())
        {
            TLCTeam team = TLCDataFacade.getTeam(data.getClientID());
            teamColor = team.getColor().getColorValue();
            teamName = team.getTeamName();
        }
        else
        {
            teamColor = Color.BLACK;
            teamName = "Spectator";
        }
    }
    
    
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    
    @Override
    public void advance()
    {
        long elapsedTime = MHGame.getGameTimerValue() - lastUpdateTime;
        
        x -= pixelsPerNano * elapsedTime;
        
        if (x <= -width)
            x += width;
        
        lastUpdateTime = MHGame.getGameTimerValue();
    }


    @Override
    public void render(Graphics2D g)
    {
        int spacing = (int)(width / 2);

        g.setColor(Color.LIGHT_GRAY);
        font.drawString(g, playerName, (int)x, y+height);
        font.drawString(g, teamName, (int)x+spacing, y+height);
        
        font.drawString(g, playerName, (int)x+spacing*2, y+height);
        font.drawString(g, teamName, (int)x+spacing*3, y+height);
        
        g.setColor(teamColor);
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(1, y, width-2, height+4, height/2, height/2);
    }

}
