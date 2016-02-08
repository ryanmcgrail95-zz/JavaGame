package ds;

import functions.Math2D;

public class mat4 extends mat {
	vec4 mat[];
	
	public mat4() {
		super();
		mat = new vec4[4];
		
		mat[0] = new vec4(1,0,0,0);
		mat[1] = new vec4(0,1,0,0);
		mat[2] = new vec4(0,0,1,0);
		mat[3] = new vec4(0,0,0,1);
	}
	
	public mat4(mat4 other) {
		super();
		mat = new vec4[4];
		set(other);
	}
	
	public mat4(float[] array) {
		super();
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
		super();
		mat = new vec4[4];
		
		mat[0] = new vec4(nA1,nA2,nA3,nA4);
		mat[1] = new vec4(nB1,nB2,nB3,nB4);
		mat[2] = new vec4(nC1,nC2,nC3,nC4);
		mat[3] = new vec4(nD1,nD2,nD3,nD4);
	}
	
	
	public void destroy() {
		super.destroy();
		for(int i = 0; i < 4; i++) {
			mat[i].destroy();
			mat[i] = null;
		}
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
	public mat4 transposee() {
		mat4 newM = transpose();
		set(newM);
		newM.destroy();
		return this;
	}
	
	public void set(int r, int c, float value) {
		if(r < 0 || r > 3 || c < 0 || c > 3)
			throw new IndexOutOfBoundsException();
		mat[r].set(c, value);;
	}
	
	public void set(mat4 other) {
		for(int i = 0; i < 4; i++) {
			if(mat[i] != null)
				mat[i].destroy();
			mat[i] = other.getRow(i);
		}
	}

	
	public float get(int r, int c) {
		if(r < 0 || r > 3 || c < 0 || c > 3)
			throw new IndexOutOfBoundsException();
		return mat[r].get(c);
	}
	
	// getRow(index)
	// Returns copy of given row in matrix.
	
	public vec4 getRow(int i) {
		if(i < 0 || i > 3)
			throw new IndexOutOfBoundsException();
		return new vec4(mat[i]);
	}
	
	public vec4 getCol(int index) {
		if(index < 0 || index > 3)
			throw new IndexOutOfBoundsException();

		vec4 newV = new vec4();
		for(int i = 0; i < 4; i++)
			newV.set(i, get(i,index));

		return newV;
	}
		
	// OPERATORS
	public vec4 mult(vec4 other) {
		vec4 newV = new vec4();
			
		vec4 row;
		for(int r = 0; r < 4; r++) {
			row = getRow(r);
			newV.set(r, row.dot(other));
			row.destroy();
		}
		
		return newV;
	}
	
	public float[] mult(float[] other) {
		float[] newA = new float[4];

		vec4 row;
		for(int r = 0; r < 4; r++) {
			row = getRow(r);
			newA[r] = row.dot(other);
			row.destroy();
		}
		
		return newA;
	}
	
	/*public mat4 multe(float value) {
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				set(r,c, get(r,c)*value);
		return this;
	}
	public mat4 multe(mat4 other) {
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				set(r,c,getRow(r).dot(other.getCol(c)));
		return this;
	}
	
	public mat4 mult(float value) {
		return copy().multe(value);
	}
	public mat4 mult(mat4 otherMat) {
		return copy().multe(otherMat);
	}*/
	
	public mat4 multe(float value) {
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				set(r,c, get(r,c)*value);
		return this;
	}
	public mat4 multe(mat4 other) {
		mat4 newM = mult(other);
		set(newM);
		newM.destroy();
		
		return this;
	}
	
	public mat4 mult(float value) {
		return copy().multe(value);
	}
	public mat4 mult(mat4 otherMat) {
		mat4 newM = copy();
		
		vec4 row, col;
		for(int r = 0; r < 4; r++) {
			row = getRow(r);
			for(int c = 0; c < 4; c++) {
				col = otherMat.getCol(c);
				newM.set(r,c, row.dot(col));
				col.destroy();
			}
			row.destroy();
		}

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
	
	public static mat4 createTranslationMatrix(float x, float y, float z) {
		return new mat4(createTranslationArray(x,y,z));
	}
	public static mat4 createScaleMatrix(float s) {
		return createScaleMatrix(s,s,s);
	}
	public static mat4 createScaleMatrix(float xS, float yS, float zS) {
		return new mat4(createScaleArray(xS,yS,zS));
	}
	
	/*public static mat4 createRotationMatrixX(float degrees) {
		return createRotationMatrix(degrees,0,0);
	}
	public static mat4 createRotationMatrixY(float degrees) {
		return createRotationMatrix(0,degrees,0);
	}
	public static mat4 createRotationMatrixZ(float degrees) {
		return createRotationMatrix(0,0,degrees);
	}*/
	
	
	public static mat4 createRotationXMatrix(float degrees) {return new mat4(createRotationXArray(degrees));}
	public static mat4 createRotationYMatrix(float degrees) {return new mat4(createRotationYArray(degrees));}
	public static mat4 createRotationZMatrix(float degrees) {return new mat4(createRotationZArray(degrees));}

	public static float[] createTranslationArray(float x, float y, float z) {
		return setTemp16(
			1, 0, 0, 0,
			0, 1, 0, 0,
			0, 0, 1, 0,
			x, y, z, 1);
	}
	
	public static float[] createScaleArray(float xS, float yS, float zS) {
		return setTemp16(
			xS, 0, 0, 0,
			0, yS, 0, 0,
			0, 0, zS, 0,
			0, 0, 0, 1);
	}
	
	public static float[] createRotationXArray(float degrees) {		
		float radians = degrees/180*3.14159f,
			co = (float) Math.cos(radians),
			si = (float) Math.sin(radians);
		return setTemp16(
			1, 0, 0, 0,
			0, co, si, 0,
			0, -si, co, 0,
			0, 0, 0, 1);
	}
	public static float[] createRotationYArray(float degrees) {		
		float radians = degrees/180*3.14159f,
			co = (float) Math.cos(radians),
			si = (float) Math.sin(radians);
		return setTemp16(
			co, 0, -si, 0,
			0, 1, 0, 0,
			si, 0, co, 0,
			0, 0, 0, 1);
	}
	public static float[] createRotationZArray(float degrees) {		
		float radians = degrees/180*3.14159f,
			co = (float) Math.cos(radians),
			si = (float) Math.sin(radians);
		return setTemp16(
			co, si, 0, 0,
			-si, co, 0, 0,
			0, 0, 1, 0,
			0, 0, 0, 1);
	}

	public static float[] createIdentityArray() {
		return createIdentityArray(new float[16]);
	}

	public static float[] createIdentityArray(float[] preMatrix) {
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				preMatrix[4*r + c] = (r == c) ? 1 : 0;
		return preMatrix;
	}
}
