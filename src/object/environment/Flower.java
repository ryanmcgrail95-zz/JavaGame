package object.environment;

import io.IO;

import java.util.ArrayList;
import java.util.List;

import obj.itm.Item;
import object.actor.Actor;
import object.actor.Player;
import object.primitive.Physical;
import sts.Stat;
import functions.Math2D;
import gfx.GOGL;
import gfx.TextureExt;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import datatypes.LinearGrid;
import datatypes.vec3;

public class Flower extends Physical {
	float zScale;
	
	public Flower(float x, float y, float scale) {
		super(x,y,0);
		z = Heightmap.getInstance().getZ(x,y);
		zScale = Math2D.rnd(.9f,2f);
		
		doUpdates = false;
	}
	
	public boolean collide(Physical other) {		
		return false;
	}

	public void land() {
	}
	
	public void hover() {
		IO.setCursor(IO.C_POINTING_FINGER);
		
		GOGL.setOrtho();
		GOGL.drawStringS(0,0,"Chop down Tree");
		GOGL.setPerspective();
		
		if(IO.getLeftClick()) {
			chopDown(Player.getInstance());
		}
	}
	
	public boolean draw() {		
		vec3 norm;
		
		GOGL.enableLighting();
		
		GOGL.transformClear();
		GOGL.transformTranslation(x,y,z);
		GOGL.transformScale(1,1,zScale);		
		GOGL.setLightColori(56, 40, 30);

		//GOGL.draw3DFrustem(10,10,-3, 6,0,8,3,false);

		GOGL.draw3DFrustem(0,0,-3, 15,12,3,3,false);
		GOGL.draw3DFrustem(0,0,0, 12,10, 7,3,false);
		GOGL.draw3DFrustem(0,0,7, 10,8, 10,3,false);
		GOGL.draw3DFrustem(0,0,17, 8,7, 30,3,false);

		GOGL.draw3DFrustem(-6,-10,-3, 7,0,11,3,false);
		GOGL.draw3DFrustem(13,0,-3, 7,0,11,3,false);
		GOGL.draw3DFrustem(-6,13,-3, 7,0,11,3,false);
		


		GOGL.setLightColori(96, 104, 70);
		for(int i = 0; i < branches.size(); i++)
			branches.get(i).draw();
		
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
		return false;
	}

	private void chopDown(Actor chopper) {
		chopper.getStat().addExp(Stat.EXP_WOODCUTTING, getCutExp());
		Item.create("Logs", x,y,z);
		destroy();
	}
	
	public int getCutExp() {
		return cutExp;
	}
}
