package tlc.data.characters;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.Random;
import mhframework.MHActor;
import mhframework.ai.path.MHNodePath;
import mhframework.ai.path.MHPathFinder;
import mhframework.ai.state.MHState;
import mhframework.tilemap.MHIsoMouseMap;
import mhframework.tilemap.MHMapCellAddress;
import mhframework.tilemap.MHTileMap;
import mhframework.tilemap.MHTileMapDirection;
import tlc.data.TLCDataFacade;
import tlc.data.TLCTeam;
import tlc.data.TLCTeamColor;
import tlc.data.characters.state.TLCStandingState;
import tlc.data.characters.state.TLCWalkingState;
import tlc.ui.TLCUI;

public class TLCCharacter extends MHActor implements TLCBuySell
{
    private static final long serialVersionUID = -8387719870479978109L;
    private static final int NUM_RANDOM_BONUSES = 10;
    private TLCCharacterData data = new TLCCharacterData();
    private int movementPoints = 0;
    private MHState state = TLCStandingState.getInstance();
    private MHMapCellAddress mapLocation;

    public MHNodePath path;
    public int nextNodeIndex = 0;
    public boolean isTakingTurn;
    
    public TLCCharacter(final TLCCharacterType charType,
                    final TLCCharacterGender charGender)
    {
        setType(charType);
        setGender(charGender);
        setTrainingLevel(0.05);
        setActionPoints(TLCDataFacade.ACTION_POINTS);
        
        // Roll up random starting stats for character.
        for (int r = 0; r < NUM_RANDOM_BONUSES; r++)
        {
            Random rand = new Random();
            double value;
            switch (rand.nextInt(4))
            {
                case 0:
                    value = getAttackValue() + 0.01;
                    setAttackValue(value);
                    break;
                case 1:
                    value = getDefenseValue() + 0.01;
                    setDefenseValue(value);
                    break;
                case 2:
                    value = getMovementValue() + 0.01;
                    setMovementValue(value);
                    break;
                default:
                    value = getTrainingLevel() + 0.01;
                    setTrainingLevel(value);
            }
        }
        // Set actor's image group based on type, gender, and color.
        TLCTeam t = TLCDataFacade.getTeam(getTeamID());
        if (t != null)
        {
            TLCTeamColor teamColor = t.getColor();
            setImageGroup(TLCDataFacade.getImageGroup(getType(), getGender(), teamColor));
        }
    }

    
    public void changeState(MHState nextState)
    {
        state.exit(this);
        state = nextState;
        state.enter(this);
    }


    public TLCCharacter clone()
    {
        TLCCharacter newChar = new TLCCharacter(this.getType(), this.getGender());
        newChar.data = this.data.clone();
        
        return newChar;
    }
    

    private void setType(final TLCCharacterType charType)
    {
        data.type = charType;
        setMovementValue(data.type.getDefaultMovementBonus());
        setMaxHealth(data.type.getDefaultHP());
        setHealth(getMaxHealth());
    }


    private void setGender(final TLCCharacterGender charGender)
    {
        data.gender = charGender;
    }


    public void setTeamID(final int id)
    {
        data.teamID = id;
        
        // Set actor's image group based on type, gender, and color.
        TLCTeam t = TLCDataFacade.getTeam(id);
        if (t != null)
        {
            TLCTeamColor teamColor = t.getColor();
            setImageGroup(TLCDataFacade.getImageGroup(getType(), getGender(), teamColor));
        }
    }


    public int getTeamID()
    {
        return data.teamID;
    }


    public String getName()
    {
        if (data.name == null || data.name.trim().length() == 0)
        {
            return ""+getType().getTitle().charAt(0) + getCharacterID();
        }

        return data.name;
    }


    public void setName(final String name)
    {
        data.name = name;
    }


    public double getAttackValue()
    {
        return data.attackValue;
    }


    public void setAttackValue(double attackValue)
    {
        if (attackValue < 0.0)
            attackValue = 0.0;

        data.attackValue = attackValue;
    }


