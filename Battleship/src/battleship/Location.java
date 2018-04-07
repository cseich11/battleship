package battleship;

public class Location {
	private int state;
    private boolean hasShip;
    
    public static final int UNGUESSED = 0;
    public static final int HIT = 1;
    public static final int MISSED = 2;
    public static final int SUNK = 3;
    
    public Location()
    {
        this.state = UNGUESSED;
        this.hasShip = false;
    }

    public boolean checkHit()
    {
        if(this.state == HIT)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean checkMiss()
    {
        if(this.state == MISSED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean checkSunk()
    {
    	return this.state == SUNK;
    }
    
    public boolean isUnguessed()
    {
        if(this.state == UNGUESSED)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void markHit()
    {
        this.state = HIT;
    }
    
    public void markMiss()
    {
        this.state = MISSED;
    }
    
    public void markSunk()
    {
    	this.state = SUNK;
    }
    
    public boolean hasShip()
    {
        return hasShip;
    }
    
    public void setShip(boolean val)
    {
        this.hasShip = val;
    }
    
    public void setStatus(int status)
    {
        this.state = status;
    }
    
    public int getStatus()
    {
        return this.state;
    }
}