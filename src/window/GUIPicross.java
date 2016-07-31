package window;

import cont.TextureController;
import ds.lst.CleanList;
import time.Stopwatch;
import time.Timer;
import window.GUIDrawable;
import io.Keyboard;
import io.Mouse;
import resource.sound.Sound;
import resource.sound.SoundBuffer;
import resource.sound.SoundSource;
import functions.Math2D;
import functions.MathExt;
import games.Picross;
import gfx.G2D;
import gfx.GL;
import gfx.MultiTexture;
import gfx.RGBA;

public class GUIPicross extends GUIDrawable {

	private final static RGBA GRAY_L = RGBA.createi(168,168,168),  GRAY_D = RGBA.createi(96,96,96);
	private Picross board;
	
	private final int
		BOARD_OFFSET_X = 60,
		BOARD_OFFSET_Y = 52;
		
	public final static byte CELL_EMPTY = Picross.CELL_EMPTY, CELL_FILLED = Picross.CELL_FILLED, CELL_MARKED = Picross.CELL_MARKED;
	private static int xNum = 15, yNum = 15, blinkTime = 30, holdTime = 20, size = 6;
	private byte holdState = 0;
	private Timer blinkTimer = new Timer(blinkTime), holdTimer = new Timer(3*holdTime,0);
	private SoundBuffer blockBreak, blockRefill, markEmpty, markFilled;
	
	
	private int prevIndX = -1, prevIndY = -1;
	
	private float mouseX, mouseY;
	
	private Timer soundTimer = new Timer(30);
	
	private Stopwatch watch;

	
	private String musicName;
	private int musicNameLength, musicNameScrollDir;
	private float musicNameScroll;
	private SoundBuffer musicBuffer;
	private SoundSource musicSource;
	private Timer musicNameScrollPauseTimer = new Timer(10);
	
	private CleanList<AnimatedBlock> animatedBlocks = new CleanList<AnimatedBlock>("Broken blocks");
	private static final MultiTexture font = new MultiTexture("Resources/Fonts/picross.png", 16,6);
	private static final MultiTexture sprites = new MultiTexture("Resources/Images/picross.png", 8,8);
	private static final MultiTexture timerSprite = new MultiTexture("Resources/Images/timer.png", 4,4);
	private static final MultiTexture numbers = new MultiTexture("Resources/Images/numbers.png", 4,4);
	
	private byte mouse[] = {
		1, 0, 0, 0, 0, 0,
		1, 1, 0, 0, 0, 0,
		1, 2, 0, 0, 0, 0,
		1, 2, 1, 0, 0, 0,
		1, 2, 2, 1, 0, 0,
		1, 2, 2, 0, 0, 0,
		1, 2, 1, 0, 0, 0,
		1, 0, 2, 0, 0, 0,
		0, 0, 0, 1, 0, 0,
	};
	private int mouseWidth = 6, mouseHeight = 9;
	
	private float shineTimer = 0, shineTimerMax = 7*50, cornerX, cornerY;

	private abstract class AnimatedBlock {
		int indX, indY, timer;
		
		public AnimatedBlock(int indX, int indY, int timer) {
			this.indX = indX;
			this.indY = indY;
			this.timer = timer;
		}

		public boolean isDone() {
			return timer < 0;
		}
		
		public void draw() {
			timer--;
		}
	}
	
	private class MarkedBlock extends AnimatedBlock {
		private final static float ANIMATION_SPEED = .1f;
		private float ind = 0;

		public MarkedBlock(int indX, int indY) {
			super(indX, indY, (int) Math.ceil(7/ANIMATION_SPEED));
		}
		
		@Override
		public void draw() {
			super.draw();

			float 
				blockX = cornerX-1 + indX*size, 
				blockY = cornerY-1 + indY*size;
			
			ind += ANIMATION_SPEED;
			
			GL.setColor(RGBA.WHITE);
			G2D.drawTexture(blockX,blockY,6,6, sprites, (int) (17+ind));
		}
	}
	
	private class BrokenBlock extends AnimatedBlock {
		Piece[] pieces;
		
		private class Piece {
			float relX, relY, velX, velY, liveTimerMax, liveTimer;
			
