package gfx;

import object.primitive.Drawable;
import cont.TextureController;

public class PageTransition extends Drawable {
	private TextureExt basis = TextureController.loadExt("Resources/Images/battle.png", TextureController.M_NORMAL);
	private float time = 0;
	
	
	public PageTransition() {
		super(false, false);
	}
	
	public float calcDepth() {
		return 1;
	}
	
	public void draw() {
		float w = GOGL.SCREEN_WIDTH,
			  h = GOGL.SCREEN_HEIGHT;
		
		time += .05;
		
		GOGL.enableShader("PageCurl");
		GOGL.passShaderFloat("iGlobalTime", time);
		
		GOGL.passShaderVec2("tRes", new float[] {basis.getWidth(), basis.getHeight()} );
		
		GOGL.setOrtho();
		GOGL.transformClear();
			GOGL.drawTexture(0,0, w,h, basis.getFrame(0));
		GOGL.transformClear();
		GOGL.disableShaders();
	}
	public void add() {
	}
}
