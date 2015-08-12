package object.environment;

import object.primitive.Environmental;
import object.primitive.Physical;
import datatypes.vec3;
import gfx.GOGL;
import resource.model.Model;
import time.Timer;

public class Fern extends Environmental {

	public Fern(float x, float y) {
		super(x,y,false,false);
	}
	
	public void draw() {
		GOGL.enableLighting();
		
			GOGL.transformClear();
				transformTranslation();
				
				GOGL.transformScale(.5f);
				GOGL.setLightColori(96, 104, 70);
		
				Model.MOD_FERN.draw();	
			GOGL.transformClear();
			
		GOGL.resetColor();
		GOGL.disableLighting();
	}

	public boolean collide(Physical other) {
		return false;
	}
}