			public Piece(int r, int c) {
				relX = c;
				relY = r;
				
				float half = (size-1)/2,
					vel = 1.2f * MathExt.rndf(.8f,1),
					ang = Math2D.calcPtDir(half,half, relX,relY) + MathExt.rndf(-10,10);
				
				velX = Math2D.calcLenX(vel, ang);
				velY = Math2D.calcLenY(vel, ang);
				
				liveTimer = liveTimerMax = MathExt.rndf(20,30);
			}
			
			public void draw(float blockX, float blockY) {
				liveTimer = MathExt.contain(0, liveTimer-1, liveTimerMax);
				float frac = liveTimer/liveTimerMax;
						
				if(frac > .66)
					GL.setColor(RGBA.BLACK);
				else if(frac > .33)
					GL.setColor(GRAY_D);
				else
					GL.setColor(GRAY_L);
				
				relX += velX;
				relY += velY;
				
				velY += .1;

				G2D.fillPolygon(blockX + relX, blockY + relY, (float) Math.ceil(2*frac)/2, 4);
			}
		}
		
		public BrokenBlock(int indX, int indY) {
			super(indX, indY, 30);
			
			pieces = new Piece[size*size];
			for(int r = 0; r < size; r++)
				for(int c = 0; c < size; c++)
					pieces[r*size + c] = new Piece(r, c);
		}
		
		@Override
		public void draw() {
			super.draw();

			float 
				blockX = cornerX + indX*size, 
				blockY = cornerY + indY*size;
			
			for(Piece piece : pieces)
				piece.draw(blockX, blockY);
			GL.setColor(RGBA.WHITE);
		}
	}
	
	private class FixedBlock extends AnimatedBlock {
		Piece[] pieces;
		
		private class Piece {
			float homeX, homeY, relX, relY, velX, velY, liveTimerMax, liveTimer;
			
			public Piece(int r, int c) {
				homeX = relX = c;
				homeY = relY = r;
				
				float half = (size-1)/2,
					vel = 1.2f * MathExt.rndf(.8f,1),
					ang = Math2D.calcPtDir(half,half, relX,relY) + MathExt.rndf(-10,10);
				
				velX = Math2D.calcLenX(vel, ang);
				velY = Math2D.calcLenY(vel, ang);
				
				liveTimer = liveTimerMax = MathExt.rndf(10,20);
				
				relX += liveTimerMax*velX;
				
				for(int i = 0; i < liveTimerMax; i++) {
					relY += velY;
					velY += .1;
				}
			}
			
			public void draw(float blockX, float blockY) {
				liveTimer = MathExt.contain(0, liveTimer-1, liveTimerMax);
				float frac = liveTimer/liveTimerMax;
						
				if(frac > .4)
					GL.setColor(GRAY_D);
				else if(frac > .2)
					GL.setColor(GRAY_L);
				else
					GL.setColor(RGBA.WHITE);

				if(liveTimer > 0) {
					velY -= .1;
					relX -= velX;
					relY -= velY;								
				}
				else {
					relX = homeX;
					relY = homeY;
				}

				G2D.drawPixel(blockX-1 + relX, blockY + relY);
			}
		}
		
		public FixedBlock(int indX, int indY) {
			super(indX, indY, 20);
			
			pieces = new Piece[(size-1)*(size-1)];
			for(int r = 0; r < size-1; r++)
				for(int c = 0; c < size-1; c++)
					pieces[r*(size-1) + c] = new Piece(r, c);
		}
		
		@Override
		public void draw() {
			super.draw();

			float 
				blockX = cornerX + indX*size, 
				blockY = cornerY + indY*size;
			
			drawCell(blockX, blockY, CELL_FILLED);
			for(Piece piece : pieces)
				piece.draw(blockX, blockY);
			GL.setColor(RGBA.WHITE);
		}
	}
	
	public GUIPicross() {
		super(0,0,160,144);
		
		board = new Picross();
		board.load("Resources/Images/picross/puzzles/11a.jpg");
		
		String dir = "Resources/Sounds/FX/picross/";
		blockBreak = Sound.loadSound(dir + "puzzle/block_break.wav", .1f);
		blockRefill = Sound.loadSound(dir + "puzzle/block_refill.wav", .1f);
		markEmpty = Sound.loadSound(dir + "puzzle/mark_empty.wav", .1f);		
		markFilled = Sound.loadSound(dir + "puzzle/mark_filled.wav", .1f);		
		
		musicBuffer = Sound.loadTemp("/Resources/Sounds/Music/Ace Attorney/steelSamurai.ogg", .3f);
		musicSource = musicBuffer.play(true);
		musicName = musicBuffer.getName().toUpperCase();
		musicNameLength = musicName.length();
		musicNameScroll = 0;
		musicNameScrollDir = 1;
		
		watch = new Stopwatch();
		watch.start();
	}

