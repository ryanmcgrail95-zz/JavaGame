package window;

import functions.Math2D;
import functions.MathExt;
import gfx.FBO;
import gfx.GL;
import gfx.G2D;
import gfx.RGBA;
import io.Controller;
import io.Keyboard;
import io.Mouse;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import ds.vec2;

public class GUIFrame extends GUIDrawable {
	private FBO fbo;
	private List<GUIDrawable> drawList;
	private List<GUIObject> nondrawList;
	private float scale = 1;
	private float[] mouseCoords = new float[2];

	private GUIDrawable selectedObject = null;
	
	private int bgColor;
	
	public GUIFrame(int x, int y, int w, int h) {
		super(x,y, w, h);
		
		drawList = new ArrayList<GUIDrawable>();
		nondrawList = new ArrayList<GUIObject>();
		
		fbo = new FBO(GL.getGL(),w,h);
	}
	
	public void destroy() {
		for(GUIDrawable obj : drawList)
			obj.destroy();
		for(GUIObject obj : nondrawList)
			obj.destroy();		
		drawList.clear();
		nondrawList.clear();
		//super.destroy();
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void add(GUIObject obj) {
		obj.setParent(this);
		if(obj.getDrawable())
			drawList.add((GUIDrawable) obj);
		else
			nondrawList.add(obj);
	}
	
	public void setBGi(int r, int g, int b) {
		bgColor = RGBA.convertRGBA2Int(r,g,b,255);
	}
	
	public void render() {
				
		controlSelectionCursor();
		
		GL.attach(fbo);
			GL.setHidden(true);
			GL.clear(bgColor);
			
			GL.setOrthoLayer(900);
			
			GL.setColor(RGBA.WHITE);
			for(GUIDrawable g : drawList)
				g.draw(0,0);
			
		GL.detach(fbo);
	}
	
	public float w() {return super.w()*scale;}
	public float h() {return super.h()*scale;}
	
	public byte draw() {return draw(x(),y(),w(),h());}
	public byte draw(float x, float y) {return draw(x,y,w(),h());}
	public byte draw(float x, float y, float w, float h) {
		GL.setColor(RGBA.WHITE);
		GL.drawFBO(x,y, w,h, fbo);
		
		return -1;	
	}
	
	
	public float getScale() {
		return scale;
	}
	public float[] getRelativeMouseCoords() {
		mouseCoords[0] = 1/scale*(Mouse.getMouseX() - getScreenX());
		mouseCoords[1] = 1/scale*(Mouse.getMouseY() - getScreenY());
		return mouseCoords;
	}
	
	public boolean checkRectangle(float x, float y, float w, float h) {
		getRelativeMouseCoords();
		return Math2D.checkRectangle(mouseCoords[0],mouseCoords[1], x,y, w, h);
	}
	
	
	public FBO getFBO() {return fbo;}
	public int getTexture() {
		return fbo.getTexture();
	}

	
	public boolean checkMouse() {
		return checkRectangle(0,0,w(),h());
	}

	
	public void controlSelectionCursor() {
		float dir = Controller.getDirPressed();
		
		if(dir == -1)
			return;
		
		float x,y;
		if(selectedObject != null) {
			x = selectedObject.centerX();
			y = selectedObject.centerY();
		}
		else {
			x = 0;
			y = 0;
		}
		
		GUIDrawable other = findNearestOther(x,y,dir);
		if(other != null)
			selectedObject = other;
	}
	
	public GUIDrawable getSelected() {
		return selectedObject;
	}
	private GUIDrawable findNearestOther(float x, float y, float dir) {
		float cX, cY, dX, dY, vX, vY, norm, curDis, minDis = -1, priority, minPri = -1, k = 0;
		GUIDrawable closest = null;
		
		vX = Math2D.calcLenX(dir);
		vY = Math2D.calcLenY(dir);
		
		//System.out.println("-----------");
		
		for(GUIDrawable d : drawList) {
			if(d == selectedObject)
				continue;
				
			cX = d.centerX();
			cY = d.centerY();
			
			dX = x - cX;
			dY = y - cY;
			norm = Math2D.calcLen(dX,dY);
				dX /= norm;
				dY /= norm;
			
			priority = Math.abs(dX*vX + dY*vY);
			
			//System.out.println("" + k++ + " " + priority);
									
			if(minPri == -1 || priority > minPri) {
				minPri = priority;
				curDis = (1 - priority) * Math2D.calcPtDis(x,y,cX,cY);
				//System.out.println("\t" + curDis);
				
				if(minDis == -1 || curDis < minDis) {
					minDis = curDis;
					closest = d;
					
					//System.out.println("USING " + (k-1) + "!");
				}
			}
		}
		
		return closest;
	}
}
