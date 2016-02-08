package obj.prim;

import java.util.ArrayList;
import java.util.List;

import ds.SortedList;

public abstract class Environmental extends Drawable {
	private static SortedList<Environmental> envList = new SortedList<Environmental>();
	
	
	public Environmental() {
		super();
		
		envList.add(this);
	}
	

	public void destroy() {
		super.destroy();
		envList.remove(this);
	}
	
	public static void clean() {
		envList.clean();
	}
	
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
}
