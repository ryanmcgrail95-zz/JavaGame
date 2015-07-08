package object.actor;

import cont.Text;
import io.Mouse;
import Datatypes.Inventory;
import Datatypes.SortedList;
import Datatypes.vec2;
import Datatypes.vec3;
import Datatypes.vec4;
import obj.itm.Item;
import obj.itm.ItemBlueprint;
import obj.prt.Dust;
import object.actor.body.Body;
import object.environment.Floor;
import object.environment.Heightmap;
import object.primitive.Physical;
import resource.sound.SoundController;
import sts.Stat;
import time.Timer;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;
import gfx.RGBA;
import gfx.SpriteMap;
import gfx.TextureExt;

public abstract class Actor extends Physical {
	private SortedList<Actor> actorList = new SortedList<Actor>();
	
	private float prevSpeed;
	
	private static float SP_FRAC = 2f/3;
	private static float SP_WALK = 9;
	{
		SP_WALK *= SP_FRAC;
	}
	
	protected float height = 56;
	protected boolean isMoving = false, isSpeaking = false, canCollide = true;
	
	private Body myBody;
	
	// MOVING TO POINT
	private vec4 toPoint = new vec4(-1,-1,-1,0);
	
	
	// SIGHT VARIABLES
	private static float SIGHT_FOV = 45, SIGHT_DIS = 200;
	
	
	private Inventory inv;
	private Stat stat;
	
	private float faceDirection, faceToDirection;
	protected float camDirection;
	
	//Air Variables
	protected float inAirDir, inAirDir2, inAirSpeed, inAirSpeed2;
	
	protected final static int A_NOTHING = -1, A_SPEAKING = 0, A_WAIT = 1, A_TOPOINT = 2, A_HOLD = 3, A_HAMMER = 4, A_TOHELPER = 5;
	protected int action = A_NOTHING;
	
	//GO TO VARIABLES
	float goToX, goToY, goToZ, goToVel;
	String goToScript;
	
	protected final static float IMAGE_SPEED = .8f;
	private float upZ = 0;
	protected boolean stepSound = false;
	
		
	private SpriteMap map;
	
	//Image Variables
		protected TextureExt image = null;
		protected float imageIndex = 0, imageNumber = 6;
	
	//Spinning Variables
		//protected boolean isSpinning = false;
		protected final static float SPIN_VEL = 3f; // 2.5
		protected float spinTimer = 0, spinDirection = 0;
		protected int[] spinSound = null;
		
	//CHARACTER VARIABLES
		protected String chrctr;
	
	//JUMPING VARIABLES
		protected boolean isJumping = false;
		protected float jumpHold = 0;
		
		protected Timer rollTimer, attackTimer;
	
	//FOLLOW VARIABLES
		private static final byte FOLLOW_NUM = 30;
		private float[] followXP = new float[FOLLOW_NUM],
				followYP = new float[FOLLOW_NUM],
				followD = new float[FOLLOW_NUM];
		private boolean[] followJP = new boolean[FOLLOW_NUM];
		
		
	public Actor(float x, float y, float z) {
		super(x, y, z);
		
		maxSpeed = 5;
		
		faceDirection = camDirection = 0;
		size = 10;		
		
		followIni();
				
		myBody = new Body();
		inv = new Inventory(this);
		stat = new Stat(this);
		
		rollTimer = new Timer(0,60);
		attackTimer = new Timer(0,2);
	}
	
	public void die() {
		dropItems();
		destroy();
	}
	

	//PARENT FUNCTIONS
		public void update(float deltaT) {
			
			prevSpeed = getXYSpeed();
			
			super.update(deltaT);
						
			followUpdate();
			
			if(canControl())
				control();
			updateAdvancedMotion();
		}
				
	public boolean collide(Physical other) {		
		return false;
	}
	
		
	//ACCESSOR AND MUTATOR FUNCTIONS		
		public float getHeight() {
			return height;
		}
		
		public void followIni() {
			//Initializes the variables for an animated object, so other objects can follow it.	
		
			for(byte i = 0; i < FOLLOW_NUM; i += 1) {
			    followXP[i] = x;
			    followYP[i] = y;
			    followD[i] = getDirection();
			    followJP[i] = false;
			}
		}
	
		public boolean draw() {
			myBody.draw(x,y,z+16, faceDirection);
							
			return true;
		}
		
		/*public void updatePosition() {
			super.updatePosition();
			
			setFaceDir(getDirection());
		}*/
		
		public void setFaceDir(float toDir) {
			
			if(prevSpeed < .1)			
				faceDirection = faceToDirection = toDir;
			else if(Math.abs(Math2D.calcAngDiff(toDir,faceDirection)) > 160)
				faceDirection = faceToDirection = toDir;
			else
				faceToDirection = toDir;
		}
		
