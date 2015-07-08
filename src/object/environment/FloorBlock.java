package object.environment;

import com.jogamp.opengl.util.texture.Texture;

import functions.Math2D;
import object.primitive.Environmental;
import object.primitive.Physical;

public class FloorBlock extends Environmental {
	protected final static byte T_NONE = -1, T_WALL = 0, T_FLOOR = 1, T_STEP = 2, T_ELEVATOR = 3;
	protected byte type = T_NONE;
	protected float x1, y1, z1, x2, y2, z2;
	private Texture tex;
	
	
	
	//Elevator Variables
	private float eDir, eSpeed;

	//Dip Variables
	private byte dip = -1;
	private float dipZ = 0;
	
	
	public FloorBlock(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
		this.x1 = x1;		this.y1 = y1;		this.z1 = z1;
		this.x2 = x2;		this.y2 = y2;		this.z2 = z2;
		this.tex = tex;
	}
	
	
	
	public boolean collide(Physical other) {
		float space = 4;
		
		
		float oX, oY, oZ, oZP, oV, oZV;
		oX = other.getX();
		oY = other.getY();
		oZ = other.getZ();
		oZP = other.getZPrevious();
		oZV = other.getZVelocity();
		
		if(oX < x1-space || oX > x2+space || oY < y1-space || oY > y2+space)
			return false;
			
		
		boolean inside = (oX < x1 || oX > x2 || oY < y1 || oY > y2);
		
		/*
			if(global.currentAction != "" && string_c(global.currentAction,0,4) != "door" && string_c(global.currentAction,0,5) != "stomp")   
			if(string_copy(global.currentAction,0,6) != "kooper")
    		exit;
		*/
		
		
		//Is There a Floor Crack (the Stompable Cracks)
		/*if object_get_parent(otherCol.object_index) = parFloorCrack
		{
		    if otherCol.broken = 1
		    {
		        if global.currentAction != "stomp_drop" && global.currentAction != "stomp_up"
		            if object_index = parPlayer
		                if stompTimer = 3
		                    global.cameraUp = 0
		        exit
		    }
		    if otherCol.broken = 0 && x < otherCol.x+16 && x > otherCol.x-16 && y < otherCol.y+16 && y < otherCol.y-16
		    {
		        if z < topZ
		            z = topZ
		        variable_local_set(zVarName,z)
		    }
		    else
		        exit
		}*/

		//Exit If You're In a Pipe
		/*if(object_get_parent(object_index) == parPlayer && global.mode == "pm")
		    if enterPipe != 3 && enterPipe != 0
		        exit;
		*/
		
		float topZ, botZ;
		topZ = Math.max(z1, z2);
		botZ = Math.min(z1, z2);


		
		//Set Check Variables (Are You On/In The Floor?)
		boolean onTop, inBetween, onBottom, justBelow, eOnTop, eInBetween;
		onTop = inBetween = onBottom = justBelow = eOnTop = eInBetween = false;

		onTop = (oZ <= topZ && oZP >= topZ) || (oZ <= topZ-dipZ && oZP >= topZ-dipZ);
		if(dip != -1)
		    inBetween = (oZ <= topZ && oZ >= botZ) || (oZ <= topZ-dipZ && oZ >= botZ-dipZ && oZV <= 0);
		onBottom = (oZ <= botZ && oZP >= botZ);
		justBelow = (oZ+16 >= topZ && oZ <= topZ && inside);
		
		//System.out.println(oZP + ", " + oZ);

		if(other.isPlayer()) {
		    if(type == T_ELEVATOR && !other.getInAir() && oZV <= 0)
		    	eOnTop = (oZ+eDir*eSpeed == topZ || oZ+eDir*eSpeed == topZ-dipZ);
		}
		else if(type == T_ELEVATOR)
		        eOnTop = (oZ+eDir*eSpeed == topZ || oZ+eDir*eSpeed == topZ-dipZ);
		//Elevator In Between
		if(other.isPlayer()) {
		    if(type == T_ELEVATOR && !other.getInAir() && oZV <= 0)
		    	eInBetween = ((oZ <= topZ-eDir*eSpeed && oZ >= botZ-eDir*eSpeed) || (oZ <= topZ-eDir*eSpeed-dipZ && oZ >= botZ-eDir*eSpeed-dipZ));
		}


		if(onTop || inBetween || onBottom || justBelow || eOnTop || eInBetween)
		{	
		    if(dip != -1)
		        dip = 1;

		    other.setCurrentFloor(this);
		    
		    //other.addPosition(x - xprevious);
		    //y += (otherCol.y - otherCol.yprevious);
		    
		    
		    other.didCollideFloor(topZ);
		}
		else
		    if(dip != -1)
		        dip = 0;
			
		return false;
	}
	
	
	
	//GLOBAL FUNCTIONS
		public static Wall createBoundary(float x1, float y1, float x2, float y2) {
			//Creates an invisible wall with the given two points. This will always be vertical.

			Wall bnd = new Wall(x1, y1, Math2D.INFINITY, x2, y2, -Math2D.INFINITY, null);
			bnd.setVisible(false);
			
			return bnd;
		}
}
