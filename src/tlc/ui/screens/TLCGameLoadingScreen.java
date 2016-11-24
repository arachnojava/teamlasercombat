package tlc.ui.screens;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import mhframework.MHDisplayModeChooser;
import mhframework.MHRandom;
import mhframework.gui.MHGUIProgressBar;
import mhframework.media.MHFont;
import tlc.ui.TLCUI;

public class TLCGameLoadingScreen extends TLCScreenBase
{
    private TLCGameScreen gameScreen;
    private TLCTipDisplay tip;
    private MHGUIProgressBar progressBar;
    private Color progressBarTextColor = Color.WHITE;
    private int alpha = 10; // rate of change for progress bar color
    
    public TLCGameLoadingScreen()
    {
        // Start loading the game screen.
        gameScreen = new TLCGameScreen();
        gameScreen.startLoading();
        
        // Pick helpful tip to display.
        tip = new TLCTipDisplay();
        
        
        // Create progress bar.
        progressBar = new MHGUIProgressBar();
        progressBar.setX(MHDisplayModeChooser.getCenterX() - 300);
        progressBar.setY(MHDisplayModeChooser.getHeight() - 200);
        progressBar.setHeight(50);
        progressBar.setWidth(600);
        progressBar.setMaxValue(100);
        Point2D top = new Point2D.Double(progressBar.getX(), progressBar.getY());
        Point2D bottom = new Point2D.Double(progressBar.getX(), progressBar.getY()+progressBar.getHeight()/2);
        progressBar.setPaint(new GradientPaint(top, new Color(100, 0, 0, 128), bottom, new Color(255, 128, 128, 220), true));
        
        add(progressBar);
    }

    
    
    
    
    @Override
    public void actionPerformed(ActionEvent arg0)
    {
    }


    @Override
    public void load()
    {
        // Start loading thread.
    }


    @Override
    public void unload()
    {
    }





    @Override
    public void advance()
    {
        super.advance();
        
        // If done loading, go to the game screen.
        double pctLoaded = gameScreen.getPercentLoaded();
        if (pctLoaded >= 1.0)
        {
            setNextScreen(gameScreen);
            setFinished(true);
            setDisposable(true);
        }
        else
        {
            Color c = progressBarTextColor;
            if (c.getAlpha() + alpha  >= 255)
            {
                progressBarTextColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
                alpha *= -1;
            }
            else if (c.getAlpha() + alpha <= 0)
            {
                progressBarTextColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
                alpha *= -1;
            }
            else
            {
                progressBarTextColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() + alpha);
            }
            
            progressBar.update(pctLoaded*100);
            progressBar.setTextColor(progressBarTextColor);
            progressBar.setText((int)(pctLoaded * 100) + "%");
        }
    }




    @Override
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);
        drawTitle("LOADING", g);
        tip.render(g);
        super.render(g);
    }

    
    private class TLCTipDisplay
    {
        private MHFont font = TLCUI.Fonts.getHelpFont();
        private String tip;
        private Rectangle2D textArea;
        
        public TLCTipDisplay()
        {
            tip = "TIP:  " + pickTip();
            textArea = new Rectangle2D.Double(MHDisplayModeChooser.getCenterX()-300, MHDisplayModeChooser.getHeight()/3, 600, 200);
        }
        
        
        private String pickTip()
        {
            String[] array = new String[]
            {
                "Be on the lookout for objects that look like containers, such as crates, barrels, buckets, and so on.  You may be able to destroy them and reveal bonus items.  Just be sure to get to them before your opponents do!",
                "Remember that characters can only attack in eight directions.  When you end your turn, try to leave your characters out of your opponents' line of fire.",
                "Spectator Mode is a great way for new would-be players to see what the game is like and get a feel for how it is played.  Invite friends to watch and maybe they'll join in next time!",
                "Moving, drawing tokens, attacking, and defending are all based mostly on random numbers.  Upgrading your characters' equipment and traininig increases the odds that this randomness will be in your favor.",
                "It only takes one attack point to destroy a container, so it may be wise to use your weaker combat tokens on containers and save your stronger ones to use against opponents.",
                "Does the combat summary seem like TMI (too much information)?  You can turn it off by clicking the Menu button and then the Combat Results button.",
                "The Auto Defense feature lets your players decide how to defend themselves, rather than requiring a decision from you. To enable or disable this feature, click the Menu button and then the Auto Defense button.",
                "The Auto Attack feature lets your players decide which combat tokens to use for each attack, rather than requiring a decision from you. To enable or disable this feature, click the Menu button and then the Auto Attack button.",
                "The players on your team can take their turns in any order you wish. Use the Next Character and Previous Character buttons on the Actions Menu to cycle through your players and issue commands.",
                "Combat tokens determine your ability to attack and defend.  Don't forget to draw some from time to time, especially if you're using Auto Attack or Auto Defense."
            };
            
            return array[MHRandom.random(0, array.length-1)];
        }

    
        public void render(Graphics2D g)
        {
            g.setColor(Color.WHITE);
            String[] lines = font.splitLines(tip, (int)textArea.getWidth());
            for (int s = 0; s < lines.length; s++)
                font.drawString(g, lines[s], (int)textArea.getX(), (int)textArea.getY() + font.getHeight() * s);
        }
    }
}
