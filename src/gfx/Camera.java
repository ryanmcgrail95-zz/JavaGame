package gfx;

import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.glu.GLU;
import datatypes.vec3;
import datatypes.lists.CleanList;
import functions.FastMath;
import functions.Math2D;
import functions.Math3D;
import object.actor.Actor;
import object.primitive.Drawable;
import object.primitive.Updatable;
import paper.Background;
import resource.model.Model;
import time.Delta;


public class Camera extends Updatable {
	//CAMERA FOCUS
	public static final byte PR_PERSPECTIVE = 0, PR_ORTHOGRAPHIC = 1, PR_ORTHOPERSPECTIVE = 2;
	private static final byte CF_STATIC = 0, CF_OBJECT = 1, CF_CAMERA = 2;
	private static CleanList<Camera> camList = new CleanList<Camera>("Cameras");
	private byte camFocusType, projType;
	
	private CleanList<Drawable> drawList = new CleanList<Drawable>("Drawable");
	private List<Actor> camFocusList = new ArrayList<Actor>();
	private Camera copyCam;
	
	private float camX, camY, camZ, toX, toY, toZ, camDir;
	private float dis, dir, dirZ, smoothing;
	private boolean isLocked, isEnabled = true, hasBG;
	private FBO fbo;
	private float[] upNormal = {0,0,1};

	
	private float smoothOnceFrac;
	
	private vec3 pos = new vec3(), toPos = new vec3();
	private float fieldOfView = 25, viewFar = 3000, //3000
			viewNear = 1, whRatio;
	private float focusAddX, focusAddY, focusAddZ;
	
	
	public Camera(byte projType, FBO fbo) {
		super();
		name = "Camera";
		whRatio = 1f*fbo.getWidth()/fbo.getHeight();
		
		setSurviveTransition(true);
		
		hasBG = false;
		
		this.projType = projType;
		setFBO(fbo);
		camList.add(this);
	}
	public Camera(byte projType, int resWidth, int resHeight) {
		super();
		name = "Camera";
		whRatio = 1f*resWidth/resHeight;

		setSurviveTransition(true);
		
		hasBG = false;

		this.projType = projType;
		setFBO(new FBO(GL.gl, resWidth, resHeight));
		camList.add(this);
	}
	
	public Camera(String name, Camera copyCam) {
		super();
		
		this.copyCam = copyCam;
		this.name = name;
	
		whRatio = copyCam.getWidthHeightRatio();
		setSurviveTransition(true);
		
		hasBG = false;

		this.projType = CF_CAMERA;
		
		FBO fbo = copyCam.fbo;
		setFBO(new FBO(GL.gl, fbo.getWidth(), fbo.getHeight()));
		camList.add(this);
	}
	public void destroy() {super.destroy();}
	
	public CleanList<Drawable> getDrawList() {
		return drawList;
	}
	
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
	
	public float[] getNormal() {
		vec3 norm = (vec3)(toPos.copy().sube(pos));
		float[] array = norm.getArray();
		norm.destroy();
		
		return array;
	}
	public float[] getShaderNormal() {
		return new float[] {0, -Math2D.calcPtDir(0,camZ,Math2D.calcPtDis(camX,camY,toX,toY),toZ)/180.f*3.14159f, -Math2D.calcPtDir(camX,camY,toX,toY)/180.f*3.14159f};
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
		return Math2D.calcProjDis(x-getX(), y-getY(), FastMath.cosd(camDir),FastMath.sind(camDir));
	}


