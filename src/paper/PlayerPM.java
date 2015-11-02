package paper;

import io.Controller;
import io.Keyboard;
import io.Mouse;
import phone.SmartPhone;
import functions.Math2D;
import gfx.GOGL;
import gfx.WorldMap;

public class PlayerPM extends ActorPM {
	private static PlayerPM instance;
	
	public PlayerPM(float x, float y, float z) {
		super("mario",x,y,z);
	}
	
	@Override
	public void update() {
		super.update();
		
		centerCamera(180, 90, -15);
	}
	
	@Override
	protected void control() {
		controlMove();
	}
	
	private void controlMove() {
		//MOVING
			float moveDir = Controller.getDPadDir();
			move(Mouse.getRightMouse(), moveDir, true);
							
		//JUMPING
		if(Keyboard.checkDown('m')) {
			setZ(300);
			setZVelocity(0);
			//roll();
		}
	}

	public static PlayerPM create(float x, float y, float z) {
		if(instance == null)
			instance = new PlayerPM(x,y,z);
		return instance;
	}
	public static PlayerPM getInstance() {
		return instance;
	}
}
