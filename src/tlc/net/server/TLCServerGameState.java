package tlc.net.server;

import mhframework.io.net.MHNetworkMessage;
import mhframework.io.net.server.MHAbstractServer;
import mhframework.io.net.server.MHClientInfo;
import mhframework.io.net.server.MHGameServer;
import mhframework.io.net.server.MHServerModule;

public class TLCServerGameState implements MHGameServer
{
    private TLCGameServer parent;
    
    public TLCServerGameState(TLCGameServer parent)
    {
        super();
        this.parent = parent;
    }
    
    public void receiveMessage(MHClientInfo sender, MHNetworkMessage message,
            MHServerModule server)
    {
    }

    @Override
    public void receiveMessage(MHNetworkMessage message,
            MHAbstractServer mhAbstractServer)
    {
        // TODO Auto-generated method stub
        
    }

}
