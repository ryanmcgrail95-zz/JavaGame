package btl;

import cont.TextureController;
import io.Keyboard;
import functions.Math2D;
import functions.MathExt;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import gfx.TextureExt;
import object.primitive.Positionable;
import paper.AnimationsPM;
import paper.BodyPM;
import paper.CharacterPM;
import paper.SpriteMap;
import resource.sound.Sound;
import time.Delta;

public abstract class BattleActor extends Positionable {
	BattleController parent;
	
	private CharacterPM myChar;
	private BodyPM myBody;
	private boolean hasAttacked = false;
	
	private int maxHP = 10, hp = maxHP;
	
	protected float originalX;
	
	private boolean isInvincible = false ;
	
	protected static int groundZ = 0;
	
	private float[] zPreviouses = new float[5];
		
	// Death Variables
	private float 
		deathAngle = 0,
		deathFallAngle = 0;
	
	private int jumpTimes = 0;
	
	protected float 
		squishFraction = 1,
		shakeFraction = 0,
		hopDir = 0,
		hopZMax = 32,
		hopNumberMax = 5,
		hopNumber = 0,
		hopZ = 0;
	
	
	protected byte state;
	
	
	private float flashTimer, flashTimerMax = 360;
	
	protected BattleActor target;
	
	protected boolean isPlayer;
	
	// Jumping Variables
	private boolean isJumping;
	
	protected final static float MOVE_SPEED = 7, CHARGE_TIME = 8;
	private float
		jumpPointX,
		jumpX0,
		jumpZ0,
		jumpHeight = 48, hopHeight = jumpHeight*.5f;

	protected float jumpDir = 0;
	
	protected final static byte
		ST_STILL = 0,
		ST_MOVE_TO = 1,
		ST_MOVE_BACK = 2,
		ST_DAMAGE = 3,
		
		ST_PREPARE_JUMP = 6,
		ST_JUMP_ON = 4,
		ST_JUMP_OFF = 5,
		ST_JUMP_AGAIN = 7,
		ST_DODGE_LAND = 10,
		ST_JUMP_AGAIN_LAND = 11,
		
		ST_DAMAGE_DODGED = 8,
		
		ST_DYING = 9,
		
		ST_GET_HAMMER = 12,
		ST_HOLD_HAMMER = 13,
		
		ST_ATTACK_HAMMER = 14,
		
		ST_TOUCHED_SPIKE = 15,
		ST_SPIKE_HOPPING_BACK = 16;
		
				
	
	protected boolean moveTo(float toX, float speed) {
		float x = getX();
		boolean isLess = x < toX;
		
		speed = Delta.convert(speed);
		
		if(isLess)
			x = Math.min(x+speed, toX);
		else
			x = Math.max(toX, x-speed);
		
		setX(x);
		
		return getX() == toX; 
	}
	
	
	
	protected float calcJumpSpeed(boolean on) {
		float f = .8f, //.8
			jumpF = jumpDir/180, 
			spd,
			minSpd = 5, //3
			maxSpd = 9; //9
		
		spd = (minSpd*(1-jumpF) + maxSpd*jumpF) * (Math.abs(Math2D.calcLenX(jumpDir))*f + 1-f);
		spd = Delta.convert(spd);
		
		return on ? spd : 2*spd;
	}
	
