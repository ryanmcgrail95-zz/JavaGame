package sts;

import brain.Name;
import datatypes.lists.CleanList;
import object.actor.Actor;
import functions.Math2D;
import functions.MathExt;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;

public class Stat {
	public static final byte 	EXP_WOODCUTTING = 0,
								EXP_FIREMAKING = 1;
	private static CleanList<Stat> instanceList = new CleanList<Stat>();
	private Actor owner;
	private String name;
	private float hp, toHP, maxHP, fp, maxFP, bp;
	private float[] statExp;
	
	public Stat(Actor owner) {
		this.owner = owner;
		
		name = Name.generateHumanName(Name.G_MALE);
		
		statExp = new float[2];
		
		toHP = hp = maxHP = 10;
		fp = maxHP = 10;
		bp = 3;
		
		instanceList.add(this);
	}
	
	
	// Stats
	public float getAttack() {
		return 1;
	}
	public float getDefense() {
		return 0;
	}
		
	// Get Stat Info
		public void setHP(float hp) {
			this.hp = MathExt.contain(0, hp, maxHP);
		}
		public void setToHP(float hp) {
			toHP = MathExt.contain(0, hp, maxHP);
		}
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
			
			hp += (toHP - hp)/10;
			
			float dX, dY, dW, dH, f;
			dW = 150;
			dH = 15;
			dX = 48;//16;
			dY = 40;//480 - (dX+dH);
			
			if(Math.abs(toHP - hp) > .05) {
				dX += MathExt.rnd(-2,2);
				dY += MathExt.rnd(-2,2);
			}
			
			String fracStr = (int)hp + "/" + (int)maxHP;
			
			//GOGL.drawString(dX,dY+5, "HP:");
			GOGL.drawFillBar(dX,dY,dW,dH, hp/maxHP);
			/*GOGL.setColor(0,0,0,1);
			GOGL.drawString(dX+24+50 - GOGL.getStringWidth(fracStr)/2,dY+5,fracStr);*/
			GOGL.setColor(1,1,1,1);
		}

		public void addExp(byte type, float expAmt) {
			statExp[type] += expAmt;
		}


		public void damage(float amt) {
			setToHP(toHP - amt);
		}


		public float calcDamageOn(Stat other) {
			return getAttack() - other.getDefense();
		}


		public void drawOverhead() {drawOverhead(owner.getX(), owner.getY(), owner.getZ()+owner.getHeight()+8);}
		public void drawOverhead(float x, float y, float z) {
			int[] pt = GOGL.calcScreenPt(x,y,z);
			GLText.drawStringCentered(pt[0],pt[1], 1,1, "<<"+name+">>");
			
			float dX, dY, dW, dH, f;
			dW = 150;
			dH = 15;
			dX = pt[0]-dW/2;//16;
			dY = pt[1]-dH/2 + 20;//480 - (dX+dH);
			
			if(Math.abs(toHP - hp) > .05) {
				dX += MathExt.rnd(-2,2);
				dY += MathExt.rnd(-2,2);
			}
			GOGL.drawFillBar(dX,dY,dW,dH, hp/maxHP);
		}


		public static void drawOverheads() {
			for(int i = 0; i < instanceList.size(); i++)
				instanceList.get(i).drawOverhead();
		}
}
