package object.environment;

import datatypes.vec3;
import gfx.GOGL;
import resource.model.Model;
import time.Timer;

public class PineTree extends Tree {

	
	
	public PineTree(float x, float y) {
		super(x,y);
	}
	
	public void draw() {
		GOGL.enableLighting();
		GOGL.transformClear();
		transformTranslation();
		
		if(checkShake())
			GOGL.transformTranslation(vec3.rnd(3));

		GOGL.transformScale(.5f);
		GOGL.transformScale(1,1,zScale);
		GOGL.setLightColori(56, 40, 30);

		Model.MOD_PINESTUMP.draw();
		
		if(!isEmpty()) {
			Model.MOD_PINETREE.draw();

			GOGL.setLightColori(96, 104, 70);
			GOGL.transformScale(1.5f);
			GOGL.transformTranslation(0,0,-10*zScale);
			Model.MOD_PINEBRANCHES.draw();
		}

		
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
	}
}
