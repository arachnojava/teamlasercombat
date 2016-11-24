package tlc.data.characters;

import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;

public class TLCCoinBonus extends TLCUpgradeItem
{
    private static final long serialVersionUID = 1L;


    public TLCCoinBonus()
    {
        super("Coin", 0, 0);
    }


    @Override
    public String getDescription()
    {
        return "Coin";
    }


    @Override
    public String getHelpText()
    {
        return "Found a coin.";
    }


    @Override
    public void applyUpgrade(TLCCharacter character)
    {
        // Get team reference.
        TLCTeam team = TLCDataFacade.getTeam(character.getTeamID());
        
        // Add one coin to team's budget.
        team.adjustCoins(1);
    }


    @Override
    public void undoUpgrade(TLCCharacter character)
    {
    }


    @Override
    public double getEffect()
    {
        return 0;
    }
}
