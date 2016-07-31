package ds.nameid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameIDMap<T> {
	private Map<String, Integer>	name2IDNumMap = new HashMap<String, Integer>();
	private List<T> 				tList = new ArrayList<T>();
	private List<ID<T>> 			idList = new ArrayList<ID<T>>();
	
	public int refactorValue;

	
	public ID<T> add(T obj) {
		int idNum = idList.size();
		ID<T> id = new ID<T>(this, idNum, "(NO STRING)");
		
		idList.add(id);
		tList.add(obj);
		
		return id;		
	}
	public ID<T> add(String name, T obj) {
		int idNum = idList.size();
		ID<T> id = new ID<T>(this, idNum, name);
		
		idList.add(id);
		tList.add(obj);
		name2IDNumMap.put(name, idNum);
		
		return id;
	}
	
	public T get(String name) 	{return get(find(name));}	
	public T get(ID<T> id) 		{return get(id.getIDNum());}
	public T get(int idNum) 	{return tList.get(idNum);}

	public ID<T> getID(int idNum) 	{return idList.get(idNum);}

	
	public void clear() {
		for(ID<T> i : idList)
			i.destroy();
		
		idList.clear();
		tList.clear();
		name2IDNumMap.clear();
	}
	
	public void destroy() {
		clear();
	}

	

	public T remove(T obj) 		{return remove(find(obj));}
	public T remove(ID<T> id) 	{return remove(id.getIDNum());}
	public T remove(int idNum) 	{
		if(idNum < 0 || idNum >= size())
			return null;
		
		T removed = tList.remove(idNum);		
		ID<T> id = idList.remove(idNum);
		
		name2IDNumMap.remove(id.getName());
		id.destroy();
		
		refactor();
		
		return removed;
	}



	public void refactor() {
		int si = idList.size();
		ID<T> i;
		
		for(int k = 0; k < si; k++) {
			i = idList.get(k);
					
			name2IDNumMap.put(i.getName(), k);
			
			refactorValue = k;
			i.refactor();
		}
	}
	
	public ID<T> findID(T obj) 		{return idList.get(find(obj));}
	public ID<T> findID(ID<T> id) 	{return idList.get(find(id));}
	public ID<T> findID(String name){return idList.get(find(name));}
	
	public int find(T obj) 		{return tList.indexOf(obj);}
	public int find(ID<T> id) 	{return idList.indexOf(id);}
	public int find(String name){
		Integer idNum = name2IDNumMap.get(name);
		
		if(idNum == null)
			return -1;
		else
			return idNum.intValue();
	}
	
	public int size() {
		return idList.size();
	}
	public void println() {
		for(ID<T> i : idList)
			System.out.println(i);
	}
	public boolean contains(String str) {
		return find(str) != -1;
	}
}
