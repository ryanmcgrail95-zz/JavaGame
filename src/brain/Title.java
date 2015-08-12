package brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Title extends Descriptor {
	private static Map<String, Title> list = new HashMap<String, Title>();
	
	private Title(String word) {
		super(word);
	}
		
	public static void add(String name, String... related) {
		list.put(name, new Title(name));
	}
	public static Title get(String name) {
		return list.get(name);
	}
}
