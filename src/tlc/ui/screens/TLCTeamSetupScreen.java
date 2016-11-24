package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGameApplication;
import mhframework.MHVideoSettings;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIColorCycleControl;
import mhframework.gui.MHGUIComponent;
import mhframework.gui.MHGUILabel;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.TLCTeamColor;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCNames;
import tlc.net.TLCMessageType;
import tlc.net.client.TLCGameClient;
import tlc.ui.TLCCustomComponent;
import tlc.ui.TLCUI;

public class TLCTeamSetupScreen extends TLCScreenBase
{
    private static final String SCREEN_TITLE = "Team Setup";

    private static final int SPACING = 20;

    TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
    
    // Enter Player Name
    private final MHGUIComponent teamNameComponent;
    private final MHGUIButton btnChangeTeamName;
    private TLCInputScreen scrTeamName;
    private String strTeamName = "";

    // Select Team Color
    private final MHGUIComponent teamColorComponent;
    private final MHGUIColorCycleControl cycColors;
    int nextColor = 0;

    // Choose Team Captain
    private MHGUILabel lblCaptainPrompt;
    private final MHGUIButton btnMaleCaptain;
    private final MHGUIButton btnFemaleCaptain;

    private boolean teamSubmitted = false;
    private int dots = 3;
    private int timer = 0;

    public TLCTeamSetupScreen()
    {
        teamNameComponent = TLCUI.createCustomComponent("Team Name", "");
        add(teamNameComponent);
        
        btnChangeTeamName = TLCUI.createSmallButton("Change");
        btnChangeTeamName.addActionListener(this);
        add(btnChangeTeamName);

        lblCaptainPrompt = new MHGUILabel("FINALLY, CHOOSE YOUR CAPTAIN:");
        add(lblCaptainPrompt);
        
        btnMaleCaptain = new MHGUIButton();
        btnMaleCaptain.setText(TLCNames.boyName());
        btnMaleCaptain.setSize(128, 192);
        btnMaleCaptain.addActionListener(this);
        add(btnMaleCaptain);

        btnFemaleCaptain = new MHGUIButton();
        btnFemaleCaptain.setText(TLCNames.girlName());
        btnFemaleCaptain.setSize(128, 192);
        btnFemaleCaptain.addActionListener(this);
        add(btnFemaleCaptain);

        teamColorComponent = TLCUI.createCustomComponent("Team Color", "");
        add(teamColorComponent);
        
        cycColors = new MHGUIColorCycleControl();
        cycColors.setSize(250, 32);
        cycColors.setValues(data.getColorList());
        add(cycColors);
    }


    @Override
    public void load()
    {
        // Wait for server to tell us if we can play or not.
        while (data.getUser(data.getClientID()) == null)
        {
            try
            {
                Thread.sleep(100);
            } 
            catch (InterruptedException e)
            {
            }
        }
        
        if (false == data.isPlayer())
        {
            setNextScreen(new TLCLobbyScreen());
            setFinished(true);
            return;
        }
        
        if (scrTeamName != null)
        {
            final String name = scrTeamName.getInputText();
            strTeamName = name;
            setFinished(false);
            setNextScreen(null);
            scrTeamName = null;
        }

        final int width = MHDisplayModeChooser.getScreenSize().width;

        centerComponent(teamNameComponent);
        teamNameComponent.setY(90);
        btnChangeTeamName.setPosition(teamNameComponent.getX()+480, teamNameComponent.getY()+50);
        
        centerComponent(teamColorComponent);
        teamColorComponent.setY(teamNameComponent.getY()+teamNameComponent.getHeight()+5);
        centerComponent(cycColors);
        cycColors.setY(teamColorComponent.getY()+50);
        
        lblCaptainPrompt.setFont(TLCUI.Fonts.getCustomLabelFont());
        lblCaptainPrompt.setX(teamColorComponent.getX());
        lblCaptainPrompt.setY(teamColorComponent.getY()+teamColorComponent.getHeight() + 30);

        final int gap = 20;
        final int mx = width/2 - btnMaleCaptain.getWidth() - gap;
        btnMaleCaptain.setPosition(mx, MHDisplayModeChooser.getMaxY()-250);

        final int fx = width/2 + gap;
        btnFemaleCaptain.setPosition(fx, btnMaleCaptain.getY());
    }


    @Override
    public void unload()
    {
    }


    @Override
   public void actionPerformed(final ActionEvent e)
    {
        if (teamSubmitted) return;

        if (btnChangeTeamName == e.getSource())
        {
            // Open dialog screen for entering team name
            scrTeamName = new TLCInputScreen(teamNameComponent, "Team Name", "");
            setNextScreen(scrTeamName);
            setFinished(true);
        }
        else if (btnMaleCaptain == e.getSource())
        {
            // Create team and recruit character.
            teamSubmitted = true;
            createTeam(TLCCharacterGender.MALE, btnMaleCaptain.getCaptionText());
        }
        else if (btnFemaleCaptain == e.getSource())
        {
            // Create team and recruit character.
            teamSubmitted = true;
            createTeam(TLCCharacterGender.FEMALE, btnFemaleCaptain.getCaptionText());
        }
    }


