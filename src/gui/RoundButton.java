package gui;

import io.Mouse;
import gfx.GOGL;

public class RoundButton {
	
	private SubMenu myMenu;
	
	public RoundButton(SubMenu myMenu) {
		this.myMenu = myMenu;
	}
	
	
	public boolean draw(float x, float y, float r, boolean isSelected) {
		
		GOGL.fillPolygon(x, y, r, 18);
		
		if(isSelected) {
			float dH = r;
			
			myMenu.draw(x + 1.5f*r, y - (dH/2+dH), dH);
		}
		
		return Mouse.clickCircle(x,y,r);
	}
}