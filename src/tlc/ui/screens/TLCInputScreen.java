package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIComponent;
import mhframework.gui.MHGUIInputDialogScreen;
import tlc.ui.TLCCustomComponent;
import tlc.ui.TLCUI;

public class TLCInputScreen extends MHGUIInputDialogScreen
{
    private TLCCustomComponent component;
    private Color backgroundColor = new Color(0, 0, 0, 200);
    private Image backgroundImage;
    
    public TLCInputScreen(MHGUIComponent c, String caption, String value)
    {
        component = TLCUI.createCustomComponent(caption, value);
        component.setPosition(c.getX(), c.getY());
    }

    
    public void load()
    {
        createBackgroundImage();
    }
    
    private void createBackgroundImage()
    {
        backgroundImage = new BufferedImage(MHDisplayModeChooser.getWidth(), MHDisplayModeChooser.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = (Graphics2D) backgroundImage.getGraphics();
        getPreviousScreen().render(bg);
        bg.setColor(backgroundColor);
        bg.fillRect(0, 0, MHDisplayModeChooser.getWidth()*2, MHDisplayModeChooser.getHeight()*2);
    }

    
    @Override
    public void render(Graphics2D g)
    {
        g.drawImage(backgroundImage, 0, 0, null);
        ((TLCScreenBase)getPreviousScreen()).drawTitle("Enter Data", g);
        component.render(g);
    }

    @Override
    public void advance()
    {
        super.advance();
        component.setValue(getInputText());
    }
    
    
    
}
