package tlc.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import mhframework.MHDisplayModeChooser;
import mhframework.MHScreen;
import mhframework.gui.MHGUIChatClient;
import mhframework.gui.MHGUIDialogBox;
import mhframework.media.MHFont;

public abstract class TLCScreenBase extends MHScreen
{
    private static final int TITLE_Y = 60;
    protected MHGUIChatClient chatClient;
    private MHGUIDialogBox dialog;

    
    protected void drawTitle(String title, Graphics2D g)
    {
        // Draw the title background
        g.drawImage(TLCUI.Images.TEAM_TITLE_BANNER, 0, 0, null);
        // Draw title text
        centerText(g, title.toUpperCase(), TITLE_Y, TLCUI.Fonts.getScreenTitleFont());
    }
    
    
    protected void drawTitle(String title, Graphics2D g, Color color)
    {
        g.drawImage(TLCUI.Images.TEAM_TITLE_BANNER, 0, 0, null);
        
        // Colored stripes
        drawStripes(g, color);
        
        centerText(g, title.toUpperCase(), TITLE_Y, TLCUI.Fonts.getScreenTitleFont());
    }


    
    private void drawStripes(Graphics2D g, Color color)
    {
        double x = 75;
        double y = 15;
        int width = 685;
        int height = 65;
        double ySpacing = 3;
        double xSpacing = ySpacing * 0.75;
        
        g.setColor(color);
        while (y <= height)
        {
            g.drawLine((int)x, (int)y, (int)x+width, (int)y);
            x -= xSpacing;
            y += ySpacing;
        }
    }


    public void advance()
    {
        super.advance();
        
        if (chatClient != null)
            chatClient.advance();
    }
    
    
    private MHGUIDialogBox getDialogBox(MHScreen screen, String txt, MHFont font)
    {
        if (dialog == null)
            dialog = new MHGUIDialogBox(screen, txt, font);
        else
        {
            dialog.setPreviousScreen(screen);
            dialog.setText(txt);
            dialog.setFont(font);
        }
        
        return dialog;
    }
    
    
    public void showDialog(MHScreen screen, String txt, MHFont font)
    {
        dialog = getDialogBox(screen, txt, font);
        setNextScreen(dialog);
        setFinished(true);
    }

    
    public void showDialog(TLCScreenBase screen, String helpText)
    {
        dialog = getDialogBox(screen, helpText, TLCUI.Fonts.getHelpFont());
        setNextScreen(dialog);
        setFinished(true);
    }



    

    public void showDialog(TLCScreenBase screen, String helpText,
            String titleText, MHFont helpTextFont, MHFont titleTextFont)
    {
        dialog = getDialogBox(screen, helpText, titleText, helpTextFont, titleTextFont);
        setNextScreen(dialog);
        setFinished(true);
    }


    private MHGUIDialogBox getDialogBox(TLCScreenBase screen, String helpText,
            String titleText, MHFont helpTextFont, MHFont titleTextFont)
    {
        if (dialog == null)
            dialog = new MHGUIDialogBox(screen, helpText, titleText, helpTextFont, titleTextFont);
        else
        {
            dialog.setPreviousScreen(screen);
            dialog.setFont(helpTextFont);
            dialog.setTitleFont(titleTextFont);
            dialog.setText(helpText);
            dialog.setTitle(titleText);
        }
        
        return dialog;
    }
}
