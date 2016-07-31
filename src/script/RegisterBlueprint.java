package script;

import ds.nameid.ID;

public class RegisterBlueprint {
	private Register 	startValue;

	public RegisterBlueprint(boolean isConstant, boolean isTrueVariable) {
		startValue = new Register("BLUEPRINT", isConstant, isTrueVariable);
	}
	
	public Register get() {return startValue;}
	
	public String toString() {
		return "[RegisterBlueprint: " + startValue + "]";
	}
}
