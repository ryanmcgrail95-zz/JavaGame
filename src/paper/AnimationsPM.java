package paper;

public interface AnimationsPM {
	public final static float IMAGE_SPEED = .7f; //1.3f;
	public final static Byte
		S_STILL = 0, S_STILL_UP = 1, 
		S_RUN = 2, S_RUN_UP = 3,
		S_JUMP = 4, S_JUMP_UP = 5,
		S_SPIN = 6, 
	  
		S_LAND = 7,
		S_JUMP_LAND = 8,
	
		S_PREPARE_JUMP = 9,
		S_HURT = 10,
		S_DODGE = 11,
		
		S_BURNED = 12;
}