	public void drawWaveform(float frameX, float frameY) {
		float dx, dy, w = 40, h = 12, px, py;
		dx = frameX + 12;
		dy = frameY + 26;
		
		dy += (8 - h)/2;
		/*
		tnow = musicSource.getByteOffset();
		
		GL.setColor(RGBA.RED);
		for(int i = 0; i < w; i++) {
			t = tnow + i;
			px = dx + i;
			py = dy + (1 - musicBuffer.getFilteredAmplitudeFraction(tnow))*h;
			
			G2D.drawPixel(px,py);
		}*/
		
		int s = 5000, start, end;
		start = musicSource.getByteOffset()-s;
		end = musicSource.getByteOffset()+s;
		GL.setColor(RGBA.WHITE);
		GL.drawWaveform(dx,dy, w,h,   start, end,  10,0, musicBuffer,true,false);
		
		int num = 8;
		
		
		if(musicNameScrollPauseTimer.checkOnce())
			musicNameScroll += musicNameScrollDir * .05f;
		if(musicNameScroll >= musicNameLength-num+1 && musicNameScrollDir == 1) {
			musicNameScrollPauseTimer.reset();
			musicNameScroll = musicNameLength-num+1;
			musicNameScrollDir = -1;
		}
		else if(musicNameScroll <= 0 && musicNameScrollDir == -1) {
			musicNameScrollPauseTimer.reset();
			musicNameScroll = 0;
			musicNameScrollDir = 1;
		}

		dx = frameX+10;
		dy = frameY+17;
				
		float 
			musicNameScrollFract, ax;
		
		musicNameScrollFract = musicNameScroll % 1;
		musicNameScrollFract = (musicNameScrollDir == 1) ? 1-musicNameScrollFract : musicNameScrollFract;
		
		ax = 3*musicNameScrollFract*musicNameScrollDir;
		
		int c;
		for(int i = 0; i < num; i++) {
			c = musicName.charAt((int) (Math.min(musicNameScroll,musicNameLength-num)+i));
			c -= '!';			
			G2D.drawTexture(dx+ax,dy, 6,8, font, c);
			
			dx += 5;
		}
	}
	
