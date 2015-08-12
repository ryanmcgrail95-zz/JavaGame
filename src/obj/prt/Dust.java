package obj.prt;

import cont.TextureController;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.MultiTexture;
import gfx.RGBA;
import gfx.Shape;
import gfx.TextureExt;

public class Dust extends Particle {
	private float spd, imageIndex;
	private boolean doFloat;
	private int imageNumber;
	private float x, y, z, direction, rotation;
	private static MultiTexture dustTex;
	
	
	public Dust(float x, float y, float z, float direction, boolean doFloat) {
		super(x, y, z);
		
		dustTex = new MultiTexture("Resources/Images/smokecloud.png",4,4);
		
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
		rotation = MathExt.rnd(360);
		this.doFloat = doFloat;		
		
		spd = 1;
		
		imageIndex = 0;
		imageNumber = 15;
	}
	
	public void destroy() {
		super.destroy();
	}
	
	public void draw() {
		
		if(imageIndex >= imageNumber)
		    destroy();
		
				
		GOGL.transformClear();
		GOGL.transformTranslation(x,y,z+imageIndex/2f);
		GOGL.transformSprite();
		
		GOGL.transformRotationZ(rotation);
		
		float sp;
		sp = spd*((imageNumber - imageIndex)/imageNumber);

		//y += Math2D.lengthdirX(sp,direction);
		//z += Math2D.lengthdirY(sp,direction);

		
		
		if(doFloat)
			imageIndex += .5;

		/*if(doFloat) {
			imageIndex += .5;
			
			setPosition(x,y,z+4+imageIndex);
		   float a = .25f*.1f + ((imageNumber-imageIndex))/imageNumber;
		   shape.setAlpha(a);
		}
		else {
		    s *= Math.sqrt(imageIndex/imageNumber);

		    imageIndex += .3;
		    
		    shape.setPosition(x,y,z);
		    shape.setAlpha(.2f + ((imageNumber-imageIndex)/2)/imageNumber);
		}*/
		
		

		float a = .25f*.1f + ((imageNumber-imageIndex))/imageNumber;
		GOGL.setAlpha(a);
		
		
		GOGL.forceColor(RGBA.WHITE);
		float w = 50, h = 30, s = .8f;
		w *= s;
		h *= s;
		GOGL.drawTexture(-w/2,-h/2,w,h, dustTex, (int)imageIndex);
		GOGL.unforceColor();
		
		GOGL.transformClear();
		GOGL.setAlpha(1);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
