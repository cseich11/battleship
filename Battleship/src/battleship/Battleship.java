package battleship;

import java.util.*;
public class Battleship
{
	//constants for user/ai turn id
	private static final String USER = "user";
	private static final String AI = "ai";
	//declare players
	private static Player player1, player2;
	//Strings for row input, ship direction, and AI/user turn
	private static String shipDirection, turn;
	//holds character of row input after using String version to make character upper case
	private static char row;
	//ship and guess identities, and a counter for number of digits in column input
	private static int p1shipNum, p2shipNum, shipRow, shipCol, shipLength, directionValue, colDigits;
	//scanner for user input
	private static Scanner input = new Scanner(System.in);
	
	//The following instance variables are used by AI players ONLY in order to make
	//intelligent guesses once a ship has been hit
	private static int hitRow, //holds row for most recent hit location of a ship
					   hitCol, //holds column for most recent hit location of a ship
					   originHitRow, //holds row for first hit location of a ship
					   originHitCol, //holds row for first hit location of a ship
					   hitCounter; //keeps track of number of locations of a ship have been hit
					   	
	/**
	 * Since ships might be adjacent, the AI's guesses could hit two or more ships
	 * before any of them are considered sunk. For this reason, an ArrayList is
	 * used to store any hit locations (row & column on ships other than the first
	 * ship was hit. This will allow the AI to move on to another hit ship and try
	 * to sink it after the first ship is sunk, and so on until all ships hit so
	 * far are marked sunk.
	 */
	private static ArrayList<Integer[]> otherHits; //list of arrays of other hits
	
	private static Integer[] hitSave; //will be length 3, storing an integer for
								      //hit row and then an integer for hit column
									  //and an integer for number of the ship in the
									  //array of player ships
	
