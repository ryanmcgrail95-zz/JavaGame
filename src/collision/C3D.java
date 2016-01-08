package collision;

import java.util.ArrayList;
import java.util.List;

import functions.ArrayMath;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;

public final class C3D extends ArrayMath {
	
	private static float[] triangleNormal = new float[3];
	
	private static float[] splitModel;
	private static int[][][] splitModelIndices;
	
	private static float modXMi, modYMi, modXMa, modYMa, modXSi, modYSi, modXBSi, modYBSi, modBLe;
	private static int modXNum, modYNum;
	
	private static float prevRaycastDis;
	
	private static int intersectNum = 0, rayNum = 0, calcNum = 0;
	/*
	 * 
	*/

	private C3D() {}
	
	
	public static void splitModel(float[][][] polygon, int xNum, int yNum, float padding) {
		destroySplitModel();
		
		splitModel = new float[polygon.length*13];
		splitModelIndices = new int[xNum][yNum][];
		
		float x, y, z;
		
		modXNum = xNum;
		modYNum = yNum;
		
		float[] boundingBox = boundingBox(polygon);
		modXMi = boundingBox[0];
		modYMi = boundingBox[1];
		modXMa = boundingBox[2];
		modYMa = boundingBox[3];
		
		modXSi = modXMa-modXMi;
		modYSi = modYMa-modYMi;
		
		modXBSi = modXSi/xNum;
		modYBSi = modYSi/yNum;
		
		modBLe = Math2D.calcLen(modXBSi, modYBSi);
		
		
		// Add Triangles to Lists
		
		List<Integer> tris = new ArrayList<Integer>();
		
		float rx1,ry1, rx2,ry2;
		float x1,y1,z1, x2,y2,z2, x3,y3,z3, nx1,ny1,nz1;
		
		
		// Add Values to Split Model
		int i = 0, k = 0;
		
		float[] e1 = new float[3],
				e2 = new float[3],
				norm = new float[3];
		
		for(float[][] tri : polygon) {
			for(float[] point : tri)
				for(float value : point)
					splitModel[i++] = value;
			
			subtract(tri[1],tri[0], e1);
			subtract(tri[2],tri[0], e2);			
			norm = normalize(cross(e1,e2));

			for(float value : norm)
				splitModel[i++] = value;
			
			splitModel[i++] = k++;
		}
		
		
		// Add Indices to Grid
		for(int xi = 0; xi < xNum; xi++) {
			rx1 = modXMi + 1f*xi/xNum*modXSi;
			rx2 = modXMi + 1f*(xi+1)/xNum*modXSi;
			
			for(int yi = 0; yi < yNum; yi++) {
				ry1 = modYMi + 1f*yi/yNum*modYSi;
				ry2 = modYMi + 1f*(yi+1)/yNum*modYSi;

				for(int ii = 0; ii < splitModel.length; ii += 13) {
					x1 = splitModel[ii];
					y1 = splitModel[ii+1];
					z1 = splitModel[ii+2];
					
					x2 = splitModel[ii+3];
					y2 = splitModel[ii+4];
					z2 = splitModel[ii+5];
					
					x3 = splitModel[ii+6];
					y3 = splitModel[ii+7];
					z3 = splitModel[ii+8];
					
					if(intersectTriangleBlock(x1,y1,z1,x2,y2,z2,x3,y3,z3, rx1-padding,ry1-padding,-MathExt.INFINITY,rx2+padding,ry2+padding,MathExt.INFINITY)) {
						tris.add((int) splitModel[ii+12]);
					}
				}
				
				if(!tris.isEmpty()) {
					splitModelIndices[xi][yi] = new int[tris.size()];
					
					i = 0;
					for(int index : tris)
						splitModelIndices[xi][yi] [i++] = index;
					tris.clear();
				}
			}
		}
	}
	
	public static void reset() {
		//System.out.println("C3D: " + intersectNum + ", " + rayNum + ", " + calcNum);
		intersectNum = 0;
		rayNum = 0;
		calcNum = 0;
	}
	
	private static float[] boundingBox(float[][][] polygon) {
		float xMi, yMi, xMa, yMa;
		float x, y;
		
		xMi = yMi = Float.MAX_VALUE;
		xMa = yMa = Float.MIN_VALUE;
		
		for(float[][] tri : polygon) {
			for(float[] pt : tri) {
				x = pt[0];
				y = pt[1];
				
				xMi = Math.min(xMi,x);
				xMa = Math.max(xMa,x);

				yMi = Math.min(yMi,y);
				yMa = Math.max(yMa,y);
			}
		}

		return new float[] {xMi,yMi, xMa,yMa};
	}


