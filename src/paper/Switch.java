package paper;

import gfx.G3D;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import object.primitive.Physical;
import object.primitive.Positionable;

public class Switch extends Physical {

	public Switch(float x, float y, float z) {
		super(x, y, z);
	}

	public void update() {
		super.update();
	}
	
	public boolean collide(Physical other) {
		activate();
		return false;
	}

	public void land() {
		setZVelocity(0);
	}

	@Override
	public void draw() {		
		transformTranslation();
			GL.setColor(RGBA.YELLOW);
			G3D.draw3DFrustem(0,0,0, 12,12, 7, 6);
			GL.setColor(RGBA.BLUE);
			G3D.draw3DSphere(0,0,10, 12,16, null, 10);
		GT.transformClear();
	}

	public void add() {		
	}
	
	public void activate() {
		
	}
}
