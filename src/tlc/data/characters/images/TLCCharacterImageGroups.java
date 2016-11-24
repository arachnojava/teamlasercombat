package tlc.data.characters.images;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mhframework.media.MHImageGroup;
import tlc.data.TLCTeamColor;

public class TLCCharacterImageGroups
{
    private static Map<TLCTeamColor, MHImageGroup> maleCaptains;
    private static Map<TLCTeamColor, MHImageGroup> femaleCaptains;
    private static Map<TLCTeamColor, MHImageGroup> maleOfficers;
    private static Map<TLCTeamColor, MHImageGroup> femaleOfficers;
    private static Map<TLCTeamColor, MHImageGroup> maleTroopers;
    private static Map<TLCTeamColor, MHImageGroup> femaleTroopers;

    public static MHImageGroup getMaleCaptain(TLCTeamColor teamColor)
    {
        if (!getMaleCaptains().containsKey(teamColor))
        {
            MHImageGroup ig = new MHImageGroup();
            ig.addSequence(0);
            ig.addFrame(0, "images/UpLeft.gif", 10);
            
            getMaleCaptains().put(teamColor, ig);
        }
        
        return getMaleCaptains().get(teamColor);
    }

    
    public static MHImageGroup getFemaleCaptain(TLCTeamColor teamColor)
    {
        if (!getFemaleCaptains().containsKey(teamColor))
        {
            MHImageGroup ig = new MHImageGroup();
            ig.addSequence(0);
            ig.addFrame(0, "images/UpLeft.gif", 10);
            
            getFemaleCaptains().put(teamColor, ig);
        }
        
        return getFemaleCaptains().get(teamColor);
    }
    
    
    public static MHImageGroup getMaleOfficer(TLCTeamColor teamColor)
    {
        return getMaleCaptain(teamColor);
    }

    
    public static MHImageGroup getFemaleOfficer(TLCTeamColor teamColor)
    {
        return getFemaleCaptain(teamColor);
    }

    
    public static MHImageGroup getMaleTrooper(TLCTeamColor teamColor)
    {
        return getMaleCaptain(teamColor);
    }

    
    public static MHImageGroup getFemaleTrooper(TLCTeamColor teamColor)
    {
        return getFemaleCaptain(teamColor);
    }

    
    private static Map<TLCTeamColor, MHImageGroup> getMaleCaptains()
    {
        if (maleCaptains == null)
            maleCaptains = Collections.synchronizedMap(new HashMap<TLCTeamColor, MHImageGroup>());
        
        return maleCaptains;
    }
   

    private static Map<TLCTeamColor, MHImageGroup> getFemaleCaptains()
    {
        if (femaleCaptains == null)
            femaleCaptains = Collections.synchronizedMap(new HashMap<TLCTeamColor, MHImageGroup>());
        
        return femaleCaptains;
    }
   

    private static Map<TLCTeamColor, MHImageGroup> getMaleOfficers()
    {
        if (maleOfficers == null)
            maleOfficers = Collections.synchronizedMap(new HashMap<TLCTeamColor, MHImageGroup>());
        
        return maleOfficers;
    }
   

    private static Map<TLCTeamColor, MHImageGroup> getFemaleOfficers()
    {
        if (femaleOfficers == null)
            femaleOfficers = Collections.synchronizedMap(new HashMap<TLCTeamColor, MHImageGroup>());
        
        return femaleOfficers;
    }

    
    private static Map<TLCTeamColor, MHImageGroup> getMaleTroopers()
    {
        if (maleTroopers == null)
            maleTroopers = Collections.synchronizedMap(new HashMap<TLCTeamColor, MHImageGroup>());
        
        return maleTroopers;
    }
   

    private static Map<TLCTeamColor, MHImageGroup> getFemaleTroopers()
    {
        if (femaleTroopers == null)
            femaleTroopers = Collections.synchronizedMap(new HashMap<TLCTeamColor, MHImageGroup>());
        
        return femaleTroopers;
    }
   

}