	public static void destroySplitModel() {
		if(splitModel == null)
			return;
		
		for(int[] [] x : splitModelIndices) {
			for(int [] y : x) {
					y = null;
				}
			x = null;
		}
					
		splitModel = null;
		splitModelIndices = null;
	}
	
	public float[] createPoint(float x, float y, float z) {return new float[] {x, y, z};}
	public float[][] createTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {return new float[][] {createPoint(x1,y1,z1), createPoint(x2,y2,z2), createPoint(x3,y3,z3)};}
	public float[][] createSimpleRectangle(float x1, float y1, float z1, float x2, float y2, float z2) {
		return new float[][] {createPoint(x1,y1,z1), createPoint(x2,y2,z2)};
	}
	
	
		// TRIANGLE INTERSECTIONS
		public static boolean intersectLineTriangle(float lx1, float ly1, float lz1, float lx2, float ly2, float lz2, float tx1, float ty1, float tz1, float tx2, float ty2, float tz2, float tx3, float ty3, float tz3) {
			if(intersectRayTriangle(lx1,ly1,lz1,lx2,ly2,lz2, tx1,ty1,tz1,tx2,ty2,tz2,tx3,ty3,tz3, 0,0,0))
				if(prevRaycastDis <= Math3D.calcPtDis(lx1,ly1,lz1, lx2,ly2,lz2))
					return true;
			return false;
		}
	
	
		/*public static float[] intersectRayTriangle(float[][] ray, float[][] triangle) {
			float r = raycastTriangle(ray, triangle);
			
			if(r == -1)
				return null;
			
			float[] i = copy(ray[0]),
					dir = ray[1];
	        i[0] += r * dir[0];
	        i[1] += r * dir[1];
	        i[2] += r * dir[2];
	        
	        return i;
		}*/
		
		
		
		public static boolean intersectTriangles(float t1x1, float t1y1, float t1z1,
				float t1x2, float t1y2, float t1z2,
				float t1x3, float t1y3, float t1z3,
				float t2x1, float t2y1, float t2z1,
				float t2x2, float t2y2, float t2z2,
				float t2x3, float t2y3, float t2z3) {
			
			if(intersectLineTriangle(t2x1,t2y1,t2z1,t2x2,t2y2,t2z2,
				t1x1,t1y1,t1z1,t1x2,t1y2,t1z2,t1x3,t1y3,t1z3))	return true;
			if(intersectLineTriangle(t2x2,t2y2,t2z2,t2x3,t2y3,t2z3, 
				t1x1,t1y1,t1z1,t1x2,t1y2,t1z2,t1x3,t1y3,t1z3))	return true;
			if(intersectLineTriangle(t2x3,t2y3,t2z3,t2x1,t2y1,t2z1,
				t1x1,t1y1,t1z1,t1x2,t1y2,t1z2,t1x3,t1y3,t1z3))	return true;
			if(intersectLineTriangle(t1x1,t1y1,t1z1,t1x2,t1y2,t1z2, 
				t2x1,t2y1,t2z1,t2x2,t2y2,t2z2,t2x3,t2y3,t2z3))	return true;
			if(intersectLineTriangle(t1x2,t1y2,t1z2,t1x3,t1y3,t1z3, 
				t2x1,t2y1,t2z1,t2x2,t2y2,t2z2,t2x3,t2y3,t2z3))	return true;
			if(intersectLineTriangle(t1x3,t1y3,t1z3,t1x1,t1y1,t1z1, 
				t2x1,t2y1,t2z1,t2x2,t2y2,t2z2,t2x3,t2y3,t2z3))	return true;
			
			return false;
		}
		
	// POLYGON COLLISIONS
		/*public static float raycastPolygon(float x, float y, float z, float normalX, float normalY, float normalZ, float[][][] polygon) {			
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
		}*/
		
