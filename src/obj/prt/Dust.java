package obj.prt;

import gfx.G2D;
import gfx.GL;
import gfx.GT;
import gfx.MultiTexture;

public class Dust extends Particle {
	private float spd, imageIndex;
	private boolean doFloat;
	private int imageNumber;
	private static final MultiTexture dustTex = new MultiTexture("Resources/Images/smoke.png",7,1);	
	
	public Dust(float x, float y, float z, float direction, boolean doFloat) {
		super(x, y, z);
				
		this.doFloat = doFloat;		
		
		spd = .4f;
		
		imageIndex = 0;
		imageNumber = 6;
	}
	
	public void destroy() {
		super.destroy();
	}

	public void update() {}
	public void add() {}
	public void draw() {
		
		if(imageIndex >= imageNumber)
		    destroy();
		
		GT.transformClear();
		transformTranslation();
		GT.transformTranslation(0,0,0+imageIndex/2f);
		GT.transformPaper();
				
		if(doFloat)
			imageIndex += spd;

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
		
		/*float a = .25f*.1f + ((imageNumber-imageIndex))/imageNumber;
		GL.setAlpha(a);*/
		
		float s = 8;
		G2D.drawTexture(-s/2,-s/2,s,s, dustTex, (int)imageIndex);
		
		GT.transformClear();
		GL.setAlpha(1);
	}

}
