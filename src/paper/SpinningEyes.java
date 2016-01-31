package paper;

import functions.Math2D;
import gfx.G3D;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import object.actor.Player;
import object.primitive.Positionable;

public class SpinningEyes extends Positionable {
	private float r = 12, baseH = 24, h = 8, sepW = 4, eyeW = 2;
	
	public SpinningEyes(float x, float y, float z) {
		super(x,y,z,false,false);
	}

	@Override
	public void draw() {
		ActorPM pl = PlayerPM.getInstance();
		float dir = Math2D.calcPtDir(x(),y(),pl.x(),pl.y());

		transformTranslation();
		GT.transformRotationZ(dir);
			GL.setColor(RGBA.BLACK);
			G3D.draw3DWall(r,sepW/2+eyeW, baseH+h,r,sepW/2, baseH);
			G3D.draw3DWall(r,-(sepW/2+eyeW), baseH+h,r,-sepW/2, baseH);
			GL.resetColor();
		GT.transformClear();
	}

	@Override
	public void add() {}
}
