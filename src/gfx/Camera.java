package gfx;

import java.util.ArrayList;
import java.util.List;

import Datatypes.vec3;

import com.jogamp.opengl.GL2;

import functions.Math2D;
import functions.Math3D;
import object.actor.Actor;
import object.primitive.Drawable;
import object.primitive.Instantiable;
import object.primitive.Updatable;
import resource.sound.SoundController;

public class Camera {
	//CAMERA FOCUS
	private static final int CF_STATIC = 0, CF_OBJECT = 1;
	private static int camFocusType;
	private static List<Actor> camFocusList = new ArrayList<Actor>();
	private static float camX, camY, camZ, toX, toY, toZ, camDir;
	private static float dis, dir, dirZ, smoothing;
	private static boolean isLocked;
	private static vec3 pos = new vec3(), toPos = new vec3();
	
	public static void setProjection(float cX, float cY, float cZ, float tX, float tY, float tZ) {
		if(isLocked)
			return;
		
		camFocusType = CF_STATIC;
		
		camX = cX; camY = cY; camZ = cZ;
		toX = tX; toY = tY; toZ = tZ;
		
		camDir = Math2D.calcPtDir(camX, camY, toX, toY);
	}

	
	public static vec3 averageActors() {
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
	

	public static float getX() {
		return pos.x();
	}
	public static float getY() {
		return pos.y();
	}
	public static float getZ() {
		return pos.z();
	}
	public static float getToX() {
		return toPos.x();
	}
	public static float getToY() {
		return toPos.y();
	}
	public static float getToZ() {
		return toPos.z();
	}
	
	public static vec3 getNormal() {
    	return new vec3(toX-camX, toY-camY, toZ-camZ);
	}
	public static vec3 getShaderNormal() {
		return new vec3(0, -Math2D.calcPtDir(0,camZ,Math2D.calcPtDis(camX,camY,toX,toY),toZ)/180.f*3.14159f, -Math2D.calcPtDir(camX,camY,toX,toY)/180.f*3.14159f);
	}
	
	public static void focus(float dis, float dir, float dirZ, Actor... actors) {
		if(isLocked)
			return;
			
		Camera.dis = dis;
		Camera.dir = dir;
		Camera.dirZ = dirZ;
		
		camFocusType = CF_OBJECT;
	
		camFocusList.clear();
		for(Actor a : actors)
			camFocusList.add(a);
	}

	public static float calcPerpDistance(float x, float y) {
		return Math.abs(Math2D.calcProjDis(x-camX, y-camY, Math2D.calcLenX(1,camDir+90),Math2D.calcLenY(1,camDir+90)));
	}
	public static float calcParaDistance(float x, float y) {
		return Math.abs(Math2D.calcProjDis(x-camX, y-camY, Math2D.calcLenX(1,camDir),Math2D.calcLenY(1,camDir)));
	}
	
	public static float getDirection() {
		return camDir;
	}


	public static void update() {
		vec3 toPos, pos;
		float aX, aY, aZ;
		aX = Math2D.calcPolarX(dis, dir, dirZ);
		aY = Math2D.calcPolarY(dis, dir, dirZ);
		aZ = Math2D.calcPolarZ(dis, dir, dirZ);
		
		switch(camFocusType) {
			case CF_STATIC: toPos = new vec3(toX,toY,toZ);
							pos = new vec3(camX,camY,camZ);
							break;
			case CF_OBJECT: 
			default:		toPos = averageActors();
							pos = toPos.add(new vec3(aX,aY,aZ));
							break;
		}
	
		if(smoothing == 0) {
			Camera.toPos = toPos;
			Camera.pos = pos;
		}
		else {
			Camera.toPos = Camera.toPos.add((toPos.subtract(Camera.toPos).mult(1f/smoothing)));
			Camera.pos = Camera.pos.add((pos.subtract(Camera.pos).mult(1f/smoothing)));
		}
	}


	public static void lock() {
		isLocked = true;
	}
	public static void unlock() {
		isLocked = false;
	}


	public static void smooth(float factor) {
		if(!isLocked)
			smoothing = factor;
	}
	public static void stiff() {
		if(!isLocked)
			smoothing = 0;
	}
}
