package tlc.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TLCTeamList
{
    private final Map<Integer, TLCTeam> list;
    private int selectedTeam;

    public TLCTeamList()
    {
        list = Collections.synchronizedMap(new HashMap<Integer, TLCTeam>());
    }


    public void addTeam(final TLCTeam newTeam)
    {
        list.put(newTeam.getID(), newTeam);
    }


    public TLCTeam get(final int teamID)
    {
        return list.get(teamID);
    }
    
    
    public int[] getTeamIDs()
    {
        int[] id = new int[list.size()];
        int teamID = 0;
        for (TLCTeam t : list.values())
        {
            id[teamID] = t.getID();
            teamID++;
        }
        
        return id;
    }


    public TLCTeam next()
    {
        while (!list.containsKey(++selectedTeam))
        {
            if (selectedTeam > list.size())
                selectedTeam = -1;
        }

        return get(selectedTeam);
    }


    public TLCTeam previous()
    {
        while (!list.containsKey(--selectedTeam))
        {
            if (selectedTeam < 0)
                selectedTeam = list.size() + 1;
        }

        return get(selectedTeam);
    }


    public int size()
    {
        return list.size();
    }
}
