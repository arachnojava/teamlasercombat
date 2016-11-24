package tlc.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHFont;
import tlc.data.TLCTokenType;

public class TLCPowerTokenButton extends MHGUIButton
{
    private MHFont font = TLCUI.Fonts.BUTTON_12;
    private Image captionImage;
    private TLCTokenType tokenType;

    public TLCPowerTokenButton(TLCTokenType type)
    {
        super(TLCUI.Images.BUTTON_POWER_TOKEN, TLCUI.Images.BUTTON_POWER_TOKEN, TLCUI.Images.BUTTON_POWER_TOKEN);
        setFont(font);
        setForeColor(Color.WHITE);
        tokenType = type;
        setText(tokenType.getName());
    }

 
    @Override
    protected void renderCaption(Graphics2D g)
    {
        int w = font.stringWidth(getCaptionText());
        int cx = w/2;
        double rotation = -40.0;
        
        captionImage = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)captionImage.getGraphics();
        final AffineTransform originalTransform = g2.getTransform();
        g2.rotate(rotation * (Math.PI / 180.0), cx, cx);  //.rotate(rotation * Math.PI / 180.0, w / 2.0, h / 2.0);
        g2.setColor(Color.BLACK);
        font.drawString(g2, getCaptionText(), 0, cx-1);
        font.drawString(g2, getCaptionText(), 2, cx+1);
        g2.setColor(Color.WHITE);
        font.drawString(g2, getCaptionText(), 1, cx);
        g2.setTransform(originalTransform);
        
        int captionX = (getX()+getWidth()/2)-cx;
        int captionY = (getY()+getHeight()/2)-cx;
        g.drawImage(captionImage, captionX, captionY, null); //(int)x, (int)y, w, h, null);
    }
    
    
    public TLCTokenType getTokenType()
    {
        return tokenType;
    }
}