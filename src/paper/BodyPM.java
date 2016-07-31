package paper;

import cont.Log;
import cont.TextureController;
import obj.prt.Dust;
import object.primitive.Positionable;
import resource.sound.Sound;
import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import gfx.TextureExt;

public class BodyPM implements AnimationsPM {
	
	private Positionable parent;
	private CharacterPM myChar;
	private TextureExt sprite;
	private float imageIndex, imageSpeed = IMAGE_SPEED, imageNumber,
		direction = 0,
		flipDir = 1,
		spinDir = 0;
	private float
		x, y, z,
		xRot, yRot, zRot, alpha = 1;
	private int animation;
	private boolean autoFlip = true, isDestroyed, doStep, stepSound, autoIndex = true;

	private RGBA forceColor;
	
	private float
		floorZ = 0;
	
	private float 
		xScale = 1,
		yScale = 1,
		xySpd = 0,
		zVel = 0;
	private byte 
		SC_CENTER = 0,
		SC_DOWN = 1,
		SC_UP = 2;
	private byte scaleDirection = SC_DOWN;
	
	private WingsPM myWings;
	private SpikePM mySpike;
	private ZapPM 	myZap;
	private ShoePM 	myShoe;
	
	// BLUR VARIABLES
	private boolean doBlur = false;
	private final int blurNum = 10, blurVarNum = 7;
	private float blurAlpha = .5f;
	private float[] blurPos;
	
	private float stepZ = 0;
	
	public BodyPM(CharacterPM ch) {
		setCharacter(ch);
		
		ini();
	}
	
	public void enableDoBlur(boolean doBlur) {
		this.doBlur = doBlur;
	}
	
	public BodyPM(Positionable parent, String name) {
		this.parent = parent;
		setCharacter(name);
		
		ini();
	}
	
	private void ini() {
		autoFlip = true;
		
		blurPos = new float[blurNum * blurVarNum];
	}
	
	
	public void enableSteps() {
		doStep = true;
	}
	
	public void setScale(float scale) {setScale(scale,scale);}
	public void setScale(float xScale, float yScale) {
		this.xScale = xScale;
		this.yScale = yScale;
	}

	public void setAnimationStill() 		{setAnimation(S_STILL);}
	public void setAnimationRun() 			{setAnimation(S_RUN);}
	public void setAnimationJump() 			{setAnimation(S_JUMP);}
	public void setAnimationLand() 	{setAnimation(S_LAND);}
	public void setAnimationHurt() 	{setAnimation(S_HURT);}
	public void setAnimationBurned(){setAnimation(S_BURNED);}
	public void setAnimationSpin()	{setAnimation(S_SPIN);}
	public void setAnimationDodge() {setAnimation(S_DODGE);}
	public void setAnimationJumpFall() 		{setAnimation(S_JUMP_FALL);}
	public void setAnimationJumpLandHurt() 	{setAnimation(S_JUMP_LAND_HURT);}
	public void setAnimation(int type) {
		animation = type;
		
		if(myChar == null)
			throw new NullPointerException();
		else if(myChar.getSpriteMap() == null)
			throw new NullPointerException();
		
		sprite = myChar.getSpriteMap().get(animation);
		imageNumber = sprite.getImageNumber();
	}
	
	private float calcImageSpeed() {
		switch(animation) {
			case S_RUN:
			case S_RUN_UP:
				return imageSpeed*xySpd/3;
		
			case S_JUMP_LAND_HURT:
			case S_JUMP_FALL:
				return imageSpeed*.35f;
			default:	return imageSpeed;
		}
	}

	public float getFrame() {
		float frameDis = MathExt.wrapDiff(imageIndex, 0,4,imageNumber),
			maxDis = 4.6f,
			index;

		if(Math.abs(frameDis) > maxDis)
			index = 0;
		else
			index = (float) (4 + Math.signum(frameDis)* (Math.abs(frameDis)) );
				
		return MathExt.wrap(0,index,imageNumber);
	}
	private float calcStepZ() {
		float frameDis = MathExt.wrapDis(imageIndex, 0,4,imageNumber),
			maxDis = 4.5f, upHeight = 3;

		if(frameDis > maxDis)
			return 0;
		else
			return (float) Math.pow((maxDis-frameDis)/maxDis,2) * upHeight;
	}
	
