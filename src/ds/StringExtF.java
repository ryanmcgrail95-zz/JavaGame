package ds;

import java.util.ArrayList;
import java.util.List;

import ds.lst.RandomList;
import functions.MathExt;

public class StringExtF {
	protected String str = new StringF();
	private final static RandomList<Character> rndChar = new RandomList<Character>("Chars");
	static {
		for(int i = 0; i < 26; i++) {
			rndChar.add((char) ('a'+i));
			rndChar.add((char) ('A'+i));
		}
		for(int i = 0; i < 10; i++)
			rndChar.add((char) ('0'+i));
	}
	
	private int pos = 0;
	
	
	// CONSTRUCTORS
	
	public StringExtF() 				{set("");}
	public StringExtF(String str) 		{set(str);}
	public StringExtF(StringExtF str) 	{set(str.get());}
	
	
	// STRING MANIPULATION
	
	public char charAt(int i) 	{return str.charAt(i);}
	public int length() 		{return str.length();}
	
	public void set(String str) {
		this.str.set(str);
		pos = 0;
	}
	public String get() 		{return toString();}
	
	
	public boolean contains(String substr) {
		return str.contains(substr);
	}
	
	// EFFECTS
	
	public String capitalize() {
	    String[] words = split(" ");
	    StringBuilder ret = new StringBuilder();
	    for(int i = 0; i < words.length; i++) {
	        ret.append(Character.toUpperCase(words[i].charAt(0)));
	        ret.append(words[i].substring(1));
	        if(i < words.length - 1) {
	            ret.append(' ');
	        }
	    }
	    
	    String retstr = ret.toString();
	    str.set(retstr);
	    return retstr;
	}

	// CHOMPING FUNCTIONS
	// These functions will "chomp" (remove and return) a certain amount of data. These are very helpful
	// for iterating through files.
	public char munchChar() {
		if(isEmpty())
			return ' ';
		
		char c = charAt(0);
		str.substringe(1);
		return c;
	}
	public void munchSpace() {
		if(isEmpty())
			return;

		int i = 0, len = length();
		char c = charAt(0);
		while((c == ' ' || c == '\t' || c == '\n') && i < len) {
			i++;
			c = charAt(i);
		}
		
		
	}

	public int munchInt() 		{return Integer.parseInt(munchNumber());}
	public long munchLong() 	{return Long.parseLong(munchNumber());}	
	public float munchFloat() 	{return Float.parseFloat(munchNumber());}
	public double munchDouble()	{return Double.parseDouble(munchNumber());}
	
	public int indexOf(String... substrs) {
		int curInd, minInd = -1;
		for(String substr : substrs)
			if((curInd = str.indexOf(substr)) != -1)
				if(minInd == -1 || curInd < minInd)
					minInd = curInd;
		return minInd;
	}
	
	public String munchWord() {
		if(isEmpty())
			return "";
		
		munchSpace();
		
		String outStr;
		int index;
		
		if((index = indexOf("\n","\t"," ",".")) == -1)
			outStr = munchAll();
		else {
			outStr = str.substring(0,index);
			str.substringe(index);
		}
		
		munchSpace();
		
		return outStr;
	}
	
	public String munchNumber() {
		if(isEmpty())
			return "";
		
		munchSpace();
		int index;
		String outStr;
		
		if((index = indexOf("\n","\t"," ")) == -1)
			return munchAll();
		else {
			outStr = str.substring(0,index);
			str.substringe(index);
		}
		
		munchSpace();
		return outStr;
	}
	
	
	
	public boolean removeFirst(String substr) {
		return replaceFirst(substr, "");
	}
	
	public boolean replaceFirst(String substr, String newStr) {
		return replace(substr, newStr, 1) > 0;
	}
	public int replace(String subStr, String newStr) {
		return replace(subStr, newStr, Integer.MAX_VALUE);
	}
	public int replace(String substr, String newStr, int num) {
		return str.replacen(substr, newStr, num);
	}
	
