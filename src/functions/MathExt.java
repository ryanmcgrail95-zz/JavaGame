package functions;

public class MathExt {
	public static final float INFINITY = Float.MAX_VALUE;

	public static float rnd() {
		return (float) Math.random();
	}
	public static float rnd(float cap) {
		return cap*rnd();
	}
	public static float rnd(float mi, float ma) {
		return mi + rnd(ma-mi);
	}
	public static float rndSign() {
		float n;
		
		do
			n = Math.round(rnd(-1,1));
		while(n == 0);
		
		return n;
	}
	
	public static float sqr(float n) {
		return n*n;
	}
	
	public static float min(float... values) {
		float mi = values[0];
		
		for(int i = 1; i < values.length; i++)
			mi = Math.min(mi, values[i]);
		
		return mi;
	}
	public static int min(int... values) {
		int mi = values[0];
		
		for(int i = 1; i < values.length; i++)
			mi = Math.min(mi, values[i]);
		
		return mi;
	}
	
	public static float max(float... values) {
		float ma = values[0];
		
		for(int i = 1; i < values.length; i++)
			ma = Math.max(ma, values[i]);
		
		return ma;
	}
	public static int max(int... values) {
		int ma = values[0];
		
		for(int i = 1; i < values.length; i++)
			ma = Math.max(ma, values[i]);
		
		return ma;
	}
	
	public static float squareMean(float a, float b) {
		return (float) Math.sqrt(a*a + b*b);
	}
	
	// MEAN MEDIAN MODE
		public static float mean(float... values) {
			
			if(values.length == 0)
				return 0;
			
			float sum = 0;
			for(Float v : values)
				sum += v;
			
			return sum/values.length;
		}
		public static float median(float... values) {
			boolean didSwitch;
			int i, ii = values.length;
			float temp;
			
			do {
				didSwitch = false;
				
				for(i = 1; i < ii; i++)
					if(values[i] < values[i-1]) {
						didSwitch = true;
						
						temp = values[i];
						
						values[i] = values[i-1];
						values[i-1] = temp;
					}
				
				ii--;
			} while(didSwitch);
			
			return values[values.length/2];
		}

	
	public static boolean between(float mi, float value, float ma) {
		float tMi, tMa;
		if(mi < ma) {
			tMi = mi;
			tMa = ma;
		}
		else {
			tMi = ma;
			tMa = mi;
		}
		
		return (tMi <= value && value <= tMa);
	}

	public static float contain(float mi, float value, float ma) {
		float tMi, tMa;
		if(mi < ma) {
			tMi = mi;
			tMa = ma;
		}
		else {
			tMi = ma;
			tMa = mi;
		}
		
		return Math.max(tMi, Math.min(value, tMa));
	}

	public static float snap(float val, float grid) {
		return grid*Math.round(val/grid);
	}	
	public static float wrap(float mi, float value, float ma) {
		float diff = ma - mi;
		
		while(value < mi)	value += diff;
		while(value > ma)	value -= diff;
		
		return value;
	}
	
	public static float interpolate(float a, float b, float frac) {
		frac = contain(0, frac, 1);
		
		return a*(1-frac) + b*frac;
	}
}
