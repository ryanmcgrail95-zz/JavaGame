package paper;

import java.util.LinkedList;
import java.util.List;

import collision.C3D;
import ds.vec2;
import ds.lst.CleanList;
import dialog.Dialog;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import obj.env.blk.GroundBlock;
import object.actor.Player;
import object.primitive.Physical;
import object.primitive.Positionable;
import object.primitive.Updatable;
import resource.model.Model;
import resource.model.ModelCreator;
import resource.sound.Sound;
import sun.misc.Queue;
import time.Delta;
import time.Timer;

public abstract class ActorPM extends Physical implements AnimationsPM {	
	protected BodyPM myBody;
	
	public static enum MOVEMENT{
		STILL,
		PACING,
		WANDERING,
		PLAYER,
		ENEMY
	};
	protected MOVEMENT movementType = null;
	
	private byte state;
	private final static byte
		ST_NULL = 0, 
		ST_ONGROUND = 1,
		ST_INAIR = 2,
		ST_TALKING = 3,
		ST_SWITCH_OUT = 4, ST_SWITCH_IN = 5;
	
	
	private Model mod;
	protected ActorPM followActor;
	
	
	private static CleanList<ActorPM> actorList = new CleanList<ActorPM>("ActorPM");
	
	// SWITCHING
	private float swX, swY, swZ, swPerc = 0;
	private String swChar;
	
	protected float SP_WALK = 1,
					SP_RUN = 3,
					SP_SPIN = 5;
		
	private boolean isMoving, didJump = false;
	
	// SPIN VARIABLES
	private boolean
		isSpinning;
	private Timer
		spinTimer = new Timer(0,60);
	

	public void setCanMoveAll(boolean canMove) {
		for(ActorPM a : actorList)
			a.setCanMove(canMove);
	}
	
	public void setCanControl(boolean canControl) {this.canControl = canControl;}
	public boolean getCanControl() {return canControl;}
	
	public static void setCanControlAll(boolean canControl) {
		for(ActorPM a : actorList)
			a.setCanControl(canControl);
	}
	
	public void setCanSee(boolean canSee) {this.canSee = canSee;}
	public boolean canSee() {return canSee;}
	
	public static void setCanSeeAll(boolean canSee) {
		for(ActorPM a : actorList)
			a.setCanSee(canSee);
	}

	
	private boolean
		canControl = true,
		canSee = true;
	
	
	private Queue<Instant> instantList = new Queue<Instant>();
	
	private class Instant {
		public float x,y,z, direction;
		public boolean didJump;
		
