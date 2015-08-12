package gfx;

import io.Mouse;

import java.util.ArrayList;
import java.util.List;

import datatypes.vec3;
import functions.Math2D;
import functions.Math3D;
import object.actor.Actor;


public class Camera {
	//CAMERA FOCUS
	private static final int CF_STATIC = 0, CF_OBJECT = 1;
	private static int camFocusType;
	private static List<Actor> camFocusList = new ArrayList<Actor>();
	private static float camX, camY, camZ, toX, toY, toZ, camDir;
	private static float dis, dir, dirZ, smoothing;
	private static boolean isLocked;
	private static vec3 pos = new vec3(), toPos = new vec3();
	private static boolean isOverhead;
	private static float viewDistance = 3000;
	private static float focusAddX, focusAddY, focusAddZ;
	
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
	

	public static float getX()			{return pos.x();}
	public static float getY()			{return pos.y();}
	public static float getZ() 			{return pos.z();}
	public static float getToX()		{return toPos.x();}
	public static float getToY()		{return toPos.y();}
	public static float getToZ() 		{return toPos.z();}
	public static float getDirection()	{return camDir;}
	
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


	public static void update() {
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
			Camera.toPos = toPos;
			Camera.pos = pos;
		}
		else {
			Camera.toPos = (vec3) Camera.toPos.add((toPos.sub(Camera.toPos).mult(1f/smoothing)));
			Camera.pos = (vec3) Camera.pos.add((pos.sub(Camera.pos).mult(1f/smoothing)));
		}
	}


	public static void lock(boolean state) 	{isLocked = state;}
	public static void lock() 				{lock(true);}
	public static void unlock() 			{lock(false);}


	public static void smooth(float factor) {
		if(!isLocked)
			smoothing = factor;
	}
	public static void stiff() {
		if(!isLocked)
			smoothing = 0;
	}
	
	
	public static boolean checkMapView() {
		return Mouse.getRightMouse();
	}


	public static boolean checkOnscreen(float x, float y) {
		return checkOnscreen(x,y,45);
	}
	public static boolean checkOnscreen(float x, float y, int fov) {
		return Math.abs(Math2D.calcAngDiff(Math2D.calcPtDir(camX,camY, x,y),camDir)) < fov;
	}


	public static float getViewDistance() {
		return viewDistance;
	}


	public static void setFocusTranslation(float x, float y, float z) {
		focusAddX = x;
		focusAddY = y;
		focusAddZ = z;
	}


	public static vec3 getPosition() {
		return pos;
	}
}
