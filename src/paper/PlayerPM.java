package paper;

import java.util.LinkedList;
import java.util.List;

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
	
	private PlayerPM(float x, float y, float z) {
		super("mario",x,y,z);
		instance = this;
		PartnerPM.create("luigi", x, y, z);
		
		C3D.splitModel(Model.get("Pleasant_Path_3").getTriangles(), 10,10,48);
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
	
	public void draw() {
		super.draw();
		
		GL.setPerspective();
		GL.setColor(RGBA.WHITE);
		GT.transformClear();
			GL.enableShader("Model");
			
			if(Keyboard.checkDown('t'))
				Model.get("Pleasant_Path_3").draw();
			else
				Model.get("Pleasant_Path_3").drawFast();
			GL.disableShaders();
		GT.transformClear();
	}
	
	private void controlMove() {
		float moveDir = Controller.getDPadDir();
		move(!Keyboard.checkDown('q'), moveDir, true);
		
		if(Keyboard.checkPressed('e')) {
			z(100);
			setZVelocity(0);
		}
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
			new PlayerPM(0,0,0);
		return instance;
	}
	public static PlayerPM create(float x, float y, float z) {
		create().setPos(x,y,z);
		return instance;
	}
	public static PlayerPM getInstance() {
		return instance;
	}
}
