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



public final class IO implements KeyEventDispatcher {
	private static IO instance;
	private static boolean aPress = false, dPress = false, aDown = false, wDown = false, dDown = false, sDown = false;
	public static String keyboardString = "";
		
	private IO() {
	}
	
	//BUTTON VARS
	private static boolean aButtonP = false, aButtonR = false;
	private static boolean zButtonP = false, zButtonR = false;	
	
	
	public static void ini() {
		instance = new IO();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(instance);
        
        Mouse.ini();
	}
	
	public static void update() {
		aPress = false;
		dPress = false;
		
		Mouse.update();
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
				
		if(event == 400) {
			if(c == 'a' || c == 'A')
				aPress = true;
			if(c == 'd' || c == 'D')
				dPress = true;
		}
		if(event == 401) {
			if(c == 'a' || c == 'A')
				aDown = true;
			if(c == 'd' || c == 'D')
				dDown = true;
			if(c == 'w' || c == 'W')
				wDown = true;
			if(c == 's' || c == 'S')
				sDown = true;
		}
		else if(event == 402) {
			if(c == 'a' || c == 'A')
				aDown = false;
			if(c == 'd' || c == 'D')
				dDown = false;
			if(c == 'w' || c == 'W')
				wDown = false;
			if(c == 's' || c == 'S')
				sDown = false;
		}
		
		if(event == 400) {
			if(c == 'u' || c == 'U')
				aButtonP = true;
			if(c == 'm' || c == 'M')
				zButtonP = true;
		}
		else if(event == 402) {
			if(c == 'u' || c == 'U')
				aButtonR = true;
			if(c == 'm' || c == 'M')
				zButtonR = true;
		}
		
		return false;
	}
	
	
	
	public void clearKeyboardString()
	{
		keyboardString = "";
	}
	
	public String getKeyboardString()
	{
		return keyboardString;
	}
	
	public void setKeyboardString(String str)
	{
		keyboardString = str;
	}



	
	
	/*@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void keyTyped(KeyEvent e)
	{
		int event = e.getID();
		
		//if(event == 400)
		//{
			System.out.println(e.getKeyCode());
			System.out.println(e.VK_BACK_SPACE);

			
			if(e.getKeyCode() == e.VK_BACK_SPACE && keyboardString.length() > 0)
				keyboardString = keyboardString.substring(0, keyboardString.length()-1);
		//}
	}*/
	
	//Access and Mutation Functions
	public static float getWASDDir() {
		int hDir = 0, vDir = 0;
		
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


	
	public static boolean getAButtonPressed() {
		if(aButtonP) {
			aButtonP = false;
			return true;
		}
		else
			return false;
	}

	public static boolean getAButtonReleased() {
		if(aButtonR) {
			aButtonR = false;
			return true;
		}
		else
			return false;
	}



	public static boolean getZButtonPressed() {
		if(zButtonP) {
			zButtonP = false;
			return true;
		}
		else
			return false;
	}
}
