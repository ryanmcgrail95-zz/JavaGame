package games;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import cont.ImageLoader;
import cont.TextureController;
import datatypes.vec2;
import time.Timer;
import window.GUIDrawable;
import io.Mouse;
import functions.MathExt;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;

public class Picross {
	
	protected static int xNum = 15, yNum = 15;	
	public final static byte CELL_EMPTY = 0, CELL_FILLED = 1, CELL_MARKED = 2;
	protected byte[][] grid = new byte[xNum][yNum];
	protected boolean[][] solutionGrid = new boolean[xNum][yNum];
	protected boolean isSolved = false;	
	private int[] hints[] = new int[30][];
	
	public Picross() {
	}
	
	public void destroy() {}

	
	
	public void load(String name) {
		try {
			BufferedImage img = ImageLoader.load(name);
			// Fill in Solution Grid; White = Empty, Black = Filled
			for(int r = 0; r < yNum; r++)
				for(int c = 0; c < xNum; c++)
					solutionGrid[c][r] = (new RGBA(img.getRGB(c,r))).getValue() < .5f;

			// Come up with Solutions
			generateHints();
		} catch (IOException e) {}
	}
	
	
	public boolean checkSolved() {
		if(isSolved)
			return true;
		
		for(int r = 0; r < yNum; r++)
			for(int c = 0; c < xNum; c++)
				if(solutionGrid[c][r] != (grid[c][r] == 1))
					return false;
		
		return isSolved = true;
	}
	
	
	private void generateHints() {
		int curLenR = 0, curLenC = 0, siR, siC;
		List<Integer> solR = new ArrayList<Integer>();
		List<Integer> solC = new ArrayList<Integer>();
		for(int i = 0; i < 15; i++) {
			curLenC = curLenR = 0;
			
			for(int p = 0; p < 15; p++) {
				if(solutionGrid[i][p])
					curLenC++;
				else if(curLenC > 0) {
					solC.add(curLenC);
					curLenC = 0;
				}
				if(solutionGrid[p][i])
					curLenR++;
				else if(curLenR > 0) {
					solR.add(curLenR);
					curLenR = 0;
				}
			}
			if(curLenC > 0)	solC.add(curLenC);
			if(curLenR > 0)	solR.add(curLenR);
			
			hints[i] = new int[solC.size()];
			hints[i+15] = new int[solR.size()];
			
			siC = solC.size();
			siR = solR.size();
			for(int k = 0; k < siC; k++)
				hints[i][siC-1-k] = solC.get(k);
			for(int k = 0; k < siR; k++)
				hints[i+15][siR-1-k] = solR.get(k);
			
			solR.clear();
			solC.clear();
		}
	}
	
	public void set(int x, int y, byte state) {
		if(0 <= x && x < 15 && 0 <= y && y < 15)
			grid[x][y] = state;
	}
	public byte get(int x, int y) {
		if(0 <= x && x < 15 && 0 <= y && y < 15)
			return grid[x][y];
		else
			return -1;
	}
	public boolean check(int x, int y, byte state) {
		return get(x,y) == state;
	}

	public boolean getSolution(int c, int r) {
		return solutionGrid[c][r];
	}

	public int[][] getHints() {
		return hints;
	}
}
