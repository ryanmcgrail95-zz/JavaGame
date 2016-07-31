package paper;

import cont.Text;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;
import object.primitive.Physical;
import paper.ActorPM.MOVEMENT;
import resource.sound.Sound;
import rm.Room;
import script.Register;
import time.Timer;

public class NPCPM extends ActorPM {
	
	private boolean isChasing = false;
	private float noticeDis = 128;
	private Register text;
	
	public NPCPM(String name, float x, float y, float z) {
		super(name, x, y, z);
		
		this.movementType = MOVEMENT.STILL;
		SP_RUN *= .7f;
		
		this.text = addVar("text").set("");
	}
	
	public void destroy() {
		super.destroy();
	}
	
	public void update() {
		//start("EnemyPM.update()");
		super.update();
		//end("EnemyPM.update()");
	}

	@Override
	public boolean collide(Physical other) {
		super.collide(other);
		
		return false;
	}

	protected void control() {
		
	}
	
	public void talk() {
		Text.talk(text.getString());
	}
	
	@Override
	public void draw() {
		super.draw();
	}
}
