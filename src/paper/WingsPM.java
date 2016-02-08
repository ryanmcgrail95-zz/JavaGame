package paper;

import time.Delta;
import gfx.GT;
import gfx.G2D;
import gfx.GL;
import gfx.TextureExt;
import cont.TextureController;

public class WingsPM implements AnimationsPM {
	private final static TextureExt wingTex = TextureController.loadExt("Resources/Images/Characters/extra/wings.gif", TextureController.M_BGALPHA);
	private int IMAGE_NUMBER = wingTex.getImageNumber();
	private float imageIndex;
	private float size = 36, downH = -size*.1f;
	
	private float exHeight = 0;
	
	public WingsPM(float height) {
		size = height;
	}

	private void animateModel() {
		imageIndex += Delta.convert(IMAGE_SPEED*.5f);
		if(imageIndex >= IMAGE_NUMBER)
			imageIndex -= IMAGE_NUMBER;
		
		float b, t, to;
		b = 0;
		t = 1;
		if(imageIndex < IMAGE_NUMBER/2f)
			exHeight += (t - exHeight)/2f;
		else
			exHeight += (b - exHeight)/4f;
	}
	
	public float getExtraHeight() {
		return exHeight*size*.1f;//Math2D.calcLenY(size*.2f,180*imageIndex/IMAGE_NUMBER);
	}
	public float getExtraBaseHeight() {
		return size*.1f;
	}
	
	public void draw() {
		GL.start("draw()");
		
		animateModel();
		
		float dX, dY, dW, dH;
		dW = size;
		dH = size;
		dX = -dW/2;
		dY = dH;
		
		GT.transformTranslation(0,downH,-1);		
		wingTex.draw(dX,dY, dW, -dH, imageIndex);
		GT.transformTranslation(0,-downH,1);

		GL.end("draw()");
	}
}
