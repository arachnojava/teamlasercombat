package tlc.ui.hud.actions;

import java.awt.event.ActionListener;
import mhframework.MHRenderable;

public interface TLCActionsMenuState extends MHRenderable, ActionListener
{

    void unload();

    void load();

}
