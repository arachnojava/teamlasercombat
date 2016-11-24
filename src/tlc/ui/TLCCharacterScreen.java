package tlc.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIComponent;
import mhframework.gui.MHGUIInputDialogScreen;
import mhframework.media.MHFont;
import tlc.data.characters.TLCCharacter;
import tlc.net.TLCGameClient;

public class TLCCharacterScreen extends TLCScreenBase
{
    private TLCCharacter character;
    private MHGUIComponent nameComponent;
    private MHGUIInputDialogScreen scrChangeName;
    private MHGUIButton btnChangeName;
    private MHGUIButton btnDone;

    
    public TLCCharacterScreen(TLCCharacter c)
    {
        character = c;
        nameComponent = TLCUI.createCustomComponent("Name", character.getName());
        add(nameComponent);
     
        btnChangeName = TLCUI.createSmallButton("Change");
        btnChangeName.setPosition(580, 200);
        btnChangeName.addActionListener(this);
        add(btnChangeName);
        
        btnDone = TLCUI.createLargeButton("Done");
        btnDone.addActionListener(this);
        add(btnDone);
    }
    
    
    public void render(Graphics2D g)
    {
        fill(g, Color.BLACK);

        MHFont sectionFont = TLCUI.Fonts.getDialogTitleFont();
        
        centerText(g, "Equipment", (int)(MHDisplayModeChooser.getWidth()*0.25), 220, sectionFont);
        centerText(g, "Training", (int)(MHDisplayModeChooser.getWidth()*0.75), 220, sectionFont);
        
        
        super.render(g);
        drawTitle("EQUIP & TRAIN", g);
        drawStatusBar(TLCGameClient.getStatusMessage(), g);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnDone)
        {
            setFinished(true);
            setNextScreen(null);
        }
        else if (e.getSource() == btnChangeName)
        {
            // Open dialog screen for entering player name
            scrChangeName = new TLCInputScreen(nameComponent, "Name", character.getName());
            scrChangeName.setTitle("");
            scrChangeName.setMessage("");
            setNextScreen(scrChangeName);
            setFinished(true);
        }
    }


    @Override
    public void load()
    {
        if (scrChangeName != null)
        {
            final String name = scrChangeName.getInputText();
            ((TLCCustomComponent)nameComponent).setValue(name);
            character.setName(name);
            TLCGameClient.sendCharacterUpdateMessage(character);
            setFinished(false);
            setNextScreen(null);
            scrChangeName = null;
        }

        centerComponent(nameComponent);
        nameComponent.setY(TLCUI.Images.TEAM_TITLE_BANNER.getHeight(null) + 5);
        btnChangeName.setPosition(nameComponent.getX()+480, nameComponent.getY()+50);

        btnDone.setX(MHDisplayModeChooser.getWidth() - btnDone.getWidth() - 5);
        btnDone.setY(MHDisplayModeChooser.getHeight() - statusBarHeight - btnDone.getHeight()-5);
    }


    @Override
    public void unload()
    {
        // TODO Auto-generated method stub

    }

}
