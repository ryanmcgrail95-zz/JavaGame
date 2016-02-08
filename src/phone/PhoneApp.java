package phone;

import java.util.ArrayList;
import java.util.List;

import resource.sound.Sound;
import time.Timer;
import io.Mouse;
import ds.vec2;
import functions.MathExt;

public abstract class PhoneApp {

	protected SmartPhone parent;
	protected List<Button> buttonList;
	
	
	public PhoneApp(SmartPhone owner) {
		parent = owner;
		buttonList = new ArrayList<Button>();
	}
	
	protected abstract class Button {
		private static final float wobbleSize = 4;
		protected Timer clickTimer;
		protected float x, y, wobbleDir;
		
		public Button(float x, float y) {
			this.x = x;
			this.y = y;
			clickTimer = new Timer(0,10);
		}
		
		public float getX() {
			boolean click = !clickTimer.checkOnce();
			return click ? x + MathExt.rnd(-wobbleSize,wobbleSize) : x;
		}
		public float getY() {
			boolean click = !clickTimer.checkOnce();
			return click ? y + MathExt.rnd(-wobbleSize,wobbleSize) : y;
		}
		
		public abstract boolean checkMouse();
		public boolean hover() {
			boolean state = checkMouse();
			if(state) {
				Mouse.setFingerCursor();
				wobbleDir += 20;
			}
			else
				wobbleDir = 0;
			return state;
		}
		public boolean click() {
			if(Mouse.consumeLeftClick()) {
				clickTimer.reset();
				Sound.play("button");
				return true;
			}
			return false;
		}
		
		public abstract void draw();
	}
	protected abstract class CircleButton extends Button{
		protected float r;
		
		public CircleButton(float x, float y, float r) {
			super(x,y);
			this.r = r;
		}
		
		public boolean checkMouse() {
			return hoverCircle(x,y,r);
		}
	}
	
	
	public void checkButtons() {
		for(Button b : buttonList) {
			if(b.hover())
				b.click();
			b.draw();
		}
	}
	
	
	public vec2 getMouseCoords() {
		return (vec2) new vec2(Mouse.getMouseX()-parent.getX(),Mouse.getMouseY()-parent.getY()).dive(parent.getScale());
	}
	public boolean hoverRectangle(float x, float y, float w, float h) {
		return Mouse.checkRectangle(parent.getX()+x*parent.getScale(),parent.getY()+y*parent.getScale(), w*parent.getScale(), h*parent.getScale());
	}
	public boolean hoverCircle(float x, float y, float r) {
		return Mouse.checkCircle(parent.getX()+x*parent.getScale(),parent.getY()+y*parent.getScale(), r*parent.getScale());
	}
	
	public abstract void draw();
}
