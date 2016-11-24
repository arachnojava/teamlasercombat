package tlc.net.server;

import java.io.Serializable;
import mhframework.io.MHSerializable;
import tlc.data.TLCPlayerMode;

public class TLCPlayerDescriptor implements MHSerializable
{
    private static final long serialVersionUID = 2520210073206025610L;
    
    public String name;
    public TLCPlayerMode type;
    public int clientID;
    public boolean ready;

    
    @Override
    public Serializable getSerializableVersion()
    {
        return this;
    }
    
    
    @Override
    public String toString()
    {
        String typeName = "";
        if (type != null)
            typeName = type.toString();
        
        return "ID:" + clientID + " Name:"+name + " Type:"+ typeName + " Ready:"+ready;
    }
}
