package paper;

import time.Delta;
import btl.Shimmer;

import com.jogamp.opengl.util.texture.Texture;

import functions.Math2D;
import gfx.GT;
import gfx.G2D;
import gfx.TextureExt;
import cont.TextureController;

public class ShoePM implements AnimationsPM {
	private final static TextureExt shoeTex = TextureController.loadExt("Resources/Images/Characters/extra/shoe.gif", TextureController.M_BGALPHA);
	private float index = 0, size = 36, imageNumber = 3*shoeTex.getImageNumber();
		
	public ShoePM(float height) {
		size = .7f*height;
	}
	
	private void animateModel() {
		index += Delta.convert(.2f);
		
		if(index >= imageNumber)
			index -= imageNumber;
	}
	
	public float getExtraHeight() {
		return getInternalHeight() + size*.2f* (float) Math.pow(Math.abs(Math2D.calcLenY(360*index/imageNumber)), .5);
	}
	
	public float getInternalHeight() {
		return size*.4f;
	}
	
	public void draw(float depth) {
		animateModel();
		float dW = size, dH = size,
			dX = -dW/2, dY = dH;
		
		GT.transformTranslation(0,-getInternalHeight(),-depth);
		shoeTex.draw(dX,dY, dW, -dH, index);
		GT.transformTranslation(0,getInternalHeight(),depth);
	}
}

