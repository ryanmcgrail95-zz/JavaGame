package object.actor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import brain.Name;
import cont.Messages;
import cont.Text;
import datatypes.StringExt;
import datatypes.vec2;
import datatypes.vec3;
import datatypes.vec4;
import datatypes.lists.CleanList;
import interfaces.Useable;
import io.Mouse;
import obj.itm.Inventory;
import obj.itm.Item;
import obj.itm.ItemBlueprint;
import obj.prt.Dust;
import object.actor.body.Body;
import object.environment.Floor;
import object.environment.Heightmap2;
import object.environment.ResourceMine;
import object.primitive.Physical;
import object.primitive.Positionable;
import paper.SpriteMap;
import resource.sound.Sound;
import sts.Stat;
import time.Delta;
import time.Timer;
import window.StoreGUI;
import fl.FileExt;
import functions.FastMath;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;
import gfx.RGBA;
import gfx.TextureExt;

public abstract class Actor extends Physical {
	private CleanList<Actor> actorList = new CleanList<Actor>();

	private float prevSpeed;

	private float sideRotAngle = 0;
	private boolean isFPS = false;
	
	private float headBobDir = 0;

	private static float SP_FRAC = 2f / 3;
	private static float SP_WALK = 9;
	{
		SP_WALK *= SP_FRAC;
	}

	protected float height = 56;
	protected boolean isMoving = false, isSpeaking = false, canCollide = true;

	private Body myBody;

	// MOVING TO POINT
	private vec4 toPoint = new vec4(-1, -1, -1, 0);

	// SIGHT VARIABLES
	private static float SIGHT_FOV = 45, SIGHT_DIS = 200;

	private Inventory inv;
	private Stat stat;

	protected float faceDirection;

	private float faceToDirection;
	protected float camDirection;

	// Air Variables
	protected float inAirDir, inAirDir2, inAirSpeed, inAirSpeed2;

	protected final static int A_NOTHING = -1, A_SPEAKING = 0, A_WAIT = 1,
			A_TOPOINT = 2, A_HOLD = 3, A_HAMMER = 4, A_TOHELPER = 5;
	protected int action = A_NOTHING;

	// GO TO VARIABLES
	float goToX, goToY, goToZ, goToVel;
	String goToScript;

	protected final static float IMAGE_SPEED = .8f;
	private float upZ = 0;
	protected boolean stepSound = false;

	private SpriteMap map;

	// Image Variables
	protected TextureExt image = null;
	protected float imageIndex = 0, imageNumber = 6;

	// Spinning Variables
	// protected boolean isSpinning = false;
	protected final static float SPIN_VEL = 3f; // 2.5
	protected float spinTimer = 0, spinDirection = 0;
	protected int[] spinSound = null;

	// CHARACTER VARIABLES
	protected String chrctr;

	// JUMPING VARIABLES
	protected boolean isJumping = false;
	protected float jumpHold = 0;

	protected Timer rollTimer, attackTimer;

	public static final byte T_MOVETO = 0, T_USE = 1, T_RUNSTORE = 2,
			T_MOVENEAR = 3, T_CHASE = 4, T_FACE = 5, T_TALKTO = 6,
			T_TALKING = 7, T_RESOURCEMINING = 8;
	private List<Task> taskList = new ArrayList<Task>();

	public void taskClear() {taskList.clear();}

	public void taskMoveTo(Positionable other) {taskMoveTo(other.getX(), other.getY());}
	public void taskMoveTo(float x, float y) {
		taskList.add(new Task(T_MOVETO, new vec4(x, y, 0, 1), null, null, null, null, null, 0));
	}

	public void taskMoveNear(Positionable other, float r) {taskMoveNear(other.x(), other.y(), r);}
	public void taskMoveNear(float x, float y, float r) {taskList.add(new Task(T_MOVENEAR, new vec4(x, y, 0, 1), null, null, null, null, null, r));}
	public void taskChase(Actor other) {taskList.add(new Task(T_CHASE, other));}
	public void taskUse(Useable obj) {taskList.add(new Task(T_USE, this, obj));}
	public void taskRunStore() {taskList.add(new Task(T_RUNSTORE));}
	public void taskFace(Actor other) {taskList.add(new Task(T_FACE, other));}
	public void taskResourceMine(ResourceMine other) {taskList.add(new Task(T_RESOURCEMINING, this, other));}

