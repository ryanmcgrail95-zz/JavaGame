package btl;

import functions.Math2D;
import functions.MathExt;

public class BattleEnemy extends BattleActor {
		
	public BattleEnemy(String name, float x, BattleController parent) {
		super(name, x, false, parent);
	}
	
	public void attack() {
		super.attack();

		state = ST_MOVE_TO;
		target = parent.getPlayer();
	}
}
