package object.environment;

import io.Mouse;
import functions.MathExt;
import gfx.GOGL;
import gfx.RGBA;
import object.actor.Actor;
import object.actor.Player;
import object.primitive.Environmental;
import object.primitive.Physical;
import time.Timer;

public class Fire extends Environmental {
	
	private float fireHeight, fireRadius, fireDir, fireDirZ;
	private Timer flickerTimer;
	
	public Fire(float x, float y) {
		super(x,y,true);
		flickerTimer = new Timer(3);
	}
	
	
	public void hover() {
		Mouse.setFingerCursor();
		if(Mouse.getLeftClick()) {
			Player.getInstance().moveToPoint(getX(),getY(),getZ());
			
			// Using the Fire
			// ???
		}
	}
		
	public boolean draw() {

		if(flickerTimer.check()) {
			fireHeight = MathExt.rnd(14,20);
			fireRadius = MathExt.rnd(4,6);
			fireDir = MathExt.rnd(360);
			fireDirZ = MathExt.rndSign()*MathExt.rnd(8);
		}
		
		GOGL.setColor(new RGBA(112,63,24));
		for(int i = 0; i < 5; i++) {
			GOGL.transformTranslation(getX(),getY(),getZ());
			GOGL.transformRotationZ(1f*i*360/5);
			GOGL.transformRotationX(90);
			GOGL.draw3DFrustem(0,0,2, 2, 4, 10);
			GOGL.transformClear();
		}
		
		float fireBH, fireTH;
		fireTH = fireHeight*3/4;
		fireBH = (fireHeight - fireTH);
		
		GOGL.setColor(new RGBA(176,54,22));
		GOGL.transformTranslation(getX(),getY(),getZ());
		
		GOGL.transformTranslation(0,0,fireBH);
		GOGL.transformRotationX(fireDir);
		GOGL.transformRotationX(fireDirZ);
		GOGL.transformRotationX(-fireDir);
		GOGL.transformTranslation(0,0,-fireBH);
		
		GOGL.draw3DFrustem(0,0,2, 0, fireRadius, fireBH);
		GOGL.draw3DFrustem(0,0,2+fireBH, fireRadius, 0, fireTH);
		GOGL.transformClear();
 		
		return false;
	}

	public boolean collide(Physical other) {return false;}
}
