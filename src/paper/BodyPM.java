package paper;

import resource.sound.Sound;
import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.TextureExt;

public class BodyPM implements AnimationsPM {
	
	private CharacterPM myChar;
	private SpriteMap myMap;
	private TextureExt sprite;
	private float imageIndex, imageNumber;
	private byte animation;
	private boolean autoFlip;
	
	private WingsPM myWings;
	private SpikePM mySpike;
	
	private float stepZ = 0;
	
	public BodyPM(CharacterPM ch) {
		myChar = ch;
		myMap = myChar.getSpriteMap();
		
		if(myChar.getHasWings())
			myWings = new WingsPM(myChar.getSpriteHeight());
		mySpike = new SpikePM(myChar.getSpriteHeight(), myChar.getHeight());
		
		autoFlip = true;
	}
	
	public void setAnimationStill() {setAnimation(S_STILL);}
	public void setAnimationRun() 	{setAnimation(S_RUN);}
	public void setAnimationJump() 	{setAnimation(S_JUMP);}
	public void setAnimationLand() 	{setAnimation(S_LAND);}
	public void setAnimationHurt() 	{setAnimation(S_HURT);}
	public void setAnimationBurned(){setAnimation(S_BURNED);}
	public void setAnimationDodge() {setAnimation(S_DODGE);}
	
	public void setAnimation(byte type) {
		animation = type;
		sprite = myMap.get(type);
		imageNumber = sprite.getImageNumber();
	}
	
	private void animateModel() {
		imageIndex += Delta.convert(IMAGE_SPEED);
		if(imageIndex >= imageNumber)
			imageIndex -= imageNumber;
		
		if(checkAnimation(S_RUN) || checkAnimation(S_RUN_UP)) {
			float toUpZ = Math.abs(Math2D.calcLenY(2,imageIndex*30));
			stepZ = MathExt.to(stepZ, toUpZ, 1.5f);
		}
		else {
			stepZ = MathExt.to(stepZ, 0, 1.5f);
			imageIndex = 0;
		}
	}

	public void setAutoFlip(boolean autoFlip) {this.autoFlip = autoFlip;}
	
	public float getSpriteWidth() 	{return myChar.getSpriteWidth();}
	public float getSpriteHeight() 	{return myChar.getSpriteHeight();}
	public float getHeight() 		{return myChar.getHeight();}
	
	public void draw() {
		animateModel();
		
		float dX, dY, dW, dH;
		dW = myChar.getSpriteWidth();
		dH = myChar.getSpriteHeight();
		dX = -dW/2;
		dY = dH;
		
		if(myWings != null)
			GOGL.transformTranslation(0,myWings.getExtraHeight(),0);
		GOGL.transformTranslation(0,stepZ/32*dH,0);
		GOGL.drawTexture(dX,dY, dW, -dH, sprite.getFrame(imageIndex));
		mySpike.draw();
		if(myWings != null)
			myWings.draw();
	}
	
	public float getExtraBaseHeight() {
		if(myWings != null)
			return myWings.getExtraBaseHeight();
		else
			return 0;
	}

	public boolean checkAnimation(Byte animation) {
		return this.animation == animation;
	}

	public boolean hasSpike() {
		return mySpike != null;
	}
}
