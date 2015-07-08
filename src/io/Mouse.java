package io;

import functions.Math2D;
import gfx.GOGL;
import gfx.RGBA;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import object.actor.Player;
import time.Timer;
import cont.GameController;

public class Mouse implements MouseListener, MouseMotionListener {
	
	private static int wMX, wMY;
	private static float mouseX, mouseY, mouseXPrevious = 0, mouseYPrevious = 0, mouseXDelta, mouseYDelta;
	private static boolean leftMouse, leftDown, rightDown, leftClick, rightClick, leftChecked, rightChecked;
	

	public static final int C_ARROW = 0, C_TEXT = 2, C_HOURGLASS = 3, C_POINTING_FINGER = 12;
	private static Timer cursorResetTimer;
	private static Timer leftTimer, rightTimer;
	

	public static void ini() {
		Mouse instance = new Mouse();
		GOGL.getCanvas().addMouseListener(instance);
		GOGL.getCanvas().addMouseMotionListener(instance);
		
		leftTimer = new Timer(1); rightTimer = new Timer(1);
		cursorResetTimer = new Timer(2);
	}
	public static void update() {				
		mouseXDelta = mouseX - mouseXPrevious;
		mouseYDelta = mouseY - mouseYPrevious;
				
		mouseXPrevious = mouseX;
		mouseYPrevious = mouseY;
				
		if(leftTimer.checkOnce())
			leftClick = false;		
		if(rightTimer.checkOnce())
			rightClick = false;
	}
	
	

	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			leftDown = true;
		else
			rightDown = true;
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(!leftChecked) {
				leftClick = true;
				leftChecked = true;
				
				leftTimer.reset();
			}
		}
		else
			if(!rightChecked) {
				rightClick = true;
				rightChecked = true;
				
				rightTimer.reset();
			}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			leftDown = false;
			leftChecked = false;
		}
		else {
			rightDown = false;
			rightChecked = false;
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent arg0) {}	
	public void mouseExited(MouseEvent arg0) {}
	
	public boolean getMouseClick(byte button) {
		boolean returnVal = false;
		
		if(button == 0) {
			returnVal = leftClick;
			leftClick = false;
		}
		else if(button == 1) {
			returnVal = rightClick;
			rightClick = false;
		}
		
		return returnVal;
	}

	
	// GETTING MOUSE COORDINATES
	
	public static float getMouseX() {
		return mouseX;
	}
	public static float getMouseY() {
		return mouseY;
	}
	public static int getMousePickX() {
		return wMX;
	}
	public static int getMousePickY() {
		return wMY;
	}


	public static boolean getLeftMouse() {
		return leftDown;
	}
	public static boolean getRightMouse() {
		return rightDown;
	}
	
	public static boolean getLeftClick() {
		return leftClick;
	}

	

	// MOUSE MOVEMENT
	
	public void mouseDragged(MouseEvent arg0) {
		mouseX = 1.f*(arg0.getX()-GOGL.BORDER_LEFT)/(GOGL.SCREEN_WIDTH-2*GOGL.BORDER_LEFT)*640;
		mouseY = 1.f*(arg0.getY()-GOGL.BORDER_TOP)/(GOGL.SCREEN_HEIGHT-GOGL.BORDER_LEFT-GOGL.BORDER_TOP)*480;
	}
	public void mouseMoved(MouseEvent arg0) {
		wMX = arg0.getX();
		wMY = arg0.getY();
		
		mouseX = 1.f*(arg0.getX()-GOGL.BORDER_LEFT)/(GOGL.SCREEN_WIDTH-2*GOGL.BORDER_LEFT)*640;
		mouseY = 1.f*(arg0.getY()-GOGL.BORDER_TOP)/(GOGL.SCREEN_HEIGHT-GOGL.BORDER_LEFT-GOGL.BORDER_TOP)*480;		
	}

	
	
	// SETTING CURSOR SYMBOL
	
	public static void setCursor(int newCursor) {
		GameController.getInstance().setCursor(newCursor);
		cursorResetTimer.reset();
	}
	public static void setArrowCursor() {
		setCursor(C_ARROW);
	} 
	public static void setFingerCursor() {
		setCursor(C_POINTING_FINGER);
	} 
	
	public static void resetCursor() {
		if(cursorResetTimer.checkOnce())
			setArrowCursor();
	}
	
	public static RGBA getPixelColor() {
		return GOGL.getPixelColor(getMousePickX(), getMousePickY());
	}
	
	
	
	// CHECKING CURSOR POSITION
	
	public static boolean checkRectangle(float x, float y, float w, float h) {
		return Math2D.checkRectangle(mouseX,mouseY, x,y,w,h);
	}
	public static boolean checkCircle(float x, float y, float r) {
		return Math2D.checkCircle(mouseX,mouseY, x,y,r);
	}
	public static boolean clickCircle(float x, float y, float r) {
		return checkCircle(x,y,r) && getLeftClick();
	}
	public static float getDeltaX() {return mouseXDelta;}
	public static float getDeltaY() {return mouseYDelta;}
}
