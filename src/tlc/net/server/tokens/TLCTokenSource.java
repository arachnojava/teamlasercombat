package tlc.net.server.tokens;

import mhframework.MHRandom;
import tlc.data.TLCTeam;
import tlc.data.TLCTokenData;
import tlc.data.characters.TLCCharacterType;

public class TLCTokenSource
{
    // TLCTokenProbabilities (pt, ht, gt)
    private static final TLCTokenProbabilities captainProb = new TLCTokenProbabilities(0.40, 0.20, 0.00);
    private static final TLCTokenProbabilities officerProb = new TLCTokenProbabilities(0.25, 0.15, 0.00);
    private static final TLCTokenProbabilities trooperProb = new TLCTokenProbabilities(0.20, 0.10, 0.10);
    
    private TLCTokenGenerator captainsGenerator, officersGenerator, troopersGenerator;
    private TLCTeam team;
    
    public TLCTokenSource(TLCTeam team)
    {
        this.team = team;
        captainsGenerator = new TLCTokenGenerator(captainProb);
        officersGenerator = new TLCTokenGenerator(officerProb);
        troopersGenerator = new TLCTokenGenerator(trooperProb);
    }

    
    public TLCTokenData drawToken(TLCCharacterType type)
    {
        TLCTokenData token = null;
        
        if (team.hasCharacterType(type))
        {
            switch (type)
            {
                case CAPTAIN:
                    token = captainsGenerator.generateToken();
                    break;
                case OFFICER:
                    token = officersGenerator.generateToken();
                    break;
                case TROOPER:
                    token = troopersGenerator.generateToken();
                    break;
            }
            token.setCharacterType(type);
        }
        else
        {
            token = captainsGenerator.generateToken();
            token.setCharacterType(TLCCharacterType.CAPTAIN);
        }
      
      return token;
    }
}
