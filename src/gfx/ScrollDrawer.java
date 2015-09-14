package gfx;

import com.jogamp.opengl.util.texture.Texture;

import functions.Math2D;

public final class ScrollDrawer {
	private ScrollDrawer() {}
	
	public static void draw(float x, float y, float w, float h,  float fracOpen, int tex) {
		float r0, r1, r;
		r0 = w/32;
		r1 = w/24;
		
		r = r0*fracOpen + r1*(1-fracOpen);
		
		draw(x,y,w,h,r,fracOpen,tex);
	}
	public static void draw(float x, float y, float w, float h,  float r, float fracOpen, int tex) {
		GOGL.bind(tex);
		
		if(fracOpen == 1)
			GOGL.fillRectangle(x-w/2,y-h/2,w,h);
		else {
			float numPts = 15;
			float xN,yN,uX,f;
			float nX,nY,nZ;
					
			float s, bodyW, exLen, curLen, ang;
			bodyW = (2*r)*(1-fracOpen) + w*fracOpen;
			s = (bodyW/w)/2;
			exLen = (w-bodyW)/2;
			
			float circ = 3.14159f*r*r;
			
			GOGL.fillRectangle(x-bodyW/2,y-h/2, bodyW,h, new float[] {.5f-s,0,.5f+s,1});
			
			for(int si = -1; si <= 1; si+=2) {
				GOGL.begin(GOGL.P_TRIANGLE_STRIP);
				for(int i = 0; i < numPts; i++) {
					f = i/(numPts-1);
					curLen = f*exLen;//(exLen)*(1-f) + f*(w/2-r);
					ang = -90 + si*curLen/circ*360;
					
					xN = Math2D.calcLenX(r,ang);
					yN = Math2D.calcLenY(r,ang);
					uX = .5f + si*(s + f*(.5f-s));
					
					nX = si*xN;
					nY = 0;
					nZ = yN;
					
					GOGL.vertex(x+si*(bodyW/2+xN), y-h/2, r+yN, uX,0, nX,nY,nZ);
					GOGL.vertex(x+si*(bodyW/2+xN), y+h/2, r+yN, uX,1, nX,nY,nZ);
				}
				GOGL.end();
			}
		}
		
		GOGL.bind(0);
	}
}