		public Instant(float x, float y, float z, float direction, boolean didJump) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.direction = direction;
			this.didJump = didJump;
		}
	}
	
	public void switchCharacter(String newChar) {
		startSwitching();
		
		this.swChar = newChar;
		this.swX = x();
		this.swY = y();
		this.swZ = z();
		
		state = ST_SWITCH_OUT;
	}
	protected void startSwitching() {};
	protected void endSwitching() {};
	
	public void updateState() {
		switch(state) {
			case ST_SWITCH_OUT:
				swPerc += .15;
				if(swPerc >= 1) {
					swPerc = 1;
					state = ST_SWITCH_IN;
					setCharacter(swChar);
				}
				break;
				
			case ST_SWITCH_IN:
				swPerc -= .15;
				if(swPerc <= 0) {
					swPerc = 0;
					state = ST_ONGROUND;
					endSwitching();
				}
				break;
		}
		
		if(swPerc > 0) {
			PlayerPM p = PlayerPM.getInstance();
			float v = swPerc, vF = 1 - v,
				toX = p.x(),
				toY = p.y(),
				toZ = p.z();
			myBody.setPosition(
				x()*vF + v*toX,
				y()*vF + v*toY,
				z()*vF + v*toZ + Math2D.calcLenY(16, v*180));
			myBody.setScale(vF);
		}
	}
	
		
	public ActorPM(String name, float x, float y, float z) {
		super(x, y, z);
		
		size = 8;
		this.name = name;

		myBody = new BodyPM(this, name);
			myBody.setAutoIndex(false);
			myBody.enableSteps();
		setAnimationStill();
		
		maxSpeed = SP_RUN;
		
		ModelCreator creator = new ModelCreator(Model.TRIANGLES);
		creator.add3DModelCylinder(0,0,4, 1.2f*size/2, myBody.getHeight()-4, 5, false);
		
		mod = creator.endModel();
		mod.addReference();
		
		myBounds.addCylinder(0,0,0, 1.2f*size/2, myBody.getHeight()-4);
		
		actorList.add(this);
	}
	
	@Override	
	public void update() {
		super.update();
		
		if(isDestroyed())
			return;
		
		//start("ActorPM.update()-control");
		if(canControl) {
			if(followActor != null)
				follow();
			else {
				switch(movementType) {
					case STILL:
					break;
					
					case PACING:
					break;

					case WANDERING:
					break;

					case PLAYER:
					case ENEMY:
						control();
					break;
					
					default:
						throw new NullPointerException("Undefined movement type for " + this + ".");
				}
				// control
				updateSpin();
			}
		}
		addInstant();
		
		//start("ActorPM.update()-animation");
		modelUpdate();
		updateState();
		myBody.animateModel();
		//end("ActorPM.update()-animation");
		
		//start("ActorPM.update()-collision");
		collideSplit();		
		//end("ActorPM.update()-collision");

		//end("ActorPM.update()");
	}
	
	public float getHeight() {
		return myBody.getHeight();
	}
	
	@Override
	public void destroy() {
		if(isDestroyed())
			return;
		
		//start("ActorPM["+ name + "]().destroy()");
		super.destroy();

		myBody.destroy();
		mod.removeReference();
		
		//instantList.dequeue(3);
		
		actorList.remove(this);
		
		myBody = null;
		mod = null;
		instantList = null;
		followActor = null;

		//end("ActorPM["+ name + "]().destroy()");
	}

	
	public void setAnimationStill() {myBody.setAnimationStill();}
	public void setAnimationRun() 	{myBody.setAnimationRun();}
	public void setAnimationJump() 	{
		if(getZVelocity() >= 0)	myBody.setAnimationJump();
		else					myBody.setAnimationJumpFall();
	}
	
	public void centerCamera(float camDis, float camDir, float camZDir) {
		float cX, cY, cZ, camX, camY, camZ;
		cX = x();
		cY = y();
		cZ = floorZ + size;
		camX = cX + Math3D.calcPolarX(camDis, camDir + 180, camZDir);
		camY = cY + Math3D.calcPolarY(camDis, camDir + 180, camZDir);
		camZ = cZ + Math3D.calcPolarZ(camDis, camDir, camZDir + 180);

		Camera cam = GL.getMainCamera();
		cam.setProjection(camX, camY, camZ, cX, cY, cZ);
	}

	
	@Override
	public void draw() {
		if(myBody != null)
			myBody.draw();
	}

	@Override
	public void add() {}

	@Override
	public boolean collide(Physical other) {
		return false;
	}

	@Override
	public void land() {
		setZVelocity(0);
		
		
	}

	
	protected void turn(float turnAmt) {
		setDirection(getDirection() + turnAmt);
	}

	protected void face(ActorPM other) {
		setDirection(calcDir(other));
	}



	protected void move(boolean isRunning, float moveDir) {
		move(isRunning, moveDir, false);
	}

	protected void move(boolean isRunning, float moveDir, boolean useCamera) {
		move(isRunning ? SP_RUN : SP_WALK, moveDir, useCamera);
	}

	protected boolean move(boolean isRunning, ActorPM p) {
		return move(isRunning, p, 0);
	}
	protected boolean move(boolean isRunning, ActorPM p, float radius) {
		return move(isRunning, p, radius, true);
	}
	protected boolean move(boolean isRunning, ActorPM p, float radius, boolean stayOut) {
		return move(isRunning, p.getX(), p.getY(), radius, stayOut);
	}
	
	
	protected boolean move(float speed, ActorPM p) {
		return move(speed, p.x(), p.y(), 0, false);
	}
	
	
	protected boolean move(boolean isRunning, float x, float y) {return move(isRunning, x, y, 0);}	
	protected boolean move(boolean isRunning, float x, float y, float radius) {return move(isRunning, x, y, radius, true);}
	protected boolean move(boolean isRunning, float x, float y, float radius, boolean stayOut) {
		return move((isRunning) ? SP_RUN : SP_WALK, x,y, radius, stayOut);
	}
	
	protected boolean move(float spd, float x, float y, float radius, boolean stayOut) {
		float curSpd = getXYSpeed(),
			dis = Math2D.calcPtDis(getX(), getY(), x, y),
			dir = Math2D.calcPtDir(getX(), getY(), x, y),
			pushAmt = (stayOut) ? radius : curSpd;
		
		if (curSpd < dis-radius) {
			move(spd, dir, false);
			return false;
		} else {
			if(stayOut) {
				setX(x - Math2D.calcLenX(pushAmt, dir));
				setY(y - Math2D.calcLenY(pushAmt, dir));
			}
			stop();
			return true;
		}
	}

	protected void move(float spd, float moveDir, boolean useCamera) {		
		//if (!canMove())
		//	return;
		
		float cDir = GL.getMainCamera().getDirection();
		if (!isPlayer() || !useCamera)
			cDir = 90;

		// If Move Direction Exists
		if (moveDir != -1) {
			// If Not in Air...
			float addAmt;
			
			if(!inAir) {
				setAnimationRun();
				addAmt = .7f;
			}
			else {
				setAnimationJump();
				addAmt = .3f;
			}

			isMoving = true;
				
			float newDir;
			newDir = cDir - 90 + moveDir;
						
			setXVelocity( getXVelocity() + Math2D.calcLenX(addAmt,newDir) );
			setYVelocity( getYVelocity() + Math2D.calcLenY(addAmt,newDir) );
			
			containXYSpeed(spd);
			//setDirection(newDir);
			//addXYSpeed( Delta.convert((spd - getXYSpeed()) / 5) );
		}
	
		// Otherwise, Not Moving
		else {
			if(!inAir) {
				setAnimationRun();
				addXYSpeed( Delta.convert((0 - getXYSpeed()) / 3f) ); // 3
				if(getXYSpeed() < .01) {
					isMoving = false;
					setXYSpeed(0);
					setAnimationStill();
				}
			}
			else
				setAnimationJump();
		}
	}
	
	/*protected boolean collideModel(Model m) {
		float[][][] mod = m.getTriangles(), myMod = this.mod.getTriangles();
		
		float height =  myBody.getHeight(),
			buffer = height/2, safety = height/4,
			floorDis = C3D.raycastPolygon(getX(),getY(),getZ()+buffer,0,0,-1, mod);
		
		if(floorDis != -1 && floorDis < buffer) {
			floorDis -= buffer;
			didCollideFloor(z()-floorDis);
		}
		
		//COLLIDE WALL
		if(C3D.intersectPolygons(mod,myMod)) {
			float dir, numDirs = 8, curDis, minDis, w = size/2;
			float maxH = myBody.getHeight(), h, numHs = 3;
			float x,y,z, xN,yN, oXN,oYN;
			float[] triangleNormal = null;
			x = getX();
			y = getY();
			z = getZ();
			for(float d = 0; d < 360; d += 360/numDirs) {
				dir = getDirection() + d;
				
				xN = Math2D.calcLenX(dir);
				yN = Math2D.calcLenY(dir);
				
				minDis = w;
				for(h = maxH; h > 2; h -= maxH/numHs) {
					curDis = C3D.raycastPolygon(x,y,z+h, xN,yN,0, mod);
					if(curDis != -1)
						if(curDis < minDis) {
							minDis = curDis;
							triangleNormal = C3D.getTriangleNormal();
						}
				}
							
				if(minDis < w) {
					float outDis = w-minDis;
					oXN = -xN; //triangleNormal[0];
					oYN = -yN; //triangleNormal[1];
					
					if(FastMath.calcAngleDiff(Math2D.calcPtDir(0,0,oXN,oYN), dir) < 90) {
						oXN *= -1;
						oYN *= -1;
					}
					
					x(x() + oXN*outDis);
					y(y() + oYN*outDis);
				}
			}
		}
		
		
		return false;
	}*/

	protected boolean collideSplit() {		
		float[][][] myMod = mod.getTriangles();
		
		float height =  myBody.getHeight(),
			buffer = height/2, 
			safety = height/4,
			floorDis = C3D.raycastSplit(getX(),getY(),getZ()+buffer,0,0,-1, size/2);
		
		if(floorDis != -1) {
			float newFloorZ = z()+buffer-floorDis;
			
			if(floorDis <= buffer + safety) {
				floorDis -= buffer;
				didCollideFloor(newFloorZ);
			}
			else
				inAir = true;

			myBody.setFloorZ(newFloorZ);
		}
		
		//COLLIDE WALL
		if(C3D.intersectPolygonsSplit(myMod, x(),y(),z())) {
			float dir, numDirs = 8, curDis, minDis, w = size/2;
			float maxH = myBody.getHeight(), h, minH = 0, numHs = 3;
			float xN,yN, oXN,oYN, dire = getDirection();
			float[] triangleNormal = null;
			for(float d = 0; d < 360; d += 360/numDirs) {
				dir = dire + d;
				
				xN = Math2D.calcLenX(dir);
				yN = Math2D.calcLenY(dir);
				
				minDis = w;
				for(h = maxH; h > 2; h -= maxH/numHs) {
					curDis = C3D.raycastSplit(x(),y(),z()+h, xN,yN,0, w);
					if(curDis != -1)
						if(curDis < minDis) {
							minDis = curDis;
							minH = h;
							triangleNormal = C3D.getTriangleNormal();
						}
				}
							
				if(minDis < w) {
					oXN = triangleNormal[0];
					oYN = triangleNormal[1];
					
					if(Math.abs(Math2D.calcAngleDiff(Math2D.calcPtDir(0,0,oXN,oYN), dir)) < 90) {
						oXN *= -1;
						oYN *= -1;
					}

					float mD = C3D.raycastSplit(x(),y(),z()+minH, -oXN,-oYN,0, w);
					
					if(mD < w) {
						float outDis = w-mD;
						oXN = triangleNormal[0];
						oYN = triangleNormal[1];
						
						x(x() + oXN*outDis);
						y(y() + oYN*outDis);
						
						walkingAlongWall(oXN,oYN);
						
						if(!C3D.intersectPolygonsSplit(myMod,x(),y(),z()))
							break; 
					}
				}
			}
		}
		
		/*
		if(C3D.intersectPolygonsSplit(myMod)) {
			float dir, numDirs = 8, curDis, minDis, w = size/2;
			float maxH = myBody.getHeight(), h, numHs = 3;
			float x,y,z, xN,yN, oXN,oYN;
			float[] triangleNormal = null;
			x = getX();
			y = getY();
			z = getZ();
			for(float d = 0; d < 360; d += 360/numDirs) {
				dir = getDirection() + d;
				
				xN = Math2D.calcLenX(dir);
				yN = Math2D.calcLenY(dir);
				
				minDis = w;
				for(h = maxH; h > 2; h -= maxH/numHs) {
					curDis = C3D.raycastSplit(x,y,z+h, xN,yN,0, size/2);
					if(curDis != -1)
						if(curDis < minDis) {
							minDis = curDis;
							triangleNormal = C3D.getTriangleNormal();
						}
				}
							
				if(minDis < w) {
					float outDis = w-minDis;
					oXN = triangleNormal[0];
					oYN = triangleNormal[1];
					
					if(Math.abs(FastMath.calcAngleDiff(Math2D.calcPtDir(0,0,oXN,oYN), dir)) < 90) {
						oXN *= -1;
						oYN *= -1;
					}
					
					//mod.translate(-getX(),-getY(),-getZ());
					x(x() + oXN*outDis);
					y(y() + oYN*outDis);
					//mod.translate(getX(), getY(), getZ());
					
					if(!C3D.intersectPolygonsSplit(myMod))
						break;
				}
			}
		}
		 */
		
		
		return false;
	}

	protected void jump() {
		if(!inAir) {
			didJump = inAir = true;
			setZVelocity(JUMP_SPEED);
			addZ(getZVelocity());
			Sound.play("jump", this);
			
			stopSpin();
		}
	}
	
	protected void spin() {
		if(!isSpinning)
			if(!this.inAir) {
				isSpinning = true;
				spinTimer.reset();	
				
				myBody.setAnimationSpin();	
				myBody.enableDoBlur(true);
				
				Sound.play("spin");
			}
	}
	
	protected void stopSpin() {
		isSpinning = false;
		myBody.enableDoBlur(false);
	}
	
	public boolean isSpinning() {
		return isSpinning;
	}
	
	private void updateSpin() {
		if(isSpinning) {
			float frac = spinTimer.getFraction();
			this.setXYSpeed((frac < .7) ? SP_SPIN : 0);
			
			if(spinTimer.checkOnce()) {
				stopSpin();
				myBody.setAnimationStill();
			}
		}
	}
	
	private void setAnimation(Byte animation) {
		myBody.setAnimation(animation);		
	}

	public boolean checkRunning() {
		return myBody.checkAnimation(AnimationsPM.S_RUN) || myBody.checkAnimation(AnimationsPM.S_RUN_UP);
	}
	
	public void modelUpdate() {
		/*if(isPlayer())
			if(isSpinning && spinTimer % 5 == 0)
				d3d_instance_create(x+lengthdir_x(5,direction+180),y+lengthdir_y(5,direction+180),z,objSmoke);*/
		//start("ActorPM.modelUpdate()");

		float addAmt = 0;
		
		if(inAir)
			addAmt = .35f*IMAGE_SPEED;
		else {
			if(isMoving)
				addAmt = getXYSpeed()/maxSpeed*IMAGE_SPEED;
			else
				myBody.setIndex(0);
		}
		
		myBody.addIndex( Delta.convert(addAmt) );
		
		myBody.setSpeed(getXYSpeed(),getZVelocity());

		myBody.setPosition(getX(), getY(), getZ());
		myBody.setDirection(getDirection());
		
		
		/*if(checkSpriteRun()) {
	        if(imageIndex > 4 && imageIndex < 6 && !stepSound) {
	        	//Sound.playSound("Footstep");
	            stepSound = true;
	            //new Dust(x+Math2D.calcLenX(5,direction+180),y+Math2D.calcLenY(5,direction+180),z, 0, true);
	        }
	        else if(imageIndex > 0 && imageIndex < 1)
            	stepSound = false;	        
		}
		else {
			stepZ += -stepZ/1.5;
			imageIndex = 0;
		}*/
		//end("ActorPM.modelUpdate()");
	}

	public void stop() {
		setXYSpeed(0);
		setAnimationStill();
		isMoving = false;
	}
	
	public boolean canControl() {
		return !Dialog.isActive();	// No Menus Open
	}
	
	protected abstract void control();
	
	private void follow() {
		Instant i = followActor.getInstant();
		
		if(i != null) {
			move(true, i.x,i.y, followActor.size/2, false);
			if(i.didJump)
				jump();
			
			if(calcDis3D(followActor) > 120)
				setPos(followActor);
		}
	}
	
	protected final void hammer() {
		CleanList<Updatable> list = Updatable.getList();
		
		float dis = size, dir, tempSize, tempX, tempY;
		dir = getDirection();
		tempSize = size;
		tempX = x();
		tempY = y();
		
		size = 8;
		addX(Math2D.calcLenX(dis, dir));
		addY(Math2D.calcLenY(dis, dir));
		
		GroundBlock.hammerAll(this);
		
		size = tempSize;
		x(tempX);
		y(tempY);
	}

	private void addInstant() {
		/*while(instantList.size() >= INSTANT_NUMBER)
			instantList.remove(0);
		instantList.add(new Instant(getX(),getY(),getZ(),getDirection(),didJump));
		didJump = false;*/
	}
	public Instant getInstant() {
		return null; //return instantList.get(0);
	}

	public void setCharacter(String name) {myBody.setCharacter(name);}

	public String getCharacterName() {
		return myBody.getCharacterName();
	}

	public SpriteMap getSpriteMap() {
		return myBody.getCharacter().getSpriteMap();
	}
}
