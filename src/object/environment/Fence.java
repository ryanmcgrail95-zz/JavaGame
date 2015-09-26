package object.environment;

import functions.Math3D;
import gfx.GOGL;

import java.util.ArrayList;
import java.util.List;

import datatypes.vec3;
import object.primitive.Environmental;
import object.primitive.Physical;

public class Fence extends Environmental {

	private List<FencePost> posts = new ArrayList<FencePost>();
	
	
	private class FencePost {
		float x,y,z;
		vec3 pt;
		
		public FencePost(float x, float y) {
			this.x = x;
			this.y = y;
			z = Heightmap.getInstance().getZ(x,y);
			pt = new vec3(x,y,z);
		}
	}
	
	
	public boolean checkOnscreen() {
		return true;
	}
	public float calcDepth() {
		return 0;
	}
	
	
	public Fence() {
		super(0,0,false,false);
		disableUpdates();
	}

	public void add() {}
	public void draw() {
		FencePost prev = null;
		vec3 data;
		float sign = -1, dis, dir, dirZ;
		for(FencePost f : posts) {
			
			if(prev != null) {
				data = Math3D.calcPtData(prev.pt,f.pt);
				
				dis = data.x();
				dir = data.y();
				dirZ = data.z();

				for(int i = 0; i < 2; i++) {
					GOGL.transformClear();
					GOGL.transformTranslation(prev.x,prev.y,prev.z+(i+1)*15);
					GOGL.transformRotation(dir, dirZ);
					GOGL.transformRotationY(90);
					if(sign == 1)
						GOGL.draw3DFrustem(0,5,dis, 3);
					else
						GOGL.draw3DFrustem(5,0,dis, 3);
					GOGL.transformClear();
				}
			}
			
			GOGL.draw3DFrustem(f.x,f.y,f.z, 0,5,40, 3);
			prev = f;
			sign *= -1;
		}
	}
	
	public void add(float x, float y) {
		posts.add(new FencePost(x,y));
	}

	public boolean collide(Physical other) {
		FencePost prev = null;
		for(FencePost f : posts) {
			if(prev != null)
				other.collideSegment(prev.x,prev.y, f.x,f.y);
			prev = f;
		}
		
		return false;
	}
}
