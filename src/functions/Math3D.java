package functions;

import Datatypes.vec2;
import Datatypes.vec3;

public final class Math3D {
	
	private Math3D() {
	}
	
	public static float calcPtDis(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (float) Math.sqrt(MathExt.sqr(x2-x1) + MathExt.sqr(y2-y1) + MathExt.sqr(z2-z1));
	}
	public static vec3 calcPtData(vec3 pt1, vec3 pt2) {
		float x1,y1,z1, x2,y2,z2, dis, dir, dirZ;
		
		x1 = pt1.get(0); y1 = pt1.get(1); z1 = pt1.get(2);
		x2 = pt2.get(0); y2 = pt2.get(1); z2 = pt2.get(2);
		
		dis = calcPtDis(x1,y1,z1,x2,y2,z2);
		dir = Math2D.calcPtDir(x1,y1, x2,y2);
		dirZ = Math2D.calcPtDir(0,z1, Math2D.calcPtDis(x1,y1,x2,y2),z2);
		
		return new vec3(dis, dir, dirZ);
	}
}
