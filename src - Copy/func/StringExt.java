package func;

public class StringExt {
	protected String str;
	
	public StringExt() {
		set("");
	}
	
	public StringExt(String str) {
		set(str);
	}
	
	public char charAt(int i) {
		return str.charAt(i);
	}
	
	public int length() {
		return str.length();
	}
	
	public void set(String str) {
		this.str = str;
	}
	public String get() {
		return str;
	}
	
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
	
	public String chompWord() {
		int i = Math2D.min(str.indexOf('\n'),str.indexOf(' '),str.indexOf('.'));
		String outStr;
		
		if(i == -1) {
			outStr = str;
			str = "";
		}
		else {
			outStr = str.substring(0,i);
			str = str.replace(outStr+'\n', "");
			outStr.replace("\n","");
			
			print();
		}
		
		return outStr;
	}
	
	public String chompLine() {
		int i = str.indexOf('\n');
		String outStr;
		
		if(i == -1) {
			outStr = str;
			str = "";
		}
		else {
			outStr = str.substring(0,i);
			str = str.replace(outStr+'\n', "");
			outStr.replace("\n","");			
		}
		
		return outStr;
	}
	
	public void print() {
		System.out.println(str);
	}
}