    public double getDefenseValue()
    {
        return data.defenseValue;
    }


    public void setDefenseValue(double defenseValue)
    {
        if (defenseValue < 0.0)
            defenseValue = 0.0;

        data.defenseValue = defenseValue;
    }


    public double getTrainingLevel()
    {
        return data.trainingLevel;
    }


    public void setTrainingLevel(double trainingLevel)
    {
        if (trainingLevel < 0.0)
            trainingLevel = 0.0;

        data.trainingLevel = trainingLevel;
    }


    public TLCCharacterGender getGender()
    {
        return data.gender;
    }


    public TLCCharacterType getType()
    {
        return data.type;
    }


    public void setMovementValue(double value)
    {
        if (value < 0.0)
            value = 0.0;

        data.movementValue = value;
    }


    public double getMovementValue()
    {
        return data.movementValue;
    }


    @Override
    public int cost()
    {
        return data.type.getCost();
    }


    @Override
    public int sellValue()
    {
        return cost() / 2;
    }


    @Override
    public void setHealth(int hp)
    {
        if (hp > data.maxHP)
            hp = data.maxHP;

        super.setHealth(hp);
        data.hp = hp;
    }


    
    
    
    public int getMaxHealth()
    {
        return data.maxHP;
    }
    
    
    @Override
    public void setMaxHealth(int maxHP)
    {
        if (maxHP > 20)
            maxHP = 20;

        super.setMaxHealth(maxHP);
        data.maxHP = maxHP;
    }


    public static TLCCharacter deserialize(final Serializable object)
    {
        final TLCCharacterData c = (TLCCharacterData) object;
        TLCCharacter newCharacter = new TLCCharacter(c.type, c.gender);
        newCharacter.data = c;
        newCharacter.setMaxHealth(c.maxHP);

        return newCharacter;
    }


    public Serializable getSerializableVersion()
    {
        return data.clone();
    }


    @Override
    public boolean equals(final Object c)
    {
        final TLCCharacter other = (TLCCharacter) c;
        return this.data.teamID == other.data.teamID && this.data.characterID == other.data.characterID;
    }
    
    
    public int getCharacterID()
    {
        return data.characterID;
    }

    @Override
    public String toString()
    {
        String output = "ID " + getCharacterID()+": " + getName();
        output += "\tLoc:" + getMapLocation();
        output += "\tTeam:" + getTeamID();
        output += "\tHP:" + getHealth();
        output += "\tTL:" + getTrainingLevel();
        output += "\tAT:" + getAttackValue();
        output += "\tDF:" + getDefenseValue();
        output += "\tMV:" + getMovementValue();

        return output;
    }


    public void setTeam(final int teamID)
    {
        data.teamID = teamID;
    }


    public void setCharacterID(int id)
    {
        data.characterID = id;
    }


    @Override
    public int getHealth()
    {
        return data.hp;
    }


    public MHMapCellAddress getMapLocation()
    {
        if (data.row <= 0 && data.column <= 0)
            return null;
        
        if (mapLocation != null)
            return mapLocation;
        
        mapLocation = new MHMapCellAddress(data.row, data.column);
        
        return mapLocation;
    }


