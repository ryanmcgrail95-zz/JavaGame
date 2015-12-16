package paper;

import java.util.LinkedList;
import java.util.List;

import io.Controller;
import io.IO;
import io.Keyboard;
import resource.model.Model;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;

public class PlayerPM extends ActorPM {
	private static PlayerPM instance;
	
	
	private List<Instant> instantList = new LinkedList<Instant>();
	private int instantNum = 10;
	
	private class Instant {
		public float x,y,z, direction;
		
		public Instant(float x, float y, float z, float direction) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.direction = direction;
		}
	}
	
	
	
	private PlayerPM(float x, float y, float z) {
		super("mario",x,y,z);
		PartnerPM.create(x, y, z);
		
		for(int i = 0; i < instantNum; i++)
			addInstant();
	}
	
	public void addInstant() {
		while(instantList.size() >= instantNum)
			instantList.remove(0);
		instantList.add(new Instant(getX(),getY(),getZ(),getDirection()));
	}
	public Instant getInstant() {
		return instantList.get(0);
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
		
		addInstant();
		Instant i = getInstant();
		PartnerPM p = PartnerPM.getInstance();
		p.move(true, i.x,i.y);
	}
	
	public void draw() {
		super.draw();
		
		GL.setPerspective();
		GL.setColor(RGBA.WHITE);
		GT.transformClear();		
			Model.get("Battle-Pleasant_Path_1").draw();
		GT.transformClear();
	}
	
	private void controlMove() {
		float moveDir = Controller.getDPadDir();
		move(true, moveDir, true);
	}
	private void controlJump() {
		if(IO.getAButtonPressed())
			jump();
	}
	private void controlSpin() {
		if(Keyboard.checkDown('m')) {
			setZ(300);
			setZVelocity(0);
		}
	}

	public static PlayerPM create() {
		if(instance == null)	
			instance = new PlayerPM(0,0,0);
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
