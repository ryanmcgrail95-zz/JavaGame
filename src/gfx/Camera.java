package gfx;

import io.Mouse;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.glu.GLU;

import datatypes.vec3;
import datatypes.lists.CleanList;
import functions.Math2D;
import functions.Math3D;
import object.actor.Actor;
import object.primitive.Drawable;
import object.primitive.Updatable;


public class Camera extends Updatable {
	//CAMERA FOCUS
	public static final byte PR_PERSPECTIVE = 0, PR_ORTHOGRAPHIC = 1, PR_ORTHOPERSPECTIVE = 2;
	private static final byte CF_STATIC = 0, CF_OBJECT = 1;
	private static CleanList<Camera> camList = new CleanList<Camera>();
	private byte camFocusType, projType;
	private List<Actor> camFocusList = new ArrayList<Actor>();
	private float camX, camY, camZ, toX, toY, toZ, camDir;
	private float dis, dir, dirZ, smoothing;
	private boolean isLocked, isEnabled = true;
	private FBO fbo;

	
	private float smoothOnceFrac;
	
	private vec3 pos = new vec3(), toPos = new vec3();
	private float fieldOfView = 45, viewFar = 3000, viewNear = 1, whRatio;
	private float focusAddX, focusAddY, focusAddZ;
	
	
	public Camera(byte projType, FBO fbo) {
		super();
		
		whRatio = 1f*fbo.getWidth()/fbo.getHeight();
		
		this.projType = projType;
		setFBO(fbo);
		camList.add(this);
	}
	public Camera(byte projType, int resWidth, int resHeight) {
		super();
		
		whRatio = 1f*resWidth/resHeight;

		this.projType = projType;
		setFBO(new FBO(GOGL.gl, resWidth, resHeight));
		camList.add(this);
	}
	
	public void destroy() {super.destroy();}
	
	public void setProjection(float cX, float cY, float cZ, float tX, float tY, float tZ) {
		if(isLocked)
			return;
		
		camFocusType = CF_STATIC;
		
		camX = cX; camY = cY; camZ = cZ;
		toX = tX; toY = tY; toZ = tZ;
		
		//camDir = Math2D.calcPtDir(camX, camY, toX, toY);
	}

	
	public vec3 averageActors() {
		// INEFFICIENT
		int num = camFocusList.size();
		float x = 0, y = 0, z = 0;
		
		for(Actor a : camFocusList) {
			x += a.getX();
			y += a.getY();
			z += (a.getZ() + 48);
		}
		
		return new vec3(x/num,y/num,z/num);
	}
	

	public float getX()			{return pos.x();}
	public float getY()			{return pos.y();}
	public float getZ() 		{return pos.z();}
	public float getToX()		{return toPos.x();}
	public float getToY()		{return toPos.y();}
	public float getToZ() 		{return toPos.z();}
	public float getDirection()	{return camDir;}
	
	public vec3 getNormal() {
		return (vec3) toPos.sub(pos);
	}
	public vec3 getShaderNormal() {
		return new vec3(0, -Math2D.calcPtDir(0,camZ,Math2D.calcPtDis(camX,camY,toX,toY),toZ)/180.f*3.14159f, -Math2D.calcPtDir(camX,camY,toX,toY)/180.f*3.14159f);
	}
	
	public void focus(float dis, float dir, float dirZ, Actor... actors) {
		if(isLocked)
			return;
			
		this.dis = dis;
		this.dir = dir;
		this.dirZ = dirZ;
		
		camFocusType = CF_OBJECT;
	
		camFocusList.clear();
		for(Actor a : actors)
			camFocusList.add(a);
	}

	public float calcPerpDistance(float x, float y) {
		return Math.abs(Math2D.calcProjDis(x-getX(), y-getY(), Math2D.calcLenX(1,camDir+90),Math2D.calcLenY(1,camDir+90)));
	}
	public float calcParaDistance(float x, float y) {
		return Math.abs(Math2D.calcProjDis(x-getX(), y-getY(), Math2D.calcLenX(1,camDir),Math2D.calcLenY(1,camDir)));
	}


