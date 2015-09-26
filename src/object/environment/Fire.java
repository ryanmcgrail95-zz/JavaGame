package object.environment;

import datatypes.lists.CleanList;
import interfaces.Useable;
import io.Mouse;
import functions.MathExt;
import gfx.GOGL;
import gfx.RGBA;
import object.actor.Actor;
import object.actor.Player;
import object.primitive.Environmental;
import object.primitive.Physical;
import time.Timer;

public class Fire extends Environmental implements Useable {
	
	private float fireHeight, fireRadius, fireDir, fireDirZ;
	private Timer flickerTimer;
	
	private CleanList<Firelet> list = new CleanList<Firelet>();
	
	public Fire(float x, float y) {
		super(x,y,true,false);
		flickerTimer = new Timer(3);
	}
	
	
	public class Firelet {
		float fX,fY,fZ;
		float dir = 0;
		
		public Firelet(float aX, float aY, float aZ) {
			fX = x() + aX;
			fY = y() + aY;
			fZ = z() + aZ;
		}
		
		public void draw() {
			dir += 2;
			
			GOGL.transformClear();
			GOGL.transformTranslation(fX,fY,fZ);
			GOGL.draw3DFrustem(3,5,3);
			GOGL.transformClear();
			
			if(dir > 360)
				list.remove();
		}
	}
	
	
	public void hover() {
		Mouse.setFingerCursor();
		if(Mouse.getLeftClick()) {
			Player p = Player.getInstance();
			p.taskClear();
			p.taskMoveTo(x(),y());
			p.taskUse(this);
		}
	}

	public void add() {}
	public void draw() {

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
		
		for(Firelet f : list)
			f.draw();
	}

	public boolean collide(Physical other) {return false;}


	public void use(Actor user) {
		user.cook();
	}
}
