package obj.env;

import java.util.ArrayList;
import java.util.List;

import obj.chr.Actor;
import obj.chr.Player;
import obj.itm.Item;
import obj.prim.Physical;
import sts.Stat;
import func.Math2D;
import gfx.GOGL;
import gfx.TextureExt;
import ds.LinearGrid;
import ds.vec3;

import com.jogamp.opengl.util.texture.Texture;

import cont.IO;
import cont.TextureController;

public class Tree extends Physical {
	private List<Branch> branches = new ArrayList<Branch>();
	float zScale;
	private int cutExp = 25;
	
	private class Branch {
		float bX,bY,bZ, h, r;
		
		public Branch(float x, float y, float z, float h, float r) {
			bX = x;
			bY = y;
			bZ = z;
			this.h = h;
			this.r = r;
		}
		
		public void draw() {
			float d, n = 5, cR, cY, pY=0,pR=0;
			int p = 7;
			
			GOGL.transformClear();
			GOGL.transformTranslation(x+bX,y+bY,z);
			GOGL.transformScale(1,1,zScale);
			GOGL.transformTranslation(0,0,bZ);
				for(int i = 0; i <= n; i++) {
					d = 90-i/n*110;
					cR = r*Math2D.calcLenX(d);
					cY = h*Math2D.calcLenY(d);
					
					if(i != 0)
						GOGL.draw3DFrustem(0,0,cY,cR,pR,Math.abs(cY-pY),p,false);
					
					pR = cR;
					pY = cY;
				}
			GOGL.transformClear();
		}
	}
	
	public Tree(float x, float y, float scale) {
		super(x,y,0);
		z = Heightmap.getInstance().getZ(x,y);
		zScale = Math2D.rnd(.9f,2f);
		
		doUpdates = false;

		branches.add(new Branch(0,0,56, 24,36));
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
