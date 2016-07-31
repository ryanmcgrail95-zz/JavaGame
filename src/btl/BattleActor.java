package btl;

import cont.TextureController;
import io.Keyboard;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import object.primitive.Drawable;
import object.primitive.Positionable;
import paper.ActorPM;
import paper.AnimationsPM;
import paper.Attack;
import paper.BodyPM;
import paper.CharacterPM;
import paper.PartnerPM;
import paper.PlayerPM;
import paper.SpriteMap;
import resource.sound.Sound;
import rm.Room;
import time.Delta;

public abstract class BattleActor extends Positionable {
	BattleController parent;
	
	public static BattleActor pl, pa;
	
	private BodyPM myBody;
	private boolean hasAttacked = false, isDestroyed = false;
		
	private int hp, winTimer = -1;
	
	protected float originalX;
	
	private boolean
		isInvincible = false,
		isMetal = false,
		mainRender = true;
	
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
		hopZ = 0,
		
		oriX0,
		moveToX0 = -1000,
		moveToZ0 = -1000;
	
	
	protected int state;
	
	
	private float flashTimer, flashTimerMax = 360;
	
	protected BattleActor target;
	
	private Attack currentAttack;
	
	protected boolean isPlayer;
	
	// Jumping Variables
	private boolean isJumping;
	
	protected final static float MOVE_SPEED = 7, CHARGE_TIME = 8;
	private float
		jumpPointX,
		jumpX0,
		jumpZ0,
		jumpHeight = 48, hopHeight = jumpHeight*.5f, jumpOutHeight = 48;

	protected float jumpDir = 0;
	
	protected final static int
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
		ST_SPIKE_HOPPING_BACK = 16,
		
		ST_WIN_START = 19,
		ST_WIN_JUMP = 17,
		ST_WIN_RUN_OUT = 18,
		
		ST_SPIKE_FALL = 20,
		
		ST_WIN_JUMP_OUT = 21;
		
	
	public void destroy() {
		if(this == pl)
			pl = null;
		if(this == pa)
			pa = null;
		
		isDestroyed = true;				
		super.destroy();
		myBody.destroy();
			myBody = null;
			currentAttack = null;
			zPreviouses = null;
			target = null;
		parent = null;
	}
	
	public void dieDestroy() {
		isDestroyed = true;
		super.destroy();
		myBody.destroy();
			myBody = null;
			currentAttack = null;
			zPreviouses = null;
			target = null;
				
		parent.remove(this);
		parent.continueDeathAnimations();
		parent = null;
	}
				
	
	protected boolean moveTo(float toX, float speed) {
		float x = getX();
		boolean isLess = x < toX;
		
		speed = Delta.convert(speed);
		
		//Move X Back and Set It
		setX(x = (isLess) ? Math.min(x+speed, toX) : Math.max(toX, x-speed));
		
		return x == toX; 
	}
	
	protected boolean moveFall(float toX) {
		float x = getX();
		boolean isLess = x < toX;
		
		if(moveToX0 == -1000) {
			moveToX0 = x;
			moveToZ0 = getZ();
		}
		
		float speed;
		speed = Math.abs(toX - moveToX0)/10;
		speed = Delta.convert(speed);
		
		//Move X Back and Set It
		setX(x = (isLess) ? Math.min(x+speed, toX) : Math.max(toX, x-speed));
		
		float z;
		z = Math.abs(x - toX)/Math.abs(toX - moveToX0);
			z = (float) (moveToZ0*Math.pow(z, .5f));
			System.out.println(z);
		setZ(z);
		
		if(x == toX) {
			moveToX0 = -1000;
			moveToZ0 = -1000;
			setZ(0);
			return true;
		}
		
		return false;
	}
	
	protected boolean moveToHop(float toX) {
		float x = getX();
		boolean isLess = x < toX;
		
		if(moveToX0 == -1000)
			moveToX0 = x;
		
		float speed;
		speed = Math.abs(toX - moveToX0)/40;
		speed = Delta.convert(speed);
		
		//Move X Back and Set It
		setX(x = (isLess) ? Math.min(x+speed, toX) : Math.max(toX, x-speed));
		
		float z;
		z = Math.abs(x - moveToX0)/Math.abs(toX - moveToX0)*180*3;
			z = hopHeight*Math.abs(Math2D.calcLenY(z));
		setZ(z);
		
		if(x == toX) {
			moveToX0 = -1000;
			setZ(0);
			return true;
		}
		
		return false;
	}
	
