package gui;

import io.Mouse;
import gfx.GOGL;

public class RectButton {
	
	private SubMenu myMenu;
	
	public RectButton(SubMenu myMenu) {
		this.myMenu = myMenu;
	}

	public boolean draw(float x, float y, float h, boolean isSelected) {
		
		float w;
		w = 6*h;
		
		GOGL.fillRectangle(x, y, w, h);
		
		if(isSelected) {
			//myMenu.draw(x + r, y - (dH/2+dH), dH);
		}
		
		return Mouse.checkRectangle(x,y, w,h);
	}
}