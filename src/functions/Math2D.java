package functions;

import datatypes.vec2;
import datatypes.vec3;

public class Math2D {	
	public static float calcLen(float... values) {
		float tot = 0;
		for(int i = 0; i < values.length; i++)
			tot += (values[i])*(values[i]);
		
		return (float) Math.sqrt(tot);
	}
	
	public static float calcLenX(float dir) {return calcLenX(1,dir);}
	public static float calcLenX(float dis, float dir) {
		return (float) (dis*FastMath.cosd(dir));
	}
	
	public static float calcLenY(float dir) {return calcLenY(1,dir);}
	public static float calcLenY(float dis, float dir) {
		return (float) (dis*FastMath.sind(dir));
	}
		
	public static float calcPtDis(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
		
	public static float calcPtDir(float x1, float y1, float x2, float y2) {
		return (float) (FastMath.atan2(y2-y1, x2-x1)/Math.PI*180);
	}
	
	
	public static float calcProjDis(float uX, float uY, float vX, float vY) {
		return (uX*vX + uY*vY);
	}
	
	/*public static float calcProjDis(float uX, float uY, float vX, float vY) {
		float coeff = (uX*vX + uY*vY)/calcPtDis(0, 0, vX, vY);
		
		return calcPtDis(0,0, coeff*vX, coeff*vY);
	}*/

	public static float calcSpeed(float deltaX, float accel) {
		return (float) Math.sqrt(2*deltaX*accel);
	}
	
	public static boolean checkCircle(float x, float y, float cX, float cY, float cR) {
		return (calcPtDis(x,y,cX,cY) <= cR);
	}
	public static boolean checkRectangle(float x, float y, float x1, float y1, float w, float h) {
		return (x1 <= x && x <= x1+w && y1 <= y && y <= y1+h);
	}
	
	public static vec2 calcLinePt(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
	    float dx, dy, t;
	    dx = x2-x1;
	    dy = y2-y1;
	    
	    if ((dx == 0) && (dy == 0))
	        return new vec2(x1, y1);
	    else {
	        t = (dx*(px-x1) + dy*(py-y1)) / (dx*dx+dy*dy);
	        if(segment)
	        	t = MathExt.contain(0,t,1);
	        return new vec2(x1 + t * dx, y1 + t * dy);
	    }
	}
	
	public static float calcLineDis(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
	    vec2 pt = calcLinePt(px,py,x1,y1,x2,y2,segment);
		float dis = calcPtDis(pt.x(),pt.y(), px, py);
		pt.destroy();
		return dis;
	}
	public static float calcLineDir(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
		vec2 pt = calcLinePt(px,py,x1,y1,x2,y2,segment);
		float dir = calcPtDir(px,py, pt.x(),pt.y());
		pt.destroy();
		return dir;
	}

	public static float calcSmoothTurn(float dir, float toDir) {
		return calcSmoothTurn(dir, toDir, 8);
	}
	
	public static float calcSmoothTurn(float dir, float toDir, float spd) {
		float newDir = 0;
		
		if(Math.abs(((((toDir - dir) % 360) + 540) % 360) - 180) >= 160)
			    newDir += FastMath.sind(toDir-dir)*16*15;

		newDir += FastMath.sind(toDir-dir)*spd;
		return newDir;
	}
}
