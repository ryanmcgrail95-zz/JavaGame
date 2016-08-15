package ds;

public class StringF {
	private StringBuilder str;
	private String rstr;
	private boolean hasLength = true;
	private int length;
	
	public StringF() {
		str = new StringBuilder("");
		rstr = "";
		length = 0;
	}
	
	public char charAt(int i) {
		return rstr.charAt(i);
	}
	
	public String toString() {
		return rstr;
	}
	
	public int length() {
		return length;
	}

	public int replacen(String substr, String newstr) {
		return replacen(substr, newstr, Integer.MAX_VALUE);
	}
	
	public int replacen(String substr, String newstr, int num) {
		int subLen = substr.length(),
			newLen = newstr.length(),
			len = length() - subLen,
			ind = 0,
			numFinds = 0;
		
		while(num-- > 0) {
			ind = str.indexOf(substr, ind);
					
			if(ind != -1) {
				str.replace(ind, ind+subLen, newstr);
				numFinds++;
				
				ind += newLen;
			}
			else
				break;
		}
				
		updateRealString();
		
		return numFinds;
	}
	
	private void updateRealString() {
		rstr = str.toString();		
		length = rstr.length();		
	}

	public String replace(String substr, String newstr) {
		replacen(substr, newstr, Integer.MAX_VALUE);
		return rstr;
	}
	public String replace(String substr, String newstr, int num) {
		replacen(substr, newstr, num);
		return rstr;
	}
	
	public void replaceFirst(String substr, String newstr) {
		int ind = indexOf(substr);
		
		if(ind == -1)
			return;
		
		str.replace(ind, ind+substr.length(), newstr);
		updateRealString();
	}

	public void set(String newStr) {
		str = new StringBuilder(newStr);
		updateRealString();
	}


	public String substring(int i1) {
		return rstr.substring(i1);
	}
	public String substring(int i1, int i2) {
		return rstr.substring(i1, i2);
	}
	
	public void substringe(int i1) {
		set(substring(i1));
	}
	public void substringe(int i1, int i2) {
		str = new StringBuilder(rstr = rstr.substring(i1, i2));
	}

	
	public int indexOf(String substr) {
		return rstr.indexOf(substr);
	}
	
	public int indexOf(String substr, int fromIndex) {
		return rstr.indexOf(substr, fromIndex);
	}

	public boolean contains(String substr) {
		return indexOf(substr) != -1;
	}

	public boolean startsWith(String substr) {
		int sublen = substr.length();
		if(sublen > length())
			return false;
		else
			return substring(0,sublen).equals(substr);
	}

	public void delete(int i1, int i2) {
		str.delete(i1, i2);		
		updateRealString();
	}
	
	public boolean equals(String other) {
		return toString().equals(other);
	}
	
	public void insert(int index, String s) {
		set(substring(0,index) + s + substring(index,length));
	}

	public void backspaceAt(int index) {
		if(index > 0) {
			if(index == length)
				set(substring(0,length-1));
			else
				set(substring(0,index-1) + substring(index,length));
		}
	}

	public void deleteAt(int index) {
		if(index < length) {
			if(index == 0)
				set(substring(1,length));
			else
				set(substring(0,index) + substring(index+1,length));
		}
	}
}
