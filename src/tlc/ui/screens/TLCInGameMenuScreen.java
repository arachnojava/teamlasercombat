package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import mhframework.MHGame;
import mhframework.gui.MHGUIButton;
import tlc.data.TLCDataFacade;
import tlc.ui.TLCUI;

public class TLCInGameMenuScreen extends TLCOptionsScreen
{
    private TLCScreenBase parent;
    private Color bgColor = new Color(0, 0, 0, 150);
    private ArrayList<MHGUIButton> buttons;
    
    private MHGUIButton btnResults, btnAutoDefense, btnAutoAttack, btnExit;
    
    public TLCInGameMenuScreen(TLCScreenBase parent)
    {
        super();
        
        this.parent = parent;
        
        String s = "Combat Results are " + onOff(TLCDataFacade.isCombatResultsOn());
        btnResults = TLCUI.createLargeButton(s);
        btnResults.addActionListener(this);
        add(btnResults);

        s = "Auto Attack is " + onOff(data.isAutoAttackOn());
        btnAutoAttack = TLCUI.createLargeButton(s);
        btnAutoAttack.addActionListener(this);
        add(btnAutoAttack);

        s = "Auto Defense is " + onOff(data.isAutoDefenseOn());
        btnAutoDefense = TLCUI.createLargeButton(s);
        btnAutoDefense.addActionListener(this);
        add(btnAutoDefense);

        btnExit = TLCUI.createLargeButton("Exit Program");
        btnExit.addActionListener(this);
        add(btnExit);
    }
    
    private String onOff(boolean value)
    {
        if (value) return "ON";
        return "OFF";
    }
    
    public void drawBackground(Graphics2D g)
    {
        parent.render(g);
        fill(g, bgColor);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnExit)
            MHGame.setProgramOver(true);
        else if (e.getSource() == btnAutoAttack)
        {
            data.setAutoAttackOn(!data.isAutoAttackOn());
            btnAutoAttack.setText("Auto Attack is " + onOff(data.isAutoAttackOn()));
        }
        else if (e.getSource() == btnAutoDefense)
        {
            data.setAutoDefenseOn(!data.isAutoDefenseOn());
            btnAutoDefense.setText("Auto Defense is " + onOff(data.isAutoDefenseOn()));
        }
        else if (e.getSource() == btnResults)
        {
            TLCDataFacade.setCombatResultsOn(!TLCDataFacade.isCombatResultsOn());
            btnResults.setText("Combat Results are " + onOff(TLCDataFacade.isCombatResultsOn()));
        }
        else
            super.actionPerformed(e);
    }

    
    
    

    @Override
    public void load()
    {
        btnAutoAttack.setText("Auto Attack is " + onOff(data.isAutoAttackOn()));
        btnAutoDefense.setText("Auto Defense is " + onOff(data.isAutoDefenseOn()));
        btnResults.setText("Combat Results are " + onOff(TLCDataFacade.isCombatResultsOn()));
        
        setFinished(false);
        setDisposable(false);
        buttons = new ArrayList<MHGUIButton>();
        buttons.add(btnSound);
        buttons.add(btnMusic);
        buttons.add(btnResults);
        buttons.add(btnAutoAttack);
        buttons.add(btnAutoDefense);
        buttons.add(btnExit);
        int y = 120;
        final int spacing = 60;
        for (MHGUIButton b : buttons)
        {
            centerComponent(b);
            b.setY(y);
            y += spacing;
        }
        
        parent.getComponentList().hideAll();
    }


    @Override
    public void unload()
    {
        parent.getComponentList().showAll();
    }


    @Override
    public void advance()
    {
        super.advance();
        parent.advance();
    }

    
    @Override
    public void keyReleased(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_ESCAPE:
                setFinished(true);
                break;
        }
    }

}
