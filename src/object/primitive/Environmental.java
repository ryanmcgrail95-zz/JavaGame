package object.primitive;

import gfx.RGBA;

import java.util.ArrayList;
import java.util.List;

import datatypes.lists.CleanList;
import object.environment.BlockTerrain;
import object.environment.Heightmap;
import object.environment.Heightmap2;

public abstract class Environmental extends Positionable {
	private static CleanList<Environmental> envList = new CleanList<Environmental>("Env");
	public static RGBA COL_SELECTED = RGBA.WHITE, 
			COL_TRUNK = RGBA.createi(72,75,49), 
			COL_LEAVES = RGBA.createi(44,103,116);
	
	public Environmental(float x, float y, boolean hoverable, boolean renderable) {
		super(x,y,0,hoverable, renderable);
		
		/*if(Heightmap.getInstance() != null)
			setZ(Heightmap.getInstance().getZ(x,y));*/
		
		name = "Environmental";
		
		envList.add(this);
	}
	

	public void destroy() {
		super.destroy();
		envList.remove(this);
	}
	
	public abstract boolean collide(Physical other);
	
	public void update() {
		super.update();
	}
	public static boolean collideAll(Physical other) {
		boolean didCol = false;

		Environmental e;
		//BlockTerrain.getInstance().collide(other);
		//if(Heightmap.getInstance() != null)
		//	Heightmap.getInstance().collide(other);
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
