package tlc.data;

import java.awt.Color;

public enum TLCTeamColor
{
    BLUE   ("Blue",   Color.BLUE),
    RED    ("Red",    Color.RED),
    GREEN  ("Green",  Color.GREEN),
    YELLOW ("Yellow", Color.YELLOW),
    PURPLE ("Purple", new Color(102, 0, 102)),
    ORANGE ("Orange", new Color(255, 102, 0)),
    PINK   ("Pink",   new Color(255, 102, 255)),
    WHITE  ("White",  Color.WHITE);

    private final Color color;
    private final String name;

    private TLCTeamColor(final String name, final Color color)
    {
        this.name = name;
        this.color = color;
    }

    public Color getColorValue()
    {
        return color;
    }

    public String getName()
    {
        return name;
    }

    public static String lookupName(final Color c)
    {
        String n = null;
        for (final TLCTeamColor tc : values())
        {
            if (tc.color.equals(c))
                n = tc.name;
        }

        return n;
    }
}
