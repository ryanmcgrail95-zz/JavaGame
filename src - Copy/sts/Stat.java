package sts;

import func.Math2D;
import gfx.GOGL;

public class Stat {
	public static final byte EXP_WOODCUTTING = 0;
	private float hp, maxHP, fp, maxFP, bp;
	public float[] statExp;
	
	public Stat() {
		statExp = new float[1];
		
		hp = maxHP = 10;
		fp = maxHP = 10;
		bp = 3;
	}
	
	
	// Stats
	public int attack() {
		return 1;
	}
	public static int defense() {
		return 0;
	}
		
	// Get Stat Info
		public float getHP() {
			return hp;
		}
		public float getMaxHP() {
			return maxHP;
		}
		
		public float getFP() {
			return fp;
		}
		public float getMaxFP() {
			return maxFP;
		}
		
		public float getBP() {
			return bp;
		}


		public void draw() {
			float dX, dY, f;
			dX = 32;
			dY = 220;
			
			String fracStr = (int)hp + "/" + (int)maxHP;
			
			GOGL.drawString(dX,dY+5, "HP:");
			GOGL.drawFillBar(dX+24,dY,100,20, hp/maxHP);
			GOGL.setColor(0,0,0,1);
			GOGL.drawString(dX+24+50 - GOGL.getStringWidth(fracStr)/2,dY+5,fracStr);
			GOGL.setColor(1,1,1,1);
		}

		public void addExp(byte type, float expAmt) {
			statExp[type] += expAmt;
		}
}
