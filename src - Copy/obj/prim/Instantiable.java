package obj.prim;

import java.util.ArrayList;
import java.util.List;

public abstract class Instantiable extends Updatable {
	private static List<Instantiable> instanceList = new ArrayList<Instantiable>();
	private static List<Instantiable> instantiableBuffer = new ArrayList<Instantiable>();
	private static List<Instantiable> instantiableClear = new ArrayList<Instantiable>();
	
	
	public Instantiable() {
		instanceList.add(this);
	}

	public void update(float deltaT) {
		//super.update(deltaT);
	}
	
	public void destroy() {
		super.destroy();
		
		instanceList.remove(this);
	}
	
	//Global Functions
		public static void updateAll(float deltaT) {
			for(Instantiable i : instanceList)
				i.update(deltaT);
		}
		
		public static void applyAddBuffer() {
			for(Instantiable u : instantiableBuffer)
				instanceList.add(u);
			
			instantiableBuffer.clear();
		}
		
		public static void applySubtractBuffer() {
			for(Instantiable u : instantiableClear)
				instanceList.remove(u);
			
			instantiableClear.clear();
		}

		public static int getNumber() {
			return instanceList.size();
		}
}