	public void update() {
		vec3 toPos, pos, aPos = Math3D.calcPolarCoords(dis,  dir, dirZ);
		
		switch(camFocusType) {
			case CF_STATIC: toPos = new vec3(toX,toY,toZ);
							pos = new vec3(camX,camY,camZ);
							break;
			case CF_OBJECT: 
			default:		toPos = averageActors();
							pos = (vec3) toPos.add(aPos);
							break;
		}
		
		vec3 focusTranslation = new vec3(focusAddX,focusAddY,focusAddZ);
		toPos.adde(focusTranslation);
		pos.adde(focusTranslation);
	
		if(smoothing == 0) {
			this.toPos = toPos;
			this.pos = pos;
		}
		else if(smoothOnceFrac > -1) {
			smoothOnceFrac += (1 - smoothOnceFrac)/smoothing;
			if(Math.abs(1-smoothOnceFrac) < .05)
				smoothOnceFrac = 1;

			this.toPos = this.toPos.interpolate(toPos, smoothOnceFrac);			
			this.pos = this.pos.interpolate(pos, smoothOnceFrac);
			
			if(smoothOnceFrac == 1) {
				smoothOnceFrac = -1;
				smoothing = 0;
			}
		}
		else {
			this.toPos = (vec3) this.toPos.add((toPos.sub(this.toPos).mult(1f/smoothing)));
			this.pos = (vec3) this.pos.add((pos.sub(this.pos).mult(1f/smoothing)));
		}
		
		camDir = Math2D.calcPtDir(this.pos.xy(), this.toPos.xy());
	}


	public void lock(boolean state) 	{isLocked = state;}
	public void lock() 				{lock(true);}
	public void unlock() 			{lock(false);}


	public void smoothOnce(float factor) {
		if(!isLocked) {
			smoothing = factor;
			smoothOnceFrac = 0;
		}
	}
	public void smooth(float factor) {
		if(!isLocked) {
			smoothing = factor;
			smoothOnceFrac = -1;
		}
	}
	public void stiff() {
		if(!isLocked) {
			smoothing = 0;
			smoothOnceFrac = -1;
		}
	}
	
	
	public static boolean checkMapView() {
		return Mouse.getRightMouse();
	}


	public boolean checkOnscreen(float x, float y) {return checkOnscreen(x,y,fieldOfView);}
	public boolean checkOnscreen(float x, float y, float fov) {
		switch(projType) {
			case PR_PERSPECTIVE:		return Math.abs(Math2D.calcAngDiff(Math2D.calcPtDir(getX(),getY(), x,y),camDir)) < fov;
			case PR_ORTHOGRAPHIC:		return true;
			case PR_ORTHOPERSPECTIVE:
				float w, h, leeway = 100;
				w = fbo.getWidth() + 2*leeway;
				h = fbo.getHeight() + 2*leeway;
				return Math2D.checkRectangle(x, y, getX()-w/2,getY()-h/2, w,h);
			default:	return true;
		}
	}


	public float getFOV() {return fieldOfView;}
	public float getWidthHeightRatio() {return whRatio;}
	public float getViewNear() {return viewNear;}
	public float getViewFar() {return viewFar;}
	

	public void setFocusTranslation(float x, float y, float z) {
		focusAddX = x;
		focusAddY = y;
		focusAddZ = z;
	}

	public void gluLookAt(GLU glu) {
		glu.gluLookAt(pos.x(),pos.y(),pos.z(),
				toPos.x(),toPos.y(),toPos.z(),
				0,0,1);
	}
	
	
	public vec3 getPosition() {return pos;}

	public void setFBO(FBO fbo) {this.fbo = fbo;}
	public FBO getFBO() {return fbo;}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public static void renderAll() {
		Camera c;
		for(int i = camList.size()-1; i >= 0; i--) {
			c = camList.get(i);
			
			if(c.isEnabled) {
				GOGL.setCamera(c);
	        	c.fbo.attach(GOGL.gl,false);
	        	
	        	c.project();
	    		
	        	GOGL.clear(RGBA.WHITE);
	        	Drawable.presort();
	    		Drawable.draw3D();
	    		
	    		c.fbo.detach(GOGL.gl);
			}
		}
	}
	public void project() {
		switch(projType) {
			case PR_PERSPECTIVE:		fbo.setPerspective(GOGL.gl);		break;
			case PR_ORTHOGRAPHIC:		fbo.setOrtho(GOGL.gl);				break;
			case PR_ORTHOPERSPECTIVE:	fbo.setOrthoPerspective(GOGL.gl);	break;
		}
	}
}
