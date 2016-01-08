package btl;

import functions.Math2D;
import functions.MathExt;
import gfx.Sprite;
import cont.TextureController;
import time.Delta;

public class DodgeStar extends SimpleParticle {
	private float index, distance, starDir, ddSign, alphaTime = .4f;
	
	private static final Sprite starTex = TextureController.getTextureExt("texDodgeStar");
	
	public DodgeStar(float x, float y, float z, float starDir) {
		super(x,y,z,12,0, starTex);
			
		this.distance = 16;
		this.starDir = starDir;
		ddSign = (starDir > 90) ? 1 : -1;
	}
		
	public void update() {
		float amt;
		//amt = (float) Math.pow(index, .5f); //24
		//amt = Math.min(amt, 1);
			setDirection(MathExt.to(getDirection(), 720*ddSign, 6));
		
		amt = getDirection()/720*ddSign;
		amt *= distance;
		
		setDrawPosition(
			x() + amt*Math2D.calcLenX(starDir),
			y(),
			z() + amt*Math2D.calcLenY(starDir));
		
		index += Delta.convert(.04f);
		
		if(index >= 1 + alphaTime)
			destroy();
	}
	
	@Override
	public float getAlpha() {
		if (index > 1)
			return (alphaTime - (index-1))/alphaTime;
		else
			return 1;
	}

	@Override
	public int getFrame() {return 0;}
}
