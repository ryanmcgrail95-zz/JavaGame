package window;

import functions.Math2D;
import functions.MathExt;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import io.Mouse;

import java.util.List;

import com.jogamp.opengl.util.awt.TextureRenderer;

import ds.lst.CleanList;

public class Window extends GUIFrame {	
	private static CleanList<Window> windowList = new CleanList<Window>("Window");
	protected final static int SIDE_BORDER = 8, TOP_BORDER = 20;
	protected final static byte W_CLOSE = 0;

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
		//new SnakeWindow(100,100);
		//new PicrossWindow(100,100);
		//StoreGUI.open("POOP");
	}
	

	
	public byte draw() {
		super.draw(x()+SIDE_BORDER,y()+TOP_BORDER);

		GOGL.setColor(new RGBA(80,73,55));
		//GOGL.fillRectangle(x(),y(),w()+sideBorder*2,h()+topBorder+sideBorder);*/
		GOGL.fillRectangle(x(),y(),SIDE_BORDER,h()+TOP_BORDER+SIDE_BORDER);
		GOGL.fillRectangle(x()+SIDE_BORDER+w(),y(),SIDE_BORDER,h()+TOP_BORDER+SIDE_BORDER);
		GOGL.fillRectangle(x(),y(),w()+2*SIDE_BORDER,TOP_BORDER);
		GOGL.fillRectangle(x(),y()+TOP_BORDER+h(),w()+2*SIDE_BORDER,SIDE_BORDER);
		
		
		float tS = 1f;
		GOGL.setColor(new RGBA(255,152,31));
		GLText.drawStringCentered(x()+w()/2, y()+TOP_BORDER/2, tS,tS, name, true);
		
		GOGL.setColor(RGBA.BLACK);
		GOGL.drawRectangle(x()+SIDE_BORDER,y()+TOP_BORDER, w(),h());
		GOGL.drawRectangle(x(),y(), w()+2*SIDE_BORDER,h()+SIDE_BORDER+TOP_BORDER);
		
		float bX,bY,bS;
		bX = x()+w()-12;
		bY = y()+2;
		bS = 16;
		GOGL.drawRectangle(bX,bY,bS,bS);
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