		public void setCameraDir(float toDir) {
			float camDir, dir;
			dir = camDirection - getDirection();
			dir = MathExt.snap(dir,90);
			
			// Rotate Camera Angle Based on Held Direction
			if(dir == 90 || dir == -90)
				camDir = toDir;
			else if(dir == 0 || dir == 180)
				camDir = toDir + .2f*Math2D.calcSmoothTurn(toDir,toDir-90+dir);
			else if(dir == 45 || dir == 135)
				camDir = toDir + .2f*Math2D.calcSmoothTurn(toDir,toDir-90+dir);
			else
				camDir = toDir - .2f*Math2D.calcSmoothTurn(toDir,180+toDir-90+dir);
			
			camDirection += Math2D.calcSmoothTurn(camDirection, camDir, 10);
		}
		
	//UPDATE FUNTIONS
		public void followUpdate() {
			//Updates the follow variables.
		
			for(byte i = FOLLOW_NUM-1; i > 0; i -= 1) {
			    followXP[i] = followXP[i-1];
			    followYP[i] = followYP[i-1];
			    followD[i] = followD[i-1];
			    followJP[i] = followJP[i-1];
			}
		
			followXP[0] = x;
			followYP[0] = y;	
			followD[0] = getDirection();
			followJP[0] = false;
		}	
		
	
	public void land() {
	    setZVelocity(0);

	    if(inAir) {
	    	inAir = false;

			float cDir = Camera.getDirection();
	        new Dust(x+Math2D.calcLenX(7,cDir-90),y+Math2D.calcLenY(7,cDir-90),z+4, 0, false);
	        new Dust(x+Math2D.calcLenX(7,cDir+90),y+Math2D.calcLenY(7,cDir+90),z+4, 0, false);
	    }
	}
	
	public float getFollowX() {
		return followXP[FOLLOW_NUM-1];
	}
	public float getFollowY() {
		return followYP[FOLLOW_NUM-1];
	}
	public float getFollowDirection() {
		return followD[FOLLOW_NUM-1];
	}
	public boolean getFollowJump() {
		return followJP[FOLLOW_NUM-1];
	}
	
	
	
	protected abstract void control();

	protected void updateAdvancedMotion() {
		
		// Lower values for third makes it like ice physics!!
		faceDirection += Math2D.calcSmoothTurn(faceDirection, faceToDirection, 15);
		
		
		// ROLLING 
		float rollSpeed;
		rollSpeed = 15;
				
		if(isRolling())
			addXYSpeed( (rollSpeed - getXYSpeed())/5 );
		
		// MOVE TO POINT
		if(toPoint.get(3) == 1) {
			if(move(true, new vec2(toPoint.get(0), toPoint.get(1))))
				toPoint.set(3,0);
		}
	}
	
	
	protected boolean canMove() {
		return !isRolling() && !isAttacking() && !Text.isActive();
	}
	protected boolean canControl() {
		return !isAttacking() && !Text.isActive();
	}
	

	protected void roll() {
		rollTimer.reset();
	}
	protected boolean isAttacking() {
		return !attackTimer.checkOnce();
	}
	protected boolean isRolling() {
		return !rollTimer.checkOnce();
	}
	
	protected void turn(float turnAmt) {
		setDirection(getDirection() + turnAmt);
	}
	protected void move(boolean isRunning) {
		move(isRunning, getDirection());
	}
	protected void move(boolean isRunning, float moveDir) {
		
		float spd;
		if(isRunning)
			spd = maxSpeed;
		else
			spd = SP_WALK;

		move(spd, moveDir, true);
	}
	protected boolean move(boolean isRunning, vec2 toPoint) {
		
		float spd;
		if(isRunning)
			spd = maxSpeed;
		else
			spd = SP_WALK;

		if(getXYSpeed() < Math2D.calcPtDis(getX(),getY(), toPoint)) {
			move(spd, Math2D.calcPtDir(getX(),getY(), toPoint), false);
			return false;
		}
		else {
			setXYSpeed(0);
			return true;
		}
	}
	protected void move(float spd, float moveDir, boolean useCamera) {

		if(!canMove())
			return;
		
		float cDir = Camera.getDirection();
		if(!isPlayer() || !useCamera)
			cDir = 90;
		
		//If Move Direction Exists
		if(moveDir != -1) {
			//If Not in Air...
			if(!inAir) {
				isMoving = true;
				
				setDirection(cDir-90+moveDir);
				addXYSpeed( (spd - getXYSpeed())/5 );
				
				float f;
				f = (getXYSpeed() - maxSpeed/2)/(maxSpeed/2);
				if(getXYSpeed() <= maxSpeed/2)
					myBody.walk(5);
				else
					myBody.animate(Body.C_WALK, Body.C_RUN, 1); //f
			}

			float d = cDir - 90 + moveDir;
			if(useCamera) {
				// Rotate Camera Angle Based on Held Direction
				if(moveDir == 90 || moveDir == -90)
					camDirection = cDir;
				else if(moveDir == 0 || moveDir == 180)
					camDirection = cDir + .2f*Math2D.calcSmoothTurn(cDir,cDir-90+moveDir);
				else if(moveDir == 45 || moveDir == 135)
					camDirection = cDir + .2f*Math2D.calcSmoothTurn(cDir,cDir-90+moveDir);
				else
					camDirection = cDir - .2f*Math2D.calcSmoothTurn(cDir,180+cDir-90+moveDir);
			}
			
			setFaceDir(d);
			setDirection(faceDirection); //d
		}
		
		// Otherwise, Not Moving
		else {			
			isMoving = false;
			
			if(!inAir) {
				addXYSpeed((0 - getXYSpeed())/3f);

				if(getXYSpeed() < .01)
					setXYSpeed(0);
			}
			
			myBody.stand();
		}
	}
	protected void stop() {
		setXYSpeed(0);
		isMoving = false;
		myBody.stand();
	}
	
	
	