    public static void main(String[] args)
    {
    	//this Scanner is only used once to advance the game after ships have been set
    	Scanner continuer = new Scanner(System.in);
    	
    	//two player objects with their own ships and grids
        player1 = new Player();
        player2 = new Player();
        
        //for storing amount of ships each player has before they are set on the grid
        int p1ShipsUnset, p2ShipsUnset;
        
        //temp storage for pointing to a ship in player's list of ships
        Ship ship;
        
        //intialize the hit counter and ArrayList for keeping track of AI hits
        hitCounter = 0;
        otherHits = new ArrayList<Integer[]>();
        
        //at game start, print empty boards and notify it is time for board preparation
        System.out.println("Time to play Battleship!");
        System.out.println();
        
        System.out.println("Player 1 board:");
        player1.printGrid();
        System.out.println("\nPlayer 2 board:");
        player2.printGrid();
        System.out.println("\n========================\n");
        System.out.println("Players may prepare their boards now!\n");
        
        //save number of ships belonging to each player in their respective ints
        p1ShipsUnset = player1.getPlayerShips().length;
        p2ShipsUnset = player2.getPlayerShips().length;
        
        //set turn id to USER
        turn = USER;
        
        //provide ship placement explanation
        System.out.println("Player 1: set ships of length 2, 3, 3, 4, 5");
        System.out.println("Vertical ships will enter top to bottom, "
            + "horizontal ships will enter left to right.\n");
        
        //loop through each ship of player 1 to set them on the player's grid
        for(int i = 0; i < p1ShipsUnset; i++){
            ship = player1.getPlayerShips()[i];
            shipLength = ship.getLength();
            System.out.println("Enter, in one line, separated by spaces, row (A - " 
            + Grid.LETTERS.charAt(Grid.NUM_ROWS - 1) + ")"
            + " and column (1 - " + (Grid.NUM_COLS) + ")");
            System.out.print("and direction (for vertical enter v / for horizontal enter h) "
            + "for start of ship (length " + shipLength + "): ");
            
            placeShip();
            while(!validPlacement(shipRow, shipCol))
            	placeShip();
            
        	
        	ship.setDirection(directionValue);
        	ship.setLocation(shipRow, shipCol);
        	player1.buildBoard(ship);
        	
            System.out.println("Player 1 Board:");
            player1.printGrid();
        }
        
        System.out.println("\nComputer will now set up Ships\n");
        System.out.println("Press enter to continue...");
        continuer.nextLine();
        
        //set up player 2 grid (AI)
        turn = AI;
        for(int i = 0; i < p2ShipsUnset; i++){
            ship = player2.getPlayerShips()[i];
            shipLength = ship.getLength();
            placeAIShip();
            while(!validPlacement(shipRow, shipCol))
            	placeAIShip();
            
            ship.setDirection(directionValue);
            ship.setLocation(shipRow, shipCol);
            player2.buildBoard(ship);
        }
        
        
        while(!player1.hasWon() && !player2.hasWon()){
            System.out.println("Player 1's Turn");
            turn = USER;
            System.out.print("Enter, in one line, separated by spaces, row (A - " 
                    + Grid.LETTERS.charAt(Grid.NUM_ROWS - 1) + ")"
                    + " and column (1 - " + (Grid.NUM_COLS)
                    + ") for a guess: ");
            guess();
            while(!validGuess(shipRow, shipCol))
            	guess();
            if(player2.getPlayerGrid().hasShip(shipRow, shipCol))
            {
            	p2shipNum = getShipNum(shipRow, shipCol);
            	player1.updateOppGrid(shipRow, shipCol, true, null);
            	player2.updateGrid(shipRow, shipCol, true);
            	if(isSunk(p2shipNum))
            	{
            		player2.getPlayerShips()[p2shipNum].sunkIt();
            		ship = player2.getPlayerShips()[p2shipNum];
            		System.out.println("Sunk It!");
            		player1.updateOppGrid(shipRow, shipCol, true, ship);
                	player2.updateGrid(shipRow, shipCol, true);
            	}
            	else
            		System.out.println("Hit!");
            }
            else
            {
            	player1.updateOppGrid(shipRow, shipCol, false, null);
            	player2.updateGrid(shipRow, shipCol, false);
            	System.out.println("Miss!");
            }
            System.out.println();
            System.out.println("Computer Board Status:");
            player1.printOppGrid();            
            
            if(player1.hasWon())
            	break;
            
            System.out.println("AI's Turn");
            turn = AI;
            AIGuess();
            while(!validGuess(shipRow, shipCol))
            	AIGuess();
            if(player1.getPlayerGrid().hasShip(shipRow, shipCol))
            {
            	if(hitCounter > 0 && newShip(shipRow, shipCol))
            	{
            		player2.updateOppGrid(shipRow, shipCol, true, null);
            		player1.updateGrid(shipRow, shipCol, true);
            		hitSave = new Integer[3];
            		hitSave[2] = getShipNum(shipRow, shipCol);
            		if(!isSunk(hitSave[2]))
            		{
            			hitSave[0] = shipRow; //save hit row in first spot
                		hitSave[1] = shipCol; //save hit column in second spot
            			otherHits.add(hitSave);
            			System.out.println("Hit!");
            		}
            		else
            		{
            			player1.getPlayerShips()[hitSave[2]].sunkIt();
	            		ship = player1.getPlayerShips()[hitSave[2]];
	            		System.out.println("Sunk It!");
	                	player1.updateGrid(shipRow, shipCol, true);
	                	player2.updateOppGrid(shipRow, shipCol, true, ship);
            		}
            	}
            	else
            	{
            		hitCounter++;
	            	hitRow = shipRow;
	            	hitCol = shipCol;
	            	p1shipNum = getShipNum(shipRow, shipCol);
	            	directionValue = player1.getPlayerShips()[p1shipNum].getDirection();
	            	player2.updateOppGrid(shipRow, shipCol, true, null);
	            	player1.updateGrid(shipRow, shipCol, true);
	            	if(isSunk(p1shipNum))
	            	{
	            		player1.getPlayerShips()[p1shipNum].sunkIt();
	            		ship = player1.getPlayerShips()[p1shipNum];
	            		System.out.println("Sunk It!");
	                	player1.updateGrid(shipRow, shipCol, true);
	                	player2.updateOppGrid(shipRow, shipCol, true, ship);
	                	if(otherHits.size() > 0)
	            		{
	                		while(otherHits.size() > 0)
	                		{
		                		hitSave = otherHits.remove(0);
		                		if(!isSunk(hitSave[2]))
		                		{
			                		p1shipNum = hitSave[2];
		                			hitRow = hitSave[0];
			            			hitCol = hitSave[1];///////////////
			            			hitCounter = 1;
			            			break;
		                		}
	                		}
	            		}
	            		else
	            		{
	            			hitCounter = 0;
	            		}
	            	}
	            	else
	            	{
	            		System.out.println("Hit!");
	            	}
            	}
            }
            else
            {            	
            	player2.updateOppGrid(shipRow, shipCol, false, null);
            	player1.updateGrid(shipRow, shipCol, false);
            	System.out.println("Miss!");
            }
            System.out.println();
            System.out.println("Player 1 Board Status:");
            player1.printGrid();
        }
        
        if(player1.hasWon())
        	System.out.println("Player 1 wins!");
        else
        	System.out.println("AI wins!");
        continuer.close();
    }
    
