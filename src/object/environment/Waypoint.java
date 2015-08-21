package object.environment;

import object.primitive.Positionable;
import functions.Math2D;
import gfx.GOGL;

public class Waypoint extends Positionable {
	
	public Waypoint(float x, float y, float z) {
		super(x, y, z, false, false);
	}

	public void draw() {
		GOGL.enableLighting();
		
		float upDir = GOGL.getTime()*5, dir = GOGL.getTime()*4;
		
		transformTranslation();
		GOGL.transformTranslation(0,0,Math2D.calcLenY(2,upDir));
		GOGL.transformRotationZ(dir);
		GOGL.draw3DFrustem(0,3,13,6);
		GOGL.transformScale(.75f,1.4f,1);
		GOGL.draw3DFrustem(0,5,10,6);
		GOGL.transformClear();
		
		GOGL.disableLighting();
	}

	public void update() {}
}
