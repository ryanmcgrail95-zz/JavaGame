package paper;

import functions.Math2D;
import functions.MathExt;
import gfx.GL;
import object.primitive.Physical;

public class CameraFollowZone extends EventZone {
	private float focusX, focusY, minDir, maxDir;
	
	public CameraFollowZone(float x1, float y1, float z1, float x2, float y2, float z2,
							float focusX, float focusY, float minDir, float maxDir) {
		super(x1, y1, z1, x2, y2, z2);
		this.focusX = focusX;
		this.focusY = focusY;
		this.maxDir = minDir;
		this.maxDir = maxDir;
	}

	public void event(Physical other) {
		if(other instanceof PlayerPM) {
			PlayerPM p = PlayerPM.getInstance();
			p.setCameraDirection( MathExt.contain(minDir, Math2D.calcPtDir(p.x(),p.y(),focusX,focusY), maxDir) );
		}
	}
}
