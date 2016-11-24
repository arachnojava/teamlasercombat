package tlc.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUINumericCycleControl;
import mhframework.media.MHResourceManager;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.net.TLCGameClient;

public class TLCGameSetupScreen extends TLCScreenBase
{
    // TODO:  Add controls for setting alliance mode and configuring victory conditions.

    // UI components
    private MHGUINumericCycleControl cycNumber;
    private TLCCustomComponent numTeamsComponent;
    private MHGUIButton btnConnect;

    int soundID;
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


    
    private TLCCustomComponent getNumTeamsComponent()
    {
        if (numTeamsComponent == null)
        {
            numTeamsComponent = TLCUI.createCustomComponent("#Players/Teams", "");
            centerComponent(numTeamsComponent);
            numTeamsComponent.setY(200);
        }
        return numTeamsComponent;
    }
    
    
    private MHGUIButton getBtnConnect()
    {
        if (btnConnect == null)
        {
            btnConnect = TLCUI.createLargeButton("Launch Server");
            btnConnect.setPosition(300, 500);
            btnConnect.addActionListener(this);
        }

        return btnConnect;
    }


    private MHGUINumericCycleControl getCycNumber()
    {
        if (cycNumber == null)
        {
            cycNumber = new MHGUINumericCycleControl();
            cycNumber.setSize(80, 25);
            centerComponent(cycNumber);
            cycNumber.setY(numTeamsComponent.getY() + TLCCustomComponent.VALUE_Y - cycNumber.getHeight()*2);
            cycNumber.setFont(TLCUI.Fonts.getDataFont());
            cycNumber.setMinValue(2);
            cycNumber.setMaxValue(8);
            cycNumber.setSelectedIndex(2);
        }

        return cycNumber;
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
            g.setFont(TLCUI.Fonts.TEXT);
            centerText(g, "Starting server...", MHDisplayModeChooser.getScreenSize().height/2, Color.RED, true, 4);
        }
        
        drawStatusBar(TLCGameClient.getStatusMessage(), g);
    }


    @Override
    public void load()
    {
        add(getNumTeamsComponent());
        add(getCycNumber());
        add(getBtnConnect());

        soundID = MHResourceManager.getSoundManager().addSound("audio/DefineParameters.wav");
        MHResourceManager.getSoundManager().play(soundID);
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
            // TODO:  Save selected values in data facade.
            TLCDataFacade.getInstance(TLCMain.DATA_ID).setNumTeams((Integer)cycNumber.getSelectedValue());

            state = State.CONNECTING;
            // Tell the client module to connect.
            TLCGameClient.connect();
        }
    }


    @Override
    public void advance()
    {
        super.advance();

        centerComponent(cycNumber);
        cycNumber.setY(numTeamsComponent.getY() + TLCCustomComponent.VALUE_Y - cycNumber.getHeight());

        if (state == State.CONNECTED)
        {
            setDisposable(true);

            if (isPlayer())
                setNextScreen(new TLCTeamSetupScreen());
            else
                setNextScreen(new TLCLobbyScreen());

            setFinished(true);
        }

        if (state == State.CONNECTING)
        {
            if (TLCGameClient.isConnected())
                state = State.CONNECTED;
        }

    }

}
