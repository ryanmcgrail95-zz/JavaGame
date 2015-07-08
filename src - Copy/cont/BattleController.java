package cont;

import func.Math2D;

public class BattleController {
	private static int playerHP, playerMaxHP;

	public void update() {
		
	}
	
	public static void setPlayerHP(int newHP) {
		playerHP = (int) Math2D.contain(0, newHP, playerMaxHP);
	}
}
