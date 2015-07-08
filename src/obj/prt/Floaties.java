package obj.prt;

import object.actor.Actor;
import object.actor.Player;
import Datatypes.SortedList;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;
import gfx.RGBA;

public class Floaties {
	private static Floaties instance;
	private static SortedList<Floatlet> particles = new SortedList<Floatlet>();
	private static float radius = 64;
	
	private class Floatlet {
		private float centerX, centerY, centerZ, moveDir, moveSpeed; 
		private float blinkDir;
		private float upDir, upAmplitude, upSpeed;
		private float deathTimer = 5;
		private boolean isVisible = false;
		
		public Floatlet() {
			Actor pl = Player.getInstance();
			float cD = 32, cDir, aX, aY, r = 64, rTZ = 32, rBZ = 8;
			
			cDir = Camera.getDirection();
			aX = Math2D.calcLenX(cD,cDir);
			aY = Math2D.calcLenY(cD,cDir);
			
			centerX = pl.getX() + aX + MathExt.rnd(-r,r);
			centerY = pl.getY() + aY + MathExt.rnd(-r,r);
			centerZ = pl.getZ() + MathExt.rnd(rBZ,rTZ);
			
			moveDir = MathExt.rnd(360);
			moveSpeed = MathExt.rndSign()*MathExt.rnd(2,3);
			
			blinkDir = MathExt.rnd(360);
			
			upDir = MathExt.rnd(360);
			upAmplitude = MathExt.rnd(6,10);
			upSpeed = MathExt.rndSign()*MathExt.rnd(3,4);
		}
		
		private void despawn() {
			particles.remove(this);
		}
		
		private boolean attemptDespawn() {
			deathTimer--;
			
			if(deathTimer == 0 || !GOGL.isPtOnscreen(centerX,centerY,centerZ)) {
				despawn();
				return true;
			}
			
			return false;
		}
		
		public void draw() {
			float oldBlinkDir = blinkDir, bA, oBA;

			blinkDir += 5;
			upDir += upSpeed;
			moveDir += moveSpeed;
			
			bA = Math2D.calcLenY(blinkDir);
			oBA = Math2D.calcLenY(oldBlinkDir);
		
			if(MathExt.between(bA,0,oBA)) {
				isVisible = true;
				if(attemptDespawn())
					return;
			}
			
			if(!isVisible)
				return;
				
			float x,y,z, blinkAlpha;
			x = centerX + Math2D.calcLenX(radius, moveDir);
			y = centerY + Math2D.calcLenY(radius, moveDir);
			z = centerZ + Math2D.calcLenY(upAmplitude, upDir);
			blinkAlpha = MathExt.contain(0, bA, 1);
			
			GOGL.transformTranslation(x,y,z);
			GOGL.transformRotationZ(Camera.getDirection()+90);
			GOGL.transformRotationX(90);
			
			GOGL.setColor(1,1,1, blinkAlpha);
			//GOGL.draw3DSphere(2);
			GOGL.fillCircle(0,0,1.5f, 10);
			GOGL.setColor(RGBA.WHITE);
			GOGL.transformClear();
		}
	}
	
	
	
	public static void ini() {
		instance = new Floaties();
	}
	
	public static void draw() {
		
		if(particles.size() < 15)
			particles.add(instance.new Floatlet());
		
		for(int i = 0; i < particles.size(); i++)
			particles.get(i).draw();
		
		particles.clean();
	}
}
