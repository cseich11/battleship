package battleship;

public class Grid {
	private Location[][] grid;

    // Constants for number of rows and columns.
    public static final int NUM_ROWS = 10;
    public static final int NUM_COLS = 10;
    public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    public Grid()
    {
        grid = new Location[NUM_ROWS][NUM_COLS];
        for(int i = 0; i < NUM_ROWS; i++)
        {
            for(int j = 0; j < NUM_COLS; j++)
            {
                grid[i][j] = new Location();
            }
        }
    }
    
    public void markHit(int row, int col)
    {
        grid[row][col].markHit();
    }
    
    public void markMiss(int row, int col)
    {
        grid[row][col].markMiss();
    }
    
    public void setStatus(int row, int col, int status)
    {
        grid[row][col].setStatus(status);
    }
    
    public int getStatus(int row, int col)
    {
        return grid[row][col].getStatus();
    }
    
    public boolean alreadyGuessed(int row, int col)
    {
        return !grid[row][col].isUnguessed();
    }
    
    public void setShip(int row, int col, boolean val)
    {
        grid[row][col].setShip(val);
    }
    
    public boolean hasShip(int row, int col)
    {
        return grid[row][col].hasShip();
    }
    
    public Location getLocation(int row, int col)
    {
        return grid[row][col];
    }
    
    public int numRows()
    {
        return NUM_ROWS;
    }
    
    public int numCols()
    {
        return NUM_COLS;
    }
    
    public void printStatus()
    {
        System.out.print(" ");
        for(int i = 1; i <= NUM_COLS; i++) {
            System.out.print(" " + i);
        }
        System.out.println();
        for(int i = 0; i < NUM_ROWS; i++) {
            System.out.print(LETTERS.charAt(i));
            for(int j = 0; j < NUM_COLS; j++) {
                if(grid[i][j].isUnguessed())
                    System.out.print(" -");
                else if(grid[i][j].checkMiss())
                    System.out.print(" O");
                else if(grid[i][j].checkSunk())
                    System.out.print(" S");
                else
                	System.out.print(" X");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void printShips()
    {
        System.out.print(" ");
        for(int i = 1; i <= NUM_COLS; i++) {
            System.out.print(" " + i);
        }
        System.out.println();
        for(int i = 0; i < NUM_ROWS; i++) {
            System.out.print(LETTERS.charAt(i));
            for(int j = 0; j < NUM_COLS; j++) {
                if(grid[i][j].hasShip())
                	if(grid[i][j].checkHit())
                		System.out.print(" X");
                	else if(grid[i][j].checkSunk())
                		System.out.print(" S");
                	else
                		System.out.print(" O");
                else
                    System.out.print(" -");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    public void addShip(Ship s)
    {
        int sRow = s.getRow();
        int sCol = s.getCol();
        int sDirection = s.getDirection();
        int sLength = s.getLength();
        
        if(sDirection == Ship.VERTICAL)
            for(int i = sRow; i < sRow + sLength; i++)
                grid[i][sCol].setShip(true);
        else if(sDirection == Ship.HORIZONTAL)
            for(int i = sCol; i < sCol + sLength; i++)
                grid[sRow][i].setShip(true);
    }
    
    
    
    public void sunkIt(Ship s)
    {
    	int row = s.getRow();
    	int col = s.getCol();
    	int direction = s.getDirection();
    	int length = s.getLength();
    	
    	if(direction == Ship.HORIZONTAL)
    		for(int i = col; i < col + length; i++)
    			grid[row][i].markSunk();
    	else
    		for(int i = row; i < row + length; i++)
    			grid[i][col].markSunk();
    }
}