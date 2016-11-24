package tlc.ui;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIComponent;
import mhframework.gui.MHGUIInputDialogScreen;
import mhframework.media.MHResourceManager;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCPlayerMode;
import tlc.net.TLCGameClient;

public class TLCLoginScreen extends TLCScreenBase
{
    private static final String DEFAULT = "Not set";

    private final String namePrompt = "Player Name";
    private String nameValue = DEFAULT;
    private MHGUIInputDialogScreen scrPlayerName;

    private final String ipPrompt = "Server IP";
    private String ipValue = DEFAULT;
    private MHGUIInputDialogScreen scrServerIP;

    private MHGUIButton btnContinue, btnBack;

    private MHGUIButton btnChangeName;

    private MHGUIButton btnEnterIP;

    MHGUIComponent nameComponent, ipComponent;
    int nameY = 150,
        ipY = 300;


    public TLCLoginScreen()
    {
    }


    @Override
    public void load()
    {
        nameComponent = TLCUI.createCustomComponent(namePrompt, nameValue);
        centerComponent(nameComponent);
        nameComponent.setY(nameY);

        ipComponent = TLCUI.createCustomComponent(ipPrompt, ipValue);
        centerComponent(ipComponent);
        ipComponent.setY(ipY);

        btnContinue = TLCUI.createSmallButton("Continue");
        btnContinue.setPosition(MHDisplayModeChooser.getCenterX()-8, 550);
        btnContinue.addActionListener(this);
        add(btnContinue);

        btnBack = TLCUI.createSmallButton("Back");
        btnBack.setPosition(MHDisplayModeChooser.getCenterX()-btnBack.getWidth()+8, 550);
        btnBack.addActionListener(this);
        add(btnBack);

        btnChangeName = TLCUI.createSmallButton("Change");
        btnChangeName.setPosition(580, 200);
        btnChangeName.addActionListener(this);
        add(btnChangeName);

        if (!isHosting())
        {
            btnEnterIP = TLCUI.createSmallButton("Enter IP");
            btnEnterIP.setPosition(580, 350);
            btnEnterIP.addActionListener(this);
            add(btnEnterIP);
        }
        if (scrPlayerName != null)
        {
            nameValue = scrPlayerName.getInputText();
            nameComponent = TLCUI.createCustomComponent(namePrompt, nameValue);
            nameComponent.setPosition(0, nameY);
            setFinished(false);
            setNextScreen(null);
            scrPlayerName = null;
        }
        if (scrServerIP != null)
        {
            ipValue = scrServerIP.getInputText();
            ipComponent = TLCUI.createCustomComponent(ipPrompt, ipValue);
            ipComponent.setPosition(0, ipY);
            setFinished(false);
            setNextScreen(null);
            scrServerIP = null;
        }
    }


    @Override
    public void unload()
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void render(final Graphics2D g)
    {
        //g.setColor(Color.BLACK);
        //g.fillRect(0, 0, MHDisplayModeChooser.getScreenSize().width, MHDisplayModeChooser.getScreenSize().height);

        tileImage(g, TLCUI.Images.BACKGROUND_TEXTURE, 0, 0);

        centerComponent(nameComponent);
        nameComponent.render(g);

        if (ipValue != null && !isHosting())
        {
            centerComponent(ipComponent);
            ipComponent.render(g);
        }

        drawTitle("Welcome!", g);
        
        super.render(g);
    }


    private boolean isHosting()
    {
        return !(TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode() == TLCPlayerMode.JOIN_LAN
        || TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode() == TLCPlayerMode.SPECTATOR);
    }


    @Override
    public void actionPerformed(final ActionEvent e)
    {
        final Object source = e.getSource();

        if (source == btnBack)
        {
            setFinished(true);
        }
        else if (source == btnChangeName)
        {
            // Open dialog screen for entering player name
            scrPlayerName = new TLCInputScreen(nameComponent, namePrompt, nameValue);
            scrPlayerName.setTitle("");
            scrPlayerName.setMessage("");
            setNextScreen(scrPlayerName);
            setFinished(true);
        }
        else if (source == btnEnterIP)
        {
            // Open dialog screen for entering server IP
            scrServerIP = new TLCInputScreen(ipComponent, ipPrompt, ipValue);
            scrServerIP.setTitle("");
            scrServerIP.setMessage("");
            setNextScreen(scrServerIP);
            setFinished(true);
        }
        else if (source == btnContinue)
        {
            // Check to see that name and IP have both been entered.
            if (nameValue.equals(DEFAULT) || nameValue.length() <= 0)
            {
                showErrorMessage("Please enter your name.");
            }
            else if ((ipValue == null || ipValue.equals(DEFAULT)) && !isHosting())
            {
                showErrorMessage("Please enter the server's IP address.");
            }
            else
            {
                if (isHosting())
                {
                    nameValue += " (Host)";
                    TLCGameClient.setPlayerName(nameValue);
                    setNextScreen(new TLCGameSetupScreen());
                }
                else if (TLCDataFacade.getInstance(TLCMain.DATA_ID).getPlayerMode() == TLCPlayerMode.SPECTATOR)
                {
                    TLCGameClient.setServerIP(ipValue);
                    TLCGameClient.setPlayerName(nameValue);
                    TLCGameClient.connect();
                    setNextScreen(new TLCLobbyScreen());
                }
                else 
                {
                    TLCGameClient.setServerIP(ipValue);
                    TLCGameClient.setPlayerName(nameValue);
                    TLCGameClient.connect();
                    setNextScreen(new TLCTeamSetupScreen());
                }

                setDisposable(true);
                setFinished(true);
            }
        }
    }
}
