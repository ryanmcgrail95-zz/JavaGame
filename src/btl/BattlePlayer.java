package btl;

import io.Keyboard;

public class BattlePlayer extends BattleActor {

	public BattlePlayer(String name, float x, BattleController parent) {
		super(name, x, true, parent);
	}
	
	
	@Override
	public void attack() {
		state = ST_MOVE_TO;
		target = parent.getEnemy();
	}
}
