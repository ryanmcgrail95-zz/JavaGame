package functions;

import java.util.ArrayList;
import java.util.List;

public final class Array {
	
	private Array() {}
	
	public static float[] concat(float[]... arrays) {
		int totSize = 0, index = 0;
		for(float[] array : arrays)
			totSize += array.length;
		
		float[] outArray = new float[totSize];
		for(float[] array : arrays) {
			System.arraycopy(array, 0, outArray, index, array.length);
			index += array.length;
		}
		
		return outArray;
	}
}
