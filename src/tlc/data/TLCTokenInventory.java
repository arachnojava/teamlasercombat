package tlc.data;

import java.util.ArrayList;
import tlc.data.characters.TLCCharacterType;

public class TLCTokenInventory
{
    private TLCCharacterTypeTokenInventory captainTokens, officerTokens, trooperTokens;

    public TLCTokenInventory()
    {
    }

    
    private TLCCharacterTypeTokenInventory getCaptainTokens()
    {
        if (captainTokens == null)
            captainTokens = new TLCCharacterTypeTokenInventory();
        
        return captainTokens;
    }
    
    
    private TLCCharacterTypeTokenInventory getOfficerTokens()
    {
        if (officerTokens == null)
            officerTokens = new TLCCharacterTypeTokenInventory();
        
        return officerTokens;
    }
    
    
    private TLCCharacterTypeTokenInventory getTrooperTokens()
    {
        if (trooperTokens == null)
            trooperTokens = new TLCCharacterTypeTokenInventory();
        
        return trooperTokens;
    }
    
    
    public ArrayList<TLCCombatTokenStack> getCombatTokens(TLCCharacterType characterType)
    {
        if (characterType.equals(TLCCharacterType.CAPTAIN))
            return getCaptainTokens().getCombatTokens();

        if (characterType.equals(TLCCharacterType.OFFICER))
            return getOfficerTokens().getCombatTokens();

        return getTrooperTokens().getCombatTokens();
    }
    
    
    public void addToken(TLCCharacterType character, TLCToken token)
    {
        if (character.equals(TLCCharacterType.CAPTAIN))
            getCaptainTokens().addToken(token);
        else if (character.equals(TLCCharacterType.OFFICER))
            getOfficerTokens().addToken(token);
        else if (character.equals(TLCCharacterType.TROOPER))
            getTrooperTokens().addToken(token);
    }
    
    public String toString()
    {
        String s = "\n--------\nCAPTAIN TOKENS:";
        if (captainTokens == null)
            s += " None\n";
        else
            s += "\n" + captainTokens.toString();
        
        s += "\n--------\nOFFICER TOKENS:";
        if (officerTokens == null)
            s += " None\n";
        else
            s += "\n" + officerTokens.toString();
        
        
        s += "\n--------\nTROOPER TOKENS:";
        if (trooperTokens == null)
            s += " None\n";
        else
            s += "\n" + trooperTokens.toString();
        
        return s;
    }


    public int getHealTokenCount(TLCCharacterType characterType)
    {
        if (characterType.equals(TLCCharacterType.CAPTAIN))
            return getCaptainTokens().getHealTokenCount();
        if (characterType.equals(TLCCharacterType.OFFICER))
            return getOfficerTokens().getHealTokenCount();

        return getTrooperTokens().getHealTokenCount();
    }


    public int getGrenadeTokenCount(TLCCharacterType characterType)
    {
        if (characterType.equals(TLCCharacterType.TROOPER))
            return getTrooperTokens().getGrenadeTokenCount();
        
        return 0;
    }
    
    
    public void useGrenadeToken()
    {
        TLCTokenData d = new TLCTokenData();
        d.setTokenType(TLCTokenType.GRENADE_TOKEN);
        try
        {
            getTrooperTokens().useToken(new TLCToken(d));
        } catch (Exception e)
        {
        }
    }


    public TLCPowerTokenSet getPowerTokens(TLCCharacterType characterType)
    {
        if (characterType.equals(TLCCharacterType.CAPTAIN))
            return getCaptainTokens().getPowerTokens();
        if (characterType.equals(TLCCharacterType.OFFICER))
            return getOfficerTokens().getPowerTokens();

        return getTrooperTokens().getPowerTokens();
    }


    public int countTokens(TLCCharacterType type)
    {
        int ct = getCombatTokens(type).size();
        int pt = getPowerTokens(type).getCount();
        int gt = getGrenadeTokenCount(type);
        
        return ct + pt + gt;
    }


