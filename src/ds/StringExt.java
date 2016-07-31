package ds;

import java.util.ArrayList;
import java.util.List;

import ds.lst.RandomList;
import functions.MathExt;

public class StringExt {
	protected String str = "";
	private StringF temp = new StringF();
	private final static RandomList<Character> rndChar = new RandomList<Character>("Chars");
	
	public static final char NULL_CHAR = 0;
	public static final String NULL_STRING = ""+NULL_CHAR;
	
	int rLen = 0;
	
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
	
	public StringExt() 					{set("");}
	public StringExt(String str) 		{set(str);}
	public StringExt(StringExt str) 	{set(str.get());}
	
	
	// STRING MANIPULATION
	
	public char charAt(int i) {
		if(i >= length())
			return NULL_CHAR;

		char c = str.charAt(pos+i);

		return c;
	}
	public boolean isValidChar(int i) {
		if(i >= 0 && i >= length())
			return false;
		else if(i < 0 && -i > pos)
			return false;
		return true;
	}
	
	public int length() {
		int len = rLen-pos;
		return (len < 0) ? 0 : len;
	}
	private int rlength() {return rLen;}
	
	public void set(String str) {
		this.str = str;
		pos = 0;
		rLen = str.length();
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
	    return str = retstr;
	}

	// CHOMPING FUNCTIONS
	// These functions will "chomp" (remove and return) a certain amount of data. These are very helpful
	// for iterating through files.
	public void chomp(char c) {chomp(""+c);}
	public void chomp(String substr) {		
		if(!nibble(substr))
        	throw new RuntimeException("Expected: " + substr);	
	}
	
	
	public char munchChar() {
		if(isEmpty())
			return ' ';
		
		char c = charAt(0);
		pos++;
		
		return c;
	}
	public void munchSpace() {
		if(isEmpty())
			return;

		int le = rlength();
		
		char c = charAt(0);
		while((c == ' ' || c == '\t' || c == '\n') && le-pos > 0) {
			munchChar();
			c = charAt(0);
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
		int le = rlength();

		int pos0 = pos;
		
		char c;
		do
			c = munchChar();
		while((c != ' ' && c != '\t' && c != '\n' && c != '.') && le-pos > 0);

		if(le-pos > 0)
			pos--;
		
		outStr = str.substring(pos0,pos);
		
		munchSpace();
		
		return outStr;
	}
	
	public String munchNumber() {
		if(isEmpty())
			return "";
		
		munchSpace();
		
		String outStr;
		int pos0 = pos, le = rLen;
		
		char c;
		do
			c = munchChar(); //eatChar(pos++)
		while((c != ' ' && c != '\t' && c != '\n') && le-pos > 0);

		if(le-pos > 0)
			pos--;

		outStr = str.substring(pos0,pos);
		
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
		int subLen = substr.length(),
			newLen = newStr.length(),
			len = rlength() - subLen,
			ind = pos,
			numFinds = 0;
		
		String a, b;
		
		while(num-- > 0) {
			ind = str.indexOf(substr, ind);
					
			if(ind != -1) {
				a = str.substring(0,ind);
				b = str.substring(ind+subLen);
				
				str = a + newStr + b;
				numFinds++;				
				ind += newLen;
			}
			else
				break;
		}
				
		return numFinds;
	}

	public boolean nibble(char c) {return nibble(""+c);}
	public boolean nibble(String substr) {		
		if(startsWith(substr)) {
			pos += substr.length();
			//removeFirst(substr);
			return true;
		}
		return false;
	}

	public char nibbleMisc(char... cs) {
		for(char cc : cs)
    		if(nibble((char) cc))
    			return cc;
    	return NULL_CHAR;
	}
	public String nibbleMisc(String... cs) {
		for(String cc : cs)
    		if(nibble(cc))
    			return cc;
    	return NULL_STRING;
	}

	
		
	public String munchTo(char c) 					{return munchTo(false, "" + c);}
	public String munchTo(boolean munchAll, char c) {return munchTo(munchAll, "" + c);}
	public String munchTo(String substr)			{return munchTo(false, substr);}
	public String munchTo(boolean munchAll, String substr) {
		int minInd = str.indexOf(substr, pos);
				
		if(minInd == -1)
			return (munchAll) ? munchAll() : "";
		else {
			String outStr = str.substring(pos,minInd);
			pos = minInd+substr.length();
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
			String outStr = str.substring(pos, minInd);
			pos = minInd+substrs[minPos].length();
			return outStr;
		}
	}
	
	public String munchAll() {
		String outString = toString();
		pos = rLen;
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
				
		while(!isEmpty()) {
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
			replace(s, "");
	}


	// DEBUGGING
	public void print() 	{System.out.print(this);}
	public void println() 	{System.out.println(this);}
	
	public boolean isEmpty() {
		return length() == 0;
	}
	
	public boolean startsWith(String substr) {
		return str.regionMatches(pos, substr, 0, substr.length());
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
		return str.substring(pos);
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
			
			temp.set(str);
			temp.delete(iStart, iEnd+strEndLen);
			
			str = temp.toString();
			
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
		return toString().equals(other);
	}

}
