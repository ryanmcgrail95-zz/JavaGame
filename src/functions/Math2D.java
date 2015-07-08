package functions;

import Datatypes.vec2;

public class Math2D {	
	public static float calcLen(float... values) {
		float tot = 0;
		for(int i = 0; i < values.length; i++)
			tot += MathExt.sqr(values[i]);
		
		return (float) Math.sqrt(tot);
	}
	
	public static float calcLenX(float dir) {
		return calcLenX(1,dir);
	}
	public static float calcLenX(float dis, float dir) {
		return (float) (dis*Math.cos(dir/180*Math.PI));
	}
	
	public static float calcLenY(float dir) {
		return calcLenY(1,dir);
	}
	public static float calcLenY(float dis, float dir) {
		return (float) (dis*Math.sin(dir/180*Math.PI));
	}
	
	public static float calcPolarX(float dis, float dir, float zDir) {
		return Math.abs(calcLenX(1, zDir))*calcLenX(dis, dir);
	}
	
	public static float calcPolarY(float dis, float dir, float zDir) {
		return Math.abs(calcLenX(1, zDir))*calcLenY(dis, dir);
	}
	
	public static float calcPolarZ(float dis, float dir, float zDir) {
		return calcLenY(dis, zDir);
	}
	
	
	public static float calcPtDis(vec2 pt1, vec2 pt2) {
		return calcPtDis(pt1.x(), pt1.y(), pt2.x(), pt2.y());
	}
	public static float calcPtDis(float x1, float y1, vec2 pt2) {
		return calcPtDis(x1, y1, pt2.x(), pt2.y());
	}
	public static float calcPtDis(vec2 pt1, float x2, float y2) {
		return calcPtDis(pt1.x(), pt1.y(), x2, y2);
	}
	public static float calcPtDis(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(MathExt.sqr(x2-x1) + MathExt.sqr(y2-y1));
	}
	
	
	public static float calcPtDir(vec2 pt1, vec2 pt2) {
		return calcPtDir(pt1.x(), pt1.y(), pt2.x(), pt2.y());
	}
	public static float calcPtDir(float x1, float y1, vec2 pt2) {
		return calcPtDir(x1, y1, pt2.x(), pt2.y());
	}
	public static float calcPtDir(vec2 pt1, float x2, float y2) {
		return calcPtDir(pt1.x(), pt1.y(), x2, y2);
	}
	public static float calcPtDir(float x1, float y1, float x2, float y2) {
		return (float) (Math.atan2(y2-y1, x2-x1)/Math.PI*180);
	}
	
	
	public static float calcProjDis(float uX, float uY, float vX, float vY) {
		float coeff;
		coeff = (uX*vX + uY*vY)/calcPtDis(0, 0, vX, vY);
		
		return calcPtDis(0,0, coeff*vX, coeff*vY);
	}

	public static float calcSpeed(float deltaX, float accel) {
		return (float) Math.sqrt(2*deltaX*accel);
	}
	
	

	public static boolean checkCircle(vec2 pt, vec2 cPt, float cR) {
		return (calcPtDis(pt.x(),pt.y(),cPt.x(),cPt.y()) <= cR);
	}
	public static boolean checkCircle(float x, float y, float cX, float cY, float cR) {
		return (calcPtDis(x,y,cX,cY) <= cR);
	}
	public static boolean checkRectangle(float x, float y, float x1, float y1, float w, float h) {
		return (x1 <= x && x <= x1+w && y1 <= y && y <= y1+h);
	}
	
	
	
	public static float calcAngDiff(float angle1, float angle2) {
		//
		//  Returns the relative angle [-180..180] between the given angles.
		//
		//      angle1      1st direction in degrees
		//      angle2      2nd direction in degress
		//
		/// GMLscripts.com/license
	
		return ((((angle1 - angle2) % 360) + 540) % 360) - 180;
	}
	
	public static vec2 calcLinePt(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
		//
		//  Returns the distance from the given point to the given line.
		//
		//      px,py       point to measuring from
		//      x1,y1       1st end point of line
		//      x2,y2       2nd end point of line
		//      segment     set to true to limit to the line segment
		//
		/// GMLscripts.com/license
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
	    return calcPtDis(calcLinePt(px,py,x1,y1,x2,y2,segment), px, py);
	}
	public static float calcLineDir(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
	    return calcPtDir(px, py, calcLinePt(px,py,x1,y1,x2,y2,segment));
	}

	public static float calcSmoothTurn(float dir, float toDir) {
		return calcSmoothTurn(dir, toDir, 8);
	}
	
	public static float calcSmoothTurn(float dir, float toDir, float spd) {
		float newDir = 0;
		
		if(Math.abs(((((toDir - dir) % 360) + 540) % 360) - 180) >= 160)
			    newDir += Math.sin((toDir-dir)/180*Math.PI)*16*15;

		newDir += Math.sin((toDir-dir)/180*Math.PI)*spd;
		return newDir;
	}
}
