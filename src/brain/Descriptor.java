package brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Descriptor {
	private static Map<String, Descriptor> masterList = new HashMap<String, Descriptor>();

	private List<Descriptor> related = new ArrayList<Descriptor>();
	private List<Descriptor> unrelated = new ArrayList<Descriptor>();

	
	private String word;


	protected Descriptor(String word) {
		this.word = word;
		masterList.put(word, this);
	}
	
	private void link(Descriptor other) {
		related.add(other);
		other.related.add(this);
	}
	private void unlink(Descriptor other) {
		unrelated.add(other);
		other.unrelated.add(this);
	}
	
	public static void link(String word1, String word2) {
		masterList.get(word1).link(masterList.get(word2));
	}
	public static void linkToFirst(String word, String... words) {
		for(String d : words)
			masterList.get(word).link(masterList.get(d));
	}
	
	public static void unlink(String word1, String word2) {
		masterList.get(word1).unlink(masterList.get(word2));
	}
	
	public void println() {
		System.out.println(word);
	}

	public boolean checkLinked(Descriptor other) {
		if(other == this)
			return true;
		return related.contains(other);
	}
	
	
	public static Descriptor get(String word) {
		return masterList.get(word);
	}

	public void print() {
		System.out.print(word);
	}

	public List<Descriptor> getRelated() {
		return related;
	}
}
