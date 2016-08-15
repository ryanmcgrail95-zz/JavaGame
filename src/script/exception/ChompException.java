package script.exception;

import ds.StringExt;

public class ChompException extends ParseException {	
	public ChompException(StringExt str, String expected, int index) {
		super(str, expected, index);
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return "Expected " + expected + " at index " + index + ".";
	}
}