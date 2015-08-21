package gfx;


import object.actor.Player;
import object.primitive.Drawable;
import object.primitive.Updatable;
import phone.SmartPhone;
import sts.Stat;
import time.Timer;
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
			else if(!SmartPhone.isActive()){
				Controller.draw();
				Messages.draw();
				Player.getInstance().getInventory().draw();
				Player.getInstance().getStat().draw();
				
				Stat.drawOverheads();
				
				
				float dX,dY;
				dX = 200;
				dY = 0;
				GLText.drawString(dX,dY, "Drawable: " + Drawable.getOnscreenNumber() + "/" + Drawable.getNumber()); dY += 20;
				GLText.drawString(dX,dY, "Updatable: " + Updatable.getNumber()); dY += 20;
				GLText.drawString(dX,dY, "Timers: " + Timer.getNumber()); dY += 20;
			}
	}

	public static void enable() {
		isEnabled = true;
	}
	public static void disable() {
		isEnabled = false;
	}
}
