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



public final class IO {		
	private IO() {
	}
	
	//BUTTON VARS
	
	
	public static void ini() {        
        Mouse.ini();
        Keyboard.ini();
	}
	
	public static void update() {
		Mouse.update();
		Keyboard.update();
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