    private static void guess()
    {
    	String inputString = input.nextLine().trim();
    	while(!validGuessInput(inputString))
    	{
    		System.out.println("Invalid input, please re-enter.");
    		System.out.print("Enter, in one line, separated by spaces, row (A - " 
                    + Grid.LETTERS.charAt(Grid.NUM_ROWS - 1) + ")"
                    + " and column (1 - " + (Grid.NUM_COLS)
                    + ") for a guess: ");
    	    inputString = input.nextLine().trim();
    	}
        row = inputString.toUpperCase().charAt(0);
        shipRow = Grid.LETTERS.indexOf(row);
        shipCol = Integer.parseInt(inputString.substring(2, 2+colDigits)) - 1;
        System.out.println();
    }
    
    private static boolean validGuessInput(String s)
    {
    	String[] arr = s.split(" ");
    	if(arr.length != 2)
    		return false;
    	if(!Character.isLetter(arr[0].charAt(0)) || arr[0].length() > 1)
    		return false;
    	for(int i = 0; i < arr[1].length(); i++)
    	{
    		if(!Character.isDigit(arr[1].charAt(i)))
    			return false;
    	}
    	colDigits = arr[1].length();
    	return true;
    }
    
    private static int AIGuess()
    {    	
    	if(hitCounter == 0)
    	{
    		shipRow = Randomizer.nextInt(0,Grid.NUM_ROWS - 1);
            shipCol = Randomizer.nextInt(0,Grid.NUM_COLS - 1);
            return 0;
    	}
    	if(hitCounter == 1)
    	{
    		originHitRow = hitRow;
    		originHitCol = hitCol;
    		shipRow = Randomizer.nextInt(hitRow - 1, hitRow + 1);
    		if(shipRow != hitRow)
        		shipCol = hitCol;
        	else
        	{
        		shipCol = Randomizer.nextInt(hitCol - 1, hitCol);
        		if(shipCol == hitCol)
        			shipCol++;
        	}
    		return 0;
    	}
    	else
    	{
    		if(directionValue == Ship.HORIZONTAL)
    		{
    			shipRow = hitRow;
    			shipCol = hitCol;
    			if(!validGuess(shipRow, shipCol + 1) && !validGuess(shipRow, shipCol - 1))
    			{
    				if(validGuess(shipRow, originHitCol - 1))
	    				shipCol = originHitCol - 1;
	    			else
	    				shipCol = originHitCol + 1;
	    			return 0;
    			}
    			else if(validGuess(shipRow, shipCol + 1))
    			{
    				shipCol++;
    				return 0;
    			}
    			else
    			{
    				shipCol--;
    				return 0;
    			}
    		}
    		else
    		{
    			shipRow = hitRow;
    			shipCol = hitCol;
    			if(!validGuess(shipRow + 1, shipCol) && !validGuess(shipRow - 1, shipCol))
    			{
    				if(validGuess(originHitRow - 1, shipCol))
	    				shipRow = originHitRow - 1;
	    			else
	    				shipRow = originHitRow + 1;
	    			return 0;
    			}
    			else if(validGuess(shipRow + 1, shipCol))
    			{
    				shipRow++;
    				return 0;
    			}
    			else
    			{
    				shipRow--;
    				return 0;
    			}
    		}
    	}
    }
    
    private static void placeShip()
    {
    	String inputString = input.nextLine().trim();
    	while(!validPlacementInput(inputString))
    	{
    		System.out.println("Invalid input, please re-enter.");
    		System.out.println("Enter, in one line, separated by spaces, row (A - " 
    	            + Grid.LETTERS.charAt(Grid.NUM_ROWS - 1) + ")"
    	            + " and column (1 - " + (Grid.NUM_COLS) + ")");
    	            System.out.print("and direction (for vertical enter v / for horizontal enter h) "
    	            + "for start of ship (length " + shipLength + "): ");
    	    inputString = input.nextLine().trim();
    	}
        row = inputString.toUpperCase().charAt(0);
        shipRow = Grid.LETTERS.indexOf(row);
        shipCol = Integer.parseInt(inputString.substring(2, 2+colDigits)) - 1;
        shipDirection = inputString.substring(3+colDigits);
        System.out.println();
    }
    
    private static boolean validPlacementInput(String s)
    {
    	String[] arr = s.split(" ");
    	if(arr.length != 3)
    		return false;
    	if(!Character.isLetter(arr[0].charAt(0)) || arr[0].length() > 1)
    		return false;
    	for(int i = 0; i < arr[1].length(); i++)
    	{
    		if(!Character.isDigit(arr[1].charAt(i)))
    			return false;
    	}
    	if(!Character.isLetter(arr[2].charAt(0)) || arr[2].length() > 1)
    		return false;
    	if(arr[2].charAt(0) != 'v' && arr[2].charAt(0) != 'h')
    		return false;
    	colDigits = arr[1].length();
    	return true;
    }
    
