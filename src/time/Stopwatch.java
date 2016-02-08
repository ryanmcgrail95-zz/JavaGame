package time;

public class Stopwatch {
	public long startTime, endTime;
	
	public long getTime() {
		return System.nanoTime();
	}
	
	public void start() {
		startTime = getTime();
	}
	
	public void stop() {
		endTime = getTime();
	}
	public void stop(boolean println) {
		stop();
		if(println)
			System.out.println("Took " + getMilli() + " mS.");
	}
	
	public float getMilli() {
		return (endTime - startTime)/1000000f;
	}
	
	public void println() {
		System.out.println(getMilli());
	}
}