	protected boolean jumpOn() 	{return jump(true);}
	protected boolean jumpOff() {return jump(false);}
	private boolean jump(boolean on) {
		if(!isJumping) {
			if(on)
				Sound.play("jump");
				
			jumpX0 = getX();
			jumpZ0 = getZ();
			isJumping = true;
		}
		
		float toX, toZ;
		
		if(on) {
			toX = target.getX();
			toZ = target.getTopZ();
		}
		else {
			float si = isPlayer ? -1 : 1;
			toX = target.getX() + si*24;
			toZ = groundZ;
		}
		
		float middleX, jumpXRadius;
		middleX = (jumpX0 + toX)/2;
		jumpXRadius = Math.abs(jumpX0 - toX)/2;
		
		// Animate Jumping
		float prevJumpDir = jumpDir;
		jumpDir += calcJumpSpeed(on);
		
		float sign, jumpFraction;
		jumpFraction = on ? 1-jumpDir/180 : 1-jumpDir/180;
		sign = (isPlayer ? 1 : -1) * (on ? -1 : 1);
		
		float jumpH;
		jumpH = on ? jumpHeight : hopHeight;
		
		setX(middleX + sign*Math2D.calcLenX(jumpXRadius, jumpDir));
		setZ(MathExt.interpolate(toZ+Math2D.calcLenY(jumpH, jumpDir),
				jumpZ0+Math2D.calcLenY(jumpH, jumpDir),
				jumpFraction));
		
		if(jumpDir > 180)
			if(on)
				if(prevJumpDir < 180)
					jumpDir = 180;
		
		if(jumpDir > 180) {
			setX(toX);
			setZ(toZ);
			isJumping = false;
			jumpDir = 0;
			state = ST_DAMAGE;
			parent.setTimer(3);
			return true;
		}
		return false;
	}

	
	public BattleActor(String name, float x, boolean isPlayer, BattleController parent) {
		super(x,0,0, false,false);
		
		this.name = name;
		this.originalX = x;
		this.isPlayer = isPlayer;
		this.parent = parent;		
		
		myChar = CharacterPM.getCharacter(name);
		myBody = new BodyPM(myChar);

		setAnimationStill();
	}
	
	@Override
	public void update() {
		updateZPrevious();

		super.update();
		
		checkActionCommand();
		
		animateModel();
		
		switch(myChar.getAttackType()) {
			case CharacterPM.AT_JUMP:	updateJumpAttack();		break;
			case CharacterPM.AT_HAMMER:	updateHammerAttack();	break;
		}
		
		if(getZ() <= groundZ)
			setZ(groundZ);
	}
	
	private void updateZPrevious() {
		int si = zPreviouses.length;
		
		for(int i = si-1; i >= 1; i--)
			zPreviouses[i] = zPreviouses[i-1];
		zPreviouses[0] = getZ();
	}
	
	public boolean hasAttacked() {
		return hasAttacked;
	}
	public void resetHasAttacked() {
		hasAttacked = false;
	}
	
	private void animateModel() {
		flashTimer -= Delta.convert(20);
		if(flashTimer < 0)
			flashTimer = 0;
		
		// Animate Death
		if(state == ST_DYING) {
			deathAngle += Delta.convert(15);
			
			if(deathAngle >= 360*2) {
				if(deathAngle < 360*3) {
					Sound.play("enemyDie");
					createSmokeBurst();
					parent.addStarPoints(2);
				}
				deathAngle = 360*3;
				
				deathFallAngle += Delta.convert(8);
				if(deathFallAngle > 90) {
					destroy();
					parent.remove(this);
					parent.continueDeathAnimations();
					parent = null;
				}
			}
		}
		
		
		// Slowly Reset Squish/Shake Values to Normal
		squishFraction = MathExt.to(squishFraction, 1, 5);
		shakeFraction = MathExt.to(shakeFraction, 0, 3);
		
		// Hopping Determined w/ Sine Value
		if(hopNumber > 0) {
			hopDir += Delta.convert(30);
			if(hopDir >= 180) {
				hopNumber--;
				
				if(hopNumber == 0)
					hopDir = 0;
				else
					hopDir -= 180;
			}
			
			hopZ = Math2D.calcLenY(hopDir)*(hopNumber/hopNumberMax)*(hopZMax);
		}
		else
			hopZ = 0;
	}

	public float getTopZ() {
		return getZ() + myBody.getExtraBaseHeight() + myBody.getHeight();
	}

	public abstract void attack();

	
	@Override
	public float calcDepth() {
		return 2;
	}
	
