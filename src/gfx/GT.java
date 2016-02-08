package gfx;

import com.jogamp.opengl.GL2;

import ds.mat4;
import ds.vec3;
import functions.Math2D;

public class GT extends GL {
	private GT() {}

	
	public static void getModelViewMatrix(float[] dst) 					{gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, dst,0);}
	public static float[] getModelViewMatrix() {
		float[] outMatrix = new float[16];
			getModelViewMatrix(outMatrix);
		return outMatrix;
	}
	
	public static void setTransformation(float[] mat)					{gl.glLoadMatrixf(mat,0);}
	public static void addTransformation(float[] mat)					{gl.glMatrixMultfEXT(GL2.GL_MODELVIEW, mat, 0);}

	
	public static void transformClear() 								{setTransformation(mat4.createIdentityArray());}
	
	public static void transformTranslation(vec3 pos) 					{transformTranslation(pos.x(),pos.y(),pos.z());}
	public static void transformTranslation(float[] pos) 				{transformTranslation(pos[0],pos[1],pos[2]);}
	public static void transformTranslation(float x, float y, float z) 	{addTransformation(mat4.createTranslationArray(x,y,z));}
	
	public static void transformScale(float s) 							{transformScale(s,s,s);}
	public static void transformScale(float xS, float yS, float zS)	 	{addTransformation(mat4.createScaleArray(xS,yS,zS));}
	
	public static void transformRotation(float dir, float dirZ)			{transformRotation(0,-dirZ,dir);}
	public static void transformRotation(vec3 rot) 						{transformRotation(rot.x(),rot.y(),rot.z());}
	public static void transformRotation(float xRot, float yRot, float zRot) {
		transformRotationZ(xRot);
		transformRotationY(yRot);
		transformRotationX(zRot);
	}
	public static void transformRotationX(float ang) 					{addTransformation(mat4.createRotationXArray(ang));}
	public static void transformRotationY(float ang) 					{addTransformation(mat4.createRotationYArray(ang));}
	public static void transformRotationZ(float ang) 					{addTransformation(mat4.createRotationZArray(ang));}
		
	public static void transformRotationNormal(float[] normal) 			{transformRotation(Math2D.calcPtDir(0,0, normal[0],normal[1]), Math2D.calcPtDir(0,0, Math2D.calcPtDis(0,0, normal[0],normal[1]),normal[2]));}
	
	public static void transformBeforeCamera(float len) {
		transformTranslation(getCamera().getPosition());
		transformRotationNormal(getCamera().getNormal());
		transformTranslation(len,0,0);
	}
	
	// Set Up Rotation Matrix for Rotating Sprites to Face Camera (Assumes they are Solely in XY-Space)
	public static void transformSprite() {
		transformRotationNormal(getCamera().getNormal());
		transformRotationY(90);
		transformRotationZ(-90);
	}
	public static void transformPaper() {
		transformRotationZ(getCamera().getDirection());
		transformRotationY(-90);
		transformRotationZ(-90);
	}
	public static void transformPaper(float flipDir) {
		transformRotationZ(90*(flipDir-1));
		transformRotationZ(getCamera().getDirection());
		transformRotationY(-90);
		transformRotationZ(-90);
	}
}
