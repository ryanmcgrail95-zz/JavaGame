package ds;

import java.util.List;

import functions.Math2D;

public class vec4 extends vec {
	
	
	public vec4() {
		super(4);
		array[3] = 1;
	}
	
	public vec4(float a, float b, float c, float d) {
		super(4,a,b,c,d);
	}

	public vec4(vec other) {
		super(4);
		set(other);
	}
	
	public vec4(vec3 abc, float d) {
		super(4, abc.get(0), abc.get(1), abc.get(2), d);
	}
	
	public float len3() {
		return Math2D.calcLen(get(0),get(1),get(2));
	}
	
	public vec4 norm3() {
		float len = len3();

		return new vec4(get(0)/len,get(1)/len,get(2)/len,get(3));
	}
	
	// Operators
	public vec4 add3(vec4 other) {
		vec4 newV = new vec4();
		
		for(int i = 0; i < 3; i++)
			newV.set(i, array[i]+other.array[i]);
		
		return newV;
	}
	

	public vec4 multe(mat4 other) {
		vec4 m = mult(other);
		set(m);
		m.destroy();
		
		return this;
	}
	public vec4 mult(mat4 other) {
		vec4 outVec = new vec4(this);
		vec4 m;
		for(int r = 0; r < 4; r++) {
			m = other.getRow(r);
			outVec.set(r, m.dot(this));
			m.destroy();
		}
		return outVec;
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
	
	
	public static float[][] convert(List<vec4> list) {
		int si = list.size();
		float[][] outArray = new float[si][4];
		
		for(int i = 0; i < si; i++)
			outArray[i] = list.get(i).array;
		
		return outArray;
	}
}