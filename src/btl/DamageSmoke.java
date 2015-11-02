package btl;

import io.Keyboard;
import object.primitive.Drawable;
import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.MultiTexture;
import gfx.RGBA;

public class DamageSmoke extends Drawable {
	private static final MultiTexture TEXTURE = new MultiTexture("Resources/Images/smoke.png",7,1);	
	private float x, y, z, dX, dZ, direction, dDir, index;
	private float size = 32, distance, speed;
	
	public DamageSmoke(float x, float z, float direction) {
		super(false, false);
		this.x = x;
		this.y = -1;
		this.z = z;
		this.direction = direction;
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
		dX = x + amt*Math2D.calcLenX(direction);
		dZ = z + amt*Math2D.calcLenY(direction);
		
		index += calcIndex();
		
		if(index >= 7)
			destroy();
	}
	
	private float getAlpha() {
		return (float) (Math.pow((7 - index)/7f, .5f));
	}
	private int getFrame() {
		return (int) index;
	}
	
	public void draw() {
		GOGL.transformClear();
			GOGL.transformTranslation(dX,y,dZ);
			GOGL.transformPaper();
			
			//Need to be rotated 180 degrees???
			GOGL.transformRotationZ(direction+180);
			
			//GOGL.setColor(RGBA.GRAY_LIGHT);
			GOGL.setAlpha(getAlpha());
			GOGL.drawTexture(-size/2,-size/2,size,size, TEXTURE, getFrame());
			GOGL.resetColor();
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
