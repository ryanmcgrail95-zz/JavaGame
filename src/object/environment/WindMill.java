package object.environment;

import functions.Math2D;
import gfx.Camera;
import gfx.GOGL;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.model.Model;

public class WindMill extends Environmental {

	public WindMill(float x, float y) {
		super(x, y, false,false);
		
		float dir,fR=100,fX,fY;
		Fence f = new Fence();
		for(int i = 0; i < 9; i++) {
			dir = 360f*i/9;
			fX = x + Math2D.calcLenX(fR,dir);
			fY = y + Math2D.calcLenY(fR,dir);
			f.add(fX,fY);
		}
	}

	public boolean collide(Physical other) {
		return false;
	}
	
	public void draw() {
		GOGL.enableLighting();
		
		GOGL.transformClear();
		GOGL.transformTranslation(x(),y(),z());
		GOGL.transformScale(.6f);
		
		GOGL.setLightColori(128,0,0);
		Model.MOD_WMBODY.draw();

		GOGL.setLightColori(255,255,255);
		Model.MOD_WMFRAME.draw();

		GOGL.transformTranslation(-100,0,370);
		GOGL.transformRotationX(GOGL.getTime());
		Model.MOD_WMBLADES.draw();
		
		GOGL.transformClear();
		
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
	}
}
