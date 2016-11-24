package tlc.data;

public enum TLCPlayerMode
{
	SINGLE_PLAYER   ("Single Player"),
	HOST_LAN        ("Host Player"),
	JOIN_LAN        ("Player"),
	AI_PLAYER       ("AI Player"),
	STANDALONE_HOST ("Host"),
	SPECTATOR        ("Spectator");

	// Internet play has been postponed until a future release.  It
	// will be made available in an expansion pack as DLC.
	//HOST_INTERNET  ("Host Internet Game"),
	//JOIN_INTERNET  ("Join Internet Game");

	private String description;

	TLCPlayerMode(final String textDescription)
	{
		description = textDescription;
	}

	@Override
    public String toString()
	{
		return description;
	}
}