	public void setAnimation(byte type) {
		myBody.setAnimation(type);
	}
	
	
	public void createAttackBurst() {	
		for(int i = 0; i < 16; i++) { //8
			float
				rndPos = 1, //8
				sX, sZ;
			sX = getX() + MathExt.rnd(-rndPos,rndPos);
			sZ = getTopZ() + MathExt.rnd(-rndPos,rndPos);
			new DamageSpark(sX,sZ,MathExt.rnd(360));
		}
	}
	public void createSmokeBurst() {	
		for(int i = 0; i < 16; i++) { //8
			float
				rndPos = 2,
				rndDir = 5,
				sX, sZ, sD;
			sX = getX() + MathExt.rnd(-rndPos,rndPos);
			sZ = getZ() + MathExt.rnd(-rndPos,rndPos);
			sD = i/16f*180 + MathExt.rnd(-rndDir,rndDir);
			new DamageSmoke(sX,sZ,sD);
		}
	}
	public void createFireBurst(boolean isHot) {		
		new Fireball(getX(),getTopZ(),isHot);
	}
	public void createDodgeBurst() {		
		float sD = 90 + getSign()*60;
		new DodgeStar(getX(),getTopZ(),sD);
	}
	
	
	public void setAnimationStill() {myBody.setAnimationStill();}
	public void setAnimationRun() 	{myBody.setAnimationRun();}
	public void setAnimationJump() 	{myBody.setAnimationJump();}
	public void setAnimationLand()	{myBody.setAnimationLand();}
	public void setAnimationHurt()	{myBody.setAnimationHurt();}
	public void setAnimationBurned(){myBody.setAnimationBurned();}
	public void setAnimationDodge()	{myBody.setAnimationDodge();}
	
	private void focusPrepareJump() {
		if(isPlayer)
			parent.focusPlayerPrepareJump((getX() + target.getX())/2);
		else
			parent.focusEnemyJump(getX());
	}
	private void focusJump() {
		if(isPlayer)
			parent.focusPlayerJump((getX() + target.getX())/2, getZ());
		else
			parent.focusEnemyJump(getX());
	}
	
	public void updateJumpAttack() {
		boolean doneJumping;
		
		switch(state) {
			case ST_MOVE_TO:
				focusPrepareJump();
				jumpPointX = (originalX + target.getX())/2;
				setAnimationRun();
				if(moveTo(jumpPointX, MOVE_SPEED)) {
					state = ST_PREPARE_JUMP;
					parent.setTimer(CHARGE_TIME);
					myBody.setAnimation(SpriteMap.S_PREPARE_JUMP);
				}
				break;
				
			case ST_PREPARE_JUMP:
				focusPrepareJump();
				squishFraction = 1 - Math2D.calcLenY(.5f, parent.getTimer()/CHARGE_TIME*180);
				if(parent.checkTimer()) {
					state = ST_JUMP_ON;
					squishFraction = 1;
					jumpTimes = 0;
				}
				break;
			
			case ST_JUMP_ON:
				if(jumpDir < 180)
					setAnimationJump();
				else
					setAnimationLand();
				
				focusJump();
				
				if(jumpOn()) {
					if(target.hasSpike()) {
						state = ST_DAMAGE;
						parent.setTimer(4);					
						squishFraction = .5f;
						target.damage(calcDamage(), myChar.getElement(), false);
						setAnimationLand();
					}
					else {
						state = ST_TOUCHED_SPIKE;
					}
				}
				break;
				
			case ST_JUMP_AGAIN:
				if(!isJumping)
					doneJumping = true;
				else
					doneJumping = jumpOn();

				focusJump();
				
				if(doneJumping) {
					if(!target.hasSpike()) {
						state = ST_JUMP_AGAIN_LAND;
						parent.setTimer(4);
						squishFraction = .5f;
						target.damage(calcDamage(), myChar.getElement(), false);
						setAnimationLand();
					}
					else {
						state = ST_TOUCHED_SPIKE;
					}
				}
				break;
			
			case ST_DAMAGE:
				setX(target.getX());
				setZ(target.getTopZ());
				setAnimationLand();
				
				focusJump();
				
				if(parent.checkTimer())
					state = ST_JUMP_OFF;
				break;
			case ST_DODGE_LAND:
				setX(target.getX());
				setZ(target.getTopZ());
				setAnimationLand();
				if(parent.checkTimer())
					state = ST_JUMP_OFF;
				break;
			case ST_JUMP_AGAIN_LAND:
				setX(target.getX());
				setZ(target.getTopZ());
				setAnimationLand();
				if(parent.checkTimer()) {
					state = ST_JUMP_ON;
					jumpTimes++;
				}
				break;
				
			case ST_DAMAGE_DODGED:
				if(!isJumping)
					doneJumping = true;
				else
					doneJumping = jumpOn();

				if(doneJumping) {
					setX(target.getX());
					setZ(target.getTopZ());
					
					parent.setTimer(4);
					
					target.damage(calcDamage(), myChar.getElement(), true);
					
					setAnimationLand();
					state = ST_DODGE_LAND;
				}
				break;
				
			case ST_JUMP_OFF:
				if(isPlayer && target.hp == 0)
					parent.focusEnemy();
				else
					parent.focusNeutral();
				setAnimationJump();
				if(jumpOff())
					state = ST_MOVE_BACK;
				break;
				
			case ST_TOUCHED_SPIKE:
				break;
				
			case ST_MOVE_BACK:
				setAnimationRun();
				if(moveTo(originalX, MOVE_SPEED)) {
					state = ST_STILL;

					setAnimationStill();
					target.setAnimationStill();
					
					endAttack();
				}
				break;
		}
	}

