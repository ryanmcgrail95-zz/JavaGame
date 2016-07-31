package paper;

import gfx.G2D;
import gfx.GL;
import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;

public class Background {
	
	private static Texture bgTex = TextureController.getTexture("bacMountains");
	private static float bgX = -280, bgW, bgH;
	
	public static void addPerpendicularMotion(float x) {
		bgX += .5f*x * bgW/640;
		if(bgX < 0)
			bgX += bgW;
		else if(bgX >= bgW)
			bgX -= bgW;
	}
	public static void changeResolution(float oldW, float newW) {
		bgW = newW;
		
		bgX = bgX/oldW*newW;
		
		if(bgX < 0)
			bgX += bgW;
		else if(bgX >= bgW)
			bgX -= bgW;
	}
	public static void draw() {		
		bgH = GL.getInternalHeight();
		bgW = 1f*bgTex.getWidth()/bgTex.getHeight()*bgH;

		GL.setColorf(1,1,1);

		GL.unforceColor();
		GL.enableTextures();
		GL.enableBlending();
				
		GL.bind(bgTex);		
			//GL.setTextureRepeat(true);
			//GL.enableShader("battleBG");
			G2D.fillRectangle(bgX-bgW,0, bgW,bgH);
			G2D.fillRectangle(bgX,0, bgW,bgH);
			//G2D.fillRectangle();
			//GL.disableShaders();
		GL.unbind();
	}
}