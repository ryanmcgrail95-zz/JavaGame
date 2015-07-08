package gui;

import java.util.ArrayList;
import java.util.List;

public class PauseMenu {
	
	private static List<RoundButton> buttonList;
	private static float x, y;
	private static int index;
	
	public static void ini() {
		
		index = -1;
		
		x = 200;
		y = 200;
		
		buttonList = new ArrayList<RoundButton>();
		
		SubMenu eqMenu = new SubMenu();
		eqMenu.add(new RectButton(null));
		eqMenu.add(new RectButton(null));
		eqMenu.add(new RectButton(null));
		
		buttonList.add(new RoundButton(eqMenu));
		buttonList.add(new RoundButton(null));
	}
	
	
	public static void draw() {
		
		float dX, dY, dR;
		int i;
		dX = x;
		dY = y;
		dR = 30;
		i = 0;
		
		for(RoundButton b : buttonList) {
			if(b.draw(dX,dY,dR, (i == index)))
				index = i;
			
			dY += 2*dR + dR/8;
			i++;
		}
	}
}
