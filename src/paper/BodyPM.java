package paper;

import obj.prt.Dust;
import resource.sound.Sound;
import time.Delta;
import functions.FastMath;
import functions.Math2D;
import functions.MathExt;
import gfx.GL;
import gfx.GT;
import gfx.TextureExt;

public class BodyPM implements AnimationsPM {
	
	private CharacterPM myChar;
	private TextureExt sprite;
	private float imageIndex, imageSpeed = IMAGE_SPEED, imageNumber,
		direction = 0,
		flipDir = 1;
	private int animation;
	private boolean autoFlip = true, isDestroyed, doStep, stepSound, autoIndex = true;

	private float 
		xScale = 1,
		yScale = 1;
	private byte 
		SC_CENTER = 0,
		SC_DOWN = 1,
		SC_UP = 2;
	private byte scaleDirection = SC_DOWN;
	
	private WingsPM myWings;
	private SpikePM mySpike;
	private ZapPM myZap;
	
	private float stepZ = 0;
	
	public BodyPM(CharacterPM ch) {
		setCharacter(ch);
		autoFlip = true;
	}
	
	public BodyPM(String name) {
		setCharacter(name);
		autoFlip = true;
	}
	
	public void enableSteps() {
		doStep = true;
	}
	
	public void setScale(float scale) {setScale(scale,scale);}
	public void setScale(float xScale, float yScale) {
		this.xScale = xScale;
		this.yScale = yScale;
	}

	public void setAnimationStill() {setAnimation(S_STILL);}
	public void setAnimationRun() 	{setAnimation(S_RUN);}
	public void setAnimationJump() 	{setAnimation(S_JUMP);}
	public void setAnimationLand() 	{setAnimation(S_LAND);}
	public void setAnimationHurt() 	{setAnimation(S_HURT);}
	public void setAnimationBurned(){setAnimation(S_BURNED);}
	public void setAnimationDodge() {setAnimation(S_DODGE);}
	public void setAnimationJumpFall() {setAnimation(S_JUMP_FALL);}
	public void setAnimationJumpLandHurt() {setAnimation(S_JUMP_LAND_HURT);}
	public void setAnimation(int type) {
		animation = type;
		sprite = myChar.getSpriteMap().get(animation);
		imageNumber = sprite.getImageNumber();
	}
	
	private float calcImageSpeed() {
		switch(animation) {
			case S_JUMP_LAND_HURT:
			case S_JUMP_FALL:
				return imageSpeed*.35f;
			default:	return imageSpeed;
		}
	}
	
	public void animateModel() {
		if(autoIndex)
			addIndex(Delta.convert(calcImageSpeed()));
									
		if(checkAnimation(S_RUN) || checkAnimation(S_RUN_UP)) {
			
			if(doStep)
				if(imageIndex > 4 && imageIndex < 6 && !stepSound) {
		        	Sound.play("Footstep");
		            stepSound = true;
		            //new Dust(x()+Math2D.calcLenX(5,direction+180),y()+Math2D.calcLenY(5,direction+180),z(), 0, true);
		        }
			
			float toUpZ = Math.abs(Math2D.calcLenY(2,imageIndex*30));
			stepZ = MathExt.to(stepZ, toUpZ, 1.5f);
		}
		else {
			stepZ = MathExt.to(stepZ, 0, 1.5f);
		}
		
		if(autoFlip) {
			float diff = FastMath.calcAngleSubt(GL.getCamera().getDirection(), direction);
			int flipSign = (int) Math.signum(diff);
			
			if(flipSign != 0)
				flipDir += flipSign*.1;
			flipDir = MathExt.contain(-1,flipDir,1);
		}
	}

	public void setAutoFlip(boolean autoFlip) {this.autoFlip = autoFlip;}
	
	public float getSpriteWidth() 	{return myChar.getSpriteWidth();}
	public float getSpriteHeight() 	{return myChar.getSpriteHeight();}
	public float getHeight() 		{return myChar.getHeight();}
	
	public void draw() {
				
		if(isDestroyed)
			return;
		
		float dX, dY, dW, dH;
		dW = myChar.getSpriteWidth();
		dH = myChar.getSpriteHeight();
		dX = -dW/2;
		dY = dH;
		
		GT.transformPaper(flipDir);
		
		if(myWings != null)
			GT.transformTranslation(0,myWings.getExtraHeight(),0);
		GT.transformTranslation(0,stepZ/32*dH,0);
		
		
		if(scaleDirection == SC_CENTER) {
			GT.transformTranslation(0,dH/2,0);
			GT.transformScale(xScale,yScale,1);
			GT.transformTranslation(0,-dH/2,0);
		}
		else if(scaleDirection == SC_DOWN)
			GT.transformScale(xScale,yScale,1);
		else if(scaleDirection == SC_UP)
			GT.transformScale(xScale,yScale,1);
		
		sprite.draw(dX,dY, dW, -dH, imageIndex);
		
		if(mySpike != null)
			mySpike.draw();
		if(myWings != null)
			myWings.draw();
		if(myZap != null)
			myZap.draw();
	}
	
	public float getExtraBaseHeight() {
		if(myWings != null)
			return myWings.getExtraBaseHeight();
		else
			return 0;
	}

	public boolean checkAnimation(int animation) {
		return this.animation == animation;
	}

	public boolean hasSpike() {
		return mySpike != null;
	}

	public void destroy() {
		isDestroyed = true;
		
		sprite = null;
		myChar = null;
		myWings = null;
		mySpike = null;
		myZap = null;
		//Wings & Spikes?
	}

	public void shimmerSpike(float x, float tZ) {
		if(mySpike != null)
			mySpike.shimmer(x, tZ);
	}

	public void setAutoIndex(boolean autoIndex) {
		this.autoIndex = autoIndex;
	}

	public void setIndex(float newInd) {
		imageIndex = newInd;
		while(imageIndex >= imageNumber) {
			imageIndex -= imageNumber;
			stepSound = false;
		}
	}
	public void addIndex(float add) {
		setIndex(imageIndex + add);
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	public void setCharacter(String name) {
		setCharacter(CharacterPM.getCharacter(name));
	}
	public void setCharacter(CharacterPM ch) {
		myChar = ch;
			imageSpeed = myChar.getImageSpeed();
		
		if(myChar.getHasWings())
			myWings = new WingsPM(myChar.getSpriteHeight());
		if(myChar.getHasSpike())
			mySpike = new SpikePM(myChar.getSpriteHeight(), myChar.getHeight());
		myZap = new ZapPM(myChar.getSpriteHeight());
	}
}
