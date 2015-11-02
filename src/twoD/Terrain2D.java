package twoD;

import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.RGBA;

import java.awt.image.BufferedImage;
import java.io.IOException;

import cont.ImageLoader;
import datatypes.BitArray;
import object.actor.Player;
import object.primitive.Collideable;
import object.primitive.Drawable;
import object.primitive.Physical;

public class Terrain2D extends Drawable {
	private static Terrain2D instance;
	private boolean[][][] blockGrid;
	private int[][] blockTypeGrid;
	private int width, height;
	private int gridSize = 16;
	
	private int B_NONE = 0, B_GRASS = 255;

	
	public Terrain2D(boolean[][][] grid, int width, int height) {
		super(false,false);
		blockGrid = grid;
		this.width = width;
		this.height = height;
	}

	public boolean isInside(float x1, float y1, float x2, float y2) {
		
		int bXInd1, bYInd1, bXInd2, bYInd2;
		bXInd1 = (int) Math.floor(x1/16)-1;
		bYInd1 = (int) Math.floor(y1/16)-1;
		bXInd2 = (int) Math.floor(x2/16)+2;
		bYInd2 = (int) Math.floor(y2/16)+2;
				
		for(int x = bXInd1; x < bXInd2; x++)
			for(int y = bYInd1; y < bYInd2; y++)
				if(blockGrid[x][y]) {
					if(x*gridSize < x2 && (x+1)*gridSize > x1 &&
								y*gridSize < y2 && (y+1)*gridSize > y1)
						return true;
				}
		
		return false;
	}
	
	public boolean attemptCollision(Actor2D other, float xP, float yP, float x, float y) {		
		float prevX, prevY, curX, curY;
		int numSteps = 5;
		float aX, aY, error = .05f;
		
		aX = (x-xP)/numSteps;
		aY = (y-yP)/numSteps;
		
		if(Math.abs(aX) < error && Math.abs(aY) < error) {
			float bX, bY, mX, mY, dX, dY;
			bX = (int) MathExt.floor(xP,gridSize);
			bY = (int) MathExt.floor(yP+gridSize/2,gridSize);
			mX = bX+gridSize/2;
			mY = bY+gridSize/2;
			
			dX = Math.min(Math.abs(xP-mX-8), Math.abs(xP-mX+8));
			dY = Math.abs(yP-mY);
			
			if(dX >= gridSize/2) {
				if(xP < mX)
					other.setX(bX-8);
				else
					other.setX(bX+16+8);
				other.setXVelocity(0);
			}
			if(dY >= 8) {
				other.setY(bY);
				other.setYVelocity(0);
			}
			
			return true;
		}

		
		float actW, actH;
		actW = actH = 16;
		
		prevX = xP;
		prevY = yP;

		for(int i = 1; i <= numSteps; i++) {
			curX = xP + i*aX;
			curY = yP + i*aY;
						
			if(isInside(curX-actW/2,curY,curX+actW/2,curY+actH))
				return attemptCollision(other, prevX,prevY,curX,curY);
						
			prevX = curX;
			prevY = curY;
		}
		
		return false;
	}
	
