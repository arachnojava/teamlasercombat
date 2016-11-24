package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.ui.TLCUI;

public class TLCOptionsScreen extends TLCScreenBase
{
    protected MHGUIButton btnDone, btnSound, btnMusic;
    private boolean soundOn = true, musicOn = true;
    
    protected TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
    
    public TLCOptionsScreen()
    {
        btnDone = TLCUI.createLargeButton("Return");
        btnDone.addActionListener(this);
        btnDone.setPosition(MHDisplayModeChooser.getWidth() - btnDone.getWidth(), MHDisplayModeChooser.getHeight() - statusBarHeight - btnDone.getHeight());
        add(btnDone);
        
        soundOn = data.isSoundOn();
        musicOn = data.isMusicOn();
        
        String s = "Sound is " + (soundOn ? "ON" : "OFF");
        btnSound = TLCUI.createLargeButton(s);
        btnSound.addActionListener(this);
        add(btnSound);

        s = "Music is " + (musicOn ? "ON" : "OFF");
        btnMusic = TLCUI.createLargeButton(s);
        btnMusic.addActionListener(this);
        add(btnMusic);
    }
    
    public void advance()
    {
        super.advance();
    }
    
    
    @Override
    public void render(final Graphics2D g)
    {
        drawBackground(g);
        drawTitle("Options", g);
        super.render(g);

        drawStatusBar(data.getStatusMessage(), g);
    }

    
    public void drawBackground(Graphics2D g)
    {
        fill(g, Color.BLACK);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnDone)
        {
            setFinished(true);
        }
        else if (e.getSource() == btnSound)
        {
            soundOn = !soundOn;
            data.setSoundOn(soundOn);
            
            if (soundOn)
                btnSound.setText("Sound is ON");
            else
                btnSound.setText("Sound is OFF");
        }
        else if (e.getSource() == btnMusic)
        {
            musicOn = !musicOn;
            data.setMusicOn(musicOn);
            
            if (musicOn)
                btnMusic.setText("Music is ON");
            else
                btnMusic.setText("Music is OFF");
        }
    }


    @Override
    public void load()
    {
        centerComponent(btnSound);
        btnSound.setY(200);
        
        centerComponent(btnMusic);
        btnMusic.setY(btnSound.getY() + btnSound.getHeight() + 100);
    }


    @Override
    public void unload()
    {
    }

}
