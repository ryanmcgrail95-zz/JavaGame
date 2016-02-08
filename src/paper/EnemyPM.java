package paper;

import object.primitive.Physical;
import resource.sound.Sound;
import rm.Room;

public class EnemyPM extends ActorPM {
	
	private boolean isChasing = false;
	private float noticeDis = 128;

	public EnemyPM(String name, float x, float y, float z) {
		super(name, x, y, z);
		
		SP_RUN = SPEED_RUN*.7f;
	}
	
	public void update() {
		start("EnemyPM.update()");
		super.update();
		end("EnemyPM.update()");
	}

	@Override
	public boolean collide(Physical other) {
		if(other instanceof PlayerPM)
			if(other.calcDis2D(this) < 8) {
				Room.changeRoom("Battle");
				Sound.playMusic("battleIntro", "battleLoop");
			}
		return false;
	}

	protected void control() {
		PlayerPM pl = PlayerPM.getInstance();
		
		if(canSee()) {
			if(calcDis2D(pl) < noticeDis)
				isChasing = true;
		}
		
		if(isChasing == true)
			if(calcDis2D(pl) < noticeDis)
				move(SP_RUN, pl);
			else {
				stop();
				isChasing = false;
			}
	}
}
