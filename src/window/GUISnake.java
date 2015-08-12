package window;

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
import io.Controller;
import io.IO;
import io.Mouse;
import functions.MathExt;
import games.Picross;
import games.Snake;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;

public class GUISnake extends GUIDrawable {

	private final static RGBA GRAY_L = new RGBA(168,168,168),  GRAY_D = new RGBA(96,96,96);

	private Snake board;
	public final static byte CELL_EMPTY = Picross.CELL_EMPTY, CELL_FILLED = Picross.CELL_FILLED, CELL_MARKED = Picross.CELL_MARKED;
	private int xNum, yNum;
	
	private boolean hasStarted = false;
	private Timer moveTimer = new Timer(8);
	
	public GUISnake(int xNum, int yNum) {
		super(0,0,xNum,yNum);
		
		this.xNum = xNum;
		this.yNum = yNum;
		
		board = new Snake(xNum,yNum,xNum+yNum);
	}

	public byte draw(float frameX, float frameY) {
			
		int dir = (int) Controller.getDPadDir();
		
		if(dir != -1)
			board.setDirection((int) MathExt.snap(dir,90));
		if(moveTimer.check())
			board.update();
		
		
		moveTimer.setMax(16 - 14*1f*board.getLen()/board.getMaxLen());
		
		GOGL.setColor(RGBA.WHITE);
		GOGL.fillRectangle(0,0, xNum,yNum);

		
		// Draw Cells
		GOGL.setColor(RGBA.BLACK);
		for(int r = 0; r < yNum; r++)
			for(int c = 0; c < xNum; c++)
				if(board.get(c,r) != CELL_EMPTY)
					GOGL.drawPixel(1+c,1+r);

		GOGL.setColor(RGBA.WHITE);
		
		return 0;
	}
	
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
