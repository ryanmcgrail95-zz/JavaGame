package object.actor;

import obj.itm.ItemBlueprint;
import object.environment.Heightmap;
import io.Controller;
import io.IO;
import io.Mouse;
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

	//PARENT FUNCTIONS
		public void update() {
			super.update();
			
			if(Mouse.getRightMouse()) {
				centerCamera(1000, 90, -89.9f);
				Overlay.disable();
			}
			else {
				centerCamera(180, camDirection, -15);
				Overlay.enable();
			}
		}
		
	public void draw() {
		
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
				
		float cDir = Camera.getDirection();
		
		//MOVING
			float moveDir = Controller.getDPadDir();
			
			if(moveDir != -1)
				stopMovingToPoint();
			
			move(Mouse.getRightMouse(), moveDir);
			
		//JUMPING
		if(IO.getZButtonPressed())
			roll();
		
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
		    if(IO.getAButtonReleased()) {
		        if(jumpHold == 1) {
		            jumpHold = 2;
		            //buttonPrevious[0] = 2;   
		        }
		    }
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
