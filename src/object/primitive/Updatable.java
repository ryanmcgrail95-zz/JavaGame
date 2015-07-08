package object.primitive;

import gfx.Camera;
import io.IO;
import io.Mouse;
import resource.sound.SoundController;
import time.Timer;
import Datatypes.SortedList;
import cont.TextureController;

public abstract class Updatable {
	private static SortedList<Updatable> updateList = new SortedList<Updatable>();
	protected boolean doUpdates;


	public Updatable() {
		doUpdates = true;
		updateList.add(this);
	}
	
	public abstract void update(float deltaT);
	
	public void destroy() {
		updateList.remove(this);
	}
	
	public static void clean() {
		updateList.clean();
	}
		
	//Global Functions
		public static void updateAll(float deltaT) {
			IO.update();
			TextureController.update(deltaT);
			Camera.update();
			Timer.tickAll(1);
			
			
		    						
			Updatable u;
			for(int i = 0; i < updateList.size(); i++) {
				u = updateList.get(i);
				
				if(u.doUpdates)
					u.update(deltaT);
			}
			
			Drawable.sort();
			
			Drawable.clean();
			Updatable.clean();
			Physical.clean();
			Environmental.clean();
			SoundController.clean();
		}

		public static int getNumber() {
			return updateList.size();
		}	
		
		public void setDoUpdates(boolean should) {
			doUpdates = should;
		}
}
