package btl;

import functions.Math2D;
import functions.MathExt;
import gfx.G2D;
import gfx.GL;
import gfx.GT;
import com.jogamp.opengl.util.texture.Texture;
import object.primitive.Drawable;
import time.Delta;

public class DodgeStar extends Drawable {
	private float x, y, z, dX, dZ;	
	private float index, distance, direction, drawDir, ddSign, size = 12, alphaTime = .4f;
	
	private static final Texture starTex = GL.loadTexture("Resources/Images/Battle/dodgeStar.png");
	
	public DodgeStar(float x, float z, float direction) {
		super(false,false);
		
		this.x = x;
		this.y = -10;
		this.z = z;
		
		this.distance = 16;
		this.direction = direction;
		
		ddSign = (direction > 90) ? 1 : -1;
	}
		
	public void update() {
		float amt;
		//amt = (float) Math.pow(index, .5f); //24
		//amt = Math.min(amt, 1);
			drawDir = MathExt.to(drawDir, 720*ddSign, 6);
		
		amt = drawDir/720*ddSign;
		amt *= distance;
		
		dX = x + amt*Math2D.calcLenX(direction);
		dZ = z + amt*Math2D.calcLenY(direction);
		
		index += Delta.convert(.04f);
		
		if(index >= 1 + alphaTime)
			destroy();
	}
	
	
	public void draw() {
		GT.transformClear();
			GT.transformTranslation(dX,y,dZ);
			GT.transformPaper();
			
			//Need to be rotated 180 degrees???
			if(index > 1)
				GL.setAlpha((alphaTime - (index-1))/alphaTime);
			GT.transformRotationZ(180 + drawDir);
			G2D.drawTexture(-size/2,-size/2,size,size, starTex);
			GL.resetColor();
		GT.transformClear();
	}

	public float calcDepth() {
		return 20;
	}

	@Override
	public void add() {}
}
