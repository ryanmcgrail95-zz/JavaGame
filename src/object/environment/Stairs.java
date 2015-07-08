package object.environment;

import object.primitive.Environmental;
import object.primitive.Physical;

public class Stairs extends Environmental {
	public final static int T_FLOOR = 0, T_FLOORSIDE = 1, T_BLOCK = 2;
	private int type;
	
	
	public Stairs(int type) {
		super();
		
		this.type = type;	
	}


	public boolean collide(Physical other) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//PARENT FUNCTIONS
}
