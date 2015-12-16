package btl;

import gfx.GL;
import io.Keyboard;

public class BattlePlayer extends BattleActor {

	public BattlePlayer(String name, float x, BattleController parent) {
		super(name, x, true, parent);
		
		GL.getMarioCamera().addDrawable(this);
	}
	
	
	@Override
	public void attack() {
		super.attack();
		
		state = ST_MOVE_TO;
		target = parent.getEnemy();
	}
}
