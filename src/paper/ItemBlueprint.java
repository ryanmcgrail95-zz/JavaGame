package paper;

import gfx.G2D;
import gfx.GT;

import com.jogamp.opengl.util.texture.Texture;

public class ItemBlueprint {
	private Texture tex;
	
	public void draw3D(float x, float y, float z, Texture tex) {
		GT.transformClear();
			GT.transformTranslation(x,y,z);
			GT.transformPaper();
			G2D.drawTexture(-tex.getWidth()/2,-tex.getHeight(), tex);
		GT.transformClear();
	}
}
