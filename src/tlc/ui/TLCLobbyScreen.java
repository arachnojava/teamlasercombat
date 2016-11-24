package tlc.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import mhframework.MHDisplayModeChooser;
import mhframework.gui.MHGUIButton;
import mhframework.gui.MHGUIClientListDisplay;
import mhframework.io.net.MHSerializableClientList;
import tlc.net.TLCGameClient;

public class TLCLobbyScreen extends TLCScreenBase
{
        MHGUIClientListDisplay clientListDisplay;
        MHSerializableClientList clientList;
        
        MHGUIButton btnUpgradeTeam, btnExit, btnReady;

        public TLCLobbyScreen()
        {
            chatClient = TLCGameClient.createChatClient(50, 100, 500, 400);
            add(chatClient);

            btnUpgradeTeam = TLCUI.createLargeButton("Upgrade Team");
            btnUpgradeTeam.setPosition(300, 540);
            btnUpgradeTeam.addActionListener(this);
            add(btnUpgradeTeam);
            
            clientListDisplay = new MHGUIClientListDisplay();
            clientListDisplay.setSize(150, 400);
            clientListDisplay.setPosition((int) (MHDisplayModeChooser.getBounds().getWidth() - clientListDisplay.getBounds().getWidth() - 50), 100);
            add(clientListDisplay);
        }


        @Override
        public void load()
        {
            setFinished(false);
        }


        @Override
        public void unload()
        {
            // TODO Auto-generated method stub
        }


        @Override
        public void advance()
        {
            super.advance();

            clientList = TLCGameClient.getClientList();
            clientListDisplay.setClientList(clientList);
        }


        @Override
        public void actionPerformed(final ActionEvent e)
        {
            if (e.getSource() == btnUpgradeTeam)
            {
                setNextScreen(new TLCTeamConfigScreen());
                setFinished(true);
            }
        }


        @Override
        public void render(final Graphics2D g)
        {
            super.fill(g, Color.BLACK);
            super.render(g);
            

            //if (TLCDataFacade.getInstance().isPlayer())
            //    drawTitle("Lobby", g, TLCDataFacade.getInstance().getTeam(TLCGameClient.getClientID()).getColor().getColorValue());
            //else
                drawTitle("Lobby", g);

            drawStatusBar(TLCGameClient.getStatusMessage(), g);
        }

        @Override
        public void keyPressed(final KeyEvent e)
        {
            if (chatClient != null)
                chatClient.keyPressed(e);
        }
}