	public void update() {
		
		if(camFocusType == CF_CAMERA || copyCam != null) {
			this.pos.set(copyCam.pos);
			this.toPos.set(copyCam.toPos);
			camDir = copyCam.camDir;
			return;
		}
		
		vec3 toPos, pos, prevPos, prevPos2, aPos = Math3D.calcPolarCoords(dis, dir, dirZ);
		prevPos = (vec3) this.pos.copy();		
		
		switch(camFocusType) {
			case CF_STATIC: toPos = new vec3(toX,toY,toZ);
							pos = new vec3(camX,camY,camZ);
							break;
			case CF_OBJECT: 
			default:		toPos = averageActors();
							pos = (vec3) toPos.copy().adde(aPos);
							break;
		}
		
		vec3 focusTranslation = new vec3(focusAddX,focusAddY,focusAddZ);
		toPos.adde(focusTranslation);
		pos.adde(focusTranslation);
	
		if(smoothing == 0) {
			this.toPos.destroy();
			this.pos.destroy();
			this.toPos = toPos;
			this.pos = pos;
		}
		else if(smoothOnceFrac > -1) {
			smoothOnceFrac += (1 - smoothOnceFrac)/smoothing;
			if(Math.abs(1-smoothOnceFrac) < .05)
				smoothOnceFrac = 1;

			vec3 oldP, oldTP;
			oldP = this.pos;
			oldTP = this.toPos;
			this.toPos = this.toPos.interpolate(toPos, smoothOnceFrac);			
			this.pos = this.pos.interpolate(pos, smoothOnceFrac);
			oldP.destroy();
			oldTP.destroy();
			
			if(smoothOnceFrac == 1) {
				smoothOnceFrac = -1;
				smoothing = 0;
			}
		}
		else {
			// DELETE THESE
			vec3 addTP, addP;
			addTP = new vec3(toPos);
				addTP.sube(this.toPos).multe(1f/smoothing);
			addP = new vec3(pos);
				addP.sube(this.pos).multe(1f/smoothing);
			
			this.toPos.adde( Delta.convert(addTP) );
			this.pos.adde( Delta.convert(addP) );
			
			addTP.destroy();
			addP.destroy();
		}
		
		camDir = Math2D.calcPtDir(this.pos.x(),this.pos.y(), this.toPos.x(),this.toPos.y());
		
		prevPos2 = (vec3) this.pos.copy().sube(prevPos);
		Background.addPerpendicularMotion(
				Math2D.calcProjDis(prevPos2.x(),prevPos2.y(), FastMath.cosd(camDir+90),FastMath.sind(camDir+90)));
		
		if(pos != this.pos)
			pos.destroy();
		if(toPos != this.toPos)
			toPos.destroy();
		prevPos.destroy();
		prevPos2.destroy();
		aPos.destroy();
		focusTranslation.destroy();
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


	public boolean checkOnscreen(float x, float y) {return checkOnscreen(x,y,fieldOfView);}
	public boolean checkOnscreen(float x, float y, float fov) {
		switch(projType) {
			case PR_PERSPECTIVE:		return FastMath.calcAngleDiff(Math2D.calcPtDir(getX(),getY(), x,y),camDir) < fov;
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
	
	public void setResolution(int w, int h) {
		//fbo.destroy();
		
		whRatio = 1f*w/h;
		setFBO(new FBO(GL.gl, w, h));
	}

	public void setFocusTranslation(float x, float y, float z) {
		focusAddX = x;
		focusAddY = y;
		focusAddZ = z;
	}

	public void gluLookAt(GLU glu) {
		glu.gluLookAt(pos.x(),pos.y(),pos.z(),
				toPos.x(),toPos.y(),toPos.z(),
				upNormal[0],upNormal[1],upNormal[2]);
	}
	
	
	public float[] getPosition() {return pos.getArray();}

	public void setFBO(FBO fbo) {this.fbo = fbo;}
	public FBO getFBO() {return fbo;}

	public void enable(boolean isEnabled) {this.isEnabled = isEnabled;}

	public static void renderAll() {
		Camera c;
		for(int i = camList.size()-1; i >= 0; i--) {
			c = camList.get(i);
			if(c.isEnabled)
				c.renderDrawable();
		}
	}
	public void renderDrawable() {
		GL.setCamera(this);
    	fbo.attach(GL.gl,false);
    	
    	project();
		
    	GL.clear(RGBA.TRANSPARENT);
    	Drawable.presort();
    	
		Drawable.draw3D();
		
		fbo.detach(GL.gl);
	}
	public void render(Drawable... obj) {
		GL.setCamera(this);
    	fbo.attach(GL.gl,false);
    	
    	project();
		
    	GL.clear(RGBA.TRANSPARENT);
    	for(Drawable d : obj)
    		d.draw();
		
		fbo.detach(GL.gl);
	}
	public void render(Model mod) {
		GL.setCamera(this);
    	fbo.attach(GL.gl,false);
    	
    	project();
		
    	GL.clear(RGBA.TRANSPARENT);
    	mod.draw();
		
		fbo.detach(GL.gl);
	}
	
	public void project() {
		switch(projType) {
			case PR_PERSPECTIVE:		fbo.setPerspective(GL.gl);		break;
			case PR_ORTHOGRAPHIC:		fbo.setOrtho(GL.gl);				break;
			case PR_ORTHOPERSPECTIVE:	fbo.setOrthoPerspective(GL.gl);	break;
		}
	}
	public void setUpNormal(float nX, float nY, float nZ) {
		upNormal[0] = nX;
		upNormal[1] = nY;
		upNormal[2] = nZ;
	}
	
	public void enableBG(boolean enable) {hasBG = enable;}
	public boolean checkBG() {
		return hasBG;
	}
	public void addDrawable(Drawable obj) {
		drawList.add(obj);
	}
}