	public boolean nibble(String substr) {
		if(startsWith(substr)) {
			removeFirst(substr);
			return true;
		}
		return false;
	}
	
	public String munchTo(char c) 					{return munchTo(false, "" + c);}
	public String munchTo(boolean munchAll, char c) {return munchTo(munchAll, "" + c);}
	public String munchTo(String substr)			{return munchTo(false, substr);}

	
	
	public String munchTo(boolean munchAll, String substr) {
		int minInd = str.indexOf(substr);
				
		if(minInd == -1)
			return (munchAll) ? munchAll() : "";
		else {
			String outStr = str.substring(0,minInd);
			str.substringe(minInd+substr.length());
			return outStr;
		}
	}
	
	public String munchTo(boolean munchAll, String... substrs) {
		int minPos = -1, k = 0;
		int curInd, minInd = -1;
		for(String substr : substrs) {
			if((curInd = str.indexOf(substr)) != -1)
				if(minInd == -1 || curInd < minInd) {
					minInd = curInd;
					minPos = 0;
				}
			k++;
		}
		
		if(minInd == -1)
			return (munchAll) ? munchAll() : "";
		else {
			String outStr = str.substring(0,minInd);
			str.substringe(minInd+substrs[minPos].length());
			return outStr;
		}
	}
	
	public String munchAll() {
		String outString = str.toString();
		str.set("");
		return outString;
	}
	
	public String munchLine() {
		String outStr = munchTo("\n");
		return (outStr == "") ? munchAll() : outStr;
	}
	
	public String munchLines(int num) {
		StringBuilder outStr = new StringBuilder();
		
		while(num-- > 0)
			outStr.append(munchLine());
		
		return outStr.toString();
	}
	
	public static String randomize(int len) {
		String outStr = "";
		
		while(len-- > 0)
			outStr += randomChar();
		
		return outStr;
	}
	
	public static char randomChar() {
		return rndChar.get();
	}

	public String[] split(char splitter) {
		return split("" + splitter);
	}
	
	public String[] split(String splitter) {
		List<String> spl = new ArrayList<String>();		
		String curStr;
		int splLen;
				
		while(!str.equals("")) {
			curStr = munchTo(true, splitter);
			if(!curStr.equals(""))
				spl.add(curStr);
		}

		String[] strArray = new String[splLen = spl.size()];
		for(int i = 0; i < splLen; i++)
			strArray[i] = spl.get(i);
		
		spl.clear();
		
		return strArray;
	}
	
		
	public void removeAll(String... sub) {
		for(String s : sub)
			str.replace(s, "");
	}


	// DEBUGGING
	public void print() 	{System.out.print(this);}
	public void println() 	{System.out.println(this);}
	
	public boolean isEmpty() {
		return str.length() == 0;
	}
	
	public boolean startsWith(String string) {
		return str.startsWith(string);
	}
	public boolean isWhiteSpace() {
		int len = str.length();
		char c;
		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);
			if(!Character.isWhitespace(c))
				return false;
		}

		return true;
	}
	
	public String toString() {
		return str.toString();
	}
	
	/*			FILE PARSING (FILE EATING) FUNCTIONS
		Munch: Consumes x number of characters from string, removing them, and returns 
			string. If they do not exist, returns empty string.
			
		Nibble: Checks to see if given string is present. If so, returns true; otherwise,
		retuns false.
	
		Chomp: Attempt to eat from string. If fails, throws exception.
	 */

	public String munchFromTo(String strStart, String strEnd) {
		int iStart = str.indexOf(strStart),
			iEnd = str.indexOf(strEnd,iStart),
			strEndLen = strEnd.length(),
			len = str.length();

		if(iStart != -1 && iEnd != -1) {
			String outStr = str.substring(iStart, iEnd+strEndLen), a = "", b = "";
			str.delete(iStart, iEnd+strEndLen);
			return outStr;
		}
		else
			return "";
	}
	public static String repeat(String str, int times) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < times; i++)
			sb.append(str);
		return sb.toString();
	}
	
	public boolean equals(String other) {
		return equals(other);
	}
}
