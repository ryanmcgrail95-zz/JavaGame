package object.actor.body;

import ds.vec3;
import functions.MathExt;
import gfx.GOGL;

public abstract class Cycle {
	public int frameNum;
	public float speed;
	public float[] bodyX, upperLegX, lowerLegX, upperArmX, lowerArmX;
	public float[] bodyY, upperLegY, lowerLegY, upperArmY, lowerArmY;
	public float[] bodyZ, upperLegZ, lowerLegZ, upperArmZ, lowerArmZ;

	
	public Cycle() {
		iniBody();
		iniLegs();
		iniArms();
	}
	
	protected abstract void iniBody();
	protected abstract void iniArms();
	protected abstract void iniLegs();
	
	public vec3 getBody(float i) {
		int si = frameNum, i1, i2;
		float uAX, uAY, uAZ;
		
		i1 = (int) Math.floor(i);
		i2 = (int) Math.ceil(i);
		i1 = i1 % frameNum;
		i2 = i2 % frameNum;

		uAX = MathExt.interpolate(bodyX[i1],bodyX[i2], i-i1);
		uAY = MathExt.interpolate(bodyY[i1],bodyY[i2], i-i1);
		uAZ = MathExt.interpolate(bodyZ[i1],bodyZ[i2], i-i1);
		
		return new vec3(uAX, uAY, uAZ);
	}
	public vec3 getUpperArm(float i) {
		int si = frameNum, i1, i2;
		float uAX, uAY, uAZ;
		
		i1 = (int) Math.floor(i);
		i2 = (int) Math.ceil(i);
		i1 = i1 % frameNum;
		i2 = i2 % frameNum;
		
		if(i2 == frameNum)
			i2 = 0;

		uAX = MathExt.interpolate(upperArmX[i1],upperArmX[i2], i-i1);
		uAY = MathExt.interpolate(upperArmY[i1],upperArmY[i2], i-i1);
		uAZ = MathExt.interpolate(upperArmZ[i1],upperArmZ[i2], i-i1);
		
		return new vec3(uAX, uAY, uAZ);
	}
	public vec3 getLowerArm(float i) {
		int si = frameNum, i1, i2;
		float uAX, uAY, uAZ;
		
		i1 = (int) Math.floor(i);
		i2 = (int) Math.ceil(i);
		i1 = i1 % frameNum;
		i2 = i2 % frameNum;
		
		if(i2 == frameNum)
			i2 = 0;

		uAX = MathExt.interpolate(lowerArmX[i1],lowerArmX[i2], i-i1);
		uAY = MathExt.interpolate(lowerArmY[i1],lowerArmY[i2], i-i1);
		uAZ = MathExt.interpolate(lowerArmZ[i1],lowerArmZ[i2], i-i1);
		
		return new vec3(uAX, uAY, uAZ);
	}
	public vec3 getUpperLeg(float i) {
		int si = frameNum, i1, i2;
		float uAX, uAY, uAZ;
		
		i1 = (int) Math.floor(i);
		i2 = (int) Math.ceil(i);
		i1 = i1 % frameNum;
		i2 = i2 % frameNum;
		
		if(i2 == frameNum)
			i2 = 0;

		uAX = MathExt.interpolate(upperLegX[i1],upperLegX[i2], i-i1);
		uAY = MathExt.interpolate(upperLegY[i1],upperLegY[i2], i-i1);
		uAZ = MathExt.interpolate(upperLegZ[i1],upperLegZ[i2], i-i1);
		
		return new vec3(uAX, uAY, uAZ);
	}
	public vec3 getLowerLeg(float i) {
		int si = frameNum, i1, i2;
		float uAX, uAY, uAZ;
		
		i1 = (int) Math.floor(i);
		i2 = (int) Math.ceil(i);
		i1 = i1 % frameNum;
		i2 = i2 % frameNum;
		
		if(i2 == frameNum)
			i2 = 0;

		uAX = MathExt.interpolate(lowerLegX[i1],lowerLegX[i2], i-i1);
		uAY = MathExt.interpolate(lowerLegY[i1],lowerLegY[i2], i-i1);
		uAZ = MathExt.interpolate(lowerLegZ[i1],lowerLegZ[i2], i-i1);
		
		return new vec3(uAX, uAY, uAZ);
	}
}
