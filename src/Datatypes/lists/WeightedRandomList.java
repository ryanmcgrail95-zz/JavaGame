package datatypes.lists;

import java.util.ArrayList;
import java.util.List;

import functions.MathExt;

public class WeightedRandomList<K> extends CleanList<K> {
	
	public WeightedRandomList(String name) {
		super(name);
	}

	private List<Float> weightList = new ArrayList<Float>();
	private float totalWeight;
	
	public K get() {
		float rnd = MathExt.rnd(totalWeight);
		
		K curK;
		float curWeight;
		int i = 0;
		while(i < size()) {
			curK = get(i);
			curWeight = weightList.get(i); 
			rnd -= curWeight;
			
			if(rnd <= 0)
				return curK;
			else
				i++;
		}
		
		return null;
	}
	
	public void clear() {
		super.clear();
		weightList.clear();
	}
	
	public void add(K obj, float weight) {
		add(obj);
		weightList.add(weight);
		totalWeight += weight;
	}
}
