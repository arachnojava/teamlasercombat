package tlc.ui.screens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import mhframework.MHDisplayModeChooser;
import mhframework.MHPoint;
import mhframework.MHScreen;
import mhframework.gui.MHGUIButton;
import tlc.TLCMain;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;
import tlc.net.TLCCombatInteractionMessage;
import tlc.ui.TLCUI;

public class TLCCombatSummaryScreen extends TLCScreenBase
{
    private static TLCCombatSummaryScreen INSTANCE = null;
    private static TLCCombatInteractionMessage summary;

    private static final int DIALOG_WIDTH = 700;
    private static final int DIALOG_HEIGHT = 500;
    private Color bgColor = new Color(0, 0, 0, 150);
    private int dialogX, dialogY;
    private MHGUIButton btnClose, btnDontShow;
    private static String attackerName,
                          attackerType,
                          attackerTeam, 
                          attackerPT[], 
                          defenderName, 
                          defenderType,
                          defenderTeam, 
                          defenderPT[];
    
    
    private TLCCombatSummaryScreen()
    {
        btnClose = TLCUI.createLargeButton("Close");
        btnClose.addActionListener(this);
        add(btnClose);

//        btnDontShow = TLCUI.createLargeButton("Don't Show This");
//        btnDontShow.addActionListener(this);
//        add(btnDontShow);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnClose)
            setFinished(true);
        else if (e.getSource() == btnDontShow)
        {
            TLCDataFacade.setCombatResultsOn(false);
            setFinished(true);
        }

        setNextScreen(null);
    }

    
    @Override
    public void load()
    {
        setFinished(false);
        dialogX = MHDisplayModeChooser.getCenterX() - DIALOG_WIDTH/2;
        dialogY = MHDisplayModeChooser.getCenterY() - DIALOG_HEIGHT/2;
        
        int cy = dialogY+DIALOG_HEIGHT-btnClose.getHeight()/2;
        btnClose.setPosition(dialogX+DIALOG_WIDTH-btnClose.getWidth(), cy);
//        btnDontShow.setPosition(btnClose.getX()-btnClose.getWidth(), cy);
    }


    @Override
    public void unload()
    {
    }


    public static MHScreen getInstance(TLCCombatInteractionMessage combatSummary)
    {
        summary = combatSummary;
        
        TLCCharacter attacker = TLCDataFacade.getCharacterList().get(summary.attackerID);
        TLCTeam attackingTeam = TLCDataFacade.getTeam(attacker.getTeamID());
        TLCCharacter defender = TLCDataFacade.getCharacterList().get(summary.defenderID);
        TLCTeam defendingTeam = TLCDataFacade.getTeam(defender.getTeamID());
        
        TLCCombatSummaryScreen.attackerName = attacker.getName();
        TLCCombatSummaryScreen.attackerType = attacker.getType().getTitle();
        TLCCombatSummaryScreen.attackerTeam = attackingTeam.getTeamName();
        TLCCombatSummaryScreen.defenderName = defender.getName();
        TLCCombatSummaryScreen.defenderTeam = defendingTeam.getTeamName();
        TLCCombatSummaryScreen.defenderType = defender.getType().getTitle();
        
        String desc;
        if (summary.attackToken == null)
            desc = "Attacker: <ERROR>";
        else
            desc = "Attacker: " + summary.attackToken.getTokenType().getName() + " - "
                       + summary.attackToken.getTokenType().getDescription();
        
        attackerPT = TLCUI.Fonts.BUTTON_12.splitLines(desc, 600);
        
        if (summary.defendToken == null)
            desc = "Defender: No token played.";
        else
            desc = "Defender: " + summary.defendToken.getTokenType().getName() + " - "
                       + summary.defendToken.getTokenType().getDescription();

        defenderPT = TLCUI.Fonts.BUTTON_12.splitLines(desc, 600);
        
        if (INSTANCE == null)
            INSTANCE = new TLCCombatSummaryScreen();
        
        return INSTANCE;
    }


