package Datatypes;

import java.util.Vector;

public class SortedList<T> {
	
	private Vector<T> array, removeArray;
	private Vector<Float> vals;
	private Vector<String> strs;
	public static final byte M_NUMBER = 0, M_STRING = 1;
	private byte mode = M_NUMBER;
	
	public SortedList() {
		array = new Vector<T>();
		removeArray = new Vector<T>();
		vals = new Vector<Float>();
		strs = new Vector<String>();
	}
	
	
	public void clear() {
		for(T obj : array)
			remove(obj);
	}

	public void remove(T obj) {
		removeArray.add(obj);
	}
	
	private void forceRemove(T obj) {
		int si = size();
		T real;
		
		for(int i = 0; i < si; i++) {
			real = get(i);
			
			if(real == obj) {
				forceRemove(i);
				return;
			}
		}
	}
	
	private void forceRemove(int index) {
		array.remove(index);
		vals.remove(index);
		strs.remove(index);
	}
	
	public void clean() {
		
		for(T obj : removeArray)
			forceRemove(obj);
		
		removeArray.clear();
	}
	
	
	public T add(T obj) {
		return addBack(obj, 0, "");
	}
	
	public T add(T obj, float val, String str) {
		return addBack(obj, val, "");
	}
	
	public T add(int pos, T obj, float val, String str) {
		array.add(pos,obj);
		vals.add(pos,val);
		strs.add(pos,str);
		return obj;
	}
	
	public T addFront(T obj, float val, String str) {
		return add(0, obj, val, str);
	}
	
	public T addBack(T obj, float val, String str) {
		return add(size(), obj, val, str);
	}

	
	public T set(int index, T obj) {
		return array.set(index, obj);
	}
	public T get(int index) {
		if(index < 0 || index >= size())
			return null;
		return array.get(index);
	}	
	
	public float setValue(int index, float value) {
		return vals.set(index, value);
	}
	public float getValue(int index) {
		return vals.get(index);
	}
	
	public String setString(int index, String value) {
		return strs.set(index, value);
	}
	public String getString(int index) {
		return strs.get(index);
	}
	
	public int size() {
		return array.size();
	}
	
	public void swap(int i1, int i2) {
		int si = size();
		if(i1 < 0 || i1 >= si || i2 < 0 || i2 >= si)
			return;
		
		T o1 = get(i1), o2 = get(i2);
		float v1 = getValue(i1), v2 = getValue(i2);
		String str1 = getString(i1), str2 = getString(i2);
		
		set(i1,o2);
		set(i2,o1);
		
		setValue(i1,v2);
		setValue(i2,v1);
		
		setString(i1,str2);
		setString(i2,str1);
	}
	
	public void setMethod(byte method) {
		mode = method;
	}
	public boolean sort(byte method, boolean forward) {
		setMethod(method);
		return sort(forward);
	}
	
	public boolean sort(boolean forward) {
		// Returns true if already sorted, false if not already sorted.
		
		boolean didSort, shouldSwap;
		int times = 0, si = size();
		float lV, rV;
		String lS, rS;
		
		do {
			didSort = false;
			
			for(int i = 1; i < si; i++) {
				
				lV = getValue(i-1);		rV = getValue(i);
				lS = getString(i-1);	rS = getString(i);
				
				if(forward)
					shouldSwap = (mode == M_NUMBER && lV > rV) || (mode == M_STRING && lS.compareToIgnoreCase(rS) > 0);
				else
					shouldSwap = (mode == M_NUMBER && lV < rV) || (mode == M_STRING && lS.compareToIgnoreCase(rS) < 0);
					
				if(shouldSwap) {
					swap(i-1,i);
					didSort = true;
				}
			}
			
			times++;
		} while(didSort);
		
		return (times == 1);
	}
	
	public boolean sort() {
		return sort(true);
	}
	
	public boolean sortReverse() {
		return sort(false);
	}
}
