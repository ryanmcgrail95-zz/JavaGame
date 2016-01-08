package btl;

import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.MultiTexture;

public class DamageSmoke extends SimpleParticle {
	private static final MultiTexture TEXTURE = new MultiTexture("Resources/Images/smoke.png",7,1);	
	private float dDir, index;
	private float distance, speed;
	
	public DamageSmoke(float x, float y, float z, float direction) {
		super(x,y,z,32,direction, TEXTURE);
		index = 0;
		
		distance = MathExt.rnd(16,40);
		speed = MathExt.rnd(.2f, .4f);
	}	
	
	public float calcIndex() {
		float f;
		f = index/4;
				
		return Delta.convert(speed); //.25 //3*(.025f*(1-f) + .4f*f);
	}
	
	public void update() {
		float amt;
		amt = (float) Math.pow(index/4f, .5f)*distance; //24
		setDrawPosition(
			x() + amt*Math2D.calcLenX(getDirection()),
			y(),
			z() + amt*Math2D.calcLenY(getDirection()));
		
		index += calcIndex();
		
		if(index >= 7)
			destroy();
	}
	
	public float getAlpha() {
		return (float) (Math.pow((7 - index)/7f, .5f));
	}
	public int getFrame() {return (int) index;}
}
