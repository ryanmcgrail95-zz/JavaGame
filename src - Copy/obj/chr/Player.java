package obj.chr;

import Sounds.SoundController;
import btl.DamageStar;
import sts.Stat;
import cont.CharacterController;
import cont.IO;
import cont.Messages;
import func.Math2D;
import gfx.GOGL;
import gfx.SpriteMap;

public class Player extends Actor {	
	private static Player instance;
	private boolean canControl = true;
	private float heldVDirection=0, heldHDirection=0;
	
	private float camDir = 0;
	
	private Player(float x, float y, float z, String characterType) {
		super(x, y, z, characterType);
		
		type = T_PLAYER;
	}

	//PARENT FUNCTIONS
		public void update(float deltaT) {
			super.update(deltaT);	
			
			if(canControl)
				control();
			
			/*
			canMove = 1
			canHammer = 1
			canJump = 1
			canSpin = 1;
			
			player_col_eventzone();
			
			if(animated_sprite_check_still())
			    image_index = 0;
			
			//Determine Whether or Not the Player Can Collide
			if global.currentAction != "" && global.currentAction != "stomp_drop" && global.currentAction != "stomp_up"
			{
			    if string_copy(global.currentAction,0,6) == "kooper"
			        canCollide = 0;
			}
			else
			    canCollide = 1
			
			//Determine Whether or Not the Player is in the Air
			if sprite = sprJump || sprite = sprJumpUp || sprite = ds_map_find_value(mySpriteMap,"hold_jump") || sprite = ds_map_find_value(mySpriteMap,"hold_jump_up")
			    inAir = true;
			else
			    inAir = false;
			    
			////////////////////////////
			//Subtract Timer Variables//
			////////////////////////////
			rocketTimer = increment_timer(rocketTimer,-1,-1)
			lightningTimer = increment_timer(lightningTimer,-1,-1)
			stompTimer = increment_timer(stompTimer,-1,-1)
			floatTimer = increment_timer(floatTimer,-1,-1)
			itemTimer = increment_timer(itemTimer,-1,-1)
			
			if(global.currentAction != "")
			    player_animation();
			
			if(enterPipe == 0 || enterPipe == 3)
			    player_con();
			
			player_update_sprite();*/
			/*player_hammer_update();
			
			if(z < -10000)
			{
			    d3d_goto_point(xstart,ystart,100)
			    zSpeed = 0;
			}
			
			if(!inAir)
			    spring = false;
			 */
			
			
			//Focus Camera on Player
			//centerCamera(220, camDirection, -10);
			centerCamera(220, camDirection, -35);
			//centerCamera(220, camDirection, 0);
		}
	
		public boolean draw() {
			super.draw();
			
			return true;
		}
		
	
	//Static Functions
	private void control() {
		if(!canControl)
		    return;

		/*if((global.currentAction != "" && !(string_p(global.currentAction,"door")+1) && !(string_p(global.currentAction,"house")+1)) || (selectHelper || selectItem) || room == rmTitleScreen || (enterPipe != 0 && enterPipe != 3) || (room == rmEditor && focus != 0) || action != "" || !global.altCam)
		{
		    if(global.currentAction == "text")// || focus != 0)
		    {
		        moving = false;
		        speed = 0;
		    }
		    exit;
		}

		if((string_p(global.currentAction,"door")+1) || (string_p(global.currentAction,"house")+1))
		    exit;*/

		boolean cMove,cJump,cStomp,cHammer,cSpin;
		cMove = true;
		cJump = true;
		cStomp = true;
		cHammer = true;
		cSpin = true;

		/*if(canInteract)
		{
		    cJump = false;
		    cStomp = false;
		}

		if(global.playerType != "mario") {
		    cStomp = false;
		    cHammer = false;
		    cSpin = false;
		}*/

		/*if(instance_exists(parHelper))
		    if(parHelper.held == 1)
		    {
		        cStomp = false;
		        cHammer = false;
		        cSpin = false;
		    }*/
		    
		if(isXyBusy()) {
		    cStomp = false;
		    cHammer = false;
		    cSpin = false;
		    cJump = false;
		    cMove = false;
		}


		if(cMove)
		    controlMove();
		if(cJump)
		    controlJump();
		/*if(cStomp)
		    player_con_stomp();
		if(cHammer)
		    player_con_hammer();*/
	}
		
