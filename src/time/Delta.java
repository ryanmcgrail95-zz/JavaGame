package time;

import java.util.concurrent.Callable;

import datatypes.vec;
import functions.Math2D;
import gfx.GOGL;

public final class Delta {

	private static float fps, targetFPS, speedFrac;
	
	
	private Delta() {
		fps = targetFPS = 60;
		speedFrac = 1;
	}
	
	// STATIC
		public static float getFPS() {
			return fps;
		}
		public static float getSleepTime() {
			return 1;
		}
		
		public static void setTargetFPS(float newTargetFPS) {
			targetFPS = newTargetFPS;
		}
		public static void setSpeed(float newSpeedFrac) {
			speedFrac = newSpeedFrac;
		}
		
		public static long run(Callable<Integer> method) {
			return runMethod(method);
		}
	
	// NONSTATIC
		
		// Delta Time Methods
		public static float calcDeltaTime() {
			if(fps <= 0 || targetFPS <= 0)
				return 1;
			else
				return speedFrac*60/fps;
		}
		public static float convert(float timeVal) {
			return timeVal; //*calcDeltaTime();
		}
		public static vec convert(vec vector) {
			return vector; //.multe(calcDeltaTime());
		}
	
	
		// RUNNING METHODS
		public static long runMethod(Callable<Integer> method) {
			long totTime;
			
			
			
			return 0;
		}

		public static void setDelta(float delta) {
			
			if(delta == 0)
				return;
						
			fps = targetFPS/delta;
		}

		public static float getTargetFPS() {
			return targetFPS; // + Math2D.calcLenX(20,GOGL.getTime()*5);
		}
	
	
}
