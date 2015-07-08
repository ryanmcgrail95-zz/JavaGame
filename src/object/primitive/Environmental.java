package object.primitive;

import java.util.ArrayList;
import java.util.List;

import object.environment.Heightmap;
import Datatypes.SortedList;

public abstract class Environmental extends Drawable {
	private static SortedList<Environmental> envList = new SortedList<Environmental>();
	private float x, y, z;
	
	public Environmental(float x, float y, boolean hoverable) {
		super(hoverable);
		
		this.x = x;
		this.y = y;
		this.z = Heightmap.getInstance().getZ(x,y);
		
		envList.add(this);
	}
	

	public void destroy() {
		super.destroy();
		envList.remove(this);
	}
	public static void clean() {envList.clean();}
	
	public abstract boolean collide(Physical other);
	
	public static boolean collideAll(Physical other) {
		boolean didCol = false;

		Environmental e;
		for(int i = 0; i < envList.size(); i++) {
			e = envList.get(i);
			didCol = didCol || e.collide(other);
		}
		
		return didCol;
	}
	
	public float getX() {return x;}
	public float getY() {return y;}
	public float getZ() {return z;}
}
