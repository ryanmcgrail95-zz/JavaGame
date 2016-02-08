package ds;

import functions.Math2D;
import functions.MathExt;

public class LinearGrid {
	private float[][] grid;
	private int width, height;
	
	public LinearGrid(int xNum, int yNum) {
		width = xNum;
		height = yNum;
		grid = new float[xNum][yNum];
	}
	public LinearGrid(float[][] grid) {
		width = grid.length;
		height = grid[0].length;
		
		this.grid = new float[width][height];
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				this.grid[x][y] = grid[x][y];
	}
	
	
	public void set(int x, int y, float v) {
		grid[x][y] = v;
	}
	public float get(float x, float y) {
		
		x = MathExt.contain(0, x, width-2);
		y = MathExt.contain(0, y, height-2);
		
		int gridX, gridY;
		gridX = (int) Math.floor((double) x);
		gridY = (int) Math.floor((double) y);

		//Then we look where on the cel the position is:

		float offsetX, offsetY;
		offsetX = x-gridX;
		offsetY = y-gridY;

		//Then we request the z-positions of the corners of the cel:

		float z1, z2, z3, z4;
		z1 = grid[gridX][gridY];
		z2 = grid[gridX+1][gridY];
		z3 = grid[gridX+1][gridY+1];
		z4 = grid[gridX][gridY+1];

		// And using these variables we calculate the z:

		if(offsetX > offsetY)
			return z1 - offsetX*(z1-z2) - offsetY*(z2-z3);
		else 
			return z1 - offsetX*(z4-z3) - offsetY*(z1-z4);
	}
	
	public void println() {
		int sum = 0;
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				sum += grid[x][y];
		
		System.out.println(sum);
	}
}
