package functions;

public class ArrayMath {
	
	public static float[]
		tempArray3 = new float[3];
	public static float[] 
		tempArray4 = new float[4],
		tempMath4 = new float[4];
	public static float[] 
		tempArray16 = new float[16],
		tempOut16 = new float[16],
		tempMath16 = new float[16];
	
	public static float[] set(float[] array, float... values) {
		int len = array.length;
		for(int i = 0; i < len; i++)
			array[i] = values[i];
		return array;
	}
	public static float[] fill(float[] array, float values) {
		int aLen = array.length;
		for(int i = 0; i < aLen; i++) {
			array[i] = values;
			//vi = (vi + 1) % vLen;
		}
		return array;
	}
	
	
	public static float[] setTemp3(float a, float b, float c) {
		return set(tempArray3,a,b,c);
	}
	public static float[] setTemp4(float a, float b, float c, float d) {
		return set(tempArray4,a,b,c,d);
	}
	public static float[] setTemp16(float... values) {
		return set(tempArray16, values);
	}
	
	public static float[] multMV(float[] mat, float[] vec) {
		fill(tempMath4, 0);
		multMV(mat, vec, tempMath4);
		return set(tempArray4, tempMath4);
	}
	public static void multMV(float[] mat, float[] vec, float[] dst) {
		for(int r = 0; r < 3; r++)
			for(int c = 0; c < 4; c++)
				dst[r] += mat[4*r+c]*vec[c];
	}
	public static float[] multMM(float[] mat1, float[] mat2) {
		fill(tempMath16, 0);
		
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				for(int i = 0; i < 4; i++)
					tempMath16[4*r+c] += mat1[4*c+i]*mat2[4*r+i];		
		return set(tempOut16, tempMath16);
	}
	
	public static float[] copy(float[] src) {
		float[] out = new float[src.length];
			copy(src, out);
		return out;
	}
	public static void copy(float[] src, float[] dst) {
		int len = src.length;
		if(len != dst.length)
			throw new UnsupportedOperationException();
		for(int i = 0; i < len; i++)
			dst[i] = src[i];
	}
	
	public static boolean lessThan(float[] a, float[] b) {
		int len = a.length;
		if(len != b.length)
			throw new UnsupportedOperationException();
		for(int i = 0; i < len; i++)
			if(a[i] > b[i])
				return false;
		return true;
	}
	public static boolean greaterThan(float[] a, float[] b) {
		int len = a.length;
		if(len != b.length)
			throw new UnsupportedOperationException();
		for(int i = 0; i < len; i++)
			if(a[i] < b[i])
				return false;
		return true;
	}
	public static boolean between(float[] min, float[] mid, float[] max) {
		if(lessThan(min,max))
			return greaterThan(mid,min) && lessThan(mid,max);
		else
			return greaterThan(mid,max) && lessThan(mid,min);
	}
	
	
	public static float[] subtract(float[] a, float[] b) {
		float[] out = new float[a.length];
			subtract(a,b, out);
		return out;
	}
	public static void subtract(float[] a, float[] b, float[] dst) {
		int len = a.length;
		if(len != b.length || len != dst.length)
			throw new UnsupportedOperationException();
		for(int i = 0; i < len; i++)
			dst[i] = a[i] - b[i];
	}
	
	public static float calcPtDis(float[] pt1, float[] pt2) {
		return length(subtract(pt2,pt1));
	}
	
	public static float length(float[] a) {
		float tot = 0;
		for(float n : a)
			tot += n*n;
		return (float) Math.sqrt(tot);
	}
	
	public static float[] normalize(float[] a) {
		float len = length(a);
		for(int i = 0; i < a.length; i++)
			a[i] /= len;
		return a;
	}

	public static float dot(float[] a, float[] b) {
		int len = a.length;
		if(len != b.length)
			throw new UnsupportedOperationException();
		int tot = 0;
		for(int i = 0; i < len; i++)
			tot += a[i]*b[i];
		return tot;
	}
	
	public static float[] cross(float[] a, float[] b) {
		float[] out = new float[3];
			cross(a,b,out);
		return out;
	}
	
	public static float[] transpose(float[] a) {
		float[] out = new float[16];
		
		for(int r = 0; r < 4; r++)
			for(int c = 0; c < 4; c++)
				out[4*r+c] = a[4*c+r];

		for(int r = 0; r < 16; r++)
			a[r] = out[r];

		return a;
	}
	
	public static void cross(float[] a, float[] b, float[] dst) {
		if(a.length != 3 || b.length != 3 || dst.length != 3)
			throw new UnsupportedOperationException();
		dst[0] = a[1]*b[2] - a[2]*b[1];		
		dst[1] = a[2]*b[0] - a[0]*b[2];
		dst[2] = a[0]*b[1] - a[1]*b[0];
	}
	public static void println(float[] a) {
		int i = a.length;
		if(i <= 4) {
			for(float n : a)
				System.out.print(n + " ");
			System.out.println();
		}
		else {
			for(int n = 0; n < 4; n++) {
				for(int k = 0; k < 4; k++)
					System.out.print(a[4*n+k] + " ");
				System.out.println();
			}
		}			
	}
}
