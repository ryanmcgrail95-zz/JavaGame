package object.primitive;

import java.util.ArrayList;
import java.util.List;

import datatypes.lists.CleanList;
import object.environment.Heightmap;

public abstract class Environmental extends Positionable {
	private static CleanList<Environmental> envList = new CleanList<Environmental>();
	
	public Environmental(float x, float y, boolean hoverable, boolean renderable) {
		super(x,y,Heightmap.getInstance().getZ(x,y),hoverable, renderable);
		
		envList.add(this);
	}
	

	public void destroy() {
		super.destroy();
		envList.remove(this);
	}
	
	public abstract boolean collide(Physical other);
	
	public void update() {}
	public static boolean collideAll(Physical other) {
		boolean didCol = false;

		Environmental e;
		Heightmap.getInstance().collide(other);
		for(int i = 0; i < envList.size(); i++) {
			e = envList.get(i);
			didCol = didCol || e.collide(other);
		}
		
		return didCol;
	}	
}
