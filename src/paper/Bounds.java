package paper;

import java.util.ArrayList;

import ds.lst.CleanList;
import object.primitive.Positionable;

public class Bounds {
	private static CleanList<Bounds> boundsList = new CleanList<Bounds>("Bounds");
	private Positionable parent;
	private float x, y, z;
	private boolean isLocked, isSolid;
	private ArrayList<BoundsChild> children = new ArrayList<BoundsChild>();
	
	
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	private void add(BoundsChild b) {
		children.add(b);
	}
	
	public void addCylinder(float subX, float subY, float subZ, float r, float h) {
		add(new BoundsCylinder(subX, subY, subZ, r, h));
	}
	
	public void destroy() {
		boundsList.remove(this);
		
		//for(BoundsChild b : children)
		//	b.destroy();

		children.clear();
	}	
	
	public void collideAll() {
		if(isLocked)
			return;
		
		/*for(Bounds b : boundsList) {
			collideWith(b);
		}*/
	}
	
	private enum childTypes{ Cylinder, Line, Box };
	private class BoundsChild {		
	}
	
	private class BoundsCylinder extends BoundsChild {

		public BoundsCylinder(float subX, float subY, float subZ, float r, float h) {
			
		}
		
		public void collide(BoundsChild b) {
			if(b instanceof BoundsCylinder) {
				
			}
		}
	}
	
	/*public boolean checkCircle(float x, float y, float r, boolean eject) {
		float dis, dir;
		dis = calcPtDis(x(),y());
		
		if(!eject)
			return dis < r;
		else if(dis < r) {
			dir = calcPtDir(x,y)+180;
			step(r-dis,dir);
			return true;
		}
		else
			return false;
	}*/
}
