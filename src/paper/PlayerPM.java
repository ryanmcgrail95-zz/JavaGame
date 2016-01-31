package paper;

import collision.C3D;
import io.Controller;
import io.IO;
import io.Keyboard;
import resource.model.Model;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;

public class PlayerPM extends ActorPM {
	private static PlayerPM instance;
	
	private PlayerPM(String type, float x, float y, float z) {
		super(type,x,y,z);
		instance = this;
		PartnerPM.create("luigi", x, y, z);
		
		setSurviveTransition(true);
	}
	
	public void destroy() {
		super.destroy();
		instance = null;
	}
		
	@Override
	public void update() {
		super.update();
						
		centerCamera(290, 90, -8.6f);
	}
	
	@Override
	protected void control() {
		controlMove();
		controlJump();		
	}
		
	private void controlMove() {
		float moveDir = Controller.getDPadDir();
		move(!Keyboard.checkDown('q'), moveDir, true);
		
		if(Keyboard.checkPressed('e')) {
			z(100);
			setZVelocity(0);
		}
		
		if(Keyboard.checkPressed('f'))
			hammer();
	}
	private void controlJump() {
		if(IO.getAButtonPressed())
			jump();
	}
	private void controlSpin() {
		if(Keyboard.checkDown('m'))
			startSpin();
	}

	public static PlayerPM create() {
		if(instance == null)
			new PlayerPM("mario", 0,0,0);
		return instance;
	}
	public static PlayerPM create(float x, float y, float z) {
		create().setPos(x,y,z);
		PartnerPM.create(x, y, z);
		return instance;
	}
	public static PlayerPM getInstance() {
		return instance;
	}
}
