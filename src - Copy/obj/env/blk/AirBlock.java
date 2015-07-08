package obj.env.blk;

import gfx.Shape;
import obj.prim.Environmental;
import obj.prim.Physical;

public class AirBlock extends Environmental {
	private float w = 16;
	
	public AirBlock() {
		shape = Shape.createBlock("ABlock", -w, -w, w, w, w, 0, null);
	}
	
	public void update(float deltaT) {
		//BEGIN STEP
			/*hitZ += hitSpeed;
	
			if(hitZ > 0)
			    if(hitSpeed > -5)
			        hitSpeed -= hitGravity;
	
			if(hitZ < 0) {
			    hitZ = 0;
			    hitSpeed = 0;
			}*/
	}
	
	public boolean collide(Physical other) {
		return false;
	}
}