	public void animateModel() {
		//GL.start("BodyPM.animateModel()");
		
		if(autoIndex)
			addIndex(Delta.convert(calcImageSpeed()));
									
		if(checkAnimation(S_RUN) || checkAnimation(S_RUN_UP)) {
			
			if(doStep)
				if(imageIndex > 4 && imageIndex < 6 && !stepSound) {					
		        	Sound.play("footstep", parent);
		            stepSound = true;
		            
		            new Dust(x+Math2D.calcLenX(5,direction+180),y+Math2D.calcLenY(5,direction+180),z+5, 0, true);
		        }
			
			float frames = 4,
				toUpZ = calcStepZ();
			stepZ = MathExt.to(stepZ, toUpZ, 1.5f);
		}
		else {
			stepZ = MathExt.to(stepZ, 0, 1.5f);
		}
		
		spinDir = checkAnimation(S_SPIN) ? spinDir+.25f : 0;
		
		if(autoFlip) {
			float diff = (float) Math2D.calcAngleSubt(GL.getCamera().getDirection(), direction);
			int flipSign = (int) Math.signum(diff);
			
			if(flipSign != 0)
				flipDir += flipSign*.1;
			flipDir = MathExt.contain(-1,flipDir,1);
		}
		
		myZap.animateModel();
		
		//NOTE: THIS WILL NOT WORK, NEED TO MAKE INTO SEPARATE OBJECT!!!!!!!!!!!!!!!!!!

		if(doBlur) {
			for(int i = blurPos.length-1-blurVarNum; i >= 0; i--)
				blurPos[i+blurVarNum] = blurPos[i];
			blurPos[0] = x;
			blurPos[1] = y;
			blurPos[2] = z;
			blurPos[3] = floorZ;
			blurPos[4] = xScale;
			blurPos[5] = yScale;
			blurPos[6] = imageIndex;
		}

		//GL.end("BodyPM.animateModel()");
	}

	public void setAutoFlip(boolean autoFlip) {this.autoFlip = autoFlip;}
	
	public float getSpriteWidth() 	{return myChar.getSpriteWidth();}
	public float getSpriteHeight() 	{return myChar.getSpriteHeight();}
	public float getHeight() 		{return myChar.getHeight();}
	
	
	public void setRotation(float xRot, float yRot, float zRot) {
		this.xRot = xRot;
		this.yRot = yRot;
		this.zRot = zRot;
	}
	
	public void drawShadow(float x, float y, float floorZ, int blurInd) {
		if(z-floorZ < -.5)
			return;
		
		//GL.start("Body.drawShadow()");

		
		GT.transformClear();
		GT.transformTranslation(x, y, floorZ);
		GT.transformRotationZ(zRot);
		
		GT.transformPaper(flipDir + (animation==S_SPIN?spinDir:0));
		
		float maxDis = 128;
		GT.transformScale((maxDis - (z-floorZ))/maxDis);

		GL.forceColor(RGBA.BLACK);
		float shH = getSpriteWidth()*.4f/2, shW = shH*1.5f;

		
		if(blurInd > 0)
			GL.setAlpha(.8f * alpha * blurAlpha * (blurNum - 1f*blurInd)/blurNum);
		else
			GL.setAlpha(.8f * alpha);
		GL.draw3DWall(-shW,.1f,shH,shW,.1f,-shH, TextureController.getTexture("texShadow"));
		GL.unforceColor();
		GL.setAlpha(1);

		//GL.end("Body.drawShadow()");
	}
	
	public void draw() {
		
		//GL.start("Body.draw()");

		if(!doBlur)
			draw(x,y,z);
		else {
			if(Math2D.calcAngleDiff(GL.getCamera().getDirection(),direction) < 90) {
				draw(x,y,z);
				for(int i = 1; i < blurNum; i++)
					draw(blurPos[blurVarNum*i], blurPos[blurVarNum*i+1], blurPos[blurVarNum*i+2], blurPos[blurVarNum*i+3], blurPos[blurVarNum*i+4], blurPos[blurVarNum*i+5], blurPos[blurVarNum*i+6], i);
			}
			else {
				for(int i = blurNum-1; i >= 1; i--)
					draw(blurPos[blurVarNum*i], blurPos[blurVarNum*i+1], blurPos[blurVarNum*i+2], blurPos[blurVarNum*i+3], blurPos[blurVarNum*i+4], blurPos[blurVarNum*i+5], blurPos[blurVarNum*i+6], i);			
				draw(x,y,z);
			}
		}	
		GL.setAlpha(1);
		
		//GL.end("Body.draw()");
	}
	
