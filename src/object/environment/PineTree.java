package object.environment;

import ds.vec3;
import gfx.GOGL;
import resource.model.Model;
import time.Timer;

public class PineTree extends Tree {

	
	
	public PineTree(float x, float y) {
		super(x,y);
	}
	
	public void draw() {
		GOGL.transformClear();
		transformTranslation();
		
		GOGL.transformScale(.5f);
		GOGL.transformScale(1,1,zScale);
		GOGL.setLightColor(COL_TRUNK);

		Model.MOD_PINESTUMP.drawFast();
		
		if(!isEmpty()) {
			Model.MOD_PINETREE.drawFast();

			GOGL.setLightColor(COL_LEAVES); //44,103,116
			GOGL.transformScale(1.5f);
			GOGL.transformTranslation(0,0,-10*zScale);
			Model.MOD_PINEBRANCHES.drawFast();
		}
	}
	public void add() {
		//GOGL.enableLighting();
		GOGL.transformClear();
		transformTranslation();
		
		if(checkShake())
			GOGL.transformTranslation(vec3.rnd(6));

		GOGL.transformScale(.5f);
		GOGL.transformScale(1,1,zScale);
		GOGL.setLightColor(pickColor(COL_TRUNK));
		
		Model.MOD_PINESTUMP.add();
		
		if(!isEmpty()) {
			Model.MOD_PINETREE.add();

			GOGL.setLightColor(pickColor(COL_LEAVES)); //44,103,116
			GOGL.transformScale(1.5f);
			GOGL.transformTranslation(0,0,-10*zScale);
			Model.MOD_PINEBRANCHES.add();
		}

		
		//GOGL.setColor(1,1,1);
		//GOGL.disableLighting();
	}
}
