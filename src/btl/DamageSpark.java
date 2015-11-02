package btl;

import io.Keyboard;
import object.primitive.Drawable;
import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.MultiTexture;

public class DamageSpark extends Drawable {
	private static final MultiTexture TEXTURE = new MultiTexture("Resources/Images/spark.png",4,1,true);	
	private float x, y, z, dX, dZ, direction, dDir, index;
	private float size = 16, distance, speed;
	
	public DamageSpark(float x, float z, float direction) {
		super(false, false);
		this.x = x;
		this.y = -10;
		this.z = z;
		this.direction = direction;
		index = 0;
		
		distance = MathExt.rnd(24,48);
		speed = MathExt.rnd(.2f, .3f);
	}	
	
	public float calcIndex() {
		float f;
		f = index/4;
				
		return speed; //.25 //3*(.025f*(1-f) + .4f*f);
	}
	
	public void update() {
		float amt;
		amt = (float) Math.pow(index/4f, .5f)*distance; //24
		dX = x + amt*Math2D.calcLenX(direction);
		dZ = z + amt*Math2D.calcLenY(direction);
		
		index += Delta.convert(calcIndex());
		
		if(index >= 4)
			destroy();
	}
	
	private int getFrame() {
		return (int) (4* Math.pow(index/4, .5));
	}
	
	public void draw() {
		GOGL.transformClear();
			GOGL.transformTranslation(dX,y,dZ);
			GOGL.transformPaper();
			
			//Need to be rotated 180 degrees???
			GOGL.transformRotationZ(direction);
			GOGL.drawTexture(-size/2,-size/2,size,size, TEXTURE, getFrame());
		GOGL.transformClear();
	}

	public float calcDepth() {
		return 20;
	}
	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}
}
