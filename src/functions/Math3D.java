package functions;

import ds.vec2;
import ds.vec3;

public final class Math3D extends ArrayMath {
	
	private Math3D() {}
	
	public static float calcLen(float x, float y, float z) {return calcPtDis(0,0,0,x,y,z);}
	public static float calcPtDis(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
	}
	
	public static float[] calcPtData(vec3 pt1, vec3 pt2) {
		float x1,y1,z1, x2,y2,z2, dis, dir, dirZ;
		
		x1 = pt1.get(0); y1 = pt1.get(1); z1 = pt1.get(2);
		x2 = pt2.get(0); y2 = pt2.get(1); z2 = pt2.get(2);
		
		dis = calcPtDis(x1,y1,z1,x2,y2,z2);
		dir = Math2D.calcPtDir(x1,y1, x2,y2);
		dirZ = Math2D.calcPtDir(0,z1, Math2D.calcPtDis(x1,y1,x2,y2),z2);
		
		return setTemp3(dis, dir, dirZ);
	}
	
	
	public static float calcPolarX(float dir, float dirZ) {return calcPolarX(1,dir,dirZ);}
	public static float calcPolarX(float dis, float dir, float dirZ) {
		return Math.abs(Math2D.calcLenX(dirZ))*Math2D.calcLenX(dis, dir);
	}
	
	public static float calcPolarY(float dir, float dirZ) {return calcPolarY(1,dir,dirZ);}
	public static float calcPolarY(float dis, float dir, float zDir) {
		return Math.abs(Math2D.calcLenX(zDir))*Math2D.calcLenY(dis, dir);
	}
	
	public static float calcPolarZ(float dir, float dirZ) {return calcPolarZ(1,dir,dirZ);}
	public static float calcPolarZ(float dis, float dir, float zDir) {
		return Math2D.calcLenY(dis, zDir);
	}
	
	public static vec3 calcPolarCoords(float dir, float dirZ) {return calcPolarCoords(1,dir,dirZ);}
	public static vec3 calcPolarCoords(float dis, float dir, float dirZ) {
		float 	coDir = Math2D.calcLenX(dir),
				siDir = Math2D.calcLenY(dir),
				coDirZ = Math2D.calcLenX(dis,dirZ),
				siDirZ = Math2D.calcLenY(dis,dirZ);
		
		return new vec3(coDir*coDirZ, siDir*coDirZ, siDirZ);
	}
}