    public void setMapLocation(int row, int column)
    {
        data.row = row;
        data.column = column;
        if (mapLocation == null)
            mapLocation = new MHMapCellAddress(row, column);
        else
        {
            mapLocation.row = data.row;
            mapLocation.column = data.column;
        }
        
    }

    
    public void setMapLocation(MHMapCellAddress location)
    {
        if (location == null) return;
        
        setMapLocation(location.row, location.column);
    }

    
    public void advance()
    {
        // DEBUG
        if (this.getName().equalsIgnoreCase("Test"))
        {
            System.out.print(" "+getClass().getName()+".advance() ");
            System.out.println(state.getClass().getName());
        }
         
        state.execute(this);
        
        super.advance();
    }

    
    @Override
    public void render(Graphics2D g, int rx, int ry)
    {
        Point rp = TLCDataFacade.getGameWorld().worldToScreen(getLocation()); 
        super.render(g, (int)rp.getX(), (int)rp.getY());
        g.setColor(TLCDataFacade.getTeam(getTeamID()).getColor().getColorValue());
        int nameX = (rp.x + MHIsoMouseMap.WIDTH/2) - TLCUI.Fonts.BUTTON_12.stringWidth(getName())/2;
        TLCUI.Fonts.BUTTON_12.drawString(g, getName(), nameX, rp.getY()+40);
        if (TLCDataFacade.DEBUG)
        {
            TLCUI.Fonts.BUTTON_12.drawString(g, "World:(" + (int)getLocation().getX() + ", " + (int)getLocation().getY() + ")", nameX, rp.getY()-20);
            TLCUI.Fonts.BUTTON_12.drawString(g, "Grid:[" + getMapLocation().row + ", " + getMapLocation().column + "]", nameX, rp.getY());
            TLCUI.Fonts.BUTTON_12.drawString(g, "Actions:" + getActionPoints(), nameX, rp.getY()+20);
            
            // DEBUG
//            if (this.getName().equalsIgnoreCase("Test"))
//            {
//                System.out.print("TEST CHARACTER RENDER: (" + rx + "," + ry + ") " + MHGame.getGameTimerValue());
//                System.out.println(" # Characters: " + TLCDataFacade.getCharacterList().size());
//            }
        }
    }


    public int getActionPoints()
    {
        return data.actionPoints;
    }
    
    
    public void setActionPoints(int points)
    {
        data.actionPoints = points;
    }


    public void setMovementPoints(int mp)
    {
        movementPoints = mp;
    }


    public int getMovementPoints()
    {
        return movementPoints;
    }


    public void walkTo(MHMapCellAddress destination)
    {
        // Generate a path to destination.
        path = this.getPath(destination);
        nextNodeIndex = 0;
        
        
        
        // Enter "walking" state.
        changeState(TLCWalkingState.getInstance()); 
        
        // DEBUG
        if (TLCDataFacade.DEBUG && path != null)
        {
            System.out.print("Path:");
            for (int i = 0; i < path.size(); i++)
                System.out.print(" "+path.get(i));
            System.out.println();
        }

    }


    public MHNodePath getPath(MHMapCellAddress destination)
    {
        if (path == null || path.size() < 1)
        {
            MHTileMap map = TLCDataFacade.getGameWorld().getMap();
            MHTileMapDirection[] directions = new MHTileMapDirection[4];
            directions[0] = MHTileMapDirection.NORTHEAST;
            directions[1] = MHTileMapDirection.SOUTHEAST;
            directions[2] = MHTileMapDirection.SOUTHWEST;
            directions[3] = MHTileMapDirection.NORTHWEST;
            path = MHPathFinder.aStarSearch(this.getMapLocation(), destination, map, directions);
        }
        return path;
    }
    
    
    public MHNodePath getPath()
    {
        return path;
    }
}



class TLCCharacterData implements Serializable
{
    private static final long serialVersionUID = 1L;

    public int row, column;
    public String name;
    public int characterID;
    public TLCCharacterGender gender;
    public double attackValue;
    public double defenseValue;
    public double trainingLevel;
    public TLCCharacterType type;
    public double movementValue;
    public int teamID;
    public int hp;
    public int maxHP;
    public int actionPoints;
    
    public TLCCharacterData clone()
    {
        TLCCharacterData data = new TLCCharacterData();
        
        data.row = row;
        data.column = column;
        data.name = name;
        data.characterID = characterID;
        data.gender = gender;
        data.attackValue = attackValue;
        data.defenseValue = defenseValue;
        data.trainingLevel = trainingLevel;
        data.type = type;
        data.movementValue = movementValue;
        data.teamID = teamID;
        data.maxHP = maxHP;
        data.hp = hp;
        data.actionPoints = actionPoints;
        
        return data;
    }
}
