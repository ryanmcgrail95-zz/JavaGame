package io;

public final class IO {		
	private IO() {
	}
	
	//BUTTON VARS

	public static void ini() {        
        Mouse.ini();
        Keyboard.ini();
	}
	

	
	//Access and Mutation Functions
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