	public boolean checkTask(byte type) {return (taskList.size() > 0) ? (taskList.get(0).getType() == type) : false;}

	public boolean isTaskMoving() {return checkTask(T_MOVETO) || checkTask(T_MOVENEAR);}
	public boolean isRunningStore() {return checkTask(T_RUNSTORE);}
	public boolean isTalking() {return checkTask(T_TALKING);}
	public boolean isResourceMining() {return checkTask(T_RESOURCEMINING);}

	private class Task {
		private byte type;
		private vec4 toPoint;
		private Item myItem, targetItem;
		private Actor myActor, targetActor;
		private Useable targetUseable;
		private float targetValue;
		private ResourceMine targetResourceMine;

		public Task(byte type) {
			this.type = type;
		}

		public Task(byte type, Actor targetActor) {
			this.type = type;
			this.targetActor = targetActor;
		}

		public Task(byte type, Actor myActor, ResourceMine targetResourceMine) {
			this.type = type;
			this.myActor = myActor;
			this.targetResourceMine = targetResourceMine;
		}

		public Task(byte type, Actor myActor, Useable targetUseable) {
			this.type = type;
			this.myActor = myActor;
			this.targetUseable = targetUseable;
		}

		public Task(byte type, vec4 toPoint, Item myItem, Item targetItem,
				Actor myActor, Actor targetActor, Useable targetUseable,
				float targetValue) {
			this.type = type;
			this.toPoint = toPoint;
			this.myItem = myItem;
			this.targetItem = targetItem;
			this.myActor = myActor;
			this.targetActor = targetActor;
			this.targetUseable = targetUseable;
			this.targetValue = targetValue;
		}

		public void perform() {
			boolean done = false;

			if (type == T_MOVETO) {
				if (move(toPoint.get(3) == 1, toPoint.xy())) {
					stop();
					done = true;
				}
			} else if (type == T_MOVENEAR) {
				if (move(toPoint.get(3) == 1, toPoint.xy())
						|| calcPtDis(toPoint.x(), toPoint.y()) < targetValue) {
					stop();
					done = true;
				}
			} else if (type == T_USE) {
				targetUseable.use(myActor);
				done = true;
			} else if (type == T_CHASE) {
				if (move(true, targetActor))
					done = true;
			} else if (type == T_FACE) {
				if (face(targetActor))
					done = true;
			} else if (type == T_RESOURCEMINING) {
				if (targetResourceMine.mine(myActor))
					done = true;
			}

			if (done)
				taskList.remove(this);
		}

		public byte getType() {
			return type;
		}
	}

	public Actor(float x, float y, float z) {
		super(x, y, z);
		
		name = "Actor";
		
		shouldAdd = true;

		maxSpeed = 5;
   
		faceDirection = camDirection = 0;
		size = 10;

		myBody = new Body();
		inv = new Inventory(this);
		stat = new Stat(this);

		rollTimer = new Timer(0, 60);
		attackTimer = new Timer(0, 2);
	}

	public void die() {
		dropItems();
		destroy();
	}

	// PARENT FUNCTIONS
	public void update() {

		prevSpeed = getXYSpeed();

		super.update();

		if (canControl())
			control();
		updateAdvancedMotion();

		myBody.step(x(),y(),z(),getDirection());
	}

	public boolean collide(Physical other) {
		return false;
	}

	// ACCESSOR AND MUTATOR FUNCTIONS
	public float getHeight() {
		return height;
	}

	public void draw() {
		myBody.draw(x(), y(), z(), 16, faceDirection);
	}
	public void add() {
		myBody.add(x(), y(), z(), 16, faceDirection);
	}

