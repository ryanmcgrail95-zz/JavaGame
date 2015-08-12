package games;

import functions.Math2D;
import functions.MathExt;

public class Snake {
	private byte grid[][];
	private int xNum, yNum, len, maxLen, prevDir, snakeDir, snakeX, snakeY;
	public byte CELL_EMPTY = 0, CELL_PELLET;
	
	public Snake(int xNum, int yNum, int maxLen) {
		this.xNum = xNum;
		this.yNum = yNum;
		grid = new byte[xNum][yNum];
		len = 1;
		this.maxLen = maxLen;
		
		CELL_PELLET = (byte)(maxLen+1);
		
		reset();
	}
	
	public void update() {
		
		int aX,aY;
		aX = (int) Math2D.calcLenX(snakeDir);
		aY = (int) -Math2D.calcLenY(snakeDir);
		
		
		if(checkOnBoard(snakeX+aX,snakeY+aY)) {
			snakeX += aX;
			snakeY += aY;
			
			prevDir = snakeDir;
			incrementBoard();
		
			byte st = get(snakeX,snakeY);
			if(st == CELL_PELLET) {
				increaseLength();
				createPellet();
			}
			
			set(snakeX,snakeY,len);
			
			if(st != CELL_EMPTY && st != CELL_PELLET)
				reset();
		}
		else if(!checkOnBoard(snakeX,snakeY))
			reset();
	}
	
	
	private void incrementBoard() {
		byte st;
		for(int c = 0; c < xNum; c++)
			for(int r = 0; r < yNum; r++) {
				st = get(c,r);
				if(st != CELL_EMPTY && st != CELL_PELLET)
					set(c,r,st-1);
			}
				
	}

	public void increaseLength() {
		len = Math.min(maxLen, len+1);
	}
	
	public void set(int x, int y, int state) {
		grid[x][y] = (byte) state;
	}
	public byte get(int x, int y) {
		return grid[x][y];
	}
	
	public void reset() {
		for(int c = 0; c < xNum; c++)
			for(int r = 0; r < yNum; r++)
				set(c,r,CELL_EMPTY);

		len = 1;
		createPellet();
		createSnake();
	}
	
	public void setDirection(int newDir) {
		if(Math.abs(Math2D.calcAngDiff(newDir,prevDir)) != 180)
			snakeDir = newDir;
	}
	
	public boolean checkOnBoard(int x, int y) {
		return (x >= 0 && x < xNum && y >= 0 && y < yNum);
	}
	
	public void createSnake() {
		int x,y;
		
		do {
			x = (int) MathExt.rnd(xNum);
			y = (int) MathExt.rnd(yNum);
		} while(!checkOnBoard(x,y));
		snakeX = x;
		snakeY = y;
		
		set(snakeX,snakeY,len);
	}
	public void createPellet() {
		int x,y;
		
		do {
			do {
				x = (int) MathExt.rnd(xNum);
				y = (int) MathExt.rnd(yNum);
			} while(!checkOnBoard(x,y));
		} while(get(x,y) != CELL_EMPTY);

		set(x,y,CELL_PELLET);
	}
	
	
	public int getLen() {
		return len;
	}
	public int getMaxLen() {
		return maxLen;
	}
}