    public TLCTokenData selectAttackToken(TLCCharacterType type)
    {
        ArrayList<TLCCombatTokenStack> ct = getCombatTokens(type);
        if (ct.size() < 1)
        {
            if (getPowerTokens(type).hasToken(TLCTokenType.PT_BLAZE_OF_GLORY))
            {
                TLCTokenData token = new TLCTokenData();
                token.setTokenType(TLCTokenType.PT_BLAZE_OF_GLORY);
                token.setAttackValue(5);
                token.setDefenseValue(0);
                getPowerTokens(type).removeToken(TLCTokenType.PT_BLAZE_OF_GLORY);
                return token;
            }
            else if (getPowerTokens(type).hasToken(TLCTokenType.PT_DESPERATION))
            {
                TLCTokenData token = new TLCTokenData();
                token.setTokenType(TLCTokenType.PT_DESPERATION);
                token.setAttackValue(4);
                token.setDefenseValue(0);
                getPowerTokens(type).removeToken(TLCTokenType.PT_DESPERATION);
                return token;
            }
            else if (getPowerTokens(type).hasToken(TLCTokenType.PT_LUCKY_SHOT))
            {
                TLCTokenData token = new TLCTokenData();
                token.setTokenType(TLCTokenType.PT_LUCKY_SHOT);
                token.setAttackValue(3);
                token.setDefenseValue(0);
                getPowerTokens(type).removeToken(TLCTokenType.PT_LUCKY_SHOT);
                return token;
            }
            else if (getPowerTokens(type).hasToken(TLCTokenType.PT_VAMPIRE))
            {
                TLCTokenData token = new TLCTokenData();
                token.setTokenType(TLCTokenType.PT_VAMPIRE);
                token.setAttackValue(4);
                token.setDefenseValue(0);
                getPowerTokens(type).removeToken(TLCTokenType.PT_VAMPIRE);
                return token;
            }
            else if (getGrenadeTokenCount(type) > 0)
            {
                TLCTokenData token = new TLCTokenData();
                token.setTokenType(TLCTokenType.GRENADE_TOKEN);
                token.setAttackValue(2);
                token.setDefenseValue(0);
                useGrenadeToken();
                return token;
            }
            else
            {
                TLCTokenData token = new TLCTokenData();
                token.setTokenType(TLCTokenType.COMBAT_TOKEN);
                token.setAttackValue(1);
                token.setDefenseValue(1);
                return token;
            }
        }
        
        int best = 0;
        for (int i = 0; i < ct.size(); i++)
        {
            if (ct.get(i).getAttackValue() > ct.get(best).getAttackValue())
                best = i;
            else if (ct.get(i).getAttackValue() == ct.get(best).getAttackValue())
            {
                if (ct.get(i).getDefenseValue() < ct.get(best).getDefenseValue())
                    best = i;
            }
        }
        
        TLCTokenData token = new TLCTokenData();
        token.setTokenType(TLCTokenType.COMBAT_TOKEN);
        token.setAttackValue(ct.get(best).getAttackValue());
        token.setDefenseValue(ct.get(best).getDefenseValue());
        ct.remove(best);

        return token;
    }
    
    
    public TLCTokenData selectDefenseToken(TLCCharacterType type)
    {
        ArrayList<TLCCombatTokenStack> ct = getCombatTokens(type);
        if (ct.size() < 1)
            return null;
        
        int best = 0;
        for (int i = 0; i < ct.size(); i++)
        {
            if (ct.get(i).getDefenseValue() > ct.get(best).getDefenseValue())
                best = i;
            else if (ct.get(i).getDefenseValue() == ct.get(best).getDefenseValue())
            {
                if (ct.get(i).getAttackValue() < ct.get(best).getAttackValue())
                    best = i;
            }
        }
        
        TLCTokenData token = new TLCTokenData();
        token.setAttackValue(ct.get(best).getAttackValue());
        token.setDefenseValue(ct.get(best).getDefenseValue());
        ct.remove(best);

        return token;
    }
}


class TLCCharacterTypeTokenInventory
{
    private int healthTokenCount, 
                grenadeTokenCount;
    private ArrayList<TLCCombatTokenStack> combatTokens;
    private TLCPowerTokenSet powerTokens;
    
    
    public TLCCharacterTypeTokenInventory()
    {
        healthTokenCount = 0;
        grenadeTokenCount = 0;
        combatTokens = new ArrayList<TLCCombatTokenStack>();
        powerTokens = new TLCPowerTokenSet();
    }

    
    public TLCPowerTokenSet getPowerTokens()
    {
        return powerTokens;
    }


    public int getHealTokenCount()
    {
        return healthTokenCount;
    }
    
    
    public int getGrenadeTokenCount()
    {
        return grenadeTokenCount;
    }
    
    public void addToken(TLCToken token)
    {
        if (token.isPowerToken())
            powerTokens.addToken(token.getTokenType());
        else if (token.isGrenadeToken())
            grenadeTokenCount++;
        else if (token.isHealToken())
            healthTokenCount++;
        else if (token.isCombatToken())
            addCombatToken(token);
    }
    
    
    public void useToken(TLCToken token)
    {
        if (token.isCombatToken())
            removeCombatToken(token);
        else if (token.isPowerToken())
            powerTokens.removeToken(token.getTokenType());
        else if (token.isGrenadeToken())
            grenadeTokenCount--;
//        else if (token.isHealToken())
//            healthTokenCount--;
    }
    
    
    private void addCombatToken(TLCToken token)
    {
        TLCCombatTokenStack stack = findTokenStack(token);
        
        if (stack == null)
        {
            stack = new TLCCombatTokenStack(token);
            if (combatTokens == null)
                combatTokens = new ArrayList<TLCCombatTokenStack>();

            combatTokens.add(stack);
        }
        
        stack.addToken();
    }
    
    
    private void removeCombatToken(TLCToken token)
    {
        TLCCombatTokenStack stack = findTokenStack(token);
        
        if (stack != null)
            stack.removeToken();
        
        if (stack.getCount() < 1)
            combatTokens.remove(stack);
    }
    
    
    public ArrayList<TLCCombatTokenStack> getCombatTokens()
    {
        return combatTokens;
    }
    
    
    TLCCombatTokenStack findTokenStack(TLCToken token)
    {
        for (TLCCombatTokenStack t : combatTokens)
        {
            if (t.getAttackValue() == token.getAttackValue() && 
                t.getDefenseValue() == token.getDefenseValue())
                return t;
        }
        
        return null;
    }
    
    
    @Override
    public String toString()
    {
        String s = "\nHeal Tokens: " + getHealTokenCount();
        s += "\nGrenades: " + getGrenadeTokenCount();
        
        s += "\nCombat Tokens: ";
        for (TLCCombatTokenStack ct : combatTokens)
        {
            s += "\n\t" + ct.toString();
        }
        
        s += "\nPower Tokens: ";
        s += powerTokens.toString();
                
        
        return s;
    }
}

