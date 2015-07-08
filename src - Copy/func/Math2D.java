package func;

import Datatypes.vec2;

public class Math2D {
	public static final float INFINITY = Float.MAX_VALUE;

	public static float rnd() {
		return (float) Math.random();
	}
	public static float rnd(float cap) {
		return cap*rnd();
	}
	public static float rnd(float mi, float ma) {
		return mi + rnd(ma-mi);
	}
	
	public static float sqr(float n) {
		return n*n;
	}
	
	public static float min(float... values) {
		float mi = values[0];
		
		for(int i = 1; i < values.length; i++)
			mi = Math.min(mi, values[i]);
		
		return mi;
	}
	public static int min(int... values) {
		int mi = values[0];
		
		for(int i = 1; i < values.length; i++)
			mi = Math.min(mi, values[i]);
		
		return mi;
	}
	
	public static float max(float... values) {
		float ma = values[0];
		
		for(int i = 1; i < values.length; i++)
			ma = Math.max(ma, values[i]);
		
		return ma;
	}
	public static int max(int... values) {
		int ma = values[0];
		
		for(int i = 1; i < values.length; i++)
			ma = Math.max(ma, values[i]);
		
		return ma;
	}
	
	public static float calcLen(float... values) {
		float tot = 0;
		for(int i = 0; i < values.length; i++)
			tot += sqr(values[i]);
		
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
		return (float) Math.sqrt(sqr(x2-x1) + sqr(y2-y1));
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
	
	public static boolean between(float mi, float value, float ma) {
		return (mi <= value && value <= ma);
	}

	public static float contain(float mi, float value, float ma) {
		return Math.max(mi, Math.min(value, ma));
	}

	public static float snap(float val, float grid) {
		return grid*rnd(val/grid);
	}	
	public static float wrap(float mi, float value, float ma) {
		float diff = ma - mi;
		
		if(value < mi)
			value += diff;
		else if(value > ma)
			value -= diff;
		
		return value;
	}

	public static boolean isInsideCircle(vec2 pt, vec2 cPt, float cR) {
		return (calcPtDis(pt.x(),pt.y(),cPt.x(),cPt.y()) <= cR);
	}
	public static boolean isInsideCircle(float x, float y, float cX, float cY, float cR) {
		return (calcPtDis(x,y,cX,cY) <= cR);
	}
	
	public static boolean isInsideRectangle(float x, float y, float x1, float y1, float x2, float y2) {
		return (x1 <= x && x <= x2 && y1 <= y && y <= y2);
	}

	public static float median(float... values) {
		boolean didSwitch;
		int i, ii = values.length;
		float temp;
		
		do {
			didSwitch = false;
			
			for(i = 1; i < ii; i++)
				if(values[i] < values[i-1]) {
					didSwitch = true;
					
					temp = values[i];
					
					values[i] = values[i-1];
					values[i-1] = temp;
				}
			
			ii--;
		} while(didSwitch);
		
		return values[values.length/2];
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
	        	t = contain(0,t,1);
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
