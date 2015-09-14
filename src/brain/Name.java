package brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datatypes.lists.RandomList;
import functions.MathExt;

public class Name {
	private static RandomList<String> firstMale = new RandomList<String>();
	private static RandomList<String> firstFemale = new RandomList<String>();
	private static RandomList<String> first = new RandomList<String>();
	private static RandomList<String> last = new RandomList<String>();
	private static Map<String, Name> list = new HashMap<String, Name>();
	private String word;
	
	public final static byte G_ANDRO = 0, G_MALE = 1, G_FEMALE = 2, G_EITHER = 3;
	
	private Name(String word) {
		this.word = word;
	}
	
	public static void ini() {
		first.add("Adrian");
		first.add("Alex");
		first.add("Blake");
		first.add("Charlie");
		first.add("Drew");
		first.add("Jordan");
		first.add("Mickey");
		first.add("Morgan");
		first.add("Pat");
		first.add("Ryan");
		first.add("Sam");
		first.add("Taylor");
		first.add("Tyler");
		
		firstMale.add("Arnold");
		firstMale.add("Colin");
		firstMale.add("Harold");
		firstMale.add("Harry");
		firstMale.add("John");
		firstMale.add("Ronald");
		firstMale.add("Steve");
		firstMale.add("Steven");

		firstFemale.add("Amy");
		firstFemale.add("Brenna");
		firstFemale.add("Catherine");
		firstFemale.add("Colleen");
		firstFemale.add("Katelyn");
		firstFemale.add("Mary");
		firstFemale.add("Michelle");
		firstFemale.add("Molly");
		firstFemale.add("Sally");
		firstFemale.add("Sarah");
		firstFemale.add("Wendy");

		
		last.add("Adams");
		last.add("Boyd");
		last.add("Brown");
		last.add("Davis");
		last.add("Einstein");
		last.add("Fuegner");
		last.add("Jackson");
		last.add("Johnson");
		last.add("King");
		last.add("Kohlbeck");
		last.add("Martin");
		last.add("McGrail");
		last.add("Moore");
		last.add("Nelson");
		last.add("O'Brien");
		last.add("Smith");
		last.add("Stevens");
		last.add("Stevenson");
		last.add("Taylor");
		last.add("Thomas");
		last.add("White");
		last.add("Williams");
		last.add("Young");
	}
	
	public static void add(String name) {list.put(name, new Name(name));}
	public static Name get(String name) {return list.get(name);}

	public static String generateHumanName(byte type) {
		float si;
		if(type == G_EITHER) {
			si = MathExt.rndSign();
			if(si == 1)
				type = G_MALE;
			else
				type = G_FEMALE;
		}
		
		si = MathExt.rndSign();
		String f;
		if(type == G_MALE) {
			if(si == 1)
				f = first.get();
			else
				f = firstMale.get();
		}
		else if(type == G_FEMALE) {
			if(si == 1)
				f = first.get();
			else
				f = firstFemale.get();
		}
		else
			f = first.get();
		
		return f + " " + last.get();
	}
}
