package time;

import java.util.ArrayList;
import java.util.List;

import object.primitive.Updatable;
import ds.lst.CleanList;
import functions.Math2D;
import functions.MathExt;

public class Timer extends Updatable {
	private float time, maxTime;
	private boolean isAutomatic;
	
	// CONSTRUCTORS
		public Timer(float startTime, float maxTime) {
			super();
			ini("Timer", startTime, maxTime);
		}
		public Timer(float maxTime) {
			super();
			ini("Timer-max",maxTime,maxTime);
		}
		
		private void ini(String name, float startTime, float maxTime) {			
			this.name = name;
			this.time = startTime;
			this.maxTime = maxTime;
			
			setSurviveTransition(true);
		}

		
		
		
	// NON-STATIC
		// Ticking
		public void update() {
			float sub = Delta.calcDeltaTime();			
			time = MathExt.contain(0, time-sub, time);
			if(isAutomatic)
				check();
		}
		
		public void enable() 		{setDoUpdates(true);}
		public void disable() 		{setDoUpdates(false);}
		public void toggle() 		{setDoUpdates(!getDoUpdates());}
		public void setEnable() 	{setDoUpdates(true);}
		public boolean isEnabled() 	{return doUpdates;}
		
		// Setting Time
		public void set(float time) {this.time = time;}
		public float get() 			{return time;}
		public void reset() 		{set(maxTime);}

		public float getFraction() {return 1 - get()/getMax();} 

		
		// Checking if Completed
		public boolean check() {
			if(time == 0) {
				reset();
				return true;
			}
			else
				 return false;
		}
		public boolean checkOnce() {
			return (time == 0);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("Timer: ");
			sb.append(get());
			sb.append("/");
			sb.append(getMax());
			
			return sb.toString();
		}
		public void println() {
			System.out.println(this);
		}
		
		public void setMax(float mT) 	{maxTime = mT;}
		public float getMax() 			{return maxTime;}
}
