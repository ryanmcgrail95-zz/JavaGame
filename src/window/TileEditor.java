package window;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cont.Log;
import fl.FileExt;
import functions.Math2D;
import functions.MathExt;
import gfx.G2D;
import gfx.GL;
import gfx.MultiTexture;
import gfx.RGBA;
import io.Controller;
import io.Mouse;

public class TileEditor {
	private int mapXNumber = 24, mapYNumber = 24;
	private int[][] tiles = new int[mapXNumber][mapYNumber];
	private boolean[][] collisions = new boolean[mapXNumber][mapYNumber];
	private int tileX, tileY;
	private int tileXNumber = 16, tileYNumber = 16, tileWidth = 8, tileHeight = 8;
	private MultiTexture tileSprite = new MultiTexture("Resources/Images/environment.png", 16,16);
	private String fileName = "Resources/Images/tmp.png";
	private int viewX = 0, viewY = 0;
	
	public TileEditor() {
		try {
			loadTiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getMapWidth() {
		return mapXNumber * tileWidth;
	}

	public int getMapHeight() {
		return mapYNumber * tileHeight;
	}

	public void setTile(int mapX, int mapY, int tileX, int tileY) {
		tiles[mapX][mapY] = tileY * tileXNumber + tileX;
	}
	
	public boolean[][] getCollisions() {
		return collisions;
	}
	
	public void loadTiles() throws IOException {
		loadTiles(fileName);
	}
	public void loadTiles(String fileName) throws IOException {
		BufferedImage inImage = ImageIO.read(FileExt.getFile(fileName));
		
		int imgW = inImage.getWidth(),
			imgH = inImage.getHeight();

		int rgba, rgbaArray[];
		for(int y = 0; y < imgH; y++)
			for(int x = 0; x < imgW; x++) {
				rgba = inImage.getRGB(x, y);
				rgbaArray = RGBA.convertInt2RGBA(rgba);
				
				tiles[x][y] = rgbaArray[0];
				collisions[x][y] = rgbaArray[1] > 128;
			}
				
		inImage.flush();		
		Log.println(Log.ID.IO, "Loaded tile map.");
	}

	public void saveTiles() throws IOException {
		saveTiles(fileName);		
	}
	public void saveTiles(String fileName) throws IOException {
		BufferedImage outImage = new BufferedImage(mapXNumber,mapYNumber, BufferedImage.TYPE_INT_ARGB);

		for(int y = 0; y < mapYNumber; y++)
			for(int x = 0; x < mapXNumber; x++)
				outImage.setRGB(x, y, RGBA.convertRGBA2Int(tiles[x][y], collisions[x][y] ? 255 : 0, 255, 255));
		
		ImageIO.write(outImage, "png", FileExt.getFile(fileName));
		
		outImage.flush();
		Log.println(Log.ID.IO, "Saved tile map.");
	}
	
	public void draw(float dX, float dY, float mouseX, float mouseY, float viewW, float viewH) {		
		float dir = Controller.getDirPressed();
		if(dir != -1) {
			int aX, aY;
			aX = (int) Math.round(Math2D.calcLenX(dir));
			aY = (int) -Math.round(Math2D.calcLenY(dir));
			System.out.println(aX + ", " + aY);
			tileX = (int) MathExt.contain(0,tileX + aX,mapXNumber);
			tileY = (int) MathExt.contain(0,tileY + aY,mapYNumber);
		}
		
		
		int mX = (int) (mouseX),
			mY = (int) (mouseY),
			b = 4,
			s = 2;
		
		if(mX < b)
			viewX -= s;
		if(mX > viewW-b)
			viewX += s;
		if(mY < b)
			viewY -= s;
		if(mY > viewH-b)
			viewY += s;

		int cX = (int) (dX - viewX), cY = (int) (dY - viewY);
		drawMap(cX, cY, true);

		drawHud(dX, dY);
		
		int mapX = (int) MathExt.contain(0, ((mX - cX)/tileWidth), mapXNumber-1),
			mapY = (int) MathExt.contain(0, ((mY - cY)/tileHeight), mapYNumber-1),
			tX = tileWidth * mapX, 
			tY = tileHeight * mapY;
				
		G2D.setColor(RGBA.RED);
		G2D.drawRectangle(cX + tX,cY + tY, tileWidth,tileHeight);
		
		if(Mouse.getLeftMouse())
			setTile(mapX,mapY, tileX,tileY);
		if(Mouse.getRightClick())
			collisions[mapX][mapY] = !collisions[mapX][mapY];
		
		G2D.setColor(RGBA.WHITE);
	}
	
	public void drawHud(float dX, float dY) {
		G2D.drawTexture(dX,dY,tileSprite.getTexture());
		G2D.setColor(RGBA.YELLOW);
		G2D.drawRectangle(tileWidth*tileX,tileHeight*tileY, 8,8);
		G2D.setColor(RGBA.WHITE);
	}
	
	public void drawMap(float dX, float dY, boolean showCollisions) {
		G2D.setColor(RGBA.WHITE);
		for(int y = 0; y < mapXNumber; y++)
			for(int x = 0; x < mapYNumber; x++) {
				boolean isGrass = (tiles[x][y] == 3*16);

				//if(isGrass)
				//	GL.enableShader("Grass");
					
				if(showCollisions && collisions[x][y])
					GL.setColorf(1f,0,0);
				G2D.drawTexture(dX + 8*x, dY + 8*y, 8,8, tileSprite, tiles[x][y]);
				G2D.setColor(RGBA.WHITE);

				GL.disableShaders();
			}
	}

	public int getMapXNumber() {
		return mapXNumber;
	}
	public int getMapYNumber() {
		return mapYNumber;
	}
}
