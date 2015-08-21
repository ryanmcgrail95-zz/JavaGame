package object.actor;

import obj.itm.ItemBlueprint;
import object.environment.Heightmap;
import object.environment.Waypoint;
import phone.SmartPhone;
import io.Controller;
import io.IO;
import io.Keyboard;
import io.Mouse;
import functions.Math2D;
import gfx.Camera;
import gfx.GOGL;
import gfx.Overlay;
import gfx.SpriteMap;

public class Player extends Actor {	
	private static Player instance;
	private boolean canControl = true;
		
	private Player(float x, float y, float z) {
		super(x, y, z);
		type = T_PLAYER;
	}

	
	public boolean isLookCameraActive() {
		return SmartPhone.isActive();
	}
	
	//PARENT FUNCTIONS
		public void update() {
			super.update();
			
			if(Mouse.getRightMouse()) {
				centerCamera(1000, 90, -89.9f);
				Overlay.disable();
			}
			else {
				if(isLookCameraActive())
					lookCamera();
				else
					centerCamera(180, camDirection, -15);
				Overlay.enable();
			}
			
			if(Keyboard.checkPressed('1'))
				new Waypoint(x(),y(),z()+20);
		}
		
	public void draw() {
		
		if(!isLookCameraActive())
			super.draw();
	}
	
	//Static Functions
	protected void control() {
		if(!canControl)
		    return;

		boolean cMove,cJump,cStomp,cHammer,cSpin;
		cMove = true;
		cJump = true;
		cStomp = true;
		cHammer = true;
		cSpin = true;

		if(cMove)
		    controlMove();
		if(cJump)
		    controlJump();
	}
		
	private void controlMove() {
				
		//float cDir = Camera.getDirection();
		
		//MOVING
			float moveDir = Controller.getDPadDir();
			
			if(moveDir != -1)
				stopMovingToPoint();
			
			boolean fps = isLookCameraActive();
			if(!fps)
				move(Mouse.getRightMouse(), moveDir, true);
			else {
				float hori, vert;
				hori = Math2D.calcLenX(moveDir);
					if(Math.abs(hori) < .2)
						hori = 0;
				vert = Math2D.calcLenY(moveDir);

				System.out.println(getDirection());
				if(moveDir != -1) {
					if(hori != 0)
						this.setDirection(getDirection() - Math.signum(hori)*4);
					if(vert != 0)
						move(vert*maxSpeed);
				}
				else
					move(false,-1,false,true);
			}
			
		//JUMPING
		if(IO.getZButtonPressed())
			roll();
		else if(Keyboard.checkPressed('E')) {
			if(!SmartPhone.isActive())
				setDirection(camDirection);
			else
				camDirection = getDirection();
			GOGL.getCamera().smoothOnce(10);
			SmartPhone.setActive(!SmartPhone.isActive());
		}		
		//Mouse Control
		/*if(Mouse.getLeftClick()) {
			moveToPoint(Heightmap.getInstance().raycastMouse());
		}*/
	}
	
	protected void controlJump() {
		if(jumpHold == 0) {
		    //Jump
		    if(IO.getAButtonPressed() && !inAir)
		    	jump();
		}
		else {
		    if(IO.getAButtonReleased())
		        if(jumpHold == 1)
		            jumpHold = 2;
		    if(jumpHold == 2) {
		        jumpHold = 3;
		        if(getZVelocity() > 0)
		            setZVelocity(0);   
		    }
		    if(!inAir)
		        if(jumpHold == 3 || jumpHold == 4)
		            jumpHold = 0;
		}            
	}
		
	public static void setControllable(boolean controllable, boolean stopCompletely) {
		instance.canControl = controllable;
		
		if(!controllable && stopCompletely) {
			instance.setVelocity(0,0,0);
			instance.isMoving = false;
		}
	}
	
	public static Player getInstance() {
		if(instance == null)
			instance = new Player(0,0,0);
		return instance;
	}
}
