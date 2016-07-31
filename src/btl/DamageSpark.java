package btl;

import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.MultiTexture;
import resource.image.Img;

public class DamageSpark extends SimpleParticle {
	private static final MultiTexture TEXTURE = new MultiTexture("Resources/Images/spark.png", Img.AlphaType.GRAYSCALE_MASK, 4,1);
	private float index;
	private float distance, speed, weight;
	
	public DamageSpark(float x, float y, float z, float direction, float weight, float disWeight, float spdWeight, float sizeWeight) {
		super(x,y,z,16*sizeWeight,direction, TEXTURE);
		index = 0;
		
		this.weight = weight; // .5
		
		distance = disWeight*MathExt.rndf(24,48);
		speed = spdWeight*MathExt.rndf(.2f, .3f);
	}	
	
	public float calcIndex() {
		float f;
		f = index/4;
				
		return speed; //.25 //3*(.025f*(1-f) + .4f*f);
	}
	
	public void update() {
		float amt;
		amt = (float) Math.pow(index/4f, weight)*distance; // .5

		setDrawPosition(
			x() + amt*Math2D.calcLenX(getDirection()),
			y(),
			z() + amt*Math2D.calcLenY(getDirection()));
		
		index += Delta.convert(calcIndex());
		
		if(index >= 4)
			destroy();
	}
	
	public float getAlpha() {return 1;}
	
	public int getFrame() {
		return (int) (4* Math.pow(index/4, weight));
	}
}
