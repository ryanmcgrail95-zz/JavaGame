package obj.prt;

import object.primitive.Drawable;
import object.primitive.Positionable;

public abstract class Particle extends Positionable {

	public Particle(float x, float y, float z) {
		super(x,y,z,false,false);
	}
}
