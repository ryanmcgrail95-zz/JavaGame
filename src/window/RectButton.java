package window;

import io.Mouse;

public abstract class RectButton extends Button {

	public RectButton(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	
	public boolean checkMouse() {		
		return getParent().checkRectangle(x(),y(),w(),h());
	}
}
