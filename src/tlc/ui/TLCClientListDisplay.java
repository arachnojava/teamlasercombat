package tlc.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import mhframework.gui.MHGUIClientListDisplay;
import mhframework.io.net.MHSerializableClientInfo;
import mhframework.io.net.MHSerializableClientList;
import mhframework.media.MHFont;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.net.server.TLCPlayerDescriptor;

public class TLCClientListDisplay extends MHGUIClientListDisplay
{
    private MHFont font, readyFont;
    private MHSerializableClientList data;
    
    public TLCClientListDisplay()
    {
        super();
        font = new MHFont("Monospaced", Font.PLAIN, 12);
        readyFont = new MHFont("Monospaced", Font.BOLD, 16);
    }
    
    
    public void drawClientList(Graphics2D g)
    {
        final int spacing = 5;
        final int stroke = 2;
        int boxHeight = font.getHeight() + spacing + stroke;
        int y = getY() + boxHeight;
        data = getClientList();
        if (data != null)
        {
            for (final MHSerializableClientInfo client : data)
            {
                if (TLCDataFacade.getInstance(TLCMain.DATA_ID).isPlayer(client.id))
                {
                    g.setColor(client.color);
                    g.setStroke(new BasicStroke(stroke));
                    g.drawRect(getX(), y-boxHeight, getWidth(), boxHeight-stroke*2);
                }
                g.setColor(Color.LIGHT_GRAY);
                font.drawString(g, client.name, getX()+15, y-stroke-4);

                // If player has signaled ready, show green +.
                TLCPlayerDescriptor user = TLCDataFacade.getInstance(TLCMain.DATA_ID).getUser(client.id); 
                if (user != null && user.ready)
                {
                    g.setColor(Color.GREEN);
                    readyFont.drawString(g, "+", getX()+4, y-stroke-4);
                }
                else
                {
                    g.setColor(Color.RED);
                    readyFont.drawString(g, "-", getX()+4, y-stroke-4);
                }
                
                
                y += font.getHeight() + spacing;
            }
        }
    }

}
