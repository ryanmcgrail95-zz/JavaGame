package gui;

import java.util.ArrayList;
import java.util.List;

public class SubMenu {
	private List<RectButton> buttonList;	

	public SubMenu() {
		buttonList = new ArrayList<RectButton>();
	}
	
	public void add(RectButton btn) {
		buttonList.add(btn);
	}
	
	public void draw(float x, float y, float dH) {
		
		float dX, dY;
		dX = x;
		dY = y;
		
		for(RectButton b : buttonList) {
			b.draw(dX,dY,dH, false);
			
			dY += dH+1;
		}
	}
}
