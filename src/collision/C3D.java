package collision;

import functions.ArrayMath;

public final class C3D extends ArrayMath {
	
	private static float[] triangleNormal;

	private C3D() {}
	
		// TRIANGLE INTERSECTIONS
		public static boolean intersectLineTriangle(float[][] line, float[][] triangle) {
			float[] pt1 = line[0],
					pt2 = line[1],
					dir = subtract(pt2,pt1);
			
			float r = raycastTriangle(new float[][] {pt1, dir}, triangle);
			
			if(r == -1)
				return false;
			else if(r <= calcPtDis(pt1, pt2))
				return true;
			else
				return false;
		}
		public static float[] intersectRayTriangle(float[][] ray, float[][] triangle) {
			float r = raycastTriangle(ray, triangle);
			
			if(r == -1)
				return null;
			
			float[] i = copy(ray[0]),
					dir = ray[1];
	        i[0] += r * dir[0];
	        i[1] += r * dir[1];
	        i[2] += r * dir[2];
	        
	        return i;
		}
		public static boolean intersectTriangles(float[][] tri1, float[][] tri2) {
			float[] pt11 = tri1[0], pt12 = tri1[1], pt13 = tri1[2],
					pt21 = tri2[0], pt22 = tri2[1], pt23 = tri2[2];
			float[][] 
					sg11 = {pt11, pt12},
					sg12 = {pt12, pt13},
					sg13 = {pt13, pt11},
					sg21 = {pt21, pt22},
					sg22 = {pt22, pt23},
					sg23 = {pt23, pt21};
			
			if(intersectLineTriangle(sg21, tri1))	return true;
			if(intersectLineTriangle(sg22, tri1))	return true;
			if(intersectLineTriangle(sg23, tri1))	return true;
			if(intersectLineTriangle(sg11, tri2))	return true;
			if(intersectLineTriangle(sg12, tri2))	return true;
			if(intersectLineTriangle(sg13, tri2))	return true;
			
			return false;
		}
		
	// POLYGON COLLISIONS
		public static float raycastPolygon(float x, float y, float z, float normalX, float normalY, float normalZ, float[][][] polygon) {			
			return raycastPolygon(new float[][] {new float[] {x,y,z}, new float[] {normalX,normalY,normalZ}}, polygon);
		}
		public static float raycastPolygon(float[][] ray, float[][][] polygon) {			
			float r, minR = -1;
			float[] bestTriangle = null;
			for(float[][] triangle : polygon) {
				r = raycastTriangle(ray, triangle);
				if(r != -1)
					if(minR == -1 || r < minR) {
						bestTriangle = triangleNormal;
						minR = r;
					}
			}
			
			if(minR != -1)
				triangleNormal = bestTriangle;
			return minR;
		}
		public static boolean intersectPolygons(float[][][] poly1, float[][][] poly2) {
			for(float[][] tri1 : poly1)
				for(float[][] tri2 : poly2)
					if(intersectTriangles(tri1,tri2))
						return true;
			return false;
		}
		
	// BLOCK INTERSECTIONS
		public static boolean intersectPointBlock(float[] pt, float[][] rect) {
			return between(rect[0], pt, rect[1]);
		}
		public static boolean intersectLineBlock(float[][] line, float[][] rect) {
			if(intersectPointBlock(line[0],rect))
				return true;
			else if(intersectPointBlock(line[1],rect))
				return true;
			else if(intersectPointBlock(line[0],rect))
				return true;
			else
				return false;
		}

	public static float raycastTriangle(float[][] ray, float[][] triangle) {
		float SMALL = 0.00000001f;
		float[] u, v, n;
        float[] dir,
        		w0;
        float r, a, b;
        
        u = copy(triangle[1]);
        	subtract(u,triangle[0],u);
        v = copy(triangle[2]);
        	subtract(v,triangle[0],v);
                	
        w0 = copy(ray[0]);
    	dir = copy(ray[1]);
        
        float[] h;
        h = cross(dir,v);
    	a = dot(u, h);
    	
    	if(a > -SMALL && a < SMALL)
    		return -1;
    	
    	float f = 1/a;
    	subtract(w0,triangle[0],w0);
    	
    	float uu = f * dot(w0, h);
    	if(uu < 0 || uu > 1)
    		return -1;
    	
    	float[] q = cross(w0, u);
    	float vv = f * dot(dir, q);
    	
    	if(vv < 0 || uu+vv > 1)
    		return -1;
    	
    	
    	float t = f * dot(v,q);
    	
    	if(t >= 0) {
    		triangleNormal = normalize(cross(u,v));
    		return t;
    	}
    	else
    		return -1;
    		
        
        /*n = cross(u, v);
        
        if (length(n) == 0)
            return -1;
        
        dir = copy(ray[1]);
        w0 = copy(ray[0]);
        	subtract(w0,triangle[0],w0);
        a = -dot(n, w0);
        b = dot(n, dir);
        
        if ((float) Math.abs(b) < 0.00000001f)
            return -1;
        
        r = a / b;
        if (r < 0.0)
            return -1;
        
        return r;*/
    }

	public static float[] getTriangleNormal() {
		return triangleNormal;
	}
}
