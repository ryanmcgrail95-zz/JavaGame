package datatypes;

import functions.FastMath;
import functions.Math2D;
import functions.MathExt;

public class WeightedSmoothFloat {
	private static final float SQRT_TWO = (float) Math.sqrt(2);
	private float value, startValue, toValue, weightFrac, inflectionFrac, frac;
	private int numSteps;
	private float[][] points;

	
	public WeightedSmoothFloat(float value) { 
		restart(value,value,0,0,2);
	}
	public WeightedSmoothFloat(float start, float to, float weight, float inflection, int numSteps) { 
		restart(start,to,weight,inflection,numSteps);
	}
	
	public void setValue(float newValue) {
		startValue = toValue = newValue;
		frac = 1;
	}
	
	public void restart(float startValue, float toValue) {
		restart(startValue,toValue,weightFrac,inflectionFrac,numSteps);
	}
	public void restart(float startValue, float toValue, float weightFrac, float inflectionFrac, int numSteps) {
		this.startValue = startValue;
		this.toValue = toValue;
		this.weightFrac = weightFrac;
		this.inflectionFrac = inflectionFrac;
		this.numSteps = numSteps;
		
		generatePoints();
		
		frac = 0;
	}
	public void flip() {
		float start = startValue, to = toValue;
		frac = 1-frac;
		startValue = to;
		toValue = start;
		
		if(inflectionFrac != -1)
			inflectionFrac = 1-inflectionFrac;
		else
			weightFrac *= -1;
		
		generatePoints();
	}
	
	private void generatePoints() {
		points = generatePoints(weightFrac,inflectionFrac,numSteps);
	}
	private float calcValue() {
		if(frac == 1)
			return toValue;
		else {
			float mid = frac*(numSteps-1), f;
			int lower = (int) Math.floor(mid),
				upper = (int) Math.ceil(mid);
			
			f = mid-lower;
			float ff = points[lower][1]*(1-f) + points[upper][1]*f;
			
			return startValue*(1-ff) + toValue*ff;
		}
	}



	public void step() {
		if(frac < 1)
			frac = Math.min(1,frac+1f/(numSteps-1));
		else
			frac = 1;
		
		value = calcValue();
	}
	
	
	public float get() {
		return value;
	}
	
	public float getTo() {
		return toValue;
	}
	
	// Point Generation Functions
		private float[] generateHyperbola(float width, float fracHeight) {
			return new float[] {width/2*fracHeight, width/2};
		}
		private float[][] generatePoints(float weightFrac, float inflectionFrac, int pointNum) {			
			float 
				startX = 0,
				totWidth = SQRT_TWO,
				inflectionPt = inflectionFrac*totWidth;
			
			float width, aConv;
			float a,b,c,d, x,y;
			float[] ab;
			float[][] points;
			
			if(inflectionFrac == -1 || weightFrac == 0) {
			    width = totWidth;
			    inflectionFrac = -1;
			}
			else
			    width = totWidth*inflectionFrac;
			
			weightFrac = MathExt.contain(-1, weightFrac, 1)*1.5f;
			aConv = 1.41f;
			
			if(weightFrac >= 0) {
			    ab = generateHyperbola(width,weightFrac);
				a = ab[0];
				b = ab[1];
			    c = a*aConv;
			}
			else {
				ab = generateHyperbola(width,-weightFrac);
				a = ab[0];
				b = ab[1];
			    c = -a*aConv;
			}
			
			d = startX+width/2;
			
			float prevPt = 0;
			points = new float[pointNum][2];
			for(int i = 0; i < pointNum; i++) {
				x = SQRT_TWO*i/(pointNum-1);
			    if(prevPt <= inflectionPt && inflectionPt < x) {
			        weightFrac = weightFrac*-1;
			        startX = inflectionPt;
			
			        width = totWidth*(1-inflectionFrac);
			
			        if(weightFrac >= 0) {
					    ab = generateHyperbola(width,weightFrac);
						a = ab[0];
						b = ab[1];
					    c = a*aConv;
					}
					else {
						ab = generateHyperbola(width,-weightFrac);
						a = ab[0];
						b = ab[1];
					    c = -a*aConv;
					}
			
			        d = startX+width/2;
			    }
			    prevPt = x;
			    
			    if(weightFrac >= 0)
			        y = (float) (b*b*c - Math.sqrt(a*a*b*b*b*b + a*a*b*b*d*d - 2*a*a*b*b*d*x + a*a*b*b*x*x))/(b*b);
			    else
			        y = (float) (b*b*c + Math.sqrt(a*a*b*b*b*b + a*a*b*b*d*d - 2*a*a*b*b*d*x + a*a*b*b*x*x))/(b*b);
			    
			    points[i][0] = x;
			    points[i][1] = y;
			}
			
			float
				ang = 45,
				co = Math2D.calcLenX(ang),
				si = Math2D.calcLenY(ang);
			    
			for(int i = 0; i < pointNum; i++) {
				x = points[i][0];
				y = points[i][1];
				
				points[i][0] = x*co - y*si;
				points[i][1] = x*si + y*co;				
			}
			
			return points;
		}
	

	public void setFraction(float newFrac) {
		frac = newFrac;
	}
	public float getFraction() {
		return frac;
	}
	public boolean equals(float other) {return value == other;}
}
