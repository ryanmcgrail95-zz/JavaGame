package datatypes;

import java.util.List;

import functions.Math2D;

public final class vec2 extends vec {	
	public vec2() {super(2);}
	public vec2(float a, float b) {super(2, a, b);}
	public vec2(vec other) {
		super(2);
		set(other);
	}
		
	
	/*public static float[][] convert(List<vec2> list) {
		int si = list.size();
		float[][] outArray = new float[si][2];
		
		for(int i = 0; i < si; i++)
			outArray[i] = list.get(i).array;
		
		return outArray;
	}*/
}