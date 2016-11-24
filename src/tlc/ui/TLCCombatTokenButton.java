package tlc.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import mhframework.gui.MHGUIButton;
import mhframework.media.MHFont;
import tlc.data.TLCCombatTokenStack;

public class TLCCombatTokenButton extends MHGUIButton
{
    private TLCCombatTokenStack token;
    private MHFont font = TLCUI.Fonts.getHelpFont();

    public TLCCombatTokenButton(TLCCombatTokenStack token)
    {
        super(TLCUI.Images.BUTTON_COMBAT_TOKEN, TLCUI.Images.BUTTON_COMBAT_TOKEN, TLCUI.Images.BUTTON_COMBAT_TOKEN);
        this.token = token;
    }

    
    public TLCCombatTokenStack getTokenStack()
    {
        return token;
    }
    
 
    public void render(Graphics2D g)
    {
        super.render(g);

        int textY = getY() + 39;
        
        g.setColor(Color.WHITE);
        font.drawString(g, ""+token.getAttackValue(), getX()+13, textY+1);
        g.setColor(Color.RED);
        font.drawString(g, ""+token.getAttackValue(), getX()+12, textY);

        g.setColor(Color.LIGHT_GRAY);
        font.drawString(g, "/", getX()+28, textY);

        g.setColor(Color.WHITE);
        font.drawString(g, ""+token.getDefenseValue(), getX()+41, textY+1);
        g.setColor(Color.BLUE);
        font.drawString(g, ""+token.getDefenseValue(), getX()+40, textY);
        
        g.setColor(Color.BLACK);
        font.drawString(g, "x"+token.getCount(), getX()+44, getY()+64);
        font.drawString(g, "x"+token.getCount(), getX()+46, getY()+66);
        g.setColor(Color.WHITE);
        font.drawString(g, "x"+token.getCount(), getX()+45, getY()+65);
    }
}