package gfx;


import object.actor.Player;
import sts.Stat;
import window.Window;
import cont.Messages;
import cont.Text;
import io.Controller;

public class Overlay {
	private static boolean isEnabled = true;
	
	public static void draw() {
		if(isEnabled)
			if(Text.isActive())
				Text.draw();
			else if(Window.isWindowOpen())
				Window.drawAll();
			else {
				Controller.draw();
				Messages.draw();
				Player.getInstance().getInventory().draw();
				Player.getInstance().getStat().draw();
				
				Stat.drawOverheads();
			}
	}

	public static void enable() {
		isEnabled = true;
	}
	public static void disable() {
		isEnabled = false;
	}
}
