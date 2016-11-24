package tlc.net.server.tokens;

import mhframework.MHRandom;
import tlc.data.TLCPowerTokenSet;
import tlc.data.TLCTokenType;

public class TLCPowerTokenSelector
{
    private static final int NUM_POWER_TOKENS = 8;
    
    private TLCPowerTokenSet tokensUsed;
    
    public TLCPowerTokenSelector()
    {
        tokensUsed = new TLCPowerTokenSet();
    }
    
    public void reset()
    {
        tokensUsed.reset();
    }
    
    public TLCTokenType selectToken() throws Exception
    {
        // Try 10 times to find an unused token.
        for (int tries = 0; tries < 10; tries++)
        {
            int rand = MHRandom.random(0, NUM_POWER_TOKENS-1);
            if (!tokensUsed.hasToken(rand))
            {
                tokensUsed.addToken(rand);
                return TLCTokenType.values()[rand];
            }
        }

        // If that fails, do a linear search.
        for (int i = 0; i < tokensUsed.getCount(); i++)
        {
            if (!tokensUsed.hasToken(i))
            {
                tokensUsed.addToken(i);
                return TLCTokenType.values()[i];
            }
        }
        
        // If that fails, return an error.
        throw new Exception("Power Token supply empty.");
    }
}
