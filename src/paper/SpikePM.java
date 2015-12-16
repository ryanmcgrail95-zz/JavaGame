package paper;

import btl.Shimmer;

import com.jogamp.opengl.util.texture.Texture;

import gfx.GT;
import gfx.G2D;
import cont.TextureController;

public class SpikePM implements AnimationsPM {
	private final static Texture spikeTex = TextureController.loadExt("Resources/Images/Characters/extra/spike.png", TextureController.M_BGALPHA).getFrame(0);
	private float size = 36, yPlacement;
		
	public SpikePM(float height, float realHeight) {
		size = .4f*height;
		yPlacement = realHeight - 2;
	}
	
	public void draw() {
		float dW = size, dH = size,
			dX = -dW/2, dY = dH;
		
		GT.transformTranslation(0,yPlacement,-1);
		G2D.drawTexture(dX,dY, dW, -dH, spikeTex);
		GT.transformTranslation(0,-yPlacement,1);
	}

	public void shimmer(float x, float bZ) {
		new Shimmer(x, bZ + size*.7f);
	}
}

