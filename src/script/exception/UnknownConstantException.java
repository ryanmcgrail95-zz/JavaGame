package script.exception;

import ds.StringExt;

public class UnknownConstantException extends ParseException {	
	public UnknownConstantException(StringExt str, String expected, int index) {
		super(str, expected, index);
	}

	@Override
	public String getError() {
		// TODO Auto-generated method stub
		return "Unknown constant at index " + index + ".";
	}
}