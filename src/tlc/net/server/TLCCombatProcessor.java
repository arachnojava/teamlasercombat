package tlc.net.server;

import java.util.ArrayList;
import mhframework.MHActor;
import mhframework.io.net.MHNetworkMessage;
import mhframework.tilemap.MHMapCell;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHTileMapDirection;
import tlc.data.TLCCharacterList;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTokenType;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.net.TLCCombatInteractionMessage;
import tlc.net.TLCMessageType;

public abstract class TLCCombatProcessor
{
    // Implement game rules to resolve combat.
    public static void resolveCombat(TLCCombatInteractionMessage combatData)
    {
        // Figure out what kinds of tokens have been played and delegate.

        TLCCharacter attacker = TLCDataFacade.getCharacterList().get(combatData.attackerID);
        TLCCharacter defender = TLCDataFacade.getCharacterList().get(combatData.defenderID);

        // Compute all factors for AT and DF and store them in results.
        computeAT(attacker, defender, combatData);
        computeDF(attacker, defender, combatData);
        
        // Update data model with effects of combat.
        if (combatData.attackResult > combatData.defenseResult)
        {
            int damage = combatData.attackResult - combatData.defenseResult;
            defender.setHealth(defender.getHealth() - damage);
        }
        
        // Send combat results summary to all players.
        MHNetworkMessage msg = new MHNetworkMessage();
        msg.setMessageType(TLCMessageType.COMBAT_RESULTS);
        msg.setPayload(combatData); 
        TLCGameServer.server.sendToAll(msg);
    }
    

