package tlc.ui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import mhframework.gui.MHGUIComponent;

public class TLCCustomComponent extends MHGUIComponent
{
    private static final short CAPTION_X = 80;
    private static final short CAPTION_Y = 34;
    public static final short VALUE_X = 60;
    public static final short VALUE_Y = 85;

    private String caption = "";
    private String value = "";

    public TLCCustomComponent(final String caption, final String value, final double scale)
    {
        setCaption(caption);
        setValue(value);
        setWidth((int)(TLCUI.Images.COMPONENT_FRAME.getWidth(null) * scale));
        setHeight((int)(TLCUI.Images.COMPONENT_FRAME.getHeight(null) * scale));
    }

    
    public void render(final Graphics2D g)
    {
        g.drawImage(TLCUI.Images.COMPONENT_FRAME, getX(), getY(), getWidth(), getHeight(), null);

        drawCaption(g);
        drawValue(g);
    }


    private void drawCaption(final Graphics2D g)
    {
        TLCUI.Fonts.getCustomLabelFont().drawString(g, caption.toUpperCase(), getX()+CAPTION_X, getY()+CAPTION_Y);
    }


    private void drawValue(final Graphics2D g)
    {
        TLCUI.Fonts.getCustomValueFont().drawString(g, value, getX()+VALUE_X, getY()+VALUE_Y);
    }

    public void setCaption(final String caption)
    {
        this.caption = caption;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(final String value)
    {
        this.value = value;
    }


    @Override
    public void keyPressed(final KeyEvent e)
    {
    }

    @Override
    public void keyReleased(final KeyEvent e)
    {
    }

    @Override
    public void keyTyped(final KeyEvent e)
    {
    }

    @Override
    public void mouseClicked(final MouseEvent e)
    {
    }

    @Override
    public void mouseMoved(final MouseEvent e)
    {
    }

    @Override
    public void mousePressed(final MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(final MouseEvent e)
    {
    }

    @Override
    public void advance()
    {
    }
}
