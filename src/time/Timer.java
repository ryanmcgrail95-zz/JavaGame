package time;

import java.util.ArrayList;
import java.util.List;

import functions.Math2D;
import functions.MathExt;

public class Timer {
	private float time, maxTime;
	private static List<Timer> timerList = new ArrayList<>();
	private boolean isActive;
	
	
	
	// CONSTRUCTORS
		public Timer(float time, float timeMax) {
			this.time = time;
			this.maxTime = timeMax;
			isActive = true;
			
			timerList.add(this);
		}
		public Timer(float timeMax) {
			this.time = this.maxTime = timeMax;
			isActive = true;
			
			timerList.add(this);
		}

		
		
	// STATIC
		public static void tickAll(float deltaTime) {
			for(Timer t : timerList)
				t.tick(deltaTime);
		}
		public static void enableAll(float deltaTime) {
			for(Timer t : timerList)
				t.enable();
		}
		public static void disableAll(float deltaTime) {
			for(Timer t : timerList)
				t.disable();
		}

		
		
	// NON-STATIC
		// Ticking
		public void tick() {
			tick(1);
		}
		public void tick(float deltaTime) {
			if(isActive)
				time = MathExt.contain(0, time-deltaTime, time);
		}
		
		
		// Setting Time
		public void set(float time) {
			this.time = time;
		}
		public float get() {
			return time;
		}
		public void reset() {
			set(maxTime);
		}
			
		
		// Enabling/Disabling Automatic Ticking
		public void setActive(boolean state) {
			isActive = state;
		}
		public void enable() {
			setActive(true);
		}
		public void disable() {
			setActive(false);
		}
		
		
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
		public void println() {
			System.out.println("Timer: " + time + "/" + maxTime);
		}
		public float getFraction() {
			return time/maxTime;
		}
}
