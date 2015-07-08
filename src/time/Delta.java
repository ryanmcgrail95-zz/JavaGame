package time;

import java.util.concurrent.Callable;

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
		private static float calcDeltaTime() {
			if(fps <= 0 || targetFPS <= 0)
				return 1;
			else
				return speedFrac*targetFPS/fps;
		}
		private static float convert(float timeVal) {
			return calcDeltaTime()*timeVal;
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
	
	
}
