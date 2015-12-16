package datatypes;

import java.util.List;

import animation.Interpolatable;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;

public final class vec3 extends vec implements Interpolatable {
	
	
	public vec3() {
		super(3);
	}
	
	public vec3(float a, float b, float c) {
		super(3,a,b,c);
	}

	public vec3(vec other) {
		super(3);
		set(other);
	}
	
		
	// Operators		
	public vec3 cross(vec3 other) {
		vec3 newV = new vec3();
		
		newV.set(0, array[1]*other.array[2] - array[2]*other.array[1]);		
		newV.set(1, array[2]*other.array[0] - array[0]*other.array[2]);
		newV.set(2, array[0]*other.array[1] - array[1]*other.array[0]);
		
		return newV;
	}

	public static vec3[] array(float[] a, float[] b, float[] c) {
		if(a.length != b.length || a.length != c.length) {
			System.out.println("vec3 error: Arrays not same length.");
			System.exit(2);
		}
		
		int si = a.length;
		vec3[] arrays = new vec3[si];

		for(int i = 0; i < si; i++)
			arrays[i] = new vec3(a[i],b[i],c[i]);
		
		return arrays;
	}
	
	/*public static float[][] convert(List<vec3> list) {
		int si = list.size();
		float[][] outArray = new float[si][3];
		
		for(int i = 0; i < si; i++)
			outArray[i] = list.get(i).array;
		
		return outArray;
	}*/
	
	public vec3 interpolate(vec3 other, float frac) {
		float f = MathExt.contain(0, frac, 1), iF = 1-f;
		vec3 newVec = new vec3();
		
		for(int i = 0; i < 3; i++)
			newVec.set(i, get(i)*iF + other.get(i)*f);
		
		return newVec;
	}

	
	public static vec3 rnd(float maxR) {
		return Math3D.calcPolarCoords(MathExt.rnd()*maxR,MathExt.rnd(360),MathExt.rnd(180));
	}

	
	public float xyLen() {
		return Math2D.calcLen(x(),y());
	}
	public void setDirection(float direction) {
		float xySpd = xyLen();
		x(Math2D.calcLenX(xySpd, direction));
		y(Math2D.calcLenY(xySpd, direction));
	}
	public float getDirection() {
		return Math2D.calcPtDir(0,0,x(),y());
	}

	public void setXYLen(float newSpd) {
		float dir = getDirection();
		x(Math2D.calcLenX(newSpd, dir));
		y(Math2D.calcLenY(newSpd, dir));
	}


	public void updateValues(Interpolatable other) {
		set((vec3) other);
	}
	public Interpolatable tween(Interpolatable toValue, float frac) {
		return interpolate((vec3) toValue, frac);
	}
}