	private void draw(float x, float y, float z) {
		draw(x,y,z,floorZ,xScale,yScale,imageIndex,0);
	}
	private void draw(float x, float y, float z, float floorZ, float xScale, float yScale, float imageIndex, int blurInd) {

		//GL.start("Body.draw(...)");

		if(isDestroyed)
			return;
		
		float dX, dY, dW, dH;
		dW = myChar.getSpriteWidth();
		dH = myChar.getSpriteHeight();
		dX = -dW/2;
		dY = dH;

		drawShadow(x,y,floorZ, blurInd);

		GT.transformClear();
		GT.transformTranslation(x, y, z-1);	
		GT.transformRotationZ(zRot);
		GT.transformPaper(flipDir + (animation==S_SPIN?spinDir:0));
		GT.transformRotationX(xRot);
		
		//Log.println(Log.ID.RESOURCE, true, "BodyPM.draw(...)-translation");
		if(myShoe != null)
			GT.transformTranslation(0,myShoe.getExtraHeight(),0);
		else if(myWings != null)
			GT.transformTranslation(0,myWings.getExtraHeight(),0);
		else
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
		//GL.end(Log.ID.RESOURCE, false, "Body.draw(...)-translation");
				
		
		if(forceColor != null)
			GL.forceColor(forceColor);
		
		//GL.start("Body.draw(...)-alpha");
		if(blurInd > 0)
			GL.setAlpha(alpha * blurAlpha*(blurNum - 1f*blurInd)/blurNum);
		else
			GL.setAlpha(alpha);
		//GL.end("Body.draw(...)-alpha");

		if(sprite != null)
			sprite.draw(dX,dY, dW, -dH, getFrame());
		
		//GL.start("Body.draw(...)-subparts");
		if(mySpike != null)
			mySpike.draw();
		if(myWings != null)
			myWings.draw();
		if(myZap != null)
			myZap.draw();
		if(myShoe != null)
			myShoe.draw(-flipDir);
		//GL.end("Body.draw(...)-subparts");
		
		GT.transformClear();

		GL.unforceColor();
		
		//GL.end("Body.draw(...)");
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

	public boolean hasSpike() {return mySpike != null;}

	public void destroy() {
		isDestroyed = true;
		
		myChar.removeReference();
		
		sprite = null;
		myChar = null;
		myWings = null;
		mySpike = null;
		myZap = null;
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
		
		if(imageNumber > 0)
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
		CharacterPM c = CharacterPM.getCharacter(name, true); 
		setCharacter(c);
	}
	public void setCharacter(CharacterPM ch) {
		if(myChar != null)
			myChar.removeReference();
		
		myChar = ch;
		myChar.addReference();
		
		myChar.load();
			imageSpeed = myChar.getImageSpeed();
		
		if(myChar.getHasWings())
			myWings = new WingsPM(myChar.getSpriteHeight());
		if(myChar.getHasSpike())
			mySpike = new SpikePM(myChar.getSpriteHeight(), myChar.getHeight());
		myZap = new ZapPM(myChar.getSpriteHeight());
		if(myChar.getHasShoe())
			myShoe = new ShoePM(myChar.getSpriteHeight());
	}

	public void setSpeed(float xySpd, float zVel) {
		this.xySpd = xySpd;
		this.zVel = zVel;
	}

	public CharacterPM getCharacter() {
		return myChar;
	}

	public String getCharacterName() {
		return myChar.getName();
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setFloorZ(float floorZ) {
		this.floorZ = floorZ;
	}

	public void setAlpha(float a) {
		alpha = a;
	}

	public void setForceColor(RGBA color) {
		forceColor = color;
	}
}
