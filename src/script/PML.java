package script;

public abstract class PML {
	protected static final byte V_NUMBER = 0, V_OBJECT = 1, V_STRING = 2, V_TEXTURE = 3, V_ACTION = 4, V_VARIABLE = 5, V_POINTER = 6, V_BOOLEAN = 7;
	protected static final byte P_NUMBER = 0, P_OBJECT = 1, P_ACTION = 2, P_VARIABLE = 3, P_STRING = 4, P_NEW_ACTION = 5, P_NEW_OBJECT = 6, P_CONSTANT = 7;
	protected static final int I_EQUAL = 0, I_NOT_EQUAL = 1, I_LESS = 2, I_LESS_EQUAL = 3, I_GREATER = 4, I_GREATER_EQUAL = 5;
	protected static Variable C_EQUAL, C_NOT_EQUAL, C_LESS, C_LESS_EQUAL, C_GREATER, C_GREATER_EQUAL;
	
	protected static final byte O_PLAYER = 0;
	
	public static void ini() {
		Action.ini();
		
		C_EQUAL = new Variable("C_EQUAL", true, true).set(I_EQUAL);
		C_NOT_EQUAL = new Variable("C_NOT_EQUAL", true, true).set(I_NOT_EQUAL);
		C_LESS = new Variable("C_LESS", true, true).set(I_LESS);
		C_LESS_EQUAL = new Variable("C_LESS_EQUAL", true, true).set(I_LESS_EQUAL);
		C_GREATER = new Variable("C_GREATER_EQUAL", true, true).set(I_GREATER);
		C_GREATER_EQUAL = new Variable("C_GREATER_EQUAL", true, true).set(I_GREATER_EQUAL);
	}
}