	@Override
	public byte draw(float frameX, float frameY) {
		
		if(Keyboard.checkDown('s'))
			board.solve();
				
		blinkTimer.check();
		
		shineTimer -= 4;
		if(shineTimer < 0)
			shineTimer = shineTimerMax;
		
		float dX,dY, cX,cY;
		dX = frameX+x();
		dY = frameY+y();
		
		cX = cornerX = dX + BOARD_OFFSET_X;
		cY = cornerY = dY + BOARD_OFFSET_Y;

		GL.disableBlending();
		
		GL.setColor(RGBA.WHITE);
		G2D.drawTexture(frameX+x(),frameY+y(), TextureController.getTexture("texPicross"));
		
		drawWaveform(frameX, frameY);

		cX = cornerX; //58
		cY = cornerY; //50
		
		// Print Solutions
		GL.setColor(RGBA.BLACK);
		int[][] hints = board.getHints();
		float s = .6f, gap = 6;
		for(int i = 0; i < 15; i++) {
			for(int p = 0; p < hints[i].length; p++)
				G2D.drawTexture(cX+size*i - 1,cY - 15 - gap*p, size,size, numbers, hints[i][p]);
			for(int p = 0; p < hints[i+15].length; p++)
				G2D.drawTexture(cX - 15 - gap*p,cY-1+size*i, size,size, numbers, hints[i+15][p]);
		}
		
		int time = (int) (watch.getCurrentMilli()/1000),
			secs = time % 60,
			mins = (time - secs)/60;

		int timeX = 14, timeY = 39;
		GL.setColor(RGBA.WHITE);
		G2D.drawTexture(timeX, timeY, 8,8, timerSprite, (mins - mins % 10)/10); timeX += 8;
		G2D.drawTexture(timeX, timeY, 8,8, timerSprite, mins % 10); timeX += 8;
		G2D.drawTexture(timeX, timeY, 8,8, timerSprite, 10); timeX += 4;
		G2D.drawTexture(timeX, timeY, 8,8, timerSprite, (secs - secs % 10)/10); timeX += 8;
		G2D.drawTexture(timeX, timeY, 8,8, timerSprite, secs % 10); timeX += 8;
		
		// Draw Cells
		if(!board.checkSolved()) {
			for(int r = 0; r < yNum; r++)
				for(int c = 0; c < xNum; c++) {
					cX = cornerX + size*c;
					cY = cornerY + size*r;
					
					if(board.get(c,r) != CELL_EMPTY)
						drawCell(cX,cY,board.get(c,r));
				}
			
			cX = cornerX;
			cY = cornerY;
			
			if(getParent().checkRectangle(cX,cY,89,89)) {
							
				float[] pos = getParent().getRelativeMouseCoords();	
				mouseX = pos[0];
				mouseY = pos[1];
				pos[0] = (pos[0] - cX)/size;
				pos[1] = (pos[1] - cY)/size;
				
				int
					mX = (int) pos[0],
					mY = (int) pos[1];
	
				
				boolean lC, rC;
				byte st;
				lC = Mouse.getLeftClick();
				rC = Mouse.getRightClick(); 
				st = board.get(mX,mY);
				if(lC || rC) {
					holdTimer.reset();
					holdState = (byte) ( lC ? (st == CELL_FILLED ? CELL_EMPTY : CELL_FILLED) : (st == CELL_MARKED ? CELL_EMPTY : CELL_MARKED) );				
				}
	
				cX = cornerX + size*mX;
				cY = cornerY + size*mY;
				
				GL.setColor(GRAY_D);
				drawTarget(mX,mY);
				
				boolean lM, rM;
				lM = Mouse.getLeftMouse();
				rM = Mouse.getRightMouse();
				
				
				pos = getParent().getRelativeMouseCoords();
				float scale = getParent().getScale();
				float mouseX, mouseY;
				mouseX = pos[0]*scale;
				mouseY = pos[1]*scale;
				int x1,y1,x2,y2;
				x1 = (int) ((mouseX-Mouse.getDeltaX())/scale);
				y1 = (int) ((mouseY-Mouse.getDeltaY())/scale);
				x2 = (int) (mouseX/scale);
				y2 = (int) (mouseY/scale);

				if(Keyboard.checkPressed('z')) {
					prevIndX = x2;
					prevIndY = y2;
				}
				else if(Keyboard.checkDown('z')) {
					if(prevIndX != -1 && prevIndY != -1) {
						int x, y;
						for(float i = 0; i < 1; i += .066) {
							x = (int) MathExt.interpolate(prevIndX, x2, i);
							y = (int) MathExt.interpolate(prevIndY, y2, i);
							
							drawTarget((int)((x-cornerX)/size),(int)((y-cornerY)/size));
						}

						if((lC || rC)) {
							byte state = board.get((int) ((prevIndX-cornerX)/size), (int)((prevIndY-cornerY)/size));
							state = ( lC ? (state == CELL_FILLED ? CELL_EMPTY : CELL_FILLED) : (state == CELL_MARKED ? CELL_EMPTY : CELL_MARKED) );
							
							if(setLineXY(prevIndX,prevIndY,x2,y2,state))
								playSound(state);													
						}
					}
				}
				else if(lC || rC) {
										
					boolean didChange;
					byte state;
					//if(holdTimer.get() % holdTime == 0)
					//	didChange = setLineXY(x1,y1,x2,y2,state = holdState);
					//else
					didChange = setXY(x2,y2, state = (byte) (lC ? ((st == CELL_FILLED) ? CELL_EMPTY : CELL_FILLED) : ((st == CELL_MARKED) ? CELL_EMPTY : CELL_MARKED)));
					if(didChange)
						playSound(state);
				}
				
			}
			else
				blinkTimer.reset();
		}
		else {
			for(int r = 0; r < yNum; r++)
				for(int c = 0; c < xNum; c++) {
					cX = cornerX + size*c;
					cY = cornerY + size*r;
					
					if(board.getSolution(c,r))
						drawCell(cX,cY,true,false,false);
				}
		}
		
		for(AnimatedBlock block : animatedBlocks) {
			if(block.isDone())
				animatedBlocks.remove();
			else
				block.draw();
		}
		
		GL.setColor(RGBA.WHITE);

		return 0;
	}

