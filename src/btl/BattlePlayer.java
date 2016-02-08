package btl;

import gfx.GL;
import io.Keyboard;
import object.primitive.Drawable;

public class BattlePlayer extends BattleActor {	
	public BattlePlayer(String name, float x, BattleController parent) {
		super(name, x, true, parent);
		
		GL.getMarioCamera().addDrawable(this);
		
		this.setSurviveTransition(true);
	}
	
	
	@Override
	public void attack() {
		super.attack();
		
		state = ST_MOVE_TO;
		target = parent.getEnemy();
	}
}
