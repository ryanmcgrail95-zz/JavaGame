package object.primitive;

import paper.Bounds;

public abstract class Collideable extends Positionable {
	protected Bounds myBounds;
	
	
	public void destroy() {
		myBounds.destroy();
		
		super.destroy();
	}
	
	public Collideable(float x, float y, float z, boolean hoverable, boolean renderable) {
		super(x, y, z, hoverable, renderable);
		
		myBounds = new Bounds();
	}
	
	public void update() {
		super.update();
		
		myBounds.setPosition(x(), y(), z());
		myBounds.collideAll();
	}
}