	private void drawTarget(int xInd, int yInd) {		
		float frac = blinkTimer.getFraction(), len = .35f;
		if(frac < len)
			GL.setColor(GRAY_D);
		else if(frac < .5)
			GL.setColor(GRAY_L);
		else if(frac < .5+len)
			GL.setColor(RGBA.WHITE);
		else
			GL.setColor(GRAY_L);
		G2D.drawRectangle(cornerX-1 + size*xInd,cornerY-1 + size*yInd,6,6);
	}
	
	
	public void drawCell(float x, float y, byte state) {
		drawCell(x,y,state == 1,state == 2,state == 1);
	}
	public void drawCell(float x, float y, boolean filled, boolean marked, boolean shadow) {
		GL.setColor(RGBA.WHITE);
		
		int shadowInd = shadow ? 1 : 0;
		float animInd, animInds[] = new float[] {shineTimer, shineTimer - 7*3, shineTimer - 7*10, shineTimer - 7*13};
		
		for(int i = 0; i < 4; i++) {
			animInds[i] -= (y/size)*7 - (x/size)*4;
			if(animInds[i] < 0 || animInds[i] >= 7)
				animInds[i] = 0;
		}

		animInd = MathExt.max(animInds);
		
		if(filled) {
			sprites.draw(x-1,y-1,size,size, shadowInd + animInd);
			
			int shX, shY;
			shX = (int) (mouseX - 2);
			shY = (int) (mouseY + 5);
			
			byte st;
			
			if(shX+mouseWidth > x-1 && shX < x-1+size && shY+mouseHeight > y-1 && shY < y-1+size) {
				for(int r = 0; r < mouseHeight; r++)
					if(shY+r > y && shY+r < y+size-1)
						for(int c = 0; c < mouseWidth; c++)
							if(shX+c+1 > x && shX+c < x+size-2)
								if((st = mouse[r*mouseWidth + c]) != 0) {
									
									if(st == 2)
										G2D.setColor(GRAY_L);
									if(st == 1)
										G2D.setColor(RGBA.WHITE);
									
									G2D.drawPixel(shX+c, shY+r);
								}
			}
		}
		else if(marked)
			sprites.draw(x-1,y-1,size,size, 8 + shadowInd);
		G2D.setColor(RGBA.WHITE);
	}
	
	
	private void playSound(byte state) {
		if(soundTimer.checkOnce()) {
			if(state == CELL_EMPTY) {
				blockRefill.play();
				markEmpty.play();
			}
			else if(state == CELL_FILLED) {
				blockBreak.play();
				markFilled.play();
			}
			
			soundTimer.reset();
		}
	}
	
	public boolean setXY(float x, float y, byte state) {
		float cX,cY;
		cX = x()+BOARD_OFFSET_X;
		cY = y()+BOARD_OFFSET_Y;
		
		int
			mX = (int) (x - cX)/size,
			mY = (int) (y - cY)/size;

		byte st;
		if((st = board.get(mX,mY)) != state) {
			if(board.set(mX,mY,state)) {
				if(state == CELL_FILLED)
					animatedBlocks.add(new BrokenBlock(mX, mY));
				else if(state == CELL_MARKED)
					animatedBlocks.add(new MarkedBlock(mX, mY));
				else if(st == CELL_FILLED)
					animatedBlocks.add(new FixedBlock(mX, mY));
				return true;
			}
		}
		
		return false;
	}
	public boolean setLineXY(float x1, float y1, float x2, float y2, byte state) {
		boolean didChange = false;
		float x,y;
		for(float i = 0; i < 1; i += .066) {
			x = (int) MathExt.interpolate(x1,x2, i);
			y = (int) MathExt.interpolate(y1,y2, i);
			
			didChange = setXY(x,y,state) || didChange;
		}
		
		return didChange;
	}

	@Override
	public void destroy() {
		blockRefill.destroy();
		blockBreak.destroy();
		markFilled.destroy();
		markEmpty.destroy();
		
		musicSource.destroy();
		musicBuffer.destroy();
	}

	@Override
	public boolean checkMouse() {
		return getParent().checkMouse();
	}
	
	
	public static void createWindow(int x, int y) {
		Window w = new Window("Picross",x,y,160,144,true);
		w.setScale(2);
		w.add(new GUIPicross());
	}
}
