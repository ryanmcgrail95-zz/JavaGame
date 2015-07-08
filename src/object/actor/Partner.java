package object.actor;

import functions.Math2D;

public class Partner extends Actor {
	private final static byte HA_NONE = -1, HA_SWITCH_TOPLAYER = 0, HA_SWITCH_OUT = 1, HA_SWITCH_IN = 2;
	private byte helperAction = HA_NONE;

	//SWITCHING VARIABLES
	private String newPartner = "";
	private float switchingPartner = 0,
		floatshrinkStartX = 0,
		shrinkStartY = 0,
		shrinkStartZ = 0,
		shrink = 1;

	
	//WATT VARIABLES
	private boolean held = false;


	
	public Partner(float x, float y, float z, String characterType) {
		super(x, y, z, characterType);
		
		autoXYVel = false;
	}
	
	
	public void update(float deltaT) {
		super.update(deltaT);
		
		byte ca = 0;
		
		/*if(d3d_point_distance(parCamera.x,y,z,parCamera.x,parCamera.y,parCamera.z) > 150) {
		    x = parPlayer.x + lengthdir_x(16,parPlayer.direction+180);
		    y = parPlayer.y + lengthdir_y(16,parPlayer.direction+180);
		    z = parPlayer.z;
		}*/
		
		//if(ca == CA_ROOM_ENTER)
		//	return;
		
		/*if(held)
		{
		    if(parPlayer.vDir == -1)
		        depth = -1000;
		    else
		        depth = -998;
		}
		else
		{
		    if point_distance(x,y,parCamera.x,parCamera.y) > point_distance(parPlayer.x,parPlayer.y,parCamera.x,parCamera.y)
		        depth = -998;
		    else
		        depth = -1000;
		}

		if(ca != "" || helperAction != HA_NONE)
		    helper_animation();
		*/		

		
		
		//ca == PARAKARRY
		//ca == KOOPER
		//if(ca == CA_SWITCH_TOPLAYER || ca == CA_SWITCH_OUT || ca == CA_SWITCH_IN) return;
		
		
		//Set Sprite if Not Speaking
		if(!inAir)
		{
		    if(isMoving)
		        setSpriteRun();
		    else
		        setSpriteStill();
		}
		else
		    setSpriteJump();


		//Set Sprite if Speaking
		/*if(isSpeaking) {
		    sprite = sprSpeaking;
		    //Animate Mouth
		    if(string_char_at(string(global.currentText),global.numText) != " " && string_char_at(string(global.currentText),global.numText) != "^" && string_char_at(string(global.currentText),global.numText) != "")
		        image_index -= .25;
		    else
		        image_index -= .5;
		    if(global.endLine)
		        image_index = 0;
		}
		if(global.currentText == "")
		    isSpeaking = false;*/
		
		
		//END 2
		
		//if(global.currentAction == "text" || global.currentAction == "bow_toplayer" || global.currentAction == "bow_invisible" || global.currentAction == "watt_toplayer" || global.currentAction == "tippi_toplayer")
		//    exit;

		Actor followInstance = Player.getInstance();

		//if global.currentAction != "" && !(string_p(global.currentAction,"switch")+1) && string_c(global.currentAction,0,4) != "door" || global.spin = 1
		//if((global.currentAction == "" || string_c(global.currentAction,0,4) == "door") && !global.spin && !helperIsXYBusy()) {
			updateFollowPlayer(followInstance);
		//}

		//Jump When Player Jumps
		//helper_update_jump();
	}
	
	public void updateFollowPlayer(Actor pl) {
		if(!inAir) {
	        if(calcDis(pl) > 16) {//!collision_circle(x,y,16,followInstance,0,1)) {
	        	x = pl.getFollowX();
	        	y = pl.getFollowY();
	        	
	        	setXYSpeed(CHAR_SPEED);
	        	//move_towards_point(followInstance.getFollowX(),followInstance.getFollowY(),point_distance(x,y,followInstance.getFollowX(),followInstance.getFollowY())/12);//12
	            isMoving = true;
	        }   
	        else
	        {
	            isMoving = false;
	            setXYSpeed(0);
	        }
	    }
	    else
	    {
	    	x = pl.getFollowX();
        	y = pl.getFollowY();
        	
        	setXYSpeed(CHAR_SPEED);

	        //move_towards_point_fixed(followInstance.xPrevious[9],followInstance.yPrevious[9],point_distance(x,y,followInstance.xPrevious[9],followInstance.yPrevious[9])/6);
	        isMoving = true;
	    }
		
		if(x != xP && y != yP)
			setDirection(Math2D.calcPtDir(xP,yP,x,y));
	    
	    if(!held)
	       setDirection(pl.getFollowDirection());
	    //if held = 1
	    //    flipDir = followInstance.flipDir
	}
	
	public void startSwitching(String newPartner) {
		this.newPartner = newPartner;
		helperAction = HA_SWITCH_TOPLAYER;
		switchingPartner = .5f;
		
		/*
		with(parHelper)
		{
		    var sm, la;
		    sm = 4;
		    la = 24;
		
		    shrinkStartX = parPlayer.x+lengthdir_x(la,parCamera.direction + parPlayer.dir*90);
		    shrinkStartY = parPlayer.y+lengthdir_y(la,parCamera.direction + parPlayer.dir*90);
		    shrinkStartZ = parHelper.z;
		
		    shrinkEndX = parPlayer.x+lengthdir_x(sm,parCamera.direction + parPlayer.dir*90);
		    shrinkEndY = parPlayer.y+lengthdir_y(sm,parCamera.direction + parPlayer.dir*90);
		}
		*/
	}
	
	public boolean isSwitching() {
		return (helperAction == HA_SWITCH_TOPLAYER || helperAction == HA_SWITCH_OUT || helperAction == HA_SWITCH_IN);
	}
}
