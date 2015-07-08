package obj.prt;

import cont.TextureController;
import func.Math2D;
import gfx.Shape;
import gfx.TextureExt;

public class Dust extends Particle {
	private float spd, imageIndex;
	private boolean doFloat;
	private int imageNumber;
	private float x, y, z, direction;
	private TextureExt tex;
	
	public Dust(float x, float y, float z, float direction, boolean doFloat) {
		super(x, y, z);
		
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;
		this.doFloat = doFloat;
		
		this.tex = TextureController.getTextureExt("texDust");
		
		
		shape = Shape.createSprite("Dust", x,y,z, 10,10, tex.getFrame(0));
		spd = 1;
		
		imageIndex = 0;
		imageNumber = 7;
	}
	
	public void update(float deltaT) {
		float sp;
		sp = spd*((imageNumber - imageIndex)/imageNumber);

		//y += Math2D.lengthdirX(sp,direction);
		//z += Math2D.lengthdirY(sp,direction);

		    
		if(imageIndex >= imageNumber)
		    destroy();
		
		
		
		float s;
		s = 1;//sc;

		if(doFloat) {
			imageIndex += .5;
			
			shape.setPosition(x,y,z+4+imageIndex);
		   float a = .25f*.1f + ((imageNumber-imageIndex))/imageNumber;
		   shape.setAlpha(a);
		}
		else {
		    s *= Math.sqrt(imageIndex/imageNumber);

		    imageIndex += .3;
		    
		    shape.setPosition(x,y,z);
		    shape.setAlpha(.2f + ((imageNumber-imageIndex)/2)/imageNumber);
		}
		
		shape.setTexture(tex.getFrame(imageIndex));
		//d3d_draw_wall(-5*s,0,5*s,5*s,0,-5*s,sprite_get_texture(sprSmoke,image_index),1,1);
	}
}