	public boolean collide(Actor2D other) {
		
		//other.setY(y);
		//other.setYVelocity(0);		
		
		float x,y, xP,yP, xV, yV;
		x = other.getX();
		y = other.getY();
		xP = other.getXPrevious();
		yP = other.getYPrevious();
		xV = other.getXVelocity();
		yV = other.getYVelocity();
		
		float x1, y1, x2, y2;
		x1 = x-8;
		y1 = y;
		x2 = x+8;
		y2 = y+16;
		
		float bX, bY;
		
		int bXInd1, bYInd1, bXInd2, bYInd2;
		bXInd1 = (int) Math.floor(x1/16)-1;
		bYInd1 = (int) Math.floor(y1/16)-1;
		bXInd2 = (int) Math.floor(x2/16)+2;
		bYInd2 = (int) Math.floor(y2/16)+2;
		
		boolean[] a;
		
		boolean onWall = false, onGround = false;
				
		for(int xx = bXInd1; xx < bXInd2; xx++)
			for(int yy = bYInd1; yy < bYInd2; yy++)
				if((a = blockGrid[xx][yy]) != null) {
					bX = xx*16;
					bY = (yy+1)*16;
					
					
					if(x+8 > bX && x-8 < bX+16) {
						if(!a[1])
							if(yP >= bY && bY >= y && yV < 0) {
								other.land(bY);
								onGround = true;
							}
						if(!a[3])
							if(yP+16 <= bY-16 && bY-16 <= y+16) {
								other.setY(bY-32);
								other.setYVelocity(0);
							}
					}
					if(y < bY && y+32 > bY) {
						// LEFT WALL
						if(!a[0])
							if(x-8 <= bX+16 && bX+16 <= xP-8 && xV <= 0) {
								other.setX(bX+16+8);
								other.setXVelocity(0);
								
								other.slideOnWall(Actor2D.LEFT);
								onWall = true;
							}
						// RIGHT WALL
						if(!a[2])
							if(x+8 >= bX && bX >= xP+8 && xV >= 0) {
								other.setX(bX-8);
								other.setXVelocity(0);
								
								other.slideOnWall(Actor2D.RIGHT);
								onWall = true;
							}
					}
				}
					
		if(!onWall)
			other.offWall(Actor2D.ST_IN_AIR);
		if(onGround)
			other.offWall(Actor2D.ST_ON_GROUND);
		
		return true;
		
		
		/*float x,y, xP,yP, xS, yS, m, amt = 5;
		x = other.getX();
		y = other.getY();
		xP = other.getXPrevious();
		yP = other.getYPrevious();
		
		xS = Math.signum(x-xP);
		yS = Math.signum(y-yP);
		
		/*if(xS == 0 && yS == 0)
			return false;
		else if(xS == 0)
			yP -= yS*amt;
		else if(yS == 0)
			xP -= xS*amt;
		else {
			m = (y-yP)/(x-xP);
			xP -= xS*amt;
			yP -= xS*amt*m;
		}
		
		return attemptCollision(other, xP,yP, x,y);*/
	}
	

	public static void ini() {
		instance = loadTerrain("Resources/Images/level.png");
	}
	public static Terrain2D loadTerrain(String fileName) {
		try {
			Player2D p = Player2D.getInstance();
			BufferedImage img;
			img = ImageLoader.load(fileName);
		
			int w, h;
			w = img.getWidth();
			h = img.getHeight();
			
			RGBA argb;
			int r,g,b,a;
			
			boolean[][] grid = new boolean[w][h];
			
			int rgb;
			for(int x = 0; x < w; x++)
				for(int y = h-1; y >= 0; y--) {
					rgb = img.getRGB(x, h-1 - y);
					
					argb = new RGBA(rgb);
						a = 0;
						r = argb.getRi();
						g = argb.getGi();
						b = argb.getBi();

					if(a != 0 || r != 0 || g  != 0 || b != 0)
						System.out.println(r + ", " + g + ", " + b + ", " + a);
						
					if(r > 128)
						grid[x][y] = true;
					if(g > 128) {
						p.setX(16*x);
						p.setY(16*y);
					}
				}
			
			img.flush();
			
			
			boolean[][][] blockGrid = new boolean[w][h][];
			boolean[] curBlock;
			for(int x = 0; x < w; x++)
				for(int y = 0; y < h; y++) {
					if(!grid[x][y])
						continue;
					
					curBlock = new boolean[4];
					int num = 0, aX, aY, xx, yy;
					for(int i = 0; i < 4; i++) {
						aX = (int) Math.round(Math2D.calcLenX(1,i*90));
						aY = (int) Math.round(Math2D.calcLenY(1,i*90));
									
						xx = x+aX;
						yy = y+aY;
						
						if(xx >= 0 && xx < w && yy >= 0 && yy < h) {
							curBlock[i] = grid[xx][yy];
							if(curBlock[i])
								num++;
						}
					}
					
					if(num < 4)
						blockGrid[x][y] = curBlock;
				}

			
			return new Terrain2D(blockGrid,w,h);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static Terrain2D getInstance() {
		return instance;
	}
	
	@Override
	public void draw() {
		GOGL.setOrtho();
		
		Player2D p = Player2D.getInstance();
		GOGL.transformClear();
		GOGL.transformTranslation(- (p.x-320), -(p.y-480*.25f), 0);
		
		float bX,bY;
		
		float pY = Player2D.getInstance().getY();
		
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				bY = (y+1)*16;

				if(blockGrid[x][y] != null) {
					if(blockGrid[x][y][2]) //if(pY < bY && pY+32 > bY)
						GOGL.setColor(RGBA.BLACK);
					else
						GOGL.setColor(RGBA.WHITE);
					
					GOGL.fillRectangle(x*16, y*16, 16, 16);
				}
			}
	}

	@Override
	public void add() {}
}