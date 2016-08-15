package ds.lst;

import java.util.ArrayList;

import functions.MathExt;

public class RandomList<K> extends CleanList<K> {
	public RandomList(String name) {
		super(name);
	}

	public K get() {
		return get(MathExt.rndi(0, size()-1));
	}
}
