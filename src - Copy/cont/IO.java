package cont;
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

import func.Math2D;
import gfx.GOGL;



public final class IO implements KeyEventDispatcher, MouseListener, MouseMotionListener {
	private static IO instance;
	private static boolean aPress = false, dPress = false, aDown = false, wDown = false, dDown = false, sDown = false, leftDown, rightDown, leftClick, rightClick, leftChecked, rightChecked;
	public static float mouseX = 0, mouseY = 0, wMX, wMY, moXP = 0, moYP = 0, moXD, moYD;
	public static String keyboardString = "";
	
	private static boolean leftMouse;
	
	final static int CHECK_TIMER = 1;
	static int leftCheckTimer = 0, rightCheckTimer = 0;
	
	public static final int C_ARROW = 0, C_TEXT = 2, C_HOURGLASS = 3, C_POINTING_FINGER = 12;
	private static int cursorResetTimer = 2;
	
	private IO() {
	}
	
	//BUTTON VARS
	private static boolean aButtonP = false, aButtonR = false;
	private static boolean zButtonP = false, zButtonR = false;	
	
	
	public static void ini() {
		instance = new IO();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(instance);
		GOGL.getCanvas().addMouseListener(instance);
		GOGL.getCanvas().addMouseMotionListener(instance);
	}
	
	public static void update() {
		moXP = mouseX;
		moYP = mouseY;
		
		/*if(gameContr.isVisible()) {		
			mouseX = MouseInfo.getPointerInfo().getLocation().x - gameContr.getLocationOnScreen().x;
			mouseY = MouseInfo.getPointerInfo().getLocation().y - gameContr.getLocationOnScreen().y;
		}*/
		
		moXD = mouseX - moXP;
		moYD = mouseY - moYP;
		
		aPress = false;
		dPress = false;
		
		if(leftCheckTimer > 0)
			leftCheckTimer--;
		else
			leftClick = false;
		
		if(rightCheckTimer > 0)
			rightCheckTimer--;
		else
			rightClick = false;
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

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			leftDown = true;
		else
			rightDown = true;
		
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			if(!leftChecked)
			{
				leftClick = true;
				leftChecked = true;
				
				leftCheckTimer = CHECK_TIMER;
			}
		}
		else
			if(!rightChecked)
			{
				rightClick = true;
				rightChecked = true;
				
				rightCheckTimer = CHECK_TIMER;
			}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			leftDown = false;
			leftChecked = false;
		}
		else
		{
			rightDown = false;
			rightChecked = false;
		}
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



	
	public boolean getMouseClick(byte button)
	{
		boolean returnVal = false;
		
		if(button == 0)
		{
			returnVal = leftClick;
			leftClick = false;
		}
		else if(button == 1)
		{
			returnVal = rightClick;
			rightClick = false;
		}
		
		return returnVal;
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
	
	public static float getMouseX() {
		return mouseX;
	}
	public static float getMouseY() {
		return mouseY;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouseX = 1.f*(arg0.getX()-GOGL.BORDER_LEFT)/(GOGL.SCREEN_WIDTH-2*GOGL.BORDER_LEFT)*640;
		mouseY = 1.f*(arg0.getY()-GOGL.BORDER_TOP)/(GOGL.SCREEN_HEIGHT-GOGL.BORDER_LEFT-GOGL.BORDER_TOP)*480;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		wMX = arg0.getX();
		wMY = arg0.getY();
		
		mouseX = 1.f*(arg0.getX()-GOGL.BORDER_LEFT)/(GOGL.SCREEN_WIDTH-2*GOGL.BORDER_LEFT)*640;
		mouseY = 1.f*(arg0.getY()-GOGL.BORDER_TOP)/(GOGL.SCREEN_HEIGHT-GOGL.BORDER_LEFT-GOGL.BORDER_TOP)*480;		
	}

	public static boolean getLeftMouse() {
		return leftDown;
	}
	
	public static float getMousePickX() {
		return wMX;
	}
	public static float getMousePickY() {
		return 480-wMY + 8;
	}

	public static boolean getRightMouse() {
		return rightDown;
	}

	public static boolean getLeftClick() {
		return leftClick;
	}
	
	public static void setCursor(int newCursor) {
		GameController.getInstance().setCursor(newCursor);
		cursorResetTimer = 2;
	}
	public static void resetCursor() {
		if(cursorResetTimer > 0) {
			cursorResetTimer--;
			return;
		}
		
		setCursor(C_ARROW);
	}
}
