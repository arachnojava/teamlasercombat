package tlc.data.world;

import java.awt.Color;
import java.awt.Graphics2D;
import mhframework.MHActor;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeamColor;
import tlc.data.characters.TLCArmorUpgrade;
import tlc.data.characters.TLCCharacter;
import tlc.data.characters.TLCCharacterGender;
import tlc.data.characters.TLCCharacterType;
import tlc.data.characters.TLCUpgradeItem;

public class TLCPickupItem extends MHActor
{
    TLCUpgradeItem item;
    
    public TLCPickupItem(TLCUpgradeItem item)
    {
        this.item = item;
        
        //if (item instanceof TLCArmorUpgrade)
            this.setImageGroup(TLCDataFacade.getImageGroup(TLCCharacterType.TROOPER, TLCCharacterGender.MALE, TLCTeamColor.RED));
    }
    
    
    public void render(Graphics2D g)
    {
        super.render(g);
    }


    @Override
    public void render(Graphics2D g, int rx, int ry)
    {
        super.render(g, rx, ry);
    }


    public void apply(TLCCharacter c)
    {
        item.applyUpgrade(c);
    }
    
    
    public String getName()
    {
        return item.getName();
    }
}
