package obj.env.blk;

import com.jogamp.opengl.util.texture.Texture;

import gfx.GL;
import gfx.GT;
import object.primitive.Environmental;
import object.primitive.Physical;

public class AirBlock extends Environmental {
	private float w = 8, hitZ = 0, hitSpeed = 0, hitGravity = .25f;
	
	public AirBlock(float x, float y, float z) {
		super(x,y,z+32, false,false);
	}
	
	public void draw() {
		//BEGIN STEP
		hitZ += hitSpeed;
	
		if(hitZ > 0)
			if(hitSpeed > -5)
				hitSpeed -= hitGravity;
	
		if(hitZ < 0) {
		    hitZ = 0;
		    hitSpeed = 0;
		}
		
		transformTranslation();
		GL.draw3DBlock(-w,-w,2*w,w,w,0, (Texture) null);
		GT.transformClear();
	}
	
	public boolean collide(Physical other) {
		return false;
	}

	@Override
	public void add() {}
}
