package tlc.data.world;

import mhframework.MHActor;
import tlc.data.TLCDataFacade;
import tlc.data.characters.TLCUpgradeItem;

public class TLCContainer extends MHActor
{
    private TLCUpgradeItem item;
    
    
    public TLCContainer(TLCUpgradeItem item)
    {
        setItem(item);
        super.setImageGroup(TLCDataFacade.getContainerImageGroup());
    }
    
    
    public void setItem(TLCUpgradeItem item)
    {
        this.item = item;
    }
    
    
    public TLCUpgradeItem getItem()
    {
        return item;
    }
}
