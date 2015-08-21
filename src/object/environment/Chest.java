package object.environment;

import interfaces.Useable;
import io.Keyboard;
import functions.Math2D;
import gfx.GOGL;
import obj.itm.Item;
import obj.itm.ItemContainer;
import object.actor.Actor;
import object.primitive.Environmental;
import object.primitive.Physical;

public class Chest extends Environmental implements Useable {
	private ItemContainer cont;
	private float hopZ, hopZSpd, rot, rotSpd;
	private float w, l, h, gravity = .3f, lidAng, lidSpd, bounceFrac = .3f; //.5f
	private final static float LID_CLOSED = 0, LID_OPEN = -135+360;
	private final static byte ANI_OPEN = 0, ANI_CLOSE = 1;
	private byte animation = -1;
	
	public Chest(float x, float y, String... items) {
		super(x,y, true, false);
		
		w = 8;
		l = 5;
		h = 5;
		
		cont = new ItemContainer(4*7);
		for(String i : items)
			cont.add(i,1);
	}
	
	public void destroy() {
		cont.destroy();
		super.destroy();
	}

	public boolean collide(Physical other) {
		return false;
	}

	public void draw() {
		if(Keyboard.checkDown('q')) {
			animation = ANI_OPEN;
			lidAng = LID_CLOSED;
			lidSpd = 5;
			rot = 0;
			rotSpd = 5;
		}
		if(Keyboard.checkDown('r')) {
			animation = ANI_CLOSE;
			lidAng = LID_OPEN;
			lidSpd = -5;
			rot = 0;
			rotSpd = -5;
		}
		
		
		if(animation == ANI_OPEN) {
			lidSpd += gravity;
			lidAng += lidSpd;//Math2D.calcSmoothTurn(lidAng, LID_OPEN, 5);
			if(lidAng >= LID_OPEN) {
				lidAng = LID_OPEN;
				lidSpd *= -bounceFrac;
			}
		}
		else if(animation == ANI_CLOSE) {
			lidSpd -= gravity;
			lidAng += lidSpd;
			if(lidAng <= LID_CLOSED) {
				lidAng = LID_CLOSED;
				lidSpd *= -bounceFrac;
				if(hopZ == 0)
					hopZSpd = Math.abs(lidSpd*.2f);
			}
		}
		
		hopZSpd -= gravity;
		hopZ += hopZSpd;
		if(hopZ <= 0) {
			hopZ = 0;
			hopZSpd *= -bounceFrac;
		}
		
		float prevRot = rot;
		rotSpd -= Math.signum(rot)*gravity;
		rot += rotSpd;		
		if((prevRot < 0 && rot >= 0) || (rot <= 0 && prevRot > 0))
			rotSpd *= .3f;
		
		GOGL.transformClear();
		transformTranslation();
		
		float rotSign = Math.signum(rot);
		GOGL.transformTranslation(0,-rotSign*l,0);
		GOGL.transformRotationX(rot);
		GOGL.transformTranslation(0,rotSign*l,0);

		GOGL.transformTranslation(0,0, hopZ);
		
		GOGL.draw3DBlock(-w,-l,2*h,w,l,0);
		
		GOGL.transformTranslation(0,0,2*h);
		GOGL.transformTranslation(0,-l,0);
		GOGL.transformRotationX(lidAng);
		GOGL.transformTranslation(0,l,0);
		GOGL.draw3DBlock(-w,-l,h,w,l,0);
		
		GOGL.transformClear();
	}

	public void use(Actor user) {
		cont.giveAllTo(user);
	}
}
