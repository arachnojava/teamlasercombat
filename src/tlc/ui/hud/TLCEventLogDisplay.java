package tlc.ui.hud;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import mhframework.MHRenderable;
import mhframework.event.MHMouseMotionListener;
import mhframework.media.MHFont;
import tlc.ui.TLCUI;

public class TLCEventLogDisplay implements MHRenderable//extends TLCHUDElement
{
    public static final int WIDTH  = 280;
    public static final int HEIGHT = 100;
    private static ArrayList<TLCEventLogMessage> messages;
    private MHFont font = TLCUI.Fonts.BUTTON_12;
    private Rectangle2D bounds;
    private int x, y;
    private GradientPaint bgPaint;
    
    public TLCEventLogDisplay(int positionX, int positionY)
    {
        x = positionX;
        y = positionY;
    }

    
    @Override
    public void advance()
    {
        //super.advance();
        
        while (getMessageList().size() * font.getHeight() >= HEIGHT)
            messages.remove(0);
    }


    @Override
    public void render(Graphics2D g)
    {
        g.drawImage(getImage(), x, y, null);
    }

    
    private Image getImage()
    {
        Image image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)image.getGraphics();
        
        if (getBounds().contains(MHMouseMotionListener.getMousePoint()))
        {
            g.setPaint(getPaint(WIDTH, HEIGHT));
            g.fillRoundRect(0, 0, WIDTH-1, HEIGHT-1, 10, 10);
        }
        
        int mx = 5;
        int my = font.getHeight()+5;
        for (int i = 0; i < getMessageList().size(); i++)
        {
            TLCEventLogMessage msg = messages.get(i);
            g.setColor(msg.color);
            font.drawString(g, msg.text, mx, my);
            my += font.getHeight();
        }
        
        return image;
    }
    
    
    private static ArrayList<TLCEventLogMessage> getMessageList()
    {
        if (messages == null)
            messages = new ArrayList<TLCEventLogMessage>();
        
        return  messages;
    }
    
    public static void addMessage(Color color, String msg)
    {
        getMessageList().add(new TLCEventLogMessage(color, msg));
    }
    
    
    private Rectangle2D getBounds()
    {
        if (bounds == null)
            bounds = new Rectangle2D.Double(x, y, WIDTH, HEIGHT);
        
        return bounds;
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
    
    
    private static class TLCEventLogMessage
    {
        public Color color;
        public String text;
        
        public TLCEventLogMessage(Color c, String s)
        {
            color = c;
            text = s;
        }
    }
}
