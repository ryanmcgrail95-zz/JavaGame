package paper;

import gfx.GOGL;
import com.jogamp.opengl.util.texture.Texture;
import cont.TextureController;

public class Background {
	
	private static Texture bgTex = TextureController.getTexture("bacMountains");
	private static float bgX = -280, bgW, bgH;
	
	public static void addPerpendicularMotion(float x) {
		bgX += .5f*x;
		if(bgX < 0)
			bgX += bgW;
		else if(bgX >= bgW)
			bgX -= bgW;
	}
	public static void draw() {
		bgH = GOGL.getScreenHeight();
		bgW = 1f*bgTex.getWidth()/bgTex.getHeight()*bgH;

		GOGL.drawTexture(bgX-bgW,bgH, bgW,-bgH, bgTex);
		GOGL.drawTexture(bgX,bgH, bgW,-bgH, bgTex);
	}
}
