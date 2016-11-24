package tlc.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import mhframework.io.MHSerializable;
import tlc.data.characters.TLCCharacter;

public class TLCCharacterList implements MHSerializable, java.lang.Iterable<TLCCharacter>
{
    private static final long serialVersionUID = -5474059935027804969L;
    private final Map<Integer, TLCCharacter> list;
    private int selectedCharacter = 0;

    public TLCCharacterList()
    {
        list = Collections.synchronizedMap(new HashMap<Integer, TLCCharacter>());
    }


    public TLCCharacter get(final int charID)
    {
        return list.get(charID);
    }


    public void add(final TLCCharacter newCharacter)
    {
        list.put(newCharacter.getCharacterID(), newCharacter);
    }


    public void remove(final TLCCharacter c)
    {
        remove(c.getCharacterID());
    }


    public void remove(final int charID)
    {
        list.remove(charID);
    }


    public TLCCharacterList getTeamMembers(final int teamID)
    {
        final TLCCharacterList team = new TLCCharacterList();

        for (final TLCCharacter c : list.values())
            if (c.getTeamID() == teamID)
                team.add(c);

        return team;
    }


    public int size()
    {
        return list.size();
    }


    @Override
    public Iterator<TLCCharacter> iterator()
    {
        return list.values().iterator();
    }


    @Override
    public Serializable getSerializableVersion()
    {
        return new TLCSerializableCharacterList(this);
    }


    public static TLCCharacterList deserialize(Serializable payload)
    {   
        TLCSerializableCharacterList oldList = (TLCSerializableCharacterList) payload; 
        TLCCharacterList newList = new TLCCharacterList();
        for (Serializable c : oldList.list.values())
            newList.add(TLCCharacter.deserialize(c));
        
        return newList;
    }
}


class TLCSerializableCharacterList implements Serializable
{
    private static final long serialVersionUID = 1L;
    public final Map<Integer, Serializable> list;

    public TLCSerializableCharacterList(TLCCharacterList orig)
    {
        list = new HashMap<Integer, Serializable>();
        for (TLCCharacter c : orig)
            list.put(c.getCharacterID(), c.getSerializableVersion());
    }
}
