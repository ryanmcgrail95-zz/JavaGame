package brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Idea {
	// This can be a location, person, whatever.
	private static Map<String, Idea> list = new HashMap<String, Idea>();
	private List<Descriptor> descList = new ArrayList<Descriptor>();
	
	
	public Idea(String base) {
		add(base);
		add(Descriptor.get(base).getRelated());
	}
	public Idea(String... ws) {
		add(ws);
	}
	
	public static void ini() {
		Name.ini();
		
	
		//TITLE
			Title.add("man");
			Title.add("boy");
			
			Title.add("woman");
			Title.add("girl");
			
			Title.add("child");
			Title.add("adult");
			Title.add("infant");
			Title.add("baby");

			Title.add("plant");
			Title.add("tree");
		
		
		// ADJECTIVE
			Adjective.add("male");
			Adjective.add("female");

			Adjective.add("red");
			Adjective.add("blue");
			Adjective.add("green");
			Adjective.add("strong");
			
			
		// Tree
		create("tree");
		
		Descriptor.unlink("male", "female");
		Descriptor.linkToFirst("male", "man","boy");
		Descriptor.linkToFirst("female", "woman","girl");
		Descriptor.linkToFirst("adult", "man","woman");
		Descriptor.linkToFirst("child", "boy","girl");
	}
	
	
	
	private static void create(String... ws) {
		list.put(ws[0], new Idea(ws));
	}
	
	
	public void add(String... ws) {
		for(String w : ws)
			descList.add(Descriptor.get(w));
	}
	public void add(Descriptor... ds) {
		for(Descriptor d : ds)
			descList.add(d);
	}
	public void add(List<Descriptor> ds) {
		for(Descriptor d : ds)
			descList.add(d);
	}
	public void printInfo() {
		for(Descriptor d : descList)
			d.println();
	}
	
	public float compareTo(Idea other) {
		int numSimilar = 0, totNum = descList.size();
		
		for(Descriptor myD : descList)
			for(Descriptor otherD : other.descList) {
				myD.print(); otherD.print();
				System.out.println();
				
				if(myD.checkLinked(otherD)) {
					numSimilar++;
					break;
				}
			}
		
		return 1f*numSimilar/totNum;
	}
}
