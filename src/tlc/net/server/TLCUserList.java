package tlc.net.server;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import tlc.data.TLCPlayerMode;
import mhframework.io.MHSerializable;
import mhframework.io.net.server.MHClientInfo;

public class TLCUserList implements MHSerializable
{
    private static final long serialVersionUID = -3731896091692710744L;
    private Map<Integer, TLCPlayerDescriptor> list;

    
    public TLCUserList()
    {
        list = Collections.synchronizedMap(new HashMap<Integer, TLCPlayerDescriptor>());
    }
    

    public void addUser(TLCPlayerDescriptor user)
    {
        if (user != null)
            list.put(user.clientID, user);
    }
    
    
    public void addUser(MHClientInfo user)
    {
        TLCPlayerDescriptor newUser = new TLCPlayerDescriptor();
        newUser.clientID = user.id;
        newUser.name = user.name;
        addUser(newUser);
    }
    
    
    public void removeUser(int userID)
    {
        list.remove(userID);
    }

    
    public int size()
    {
        return list.size();
    }
    
    
    public TLCPlayerDescriptor getUser(int id)
    {
        return list.get(id);
    }

    @Override
    public Serializable getSerializableVersion()
    {
        return this;
    }


    public int countPlayers()
    {
        int count = 0;
        
        for (TLCPlayerDescriptor player : list.values())
        {
            if (player.type != null && player.type != TLCPlayerMode.SPECTATOR)
                count++;
        }
        
        return count;
    }

    
    public void resetReadyStates()
    {
        for (TLCPlayerDescriptor player : list.values())
        {
            if (player.type != null)
                if (!player.type.equals(TLCPlayerMode.SPECTATOR))
                    player.ready = false;
        }
    }

    public int countReadyPlayers()
    {
        int count = 0;
        
        for (TLCPlayerDescriptor player : list.values())
        {
            if (player.ready && player.type != null && player.type != TLCPlayerMode.SPECTATOR)
                count++;
        }
        
        return count;
    }


    public int countHumanPlayers()
    {
        int count = 0;
        
        for (TLCPlayerDescriptor player : list.values())
        {
            if (player.type != null && !player.type.equals(TLCPlayerMode.SPECTATOR) && !player.type.equals(TLCPlayerMode.AI_PLAYER))
                count++;
        }
        
        return count;
    }
    
    
    public int countReadyHumans()
    {
        int count = 0;
        
        for (TLCPlayerDescriptor player : list.values())
        {
            if (player.type != null && !player.type.equals(TLCPlayerMode.SPECTATOR) && !player.type.equals(TLCPlayerMode.AI_PLAYER) && player.ready)
                count++;
        }
        
        return count;
    }
}
