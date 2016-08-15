package io;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import functions.Math2D;



public final class Keyboard implements KeyEventDispatcher {
	private static byte K_UP = 0, K_DOWN = 1, K_PRESSED = 2, K_RELEASED = 3;
	private static int E_PRESSED = 400, E_DOWN = 401, E_RELEASED = 402;
	private static byte[] keys = new byte[256];
	public static String keyboardString = "";
	
	public final static char VK_ARROW_LEFT = 37, VK_ARROW_UP = 38, VK_ARROW_RIGHT = 39, VK_ARROW_DOWN = 40;
		
	public final static char C_NULL = 0, C_BACKSPACE = 8, C_DELETE = 127, C_ARROW_LEFT = 1, C_ARROW_UP = 2, C_ARROW_RIGHT = 3, C_ARROW_DOWN = 4;
	
	public static char charDown = C_NULL;
	
	public final static String 
		REGEX_NON_CONTROL = "[a-zA-Z0-9_;:,\\[\\]`~\\<\\>\\{\\}_\\-\\+=\"'!@#%$\\^&\\*\\(\\)\\. \\\\\\/\\|\\?\t]",
		REGEX_VALID_KEYS = "[a-z0-9\n;\\[\\],\\.\\-=\\/\\\\ \t]";
	
	private static char 
		dpadLeft = 'a',
		dpadRight = 'd',
		dpadUp = 'w',
		dpadDown = 's';
		
	private Keyboard() {}
	
	//BUTTON VARS

	
	
	public static void ini() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new Keyboard());
	}

	public static void update() {
		for(int i = 0; i < keys.length; i++)
			if(keys[i] == K_RELEASED)
				keys[i] = K_UP;
			else if(keys[i] == K_PRESSED)
				keys[i] = K_DOWN;
	}
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		int event = e.getID(),
			code = e.getKeyCode();
		char c = Character.toLowerCase((char) code);
		
		boolean isValidKey = false;
		
		if(Pattern.matches(Keyboard.REGEX_VALID_KEYS, ""+c) || c == C_BACKSPACE || c == C_DELETE || code == 222 || code == 192) {
			isValidKey = true;
			
			if(!e.isControlDown())
				c = e.getKeyChar();
		}
		
		if(code == VK_ARROW_LEFT)
			c = C_ARROW_LEFT;
		if(code == VK_ARROW_UP)
			c = C_ARROW_UP;
		if(code == VK_ARROW_DOWN)
			c = C_ARROW_DOWN;
		if(code == VK_ARROW_RIGHT)
			c = C_ARROW_RIGHT;
		
		if(c >= C_ARROW_LEFT && c <= C_ARROW_DOWN) {
			isValidKey = true;
		}
		
		
		if(event == 400) {
			if(c == 'x' || c == 'y' || c == '=' || c == ' ' || c == '+' || c == '-' || c == '*' || c == '/' ||
			   c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' ||
			   c == '8' || c == '9' || c == '(' || c == ')')
				keyboardString += c;
							
			if((int)e.getKeyChar() == e.VK_BACK_SPACE && keyboardString.length() > 0)
				keyboardString = keyboardString.substring(0, keyboardString.length()-1);
		}
		
		//System.out.println(c + "[" + event + "]: " + code);
		
		if(event == E_DOWN) {
			if(code == KeyEvent.VK_CONTROL)
				setControl(true);
			else {
				if(isValidKey)
					charDown = c;
				if(get(c) == K_UP)
					set(c,K_PRESSED);	
			}
		}
		else if(event == E_RELEASED) {			
			if(code == KeyEvent.VK_CONTROL)
				setControl(false);
			else {
				set(c, K_RELEASED);				
				if(charDown == c)
					charDown = 0;
			}
		}
			
		return false;
	}
	
	
	public static int getPos(char c) {
		return Character.toLowerCase(c);
		/*int pos = -1;
		if(Character.isAlphabetic(c)) {
			c = Character.toLowerCase(c);
			pos = c - 'a';
		}
		else if(Character.isDigit(c))
			pos = 26 + (c - '0');
		return pos;*/
	}
	private static void set(char c, byte state) {
		int pos = getPos(c);
		if(pos > -1)
			keys[pos] = state;
	}
	public static byte get(char c) {
		int pos = getPos(c);
		if(pos > -1)
			return keys[pos];
		else
			return -1;
	}
	
	private static boolean isControlActive = false;
	private static void setControl(boolean isDown) {
		isControlActive = isDown;
	}
	public static boolean isControlActive() {
		return isControlActive;
	}
	
	public static boolean checkPressed(char c) {return check(c,K_PRESSED);}
	public static boolean checkReleased(char c) {return check(c,K_RELEASED);}
	public static boolean checkDown(char c) {return check(c,K_DOWN) || check(c,K_PRESSED);}
	public static boolean checkUp(char c) {return check(c,K_UP) || check(c,K_RELEASED);}
	public static boolean check(char c, byte state) {
		return get(c) == state;
	}
	
	public static boolean eat(char c, byte state) {
		boolean output = (get(c) == state);		
		set(c, K_UP);
		
		return output;
	}
	public static boolean eatPressed(char c) {return eat(c,K_PRESSED);}


	//Access and Mutation Functions
	public static float getDPadDown()		{return getDPad(checkDown(dpadUp),checkDown(dpadLeft),checkDown(dpadDown),checkDown(dpadRight));}
	public static float getDPadPressed() 	{return getDPad(checkPressed(dpadUp),checkPressed(dpadLeft),checkPressed(dpadDown),checkPressed(dpadRight));}

	public static float getArrowDown()		{return getDPad(checkDown(C_ARROW_UP),checkDown(C_ARROW_LEFT),checkDown(C_ARROW_DOWN),checkDown(C_ARROW_RIGHT));}
	public static float getArrowPressed() 	{return getDPad(checkPressed(C_ARROW_UP),checkPressed(C_ARROW_LEFT),checkPressed(C_ARROW_DOWN),checkPressed(C_ARROW_RIGHT));}

	private static float getDPad(boolean up, boolean left, boolean down, boolean right) {
		if(!left && !right && !up && !down)
			return -1;
				
		return Math2D.calcPtDir(0,0,
			(right ? 1 : 0) - (left ? 1 : 0),
			(up ? 1 : 0) - (down ? 1 : 0));
	}

	public static char getCharDown() {
		return charDown;
	}
}
