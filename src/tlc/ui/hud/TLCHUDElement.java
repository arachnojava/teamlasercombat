package tlc.ui.hud;

import mhframework.MHRenderable;
import mhframework.gui.MHGUIComponent;
import mhframework.gui.MHGUIComponentList;

public abstract class TLCHUDElement implements MHRenderable
{
    private static final float SPEED_DIVISOR = 10f;

    HUDElementState STATE_ON;
    HUDElementState STATE_OFF;
    HUDElementState STATE_COMING;
    HUDElementState STATE_GOING;
    
    private HUDElementState state;
    //private TLCHUDElement hudElement;
    private int width, height;
    private float x, y;
    private int visibleX, visibleY;
    private int invisibleX, invisibleY;
    private MHGUIComponentList components;
    
    public TLCHUDElement()
    {
        components = new MHGUIComponentList();
        
        STATE_ON = new OnState(this);
        STATE_OFF = new OffState(this);
        STATE_COMING = new ComingState(this);
        STATE_GOING = new GoingState(this);
    }

    
//    protected void setHudElement(TLCHUDElement e)
//    {
//        hudElement = e;
//    }
    
    public void addComponent(MHGUIComponent c)
    {
        components.add(c);
    }
    
    
    public void showComponents(boolean v)
    {
        if (v)
            components.showAll();
        else
            components.hideAll();
    }
    
    
    public void advance()
    {
        state.advance();
    }

    
    public float getHSpeed()
    {
        return Math.abs((visibleX - invisibleX)/SPEED_DIVISOR);
    }


    public float getVSpeed()
    {
        return Math.abs((visibleY - invisibleY)/SPEED_DIVISOR);
    }
    

    public final void setState(HUDElementState state)
    {
        this.state = state;
    }

    public final void setVisibleLocation(int x, int y)
    {
        visibleX = x;
        visibleY = y;
    }
    
    public final void setHiddenLocation(int x, int y)
    {
        invisibleX = x;
        invisibleY = y;
    }

    public final void setVisible(boolean v)
    {
        if (v)
        {
            if (state != STATE_ON)
                state = STATE_COMING;
        }
        else
        {
            if (state != STATE_OFF)
                state = STATE_GOING;
        }
    }

    
    public boolean isVisible()
    {
        return state.isVisible();
    }

    public float getX()
    {
        return x;
    }


    public void setX(float x)
    {
        this.x = x;
    }


    public float getY()
    {
        return y;
    }


    public void setY(float y)
    {
        this.y = y;
    }


    public int getVisibleX()
    {
        return visibleX;
    }


    public int getVisibleY()
    {
        return visibleY;
    }


    public int getInvisibleX()
    {
        return invisibleX;
    }


    public int getInvisibleY()
    {
        return invisibleY;
    }


    public boolean isInOnPosition()
    {
        float dx = Math.abs(visibleX - x);
        float dy = Math.abs(visibleY - y);
        
        return (dx <= getHSpeed() && dy <= getVSpeed());
    }

    
    public boolean isInOffPosition()
    {
        float dx = Math.abs(invisibleX - x);
        float dy = Math.abs(invisibleY - y);
        
        return (dx <= getHSpeed() && dy <= getVSpeed());
    }


    public int getWidth()
    {
        return width;
    }


    public int getHeight()
    {
        return height;
    }


    public void setHeight(int height)
    {
        this.height = height;
    }


    public void setWidth(int width)
    {
        this.width = width;
    }
}


interface HUDElementState
{
    public void advance();

    public boolean isVisible();
}


class OnState implements HUDElementState
{
    private TLCHUDElement element;
    
    public OnState(TLCHUDElement e)
    {
        element = e;
    }

    public void advance()
    {
        element.setX(element.getVisibleX());
        element.setY(element.getVisibleY());
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}


class OffState implements HUDElementState
{
    private TLCHUDElement element;
    
    public OffState(TLCHUDElement e)
    {
        element = e;
    }

    public void advance()
    {
        element.setX(element.getInvisibleX());
        element.setY(element.getInvisibleY());
    }
    @Override
    public boolean isVisible()
    {
        return false;
    }
}


class ComingState implements HUDElementState
{
    private TLCHUDElement element;
    
    public ComingState(TLCHUDElement e)
    {
        element = e;
    }

    public void advance()
    {
        if (element.isInOnPosition())
            element.setState(element.STATE_ON);
        else
        {
            float dx = 0; 
            float dy = 0;
            
            if (element.getVisibleX() > element.getInvisibleX())
            {
                dx = element.getHSpeed();
            }
            else if (element.getVisibleX() < element.getInvisibleX())
            {
                dx = element.getHSpeed() * -1;
            }
            else if (element.getVisibleY() < element.getInvisibleY())
            {
                dy = element.getVSpeed() * -1;
            }
            else if (element.getVisibleY() > element.getInvisibleY())
            {
                dy = element.getVSpeed();
            }
            
            element.setX(element.getX() + dx);
            element.setY(element.getY() + dy);
        }
    }
    @Override
    public boolean isVisible()
    {
        return true;
    }
}


class GoingState implements HUDElementState
{
    private TLCHUDElement element;
    
    public GoingState(TLCHUDElement e)
    {
        element = e;
    }

    public void advance()
    {
        if (element.isInOffPosition())
            element.setState(element.STATE_OFF);
        else
        {
            float dx = 0; 
            float dy = 0;
            
            if (element.getVisibleX() > element.getInvisibleX())
            {
                // Exit to the left.
                dx = element.getHSpeed() * -1;
            }
            else if (element.getVisibleX() < element.getInvisibleX())
            {
                // Exit to the right.
                dx = element.getHSpeed();
            }
            else if (element.getVisibleY() < element.getInvisibleY())
            {
                // Exit to the bottom.
                dy = element.getVSpeed();
            }
            else if (element.getVisibleY() > element.getInvisibleY())
            {
                // Exit to the top.
                dy = element.getVSpeed() * -1;
            }
            
            element.setX(element.getX() + dx);
            element.setY(element.getY() + dy);
        }
    }
    @Override
    public boolean isVisible()
    {
        return false;
    }
}
