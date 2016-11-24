package tlc.ui.command;

import mhframework.gui.MHCommand;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCUpgradeItem;

public class TLCUpgradeCmd implements MHCommand
{
    private TLCUpgradeItem upgrade;
    private TLCCharacter character;
    private TLCTeam team;
    
    public TLCUpgradeCmd(TLCUpgradeItem upgrade, TLCCharacter character, TLCTeam team)
    {
        this.upgrade = upgrade;
        this.character = character;
        this.team = team;
    }

    @Override
    public void execute()
    {
        upgrade.applyUpgrade(character);
        team.adjustCoins(-upgrade.cost());
    }

    public TLCCharacter getTarget()
    {
        return character;
    }

    public TLCUpgradeItem getUpgrade()
    {
        return upgrade;
    }
}
