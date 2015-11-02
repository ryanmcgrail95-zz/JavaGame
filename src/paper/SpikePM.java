package paper;

import com.jogamp.opengl.util.texture.Texture;

import gfx.GOGL;
import cont.TextureController;

public class SpikePM implements AnimationsPM {
	private final static Texture spikeTex = TextureController.loadExt("Resources/Images/Characters/extra/spike.png", TextureController.M_BGALPHA).getFrame(0);
	private float size = 36, yPlacement;
		
	public SpikePM(float height, float realHeight) {
		size = .4f*height;
		yPlacement = realHeight - 2;
	}
	
	public void draw() {
		float dX, dY, dW, dH;
		dW = dH = size;
		dX = -dW/2;
		dY = dH;
		
		GOGL.transformTranslation(0,yPlacement,-1);
		GOGL.drawTexture(dX,dY, dW, -dH, spikeTex);
		GOGL.transformTranslation(0,-yPlacement,1);
	}
}

