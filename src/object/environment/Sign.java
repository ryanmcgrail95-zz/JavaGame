package object.environment;

import object.primitive.Environmental;
import object.primitive.Physical;
import gfx.FBO;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;

public class Sign extends Environmental {
	
	private int width, height;
	private FBO image;
	private String text;
	private float scale;
	

	public Sign(float x, float y, int w, int h, int aa, String text) {
		super(x, y, true, true);
		
		width = w;
		height = h;

		
		image = new FBO(GOGL.gl,w*aa,h*aa);

		
		float xS,yS;
		xS = image.getWidth()/GLText.getStringWidth(text);
		yS = image.getHeight()/GLText.getStringHeight(text);
		scale = Math.min(xS,yS);
		
		
		
		this.text = text;
	}

	public void render() {
		image.attach(GOGL.gl);
		
			GOGL.clearScreen(RGBA.WHITE);
			GLText.drawStringCentered(image.getWidth()/2,image.getHeight()/2,scale,scale,text);
		
		image.detach(GOGL.gl);
	}
	
	public void draw() {
		GOGL.transformClear();
		transformTranslation();

		float poleW, poleH;
		poleW = 3;
		poleH = 12;

		GOGL.transformRotationY(-90);
		GOGL.transformRotationZ(90);
		
		GOGL.drawFBO(-width/2,-poleH-height, width,height, image);
		
		GOGL.fillRectangle(-poleW/2,-poleH, poleW,poleH);
		
		GOGL.transformClear();
	}

	@Override
	public boolean collide(Physical other) {
		return false;
	}
}
