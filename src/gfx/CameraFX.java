package gfx;

import functions.Math2D;
import functions.MathExt;

public final class CameraFX {
	
	private CameraFX() {
	}
	
	
	private static void drawSpeedLinesP(float x, float y, int nLines) {

		float d, r, fullR,	x1,y1,x2,y2;
		fullR = 500;
		
		float cX = 320+x, cY = 240+y;
		
		GOGL.setOrtho();
		
			GOGL.setColor(RGBA.BLACK);
			
			for(int i = 0; i < nLines; i++) {
				r = MathExt.rnd(100,200);
				d = MathExt.rnd(360);
				
				x1 = cX+Math2D.calcLenX(r,d);
				y1 = cY+Math2D.calcLenY(r,d);
				x2 = cX+Math2D.calcLenX(fullR,d);
				y2 = cY+Math2D.calcLenY(fullR,d);
				
				GOGL.drawLine(x1,y1,x2,y2);
			}
		
		GOGL.setPerspective();
	}

	public static void drawSpeedLines(int nLines) {
		drawSpeedLinesP(0,0,nLines);
	}
	
	public static void drawShakingSpeedLines(int nLines) {
		float r=32, x,y;
		x = MathExt.rnd(-r,r);
		y = MathExt.rnd(-r,r);
		
		drawSpeedLinesP(x,y,nLines);
	}
}
