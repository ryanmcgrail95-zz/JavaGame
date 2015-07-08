package time;

import java.util.concurrent.Callable;

public final class Delta {

	private static Delta instance;
	private float fps, targetFPS, speedFrac;
	
	
	private Delta() {
		fps = targetFPS = 60;
		speedFrac = 1;
	}
	
	// STATIC
		private static Delta getInstance() {
			if(instance == null)
				instance = new Delta();
			
			return instance;
		}
	
		public static float getFPS() {
			return getInstance().fps;
		}
		public static float getSleepTime() {
			return 1;
		}
		
		public static void setTargetFPS(float newTargetFPS) {
			getInstance().targetFPS = newTargetFPS;
		}
		public static void setSpeed(float newSpeedFrac) {
			getInstance().speedFrac = newSpeedFrac;
		}
		
		public static long run(Callable<Integer> method) {
			return getInstance().runMethod(method);
		}
	
	// NONSTATIC
		
		// Delta Time Methods
		private float calcDeltaTime() {
			if(fps <= 0 || targetFPS <= 0)
				return 1;
			else
				return speedFrac*targetFPS/fps;
		}
		private float convert(float timeVal) {
			return calcDeltaTime()*timeVal;
		}
	
	
		// RUNNING METHODS
		public long runMethod(Callable<Integer> method) {
			long totTime;
			
			
			
			return 0;
		}
	
	
}
