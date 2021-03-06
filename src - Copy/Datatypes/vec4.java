package Datatypes;

import func.Math2D;

public final class vec4 extends vec {
	
	
	public vec4() {
		super(4);
		array[3] = 1;
	}
	
	public vec4(float a, float b, float c, float d) {
		super(4,a,b,c,d);
	}

	public vec4(vec4 other) {
		super(4);
		set(other);
	}
	
	public vec4(vec3 abc, float d) {
		super(4, abc.get(0), abc.get(1), abc.get(2), d);
	}

	public void set(vec4 other) {
		super.set(other);
	}
	
	public float len3() {
		return Math2D.calcLen(get(0),get(1),get(2));
	}
	
	public vec4 copy() {
		return new vec4(this);
	}
	
	public vec4 norm() {
		vec4 v = new vec4(this);
		return (vec4) v.norme();
	}
	
	public vec4 norm3() {
		float len = len3();

		return new vec4(get(0)/len,get(1)/len,get(2)/len,get(3));
	}
	
	// Operators
	public vec4 add(vec4 other) {
		vec4 newV = new vec4(this);
		newV.adde(other);
		
		return newV;
	}
	public float dot(vec4 other) {
		return super.dot(other);
	}
	

	public vec4 add3(vec4 other) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 3; i++)
			newV.set(i, array[i]+other.array[i]);
		
		return newV;
	}
	
	public vec4 mult(mat4 other) {
		vec4 newV = new vec4();
				
		for(int r = 0; r < 4; r++)
			newV.set(r, other.getCol(r).dot(this));
		
		return newV;
	}
	
	public vec4 mult(float val) {
		vec4 newV = new vec4(this);
		newV.multe(val);		
		
		return newV;
	}
	
	public vec4 mult3(vec4 other) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 3; i++)
			newV.set(i, array[i]*other.array[i]);
		
		return newV;
	}
	
	public vec4 mult3(float num) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 3; i++)
			newV.set(i, array[i]*num);
		
		return newV;
	}
	
	public float dot3(vec4 other) {
		float value = 0;
		
		for(int i = 0; i < 3; i++)
			value += array[i]*other.array[i];
		
		return value;
	}
	
	public vec4 cross3(vec4 other) {
		vec4 newV = new vec4();
		
		newV.set(0, array[1]*other.array[2] - array[2]*other.array[1]);		
		newV.set(1, array[2]*other.array[0] - array[0]*other.array[2]);
		newV.set(2, array[0]*other.array[1] - array[1]*other.array[0]);
		
		return newV;
	}
}