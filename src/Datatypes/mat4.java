package datatypes;

import functions.Math2D;

public class mat4 {
	vec4 mat[];
	
	public mat4() {
		mat = new vec4[4];
		
		mat[0] = new vec4(1,0,0,0);
		mat[1] = new vec4(0,1,0,0);
		mat[2] = new vec4(0,0,1,0);
		mat[3] = new vec4(0,0,0,1);
	}
	
	public mat4(mat4 other) {
		mat = new vec4[4];
		
		set(other);
	}
	
	public mat4(float[] array) {
		mat = new vec4[4];
		
		float a, b, c, d;
		
		for(int r = 0; r < 4; r++) {
			a = array[r*4+0];
			b = array[r*4+1];
			c = array[r*4+2];
			d = array[r*4+3];

			mat[r] = new vec4(a,b,c,d);
		}
	}
	
	public mat4(float nA1, float nA2, float nA3, float nA4, float nB1, float nB2, float nB3, float nB4, float nC1, float nC2, float nC3, float nC4, float nD1, float nD2, float nD3, float nD4) {
		mat = new vec4[4];
		
		mat[0] = new vec4(nA1,nA2,nA3,nA4);
		mat[1] = new vec4(nB1,nB2,nB3,nB4);
		mat[2] = new vec4(nC1,nC2,nC3,nC4);
		mat[3] = new vec4(nD1,nD2,nD3,nD4);
	}
	
	public static mat4 createRotationMatrix(float xR, float yR, float zR) {
		mat4 mX, mY, mZ;
		
		float xRRadians, yRRadians, zRRadians;
		xRRadians = xR/360*3.14159f;
		yRRadians = yR/360*3.14159f;
		zRRadians = zR/360*3.14159f;

		float coX, coY, coZ, siX, siY, siZ;
		coX = (float) Math.cos(xRRadians);
		siX = (float) Math.sin(xRRadians);
		coY = (float) Math.cos(yRRadians);
		siY = (float) Math.sin(yRRadians);
		coZ = (float) Math.cos(zRRadians);
		siZ = (float) Math.sin(zRRadians);
		
		mX = new mat4(1, 0, 0, 0,
					  0, coX, -siX, 0,
					  0, siX, coX, 0,
					  0, 0, 0, 1);
		mY = new mat4(coY, 0, siY, 0,
					  0, 1, 0, 0,
					  -siY, 0, coY, 0,
					  0, 0, 0, 1);
		mZ = new mat4(coZ, -siZ, 0, 0,
					  siZ, coZ, 0, 0,
					  0, 0, 1, 0,
					  0, 0, 0, 1);
		
		return mX.mult(mY.mult(mZ));
	}
	
	public mat4 copy() {
		return new mat4(this);
	}
	
	public mat4 transpose() {
		mat4 newM = new mat4();
		
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				newM.set(r,c, get(c,r));
				
		return newM;
	}
	
	public void set(int r, int c, float value) {
		if(r < 0 || r > 3 || c < 0 || c > 3)
			return;
		else
			mat[r].set(c, value);;
	}
	
	public void set(mat4 other) {
		for(int i = 0; i < 4; i++)
			mat[i] = new vec4(other.getRow(i));
	}

	
	public float get(int r, int c) {
		if(r < 0 || r > 3 || c < 0 || c > 3)
			return 0;
		else
			return mat[r].get(c);
	}
	
	public vec4 getRow(int i) {
		if(i < 0 || i > 3)
			return null;
		
		return mat[i];
	}
	
	public vec4 getCol(int index) {
		if(index < 0 || index > 3)
			return null;
		
		vec4 newV = new vec4();
		for(int i = 0; i < 4; i++)
			newV.set(i, get(i,index));

		return newV;
	}
		
	// OPERATORS
	public vec4 mult(vec4 other) {
		vec4 newV = new vec4();
				
		for(int r = 0; r < 4; r++)
			newV.set(r, getRow(r).dot(other));
		
		return newV;
	}
	
	public mat4 multe(float value) {
		set(mult(value));
		
		return this;
	}
	public mat4 multe(mat4 other) {
		set(mult(other));
		
		return this;
	}
	
	public mat4 mult(float value) {
		mat4 newM = new mat4();
				
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				newM.set(r,c, get(r,c)*value);
		
		return newM;
	}
	public mat4 mult(mat4 other) {
		mat4 newM = new mat4();

		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				newM.set(r,c,getRow(r).dot(other.getCol(c)));
		
		return newM;
	}
	
	public float determinant() {
		// Get All Values in Matrix
		float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;
		m00 = get(0,0);	m01 = get(0,1);	m02 = get(0,2);	m03 = get(0,3);
		m10 = get(1,0);	m11 = get(1,1);	m12 = get(1,2);	m13 = get(1,3);
		m20 = get(2,0);	m21 = get(2,1);	m22 = get(2,2);	m23 = get(2,3);
		m30 = get(3,0);	m31 = get(3,1);	m32 = get(3,2);	m33 = get(3,3);
		
		// Return Determinant
		return m03*m12*m21*m30 - m02*m13*m21*m30 - m03*m11*m22*m30 + m01*m13*m22*m30+
		   m02*m11*m23*m30 - m01*m12*m23*m30 - m03*m12*m20*m31 + m02*m13*m20*m31+
		   m03*m10*m22*m31 - m00*m13*m22*m31 - m02*m10*m23*m31 + m00*m12*m23*m31+
		   m03*m11*m20*m32 - m01*m13*m20*m32 - m03*m10*m21*m32 + m00*m13*m21*m32+
		   m01*m10*m23*m32 - m00*m11*m23*m32 - m02*m11*m20*m33 + m01*m12*m20*m33+
		   m02*m10*m21*m33 - m00*m12*m21*m33 - m01*m10*m22*m33 + m00*m11*m22*m33;
	}
	