	private void controlMove() {
				
		float cDir = GOGL.getCamDir();
		
		//MOVING
			float moveDir = IO.getWASDDir();
			
			//If Move Direction Exists
			if(moveDir != -1) {
				//Update Held Direction
				heldHDirection = (float) Math2D.calcLenX(1, moveDir);
				heldVDirection = (float) Math2D.calcLenY(1, moveDir);
				
			
				//If Not in Air...
				if(!inAir) {
					isMoving = true;
					
					setDirection(cDir-90+moveDir);
					addXYSpeed(MAX_SPEED*.75f);
					
					setSpriteRun();
				}
				
				float d;
				// Rotate Camera Angle Based on Held Direction
				if(moveDir == 90 || moveDir == -90) {
					camDirection = cDir;
					d = cDir - 90 + moveDir;
				}
				else if(moveDir == 0 || moveDir == 180) {
					d = cDir - 90 + moveDir;
					camDirection = cDir + .2f*Math2D.calcSmoothTurn(cDir,cDir-90+moveDir);
				}
				else if(moveDir == 45 || moveDir == 135) {
					d = cDir - 90 + moveDir;
					camDirection = cDir + .2f*Math2D.calcSmoothTurn(cDir,cDir-90+moveDir);
				}
				else {
					d = cDir - 90 + moveDir;	
					camDirection = cDir - .2f*Math2D.calcSmoothTurn(cDir,180+cDir-90+moveDir);
				}
				setDirection(d);
				//else
				//	add = addMax;
			}
			else {
				heldHDirection = 0;
				heldVDirection = 0;
				
				isMoving = false;
				
				if(!inAir) {
					addXYSpeed((0 - getXYSpeed())/1.5f);

					if(getXYSpeed() < .01) {
						setXYSpeed(0);
						setSpriteStill();
					}
				}
			}
			
			if(inAir) {
				inAirDir2 = moveDir;
				
				float vX, vY;
				vX = getXVelocity();
				vY = getYVelocity();
				
				if(inAirDir != -1) {
					vX = Math2D.calcLenX(inAirSpeed, inAirDir);
					vY = Math2D.calcLenY(inAirSpeed, inAirDir);
				}
				
				if(inAirDir2 != -1) {
					inAirSpeed2 = MAX_SPEED*.25f;
					vX += Math2D.calcLenX(inAirSpeed2, cDir-90 + inAirDir2);
					vY += Math2D.calcLenY(inAirSpeed2, cDir-90 + inAirDir2);
				}
				
				setVelocity(vX,vY);
			}
			else {
				inAirDir = moveDir;
				inAirSpeed2 = 0;
				inAirSpeed = getXYSpeed()*.5f;
			}
		
		//JUMPING
			
	}
	
	protected void controlJump() {
		if(jumpHold == 0) {
		    //Jump
		    if(IO.getAButtonPressed() && !inAir)
		    	jump();
		}
		else
		{
		    if(IO.getAButtonReleased()) {
		        if(jumpHold == 1)
		        {
		            jumpHold = 2;
		            //buttonPrevious[0] = 2;   
		        }
		        /*if(floatTimer > -1)
		            floatTimer = -1;*/
		    }
		    /*if(keyboard_check(ord('U')))
		        if(jumpHold == 1 && zSpeed < 0)
		            if(type == 'peach' || type == 'darkpeach')
		            {
		                zSpeed = 0;
		                floatTimer = 50;
		                jumpHold = 4;
		            }*/
		    if(jumpHold == 2)
		    {
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
			instance.setVelocity(0);
			
			instance.setSpriteStill();
			instance.isMoving = false;
		}
	}
	
	public static void stop() {
		instance.setXYSpeed(0);
		instance.imageIndex = 0;
		instance.isMoving = false;
		instance.clearHeldDirection();
	}
	
	public void clearHeldDirection() {
		//heldDirection = 0;
	    heldHDirection = 0;
	    heldVDirection = 0;
	}
	
	public boolean isXyBusy() {
		return false;
		/*if(global.switchingHelper == 1 || helper_is_switching())
		    return true;
		else
		    return false;*/
	}
	
	public static Player getInstance() {
		if(instance == null)
			instance = new Player(0,0,0,"mario");
		return instance;
	}
}
