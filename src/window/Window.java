package window;

import ds.lst.CleanList;
import functions.MathExt;
import gfx.G2D;
import gfx.GL;
import gfx.MultiTexture;
import gfx.RGBA;
import gfx.Sprite;
import io.Mouse;
import window.text.GUITextEditor;

public class Window extends GUIFrame {	
	private static CleanList<Window> windowList = new CleanList<Window>("Window");
	protected final static int SIDE_BORDER = 8, TOP_BORDER = 20;
	protected final static byte W_CLOSE = 0;

	private final static RGBA
		colorBorder = RGBA.createi(70,70,80),//RGBA.createi(80,73,55),
		colorTitle = RGBA.createi(240,240,255);//RGBA.createi(255,152,31);
	
	protected MultiTexture buttons = new MultiTexture("Resources/Images/window.png", 8,8);
	
	private String name;
	private boolean canDrag = false;
	protected boolean isDragging;
		
	private int mouseX,mouseY;

	public Window(String name, int x, int y, int w, int h, boolean canDrag) {
		super(x, y, w, h);
		this.name = name;
		windowList.add(this);
				
		this.canDrag = canDrag;
	}
	
	public void close() {
		destroy();
		windowList.remove(this);
	}
	
	protected boolean drawButton(float x, float y, float w, float h, Sprite sprite, float frame) {
		sprite.draw(x, y, w, h, frame);
		if(Mouse.checkRectangle(x + 1,y + 1,w-2,h-2)) {
			
			G2D.setAlpha(.6f);
			//G2D.fillRectangle(x,y,w,h);
			GL.forceColor(RGBA.WHITE);
			sprite.draw(x, y, w, h, frame);
			GL.unforceColor();
			G2D.setAlpha(1);
			
			Mouse.setFingerCursor();
			if(Mouse.getLeftClick()) {
				isDragging = false;
				return true;
			}
		}
		return false;
	}
	
	public static void ini() {
		GUITextEditor.createWindow(100,100);
		
		//GUIPaint.createWindow(100,100);
		
		//GUISnake.createWindow(100,100);
		//GUIPicross.createWindow(100,100);
		//GuiOverlay.createWindow(100,100);
		//StoreGUI.open("POOP");
	}
	
	public void drawBorder() {
		float bX,bY,bS;
		bS = 12;
		bX = x()+SIDE_BORDER+w() - 2*bS;
		bY = y()+TOP_BORDER/2-bS/2;
		
		// Minimize window.
		if(drawButton(bX,bY, bS,bS, buttons, 8)) {
		}

		// Close window.
		bX += bS;
		if(drawButton(bX,bY, bS,bS, buttons, 0)) {
			close();
		}
		
		int h, iH, sB = 1;
		h = (int) h();
		iH = getInternalHeight();
		
		if(iH > h) {
			int vY = getViewY();
			float vFY = vY / (iH);
			
			float y1, y2;
			y1 = h * vY / iH;
			y2 = h * (vY + h) / iH;
			
			GL.setColor(RGBA.GRAY_LIGHT);
			G2D.fillRectangle(x()+SIDE_BORDER+w()+sB,y()+TOP_BORDER+y1, SIDE_BORDER-2*sB,y2-y1);
			G2D.drawOutline(x()+SIDE_BORDER+w()+sB,y()+TOP_BORDER+y1, SIDE_BORDER-2*sB,y2-y1, true);
			GL.resetColor();
		}
	}
	
	public final byte draw() {
		super.draw(x()+SIDE_BORDER,y()+TOP_BORDER);

		GL.setColor(colorBorder);
		//GOGL.fillRectangle(x(),y(),w()+sideBorder*2,h()+topBorder+sideBorder);*/
		G2D.fillRectangle(x(),y(),SIDE_BORDER,h()+TOP_BORDER+SIDE_BORDER);
		G2D.fillRectangle(x()+SIDE_BORDER+w(),y(),SIDE_BORDER,h()+TOP_BORDER+SIDE_BORDER);
		G2D.fillRectangle(x(),y(),w()+2*SIDE_BORDER,TOP_BORDER);
		G2D.fillRectangle(x(),y()+TOP_BORDER+h(),w()+2*SIDE_BORDER,SIDE_BORDER);

		G2D.drawOutline(x()+SIDE_BORDER-1,y()+TOP_BORDER-1,w()+2,h()+2, false);
		G2D.drawOutline(x(),y(),w()+2*SIDE_BORDER,h()+SIDE_BORDER+TOP_BORDER, true);		
		/*G2D.drawLine(x()+SIDE_BORDER+w()+1,y()+TOP_BORDER,x()+SIDE_BORDER+w()+1,y()+TOP_BORDER+h());
		GL.setColor(RGBA.BLACK);
		GL.setAlpha(.5f);
		G2D.drawLine(x()+SIDE_BORDER,y()+TOP_BORDER,x()+SIDE_BORDER+w()+1,y()+TOP_BORDER);
		G2D.drawLine(x()+SIDE_BORDER,y()+TOP_BORDER,x()+SIDE_BORDER,y()+TOP_BORDER+h()+1);
		*/
		
		float tS = 1f;
		GL.setColor(colorTitle);
		G2D.drawStringCentered(x()+w()/2, y()+TOP_BORDER/2, tS,tS, name, true);

		
		GL.setColor(RGBA.BLACK);
		//G2D.drawRectangle(x(),y(), w()+2*SIDE_BORDER,h()+SIDE_BORDER+TOP_BORDER);
		GL.resetColor();

		drawBorder();
		
		if(isDestroyed())
			return 0;
		
		if(canDrag) {
			if(Mouse.checkRectangle(x(),y(),w()+2*SIDE_BORDER,TOP_BORDER)) {
				if(Mouse.getLeftClick()) {
					mouseX = (int) (Mouse.getMouseX()-x());
					mouseY = (int) (Mouse.getMouseY()-y());
					isDragging = true;
				}
			}	
			if(isDragging) {
				setPos(Mouse.getMouseX()-mouseX,Mouse.getMouseY()-mouseY);
				
				if(!Mouse.getLeftMouse())
					isDragging = false;
			}
		}
		
		return -1;
	}
	
	public static void renderAll() {
		for(int i = 0; i < windowList.size(); i++)
			windowList.get(i).render();
	}
	public static void drawAll() {
		for(Window w : windowList)
			w.draw();		
	}
	public static boolean isWindowOpen() {
		return (windowList.size() > 0);
	}
	
	public void move(float aX, float aY) {
		setPos(x()+aX,y()+aY);
	}
	public void setPos(float newX, float newY) {
		float	x = MathExt.contain(0,newX,640-(w()+2*SIDE_BORDER)),
				y = MathExt.contain(0,newY,480-(h()+SIDE_BORDER+TOP_BORDER));
		
		x(x);
		y(y);
	}
		
	
	public static boolean checkMouseAll() {
		for(Window w : windowList)
			if(w.checkMouse())
				return true;
		return false;
	}
	
	public boolean checkMouse() {
		return Mouse.checkRectangle(x(),y(), w()+2*SIDE_BORDER,h()+SIDE_BORDER+TOP_BORDER);
	}
	
	public float getScreenX() {return x()+SIDE_BORDER;}
	public float getScreenY() {return y()+TOP_BORDER;}

	public void setTitle(String name) {
		this.name = name;
	}
}
