package ds;

public class StringF {
	private StringBuilder str = new StringBuilder();
	private String rstr;
	private boolean didChange = true;
	private int length;
	
	public char charAt(int i) {
		return rstr.charAt(i);
	}
	
	public String toString() {
		return rstr;
	}
	
	public int length() {
		if(didChange) {
			didChange = false;
			return length = str.length();
		}
		else
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
				didChange = true;
				str.replace(ind, ind+subLen, newstr);
				numFinds++;
				
				ind += newLen;
			}
			else
				break;
		}
		
		rstr = str.toString();
		
		return numFinds;
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
		
		didChange = true;
		str.replace(ind, ind+substr.length(), newstr);
		rstr = str.toString();
	}

	public void set(String newStr) {
		didChange = true;
		str = new StringBuilder(rstr = newStr);
	}


	public String substring(int i1) {
		return rstr.substring(i1);
	}
	public String substring(int i1, int i2) {
		return rstr.substring(i1, i2);
	}
	
	public void substringe(int i1) {
		didChange = true;
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
		didChange = true;
		str.delete(i1, i2);
		rstr = str.toString();
	}
	
	public boolean equals(String other) {
		return toString().equals(other);
	}
}
