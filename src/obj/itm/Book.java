package obj.itm;

import gfx.GOGL;
import object.primitive.Drawable;

public class Book extends Drawable {

	
	
	public void draw() {
		GOGL.setOrtho();
		GOGL.setPerspective();
	}

	public boolean checkOnscreen() {
		return true;
	}

	@Override
	public float calcDepth() {
		return 0;
	}

	@Override
	public void update() {		
	}
}
