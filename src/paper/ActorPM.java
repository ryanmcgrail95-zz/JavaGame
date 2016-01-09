package paper;

import java.util.LinkedList;
import java.util.List;

import collision.C3D;
import datatypes.vec2;
import functions.FastMath;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import object.primitive.Physical;
import object.primitive.Positionable;
import resource.model.Model;
import resource.model.ModelCreator;
import resource.sound.Sound;
import time.Delta;

public abstract class ActorPM extends Physical implements AnimationsPM {	
	private BodyPM myBody;
	private byte state;
	private final static byte
		ST_NULL = 0, 
		ST_ONGROUND = 1,
		ST_INAIR = 2,
		ST_TALKING = 3;
	
	private Model mod;
	protected ActorPM followActor;

	private float size = 16;
	
	private static float 	SP_WALK = 1,
							SP_RUN = 3;
	private boolean isMoving, isSpinning, didJump = false;
	
	
	private List<Instant> instantList = new LinkedList<Instant>();
	private final static int INSTANT_NUMBER = 10;
	
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
	
	
		
	public ActorPM(String name, float x, float y, float z) {
		super(x, y, z);

		myBody = new BodyPM(name);
			myBody.setAutoIndex(false);
		setAnimationStill();
		
		maxSpeed = SP_RUN;
		
		ModelCreator creator = new ModelCreator(Model.TRIANGLES);
		creator.add3DModelCylinder(0,0,4, 1.2f*size/2, myBody.getHeight()-4, 5, false);
		
		mod = creator.endModel();
		
		for(int i = 0; i < INSTANT_NUMBER; i++)
			addInstant();
	}
	
	public void update() {
		super.update();
		
		if(followActor == null)
			control();
		else
			follow();
		addInstant();
		
		modelUpdate();
				
		//Model.get("Pleasant_Path_3"
		collideSplit();
		
		mod.translate(-getXPrevious(),-getYPrevious(),-getZPrevious());
		//mod.rotateZ(-getDirectionPrevious());
		//mod.rotateZ(getDirection());
		mod.translate(getX(), getY(), getZ());
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
		GL.setPerspective();
		GT.transformClear();
			transformTranslation();
			
			GL.setColor(RGBA.WHITE);
			myBody.draw();
		GT.transformClear();
		
		//mod.draw();
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



	protected void move(boolean isRunning, float moveDir) {
		move(isRunning, moveDir, false);
	}

	protected void move(boolean isRunning, float moveDir, boolean useCamera) {
		move(isRunning ? SP_RUN : SP_WALK, moveDir, useCamera);
	}

	protected boolean move(boolean isRunning, ActorPM other) {
		return move(isRunning, other.getX(), other.getY());
	}

	protected boolean move(boolean isRunning, float x, float y) {return move(isRunning, x, y, 0);}	
	protected boolean move(boolean isRunning, float x, float y, float radius) {return move(isRunning, x, y, radius, true);}
	protected boolean move(boolean isRunning, float x, float y, float radius, boolean stayOut) {
		float spd = (isRunning) ? SP_RUN : SP_WALK,
			curSpd = getXYSpeed(),
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
			if (!inAir) {
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
			buffer = height/2, safety = height/4,
			floorDis = C3D.raycastSplit(getX(),getY(),getZ()+buffer,0,0,-1, size/2);
				
		if(floorDis != -1 && floorDis < buffer) {
			floorDis -= buffer;
			didCollideFloor(z()-floorDis);
		}
		
		//COLLIDE WALL
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
		
		
		return false;
	}

	protected void jump() {
		if(!inAir) {
			didJump = inAir = true;
			setZVelocity(JUMP_SPEED);
			addZ(getZVelocity());
			Sound.play("jump");
		}
	}
	
	protected void startSpin() {
		
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
		
		myBody.animateModel();
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
	}

	public void stop() {
		setXYSpeed(0);
		setAnimationStill();
		isMoving = false;
	}
	
	protected abstract void control();
	private void follow() {
		Instant i = followActor.getInstant();
		move(true, i.x,i.y, followActor.size/2, false);
		if(i.didJump)
			jump();
		
		if(calcDis3D(followActor) > 120)
			setPos(followActor);
	}

	private void addInstant() {
		while(instantList.size() >= INSTANT_NUMBER)
			instantList.remove(0);
		instantList.add(new Instant(getX(),getY(),getZ(),getDirection(),didJump));
		didJump = false;
	}
	public Instant getInstant() {
		return instantList.get(0);
	}

	public void setCharacter(String name) {myBody.setCharacter(name);}
}
