package Datatypes;

import functions.Math2D;
import functions.MathExt;

public final class vec3 extends vec {
	
	
	public vec3() {
		super(3);
	}
	
	public vec3(float a, float b, float c) {
		super(3,a,b,c);
	}

	public vec3(vec3 other) {
		super(3);
		set(other);
	}
	
	public void set(vec3 other) {
		super.set(other);
	}
	
	public vec3 copy() {
		return new vec3(this);
	}
	
	public vec3 norm() {
		vec3 v = new vec3(this);
		return (vec3) v.norme();
	}
	
		
	// Operators
	public vec3 add(vec3 other) {
		vec3 newV = new vec3(this);
		newV.adde(other);
		
		return newV;
	}
	public vec3 subtract(vec3 other) {
		vec3 newV = new vec3(this);
		newV.sube(other);
		
		return newV;
	}

	public float dot(vec3 other) {
		return super.dot(other);
	}
	
	
	public vec3 mult(float num) {
		vec3 newV = new vec3(this);
		newV.multe(num);
		
		return newV;
	}
		
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
	
	public vec3 interpolate(vec3 other, float frac) {
		float f = MathExt.contain(0, frac, 1), iF = 1-f;
		vec3 newVec = new vec3();
		
		for(int i = 0; i < 3; i++)
			newVec.set(i, get(i)*iF + other.get(i)*f);
		
		return newVec;
	}

	public float x() {
		return get(0);
	}
	public float y() {
		return get(1);
	}
	public float z() {
		return get(2);
	}
}