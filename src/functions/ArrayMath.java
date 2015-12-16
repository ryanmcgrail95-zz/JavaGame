package functions;

public class ArrayMath {
	public static float[] mult(float[] mat, float[] vec) {
		float[] outVec = {0,0,0,0};
		for(int i = 0; i < 4; i++)
			for(int ii = 0; ii < 4; ii++)
				outVec[i] += mat[4*i+ii]*vec[ii];
		return outVec;
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
	public static void cross(float[] a, float[] b, float[] dst) {
		if(a.length != 3 || b.length != 3 || dst.length != 3)
			throw new UnsupportedOperationException();
		dst[0] = a[1]*b[2] - a[2]*b[1];		
		dst[1] = a[2]*b[0] - a[0]*b[2];
		dst[2] = a[0]*b[1] - a[1]*b[0];
	}
}
