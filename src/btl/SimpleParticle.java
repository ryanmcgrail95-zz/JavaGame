package btl;

import gfx.G2D;
import gfx.GL;
import gfx.GT;
import gfx.Sprite;

public abstract class SimpleParticle extends Particle {
	private Sprite sprite;
	private float dX,dY,dZ;
	private float size;
	
	public SimpleParticle(float x, float y, float z, float size, float direction, Sprite sprite) {
		super(x,y,z);
		this.size = size;
		this.sprite = sprite;
		setDirection(direction);
	}

	public void setDrawPosition(float dX, float dY, float dZ) {
		this.dX = dX;
		this.dY = dY;
		this.dZ = dZ;
	}
	public void setSize(float size) {this.size = size;}

	public abstract float getAlpha();
	public abstract int getFrame();
	
	public void add() {}
	public void draw() {
		GL.setAlpha(getAlpha());
		
		GT.transformClear();
			GT.transformTranslation(dX,dY,dZ);
			GT.transformPaper();			
			GT.transformRotationZ(getDirection());
			sprite.draw(-size/2,-size/2,size,size, getFrame());
		GT.transformClear();
		
		GL.resetColor();
	}
}
