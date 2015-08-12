package object.environment;

import functions.MathExt;
import object.actor.Actor;
import object.primitive.Environmental;
import time.Timer;

public abstract class ResourceMine extends Environmental {
	
	protected Timer refillTimer;
	protected int useTime = 60, progress = -1, progressMax = 60*5;

	
	public ResourceMine(float x, float y) {
		super(x,y,true,false);
		
		disableUpdates();
		
		refillTimer = new Timer(0,120);
	}
	
	public boolean checkShake() {
		int r = (int) (progress % useTime), s = 3;
		
		return (progress > -1) ? (r < s || r > useTime-s) : false;
	}
	
	public void draw() {
		updateUsage();
	}
	

	public boolean mine(Actor user) {
		if(isEmpty())
			return true;
		
		if(progress == -1) {
			progress = progressMax;
		}
		else {
			progress--;
			if(progress == -1) {
				progress = -1;
				refillTimer.reset();
				giveItem(user);
				
				return true;
			}
		}
			
		return false;
	}
	public void stopMining() {
		progress = -1;
	}
	

	public void updateUsage() {
		
		// If DONE

		/*else if(progressTimer.getActive()) {
			if(progress % useTime == 0)
				if(progressTimer.get() != progressTimer.getMax())
					giveItem();
		}*/
	}
	
	public boolean isEmpty() {
		return !refillTimer.checkOnce();
	}
	
	public abstract void giveItem(Actor user);
}
