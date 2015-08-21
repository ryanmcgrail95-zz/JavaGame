package object.primitive;

import gfx.Camera;
import io.IO;
import io.Keyboard;
import io.Mouse;
import resource.sound.Sound;
import time.Timer;
import cont.TextureController;
import datatypes.lists.CleanList;

public abstract class Updatable {
	private static CleanList<Updatable> updateList = new CleanList<Updatable>();
	protected boolean doUpdates;


	public Updatable() {
		doUpdates = true;
		updateList.add(this);
	}
	
	public abstract void update();
	
	public void destroy() {
		updateList.remove(this);
	}

		
	//Global Functions
		public static void updateAll() {
			Mouse.update();
			Sound.update();
			Timer.tickAll(1);
			
			
			for(Updatable u : updateList) {				
				if(u.doUpdates)
					u.update();
			}
						
			Sound.clean();
		}

		public static int getNumber() {
			return updateList.size();
		}	
		
		public void disableUpdates() {
			updateList.remove(this);
		}
		public void setDoUpdates(boolean should) {
			doUpdates = should;
		}
}