	public boolean setFaceDir(float toDir) {

		if (prevSpeed < .1)
			faceDirection = faceToDirection = toDir;
		else if (FastMath.calcAngleDiff(toDir, faceDirection) > 160)
			faceDirection = faceToDirection = toDir;
		else
			faceToDirection = toDir;

		return (faceDirection == toDir);
	}

	public void setCameraDir(float toDir) {
		float camDir, dir;
		dir = camDirection - getDirection();
		dir = MathExt.snap(dir, 90);

		// Rotate Camera Angle Based on Held Direction
		if (dir == 90 || dir == -90)
			camDir = toDir;
		else if (dir == 0 || dir == 180)
			camDir = toDir + .2f
					* Math2D.calcSmoothTurn(toDir, toDir - 90 + dir);
		else if (dir == 45 || dir == 135)
			camDir = toDir + .2f
					* Math2D.calcSmoothTurn(toDir, toDir - 90 + dir);
		else
			camDir = toDir - .2f
					* Math2D.calcSmoothTurn(toDir, 180 + toDir - 90 + dir);

		camDirection += Math2D.calcSmoothTurn(camDirection, camDir, 10);
	}

	public void land() {
		setZVelocity(0);
		isJumping = false;

		if (inAir) {
			inAir = false;

			float cDir = GOGL.getCamera().getDirection();
			// new
			// Dust(x+Math2D.calcLenX(7,cDir-90),y+Math2D.calcLenY(7,cDir-90),z+4,
			// 0, false);
			// new
			// Dust(x+Math2D.calcLenX(7,cDir+90),y+Math2D.calcLenY(7,cDir+90),z+4,
			// 0, false);
		}
	}

	protected abstract void control();

	protected void updateAdvancedMotion() {

		// Lower values for third makes it like ice physics!!
		if(!isFPS)
			faceDirection += Delta.convert(Math2D.calcSmoothTurn(faceDirection, faceToDirection,15));

		// ROLLING
		float rollSpeed;
		rollSpeed = 15;

		if (isRolling())
			addXYSpeed((rollSpeed - getXYSpeed()) / 5);

		// PERFORM TASKS
		if (!taskList.isEmpty())
			taskList.get(0).perform();
	}

	protected boolean canMove() {
		return !isRolling() && !isAttacking() && !isResourceMining(); // &&
																		// !Text.isActive();
	}

	protected boolean canControl() {
		return !isAttacking() && !Text.isActive() && !isTaskMoving()
				&& !isResourceMining();
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
		move(isRunning, moveDir, false);
	}

	protected void move(boolean isRunning, float moveDir, boolean useCamera) {move(isRunning,moveDir,useCamera,false);}
	protected void move(boolean isRunning, float moveDir, boolean useCamera, boolean fps) {

		float spd;
		if (isRunning)
			spd = maxSpeed;
		else
			spd = SP_WALK;

		move(spd, moveDir, useCamera, fps);
	}

	protected boolean move(boolean isRunning, Actor other) {
		return move(isRunning, other.getPos().xy());
	}

	protected boolean move(boolean isRunning, vec2 toPoint) {

		float spd;
		if (isRunning)
			spd = maxSpeed;
		else
			spd = SP_WALK;

		if (getXYSpeed() < Math2D.calcPtDis(getX(), getY(), toPoint)) {
			move(spd, Math2D.calcPtDir(getX(), getY(), toPoint), false);
			return false;
		} else {
			setXYSpeed(0);
			return true;
		}
	}

	protected void move(float spd) {
		move(spd, getDirection(), false, true);
	}

