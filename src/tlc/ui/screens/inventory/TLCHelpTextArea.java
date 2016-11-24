package tlc.ui.screens.inventory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import mhframework.MHRenderable;
import mhframework.gui.MHGUIComponent;
import mhframework.media.MHFont;
import tlc.ui.TLCUI;

public class TLCHelpTextArea implements MHRenderable, MouseListener
{
    private int x, y, width;
    private int textX, textY;
    private MHFont font;
    private String text;
    
    public TLCHelpTextArea(int x, int y, int width)
    {
        font = TLCUI.Fonts.getDataFont();
        font.setScale(1.0);
        this.x = x;
        this.y = y;
        this.width = width;
        textX = 0;
        textY = font.getHeight();
    }

    
    public void setText(String message)
    {
        text = message;
    }
    
    
    @Override
    public void advance()
    {

    }

    
    @Override
    public void render(Graphics2D g)
    {
        g.drawImage(getImage(), x, y, null);
    }
    
    
    private Image getImage()
    {
        font.setScale(1.0);
        Image img = new BufferedImage(width, font.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)img.getGraphics();
        
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, font.getHeight());

        g.setColor(Color.DARK_GRAY);
        for (int ly = 0; ly < font.getHeight(); ly+=4)
            g.drawLine(0, ly, width, ly);
            
        g.setColor(Color.LIGHT_GRAY);
        g.draw3DRect(0, 0, width, font.getHeight(), false);
        

        textX -= 8;
        
        int w = font.stringWidth(text);
        
        if (textX < -w)
            textX += w;
        
        font.drawString(g, text, textX, textY);
        font.drawString(g, text, textX+font.stringWidth(text), textY);

        return img;
    }


    @Override
    public void mouseClicked(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void mouseEntered(MouseEvent e)
    {
        MHGUIComponent btn = (MHGUIComponent)e.getSource();
        text = btn.getToolTip() + "     ";
        textX = width;
    }


    @Override
    public void mouseExited(MouseEvent e)
    {
        text = "";
    }


    @Override
    public void mousePressed(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void mouseReleased(MouseEvent e)
    {
        // TODO Auto-generated method stub
        
    }
    
    
}
