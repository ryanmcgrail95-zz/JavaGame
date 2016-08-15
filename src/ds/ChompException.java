package ds;	

public class ChompException extends Throwable {
	private StringExt str;
	private String expected;
	private int index;
	
	public ChompException(StringExt str, String expected, int index) {
		this.str = str;
		this.expected = expected;
		this.index = index;
	}
	
	public String getString() {
		return str.getOriginal();
	}
	
	public String getExpected() {
		return expected;
	}
	public int getIndex() {
		return index;
	}
		
	@Override
	public void printStackTrace() {
		System.err.println(getError());
		
		int b = 8;
		
		String s = str.getOriginal().replaceAll("\n", " ");
		System.err.println(s.substring(Math.max(0, index-b), Math.min(index+b, s.length())));
		
		super.printStackTrace();
	}

	public String getError() {
		// TODO Auto-generated method stub
		return "Expected " + expected + " at index " + index + ".";
	}
}