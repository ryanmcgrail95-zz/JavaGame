package datatypes;

import datatypes.lists.RandomList;
import functions.MathExt;

public class StringExt {
	protected String str;
	private static RandomList<Character> rndChar = new RandomList<Character>(); {
		for(int i = 0; i < 26; i++) {
			rndChar.add((char) ('a'+i));
			rndChar.add((char) ('A'+i));
		}
		for(int i = 0; i < 10; i++)
			rndChar.add((char) ('0'+i));
	}
	
	
	// CONSTRUCTORS
	
	public StringExt() 				{set("");}
	public StringExt(String str) 	{set(str);}
	public StringExt(StringExt str) {set(str.get());}
	
	
	// STRING MANIPULATION
	
	public char charAt(int i) {
		if(i >= length())
			return ' ';//throw new IndexOutOfBoundsException();
		return str.charAt(i);
	}
	public int length() {
		return str.length();
	}
	
	public void set(String str) {this.str = str;}
	public String get() 		{return str;}
	
	
	// EFFECTS
	
	public String capitalize() {
	    String[] words = str.split(" ");
	    StringBuilder ret = new StringBuilder();
	    for(int i = 0; i < words.length; i++) {
	        ret.append(Character.toUpperCase(words[i].charAt(0)));
	        ret.append(words[i].substring(1));
	        if(i < words.length - 1) {
	            ret.append(' ');
	        }
	    }
	    str = ret.toString();
	    return str;
	}

	// CHOMPING FUNCTIONS
	// These functions will "chomp" (remove and return) a certain amount of data. These are very helpful
	// for iterating through files.
	public char chompChar() {
		if(str == null)
			return ' ';
		
		char c = charAt(0);
		str = str.replaceFirst(""+c, "");
		return c;
	}
	public void chompSpace() {
		if(str == null)
			return;
		
		char c = charAt(0);
		while((c == ' ' || c == '\t' || c == '\n') && length() > 0) {
			chompChar();
			c = charAt(0);
		}
	}
	public float chompNumber() {
		return Float.parseFloat(chompWord());
	}
	public String chompWord() {
		if(str == null)
			return "";
		
		chompSpace();
		
		
		int lineInd = str.indexOf('\n'),
			spaceInd = str.indexOf(' '),
			periodInd = 10000; //str.indexOf('.');
		
		if(lineInd == -1)	lineInd = 10000;
		if(spaceInd == -1)	spaceInd = 10000;
		if(periodInd == -1)	periodInd = 10000;
		
		int i = MathExt.min(lineInd,spaceInd,periodInd);
		
		String outStr;
		
		if(i == 10000) {
			outStr = str;
			str = "";
		}
		else {
			outStr = str.substring(0,i);
			str = str.replaceFirst(outStr, "");
			outStr = outStr.replaceFirst("\n","");
		}
		
		chompSpace();
		
		return outStr;
	}
	public String chompTo(char c) {
		
		int i = str.indexOf(c);
		String outStr;
		
		if(i == -1) {
			outStr = str;
			str = "";
		}
		else {
			outStr = str.substring(0,i);
			str = str.replaceFirst(outStr+c, "");
		}
		return outStr;
	}
	public String chompLine() {
		if(str == null)
			return "";
		
		int i = str.indexOf('\n');
		String outStr;
		
		if(i == -1) {
			outStr = str;
			str = "";
		}
		else {
			outStr = str.substring(0,i);
			str = str.substring(i+1);
			outStr.replace("\n","");			
		}
		
		return outStr;
	}
	
	public String chompLine(int i) {
		String outStr = "";
		
		for(int k = 0; k < i; k++)
			outStr += chompLine();
		
		return outStr;
	}
	
	public static String randomize(int n) {
		String outStr = "";
		char c;
		
		for(int k = 0; k < n; k++)
			outStr += randomChar();
		
		return outStr;
	}
	
	public static char randomChar() {
		return rndChar.get();
	}

	
	public String[] split(char spl) {
		int strNum = 1;
		
		for(int i = 0; i < str.length(); i++)
			if(charAt(i) == spl)
				strNum++;
		
		String[] strings = new String[strNum];
		StringExt copy = new StringExt(this);
				
		for(int i = 0; i < strNum; i++)
			strings[i] = chompTo(spl);
		
		return strings;
	}
	
	
	public void removeAll(String... sub) {
		for(String s : sub)
			str = str.replaceAll(s, "");
	}


	// DEBUGGING
	public void println() {
		System.out.println(str);
	}
	public boolean isEmpty() {
		return str.length() == 0;
	}
}
