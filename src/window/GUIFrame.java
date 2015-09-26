package window;

import functions.Math2D;
import gfx.FBO;
import gfx.GOGL;
import gfx.RGBA;
import io.Mouse;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.FBObject;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import datatypes.vec2;

public class GUIFrame extends GUIDrawable {
	private FBO fbo;
	private List<GUIDrawable> drawList;
	private List<GUIObject> nondrawList;
	private float scale = 1;

	
	public GUIFrame(int x, int y, int w, int h) {
		super(x,y, w, h);
		
		drawList = new ArrayList<GUIDrawable>();
		nondrawList = new ArrayList<GUIObject>();
		
		fbo = new FBO(GOGL.gl,w,h);
	}
	
	public void destroy() {
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
	
	public void render() {
				
		fbo.attach(GOGL.gl);
			GOGL.enableDepth();
			GOGL.clear(new RGBA(0,0,0,0));
			
			GOGL.setOrthoLayer(900);
			
			GOGL.setColor(RGBA.WHITE);
			for(GUIDrawable g : drawList)
				g.draw(0,0);
			
		fbo.detach(GOGL.gl);
	}
	
	public float w() {return super.w()*scale;}
	public float h() {return super.h()*scale;}
	
	public byte draw() {return draw(x(),y(),w(),h());}
	public byte draw(float x, float y) {return draw(x,y,w(),h());}
	public byte draw(float x, float y, float w, float h) {
		GOGL.setColor(RGBA.WHITE);
		GOGL.drawFBO(x,y, w,h, fbo);
		
		return -1;	
	}
	
	
	public float getScale() {
		return scale;
	}
	public vec2 getRelativeMouseCoords() {
		float x, y;
		x = 1/scale*(Mouse.getMouseX() - getScreenX());
		y = 1/scale*(Mouse.getMouseY() - getScreenY());

		return new vec2(x,y);
	}
	
	public boolean checkRectangle(float x, float y, float w, float h) {
		vec2 mousePos = getRelativeMouseCoords();
		return Math2D.checkRectangle(mousePos.x(),mousePos.y(), x,y, w, h);
	}
	
	
	public FBO getFBO() {
		return fbo;
	}
	public int getTexture() {
		return fbo.getTexture();
	}

	
	public boolean checkMouse() {
		return checkRectangle(0,0,w(),h());
	}
}
