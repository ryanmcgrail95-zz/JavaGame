package object.environment;

import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.GOGL;
import resource.model.Model;

public class OakTree extends Tree {
	
	private Branch[] branches;

	public OakTree(float x, float y) {
		super(x,y);
		
		int n = 8;
		float dis,dir,dirZ;
		float[] pos;
		branches = new Branch[n];
		for(int i = 0; i < n; i++) {
			dis = 30;
			dir = MathExt.rnd(360);
			dirZ = MathExt.rnd(-5,90);
			
			pos = Math3D.calcPolarCoords(dis, dir, dirZ).getArray();
			
			branches[i] = new Branch(x()+pos[0],y()+pos[1],z()+zScale*70+pos[2],dir,dirZ);
		}
	}
	
	public class Branch {
		float x,y,z, dir, dirZ;
		
		public Branch(float x, float y, float z, float dir, float dirZ) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.dir = dir;
			this.dirZ = dirZ;
		}
		
		public void draw() {
			GOGL.transformClear();
			GOGL.transformTranslation(x,y,z);
			GOGL.transformRotation(dir, dirZ);
			GOGL.transformRotationY(90);

			
			GOGL.draw3DFrustem(30,0,1,3,false);
			
			GOGL.transformClear();
		}
	}
	
	public void draw() {
		GOGL.enableLighting();
		
		GOGL.transformClear();
		transformTranslation();
		GOGL.transformScale(.5f);
		GOGL.transformScale(1,1,zScale);
		GOGL.setLightColori(56, 40, 30);

		Model.MOD_PINESTUMP.draw();
		
		if(!isEmpty()) {
			Model.MOD_PINETREE.draw();

			GOGL.setLightColori(96, 104, 70);
			GOGL.transformScale(1.5f);
			GOGL.transformTranslation(0,0,-10*zScale);
			
			
			for(Branch b : branches)
				b.draw();
		}
		
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
	}
}
