package object.environment;

import object.primitive.Environmental;
import object.primitive.Physical;
import datatypes.vec3;
import gfx.GOGL;
import resource.model.Model;
import time.Timer;

public class Fern extends Environmental {

	private float squash = 0;
	private Timer twitchTimer;
	
	public Fern(float x, float y) {
		super(x,y,false,false);
		twitchTimer = new Timer(10,0);
	}
	
	public void destroy() {
		twitchTimer.destroy();
		super.destroy();
	}
	
	public void draw() {
		GOGL.enableLighting();
		
			GOGL.transformClear();
				transformTranslation();
				
				GOGL.transformScale(.5f);
				GOGL.transformScale(1+2f*squash,1+2f*squash,1-.2f*squash);
				GOGL.setLightColor(COL_LEAVES);
		
				Model.MOD_FERN.draw();	
			GOGL.transformClear();
			
		GOGL.resetColor();
		GOGL.disableLighting();
	}

	public boolean collide(Physical other) {
		squash += ( (other.checkCircle(x(),y(), 30)) ? 1 : 0 - squash)/5f;
		return false;
	}
}