    @Override
    public void render(Graphics2D g)
    {
        this.getPreviousScreen().render(g);
        super.fill(g, bgColor);
        
        // Dialog box background
        g.setColor(Color.DARK_GRAY);
        g.fill3DRect(dialogX, dialogY, DIALOG_WIDTH, DIALOG_HEIGHT, true);
        
        // Attacker label
        String s = "Attacker Info";
        g.setColor(Color.BLACK);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+6, dialogY+30);
        g.setColor(Color.WHITE);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+5, dialogY+29);

        // Attacker area
        g.setColor(Color.BLACK);
        g.fillRect(dialogX+5, dialogY+35, DIALOG_WIDTH-10, 80);
        g.setColor(Color.LIGHT_GRAY);
        g.draw3DRect(dialogX+5, dialogY+35, DIALOG_WIDTH-10, 80, false);
        
        // Attacking unit
        g.setColor(Color.WHITE);
        TLCUI.Fonts.getHelpFont().drawString(g, attackerName, dialogX+10, dialogY+60);
        TLCUI.Fonts.BUTTON_12.drawString(g, attackerType, dialogX+10, dialogY+80);
        TLCUI.Fonts.BUTTON_12.drawString(g, attackerTeam, dialogX+10, dialogY+100);
        
        // Attack factor labels
        g.setColor(Color.WHITE);
        int y = dialogY+48;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Token", dialogX+200, y);
        TLCUI.Fonts.BUTTON_12.drawString(g, "Weapon", dialogX+350, y);
        TLCUI.Fonts.BUTTON_12.drawString(g, "Training", dialogX+500, y);
        TLCUI.Fonts.BUTTON_12.drawString(g, "TOTAL", dialogX+650, y);
        
        // Attacker data
        y += 50;
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.attackTokenValue, dialogX+200+5, y);
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.weaponFactor, dialogX+350+5, y);
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.attackerTrainingFactor, dialogX+500+5, y);
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.attackResult, dialogX+650+5, y);
        
        // Defender label
        s = "Defender Info";
        g.setColor(Color.BLACK);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+6, dialogY+150);
        g.setColor(Color.WHITE);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+5, dialogY+149);

        // Defender area
        g.setColor(Color.BLACK);
        g.fillRect(dialogX+5, dialogY+155, DIALOG_WIDTH-10, 80);
        g.setColor(Color.LIGHT_GRAY);
        g.draw3DRect(dialogX+5, dialogY+155, DIALOG_WIDTH-10, 80, false);
        
        // Defending unit
        g.setColor(Color.WHITE);
        TLCUI.Fonts.getHelpFont().drawString(g, defenderName, dialogX+10, dialogY+180);
        TLCUI.Fonts.BUTTON_12.drawString(g, defenderType, dialogX+10, dialogY+200);
        TLCUI.Fonts.BUTTON_12.drawString(g, defenderTeam, dialogX+10, dialogY+220);
        
        // Defense factor labels
        g.setColor(Color.WHITE);
        y = dialogY+167;
        TLCUI.Fonts.BUTTON_12.drawString(g, "Token", dialogX+200, y);
        TLCUI.Fonts.BUTTON_12.drawString(g, "Armor", dialogX+350, y);
        TLCUI.Fonts.BUTTON_12.drawString(g, "Training", dialogX+500, y);
        TLCUI.Fonts.BUTTON_12.drawString(g, "TOTAL", dialogX+650, y);
        
        // Defender data
        y += 50;
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.defenseTokenValue, dialogX+200+5, y);
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.armorFactor, dialogX+350+5, y);
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.defenderTrainingFactor, dialogX+500+5, y);
        TLCUI.Fonts.getDataFont().drawString(g, ""+summary.defenseResult, dialogX+650+5, y);
        
        // Results label
        s = "Result Summary";
        g.setColor(Color.BLACK);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+6, dialogY+270);
        g.setColor(Color.WHITE);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+5, dialogY+269);
        
        // Results area
        Rectangle2D r = new Rectangle2D.Double(dialogX+5, dialogY+275, DIALOG_WIDTH-10, 40);
        g.setColor(Color.BLACK);
        g.fillRect(dialogX+5, dialogY+275, DIALOG_WIDTH-10, 40);
        g.setColor(Color.LIGHT_GRAY);
        g.draw3DRect(dialogX+5, dialogY+275, DIALOG_WIDTH-10, 40, false);
        
        String strResults;
        if (summary.attackResult > summary.defenseResult)
        {
            int dmg = summary.attackResult - summary.defenseResult;
            strResults = defenderName + " took " + dmg + " damage.";
        }
        else
            strResults = defenderName + " took no damage.";
       
        MHPoint p = TLCUI.Fonts.getHelpFont().centerOn(r, g, strResults);
        TLCUI.Fonts.getHelpFont().drawString(g, strResults, p.getX(), p.getY());

        // Tokens label
        s = "Tokens Played";
        g.setColor(Color.BLACK);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+6, dialogY+350);
        g.setColor(Color.WHITE);
        TLCUI.Fonts.getDialogTitleFont().drawString(g, s, dialogX+5, dialogY+349);
        
        // Tokens area
        g.setColor(Color.BLACK);
        g.fillRect(dialogX+5, dialogY+355, DIALOG_WIDTH-10, 90);
        g.setColor(Color.LIGHT_GRAY);
        g.draw3DRect(dialogX+5, dialogY+355, DIALOG_WIDTH-10, 90, false);

        y = dialogY+370;
        for (int i = 0; i < attackerPT.length; i++)
        {
            TLCUI.Fonts.BUTTON_12.drawString(g, attackerPT[i], dialogX+20, y);
            y += 15;
        }
        y += 15;
        
        for (int i = 0; i < defenderPT.length; i++)
        {
            TLCUI.Fonts.BUTTON_12.drawString(g, defenderPT[i], dialogX+20, y);
            y += 15;
        }
        
        super.render(g);
        super.drawTitle("COMBAT SUMMARY", g);
    }


    @Override
    public void keyReleased(KeyEvent e)
    {
        setFinished(true);
        setNextScreen(null);
    }


    @Override
    public void mouseReleased(MouseEvent e)
    {
        setFinished(true);
        setNextScreen(null);
    }

}
