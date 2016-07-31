package gfx;

import io.IO;
import io.Keyboard;
import object.primitive.Drawable;

public class Face extends Drawable {
	private MultiTexture eyeTex;
	private float eyeX, eyeY, eyeInd = 0, eyeStartInd = 0, eyeIndNum = 1;
	
	public Face() {
		super(false, false);
		eyeTex = new MultiTexture("Resources/Images/eyes.png", 4, 8);

		System.out.println(eyeTex.getWidth() + ", " + eyeTex.getHeight());
		
		eyeX = -eyeTex.getWidth()/2;
		eyeY = -eyeTex.getHeight()/4 * 12;
	}
	
	public void draw() {
		drawEyes();
	}
	
	public void drawEyes() {
		boolean didSet = false;
		
		if(Keyboard.checkPressed('a')) {
			didSet = true;
			eyeStartInd = 0;
			eyeIndNum = 1;
		}
		else if(Keyboard.checkPressed('s')) {
			didSet = true;
			eyeStartInd = 4;
			eyeIndNum = 3;
		}
		else if(Keyboard.checkPressed('d')) {
			didSet = true;
			eyeStartInd = 8;
			eyeIndNum = 3;
		}
		else if(Keyboard.checkPressed('f')) {
			didSet = true;
			eyeStartInd = 12;
			eyeIndNum = 3;
		}
		
		if(didSet)
			eyeInd = 0;
		
		System.out.println("DRAWING!");
		
		eyeInd += .05;
		
		if(eyeInd >= eyeIndNum)
			eyeInd -= eyeIndNum;
		
		GL.clear(RGBA.WHITE);
		GT.transformClear();
		GT.transformTranslation(320,200,0);

		eyeTex.draw(eyeX,eyeY, getEyeIndex());

		GT.transformClear();
	}
	
	public float getEyeIndex() {
		return eyeStartInd + (float) Math.pow(eyeInd/eyeIndNum, 15)*eyeIndNum;
	}

	
	@Override
	public float calcDepth() {return 0;}

	@Override
	public boolean checkOnscreen() {return true;}
	
	public void add() {}
}
