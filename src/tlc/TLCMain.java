package tlc;

import mhframework.MHAppLauncher;
import mhframework.MHDisplayModeChooser;
import mhframework.MHGameApplication;
import mhframework.MHScreen;
import mhframework.MHVideoSettings;
import tlc.data.TLCDataFacade;
import tlc.ui.screens.TLCMainMenuScreen;

public class TLCMain
{
    public static final long DATA_ID = TLCDataFacade.generateDataID();

    public static void main(final String[] args)
    {
        final MHScreen screen = new TLCMainMenuScreen();

        final MHVideoSettings settings = new MHVideoSettings();
        settings.fullScreen = MHAppLauncher.showDialog(MHDisplayModeChooser.getFrame(), true);// false;
        settings.displayWidth = MHAppLauncher.getResolution().width;//800;
        settings.displayHeight = MHAppLauncher.getResolution().height;//600;
        settings.windowCaption = "Team Laser Combat";

        new MHGameApplication(screen, settings);

        System.exit(0);
    }
}
