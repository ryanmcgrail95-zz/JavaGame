package object.primitive;

import gfx.Camera;
import io.IO;
import io.Keyboard;
import io.Mouse;
import resource.sound.Sound;
import time.Delta;
import time.Timer;
import cont.TextureController;
import datatypes.lists.CleanList;

public abstract class Updatable {
	private static CleanList<Updatable> instanceList = new CleanList<Updatable>();
	private static CleanList<Updatable> updateList = new CleanList<Updatable>();
	protected boolean doUpdates;


	public Updatable() {
		doUpdates = true;
		
		instanceList.add(this);
		updateList.add(this);
	}
	
	public abstract void update();
	
	public void destroy() {
		instanceList.remove(this);
		updateList.remove(this);
	}

		
	//Global Functions
		public static void updateAll() {
			Mouse.update();
			Sound.update();			
			
			for(Updatable u : updateList) {				
				if(u.doUpdates)
					u.update();
			}
						
			Sound.clean();
		}

		public static int getNumber() {
			return updateList.size();
		}
		
		protected void disableUpdates() {
			updateList.remove(this);
		}
		protected void setDoUpdates(boolean should) {
			doUpdates = should;
		}

		public static void unload() {
			for(Updatable u : instanceList)
				u.destroy();
			instanceList.clear();
		}
}
