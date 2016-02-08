package brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ds.lst.RandomList;
import functions.MathExt;

public class Adjective extends Descriptor {
	private static Map<String, Adjective> list = new HashMap<String, Adjective>();
	
	private Adjective(String word) {
		super(word);
	}
	
	public static void add(String name) {list.put(name, new Adjective(name));}
	public static Adjective get(String name) {return list.get(name);}
}