	public void centerCamera(float camDis, float camDir, float camZDir) {
		float cX, cY, cZ, camX, camY, camZ;
		cX = x;
		cY = y;
		cZ = floorZ+height;
		camX = cX + Math2D.calcPolarX(camDis, camDir+180, camZDir);
		camY = cY + Math2D.calcPolarY(camDis, camDir+180, camZDir);
		camZ = cZ + Math2D.calcPolarZ(camDis, camDir, camZDir+180);
		
		Camera.setProjection(camX, camY, camZ,  cX, cY, cZ);
		Camera.stiff();
	}

	public void moveToPoint(vec3 pt) 	{moveToPoint(pt.x(),pt.y(),pt.z());}
	public void moveToPoint(float x, float y, float z) {
		toPoint.set(0,x);
		toPoint.set(1,y);
		toPoint.set(2,z);
		toPoint.set(3,1);
	}
	public void moveToLenDir(float len, float dir) {moveToPoint(x+Math2D.calcLenX(len,dir),y+Math2D.calcLenY(len,dir),z);}	
	public void stopMovingToPoint() 	{toPoint.set(3,0);}
	
	
	// INTERACTING WITH OTHER ACTORS
	protected float calcDis(Actor a) {
		return Math2D.calcPtDis(x,y,a.getX(),a.getY());
	}
	protected float calcDir(Actor a) {
		return Math2D.calcPtDir(x,y,a.getX(),a.getY());
	}
	public void setPos(float x, float y) {
		setX(x);
		setY(y);
	}
	public void face(Actor a) {
		float dir = calcDir(a);
		setFaceDir(dir);
	}
	public void follow(Actor a) {
		float dis, dir;
		dis = calcDis(a);
		dir = calcDir(a);
		
		if(dis < 48) {
			stop();
			setFaceDir(dir);
		}
		else
			move(dis > 100, dir);
	}
	public boolean canSee(Actor a) {
		return (calcDis(a) < SIGHT_DIS && Math.abs(getDirection()-calcDir(a)) < SIGHT_FOV);
	}
	
	public void attack() {
		for(Actor a : actorList) {
			
		}
	}
	public void attack(Actor a) {
		a.damage(stat.calcDamageOn(a.stat));
	}
	
	public void heal(float amt) {
		stat.setHP(stat.getHP() + amt);
	}
	public void damage(float amt) {
		stat.damage(amt);
	}
	
	protected void jump() {
		float jumpSpeed;
		/*switch(chrctr)
		{
		    case "mario": jumpSpeed = JUMP_SPEED; break;
		    
		    case "luigi": jumpSpeed = 1.25f*JUMP_SPEED; break;
		    
		    case "peach":
		    case "darkpeach": jumpSpeed = .4f*JUMP_SPEED; break;
		    
		    default: jumpSpeed = JUMP_SPEED; break;
		}*/
		
		jumpSpeed = JUMP_SPEED;
		
		setZVelocity(jumpSpeed);
		z += getZVelocity();
		//buttonPrevious[0] = 1;
		isJumping = true;
		SoundController.playSound("Jump");
		    
		
		inAir = true;
		imageIndex = 0;
		
		
		jumpHold = 1;
		/*if(heldVDirection == 0 && heldHDirection == 0)
		    inAirDir = -1;
		else
		    inAirDir = Math2D.calcPtDir(0,0,heldHDirection,heldVDirection);*/
		if(getXYSpeed() == 0)
			inAirDir = -1;
		else
			inAirDir = getDirection();
				   
		inAirSpeed = getXYSpeed();
	}
	
	// Inventory
	public Inventory getInventory() {
		return inv;
	}
	public void addCoins(int num) {inv.addCoins(num);}
	
	public void give(Item it) {inv.add(it);}
	public void give(String name, int amount) {inv.add(name, amount);}
	public void give(ItemBlueprint itemType, int amount) {inv.add(itemType, amount);}
	
	private void dropItems() {inv.dropAll(new vec3(x,y,Heightmap.getInstance().getZ(x,y)));}
	public void buyItem(ItemBlueprint itemType, int value) {
		if(inv.getCoinNum() >= value) {
			give(itemType,1);
			addCoins(-value);
		}
	}
	
	public Stat getStat() {
		return stat;
	}
}