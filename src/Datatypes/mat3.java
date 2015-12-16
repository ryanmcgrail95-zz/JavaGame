package datatypes;

public class mat3 extends mat {
	vec3 mat[];
	
	public mat3() {
		super();
		mat = new vec3[3];
		
		mat[0] = new vec3(1,0,0);
		mat[1] = new vec3(0,1,0);
		mat[2] = new vec3(0,0,1);
	}
	
	public mat3(mat3 other) {
		super();
		mat = new vec3[3];
		
		set(other);
	}
	
	public mat3(float nA1, float nA2, float nA3, float nB1, float nB2, float nB3, float nC1, float nC2, float nC3) {
		super();
		mat = new vec3[3];
		
		mat[0] = new vec3(nA1,nA2,nA3);
		mat[1] = new vec3(nB1,nB2,nB3);
		mat[2] = new vec3(nC1,nC2,nC3);
	}

	public mat3(vec3 v1, vec3 v2, vec3 v3) {
		super();
		mat = new vec3[3];
		
		mat[0] = new vec3(v1);
		mat[1] = new vec3(v2);
		mat[2] = new vec3(v3);
	}
	
	
	public void destroy() {
		super.destroy();
		for(int i = 0; i < 3; i++) {
			mat[i].destroy();
			mat[i] = null;
		}
	}
	
	
	public mat3 copy() {
		return new mat3(this);
	}
	
	public mat3 transpose() {
		mat3 newM = new mat3();
		
		for(int r = 0; r < 3; r++)
			for(int c = 0; c < 3; c++)
				newM.set(r,c, get(c,r));
				
		return newM;
	}
	
	public void set(int r, int c, float value) {
		if(r < 0 || r > 3 || c < 0 || c > 3)
			return;
		else
			mat[r].set(c, value);;
	}
	
	public void set(mat3 other) {
		for(int i = 0; i < 3; i++)
			mat[i] = new vec3(other.getRow(i));
	}

	
	public float get(int r, int c) {
		if(r < 0 || r > 2 || c < 0 || c > 2)
			return 0;
		else
			return mat[r].get(c);
	}
	
	public vec3 getRow(int i) {
		if(i < 0 || i > 2)
			return null;
		
		return mat[i];
	}
	
	public vec3 getCol(int index) {
		if(index < 0 || index > 2)
			return null;
		
		vec3 newV = new vec3();
		for(int i = 0; i < 4; i++)
			newV.set(i, get(i,index));

		return newV;
	}
		
	// OPERATORS
	public vec3 mult(vec3 other) {
		vec3 newV = new vec3();
				
		for(int r = 0; r < 3; r++)
			newV.set(r, getRow(r).dot(other));
		
		return newV;
	}
	
	public mat3 multe(float value) { 
		mat3 newM = mult(value);
		set(newM);
		newM.destroy();
		
		return this;
	}
	
	public mat3 multe(mat3 other) {
		mat3 newM = mult(other);
		set(newM);
		newM.destroy();
		
		return this;
	}
	
	public mat3 mult(float value) {
		mat3 newM = new mat3();
				
		for(int r = 0; r < 3; r++)
			for(int c = 0; c < 3; c++)
				newM.set(r,c, get(r,c)*value);
		
		return newM;
	}

	public mat3 mult(mat3 other) {
		mat3 newM = new mat3();

		for(int r = 0; r < 3; r++)
			for(int c = 0; c < 3; c++)
				newM.set(r,c,getRow(r).dot(other.getCol(c)));
		
		return newM;
	}
	
	public float determinant() {
		return 0;
	}
	
	// Calculate Inverse of Matrix
	public mat3 inverse() {
		return null;
	}
	
	public void println() {
		for(int r = 0; r < 3; r++) {
			for(int c = 0; c < 3; c++)
				System.out.print(get(r,c) + " ");
			
			System.out.print("\n");
		}
	}
	
	public static mat3 fromEuler(vec3 ang) {
		float a1x = (float) Math.sin(ang.get(0)),
			a1y = (float) Math.cos(ang.get(0));
		float a2x = (float) Math.sin(ang.get(1)),
			a2y = (float) Math.cos(ang.get(1));
		float a3x = (float) Math.sin(ang.get(2)),
			a3y = (float) Math.cos(ang.get(2));

		return new mat3(new vec3(a1y*a3y+a1x*a2x*a3x,a1y*a2x*a3x+a3y*a1x,-a2y*a3x),
			new vec3(-a2y*a1x,a1y*a2y,a2x),
			new vec3(a3y*a1x*a2x+a1y*a3x,a1x*a3x-a1y*a3y*a2x,a2y*a3y));
	}
}
