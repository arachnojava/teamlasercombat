package tlc.net.server.ai;

import java.util.ArrayList;
import mhframework.MHRandom;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.characters.TLCArmorUpgrade;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCCombatTraining;
import tlc.data.characters.TLCEnduranceTraining;
import tlc.data.characters.TLCMovementUpgrade;
import tlc.data.characters.TLCNames;
import tlc.data.characters.TLCWeaponUpgrade;
import tlc.ui.command.TLCRecruitCmd;
import tlc.ui.command.TLCUpgradeCmd;

public class TLCAIShopper
{
    public static void goShopping(long dataID)
    {
        TLCDataFacade data = TLCDataFacade.getInstance(dataID); 
        int teamID = data.getTeamID();
        TLCTeam team = null;
        
        // Wait until the team is ready.  (Necessary before the first round.)
        while (team == null)
        {
            team = TLCDataFacade.getTeam(teamID);
            
            try
            {
                Thread.sleep(250);
            } catch (InterruptedException e)
            {
            }
        }
        
        do
        {
            int budget = team.getCoins();

            // Do we want to upgrade or recruit?
            if (MHRandom.flipCoin() || data.getMemberCount(data.getClientID()) >= 5)
            {
                // Build a list of affordable upgrades.
                ArrayList<TLCUpgradeCmd> list = getUpgrades(budget, data);
                
                // Find out if we can afford anything.
                if (list.size() > 0)
                {
                    // Pick something out and buy it.
                    int item = MHRandom.random(0, list.size()-1);
                    TLCUpgradeCmd cmd = list.get(item);
                    cmd.execute();
                    int cost = cmd.getUpgrade().cost();
                    team.adjustCoins(-cost);
                    //data.sendCharacterUpdateMessage(cmd.getTarget(), true);
                }
                else return;
            }
            else
            {
                // Build a list of affordable characters.
                ArrayList<TLCRecruitCmd> list = getCharacters(budget, data);

                // Find out if we can afford anything, or if we already have enough members.
                if (list.size() > 0)
                {
                    // Pick something out and buy it.
                    int item = MHRandom.random(0, list.size()-1);
                    TLCRecruitCmd cmd = list.get(item);
                    cmd.execute();
                }
            }
            
            // Randomly decide if we want to shop more.
        } while (MHRandom.rollD4() != 1);        
    }
    
    
    private static ArrayList<TLCUpgradeCmd> getUpgrades(int budget, TLCDataFacade data)
    {
        ArrayList<TLCUpgradeCmd> list = new ArrayList<TLCUpgradeCmd>();
        TLCTeam team = TLCDataFacade.getTeam(data.getTeamID());
        
        for (TLCCharacter c : TLCDataFacade.getCharacterList())
        {
            if (c.getTeamID() != data.getTeamID()) continue;
            
        // Weapon
        if (TLCWeaponUpgrade.COST <= budget)
        {
            TLCWeaponUpgrade weapon = new TLCWeaponUpgrade();
            list.add(new TLCUpgradeCmd(weapon, c, team));
        }
        
        // Armor
        if (TLCArmorUpgrade.COST <= budget)
        {
            TLCArmorUpgrade armor = new TLCArmorUpgrade();
            list.add(new TLCUpgradeCmd(armor, c, team));
        }
        
        // Boots
        if (TLCMovementUpgrade.COST <= budget)
        {
            TLCMovementUpgrade boots = new TLCMovementUpgrade();
            list.add(new TLCUpgradeCmd(boots, c, team));
        }
        
        // Combat
        if (TLCCombatTraining.COST <= budget)
        {
            TLCCombatTraining combatTraining = new TLCCombatTraining();
            list.add(new TLCUpgradeCmd(combatTraining, c, team));
        }
        
        // Endurance
        if (TLCEnduranceTraining.COST <= budget)
        {
            TLCEnduranceTraining enduranceTraining = new TLCEnduranceTraining();
            list.add(new TLCUpgradeCmd(enduranceTraining, c, team));
        }
        }
        
        return list;
    }
    
    private static ArrayList<TLCRecruitCmd> getCharacters(int budget, TLCDataFacade data)
    {
        ArrayList<TLCRecruitCmd> list = new ArrayList<TLCRecruitCmd>();
        TLCCharacter character;
        
        if (TLCCharacterType.TROOPER.getCost() <= budget)
        {
            character = new TLCCharacter(TLCCharacterType.TROOPER, TLCCharacterGender.MALE);
            character.setName(TLCNames.boyName());
            list.add(new TLCRecruitCmd(character, null, data));
            
            character = new TLCCharacter(TLCCharacterType.TROOPER, TLCCharacterGender.FEMALE);
            character.setName(TLCNames.girlName());
            list.add(new TLCRecruitCmd(character, null, data));
        }
        
        if (TLCCharacterType.OFFICER.getCost() <= budget)
        {
            character = new TLCCharacter(TLCCharacterType.OFFICER, TLCCharacterGender.MALE);
            character.setName(TLCNames.boyName());
            list.add(new TLCRecruitCmd(character, null, data));
            
            character = new TLCCharacter(TLCCharacterType.OFFICER, TLCCharacterGender.FEMALE);
            character.setName(TLCNames.girlName());
            list.add(new TLCRecruitCmd(character, null, data));
        }
        
        return list;
    }
}
