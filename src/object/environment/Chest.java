package object.environment;

import cont.Text;
import interfaces.Useable;
import io.Keyboard;
import io.Mouse;
import functions.Math2D;
import gfx.GOGL;
import gfx.ScrollDrawer;
import obj.itm.Item;
import obj.itm.ItemContainer;
import object.actor.Actor;
import object.actor.Player;
import object.primitive.Environmental;
import object.primitive.Physical;
import time.Delta;

public class Chest extends Environmental implements Useable {
	private ItemContainer cont;
	private float hopZ, hopZSpd, rot, rotSpd;
	private float w, l, h, gravity = .3f, lidAng, lidSpd, bounceFrac = .3f; //.5f
	private final static float LID_CLOSED = 0, LID_OPEN = -135+360;
	private final static byte ANI_OPEN = 0, ANI_CLOSE = 1, ANI_NONE = -1;
	private byte animation = -1;
	private boolean isOpen = false;
	
	public Chest(float x, float y, String... items) {
		super(x,y, true, false);
		
		shouldAdd = true;
		
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

	public void open() {
		if(isOpen || animation != ANI_NONE)
			return;
		animation = ANI_OPEN;
		lidAng = LID_CLOSED;
		lidSpd = 5;
		rot = 0;
		rotSpd = 5;
		isOpen = true;
	}
	
	public void close() {
		if(!isOpen || animation != ANI_NONE)
			return;
		animation = ANI_CLOSE;
		lidAng = LID_OPEN;
		lidSpd = -5;
		rot = 0;
		rotSpd = -5;
		isOpen = false;
	}
	
	public void update() {
		if(animation == ANI_OPEN) {
			lidSpd += Delta.convert(gravity);
			lidAng += Delta.convert(lidSpd);//Math2D.calcSmoothTurn(lidAng, LID_OPEN, 5);
			if(lidAng >= LID_OPEN) {
				if(Math.abs(lidSpd) < .3) {
					lidAng = LID_OPEN;
					lidSpd = 0;
					animation = ANI_NONE;
				}
				
				lidAng = LID_OPEN;
				lidSpd *= -bounceFrac;
				
			}
		}
		else if(animation == ANI_CLOSE) {
			lidSpd -= Delta.convert(gravity);
			lidAng += Delta.convert(lidSpd);
			if(lidAng <= LID_CLOSED) {
				if(Math.abs(lidSpd) < .3) {
					lidAng = LID_CLOSED;
					lidSpd = 0;
					animation = ANI_NONE;
				}
				
				lidAng = LID_CLOSED;
				lidSpd *= -bounceFrac;
				if(hopZ == 0)
					hopZSpd = Math.abs(lidSpd*.2f);
			}
		}
		
		hopZSpd -= Delta.convert(gravity);
		hopZ += Delta.convert(hopZSpd);
		if(hopZ <= 0) {
			hopZ = 0;
			hopZSpd *= -bounceFrac;
		}
		
		float prevRot = rot;
		rotSpd -= Delta.convert(Math.signum(rot)*gravity);
		rot += Delta.convert(rotSpd);
		if((prevRot < 0 && rot >= 0) || (rot <= 0 && prevRot > 0))
			rotSpd *= .3f;
	}
	
	public void draw() {
		/*GOGL.enableLighting();
		GOGL.setLightColor(COL_TRUNK);
		
		GOGL.transformClear();
		transformTranslation();
				
		float rotSign = Math.signum(rot);
		GOGL.transformTranslation(0,-rotSign*l,0);
		GOGL.transformRotationX(rot);
		GOGL.transformTranslation(0,rotSign*l,0);

		GOGL.transformTranslation(0,0, hopZ);
		
		GOGL.draw3DBlock(-w,-l,2*h,w,l,0);
		
		GOGL.transformTranslation(0,-l,2*h);
		GOGL.transformRotationX(lidAng);
		GOGL.transformTranslation(0,l,0);
		GOGL.draw3DBlock(-w,-l,h,w,l,0);
		
		GOGL.transformClear();
		
		GOGL.disableLighting();
		
		GOGL.resetColor();*/
	}
	public void add() {
		GOGL.setLightColor(COL_TRUNK);
		
		GOGL.transformClear();
		transformTranslation();
				
		float rotSign = Math.signum(rot);
		GOGL.transformTranslation(0,-rotSign*l,0);
		GOGL.transformRotationX(rot);
		GOGL.transformTranslation(0,rotSign*l,0);

		GOGL.transformTranslation(0,0, hopZ);
		
		GOGL.add3DBlock(-w,-l,2*h,w,l,0);
		
		GOGL.transformTranslation(0,-l,2*h);
		GOGL.transformRotationX(lidAng);
		GOGL.transformTranslation(0,l,0);
		GOGL.add3DBlock(-w,-l,h,w,l,0);
		
		GOGL.transformClear();
	}

	public void use(Actor user) {
		if(isOpen)
			return;
		Text.chest(cont.get(0).getName());
		cont.giveAllTo(user);
		open();
	}
	
	public void hover() {
		if(Mouse.getLeftClick()) {
			Player p = Player.getInstance();
			p.taskClear();
			p.taskMoveTo(x(),y());
			p.taskUse(this);
		}
	}
}
