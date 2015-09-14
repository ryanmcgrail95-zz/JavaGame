package datatypes;

import datatypes.lists.CleanList;


// Quad Search Map

public class QuadSearchMap<T> {
	private CleanList<QuadSearchMap<T>> subMaps;
	private CleanList<T> list;
	
	
	public QuadSearchMap() {
		subMaps = new CleanList<QuadSearchMap<T>>();
		list = new CleanList<T>();
	}
	
	
	public boolean split() {
		if(!subMaps.isEmpty())
			return false;

		for(int i = 0; i < 4; i++)
			
		subMaps
		
		list.clear();
		return true;
	}
}
