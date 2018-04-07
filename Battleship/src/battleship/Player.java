package battleship;

public class Player {
	// These are the lengths of all of the ships.
    private static final int[] SHIP_LENGTHS = {2, 3, 3, 4, 5};

    //instance variables for personal grid and opponent grid
    private Grid playerGrid, opponentGrid;
    private Ship[] playerShips;
    
    /**
     * Constructor for objects of class Player
     */
    public Player()
    {
        // initialize instance variables
        playerGrid = new Grid();
        opponentGrid = new Grid();
        playerShips = new Ship[SHIP_LENGTHS.length];
        for(int i = 0; i < playerShips.length; i++)
            playerShips[i] = new Ship(SHIP_LENGTHS[i]);
    }
    
    public Grid getPlayerGrid()    {return playerGrid;}
    public Grid getOpponentGrid()  {return opponentGrid;}
    public Ship[] getPlayerShips() {return playerShips;}
    
    public void buildBoard(Ship s)
    {
    	playerGrid.addShip(s);
    }
    
    public void updateGrid(int row, int col, boolean hit)
    {
    	if(hit)
    	{
    		playerGrid.markHit(row, col);
    		for(int i = 0; i < playerShips.length; i++)
        	{
        		Ship s = playerShips[i];
        		if(s.isSunk())
        		{
        			playerGrid.sunkIt(s);
        		}
        	}
    	}
    	else
    		playerGrid.markMiss(row, col);
    }
    
    public void updateOppGrid(int row, int col, boolean hit, Ship s)
    {
    	if(hit)
    	{
    		opponentGrid.markHit(row, col);
    	}
    	else
    		opponentGrid.markMiss(row, col);
    	
    	if(s != null)
    	{
    		opponentGrid.sunkIt(s);
    	}
    }
    
    public void printGrid()
    {
    	playerGrid.printShips();
    }
    
    public void printOppGrid()
    {
    	opponentGrid.printStatus();
    }
    
    public boolean hasWon()
    {
        int hitsNeeded = 0;
        int totalHits = 0;
        for(int i = 0; i < SHIP_LENGTHS.length; i++)
            hitsNeeded += SHIP_LENGTHS[i];
        for(int i = 0; i < Grid.NUM_ROWS; i++)
            for(int j = 0; j < Grid.NUM_COLS; j++)
                if(opponentGrid.getLocation(i, j).checkHit() || opponentGrid.getLocation(i, j).checkSunk())
                    totalHits++;
        return totalHits == hitsNeeded;
    }
}