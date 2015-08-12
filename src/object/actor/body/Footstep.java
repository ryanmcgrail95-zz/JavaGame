package object.actor.body;

import functions.Math2D;
import gfx.GOGL;
import gfx.RGBA;
import object.primitive.Positionable;
import time.Timer;

public class Footstep extends Positionable {

	private float direction;

	private Timer destroyTimer = new Timer(5);
	private float dirZ, angle, sHor = .5f, sVer = .5f;
	private float[] pts;
	
	public Footstep(float x, float y, float z, float direction, float dirZ, float angle) {
		super(x, y, z, false,false);
		this.direction = direction;
		this.dirZ = dirZ;
		this.angle = angle;
		
		
		boolean n = true;
		float max = 5, min = 2;
		pts = new float[9];
		for(int i = 0; i < 9; i++) {
			pts[i] = n ? max : min;
			n = !n;
		}
	}

	public void draw() {
		if(destroyTimer.check())
			destroy();
		
		sHor += .3f;
		sVer += .7f;
		
		GOGL.transformClear();
		transformTranslation();
		GOGL.transformRotation(direction,dirZ);
		GOGL.transformScale(sVer,sHor,1);
		
		
		GOGL.setColor(RGBA.BLACK);
		GOGL.begin(GOGL.P_LINES);
			int len = pts.length;
			float dir, pX=0,pY=0, prevX, prevY;
			
			for(int i = 0; i < len; i++) {
				dir = angle*(i-len/2)/(len/2);

				if(i > 0) {
					prevX = pX;
					prevY = pY;
					pX = Math2D.calcLenX(pts[i],dir);
					pY = Math2D.calcLenY(pts[i],dir);

					GOGL.vertex(prevX,prevY,0);
					GOGL.vertex(pX,pY,0);
				}
				else {
					pX = Math2D.calcLenX(pts[i],dir);
					pY = Math2D.calcLenY(pts[i],dir);
				}
			}
			
		GOGL.end();
		GOGL.setColor(RGBA.WHITE);
		
		GOGL.transformClear();
	}
	public void update() {}	
}
