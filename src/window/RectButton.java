package window;

import io.Mouse;

public abstract class RectButton extends Button {

	public RectButton(float x, float y, float w, float h) {
		super(x, y, w, h);
	}
	
	public boolean checkMouse() {		
		return Mouse.checkRectangle(getScreenX(),getScreenY(), w(),h());
	}
}
