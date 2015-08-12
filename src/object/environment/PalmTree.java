package object.environment;

import datatypes.vec3;
import functions.MathExt;
import gfx.GOGL;
import resource.model.Model;
import time.Timer;

public class PalmTree extends Tree {

	private int segmentNum;
	private float direction;
	private int[] segmentDirs;
			
	
	public PalmTree(float x, float y) {
		super(x,y);
		
		direction = MathExt.rnd(360);
		segmentNum = (int) MathExt.rnd(3,5);
		
		segmentDirs = new int[segmentNum];
		for(int i = 0; i < segmentNum; i++)
			segmentDirs[i] = (int) MathExt.rnd(-10,0);
	}
	
	public void draw() {
		GOGL.enableLighting();
		
		GOGL.transformClear();
		transformTranslation();
		
		if(checkShake())
			GOGL.transformTranslation(vec3.rnd(3));

		
		GOGL.transformRotationZ(direction);
		
		GOGL.transformScale(.5f);
		GOGL.transformScale(1,1,1);
		GOGL.setLightColori(56, 40, 30);

		
		float w = 20, h = 80, l = 30, s = .5f, rot = 20, e = .5f;

		int len = isEmpty() ? 1 : segmentNum;
		for(int i = 0; i < len; i++) {
			GOGL.transformRotationY(segmentDirs[i]);
			GOGL.draw3DFrustem(w,0,h*(1 + e),3,false);
			GOGL.transformTranslation(0,0,h);
		}
		
		
		
		if(!isEmpty()) {			
			GOGL.setLightColori(96, 104, 70);
			GOGL.transformScale(1.5f);
			
			for(int i = 0; i < 3; i++) {
				GOGL.transformRotationZ(120);
				GOGL.transformScale(1,s,1);
				GOGL.transformTranslation(l,0,0);
				GOGL.transformRotationY(rot);
				GOGL.draw3DFrustem(1.5f*l,0,7,3,false);
				GOGL.transformRotationY(-rot);
				GOGL.transformTranslation(-l,0,0);
				GOGL.transformScale(1,1/s,1);
			}
		}

		GOGL.transformClear();
				
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
	}
}
