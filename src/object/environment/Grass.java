package object.environment;

import java.util.ArrayList;
import java.util.List;

import object.actor.Player;
import object.primitive.Physical;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.TextureExt;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import ds.LinearGrid;
import ds.vec3;

public class Grass extends Physical {
	private List<Grasslet> strands = new ArrayList<Grasslet>();
	
	private class Grasslet {
		private float bendAmt;
		float x, y, z, len;
		float rot, dir;
		
		public Grasslet(float x, float y, float z, float len) {
			bendAmt = 0;
			this.x = x;
			this.y = y;
			this.z = z;
			this.len = len;
			dir = MathExt.rnd()*360;
			rot = MathExt.rnd(-1, 1)*30;
		}
		
		public void draw() {
			
			float pX,pY;
			pX = Player.getInstance().getX();
			pY = Player.getInstance().getY();
			
			
			GOGL.transformClear();
			GOGL.transformTranslation(x,y,z-2);
			
			GOGL.transformRotationZ(dir*(1-bendAmt));
			GOGL.transformRotationX(rot*(1-bendAmt));
			
			int toVal = 0;
			if(Math2D.calcPtDis(x,y,pX,pY) < 16)
				toVal = 1;
			bendAmt += (toVal - bendAmt)/10.;
			
			if(bendAmt > 0) {
				float faceDir = 0;
				faceDir = Math2D.calcPtDir(pX,pY,x,y)+90;
				GOGL.transformRotationZ(faceDir);
				GOGL.transformRotationX(20*bendAmt);
				GOGL.transformRotationZ(-faceDir);
			}
			
				GOGL.draw3DFrustem(1,0,len,3);
			GOGL.transformClear();
		}
	}
	
	public Grass(float x, float y, float lenM, float disM, int dirN, int disN, float shrF) {
		super(x,y,0);
		
		size = disM;
		doUpdates = false;
				
		float dis, dir, sX, sY, sZ, sL;
		for(int d = 0; d < dirN; d++) {
			dir = 1f*d/dirN*360;
			for(int i = 1; i <= disN; i++) {
				dis = 1f*i/disN*disM;
				
				sX = x + Math2D.calcLenX(dis,dir) + MathExt.rnd(-1,1)*disM/disN;
				sY = y + Math2D.calcLenY(dis,dir) + MathExt.rnd(-1,1)*disM/disN;
				sZ = Heightmap2.getInstance().getZ(sX,sY);
				
				sL = shrF*(disM + disM/disN - dis)/disM + (1-shrF);
				sL *= lenM*(.8f+MathExt.rnd(.4f));
				
				strands.add(new Grasslet(sX,sY,sZ,sL));
			}
		}
	}
	
	public boolean collide(Physical other) {
		
		return false;
	}

	public void land() {
	}
	
	public boolean draw() {
		
		vec3 norm;
		
		GOGL.enableLighting();
		GOGL.setLightColori(66, 75, 18);
		
		for(int i = 0; i < strands.size(); i++)
			strands.get(i).draw();
		
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
		return false;
	}
}