	private boolean moveToSmooth(float toX, float toZ, float spd, float error) {
		float x, z;
		x = getX();
		z = getZ();
		
		x += (toX - x)/spd;
		z += (toZ - z)/spd;
		
		setX(x);
		setZ(z);
		
		if(Math.abs(toX - x) < error && Math.abs(toZ - z) < error) {
			setX(toX);
			setZ(toZ);
			return true;
		}
		return false;
	}

	protected boolean moveTo(float toX, float toZ, float speed) {
		float x = getX(), z = getZ();
		boolean isLessX = x < toX, isLessZ = z < toZ;
		
		speed = Delta.convert(speed);
		
		if(isLessX)
			x = Math.min(x+speed, toX);
		else
			x = Math.max(toX, x-speed);
		if(isLessZ)
			z = Math.min(z+speed, toZ);
		else
			z = Math.max(toZ, z-speed);
		
		setX(x);
		setZ(z);
		
		return getX() == toX && getZ() == toZ;
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
	protected float calcHopSpeed() {
		float f = .99f, //.8
			jumpF = jumpDir/180, 
			spd,
			minSpd = 6, //3
			maxSpd = 6; //9
		
		spd = (minSpd*(1-jumpF) + maxSpd*jumpF) * (Math.abs(Math2D.calcLenX(jumpDir))*f + 1-f);
		spd = Delta.convert(spd);
		
		return spd;
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
	private boolean jumpInPlace(boolean out) {
		return jumpInPlace(1, out);
	}
	private boolean jumpInPlace(float speed, boolean out) {
		if(!isJumping) {
			Sound.play("jump");
				
			jumpX0 = getX();
			jumpZ0 = getZ();
			isJumping = true;
		}
		
		float toX, toZ;
		
		toX = jumpX0;
		toZ = jumpZ0;
				
		// Animate Jumping
		float prevJumpDir = jumpDir;
		
		if(out)
			jumpDir += speed*calcHopSpeed();
		else
			jumpDir += speed*calcJumpSpeed(true);
		
		float jumpH;
		jumpH = jumpOutHeight;
		
		setZ(toZ+Math2D.calcLenY(jumpH, jumpDir));
		
		if(jumpDir > 180)
			if(prevJumpDir < 180)
				jumpDir = 180;
		
		if(jumpDir > 180) {
			setX(toX);
			setZ(toZ);
			isJumping = false;
			jumpDir = 0;
			return true;
		}
		return false;
	}

	
	public BattleActor(String name, float x, boolean isPlayer, BattleController parent) {
		super(x,0,0, false,false);
		oriX0 = x;
		setSurviveTransition(true);
		
		this.name = name;
		this.originalX = x;
		this.isPlayer = isPlayer;
		this.parent = parent;		
		

		myBody = new BodyPM(this, name);
		hp = getCharacter().getMaxHP();

		setAnimationStill();
	}
	
	private CharacterPM getCharacter() {
		return myBody.getCharacter();
	}

	@Override
	public void update() {
		//start("BattleActor.update()");
				
		updateZPrevious();
		myBody.setFloorZ(0);

		super.update();
		
		checkActionCommand();
		
		animateModel();		
		
		if(isDestroyed)
			return;

		myBody.animateModel();

		if(currentAttack != null)
			switch(currentAttack.getAttackType()) {
				case Attack.AT_JUMP:	updateJumpAttack();		break;
				case Attack.AT_HAMMER:	updateHammerAttack();	break;
			}
		
		if(!isDestroyed)		
			if(getZ() <= groundZ)
				setZ(groundZ);
		
		//end("BattleActor.update()");
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
				if(deathFallAngle > 90)
					dieDestroy();
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

	public void attack() {
		currentAttack = getCharacter().getRandomAttack();
	}

	
	@Override
	public float calcDepth() {
		return 2;
	}
	

	
	
	public void createAttackBurst() {	
		for(int i = 0; i < 16; i++) { //8
			float
				rndPos = 1, //8
				sX, sZ;
			sX = getX() + MathExt.rndf(-rndPos,rndPos);
			sZ = getTopZ() + MathExt.rndf(-rndPos,rndPos);
			new DamageSpark(sX,-10,sZ,MathExt.rndf(360), .5f, 1, 1, 1);
			
			if(i < 8)
				new DamageSpark(sX,-10,sZ,MathExt.rndf(360), 1.6f, .4f, 1.2f, 2.1f);
		}
	}
	public void createSmokeBurst() {	
		for(int i = 0; i < 16; i++) { //8
			float
				rndPos = 2,
				rndDir = 5,
				sX, sZ, sD;
			sX = getX() + MathExt.rndf(-rndPos,rndPos);
			sZ = getZ() + MathExt.rndf(-rndPos,rndPos);
			sD = i/16f*180 + MathExt.rndf(-rndDir,rndDir);
			new DamageSmoke(sX,-1,sZ,sD);
		}
	}
	public void createFireBurst(boolean isHot) {		
		new Fireball(getX(),getTopZ(),isHot);
	}
	public void createDodgeBurst() {		
		float sD = 90 + getSign()*60;
		new DodgeStar(getX(),getY(),getTopZ(),sD);
	}
	
	public void setAnimation(byte type) {
		myBody.setAnimation(type);
	}
	
	public void setAnimationStill() {myBody.setAnimationStill();}
	public void setAnimationRun() 	{myBody.setAnimationRun();}
	public void setAnimationJump() 	{
		if(jumpDir < 90)
			myBody.setAnimationJump();
		else
			myBody.setAnimationJumpFall();
	}
	public void setAnimationLand()	{myBody.setAnimationLand();}
	public void setAnimationJumpLandHurt()	{myBody.setAnimationJumpLandHurt();}
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
					if(!target.hasSpike()) {
						state = ST_DAMAGE;
						parent.setTimer(4);					
						squishFraction = .5f;
						target.damage(calcDamage(), currentAttack.getElement(), false);
						setAnimationLand();
					}
					else
						gotoSpikeState();
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
						target.damage(calcDamage(), currentAttack.getElement(), false);
						setAnimationLand();
					}
					else
						gotoSpikeState();
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

				if(doneJumping) 
					if(!target.hasSpike()) {
						setX(target.getX());
						setZ(target.getTopZ());
						
						parent.setTimer(4);
						
						target.damage(calcDamage(), currentAttack.getElement(), true);
						
						setAnimationLand();
						state = ST_DODGE_LAND;
					}
					else
						gotoSpikeState();
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
				float uSpd = 6;
				
				if(isPlayer)
					parent.focusSpike(getX(), uSpd);
				setAnimationJumpLandHurt();
				
				float toX, toZ, dis, disP;
				toX = target.x() - getSign()*32;
				toZ = target.getTopZ()+32;
				
				disP = Math2D.calcPtDis(getX(),getZ(), toX,toZ);
				
				if(moveToSmooth(toX, toZ, uSpd, .1f)) {
					float mi = 30, ti = 10;
					if(parent.getTimer() == 0)
						parent.setTimer(mi+ti);
					if(parent.getTimer() < mi) {
						parent.setTimer(0);
						state = ST_SPIKE_FALL;
						parent.focusNeutral();
					}
				}
				
				dis = Math2D.calcPtDis(getX(),getZ(), toX,toZ);
				if(MathExt.between(dis, 8, disP))
					target.shimmer();
				
				break;
			case ST_SPIKE_FALL:
				if(moveFall(target.x() - getSign()*40))
					state = ST_SPIKE_HOPPING_BACK;
				break;
			case ST_SPIKE_HOPPING_BACK:
				if(moveToHop(originalX)) {
					state = ST_STILL;
					
					setAnimationStill();
					target.setAnimationStill();
					
					endAttack();	
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
				
			case ST_WIN_START:
    			pl.myBody.setFloorZ(-100);
    			pa.myBody.setFloorZ(-100);

				pl.mainRender = true;
				pa.mainRender = true;				
        		GL.getMarioCamera().render(pl, pa);
				pl.mainRender = false;
				pa.mainRender = false;				

        		if(parent.checkTimer()) {
					state = ST_WIN_JUMP_OUT;
					winTimer = 0;
					GL.getMarioCamera().enable(false);					

					pl.myBody.setAlpha(.6f);
					pa.myBody.setAlpha(.6f);
					GL.getMainCamera().renderDrawable();
					pl.myBody.setAlpha(1);
					pa.myBody.setAlpha(1);
					
					GL.setOrtho();
					GL.beginPageCurl();
					GL.setPerspective();
					
					Sound.fadeMusic(0);
					
					Room.revertRoom();
				}
				break;
			case ST_WIN_JUMP_OUT:
				//winTimer--;
				//if(parent.checkTimer()) {
				//GL.getMainCamera().enable(false);
				//}
				pa.setAnimationJump();
				pl.setAnimationJump();
				
				
    			pl.myBody.setFloorZ(-100);
    			pa.myBody.setFloorZ(-100);


				pl.mainRender = true;
				pa.mainRender = true;				
        		GL.getMarioCamera().render(pl, pa);
				pl.mainRender = false;
				pa.mainRender = false;				

        		
        		Camera c = GL.getMainCamera();
        		
				ActorPM pll = PlayerPM.getInstance();
				ActorPM pal = PartnerPM.getInstance();
				
				float dX;
				dX = pl.x() - pa.x();
				
				if(pll != null) {
					pl.x(pll.x());
					pl.y(pll.y());
					pl.z(pll.z());
					
					pa.x(pll.x() - dX);
					pa.y(y());
					pa.z(z());
					pal.x(pa.x());
					pal.y(pa.y());
					pal.z(pa.z());
					
					pll.setVisible(false);
					pll.setCanControl(false);
					pal.setVisible(false);
					pal.setCanControl(false);

					
					float r, tX, tY, tZ, toD, x,y,z;
					r = 205;
					tX = x();
					tY = y();
					tZ = z() + 17 + 9; //18
					toD = 8.6f;
										
					x = tX;
					y = tY - Math2D.calcLenX(r,toD);
					z = tZ + Math2D.calcLenY(r,toD);
					
					c.setProjection(x,y,z,tX,tY,tZ);
					c.teleport();
					c.lock();
				}
				
				if(winTimer == 0) {
					//GL.getMainCamera().enable(false);
					setVisible(true);
					winTimer = 1;
				}
				if(winTimer == 1) {
					pa.jumpInPlace(2, true);
					if(pl.jumpInPlace(2,true))
						winTimer = 2;
				}
				if(winTimer == 2) {
					pll.setCanControl(true);
					pll.setVisible(true);
					pal.setCanControl(true);
					pal.setVisible(true);
					GL.endPageCurl();
					c.enable(true);
					c.unlock();
					c.smoothOnce(2f);
										
					pl.destroy();
					pa.destroy();					
				}
				break;

			case ST_WIN_JUMP:
				setAnimationJump();
				if(jumpInPlace(false))
					state = ST_WIN_RUN_OUT;
				break;
			case ST_WIN_RUN_OUT:
				setAnimationRun();
				if(moveTo(originalX + 80, MOVE_SPEED/2)) {
					state = ST_STILL;
					//TransitionController.startTransitionColor("what", RGBA.BLACK, 20);
				}
				break;
		}
	}

	private void gotoSpikeState() {
		state = ST_TOUCHED_SPIKE;
		
		addHP(-1);
		new DamageStar(target.getX(),-5,target.getZ(),1, isPlayer);
		target.createAttackBurst();
		
	}

	public void shimmer() {
		myBody.shimmerSpike(getX(),getTopZ());
	}

	public void updateHammerAttack() {
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
		return currentAttack.getAttack() - target.getCharacter().getDefense();
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
		if(isMetal)
			amt--;
		if(dodge)
			amt--;
		
		if(amt > 0)	
			addHP(-amt);
					
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
	
	private void addHP(int amt) {
		hp = MathExt.contain(0, hp+amt, getMaxHP());
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
		if(currentAttack == null)
			return false;
		
		if(currentAttack.getAttackType() == Attack.AT_JUMP)
			if(state == ST_JUMP_ON || state == ST_JUMP_AGAIN) {
				if(jumpDir > 130)
					if(jumpTimes == 0)
						return true;
			}
		return false;
	}
	
	public void checkActionCommand() {
		boolean timed = false;
		
		if(currentAttack == null)
			return;
		
		if(currentAttack.getAttackType() == Attack.AT_JUMP && Keyboard.checkPressed('U'))
			timed = canActionCommand();
		
		if(timed)
			if(isPlayer)
				state = ST_JUMP_AGAIN;
			else
				state = ST_DAMAGE_DODGED;
	}
		
	@Override
	public void draw() {
		
		boolean inMain, inMario, overCurl, parentTimer;
		inMain = GL.getCamera() == GL.getMainCamera();
		inMario = GL.getCamera() == GL.getMarioCamera();
		overCurl = GL.getPageCurl() > -1 && inMario;		
				
		//if(!(overCurl || inMain))
		//	return;
		
		if(isDestroyed)
			return;
		
		GL.setPerspective();
		
		if(!mainRender) {
			myBody.setFloorZ(0);
			myBody.drawShadow(oriX0, y(), 0, 0);
			return;
		}
		
		float dXT, dZT;
		
		myBody.setScale( (isPlayer ? 1 : -1)*(2-squishFraction), squishFraction);
		
		dZT = hopZ;
			
		dXT = 0;
		if(shakeFraction > 0) {
			float shR = 20, shF;
			shF = (float) Math.pow(shakeFraction, .4f);
			dXT += shF*MathExt.rndf(-shR,shR);
			//dZ += shF*MathExt.rnd(-shR,shR);
		}
		
		if(hopZ != 0)
			dZT += MathExt.rndf(4);
		
		myBody.setPosition(x() + dXT, y(), z() + dZT);
		
		GT.transformClear();
			
			/*if(GL.getCamera() == GL.getMainCamera()) {
				GL.forceColor(RGBA.BLACK);
				float shH = myBody.getSpriteWidth()*.4f/2, shW = shH*1.5f;
				GL.setAlpha(.5f);
				GL.draw3DFloor(-shW,-shH,shW,shH,.1f, TextureController.getTexture("texShadow"));
				GL.unforceColor();
				GL.setAlpha(1);
			}*/

			if(GL.getPageCurl() == -1 || inMario) {
				myBody.setRotation(deathFallAngle, 0, deathAngle);
				//GL.transformPaper();
	
				
				GL.setColor(RGBA.WHITE);
				
				if(canActionCommand() || (flashTimer != 0 && Math2D.calcLenY(flashTimer*4) > 0))
					myBody.setForceColor(RGBA.WHITE);
				else	
					myBody.setForceColor(null);
				
				if(isInvincible)
					GL.enableShaderRainbow();
				else if(isMetal)
					GL.enableShaderMetal(getX(),getY(),getZ());
				myBody.draw();
				GL.unforceColor();
				GL.disableShaders();
			}
		GT.transformClear();
		
		/*if(state == ST_JUMP_ON && jumpTimes == 1) {
			int si = zPreviouses.length;
			for(int i = 0; i < si; i++) {
				GL.transformClear();
				GL.transformTranslation(getX(),getY(),zPreviouses[i]);
				GL.transformPaper();
				
				GL.setAlpha(.5f*i/si);				
				GL.drawTexture(dX,dH+dZ, dW, -dH, sprite.getFrame(imageIndex));
				GL.resetColor();
				
				GL.transformClear();
			}
		}*/
		//GL.setColor(RGBA.BLACK);
		//GL.fillRectangle(dX,dZ, dW, -dH);
	}
	
	@Override
	public void add() {}
	
	private boolean hasSpike() {return myBody.hasSpike();}
	
	private void endAttack() {
		hasAttacked = true;
		parent.continueTurn();
	}

	public void die() {
		state = ST_DYING;
	}

	public int getHP() {return hp;}
	public int getMaxHP() {return getCharacter().getMaxHP();}


	public void gotoWinState() {
		if(state != ST_WIN_START && state != ST_WIN_JUMP_OUT && state != ST_WIN_JUMP && state != ST_WIN_RUN_OUT) {
			parent.setTimer(60);
			state = ST_WIN_START;
		}
	}
}
