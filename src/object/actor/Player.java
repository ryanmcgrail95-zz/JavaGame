package object.actor;

import obj.itm.ItemBlueprint;
import object.environment.Heightmap2;
import object.environment.Waypoint;
import paper.SpriteMap;
import phone.SmartPhone;
import window.Window;
import io.Controller;
import io.IO;
import io.Keyboard;
import io.Mouse;
import functions.Math2D;
import gfx.Camera;
import gfx.FireSprite;
import gfx.GOGL;
import gfx.Overlay;
import gfx.RGBA;
import gfx.WorldMap;

public class Player extends Actor {	
	private static Player instance;
	private boolean canControl = true;
		
	private Player(float x, float y, float z) {
		super(x, y, z);
		
		shouldAdd = false;
		
		name = "Player";
		
		type = T_PLAYER;
		
		loadInventory("Resources/Data/Player/inventory.dat");
	}
	
	
	private boolean checkControllable() {
		return canControl && !Window.isWindowOpen();
	}

	
	public boolean isLookCameraActive() {
		return SmartPhone.isActive() || WorldMap.isActive();
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
	
	public void add() {		
		if(!isLookCameraActive())
			super.add();
	}
	public void draw() {		
		if(!isLookCameraActive())
			super.draw();
		
		GOGL.setPerspective();
		
		GOGL.setColor(RGBA.WHITE);
		GOGL.transformClear();
			transformTranslation();
			GOGL.transformTranslation(0,0,100);
			GOGL.transformSprite();
			
			GOGL.drawFBO(-8,-16, 16,32, FireSprite.getFBO());
		GOGL.transformClear();
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

				if(moveDir != -1) {
					if(hori != 0)
						this.setDirection(faceDirection = faceDirection - Math.signum(hori)*4);
					if(vert != 0)
						if(vert > 0)
							move(vert*maxSpeed,faceDirection,false,true);
						else
							move(vert*maxSpeed,faceDirection+180,false,true);
				}
				else
					move(false,-1,false,true);
			}
			
		//JUMPING
		if(Keyboard.checkDown('m')) {
			setZ(300);
			setZVelocity(0);
			//roll();
		}
		else if(Keyboard.checkPressed('e') && !WorldMap.isActive()) {
			if(!SmartPhone.isActive())
				setDirection(faceDirection = camDirection);
			else
				camDirection = faceDirection;
			GOGL.getCamera().smoothOnce(10);
			SmartPhone.setActive(!SmartPhone.isActive());
		}	
		else if(Keyboard.checkPressed('f') && !SmartPhone.isActive()) {
			if(!WorldMap.isActive())
				setDirection(faceDirection = camDirection);
			else
				camDirection = faceDirection;
			GOGL.getCamera().smoothOnce(10);
			WorldMap.setActive(!WorldMap.isActive());
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
		return instance;
	}


	public static Player create(float x, float y, float z) {
		if(instance == null)
			instance = new Player(x,y,z);
		return instance;
	}
}
