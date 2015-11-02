package paper;

import datatypes.vec2;
import functions.FastMath;
import functions.Math2D;
import functions.Math3D;
import gfx.Camera;
import gfx.GOGL;
import gfx.RGBA;
import gfx.TextureExt;
import obj.prt.Dust;
import object.actor.Actor;
import object.actor.body.Body;
import object.primitive.Environmental;
import object.primitive.Physical;
import object.primitive.Positionable;
import time.Delta;

public abstract class ActorPM extends Physical {
	private float IMAGE_SPEED = 1.3f;
	
	private BodyPM myBody;
	private byte state;
	private final static byte
		ST_NULL = 0, 
		ST_ONGROUND = 1,
		ST_INAIR = 2,
		ST_TALKING = 3;
	
	private float stepZ, size = 32;
	
	private static float 	SP_WALK = 3,
							SP_RUN = 6;
	private boolean isMoving, stepSound;
	
	private float imageIndex = 0;
	private int imageNumber = 0;
	
	public ActorPM(String name, float x, float y, float z) {
		super(x, y, z);

		myBody = new BodyPM(name);
		setSprite(spriteMap.getStill());
		
		maxSpeed = SP_RUN;
	}
	
	public void update() {
		super.update();
		control();
		
		modelUpdate();
		if(imageIndex > imageNumber) {
            imageIndex -= imageNumber;
            stepSound = false;
        }
	}

	
	public void centerCamera(float camDis, float camDir, float camZDir) {
		float cX, cY, cZ, camX, camY, camZ;
		cX = x();
		cY = y();
		cZ = floorZ + size;
		camX = cX + Math3D.calcPolarX(camDis, camDir + 180, camZDir);
		camY = cY + Math3D.calcPolarY(camDis, camDir + 180, camZDir);
		camZ = cZ + Math3D.calcPolarZ(camDis, camDir, camZDir + 180);

		Camera cam = GOGL.getMainCamera();
		cam.setProjection(camX, camY, camZ, cX, cY, cZ);
	}

	
	public void setSprite(TextureExt sprite) {
		this.sprite = sprite;
		imageNumber = sprite.getImageNumber();
	}
	
	@Override
	public void draw() {
		GOGL.setPerspective();
		GOGL.setColor(RGBA.WHITE);
		GOGL.transformClear();
		transformTranslation();
		GOGL.transformTranslation(0,0,stepZ);
		GOGL.transformPaper();
		GOGL.drawRectangle(-size/2,-size, size,size);
		GOGL.drawTexture(-size/2,-size, size,size, sprite.getFrame(imageIndex));
		GOGL.transformClear();
	}

	@Override
	public void add() {
	}

	@Override
	public boolean collide(Physical other) {
		// TODO Auto-generated method stub
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

		float spd;
		if (isRunning)
			spd = SP_RUN;
		else
			spd = SP_WALK;

		move(spd, moveDir, useCamera);
	}

	protected boolean move(boolean isRunning, Actor other) {
		return move(isRunning, other.getPos().xy());
	}

	protected boolean move(boolean isRunning, vec2 toPoint) {

		float spd;
		if (isRunning)
			spd = SP_RUN;
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

	protected void move(float spd, float moveDir, boolean useCamera) {		
		//if (!canMove())
		//	return;
		
		float cDir = GOGL.getMainCamera().getDirection();
		if (!isPlayer() || !useCamera)
			cDir = 90;

		// If Move Direction Exists
		if (moveDir != -1) {
			// If Not in Air...
			if (!inAir) {

				setAnimation(AnimationsPM.S_RUN);
				isMoving = true;

				
				float newDir, curDir;
				curDir = getDirection();
				newDir = cDir - 90 + moveDir;
				

				if(FastMath.calcAngleDiff(newDir, curDir) <= 130) {
					setDirection(newDir);
					addXYSpeed( Delta.convert((spd - getXYSpeed()) / 5) );
				}
				else {
					addXYSpeed( Delta.convert((0 - getXYSpeed()) / 1.3f) );
					if(getXYSpeed() < .01)
						setDirection(newDir);
				}
			}
	
			//setFaceDir(d);
			//setDirection(faceDirection); // d
		}

		// Otherwise, Not Moving
		else {
			setSprite(spriteMap.getRun());

			if (!inAir) {
				addXYSpeed( Delta.convert((0 - getXYSpeed()) / 2f) ); // 3
				if(getXYSpeed() < .01) {
					isMoving = false;
					setXYSpeed(0);
					setSprite(spriteMap.getStill());
				}
			}
		}
	}
	
	private void setAnimation(Byte animation) {
		myBody.setAnimation(animation);		
	}

	public boolean checkRunning() {
		return sprite == spriteMap.getRun() || sprite == spriteMap.getRunUp();
	}
	
	public void modelUpdate() {
		/*if(isPlayer())
			if(isSpinning && spinTimer % 5 == 0)
				d3d_instance_create(x+lengthdir_x(5,direction+180),y+lengthdir_y(5,direction+180),z,objSmoke);*/
		
		if(isMoving)
			imageIndex += getXYSpeed()/maxSpeed*IMAGE_SPEED;
		else
			imageIndex = 0;
		
		if(checkSpriteRun()) {
	        if(imageIndex > 4 && imageIndex < 6 && !stepSound) {
	        	//Sound.playSound("Footstep");
	            stepSound = true;
	            //new Dust(x+Math2D.calcLenX(5,direction+180),y+Math2D.calcLenY(5,direction+180),z, 0, true);
	        }
	        else if(imageIndex > 0 && imageIndex < 1)
	            	stepSound = false;	        
			
			float toUpZ;
			toUpZ = Math.abs(Math2D.calcLenY(2,imageIndex*30));
			stepZ += (toUpZ - stepZ)/1.5;
			/*
		        if(image_index >= 0 && image_index < 2)
		            	flyZ += -flyZ/1.5;
		        else if((image_index >= 2 && image_index < 3) || (image_index >= 5 && image_index < 6))
		            	flyZ += (1 - flyZ)/1.5; //1
		        else if(image_index >= 3 && image_index < 5)
		            	flyZ += (2 - flyZ)/1.5; //2
			*/
		}
		else {
			stepZ += -stepZ/1.5;
			imageIndex = 0;
		}
	}

	public void stop() {
		setXYSpeed(0);
		isMoving = false;
	}
	
	protected abstract void control();
}
