package tlc.data;

import java.io.Serializable;
import java.util.ArrayList;
import mhframework.io.MHSerializable;
import tlc.TLCMain;
import tlc.data.characters.TLCArmorUpgrade;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCCombatTraining;
import tlc.data.characters.TLCEnduranceTraining;
import tlc.data.characters.TLCMovementUpgrade;
import tlc.data.characters.TLCUpgradeItem;
import tlc.data.characters.TLCWeaponUpgrade;

public class TLCTeam implements MHSerializable
{
    /**
     *
     */
    private static final long serialVersionUID = -2764726534558477914L;
    public static final int STARTING_BUDGET = 16;
    public static final int MAX_CHARACTERS = 10;  // Future enhancement:  Let players set the limit.

    private String teamName;
    private final TLCTeamColor color;
    private int coins;
    private int teamID = -1;


    public TLCTeam(final TLCTeamColor color, final int id)
    {
        this.color = color;
        this.teamID = id;
        this.coins = STARTING_BUDGET;
    }


    public void setTeamName(final String n)
    {
        teamName = n;
    }


    public String getTeamName()
    {
        return teamName;
    }



    public TLCTeamColor getColor()
    {
        return color;
    }


    public int getCoins()
    {
        return coins;
    }


    public void adjustCoins(final int change)
    {
        coins += change;

        if (coins < 0)
            coins = 0;
    }

    @Override
    public String toString()
    {
        String s = "Team Name:  " + (getTeamName() == null ? "No name assigned yet." : getTeamName());
        //s += "\nPlayer Name:  " + (getPlayerName() == null ? "TLC Player" : getPlayerName());
        s += "\nTeam Color:  " + getColor().getName();
        s += "\nTeam ID:  " + getID();
        s += "\nCoins:  " + getCoins();
        s += "\nMembers:  \n";

        final TLCCharacterList teamMembers = TLCDataFacade.getCharacterList().getTeamMembers(getID());

        if (teamMembers.size() <= 0)
            s += "\tNo members recruited yet.\n\n";
        else
        {
            s += "MEMBER\t\t\t\tHP\tTL\tAT\tDF\tMV\t\n";
            for (final TLCCharacter c : teamMembers)
            {
                s += format(c.getType().toString() + " " + c.getName()) + " (" + c.getGender().getName() + ")\t";
                s += "\t" + c.getMaxHealth();
                s += "\t" + c.getTrainingLevel();
                s += "\t" + c.getAttackValue();
                s += "\t" + c.getDefenseValue();
                s += "\t" + c.getMovementValue() + "\n";
            }
        }

        return s;
    }

    private String format(final String s)
    {
        if (s.length() > 20)
            return s.substring(0, 19);

        return s;
    }


    public int getID()
    {
        return teamID ;
    }


    public void setID(final int id)
    {
        teamID = id;
    }


    @Override
    public Serializable getSerializableVersion()
    {
        return this;
    }


    public void setCoins(int startingBudget)
    {
        coins = startingBudget;
    }


    /****************************************************************
     * Sells off all of a character's upgrades and then removes the
     * character from the game.
     * 
     * @param character
     */
    public void retireCharacter(TLCCharacter character)
    {
        // Weapon
        TLCUpgradeItem upgrade = new TLCWeaponUpgrade();
        while (character.getAttackValue() > upgrade.getEffect())
        {
            upgrade.undoUpgrade(character);
            adjustCoins(upgrade.sellValue());
        }
        
        // Armor
        upgrade = new TLCArmorUpgrade();
        while (character.getDefenseValue() > upgrade.getEffect())
        {
            upgrade.undoUpgrade(character);
            adjustCoins(upgrade.sellValue());
        }

        // Boots
        upgrade = new TLCMovementUpgrade();
        while (character.getMovementValue() > upgrade.getEffect() + character.getType().getDefaultMovementBonus())
        {
            upgrade.undoUpgrade(character);
            adjustCoins(upgrade.sellValue());
        }

        // Combat training
        upgrade = new TLCCombatTraining();
        while (character.getTrainingLevel() > upgrade.getEffect())
        {
            upgrade.undoUpgrade(character);
            adjustCoins(upgrade.sellValue());
        }

        // Endurance training
        upgrade = new TLCEnduranceTraining();
        while (character.getMaxHealth() > character.getType().getDefaultHP())
        {
            upgrade.undoUpgrade(character);
            adjustCoins(upgrade.sellValue());
        }
        
        adjustCoins(character.getType().getCost()/2);
        TLCDataFacade.getCharacterList().remove(character);
    }


    public boolean hasCharacterType(TLCCharacterType characterType)
    {
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
        {
            if (c.getTeamID() == getID() && c.getType().equals(characterType))
                return true;
        }
        
        return false;
    }
}
