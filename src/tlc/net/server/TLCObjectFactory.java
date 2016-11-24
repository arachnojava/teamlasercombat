package tlc.net.server;

import mhframework.MHActor;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHObjectFactory;

/********************************************************************
 * The server's version of the object factory.
 *
 */
public class TLCObjectFactory implements MHObjectFactory
{
    public TLCObjectFactory()
    {
    }

    
    @Override
    public MHActor getObject(int layer, int tileID, MHMapCellAddress location)
    {
        // If spawn point, tell server to add it to the list of spawn
        // points and return a null object.
        return null;
    }
}