	// Calculate Inverse of Matrix
	public mat4 inverse() {
		// Get All Values in Matrix
		float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;
		m00 = get(0,0);	m01 = get(0,1);	m02 = get(0,2);	m03 = get(0,3);
		m10 = get(1,0);	m11 = get(1,1);	m12 = get(1,2);	m13 = get(1,3);
		m20 = get(2,0);	m21 = get(2,1);	m22 = get(2,2);	m23 = get(2,3);
		m30 = get(3,0);	m31 = get(3,1);	m32 = get(3,2);	m33 = get(3,3);

		// Calculate Each New Value
		float b11, b12, b13, b14, b21, b22, b23, b24, b31, b32, b33, b34, b41, b42, b43, b44;
		b11 = m12*m23*m31 - m13*m22*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33;
		b12 = m03*m22*m31 - m02*m23*m31 - m03*m21*m32 + m01*m23*m32 + m02*m21*m33 - m01*m22*m33;
		b13 = m02*m13*m31 - m03*m12*m31 + m03*m11*m32 - m01*m13*m32 - m02*m11*m33 + m01*m12*m33;
		b14 = m03*m12*m21 - m02*m13*m21 - m03*m11*m22 + m01*m13*m22 + m02*m11*m23 - m01*m12*m23;
		b21 = m13*m22*m30 - m12*m23*m30 - m13*m20*m32 + m10*m23*m32 + m12*m20*m33 - m10*m22*m33;
		b22 = m02*m23*m30 - m03*m22*m30 + m03*m20*m32 - m00*m23*m32 - m02*m20*m33 + m00*m22*m33;
		b23 = m03*m12*m30 - m02*m13*m30 - m03*m10*m32 + m00*m13*m32 + m02*m10*m33 - m00*m12*m33;
		b24 = m02*m13*m20 - m03*m12*m20 + m03*m10*m22 - m00*m13*m22 - m02*m10*m23 + m00*m12*m23;
		b31 = m11*m23*m30 - m13*m21*m30 + m13*m20*m31 - m10*m23*m31 - m11*m20*m33 + m10*m21*m33;
		b32 = m03*m21*m30 - m01*m23*m30 - m03*m20*m31 + m00*m23*m31 + m01*m20*m33 - m00*m21*m33;
		b33 = m01*m13*m30 - m03*m11*m30 + m03*m10*m31 - m00*m13*m31 - m01*m10*m33 + m00*m11*m33;
		b34 = m03*m11*m20 - m01*m13*m20 - m03*m10*m21 + m00*m13*m21 + m01*m10*m23 - m00*m11*m23;
		b41 = m12*m21*m30 - m11*m22*m30 - m12*m20*m31 + m10*m22*m31 + m11*m20*m32 - m10*m21*m32;
		b42 = m01*m22*m30 - m02*m21*m30 + m02*m20*m31 - m00*m22*m31 - m01*m20*m32 + m00*m21*m32;
		b43 = m02*m11*m30 - m01*m12*m30 - m02*m10*m31 + m00*m12*m31 + m01*m10*m32 - m00*m11*m32;
		b44 = m01*m12*m20 - m02*m11*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 + m00*m11*m22;

		// Divide Inverse Matrix by Determinant
		mat4 inv = new mat4(b11,b12,b13,b14,b21,b22,b23,b24,b31,b32,b33,b34,b41,b42,b43,b44);
		inv.multe(1.f/determinant());

		// Return Inverse Matrix
		return inv;
	}
	
	public void println() {
		for(int r = 0; r < 4; r++) {
			for(int c = 0; c < 4; c++)
				System.out.print(get(r,c) + " ");
			
			System.out.print("\n");
		}
	}

	public float[] array() {
		float[] array = new float[16];
		
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				array[r*4 + c] = get(r,c);
		
		return array;
	}
	
	
	public void setIdentity() {
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				if(r == c)
					set(r,c,1);
				else
					set(r,c,0);
	}
	
	public static mat4 translation(float x, float y, float z) {
		return new mat4(
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1);
	}
	public static mat4 scale(float s) {
		return scale(s,s,s);
	}
	public static mat4 scale(float xS, float yS, float zS) {
		return new mat4(
				xS, 0, 0, 0,
				0, yS, 0, 0,
				0, 0, zS, 0,
				0, 0, 0, 1);
	}
	public static mat4 rotationX(float amt) {
		float rad, co,si;
		rad = -amt/180*3.14159f;
		co = (float) Math.cos(rad);
		si = (float) Math.sin(rad);
		
		return new mat4(
				1, 0, 0, 0,
				0, co, -si, 0,
				0, si, co, 0,
				0, 0, 0, 1);
	}
	public static mat4 rotationY(float amt) {
		float rad, co,si;
		rad = -amt/180*3.14159f;
		co = (float) Math.cos(rad);
		si = (float) Math.sin(rad);
		
		return new mat4(
				co, 0, si, 0,
				0, 1, 0, 0,
				-si, 0, co, 0,
				0, 0, 0, 1);
	}
	public static mat4 rotationZ(float amt) {
		float rad, co,si;
		rad = -amt/180*3.14159f;
		co = (float) Math.cos(rad);
		si = (float) Math.sin(rad);
		
		return new mat4(
				co, -si, 0, 0,
				si, co, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}
}
