package ds.lst;

import interfaces.Destroyable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import functions.MathExt;


public class CleanList<T> extends ArrayList<T> implements Iterable<T> {
	private static CleanList<CleanList> completeList = new CleanList<CleanList>("CleanLists");
	
	private HashMap<Integer,Boolean> removeMap = new HashMap<Integer,Boolean>();
	private ArrayList<Integer> removeList = new ArrayList<Integer>();
	private boolean moveForward = true;
	private byte isIterating = 0, removeNum = 0;
	private String name;
	
	private ArrayList<ListIterator> iteratorList = new ArrayList<ListIterator>();
	
	public CleanList(String name) {
		super();
		
		this.name = name;
		if(completeList != null)
			completeList.add(this);
	}
	
	public void remove() {
		if(isIterating > 0)
			remove(iteratorList.get(isIterating-1).getCursor());
		else
			throw new UnsupportedOperationException();
	}
	public T remove(int position) {
		if(isIterating > 0) {
			if(!removeMap.containsKey(position)) {
				removeNum++;
				set(position,null);
				removeList.add(position);
				removeMap.put(position,true);
			}
			return null;
		}
		else
			return super.remove(position);
	}
	
	public void broke() {
		if(iteratorList.size() > 0)
			iteratorList.get(iteratorList.size()-1).end();
	}
	
	public int size() {
		return super.size()-removeNum;
	}
	
	private void clean() {
		if(isIterating > 0)
			return;
		
		T obj;
		int si = removeList.size();
		for(int i = 0; i < si; i++) {
			obj = removeForcibly(removeList.get(i)-i);
			
			if(obj instanceof Destroyable)
				((Destroyable) obj).destroy();
		}
		removeList.clear();
		removeNum = 0;
	}
	
	protected T removeForcibly(int position) {
		return super.remove(position);
	}
	
	
	public Iterator iterator() {
		return new ListIterator();
	}
	
	private class ListIterator implements Iterator<T> {
		private int cursor;
		private String name;

		public ListIterator() {
			begin();
			if(moveForward)
				cursor = 0;
			else
				cursor = CleanList.super.size()-1;
		}
		
		public String toString() {
			return name;
		}
		
		public void begin() {
			isIterating++;
			iteratorList.add(this);
		}
		
		
		public void end() {
			isIterating--;
			iteratorList.remove(this);
			
			clean();
		}
		
		public int getCursor() {
			return cursor-1;
		}
		
		public boolean hasNext() {
			
			boolean state = checkNotDone();
				
			if(state)
				while(get(cursor) == null) {
					increment();
					if(!checkNotDone()) {
						state = false;
						break;
					}
				}
				
			if(state == false)
				end();
			return state;
		}
		
		public void increment() {
			if(moveForward)	cursor++;
			else			cursor--;
		}
		
		public boolean checkNotDone() {
			if(moveForward)
				return cursor < CleanList.super.size();
			else
				return cursor > 0;
		}
		
		public T next() {
			if(hasNext()) {
				int currentPos = cursor;
				
				increment();
				
				return get(currentPos);
			}
			throw new NoSuchElementException();
		}
	}

	public void swap(int ind1, int ind2) {
		T it1, it2;
		it1 = get(ind1);
		it2 = get(ind2);
		set(ind1, it2);
		set(ind2, it1);
	}
	public static int getLoops() {
		int loopNum = 0;
		
		for(CleanList c : completeList) {
			loopNum += c.isIterating;
		
			for(Object i : c.iteratorList)
				System.out.println(i);
		}
			
		return loopNum;
	}

	public static int totalSize() {
		int totalSize = 0;
		for(CleanList c : completeList)
			totalSize += c.size() + c.removeList.size() + c.removeMap.size();
		return totalSize;
	}
	
	public static CleanList<CleanList> getLists() {
		return completeList;
	}
	public static int getNumber() {
		return completeList.size();
	}
	
	public T getRandom() {
		return get(MathExt.rndi(0, size()-1));
	}

	public String getName() {
		return name;
	}

	public void destroy() {
		clear();
		completeList.remove(this);
	}
}
