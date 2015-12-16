package datatypes.lists;

import java.util.ArrayList;

import functions.MathExt;

public class RandomList<K> extends CleanList<K> {
	public RandomList(String name) {
		super(name);
	}

	public K get() {
		int si = size(), i;
		do
			i = (int) Math.floor(MathExt.rnd(si));
		while(i == si);
		
		return get(i);
	}
}