		public static float raycastSplit(float rx0, float ry0, float rz0, float rxn, float ryn, float rzn, float maxDis) {
			float r, minR = -1;			
			float curDis = 0;
			
			int MAX_STEPS = 3;
			float rx, ry, rz, rDis;
			int xi,yi,zi, rxns, ryns, rzns;
			
			rxns = (int) Math.signum(rxn);
			ryns = (int) Math.signum(ryn);
			rzns = (int) Math.signum(rzn);
			
			int [] curBlock;
			
			int ii;
			//System.out.println("START");
			for(int i = 0; curDis-modBLe < maxDis; i++) {
				curDis += modBLe;
				//rayNum++;
				
				rx = rx0 + rxn*i*modBLe;
				ry = ry0 + ryn*i*modBLe;
				rz = rz0 + rzn*i*modBLe;
				
				// Get CurBlock
				xi = (int) ((rx - modXMi)/modXBSi);
				yi = (int) ((ry - modYMi)/modYBSi);
				
				//System.out.print("\t(" + xi + ", " + yi + "): ");
				
				if((xi < 0 || xi >= modXNum) || (yi < 0 || yi >= modYNum))
					break;
				
				curBlock = splitModelIndices[xi][yi];

				
				if(curBlock != null) {
					//System.out.println(curBlock.length);

					for(int index : curBlock) {
						ii = index*13;
						rayNum++;
						if(intersectRayTriangle(rx,ry,rz, rxn,ryn,rzn, splitModel[ii],splitModel[ii+1],splitModel[ii+2],splitModel[ii+3],splitModel[ii+4],splitModel[ii+5],splitModel[ii+6],splitModel[ii+7],splitModel[ii+8],splitModel[ii+9],splitModel[ii+10],splitModel[ii+11])) {
							triangleNormal[0] = splitModel[ii+9];
							triangleNormal[1] = splitModel[ii+10];
							triangleNormal[2] = splitModel[ii+11];
							rDis = prevRaycastDis+i*modBLe;
							if(minR == -1 || rDis < minR)
								minR = rDis;
						}
					}
				}
				
				if(minR != -1)
					break;
			}

			if(minR == -1)
				return -1;
			else
				return minR;
		}
		
		/*public static boolean intersectPolygons(float[][][] poly1, float[][][] poly2) {
			for(float[][] tri1 : poly1)
				for(float[][] tri2 : poly2)
					if(intersectTriangles(tri1,tri2))
						return true;
			return false;
		}*/
		
		public static boolean intersectPolygonsSplit(float[][][] poly) {
			float[] boundingBox = boundingBox(poly);
			float xMi,yMi, xMa,yMa;
			xMi = boundingBox[0];
			yMi = boundingBox[1];
			xMa = boundingBox[2];
			yMa = boundingBox[3];
			
			int xiMi,yiMi, xiMa,yiMa;
			xiMi = MathExt.contain(0, (int) ((xMi - modXMi)/modXBSi), modXNum);
			yiMi = MathExt.contain(0, (int) ((yMi - modYMi)/modYBSi), modYNum);
			xiMa = MathExt.contain(0, (int) ((xMa - modXMi)/modXBSi), modXNum);
			yiMa = MathExt.contain(0, (int) ((yMa - modYMi)/modYBSi), modYNum);
						
			int[] tris;
			
			float 
				t1x1,t1y1,t1z1,
				t1x2,t1y2,t1z2,
				t1x3,t1y3,t1z3,
				t2x1,t2y1,t2z1,
				t2x2,t2y2,t2z2,
				t2x3,t2y3,t2z3;
			
			int ii;
			
			for(int xi = xiMi; xi <= xiMa; xi++)
				for(int yi = yiMi; yi <= yiMa; yi++) {
						tris = splitModelIndices[xi][yi];
						
						//intersectNum++;
						if(tris != null)
							for(float[][] tri2 : poly) {
								t2x1 = tri2[0][0];
								t2y1 = tri2[0][1];
								t2z1 = tri2[0][2];
								
								t2x2 = tri2[1][0];
								t2y2 = tri2[1][1];
								t2z2 = tri2[1][2];
								
								t2x3 = tri2[2][0];
								t2y3 = tri2[2][1];
								t2z3 = tri2[2][2];
								
								for(int index : tris) {
									ii = index*13;
									t1x1 = splitModel[ii];
									t1y1 = splitModel[ii+1];
									t1z1 = splitModel[ii+2];
									
									t1x2 = splitModel[ii+3];
									t1y2 = splitModel[ii+4];
									t1z2 = splitModel[ii+5];
									
									t1x3 = splitModel[ii+6];
									t1y3 = splitModel[ii+7];
									t1z3 = splitModel[ii+8];
									
									if(intersectTriangles(
											t1x1,t1y1,t1z1,
											t1x2,t1y2,t1z2,
											t1x3,t1y3,t1z3,
											t2x1,t2y1,t2z1,
											t2x2,t2y2,t2z2,
											t2x3,t2y3,t2z3))
										return true;
								}
					}
				}
			return false;
		}
		
