package battleship;

public class Ship {
	public static final int UNSET = -1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int direction;
    private int length;
    private int col;
    private int row;
    private boolean sunk;

    
    public Ship(int length)
    {
        this.length = length;
        this.direction = UNSET;
        this.row = UNSET;
        this.col = UNSET;
        this.sunk = false;
    }
    
    public boolean isLocationSet()
    {
        if(this.row == UNSET && this.col == UNSET)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public boolean isDirectionSet()
    {
        if(this.direction == HORIZONTAL || this.direction == VERTICAL)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void setLocation(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    
    public void setDirection(int direction)
    {
        this.direction = direction;
    }
    
    public int getRow()
    {
        return this.row;
    }
    
    public int getCol()
    {
        return this.col;
    }
    
    public int getLength()
    {
        return this.length;
    }
    
    public int getDirection()
    {
        return this.direction;
    }
    
    public void sunkIt()
    {
    	this.sunk = true;
    }
    
    public boolean isSunk()
    {
    	return sunk;
    }
    
    private String directionToString()
    {
        if(this.direction == HORIZONTAL)
        {
            return "horizontal";
        }
        else if(this.direction == VERTICAL)
        {
            return "vertical";
        }
        else
        {
            return "unset direction";
        }
    }
    
    private String locationToString()
    {
        if(this.row == UNSET || this.col == UNSET)
            return "(unset location)";
        else
            return "(" + this.row + ", " + this.col + ")";
    }
    
    public String toString()
    {
        return (directionToString() + " ship of length " + this.length + " at " + locationToString());
    }
}