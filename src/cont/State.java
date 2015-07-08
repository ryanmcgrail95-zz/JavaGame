package cont;

public final class State {
	
	private static final int ST_NONE = -1, ST_TALKING = 0;
	private static int state;
	
	

	private State() {
	}

	
	public static void set(int newState) {
		state = newState;
	}
	public static int get() {
		return state;
	}
}
