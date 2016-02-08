package window;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;

import cont.ImageLoader;
import cont.TextureController;
import ds.vec2;
import time.Timer;
import window.GUIDrawable;
import io.Mouse;
import functions.MathExt;
import games.Picross;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;

public class GUIPicross extends GUIDrawable {

	private final static RGBA GRAY_L = new RGBA(168,168,168),  GRAY_D = new RGBA(96,96,96);
	private Picross board;
	public final static byte CELL_EMPTY = Picross.CELL_EMPTY, CELL_FILLED = Picross.CELL_FILLED, CELL_MARKED = Picross.CELL_MARKED;
	private static int xNum = 15, yNum = 15, blinkTime = 30, holdTime = 20, size = 6;
	private byte holdState = 0;
	private Timer blinkTimer = new Timer(blinkTime), holdTimer = new Timer(3*holdTime,0);
	
	public GUIPicross() {
		super(0,0,160,144);
		
		board = new Picross();
		board.load("Resources/Images/picross1.png");
	}

	public byte draw(float frameX, float frameY) {
				
		
		
		blinkTimer.check();
		
		float dX,dY, cX,cY;
		dX = frameX+x();
		dY = frameY+y();

		GOGL.disableBlending();
		
		GOGL.setColor(RGBA.WHITE);
		GOGL.drawTexture(frameX+x()-1,frameY+y()-1, TextureController.getTexture("texPicross"));
		
		
		cX = dX + 58;
		cY = dY + 50;
		
		
		
		// Print Solutions
		int[][] hints = board.getHints();
		float s = .4f, gap = 6;
		for(int i = 0; i < 15; i++) {
			for(int p = 0; p < hints[i].length; p++)
				GLText.drawString(cX+size*i,cY - 15 - gap*p, s,s, ""+hints[i][p]);
			for(int p = 0; p < hints[i+15].length; p++)
				GLText.drawString(cX - 15 - gap*p,cY+size*i, s,s, ""+hints[i+15][p]);
		}

		
		// Draw Cells
		if(!board.checkSolved()) {
			for(int r = 0; r < yNum; r++)
				for(int c = 0; c < xNum; c++) {
					cX = dX+59 + size*c;
					cY = dY+51 + size*r;
					
					if(board.get(c,r) != CELL_EMPTY)
						drawCell(cX,cY,board.get(c,r));
				}
			
			cX = dX+59;
			cY = dY+51;
			
			if(getParent().checkRectangle(cX,cY,89,89)) {
							
				vec2 pos = getParent().getRelativeMouseCoords();
				
				pos.sube( (new vec2(cX,cY)) ).dive(size);
				
				int mX,mY;
				mX = (int) pos.x();
				mY = (int) pos.y();
	
				
				boolean lC, rC;
				byte st;
				lC = Mouse.getLeftClick();
				rC = Mouse.getRightClick(); 
				st = board.get(mX,mY);
				if(lC || rC) {
					holdTimer.reset();
					holdState = (byte) ( lC ? (st == CELL_FILLED ? CELL_EMPTY : CELL_FILLED) : (st == CELL_MARKED ? CELL_EMPTY : CELL_MARKED) );
				}
	
				
				cX = dX+59 + size*mX;
				cY = dY+51 + size*mY;
				
				GOGL.setColor(GRAY_D);
				GOGL.drawRectangle(cX-1,cY-1,6,6);
				
				if(blinkTimer.get() > blinkTime/2)
					drawCell(cX,cY,true,st == 2,st == 1);
				else
					drawCell(cX,cY,false,st == 2,st == 1);
				
				boolean lM, rM;
				lM = Mouse.getLeftMouse();
				rM = Mouse.getRightMouse();
				
				
				pos = getParent().getRelativeMouseCoords();
				float scale = getParent().getScale();
				float mouseX, mouseY;
				mouseX = pos.x()*scale;
				mouseY = pos.y()*scale;
				int x1,y1,x2,y2;
				x1 = (int) ((mouseX-Mouse.getDeltaX())/scale);
				y1 = (int) ((mouseY-Mouse.getDeltaY())/scale);
				x2 = (int) (mouseX/scale);
				y2 = (int) (mouseY/scale);
				
				System.out.println(x2 + ", " + y2);
		
				if((lM || rM))
					if(holdTimer.get() % holdTime == 0)
						setLineXY(x1,y1,x2,y2,holdState);
					else
						board.set(mX,mY, (byte) (lM ? ((st == CELL_FILLED) ? CELL_EMPTY : CELL_FILLED) : ((st == CELL_MARKED) ? CELL_EMPTY : CELL_MARKED)));
			}
			else
				blinkTimer.reset();
		}
		else {
			for(int r = 0; r < yNum; r++)
				for(int c = 0; c < xNum; c++) {
					cX = dX+59 + size*c;
					cY = dY+51 + size*r;
					
					if(board.getSolution(c,r))
						drawCell(cX,cY,true,false,false);
				}
		}
		
		GOGL.setColor(RGBA.WHITE);

		return 0;
	}

	
	public void drawCell(float x, float y, byte state) {
		drawCell(x,y,state == 1,state == 2,state == 1);
	}
	public void drawCell(float x, float y, boolean filled, boolean marked, boolean shadow) {
		GOGL.setColor(filled ? GRAY_D : RGBA.WHITE);
		GOGL.fillRectangle(x-1,y-1,5,5);
		
		if(marked) {
			GOGL.setColor(filled ? RGBA.BLACK : GRAY_D);
			GOGL.drawPixel(x+1,y+1);
			GOGL.drawPixel(x+3,y+1);
			GOGL.drawPixel(x+2,y+2);
			GOGL.drawPixel(x+1,y+3);
			GOGL.drawPixel(x+3,y+3);
		}
		if(shadow) {
			GOGL.setColor(RGBA.BLACK);
			GOGL.drawLine(x-1,y,x+4,y);
			GOGL.drawLine(x,y,x,y+4);
		}
	}
	
	
	public void setXY(float x, float y, byte state) {
		float cX,cY;
		cX = x()+59;
		cY = y()+51;
		
		vec2 pos = new vec2(x,y);
		pos.sube( (new vec2(cX,cY)) ).dive(size);
		
		int mX,mY;
		mX = (int) pos.x();
		mY = (int) pos.y();

		board.set(mX,mY,state);
	}
	public void setLineXY(float x1, float y1, float x2, float y2, byte state) {
		float x,y;
		for(float i = 0; i < 1; i += .1) {
			x = (int) MathExt.interpolate(x1,x2, i);
			y = (int) MathExt.interpolate(y1,y2, i);
			
			setXY(x,y,state);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkMouse() {
		return getParent().checkMouse();
	}
}
