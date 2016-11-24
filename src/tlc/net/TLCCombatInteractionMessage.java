package tlc.net;

import java.io.Serializable;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.TLCTokenData;
import tlc.data.characters.TLCCharacter;

@SuppressWarnings("serial")
public class TLCCombatInteractionMessage implements Serializable
{
    // Inputs
    public int attackerID;
    public TLCTokenData attackToken;
    public int defenderID;
    public TLCTokenData defendToken;
    
    // Outputs
    public int attackResult;
    public int attackerTrainingFactor;
    public int attackerCharDynamic;
    public int attackTokenValue;
    public int weaponFactor;
    public int defenseResult;
    public int defenderTrainingFactor;
    public int defenderCharDynamic;
    public int defenseTokenValue;
    public int armorFactor;
    
    
    @Override
    public String toString()
    {
        TLCCharacter attacker = TLCDataFacade.getCharacterList().get(attackerID);
        TLCTeam attackingTeam = TLCDataFacade.getTeam(attacker.getTeamID());
        TLCCharacter defender = TLCDataFacade.getCharacterList().get(defenderID);
        TLCTeam defendingTeam = TLCDataFacade.getTeam(defender.getTeamID());
        
        String s =  "\nATTACKER:";
               s += "\n\t" + attacker.getName();
               s += "\n\t" + attackingTeam.getTeamName();
               s += "\nDEFENDER:";
               s += "\n\t" + defender.getName();
               s += "\n\t" + defendingTeam.getTeamName();
               s += "\nATTACK FACTORS:";
               s += "\n\tToken Value: " + attackTokenValue;
               s += "\n\t  Equipment: " + weaponFactor;
               s += "\n\t   Training: " + attackerTrainingFactor;
               s += "\n\tTOTAL ATTACK VALUE: " + attackResult;
               s += "\nDEFENSE FACTORS:";
               s += "\n\tToken Value: " + defenseTokenValue;
               s += "\n\t  Equipment: " + armorFactor;
               s += "\n\t   Training: " + defenderTrainingFactor;
               s += "\n\tTOTAL DEFENSE VALUE: " + defenseResult;

        return s;
    }
}