    // Attack = token attack value + gender factor + weapon factor + char. dynamic
    private static void computeAT(TLCCharacter attacker, TLCCharacter defender, TLCCombatInteractionMessage combatData)
    {
        if (combatData.attackToken == null)
        {
            combatData.attackResult 
            = combatData.attackTokenValue 
            = combatData.attackerTrainingFactor
            = combatData.weaponFactor
            = combatData.attackerCharDynamic = 0;
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.COMBAT_TOKEN))
        {
            combatData.attackTokenValue = combatData.attackToken.getAttackValue();
            combatData.attackerTrainingFactor = computeAttackerTrainingFactor(attacker);
            combatData.weaponFactor = computeWeaponFactor(attacker);

            if (attacker.getType().equals(TLCCharacterType.TROOPER) && defender.getType().equals(TLCCharacterType.CAPTAIN))
                combatData.attackerCharDynamic = 1;
            else
                combatData.attackerCharDynamic = 0;

            combatData.attackResult = combatData.attackTokenValue 
                    + combatData.attackerTrainingFactor
                    + combatData.weaponFactor
                    + combatData.attackerCharDynamic;
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.GRENADE_TOKEN))
        {
            // Damage the defender and all objects within 2m radius.
            MHMapCellAddress start = defender.getMapLocation();
            start = TLCDataFacade.getGameWorld().getMap().tileWalk(start, MHTileMapDirection.NORTH);
            start = TLCDataFacade.getGameWorld().getMap().tileWalk(start, MHTileMapDirection.NORTH);
            
            for (int row = start.row; row < start.row + 4; row++)
            {
                for (int col = start.column; col < start.column + 4; col++)
                {
                    MHActor target = TLCDataFacade.getGameWorld().getMapCell(row, col).getLayer(MHMapCell.WALL_LAYER); 
                    if (target != null)
                    {
                        if (target instanceof TLCCharacter)
                        {
                            TLCCharacter c = (TLCCharacter) target;
                            c.setHealth(c.getHealth() - 2);
                        }
                        // TODO: Destroy any containers within radius.
                        //else if (target instanceof TLCContainer)
                    }
                }
            }
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_VAMPIRE))
        {
            // Vampire: If the attack does damage, the attacker heals
            // one hit point per unit of damage dealt.
            combatData.attackTokenValue = combatData.attackToken.getAttackValue();
            combatData.attackerTrainingFactor = computeAttackerTrainingFactor(attacker);
            combatData.weaponFactor = computeWeaponFactor(attacker);

            if (attacker.getType().equals(TLCCharacterType.TROOPER) && defender.getType().equals(TLCCharacterType.CAPTAIN))
                combatData.attackerCharDynamic = 1;
            else
                combatData.attackerCharDynamic = 0;

            combatData.attackResult = combatData.attackTokenValue 
                    + combatData.attackerTrainingFactor
                    + combatData.weaponFactor
                    + combatData.attackerCharDynamic;
            computeDF(attacker, defender, combatData);

            if (combatData.attackResult > combatData.defenseResult)
            {
                attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth()+(combatData.attackResult - combatData.defenseResult)));
            }
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_LUCKY_SHOT))
        {
            // Lucky Shot: If the attack does damage, the damage
            // amount is doubled.
            combatData.attackTokenValue = combatData.attackToken.getAttackValue();
            combatData.attackerTrainingFactor = computeAttackerTrainingFactor(attacker);
            combatData.weaponFactor = computeWeaponFactor(attacker);

            if (attacker.getType().equals(TLCCharacterType.TROOPER) && defender.getType().equals(TLCCharacterType.CAPTAIN))
                combatData.attackerCharDynamic = 1;
            else
                combatData.attackerCharDynamic = 0;

            combatData.attackResult = combatData.attackTokenValue 
                    + combatData.attackerTrainingFactor
                    + combatData.weaponFactor
                    + combatData.attackerCharDynamic;
            computeDF(attacker, defender, combatData);

            if (combatData.attackResult > combatData.defenseResult)
            {
                combatData.attackResult += (combatData.attackResult - combatData.defenseResult);
            }
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_DESPERATION))
        {
            // Desperation: AT is normally 4, but if unit is only
            // remaining unit on team, then AT is 10.
            if (countActiveTeamMembers(attacker) > 1)
                combatData.attackTokenValue = 4;
            else
                combatData.attackTokenValue = 10;
                
            combatData.attackerTrainingFactor = computeAttackerTrainingFactor(attacker);
            combatData.weaponFactor = computeWeaponFactor(attacker);

            if (attacker.getType().equals(TLCCharacterType.TROOPER) && defender.getType().equals(TLCCharacterType.CAPTAIN))
                combatData.attackerCharDynamic = 1;
            else
                combatData.attackerCharDynamic = 0;

            combatData.attackResult = combatData.attackTokenValue 
                    + combatData.attackerTrainingFactor
                    + combatData.weaponFactor
                    + combatData.attackerCharDynamic;
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_BLAZE_OF_GLORY))
        {
            // Blaze of Glory: If the attack does damage, the damage
            // amount is doubled, but if the attack is blocked, the 
            // attacker takes 5 points of damage.
            combatData.attackTokenValue = combatData.attackToken.getAttackValue();
            combatData.attackerTrainingFactor = computeAttackerTrainingFactor(attacker);
            combatData.weaponFactor = computeWeaponFactor(attacker);

            if (attacker.getType().equals(TLCCharacterType.TROOPER) && defender.getType().equals(TLCCharacterType.CAPTAIN))
                combatData.attackerCharDynamic = 1;
            else
                combatData.attackerCharDynamic = 0;

            combatData.attackResult = combatData.attackTokenValue 
                    + combatData.attackerTrainingFactor
                    + combatData.weaponFactor
                    + combatData.attackerCharDynamic;
            computeDF(attacker, defender, combatData);

            if (combatData.attackResult > combatData.defenseResult)
            {
                combatData.attackResult += (combatData.attackResult - combatData.defenseResult);
            }
            else
            {
                attacker.setHealth(Math.max(0, attacker.getHealth()-5));
            }
        }
    }
    
    
    private static int countActiveTeamMembers(TLCCharacter attacker)
    {
        TLCCharacterList units = TLCDataFacade.getCharacterList().getTeamMembers(attacker.getTeamID());
        int count = 0;
        for (TLCCharacter c : units)
        {
            if (c.getHealth() > 0)
                count++;
        }
        
        return count;
    }


    private static int computeWeaponFactor(TLCCharacter character)
    {
        int weaponFactor = 0;
        double av = character.getAttackValue();
        
        while (av >= 1.0)
        {
            weaponFactor++;
            av -= 1.0;
        }

        if (Math.random() <= av)
            weaponFactor++; 
        
        return weaponFactor;
    }


    private static int computeAttackerTrainingFactor(TLCCharacter character)
    {
        if (character.getGender().equals(TLCCharacterGender.MALE))
            return 0;
        
        int genderFactor = 0;
        double training = character.getTrainingLevel();
        
        while (training >= 1.0)
        {
            genderFactor++;
            training -= 1.0;
        }

        if (Math.random() <= training)
            genderFactor++; 
        
        return genderFactor;
    }
    

    // Defense = token defense value + gender factor + armor factor + char. dynamic
    private static void computeDF(TLCCharacter attacker, TLCCharacter defender,
            TLCCombatInteractionMessage combatData)
    {
        if (combatData.defendToken == null)
        {
            combatData.defenseResult 
            = combatData.defenseTokenValue 
            = combatData.defenderTrainingFactor
            = combatData.armorFactor
            = combatData.defenderCharDynamic = 0;
            return;
        }
        else if (combatData.defendToken.getTokenType() == null)
            combatData.defendToken.setTokenType(TLCTokenType.COMBAT_TOKEN);
        
        if (combatData.defendToken.getTokenType().equals(TLCTokenType.COMBAT_TOKEN))
        {                
            combatData.defenseTokenValue = combatData.defendToken.getDefenseValue();
            combatData.defenderTrainingFactor = computeDefenderTrainingFactor(defender);
            combatData.armorFactor = computeArmorFactor(defender);

            if (attacker.getType().equals(TLCCharacterType.CAPTAIN) && defender.getType().equals(TLCCharacterType.TROOPER))
                combatData.defenderCharDynamic = 1;
            else
                combatData.defenderCharDynamic = 0;

            combatData.defenseResult = combatData.defenseTokenValue 
                    + combatData.defenderTrainingFactor
                    + combatData.armorFactor
                    + combatData.defenderCharDynamic;
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.GRENADE_TOKEN))
        {
            // The rules state that grenade tokens cannot be countered.
            combatData.defenseResult 
            = combatData.defenseTokenValue 
            = combatData.defenderTrainingFactor
            = combatData.armorFactor
            = combatData.defenderCharDynamic = 0;
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_REFLECTION))
        {
            // Reflection: Any attack, regardless of power, is
            // reflected back at the attacker. The defender takes no
            // damage.
            combatData.attackResult = 0;
            combatData.defenseResult = 99;
            attacker.setHealth(attacker.getHealth() - combatData.attackTokenValue);
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_RESOLVE))
        {
            // Resolve: Defense value goes up as the character's 
            // health goes down. DF = maxHP - currentHP
            combatData.defenseTokenValue = defender.getMaxHealth() - defender.getHealth();
            combatData.defenderTrainingFactor = computeDefenderTrainingFactor(defender);
            combatData.armorFactor = computeArmorFactor(defender);

            if (attacker.getType().equals(TLCCharacterType.CAPTAIN) && defender.getType().equals(TLCCharacterType.TROOPER))
                combatData.defenderCharDynamic = 1;
            else
                combatData.defenderCharDynamic = 0;

            combatData.defenseResult = combatData.defenseTokenValue 
                    + combatData.defenderTrainingFactor
                    + combatData.armorFactor
                    + combatData.defenderCharDynamic;
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_LUCKY_BREAK))
        {
            // Lucky Break: If the attack is blocked, any excess
            // defense points are converted to health points for your
            // unit.
            combatData.defenseTokenValue = combatData.defendToken.getDefenseValue();
            combatData.defenderTrainingFactor = computeDefenderTrainingFactor(defender);
            combatData.armorFactor = computeArmorFactor(defender);

            if (attacker.getType().equals(TLCCharacterType.CAPTAIN) && defender.getType().equals(TLCCharacterType.TROOPER))
                combatData.defenderCharDynamic = 1;
            else
                combatData.defenderCharDynamic = 0;

            combatData.defenseResult = combatData.defenseTokenValue 
                    + combatData.defenderTrainingFactor
                    + combatData.armorFactor
                    + combatData.defenderCharDynamic;

            if (combatData.attackResult > combatData.defenseResult)
            {
                int health = combatData.attackResult - combatData.defenseResult;
                defender.setHealth(defender.getHealth() + health);
            }
        }
        else if (combatData.attackToken.getTokenType().equals(TLCTokenType.PT_SHATTER_AND_SCATTER))
        {
            // Shatter and Scatter: If the attack is blocked, each 
            // member of the attacking team takes two points of 
            // damage.
            combatData.defenseTokenValue = combatData.defendToken.getDefenseValue();
            combatData.defenderTrainingFactor = computeDefenderTrainingFactor(defender);
            combatData.armorFactor = computeArmorFactor(defender);

            if (attacker.getType().equals(TLCCharacterType.CAPTAIN) && defender.getType().equals(TLCCharacterType.TROOPER))
                combatData.defenderCharDynamic = 1;
            else
                combatData.defenderCharDynamic = 0;

            combatData.defenseResult = combatData.defenseTokenValue 
                    + combatData.defenderTrainingFactor
                    + combatData.armorFactor
                    + combatData.defenderCharDynamic;

            if (combatData.attackResult > combatData.defenseResult)
            {
                TLCCharacterList t = TLCDataFacade.getCharacterList().getTeamMembers(combatData.attackerID);
                for (TLCCharacter c : t)
                {
                    c.setHealth(c.getHealth() - 2);
                }
            }
        }
    }

    
    private static int computeDefenderTrainingFactor(TLCCharacter character)
    {
        if (character.getGender().equals(TLCCharacterGender.FEMALE))
            return 0;
        
        int genderFactor = 0;
        double training = character.getTrainingLevel();
        
        while (training >= 1.0)
        {
            genderFactor++;
            training -= 1.0;
        }

        if (Math.random() <= training)
            genderFactor++; 
        
        return genderFactor;
    }

    
    private static int computeArmorFactor(TLCCharacter character)
    {
        int armorFactor = 0;
        double av = character.getDefenseValue();
        
        while (av >= 1.0)
        {
            armorFactor++;
            av -= 1.0;
        }

        if (Math.random() <= av)
            armorFactor++; 
        
        return armorFactor;
    }
    
} // class
