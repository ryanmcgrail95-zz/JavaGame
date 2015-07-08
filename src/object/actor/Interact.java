package object.actor;

import object.primitive.Drawable;
import object.primitive.Physical;
import gfx.GOGL;

import com.jogamp.opengl.util.texture.Texture;

public class Interact extends Drawable {
	
	float timer, size, rotate;
	Texture sprite;
	Player owner;
	Physical ptObj;
	
	public Interact() {
		//global.doorOpen = true;

		sprite = null;//sprExcPoint;
		rotate = GOGL.getCamDir() + 90;
		size = 0;

		timer = -1;

		ptObj = null;
	}
	
	public void update(float deltaT) {
		/*if(global.currentText != "")
		    return;

		timer = increment_timer(timer,-1,-.5);

		//Follow Player
		goto_point(parPlayer.x,parPlayer.y);

		//Reset Variables
		canInteract = false;

		//Set Direction to Player's Direction
		var hDir,vDir;
		hDir = parPlayer.dir;
		if(parPlayer.heldVDirection != 0)
		    vDir = parPlayer.heldVDirection;
		else
		    vDir = 0;
		direction = parCamera.direction - 90 + point_direction(0,0,hDir,vDir);

		
		var dir, toCamDir, aX, aY;
		dir = parCamera.direction - parPlayer.flipDir*90;
		toCamDir = parCamera.direction + 180;
		aX = lengthdir_x(8,toCamDir);
		aY = lengthdir_y(8,toCamDir);
		//Create Smoke when Stomping or Hammering
		/*if(parPlayer.action == "hammer" && parPlayer.image_index >= 10 && parPlayer.image_index <= 11) {
		    d3d_instance_create(parPlayer.x+aX+lengthdir_x(20,dir),parPlayer.y+aY+lengthdir_y(20,dir),parPlayer.floorZ,objSmoke);
		    d3d_instance_create(parPlayer.x+aX+lengthdir_x(6,dir),parPlayer.y+aY+lengthdir_y(6,dir),parPlayer.floorZ,objSmoke);
		}
		if(parPlayer.stompTimer == 1.5) {
		    d3d_instance_create(parPlayer.x+lengthdir_x(-7,dir),parPlayer.y+lengthdir_y(-7,dir),parPlayer.floorZ,objSmoke);
		    d3d_instance_create(parPlayer.x+lengthdir_x(7,dir),parPlayer.y+lengthdir_y(7,dir),parPlayer.floorZ,objSmoke);
		}*//*

		//Spin Floating Symbols
		if(rotate >= 180)
		    rotate = 0;
		
		
		//END STEP
		
		if(global.currentAction == "text") {
		    canInteract = false;
		    exit;
		}

		if(!owner.isSpinning) {
		    interact_col_npc();
		    interact_col_sign();
		    interact_col_groundblock();
		}
		    
		if(!canInteract) {
		    rotate = parCamera.direction;
		    size = 0;
		    visible = false;
		}
		if(canInteract) {
		    visible = true;
		    if(size != 1)
		    {
		        size += (1 - size)/5;
		        if(Math.abs(1-size) < .05)
		            size = 1;
		    }
		    else
		    {
		        if(sprite == sprTalkWait)
		            rotate += 6;
		        if(sprite == sprExcPoint)
		            rotate += 6;
		    }
		}*/
	}
}
