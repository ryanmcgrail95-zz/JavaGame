package games;

import functions.Math2D;
import functions.MathExt;

public class Snake {
	private Cell grid[][];
	private int xNum, yNum, snakeLength, snakeLengthMax, prevDir, snakeDir, snakeX, snakeY;
	public byte CELL_EMPTY = 0, CELL_PELLET;
	private int[] tempPoint = new int[2];
	
	public class Cell {
		public int snakeAmount;
		public int snakeDirection;
		public boolean hasItem;
		
		public String toString() {
			return "Cell[Snake]: " + snakeAmount + ", " + snakeDirection + ", " + hasItem;
		}
	}
	
	public Snake(int xNum, int yNum, int maxLen) {
		this.xNum = xNum;
		this.yNum = yNum;
		
		grid = new Cell[xNum][yNum];
		for(int y = 0; y < yNum; y++)
			for(int x = 0; x < xNum; x++)
				grid[x][y] = new Cell();
		
		snakeLength = 1;
		this.snakeLengthMax = maxLen;
		
		CELL_PELLET = (byte)(maxLen+1);
		
		reset();
	}
	
	public void update() {
				
		int aX,aY;
		aX = (int) Math.round(Math2D.calcLenX(snakeDir));
		aY = (int) -Math.round(Math2D.calcLenY(snakeDir));
						
		if(!checkOnBoard(snakeX,snakeY))
			reset();
		else {
			snakeX = (int) MathExt.wrap(0, snakeX+aX, xNum-1);
			snakeY = (int) MathExt.wrap(0, snakeY+aY, yNum-1);
			
				
			prevDir = snakeDir;
			incrementBoard();
					
			Cell cell = getCell(snakeX,snakeY);

			if(cell.hasItem) {
				cell.hasItem = false;
				increaseLength();
				createPellet();
			}
			
			if(cell.snakeAmount != 0)
				reset();
			else {			
				cell.snakeAmount = snakeLength;
				cell.snakeDirection = snakeDir;
			}
		}
	}
	
	
	private void incrementBoard() {
		Cell cell;
		for(int c = 0; c < xNum; c++)
			for(int r = 0; r < yNum; r++) {
				cell = getCell(c,r);
				cell.snakeAmount = Math.max(0, --cell.snakeAmount);
			}
				
	}

	public void increaseLength() {
		snakeLength = Math.min(snakeLengthMax, snakeLength+1);
	}
	
	public Cell getCell(int x, int y) {
		return grid[x][y];
	}
	public int getSnakeAmount(int x, int y) {
		return getCell(x,y).snakeAmount;
	}
	
	
	public void reset() {
		Cell cell;
		for(int c = 0; c < xNum; c++)
			for(int r = 0; r < yNum; r++) {
				cell = getCell(c,r);
				cell.snakeAmount = 0;
				cell.hasItem = false;
			}

		snakeLength = 1;
		
		createPellet();
		createSnake();
	}
	
	public void setSnakeDirection(int newDir) {
		if(Math.abs(Math2D.calcAngleDiff(newDir,prevDir)) != 180)
			snakeDir = newDir; 
	}
	
	public boolean checkOnBoard(int x, int y) {
		return (x >= 0 && x < xNum && y >= 0 && y < yNum);
	}
	
	private void generateBoardPoint() {
		System.out.println("Generating point...");
		
		Cell cell;
		int x,y;
		do {
			do {
				x = (int) MathExt.rndi(xNum);
				y = (int) MathExt.rndi(yNum);
			} while(!checkOnBoard(x,y));
			cell = getCell(x,y);
		} while(! (!cell.hasItem && cell.snakeAmount == 0) );
		
		tempPoint[0] = x;
		tempPoint[1] = y;
	}
	
	public void createSnake() {
		generateBoardPoint();
		grid[snakeX = tempPoint[0]][snakeY = tempPoint[1]].snakeAmount = snakeLength;
	}
	public void createPellet() {		
		generateBoardPoint();
		getCell(tempPoint[0],tempPoint[1]).hasItem = true;
	}	
	
	public int getSnakeLength()		{return snakeLength;}
	public int getSnakeLengthMax() 	{return snakeLengthMax;}
	public int getSnakeDirection() 	{return snakeDir;}
}
