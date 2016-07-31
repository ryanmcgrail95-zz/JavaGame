package window;

import functions.Math2D;
import functions.MathExt;
import gfx.G2D;
import gfx.GL;
import gfx.RGBA;
import io.Mouse;

import java.util.List;

import com.jogamp.opengl.util.awt.TextureRenderer;

import ds.lst.CleanList;

public class Window extends GUIFrame {	
	private static CleanList<Window> windowList = new CleanList<Window>("Window");
	protected final static int SIDE_BORDER = 8, TOP_BORDER = 20;
	protected final static byte W_CLOSE = 0;

	private final static RGBA
		colorBorder = RGBA.createi(70,70,80),//RGBA.createi(80,73,55),
		colorTitle = RGBA.createi(240,240,255);//RGBA.createi(255,152,31);
	
	private String name;
	private boolean canDrag = false, isDragging;
	
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
	
	
	public static void ini() {
		new SnakeWindow(100,100);
		//GUIPicross.createWindow(100,100);
		//GuiOverlay.createWindow(100,100);
		//StoreGUI.open("POOP");
	}
	

	
	public byte draw() {
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
		
		float bX,bY,bS;
		bS = 12;
		bX = x()+SIDE_BORDER+w() - bS;
		bY = y()+TOP_BORDER/2-bS/2;
		GL.setColorf(1,0,0);
		G2D.fillRectangle(bX,bY,bS,bS);
		G2D.drawOutline(bX,bY,bS,bS, true);
		if(Mouse.checkRectangle(bX,bY, bS,bS)) {
			Mouse.setFingerCursor();
			if(Mouse.getLeftClick()) {
				close();
				return W_CLOSE;
			}
		}
		
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
}
