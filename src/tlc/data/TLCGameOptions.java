package tlc.data;

import java.io.Serializable;
import mhframework.io.MHSerializable;
import mhframework.media.MHResourceManager;

/********************************************************************
 * Class for storing game setup parameters.  An object of this class
 * will store the options as they're being selected through the UI, 
 * and will then be passed to the server when time to initialize the 
 * game.
 *
 * <p>This class is initialized by the game host and then passed to
 * the server to tell it how to set up the game.</p>
 */
public final class TLCGameOptions implements MHSerializable
{
    public static final boolean DEBUG = true;

    private static final long serialVersionUID = 1L;
	private TLCPlayerMode playerMode;
	private boolean soundOn = true;
    private boolean musicOn = true;
    private static boolean combatResultsOn = true;
    private static boolean autoAttackOn = false;
    private static boolean autoDefenseOn = false;

	// Speculative features yet to be prioritized:
	//private TLCAllianceOption allianceMode; // Decorator pattern implementation.
	//private TLCVictoryCondition victoryCondition; // Strategy pattern implementation.


	/****************************************************************
	 * Returns the currently set player mode.  Since there are a
	 * finite number of modes, the value returned is an enumeration.
	 *
	 * @return A PlayerMode enumeration indicating the selected
	 * player mode.
	 */
    public TLCPlayerMode getPlayerMode()
    {
        return playerMode;
    }


    public static boolean isCombatResultsOn()
    {
        return combatResultsOn;
    }


    public static void setCombatResultsOn(boolean combatResultsOn)
    {
        TLCGameOptions.combatResultsOn = combatResultsOn;
    }


    public boolean isAutoAttackOn()
    {
        return autoAttackOn;
    }


    public void setAutoAttackOn(boolean autoAttackOn)
    {
        TLCGameOptions.autoAttackOn = autoAttackOn;
    }


    public boolean isAutoDefenseOn()
    {
        return autoDefenseOn;
    }


    public void setAutoDefenseOn(boolean autoDefenseOn)
    {
        TLCGameOptions.autoDefenseOn = autoDefenseOn;
    }


    /****************************************************************
     * Sets the player mode to be used for configuring the game.
     *
     * @param playerMode A value from the PlayerMode enumeration.
     */
    public void setPlayerMode(final TLCPlayerMode playerMode)
    {
        if (playerMode != null)
            this.playerMode = playerMode;
    }


    @Override
    public Serializable getSerializableVersion()
    {
        return this;
    }


    public void setSoundOn(boolean soundOn)
    {
        this.soundOn = soundOn;
        MHResourceManager.getSoundManager().setSoundOn(soundOn);
    }


    public boolean isSoundOn()
    {
        return soundOn;
    }


    public void setMusicOn(boolean musicOn)
    {
        this.musicOn = musicOn;
    }


    public boolean isMusicOn()
    {
        return musicOn;
    }
}