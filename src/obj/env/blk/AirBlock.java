package obj.env.blk;

import com.jogamp.opengl.util.texture.Texture;

import gfx.GL;
import gfx.GT;
import object.primitive.Environmental;
import object.primitive.Physical;
import paper.ActorPM;

public class AirBlock extends Block {
	private float floorZ, w = 8, hitZ = 0, hitSpeed = 0, hitGravity = .25f;
	
	public AirBlock(float x, float y, float z, boolean inAir) {
		super(x,y,z + (inAir ? 48 : 0));
		floorZ = z;
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

		GT.transformClear();
		GL.draw3DFloor(x()-w,y()-w,x()+w,y()+w,floorZ + .1f, shadowTex);
		
		transformTranslation();
		GL.draw3DBlock(-w,-w,2*w,w,w,0, (Texture) null);
		GT.transformClear();
	}
	
	public boolean collide(Physical other) {
		ActorPM a;
		if(other instanceof ActorPM) {
			a = (ActorPM) other;
		
			if(other.z()+a.getHeight() > z())
				if(other.collideRectangle(x()-w, y()-w, 2*w, 2*w)) {
					
					return true;
				}
		}
		return false;
	}

	@Override
	public void add() {}
}
