package gfx;

import cont.TextureController;
import object.primitive.Drawable;
import window.GUIFrame;
import window.GUIPicross;
import window.GUISnake;

public class Gameboy extends Drawable {

	private static Gameboy inst;
	private GUIFrame screen, innerFrame;
	
	public Gameboy() {
		super(false,true);
		
		screen = new GUIFrame(0,0,160,144);

		/*if(game == SNAKE) s{
			innerFrame = new GUIFrame(8,0,16,16);
			innerFrame.setScale(9);
			innerFrame.add(new GUISnake(16,16));
			
			screen.add(innerFrame);
		}*/
		{
			screen.add(new GUIPicross());
		}
		
		inst = this;
	}
	
	
	public static Gameboy getInstance() {
		return inst;
	}
	
	
	public void render() {
		if(innerFrame != null)
			innerFrame.render();
		screen.render();
	}
	public void draw() {		
		
		GOGL.setColor(RGBA.WHITE);
		GOGL.transformClear();
		
		GOGL.transformBeforeCamera(50);		
		GOGL.transformRotationY(90);
		
		float s = 20, w = 14, sW = 7;
		
		GOGL.transformRotationZ(-90);
		GOGL.drawTexture(-w,-9,2*w,4*w, TextureController.getTexture("texGameboy"));

		GOGL.transformTranslation(0,0,-1);
		GOGL.drawFBO(-sW,-2.5f,  2*sW, 2*sW*(144f/160), screen.getFBO());
		
		GOGL.transformClear();
	}

	public boolean checkOnscreen() {
		return true;
	}

	public float calcDepth() {
		return 0;
	}

	public void update() {
	}
	
}
