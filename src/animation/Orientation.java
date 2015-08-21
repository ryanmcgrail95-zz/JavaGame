package animation;

import datatypes.vec3;

public class Orientation implements Interpolatable {
	private vec3 pos, rot;
	
	
	public Orientation(vec3 pos, vec3 rot) {
		this.pos = pos;
		this.rot = rot;
	}
	
	public vec3 getPosition() {
		return pos;
	}
	
	public vec3 getRotation() {
		return pos;
	}

	public Interpolatable tween(Interpolatable toValue, float amt) {
		Orientation toOri = (Orientation) toValue;
		return new Orientation(pos.interpolate(toOri.pos,amt),rot.interpolate(toOri.rot,amt));
	}
	public void updateValues(Interpolatable other) {
		Orientation toOri = (Orientation) other;
		pos.set(toOri.pos);
		rot.set(toOri.rot);
	}
}
