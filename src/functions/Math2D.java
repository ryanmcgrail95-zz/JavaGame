package functions;

import ds.vec2;
import ds.vec3;

public class Math2D {
	private static float[] tempPt = new float[2];
	
	public static float calcLen(float... values) {
		float tot = 0;
		for(int i = 0; i < values.length; i++)
			tot += (values[i])*(values[i]);
		
		return (float) Math.sqrt(tot);
	}
		
	private static final int
		SIN_BITS = 12,
		SIN_MASK = ~(-1 << SIN_BITS),
		SIN_COUNT = SIN_MASK + 1,
	   	ATAN2_SIZE = 1024;

	private static final double
		PI_2_4 = Math.PI/2,
		PI_6_4 = Math.PI*3/2,
		PI_8_4 = Math.PI*2,
	   	radFull = (double) (Math.PI * 2.0),
	   	degFull = (double) (360.0),
	   	radToIndex = SIN_COUNT / radFull,
	   	degToIndex = SIN_COUNT / degFull,
		SIN_TABLE[] = new double[SIN_COUNT],
		ATAN2_TABLE[] = new double[ATAN2_SIZE + 1];

	static {   
		for (int i = 0; i < SIN_COUNT; i++)
			SIN_TABLE[i] = (double) Math.sin((double) (i + 0.5f) / SIN_COUNT * radFull);
        for (int i = 0; i <= ATAN2_SIZE; i++)
            ATAN2_TABLE[i] = StrictMath.atan(1. * i / ATAN2_SIZE);
	}

	public static final double sinr(double rad) {return SIN_TABLE[(int) (rad * radToIndex) & SIN_MASK];}
	public static final double cosr(double rad) {return SIN_TABLE[(int) ((rad+Math.PI/2) * radToIndex) & SIN_MASK];}
	public static final double sind(double deg) {return SIN_TABLE[(int) (deg * degToIndex) & SIN_MASK];}
	public static final double cosd(double deg) {return SIN_TABLE[(int) ((deg+90) * degToIndex) & SIN_MASK];}

	public static final double atan2(double y, double x) {
	    if (x >= 0) {
	        if (y >= 0) {
	            if (x >= y) 	return ATAN2_TABLE[(int)(ATAN2_SIZE * y / x + 0.5)];				// 0 to 45
	            else			return PI_2_4 - ATAN2_TABLE[(int)(ATAN2_SIZE * x / y + 0.5)];	// 45 to 90
	        }
	        else {
	            if (x >= -y)	return PI_8_4 - ATAN2_TABLE[(int)(-ATAN2_SIZE * y / x + 0.5)];				// -45 to 0
	            else			return PI_6_4 + ATAN2_TABLE[(int)(-ATAN2_SIZE * x / y + 0.5)];				// 
	        }
	    }
	    else {
	        if (y >= 0) {
	            if (-x >= y)	return Math.PI - ATAN2_TABLE[(int)(-ATAN2_SIZE * y / x + 0.5)];
	            else			return PI_2_4 + ATAN2_TABLE[(int)(-ATAN2_SIZE * x / y + 0.5)];
	        }
	        else {
	            if (x <= y)		return Math.PI + ATAN2_TABLE[(int)(ATAN2_SIZE * y / x + 0.5)];
	            else			return PI_6_4 - ATAN2_TABLE[(int)(ATAN2_SIZE * x / y + 0.5)];
	        }
	    }
	}

	public static double calcAngleDiff(double a1, double a2) {
		return 180 - Math.abs(Math.abs(a1 - a2) - 180);
	}
	public static double calcAngleSubt(double a1, double a2) {
		int s = (a1 >= a2) ? ((a1 - a2 > 180) ? -1 : 1) : ((a2 - a1 > 180) ? 1 : -1);
		return s*(180 - Math.abs(Math.abs(a1 - a2) - 180));
	}
	
	public static float calcLenX(float dir) {return calcLenX(1,dir);}
	public static float calcLenX(float dis, float dir) {
		return (float) (dis*cosd(dir));
	}
	
	public static float calcLenY(float dir) {return calcLenY(1,dir);}
	public static float calcLenY(float dis, float dir) {
		return (float) (dis*sind(dir));
	}

	public static double calcAbsLenX(float dir) {return calcAbsLenX(1,dir);}
	public static double calcAbsLenX(float dis, float dir) {return Math.abs(calcLenX(dis,dir));}

	public static double calcAbsLenY(float dir) {return calcAbsLenY(1,dir);}
	public static double calcAbsLenY(float dis, float dir) {return Math.abs(calcLenY(dis,dir));}
	
	public static float calcPtDis(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
		
	public static float calcPtDir(float x1, float y1, float x2, float y2) {
		return (float) (atan2(y2-y1, x2-x1)/Math.PI*180);
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
	
	public static float[] calcLinePt(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
	    float dx, dy, t;
	    dx = x2-x1;
	    dy = y2-y1;
	    
	    if ((dx == 0) && (dy == 0)) {
	        tempPt[0] = x1;
	        tempPt[1] = y1;
	    }
	    else {
	        t = (dx*(px-x1) + dy*(py-y1)) / (dx*dx+dy*dy);
	        if(segment)
	        	t = MathExt.contain(0,t,1);
	        
	        tempPt[0] = x1 + t*dx;
	        tempPt[1] = y1 + t*dy;
	    }
	    
	    return tempPt;
	}
	
	public static float calcLineDis(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
	    calcLinePt(px,py,x1,y1,x2,y2,segment);
		float dis = calcPtDis(tempPt[0],tempPt[1], px, py);
		return dis;
	}
	public static float calcLineDir(float px, float py, float x1, float y1, float x2, float y2, boolean segment) {
		calcLinePt(px,py,x1,y1,x2,y2,segment);
		float dir = calcPtDir(px,py, tempPt[0],tempPt[1]);
		return dir;
	}

	public static float calcSmoothTurn(float dir, float toDir) {
		return calcSmoothTurn(dir, toDir, 8);
	}
	
	public static float calcSmoothTurn(float dir, float toDir, float spd) {
		float newDir = 0;
		
		if(Math.abs(((((toDir - dir) % 360) + 540) % 360) - 180) >= 160)
			    newDir += sind(toDir-dir)*16*15;

		newDir += sind(toDir-dir)*spd;
		return newDir;
	}
}
