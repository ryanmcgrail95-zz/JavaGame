package io;

public final class IO {		
	public static final byte 
		C_DOWN = 0,
		C_RIGHT = 1,
		C_LEFT = 2,
		C_UP = 3;

	private IO() {
	}
	
	//BUTTON VARS

	public static void ini() {        
        Mouse.ini();
        Keyboard.ini();
	}
	

	
	//Access and Mutation Functions
	public static boolean checkPressed(byte button) {
		//NOTE: SAVE MAP/LIST OF KEYS
		
		switch(button) {
			case C_DOWN:	return Keyboard.checkPressed('k');
			case C_LEFT:	return Keyboard.checkPressed('j');
			case C_RIGHT:	return Keyboard.checkPressed('l');
			case C_UP:		return Keyboard.checkPressed('i');
		
			default: return false;
		}
	}
	
	public static boolean getAButtonPressed() {
		return Keyboard.checkPressed('u');
	}

	public static boolean getAButtonReleased() {
		return Keyboard.checkReleased('u');
	}

	public static boolean getZButtonPressed() {
		return Keyboard.checkPressed('m');
	}

	public static boolean getZButtonReleased() {
		return Keyboard.checkReleased('m');
	}
}
