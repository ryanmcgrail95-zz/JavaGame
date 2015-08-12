package object.environment;

import interfaces.Useable;
import io.IO;
import io.Mouse;

import java.util.ArrayList;
import java.util.List;

import obj.itm.Item;
import object.actor.Actor;
import object.actor.Player;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.model.Model;
import sts.Stat;
import time.Timer;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;
import gfx.TextureExt;

import com.jogamp.opengl.util.texture.Texture;

import cont.Messages;
import cont.TextureController;
import datatypes.LinearGrid;
import datatypes.vec3;

public abstract class Tree extends ResourceMine {
	float zScale;
	private int cutExp = 25;
	
	
		
	public Tree(float x, float y) {
		super(x,y);
		
		zScale = MathExt.rnd(.9f,2f);
		
		disableUpdates();
	}
	
	public boolean collide(Physical other) {		
		return other.collideCircle(x(),y(),20);
	}
	
	public void hover() {
		Mouse.setFingerCursor();
		
		Messages.setActionMessage("Chop down Tree");
		
		if(Mouse.getLeftClick()) {
			Player p = Player.getInstance();
			p.taskClear();
			p.taskMoveNear(this,25);
			p.taskResourceMine(this);
		}
	}
	
	public void giveItem(Actor user) {
		user.getStat().addExp(Stat.EXP_WOODCUTTING, getExp());
		user.give("Logs",1);
	}
	
	public int getExp() {
		return cutExp;
	}
}
