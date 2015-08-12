package io;
import java.awt.Container;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cont.GameController;
import functions.Math2D;
import gfx.GOGL;



public final class Keyboard implements KeyEventDispatcher {
	private static byte K_UP = 0, K_DOWN = 1, K_PRESSED = 2, K_RELEASED = 3;
	private static int E_PRESSED = 400, E_DOWN = 401, E_RELEASED = 402;
	private static byte[] keys = new byte[36];
	public static String keyboardString = "";
		
	private Keyboard() {}
	
	//BUTTON VARS

	
	
	public static void ini() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new Keyboard());
	}

	public static void update() {
		for(int i = 0; i < 36; i++)
			if(keys[i] == K_RELEASED)
				keys[i] = K_UP;
	}
	

	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		int event = e.getID();
		char c = e.getKeyChar();
		
		if(event == 400) {
			if(c == 'x' || c == 'y' || c == '=' || c == ' ' || c == '+' || c == '-' || c == '*' || c == '/' ||
			   c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' ||
			   c == '8' || c == '9' || c == '(' || c == ')')
				keyboardString += c;
			
			//System.out.println(e.);
			//System.out.println(e.VK_BACK_SPACE);

				
			if((int)e.getKeyChar() == e.VK_BACK_SPACE && keyboardString.length() > 0)
				keyboardString = keyboardString.substring(0, keyboardString.length()-1);
		}
		
		
		
		if(event == E_PRESSED)
			set(c,K_PRESSED);
		else if(event == E_DOWN)
			set(c,K_DOWN);
		else if(event == E_RELEASED)
			set(c,K_RELEASED);
			
		return false;
	}
	
	
	public static int getPos(char c) {
		int pos = -1;
		if(Character.isAlphabetic(c)) {
			c = Character.toLowerCase(c);
			pos = c - 'a';
		}
		else if(Character.isDigit(c))
			pos = 26 + (c - '0');
		return pos;
	}
	public static void set(char c, byte state) {
		int pos = getPos(c);
		if(pos > -1)
			keys[pos] = state;
	}
	public static  byte get(char c) {
		int pos = getPos(c);
		if(pos > -1)
			return keys[pos];
		else
			return -1;
	}
	
	
	public static boolean checkPressed(char c) {return check(c,K_PRESSED);}
	public static boolean checkReleased(char c) {return check(c,K_RELEASED);}
	public static  boolean checkDown(char c) {return check(c,K_DOWN) || check(c,K_PRESSED);}
	public static  boolean checkUp(char c) {return check(c,K_UP) || check(c,K_RELEASED);}
	public static  boolean check(char c, byte state) {
		return get(c) == state;
	}
	


	//Access and Mutation Functions
	public static float getWASDDir() {
		int hDir = 0, vDir = 0;
		boolean aDown = checkDown('A'),
				wDown = checkDown('W'),
				dDown = checkDown('D'),
				sDown = checkDown('S');
		
		if(!aDown && !dDown && !sDown && !wDown)
			return -1;
			
		if(aDown && !dDown)
			hDir = -1;
		else if(dDown && !aDown)
			hDir = 1;
		
		if(sDown && !wDown)
			vDir = -1;
		else if(wDown && !sDown)
			vDir = 1;
		
		return Math2D.calcPtDir(0,0,hDir,vDir);
	}
}