	// BLOCK INTERSECTIONS
		public static boolean intersectPointBlock(float[] pt, float[][] rect) {
			return between(rect[0], pt, rect[1]);
		}
		public static boolean intersectLineBlock(float lx1, float ly1, float lz1, float lx2, float ly2, float lz2, float rx1, float ry1, float rz1, float rx2, float ry2, float rz2) {
			return (lx1 <= rx2 && lx2 >= rx1) && (ly1 <= ry2 && ly2 >= ry1) && (lz1 <= rz2 && lz2 >= rz1);
		}
		
		public static boolean intersectTriangleBlock(float tx1,float ty1,float tz1, float tx2,float ty2,float tz2, float tx3,float ty3,float tz3, float rx1, float ry1, float rz1, float rx2, float ry2, float rz2) {
			if(intersectLineBlock(tx1,ty1,tz1, tx2,ty2,tz2, rx1, ry1, rz1, rx2, ry2, rz2))	return true;
			if(intersectLineBlock(tx2,ty2,tz2, tx3,ty3,tz3, rx1, ry1, rz1, rx2, ry2, rz2))	return true;
			if(intersectLineBlock(tx3,ty3,tz3, tx1,ty1,tz1, rx1, ry1, rz1, rx2, ry2, rz2))	return true;
			return false;
		}

	
	public static float raycastTriangle(float rx,float ry,float rz, float rxn,float ryn,float rzn, float tx1,float ty1,float tz1, float tx2,float ty2,float tz2, float tx3,float ty3,float tz3) {
		if(intersectRayTriangle(rx,ry,rz,rxn,ryn,rzn,tx1,ty1,tz1,tx2,ty2,tz2,tx3,ty3,tz3,0,0,0))
			return prevRaycastDis;
		return -1;
	}
		
	public static boolean intersectRayTriangle(float rx,float ry,float rz, float rxn,float ryn,float rzn, float tx1,float ty1,float tz1, float tx2,float ty2,float tz2, float tx3,float ty3,float tz3, float txn,float tyn,float tzn) {
		float 
			px, py, pz,
			qx, qy, qz;
		float det, u, v;
		
		// Get Edges
		tx2 -= tx1;
		ty2 -= ty1;
		tz2 -= tz1;
		tx3 -= tx1;
		ty3 -= ty1;
		tz3 -= tz1;
		
		// Get 
		
		px = ryn*tz3 - rzn*ty3;
		py = rzn*tx3 - rxn*tz3;
		pz = rxn*ty3 - ryn*tx3;

		det = tx2*px + ty2*py + tz2*pz;
		
		// Calculate T Vector
		rx -= tx1;
		ry -= ty1;
		rz -= tz1;

		u = rx*px + ry*py + rz*pz;
		
			    if (det > 0.0001){
			          if (u < 0.0 || u > det)return false;

			          qx = ry*tz2 - rz*ty2;
			          qy = rz*tx2 - rx*tz2;
			          qz = rx*ty2 - ry*tx2;
			          
			          v = rxn*qx + ryn*qy + rzn*qz;
			          if (v < 0.0 || u + v > det)return false;
			    }else if(det < -0.0001){
			        if (u > 0.0 || u < det)return false;
			        
			        qx = ry*tz2 - rz*ty2;
			  		qy = rz*tx2 - rx*tz2;
			  		qz = rx*ty2 - ry*tx2;
		          
			  		v = rxn*qx + ryn*qy + rzn*qz;
			        if (v > 0.0 || u + v < det)return false;
			    }else return false;

	    prevRaycastDis = (tx3*qx + ty3*qy + tz3*qz) / det;
			    
	    return prevRaycastDis >= 0;
	}
		
	/*public static float raycastTriangle(float[][] ray, float[][] triangle) {
		
		calcNum++;
		
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
    //}

	public static float[] getTriangleNormal() {
		return triangleNormal;
	}
}