    private static void placeAIShip()
    {
    	directionValue = Randomizer.nextInt(Ship.HORIZONTAL,Ship.VERTICAL);
    	if(directionValue == Ship.HORIZONTAL)
    	{
    		shipDirection = "h";
    		shipRow = Randomizer.nextInt(0,Grid.NUM_ROWS - 1);
    		shipCol = Randomizer.nextInt(0,Grid.NUM_COLS - shipLength);
    	}
    	else
    	{
    		shipDirection = "v";
    		shipRow = Randomizer.nextInt(0,Grid.NUM_ROWS - shipLength);
            shipCol = Randomizer.nextInt(0,Grid.NUM_COLS - 1);
    	}
    }
    
    private static boolean validPlacement(int row, int col)
    {
    	Grid grid;
    	if(turn == AI)
    		grid = player2.getPlayerGrid();
    	else
    		grid = player1.getPlayerGrid();
    	
    	if(shipDirection.equals("v"))
    	{
            directionValue = Ship.VERTICAL;
            if((shipRow + shipLength > grid.numRows()) || shipCol >= grid.numCols())
            {
                if(turn.equals(USER))
                	System.out.print("Ship doesn't fit. Please reenter: ");
                return false;
            }
            for(int shipCheck = shipRow; shipCheck < shipRow + shipLength; shipCheck++)
                if(grid.hasShip(shipCheck, shipCol))
                {
                	if(turn.equals(USER))
                    	System.out.print("Ship already there. Please reenter: ");
                    return false;
                }
    	}
    	else
    	{
    		directionValue = Ship.HORIZONTAL;
    		if((shipCol + shipLength > grid.numCols()) || shipRow >= grid.numRows())
    		{
    			if(turn.equals(USER))
                	System.out.print("Ship doesn't fit. Please reenter: ");
                return false;
            }
    		for(int shipCheck = shipCol; shipCheck < shipCol + shipLength; shipCheck++)
                if(grid.hasShip(shipRow, shipCheck))
                {
                	if(turn.equals(USER))
                    	System.out.print("Ship already there. Please reenter: ");
                    return false;
                }
    	}
    	return true;
    }
    
    private static boolean validGuess(int row, int col)
    {
    	Grid grid;
    	if(turn == AI)
    		grid = player2.getOpponentGrid();
    	else
    		grid = player1.getOpponentGrid();
    	
    	if(row < 0 || col < 0)
    		return false;
    	if(row >= grid.numRows() || col >= grid.numCols())
    	{
    		if(turn.equals(USER))
    			System.out.println("This location is not on the board."
    					+ " Please guess a different location: ");
    		return false;
    	}
    	if(grid.alreadyGuessed(row, col))
    	{
    		if(turn.equals(USER))
    			System.out.println("You already guessed this location."
    					+ " Please guess a different location: ");
    		return false;
    	}
    	return true;
    }
    
    private static int getShipNum(int row, int col)
    {
    	Player p;
    	if(turn == USER)
    		p = player2;
    	else
    		p = player1;
    	
    	int sRow, sCol, direction, length;
    	int ships = p.getPlayerShips().length;
    	Ship s;
    	
    	for(int i = 0; i < ships; i++)
    	{
    		s = p.getPlayerShips()[i];
    		sRow = s.getRow();
            sCol = s.getCol();
            direction = s.getDirection();
            length = s.getLength();
            
            if(sRow == row && sCol == col)
            	return i;
            if(direction == Ship.HORIZONTAL)
            {
            	for(int j = sCol; j < sCol + length; j++)
            		if(sRow == row && j == col)
            			return i;
            }
            else
            {
            	for(int j = sRow; j < sRow + length; j++)
            		if(j == row && sCol == col)
            			return i;
            }
    	}
        return -1;
    }
    
    private static boolean newShip(int row, int col)
    {
    	int newShipNum = getShipNum(row, col);
    	return newShipNum != p1shipNum;
    }
    
    private static boolean isSunk(int shipNum)
    {
    	Grid grid;
    	Ship s;
    	
    	if(turn == USER)
    	{
    		grid = player2.getPlayerGrid();
    		s = player2.getPlayerShips()[shipNum];
    	}
    	else
    	{
    		grid = player1.getPlayerGrid();
    		s = player1.getPlayerShips()[shipNum];
    	}
    	
    	int row = s.getRow();
    	int col = s.getCol();
    	int direction = s.getDirection();
    	int length = s.getLength();
    	
    	if(direction == Ship.HORIZONTAL)
    	{
    		for(int i = col; i < col + length; i++)
    			if(!grid.getLocation(row, i).checkHit())
    				return false;
    	}
    	else
    	{
    		for(int i = row; i < row + length; i++)
    			if(!grid.getLocation(i, col).checkHit())
    				return false;
    	}
    	return true;
    }
}