package object.primitive;

import collision.C3D;
import io.Mouse;
import resource.sound.Sound;
import time.Stopwatch;
import ds.lst.CleanList;
import gfx.GL;

public abstract class Updatable {
	private static CleanList<Updatable> instanceList = new CleanList<Updatable>("Inst");
	private static CleanList<Updatable> updateList = new CleanList<Updatable>("Upd");
	protected boolean doUpdates;
	protected String name = "";
	
	private static boolean timeUpdates = false;
	
	private boolean canSurviveTransition;
	private boolean isDestroyed;

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
		if(!isDestroyed) {
			isDestroyed = true;
			instanceList.remove(this);
			updateList.remove(this);
		}
	}
	
	public boolean isDestroyed() {
		return isDestroyed;
	}

		
	//Global Functions
		public static void transition() {
			//GL.start("Updatable.transition()");
			
			Drawable.printList();
			
			for(Updatable u : instanceList)
				if(!u.canSurviveTransition)
					u.destroy();
			for(Updatable u : instanceList)
				if(!u.canSurviveTransition)
					u.destroy();
			
			Drawable.printList();

			//GL.end("Updatable.transition()");
		}
		public static void updateAll() {
			//GL.start("Updatable.updateAll()");
			
			Stopwatch s = new Stopwatch();
						
			Mouse.update();
			Sound.update();			
			C3D.reset();
			
			for(Updatable u : updateList)
				if(u.doUpdates) {
					if(timeUpdates)
						s.start();
					u.update();
					
					if(timeUpdates) {
						s.stop();
						System.out.println(u.getName() + ": " + s.getMilli());
					}
				}
						
			Sound.clean();

			//GL.end("Updatable.updateAll()");
		}

		public static int getNumber() {
			return instanceList.size();
		}
		
		protected void disableUpdates() {
			updateList.remove(this);
		}

		protected void setDoUpdates(boolean should) 	{doUpdates = should;}
		protected boolean getDoUpdates(boolean should) 	{return doUpdates;}
		
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
