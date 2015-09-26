package object.primitive;

import gfx.RGBA;

import java.util.ArrayList;
import java.util.List;

import datatypes.lists.CleanList;
import object.environment.BlockTerrain;
import object.environment.Heightmap;

public abstract class Environmental extends Positionable {
	private static CleanList<Environmental> envList = new CleanList<Environmental>();
	public static RGBA COL_SELECTED = RGBA.WHITE, 
			COL_TRUNK = new RGBA(72,75,49), 
			COL_LEAVES = new RGBA(44,103,116);
	
	public Environmental(float x, float y, boolean hoverable, boolean renderable) {
		super(x,y,BlockTerrain.getInstance().getSurfaceZ(x,y),hoverable, renderable);
		
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
		BlockTerrain.getInstance().collide(other);
		//Heightmap.getInstance().collide(other);
		for(int i = 0; i < envList.size(); i++) {
			e = envList.get(i);
			didCol = didCol || e.collide(other);
		}
		
		return didCol;
	}
	
	public RGBA pickColor(RGBA color) {
		return isSelected ? COL_SELECTED : color;
	}
}
