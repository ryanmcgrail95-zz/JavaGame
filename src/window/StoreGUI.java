package window;

import java.util.ArrayList;
import java.util.List;

import object.actor.Player;
import io.Mouse;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;

public class StoreGUI extends Window {
	
	private static boolean isOpen = false;
	
	public StoreGUI(String name) {
		super(name + "'s General Store",320-240,90,480,280,false);
	}
	
	public static void open(String owner) {
		
		if(isOpen)
			return;
		
		StoreGUI w = new StoreGUI(owner);
		
		List<String> items = new ArrayList<String>();

		items.add("Empty Bucket");
		items.add("Logs");
		items.add("Empty Bowl");
		items.add("Empty Bowl");
		for(int i = items.size(); i < 27; i++)
			items.add("Bread");
		
		int bX,bY;
		for(int r = 0; r < 3; r++)
			for(int c = 0; c < 9; c++) {
				bX = 12 + c*(44+8);
				bY = 20 + r*(44+16);
				w.add(new ItemButton(items.get(r*9 + c),10,bX,bY));
			}
	}
	
	public byte draw() {
		float x,y;
		x = x()+SIDE_BORDER;
		y = y()+TOP_BORDER;
		GOGL.fillVGradientRectangle(x,y,w(),65, new RGBA(28,24,20), new RGBA(68,60,48), 10);
		GOGL.setColor(new RGBA(68,60,48));
		GOGL.fillRectangle(x,y+65,w(),h()-65-24);
		GOGL.fillVGradientRectangle(x,y+h()-24,w(),24, new RGBA(68,60,48), new RGBA(91,91,98), 10);
		
		if(super.draw() == W_CLOSE)
			isOpen = false;
		
		return -1;
	}
}