	protected void move(float spd, float moveDir, boolean useCamera) {move(spd,moveDir,useCamera,false);}
	protected void move(float spd, float moveDir, boolean useCamera, boolean fps) {
		float toAng = 0, toSpd = 1;
		
		if (!canMove())
			return;
		
		isFPS = fps;

		float cDir = GOGL.getMainCamera().getDirection();
		if (!isPlayer() || !useCamera)
			cDir = 90;

		// If Move Direction Exists
		if (moveDir != -1) {
			// If Not in Air...
			if (!inAir) {
				isMoving = true;

				toAng = FastMath.calcAngleSubt(getDirection(), cDir - 90 + moveDir) * .7f;
				toSpd = 10;

				//setDirection(cDir - 90 + moveDir);

				if(!fps)
					setDirection(cDir - 90 + moveDir);
				else {
					if(spd >= 0) {
						setDirection(moveDir);
						faceDirection = getDirection();
					}
					else {
						setDirection(moveDir);
						spd = Math.abs(spd);
					}
					
					headBobDir = myBody.getFrame()/12*360;
				}
				addXYSpeed( Delta.convert((spd - getXYSpeed()) / 5) );

				float f;
				f = (getXYSpeed() - maxSpeed / 2) / (maxSpeed / 2);
				if (getXYSpeed() <= maxSpeed / 2)
					myBody.walk(5);
				else
					myBody.animate(Body.C_WALK, Body.C_RUN, 1); // f
			}

			if(!fps) {
				float d = cDir - 90 + moveDir;
				if (useCamera) {
					// Rotate Camera Angle Based on Held Direction
					if (moveDir == 90 || moveDir == -90)
						camDirection = cDir;
					else if (moveDir == 0 || moveDir == 180)
						camDirection = cDir + .2f
								* Math2D.calcSmoothTurn(cDir, cDir - 90 + moveDir);
					else if (moveDir == 45 || moveDir == 135)
						camDirection = cDir + .2f
								* Math2D.calcSmoothTurn(cDir, cDir - 90 + moveDir);
					else
						camDirection = cDir - .2f
								* Math2D.calcSmoothTurn(cDir, 180 + cDir - 90
										+ moveDir);
				}
	
				setFaceDir(d);
				setDirection(faceDirection); // d
			}
		}

		// Otherwise, Not Moving
		else {
			isMoving = false;

			if (!inAir) {
				addXYSpeed( Delta.convert((0 - getXYSpeed()) / 4f) ); // 3
				//if(getXYSpeed() < .01)
				//	setXYSpeed(0);
				float nearestHBDir;
				if(headBobDir < 90)
					nearestHBDir = 0;
				else if(headBobDir < 270)
					nearestHBDir = 180;
				else
					nearestHBDir = 360;
				headBobDir += (nearestHBDir - headBobDir)/4;
			}

			myBody.stand();

			toAng = 0;
			toSpd = 2;
		}
		
		headBobDir %= 360;

		sideRotAngle += (toAng - sideRotAngle) / toSpd;

		myBody.setSideRotation(sideRotAngle);
	}

	public void stop() {
		setXYSpeed(0);
		isMoving = false;
		myBody.stand();
	}

	public void lookCamera() {
		float cX, cY, cZ, camX, camY, camZ, dis, dir, dirZ;
		camX = x();
		camY = y();
		camZ = floorZ + height * .9f;
		

		dis = 10;
		dir = faceDirection;
		dirZ = 0;
		
		float bobSide = Math2D.calcLenY(5,headBobDir),
			bobUp = Math.abs(Math2D.calcLenY(5,headBobDir));
		camX += Math2D.calcLenX(bobSide, dir+90);
		camY += Math2D.calcLenY(bobSide, dir+90);
		camZ += bobUp;
		
		cX = camX + Math3D.calcPolarX(dis, dir, dirZ);
		cY = camY + Math3D.calcPolarY(dis, dir, dirZ);
		cZ = camZ + Math3D.calcPolarZ(dis, dir, dirZ);

		Camera cam = GOGL.getMainCamera();
		cam.setProjection(camX, camY, camZ, cX, cY, cZ);
	}

	public void centerCamera(float camDis, float camDir, float camZDir) {
		float cX, cY, cZ, camX, camY, camZ;
		cX = x();
		cY = y();
		cZ = floorZ + height;
		camX = cX + Math3D.calcPolarX(camDis, camDir + 180, camZDir);
		camY = cY + Math3D.calcPolarY(camDis, camDir + 180, camZDir);
		camZ = cZ + Math3D.calcPolarZ(camDis, camDir, camZDir + 180);

		Camera cam = GOGL.getMainCamera();
		cam.setProjection(camX, camY, camZ, cX, cY, cZ);
	}

