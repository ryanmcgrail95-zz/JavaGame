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
	private static CleanList<Updatable> instanceList = new CleanList<Updatable>("Inst");
	private static CleanList<Updatable> updateList = new CleanList<Updatable>("Upd");
	protected boolean doUpdates;
	protected String name = "";
	
	private boolean canSurviveTransition;


	public Updatable() {
		doUpdates = true;
		
		canSurviveTransition = false;
			
		updateList.add(this);
		instanceList.add(this);
	}
	
	public void setSurviveTransition(boolean willSurvive) {
		this.canSurviveTransition = willSurvive;
	}
	
	public abstract void update();
	
	public void destroy() {
		instanceList.remove(this);
		updateList.remove(this);
	}

		
	//Global Functions
		public static void transition() {
			for(Updatable u : instanceList)
				if(!u.canSurviveTransition)
					u.destroy();
		}
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

		public static CleanList<Updatable> getList() {
			return instanceList;
		}

		public String getName() {
			return name;
		}
}
