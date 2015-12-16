package datatypes.lists;

import java.util.ArrayList;
import java.util.List;

public class EclecticList<T> extends CleanList<T> {
	public EclecticList(String name) {
		super(name);
	}

	public void add(List<T> otherList) {
		for(T obj : otherList)
			add(obj);
	}
	
	public void add(T... others) {
		for(T obj : others)
			add(obj);
	}
}
