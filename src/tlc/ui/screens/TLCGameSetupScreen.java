package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUINumericCycleControl;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.ui.TLCCustomComponent;
import tlc.ui.TLCUI;

public class TLCGameSetupScreen extends TLCScreenBase
{
    // TODO:  Add controls for setting alliance mode and configuring victory conditions.

    // UI components
    private MHGUINumericCycleControl cycNumHumans, cycNumAI;
    private TLCCustomComponent numHumansComponent, numAIComponent;
    private MHGUIButton btnConnect;

    // Server connection stuff
    private enum State
    {
        SETUP, CONNECTING, CONNECTED
    }
    private State state;

    public TLCGameSetupScreen()
    {
        state = State.SETUP;
    }


    
    private TLCCustomComponent getNumHumansComponent()
    {
        if (numHumansComponent == null)
        {
            numHumansComponent = TLCUI.createCustomComponent("# Humans", "");
            centerComponent(numHumansComponent);
            numHumansComponent.setY(100);
        }
        return numHumansComponent;
    }
    
    
    private TLCCustomComponent getNumAIComponent()
    {
        if (numAIComponent == null)
        {
            numAIComponent = TLCUI.createCustomComponent("# Computers", "");
            centerComponent(numAIComponent);
            numAIComponent.setY(numHumansComponent.getY()+100);
        }
        return numAIComponent;
    }
    
    
    private MHGUIButton getBtnConnect()
    {
        if (btnConnect == null)
        {
            btnConnect = TLCUI.createLargeButton("Launch Server");
            centerComponent(btnConnect);
            btnConnect.setY(MHDisplayModeChooser.getHeight() - 100);
            btnConnect.addActionListener(this);
        }

        return btnConnect;
    }


    private MHGUINumericCycleControl getCycNumHumans()
    {
        if (cycNumHumans == null)
        {
            cycNumHumans = new MHGUINumericCycleControl();
            cycNumHumans.setSize(80, 25);
            centerComponent(cycNumHumans);
            cycNumHumans.setY(numHumansComponent.getY() + TLCCustomComponent.VALUE_Y - cycNumHumans.getHeight()*2);
            cycNumHumans.setFont(TLCUI.Fonts.getDataFont());
            cycNumHumans.setMinValue(0);
            cycNumHumans.setMaxValue(TLCDataFacade.MAX_PLAYERS);
            cycNumHumans.setSelectedIndex(2);
        }

        return cycNumHumans;
    }


    private MHGUINumericCycleControl getCycNumAI()
    {
        if (cycNumAI == null)
        {
            cycNumAI = new MHGUINumericCycleControl();
            cycNumAI.setSize(80, 25);
            centerComponent(cycNumAI);
            cycNumAI.setY(numAIComponent.getY() + TLCCustomComponent.VALUE_Y - cycNumAI.getHeight()*2);
            cycNumAI.setFont(TLCUI.Fonts.getDataFont());
            cycNumAI.setMinValue(0);
            cycNumAI.setMaxValue(TLCDataFacade.MAX_PLAYERS);
            cycNumAI.setSelectedIndex(1);
        }

        return cycNumAI;
    }


    private boolean isPlayer()
    {
        final TLCPlayerMode mode = TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode();

        return (mode == TLCPlayerMode.SINGLE_PLAYER || mode == TLCPlayerMode.HOST_LAN || mode == TLCPlayerMode.JOIN_LAN);
    }


    @Override
    public void render(final Graphics2D g)
    {
        fill(g, Color.BLACK);
        drawTitle("Game Setup", g);
        super.render(g);

        if (state == State.CONNECTING)
        {
            g.setColor(new Color(0, 0, 0, 222));
            final Rectangle2D r = MHDisplayModeChooser.getBounds();
            g.fillRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
            g.setColor(Color.WHITE);
            centerText(g, "Starting server...", MHDisplayModeChooser.getScreenSize().height/2, TLCUI.Fonts.getHelpFont());
        }
        
        drawStatusBar(TLCDataFacade.getInstance(TLCMain.DATA_ID).getStatusMessage(), g);
    }


    @Override
    public void load()
    {
        add(getNumHumansComponent());
        add(getCycNumHumans());
        add(getNumAIComponent());
        add(getCycNumAI());
        add(getBtnConnect());

        //soundID = MHResourceManager.getSoundManager().addSound("audio/DefineParameters.wav");
        //MHResourceManager.getSoundManager().play(soundID);
    }


    @Override
    public void unload()
    {
        //MHResourceManager.getSoundManager().remove(soundID);
    }


    @Override
    public void keyPressed(final KeyEvent e)
    {
        super.keyPressed(e);
    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        if (state != State.SETUP) return;

        if (e.getSource() == btnConnect)
        {
            btnConnect.setPosition(-9999, -9999);
            // Save selected values in data facade.
            TLCDataFacade.getInstance(TLCMain.DATA_ID).setNumHumanPlayers((Integer)cycNumHumans.getSelectedValue());
            TLCDataFacade.getInstance(TLCMain.DATA_ID).setNumAIPlayers((Integer)cycNumAI.getSelectedValue());

            state = State.CONNECTING;
            
            // Tell the client module to connect.
            TLCDataFacade.getInstance(TLCMain.DATA_ID).connect();
            
        }
    }


    @Override
    public void advance()
    {
        super.advance();

        centerComponent(cycNumHumans);
        cycNumHumans.setY(numHumansComponent.getY() + TLCCustomComponent.VALUE_Y - cycNumHumans.getHeight());

        centerComponent(cycNumAI);
        cycNumAI.setY(numAIComponent.getY() + TLCCustomComponent.VALUE_Y - cycNumAI.getHeight());

        final TLCPlayerMode mode = TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode();
        if (mode.equals(TLCPlayerMode.SINGLE_PLAYER))
        {
            cycNumHumans.setSelectedIndex(1);
            cycNumHumans.setPosition(99999, 99999);
            numHumansComponent.setValue("1");
            cycNumAI.setMinValue(1);
            cycNumAI.setMaxValue(TLCDataFacade.MAX_PLAYERS - 1);
        }
        
        // Make sure selected values are valid.
        int humans = ((Integer)cycNumHumans.getSelectedValue()).intValue();
        int ai = ((Integer)cycNumAI.getSelectedValue()).intValue();

        if (humans + ai > TLCDataFacade.MAX_PLAYERS)
        {
            ai = TLCDataFacade.MAX_PLAYERS - humans;
            cycNumAI.setSelectedIndex(ai);
        }

        if (state == State.CONNECTED)
        {
            setDisposable(true);

            if (isPlayer())
                setNextScreen(new TLCTeamSetupScreen());
            else
                setNextScreen(new TLCLobbyScreen());

            setFinished(true);
        }
        else if (state == State.CONNECTING)
        {
            if (TLCDataFacade.getInstance(TLCMain.DATA_ID).isConnected())
                state = State.CONNECTED;
        }
    }
}