    private void createTeam(final TLCCharacterGender gender, String name)
    {
        // DEBUG
        TLCGameClient.log("TLCTeamSetupScreen.createTeam()");
        System.out.println("\tTLCTeamSetupScreen.createTeam()");

        final TLCTeamColor c = TLCTeamColor.values()[nextColor];
        final TLCTeam t = new TLCTeam(c, data.getClientID());
        t.setTeamName(teamName());
        data.setPlayerColor(c.getColorValue());

        TLCGameClient.log("TLCTeamSetupScreen.createTeam():  Sending team request.");
        System.out.println("TLCTeamSetupScreen.createTeam():  Sending team request.");
        data.send(TLCMessageType.UPDATE_TEAM, t.getSerializableVersion());

        //while (data.getTeam(t.getID()) == null);  // Wait for team to be created before adding captain character.
        
        final TLCCharacter captain = new TLCCharacter(TLCCharacterType.CAPTAIN, gender);
        captain.setName(name);
        captain.setTeamID(data.getClientID());

        TLCGameClient.log("TLCTeamSetupScreen.createTeam():  Sending character request.");
        System.out.println("TLCTeamSetupScreen.createTeam():  Sending character request.");
        data.send(TLCMessageType.RECRUIT_CHARACTER, captain.getSerializableVersion());
    }

    
    @Override
    public void advance()
    {
        if (false == data.isPlayer())
        {
            setNextScreen(new TLCLobbyScreen());
            setFinished(true);
            return;
        }
        
        timer = (timer + 1) % Integer.MAX_VALUE;
        super.advance();

        // Set team name
        ((TLCCustomComponent)teamNameComponent).setValue(teamName());
        
        // Update colors
        cycColors.setValues(data.getColorList());

        // Check for server messages
        if (data.isErrorState())
        {
            showErrorMessage(data.getStatusMessage());
            TLCGameClient.log(data.getStatusMessage());
            teamSubmitted = false;
            data.clearErrorState();
        }
        else
            hideErrorMessage();
            

        // Check for team completion
        final TLCTeam team = TLCDataFacade.getTeam(data.getClientID());
        if (team != null)
        {
            System.out.println("DEBUG:  Team created!");
            // Team created!  Let's see if our captain has arrived.
            for (TLCCharacter c : TLCDataFacade.getCharacterList())
            {
                System.out.println("DEBUG: Team " + c.getTeamID() + "; " + c.getName() + ", " + c.getType().getTitle());
                
                if (c.getTeamID() == team.getID())
                {
                    // we have a character.  At this point, it can only be our captain.
                    //if (data.getPlayerMode().equals(TLCPlayerMode.SINGLE_PLAYER))
                    //    setNextScreen(new TLCTeamConfigScreen());
                    //else
                        setNextScreen(new TLCLobbyScreen());

                    System.out.println("DEBUG:  Captain received!");

                    setDisposable(true);
                    setFinished(true);
                    return;
                }
            }
        }
    }


    @Override
    public void render(final Graphics2D g)
    {
        // Check to see if selected color is still available.
        nextColor = cycColors.getSelectedIndex();
        
        do
        {
            nextColor = (nextColor+1) % TLCTeamColor.values().length;
        } while (!TLCTeamColor.values()[nextColor].getColorValue().equals(cycColors.getSelectedValue()));

        
        fill(g, Color.BLACK);

        super.drawTitle(SCREEN_TITLE, g);
        g.setColor(Color.WHITE);

        // Show labels and values
        //int y = btnChangeTeamName.getY()+btnChangeTeamName.getHeight() + SPACING;
        //y += btnChangeTeamName.getHeight() + SPACING;
        
        //final String colorName = TLCTeamColor.values()[nextColor].getName();
        //y += btnChangeTeamName.getHeight() + SPACING;
        
        //g.drawString(PROMPT_TEAM_CAPTAIN + " ", 100, y);

        if (teamSubmitted && !data.isErrorState())
        {
            String output = "Creating team";
            for (int i = 0; i < dots; i++)
                output += ".";
            for (int i = 0; i < 3-dots; i++)
                output += " ";
            showErrorMessage(output);
            if (timer % 10 == 0)
                dots++;
            dots %= 4;
        }
        super.render(g);

        drawStatusBar(data.getStatusMessage(), g);
    }



    private String teamName()
    {
        final String colorName = TLCTeamColor.values()[nextColor].getName();
        return (strTeamName.length() > 0 ? strTeamName : colorName + " Team");
    }


    public static void main(final String[] args)
    {
        final MHVideoSettings settings = new MHVideoSettings();
        settings.displayWidth = 800;
        settings.displayHeight = 600;
        settings.fullScreen = false;
        settings.windowCaption = "Team Laser Combat";

        new MHGameApplication(new TLCTeamSetupScreen(), settings);

        System.exit(0);
    }
}