	public void updateHammerAttack() {
		boolean doneJumping;
		
		switch(state) {
			case ST_MOVE_TO:
				jumpPointX = target.getX() - getSign()*16;
				setAnimationRun();
				if(moveTo(jumpPointX, MOVE_SPEED)) {
					state = ST_MOVE_BACK;
					setAnimationStill();
				}
				break;				
				
			case ST_MOVE_BACK:
				setAnimationRun();
				if(moveTo(originalX, MOVE_SPEED)) {
					state = ST_STILL;

					setAnimationStill();
					target.setAnimationStill();
					
					endAttack();
				}
				break;
		}
	}

	private int getSign() {
		return isPlayer ? 1 : -1;
	}
	private int calcDamage() {
		return myChar.getAttack() - target.myChar.getDefense();
	}

	public void damageHop() {
		hopDir = 0;
		hopNumber = hopNumberMax;
	}
	public void cancelHop() {
		hopDir = 0;
		hopNumber = 0;
	}
		
	public void damage(int amt, byte element, boolean dodge) {
		if(isInvincible)
			amt = 0;
		else if(dodge)
			amt--;
		
		hp = Math.max(0, hp-amt);
					
		if(dodge) {
			startActiveDodgeAnimation(amt);
			
			if(amt > 0)
				Sound.play("dodgeCrunch");
			else
				Sound.play("dodge");
		}
		else if(amt <= 0) {
			startPassiveDodgeAnimation();
			Sound.play("dodge");
		}
		else {
			if(element == CharacterPM.EL_NONE) {
				setAnimationHurt();
				shakeFraction = 0;
				damageHop();
				
				Sound.play("attack");
			}
			else if(element == CharacterPM.EL_FIRE) {
				setAnimationBurned();
				shakeFraction = 1;
				createFireBurst(true);
				
				Sound.play("fireAttack");
			}
			else if(element == CharacterPM.EL_ICYFIRE) {
				setAnimationBurned();
				shakeFraction = 1;
				createFireBurst(false);
				
				Sound.play("fireAttack");
			}
		}
		
		if(amt <= 0)
			createDodgeBurst();
		else {
			flashTimer = flashTimerMax;
			createAttackBurst();
			new DamageStar(getX(),-5,getZ(),amt, isPlayer);
		}
	}
	
