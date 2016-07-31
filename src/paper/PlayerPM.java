package paper;

import io.Controller;
import io.IO;
import io.Keyboard;
import object.primitive.Physical;
import resource.model.Model;
import time.Timer;
import cont.Text;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;

public class PlayerPM extends ActorPM {
	private static PlayerPM instance;
	private float cameraDirection = 90;
	
	private NPCPM talkNPC = null;
	private boolean isNearTalker = false;
	private float talkDis;
	private float talkSpinDir = 0;
	private Timer talkSpinTimer;
	
	private PlayerPM(String type, float x, float y, float z) {
		super(type,x,y,z);
		instance = this;
		PartnerPM.create("luigi", x, y, z);
		
		this.movementType = MOVEMENT.PLAYER;
		
		this.type = T_PLAYER;
		
		talkSpinTimer = new Timer(45);
		talkSpinTimer.disable();

		//setSurviveTransition(true);
	}
	
	public void destroy() {
		instance = null;
		talkSpinTimer.destroy();

		super.destroy();
	}
		
	@Override
	public void update() {
		super.update();
		
		if(!isNearTalker)
			talkSpinTimer.disable();				
		isNearTalker = false;
		
		if(canTalk())
			talkSpinDir -= 8;
		else
			talkSpinDir = 0;
		
		centerCamera(290, cameraDirection, -8.6f);
	}
	
	public void setCameraDirection(float cameraDirection) {
		this.cameraDirection = cameraDirection;
	}
	public float getCameraDirection() {
		return cameraDirection;
	}
	
	@Override
	public boolean collide(Physical other) {
		super.collide(other);
		
		if(other instanceof NPCPM) {			
			NPCPM o = (NPCPM) other;
			
			collideSize(other);
		
			float dis = other.calcDis2D(this);
			
			if(dis < 32)
				if(!isNearTalker || talkNPC == null || dis < talkDis) {
					if(!talkSpinTimer.isEnabled()) {
						talkSpinTimer.reset();
						talkSpinTimer.enable();
					}
					
					talkDis = dis;
					talkNPC = o;
					isNearTalker = true;
					
					o.face(this);
				}
		}
		
		return false;
	}
	
	@Override
	protected void control() {
		controlTalk();
		controlMove();
		controlJump();		
		controlSpin();
	}
		
	private void controlMove() {
		if(isSpinning())
			return;
		
		float moveDir = Controller.getDirDown();
		move(!Keyboard.checkDown('q'), moveDir, true);
		
		if(Keyboard.checkPressed('e')) {
			z(100);
			setZVelocity(0);
		}
		
		if(Keyboard.checkPressed('f'))
			hammer();
	}

	private void controlTalk() {
		if(canTalk())
			if(IO.eatPressed(IO.A)) {
				talkNPC.talk();
	
				//			Text.createTextDialog("HELLO! How are you doing?\nline2\nline3\nline4\nUhoh");
			}
	}
	private void controlJump() {
		if(IO.getAButtonPressed())
			jump();
	}
	private void controlSpin() {
		if(IO.getZButtonPressed())
			spin();
	}

	public static PlayerPM create() {
		if(instance == null)
			new PlayerPM("mario", 0,0,0);
		return instance;
	}
	public static PlayerPM create(float x, float y, float z) {
		create().setPos(x,y,z);
		PartnerPM.create(x, y, z);

		instance.floorZ = instance.toFloorZ = z;
		instance.centerCamera(290, 90, -8.6f);		
		GL.getMainCamera().teleport();

		return instance;
	}
	public static PlayerPM getInstance() {
		return instance;
	}
	
	public boolean canTalk() {
		return talkSpinTimer.isEnabled();
	}
	
	public void draw() {
		super.draw();
		
		if(canTalk()) {
			GT.transformClear();
			GT.transformTranslation(0,0,talkNPC.myBody.getExtraBaseHeight()+talkNPC.myBody.getHeight());
			talkNPC.transformTranslation();
			GT.transformRotationZ(talkSpinDir);
			
				GL.draw3DWall(-8,0,16,8,0,0);
			
				float v = (float) (.5 - .5*Math.cos(2*talkSpinDir/180*3.14159));
				GL.forceColor(RGBA.BLACK);
				GL.setAlpha(v);
				GL.draw3DWall(-8,0,16,8,0,0);
				GL.unforceColor();

			GT.transformClear();
		}
	}
}
