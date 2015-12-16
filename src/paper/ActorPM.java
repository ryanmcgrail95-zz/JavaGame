package paper;

import collision.C3D;
import datatypes.vec2;
import functions.FastMath;
import functions.Math2D;
import functions.Math3D;
import gfx.Camera;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import gfx.TextureExt;
import obj.prt.Dust;
import object.actor.Actor;
import object.actor.body.Body;
import object.primitive.Environmental;
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

	private float size = 32;
	
	private static float 	SP_WALK = 3,
							SP_RUN = 3;
	private boolean isMoving;
		
	public ActorPM(String name, float x, float y, float z) {
		super(x, y, z);

		myBody = new BodyPM(name);
			myBody.setAutoIndex(false);
		setAnimationStill();
		
		maxSpeed = SP_RUN;
		
		ModelCreator creator = new ModelCreator(Model.TRIANGLES);
		creator.add3DModelCylinder(0,0,4, 1.2f*size/2, myBody.getHeight()-4, 6);
		
			mod = creator.endModel();
	}
	
	public void update() {
		super.update();
		control();
		
		modelUpdate();
				
		collideModel(Model.get("Battle-Pleasant_Path_1"));
		mod.translate(-getXPrevious(),-getYPrevious(),-getZPrevious());
		mod.rotateZ(-getDirectionPrevious());
		mod.rotateZ(getDirection());
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

	protected boolean move(boolean isRunning, float x, float y) {

		float spd;
		if (isRunning)
			spd = SP_RUN;
		else
			spd = SP_WALK;

		if (getXYSpeed() < Math2D.calcPtDis(getX(), getY(), x, y)) {
			move(spd, Math2D.calcPtDir(getX(), getY(), x, y), false);
			return false;
		} else {
			setXYSpeed(0);
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
	
	protected boolean collideModel(Model m) {
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
				for(h = maxH; h == maxH; h -= 16/*maxH/numHs*/) {
					curDis = C3D.raycastPolygon(x,y,z+h, xN,yN,0, mod);
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
					x(x() + oXN*outDis);
					y(y() + oYN*outDis);
				}
			}
		}
		
		
		return false;
	}
	
	protected void jump() {
		if(!inAir) {
			setZVelocity(JUMP_SPEED);
			addZ(getZVelocity());
			inAir = true;
			Sound.play("jump");
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
		
		if(isMoving)
			myBody.addIndex(getXYSpeed()/maxSpeed*IMAGE_SPEED);
		else
			myBody.setIndex(0);
		
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
		isMoving = false;
	}
	
	protected abstract void control();

	public void setCharacter(String name) {myBody.setCharacter(name);}
}