	public void startActiveDodgeAnimation(int damageAmt) {
		if(!myBody.checkAnimation(AnimationsPM.S_DODGE)) {
			setAnimationDodge();
			
			shakeFraction = (damageAmt <= 0 ) ? 1 : 2;
			cancelHop();
		}
	}
	public void startPassiveDodgeAnimation() {
		setAnimationStill();
		shakeFraction = 1;
		cancelHop();
	}
	
	public boolean canActionCommand() {
		if(myChar.getAttackType() == CharacterPM.AT_JUMP)
			if(state == ST_JUMP_ON || state == ST_JUMP_AGAIN) {
				if(jumpDir > 130)
					if(jumpTimes == 0)
						return true;
			}
		return false;
	}
	
	public void checkActionCommand() {
		boolean timed = false;
		
		if(myChar.getAttackType() == CharacterPM.AT_JUMP && Keyboard.checkPressed('U'))
			timed = canActionCommand();
		
		if(timed)
			if(isPlayer)
				state = ST_JUMP_AGAIN;
			else
				state = ST_DAMAGE_DODGED;
	}
	
	@Override
	public void draw() {
		
		GOGL.setPerspective();
		
		float dXS, dYS, dXT, dZT;
		dYS = squishFraction;
		dXS = (1 + 1-squishFraction);

		if(!isPlayer)
			dXS *= -1;
		
		dZT = hopZ;
			
		dXT = 0;
		if(shakeFraction > 0) {
			float shR = 20, shF;
			shF = (float) Math.pow(shakeFraction, .4f);
			dXT += shF*MathExt.rnd(-shR,shR);
			//dZ += shF*MathExt.rnd(-shR,shR);
		}
		
		if(hopZ != 0)
			dZT += MathExt.rnd(4);
		

		GOGL.transformClear();
			GOGL.transformTranslation(getX(),getY(),0);
			GOGL.transformTranslation(dXT,0,0);
			
			GOGL.forceColor(RGBA.BLACK);
			float shH = myBody.getSpriteWidth()*.4f/2, shW = shH*1.5f;
			GOGL.setAlpha(.5f);
			GOGL.draw3DFloor(-shW,-shH,shW,shH,.1f, TextureController.getTexture("texShadow"));
			GOGL.unforceColor();

			GOGL.setAlpha(1);

			GOGL.transformTranslation(0,0,getZ()+dZT);
			
			GOGL.transformRotationZ(deathAngle);
			GOGL.transformPaper();

			GOGL.transformRotationX(deathFallAngle);

			GOGL.transformScale(dXS,dYS,1);
			GOGL.setColor(RGBA.WHITE);
			
			if(canActionCommand() || (flashTimer != 0 && Math2D.calcLenY(flashTimer*4) > 0))
				GOGL.forceColor(RGBA.WHITE);
				
			GOGL.enableShader("Rainbow");
			
			myBody.draw();
			
			GOGL.unforceColor();
			GOGL.disableShaders();
		GOGL.transformClear();
		
		/*if(state == ST_JUMP_ON && jumpTimes == 1) {
			int si = zPreviouses.length;
			for(int i = 0; i < si; i++) {
				GOGL.transformClear();
				GOGL.transformTranslation(getX(),getY(),zPreviouses[i]);
				GOGL.transformPaper();
				
				GOGL.setAlpha(.5f*i/si);				
				GOGL.drawTexture(dX,dH+dZ, dW, -dH, sprite.getFrame(imageIndex));
				GOGL.resetColor();
				
				GOGL.transformClear();
			}
		}*/
		//GOGL.setColor(RGBA.BLACK);
		//GOGL.fillRectangle(dX,dZ, dW, -dH);
	}
	
	@Override
	public void add() {
	}
	
	private boolean hasSpike() {
		return myBody.hasSpike();
	}
	
	private void endAttack() {
		hasAttacked = true;
		parent.continueTurn();
	}

	public void die() {
		state = ST_DYING;
	}

	public int getHP() {return hp;}
	public int getMaxHP() {
		return maxHP;
	}
}
