package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGame;
import mhframework.MHRuntimeMetrics;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIClientListDisplay;
import mhframework.io.net.MHSerializableClientList;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.net.server.ai.TLCAIManager;
import tlc.ui.TLCClientListDisplay;
import tlc.ui.TLCPlayerMarquee;
import tlc.ui.TLCUI;

public class TLCLobbyScreen extends TLCScreenBase
{
        MHGUIClientListDisplay clientListDisplay;
        MHSerializableClientList clientList;
        
        MHGUIButton btnUpgradeTeam, btnExit, btnReady;
        private TLCPlayerMarquee marquee;
        int buttonSpacing;
        private boolean ready = false;
        private boolean allReady = false;
        private boolean waitingForPlayers = true;
        
        TLCDataFacade data = TLCDataFacade.getInstance(TLCMain.DATA_ID);
        private boolean flashOn = false;
        private long lastFlashTime = MHGame.getGameTimerValue();
        private String message = "INITIALIZING";
        
        public TLCLobbyScreen()
        {
            chatClient = TLCDataFacade.getInstance(TLCMain.DATA_ID).createChatClient(50, 100, 500 + (MHDisplayModeChooser.getWidth()-800), 400 + (MHDisplayModeChooser.getHeight()-600));
            add(chatClient);

            marquee = new TLCPlayerMarquee();
            marquee.setY(TLCUI.Images.TEAM_TITLE_BANNER.getHeight(null) + 2);
            
            btnUpgradeTeam = TLCUI.createLargeButton("Upgrade Team");
            btnUpgradeTeam.addActionListener(this);
            add(btnUpgradeTeam);
            
            btnExit = TLCUI.createLargeButton("Quit");
            btnExit.addActionListener(this);
            add(btnExit);

            btnReady = TLCUI.createLargeButton("Signal Ready");
            btnReady.addActionListener(this);
            add(btnReady);

            clientListDisplay = new TLCClientListDisplay();
            clientListDisplay.setSize(150, 400 + (MHDisplayModeChooser.getHeight()-600));
            clientListDisplay.setPosition((int) (MHDisplayModeChooser.getBounds().getWidth() - clientListDisplay.getBounds().getWidth() - 50), 110);
            add(clientListDisplay);
            
            buttonSpacing = btnReady.getWidth() - 15;
        }


        @Override
        public void load()
        {
            allReady = false;
            setFinished(false);
            int y = MHDisplayModeChooser.getHeight() - 60;
            btnExit.setVisible(true);

            if (data.isPlayer())
            {
                btnReady.setPosition(MHDisplayModeChooser.getWidth()-btnReady.getWidth(), y);
                btnUpgradeTeam.setPosition(btnReady.getX() - buttonSpacing, btnReady.getY());
                btnReady.setVisible(true);
            }
            else
            {
                btnReady.setPosition(-5000, -5000);
                btnUpgradeTeam.setPosition(btnReady.getX() - buttonSpacing, btnReady.getY());
            }
            
            btnExit.setPosition(0, y);
        }


        @Override
        public void unload()
        {
        }


        @Override
        public void advance()
        {
            super.advance();

            marquee.advance();
            
            TLCAIManager.advance();
            
            clientList = data.getClientList();
            clientListDisplay.setClientList(clientList);
            
            // If all players are connected...
            if (data.countPlayers() >= data.getNumAISelected() + data.getNumHumansSelected())
            {
                waitingForPlayers = false;
                btnReady.setVisible(true);
                btnExit.setVisible(true);
                btnUpgradeTeam.setVisible(true);
            }
            
            if (!allReady && data.countReadyHumans() == data.getNumHumansSelected())
                allReady = true;
            
            if (data.isInGameState())
            {
                setNextScreen(new TLCGameLoadingScreen());
                setFinished(true);
                setDisposable(false);
            }
        }


        @Override
        public void actionPerformed(final ActionEvent e)
        {
            if (e.getSource() == btnUpgradeTeam)
            {
                setNextScreen(new TLCTeamConfigScreen());
                setFinished(true);
            }
            else if (e.getSource() == btnReady)
            {
                if (allReady)
                {
                    ready = true;
                    return;
                }
                
                ready = !ready;
                data.sendSignalReadyMessage(ready);
                
                if (ready)
                {
                    btnReady.setText("Cancel Ready");
                    btnUpgradeTeam.setPosition(-5000, -5000);
                }
                else
                {
                    btnReady.setText("Signal Ready");
                    btnUpgradeTeam.setPosition(btnReady.getX() - buttonSpacing, btnReady.getY());
                }
            }
            else if (e.getSource() == btnExit)
            {
                // TODO:  Prompt for confirmation.
                MHGame.setProgramOver(true);
            }
        }


        @Override
        public void render(final Graphics2D g)
        {
            super.fill(g, Color.BLACK);
            super.render(g);
            
            drawTitle("Lobby", g);
            marquee.render(g);
            
            if (allReady || waitingForPlayers)
            {
                btnReady.setVisible(false);
                //btnExit.setVisible(false);
                btnUpgradeTeam.setVisible(false);
                if (MHGame.getGameTimerValue() - lastFlashTime >= MHRuntimeMetrics.secToNano(0.5))
                {
                    flashOn = !flashOn;
                    lastFlashTime = MHGame.getGameTimerValue();
                    
                    if (flashOn)
                    {
                        if (!message.equalsIgnoreCase("PLEASE WAIT"))
                            message = "PLEASE WAIT";
                        else if (waitingForPlayers)
                            message = "AWAITING PLAYERS";
                        else
                            message = "INITIALIZING";
                    }
                }
                if (flashOn)
                {
                    // Display message to let the user know what we're waiting for.
                    centerText(g, message, 560, TLCUI.Fonts.getScreenTitleFont());
                }
            }
            
            drawStatusBar(data.getStatusMessage(), g);
        }

        @Override
        public void keyPressed(final KeyEvent e)
        {
            if (chatClient != null)
                chatClient.keyPressed(e);
        }
}