	public void moveToPoint(vec3 pt) {
		moveToPoint(pt.x(), pt.y(), pt.z());
	}
	public void moveToPoint(float x, float y, float z) {
		taskMoveTo(x, y);
	}

	public void moveToLenDir(float len, float dir) {
		moveToPoint(x() + Math2D.calcLenX(len, dir),
				y() + Math2D.calcLenY(len, dir), z());
	}

	public void stopMovingToPoint() {
		toPoint.set(3, 0);
	}

	// INTERACTING WITH OTHER ACTORSx
	public boolean face(Actor a) {
		return setFaceDir(calcDir(a));
	}

	public void follow(Actor a) {
		float dis, dir;
		dis = calcDis(a);
		dir = calcDir(a);

		if (dis < 48) {
			stop();
			setFaceDir(dir);
		} else
			move(dis > 100, dir);
	}

	public boolean canSee(Actor a) {
		return (calcDis(a) < SIGHT_DIS && Math.abs(getDirection() - calcDir(a)) < SIGHT_FOV);
	}

	public void attack() {
		for (Actor a : actorList) {

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

	protected void bounceOffHead() {
		setZVelocity(5);
	}

	protected void jump() {
		float jumpSpeed;
		jumpSpeed = JUMP_SPEED;

		setZVelocity(jumpSpeed);
		addZ(getZVelocity());
		// buttonPrevious[0] = 1;
		isJumping = true;
		Sound.play("jump");

		inAir = true;
		imageIndex = 0;

		jumpHold = 1;
		/*
		 * if(heldVDirection == 0 && heldHDirection == 0) inAirDir = -1; else
		 * inAirDir = Math2D.calcPtDir(0,0,heldHDirection,heldVDirection);
		 */
		if (getXYSpeed() == 0)
			inAirDir = -1;
		else
			inAirDir = getDirection();

		inAirSpeed = getXYSpeed();
	}

	// Inventory
	public Inventory getInventory() {
		return inv;
	}

	public void addCoins(int num) {
		inv.addCoins(num);
	}

	public boolean checkOwns(String itemName) {
		return (inv.findItem(itemName) != null);
	}

	public void giveTo(Actor other, String name) {
		if (checkOwns(name)) {
		}
	}

	public void give(Item it) {
		inv.add(it);
	}

	public void give(String name, int amount) {
		inv.add(name, amount);
	}
	public void give(ItemBlueprint itemType, int amount) {
		inv.add(itemType, amount);
	}

	private void replaceItem(String itOri, String itNew) {
		inv.replaceItem(itOri, itNew);
	}

	private void dropItems() {
		inv.dropAll(new vec3(x(), y(), Heightmap2.getInstance().getZ(x(), y())));
	}

	public void buyItem(ItemBlueprint itemType, int value) {
		if (inv.getCoinNum() >= value) {
			give(itemType, 1);
			addCoins(-value);
		}
	}

	public Stat getStat() {
		return stat;
	}

	public void cook() {
		replaceItem("Empty Bucket", "Water Bucket");
	}

	public void hover() {
		Player p = Player.getInstance();
				
		Mouse.setFingerCursor();

		if(isRunningStore()) {
			Messages.setActionMessage("Talk to Clerk");
			if(Mouse.getLeftClick())
				StoreGUI.open(stat.getName());
		}
	}
	
	public void loadInventory(String fileName) {
		BufferedReader str = new BufferedReader(new InputStreamReader(FileExt.get(fileName)));
		
		String itemName, count;
		String line;
		StringExt lineExt = new StringExt();
		try {
			while((line = str.readLine()) != null) {
				lineExt.set(line);
				
				itemName = lineExt.chompWord();
				count = lineExt.chompWord();
				
				itemName = itemName.replace("(", "").replace(")","").replace("_"," ");
				count = count.replace("x", "");
				
				this.give(itemName, Integer.parseInt(count));
			}
			
			str.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}