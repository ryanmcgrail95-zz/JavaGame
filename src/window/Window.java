package window;

import functions.Math2D;
import functions.MathExt;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import io.Mouse;

import java.util.List;

import com.jogamp.opengl.util.awt.TextureRenderer;

import Datatypes.SortedList;

public class Window extends GUIFrame {	
	private static SortedList<Window> windowList = new SortedList<Window>();
	protected final static int SIDE_BORDER = 8, TOP_BORDER = 20;

	private String name;
	private boolean canDrag = false, isDragging;

	public Window(String name, float x, float y, float w, float h) {
		super(x, y, w, h);
		this.name = name;
		windowList.add(this);
	}
	
	public void close() {
		destroy();
		windowList.remove(this);
	}
	
	
	public static void ini() {
		StoreGUI.open();
	}
	
	public void draw() {
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
			if(Mouse.getLeftClick())
				close();
		}
		
		if(canDrag) {
			if(Mouse.checkRectangle(x(),y(),w()+2*SIDE_BORDER,TOP_BORDER)) {
				if(Mouse.getLeftClick())
					isDragging = true;
			}	
			if(isDragging) {
				move(Mouse.getDeltaX(), Mouse.getDeltaY());
				
				if(!Mouse.getLeftMouse())
					isDragging = false;
			}
		}
	}
	
	public static void renderAll() {
		for(int i = 0; i < windowList.size(); i++)
			windowList.get(i).render();
	}
	public static void drawAll() {
		for(int i = 0; i < windowList.size(); i++)
			windowList.get(i).draw();
		
		windowList.clean();
	}
	public static boolean isWindowOpen() {
		return (windowList.size() > 0);
	}
	
	public void move(float aX, float aY) {
		float x, y;		
		x = MathExt.contain(0,x()+aX,640-(w()+2*SIDE_BORDER));
		y = MathExt.contain(0,y()+aY,480-(h()+SIDE_BORDER+TOP_BORDER));
		
		x(x);
		y(y);
	}
	
	public float getScreenX() {return x()+SIDE_BORDER;}
	public float getScreenY() {return y()+TOP_BORDER;}
}
