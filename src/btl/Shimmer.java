package btl;

import object.primitive.Drawable;

import com.jogamp.opengl.util.texture.Texture;

import time.Delta;
import gfx.G2D;
import gfx.GL;
import gfx.GT;

public class Shimmer extends Drawable {
	private static final Texture TEXTURE = GL.loadTexture("Resources/Images/shimmer.png");
	private float x, y, z, direction, index;
	private float size = 300;
	
	public Shimmer(float x, float z) {
		super(false, false);
		this.x = x;
		this.y = -10;
		this.z = z;
		this.direction = 360;
		index = 0;		
	}	
	
	public void update() {
		index += Delta.convert( calcSpeed(.05f, .025f) );
		if(index >= 1)
			destroy();
	}
	
	public float calcSpeed(float start, float end) {
		float f = index, nF = 1-index;
		return nF*start + f*end;
	}
	
	public void draw() {
		GT.transformClear();
			GT.transformTranslation(x,y,z);
			GT.transformPaper();
			
			GL.setHidden(false);
			GL.setAlpha(1-index);
			
			GT.transformScale(index);
			GT.transformRotationZ(index*direction);
			G2D.drawTexture(-size/2,-size/2,size,size, TEXTURE);
			
			GL.setHidden(true);
			GL.resetColor();
		GT.transformClear();
	}

	public float calcDepth() {
		return 20;
	}
	@Override
	public void add() {}
}
