package datatypes;

import functions.MathExt;

public class SmoothFloat {
	private float value, startValue, toValue, weightFactor, velocity = 1;
	private float curStep, numSteps;
	
	public SmoothFloat(float value) { 
		start(value,value,0,0);
	}
	public SmoothFloat(float startValue, float toValue, float numSteps) {
		start(startValue,toValue,numSteps,0);
	}
	public SmoothFloat(float startValue, float toValue, float numSteps, float weightFactor) {
		start(startValue,toValue,numSteps,weightFactor);
	}
	
	
	public void setAll(float value) {
		this.value = this.startValue = this.toValue = value;
	}
	
	public void start(float startValue, float toValue) {
		start(startValue,toValue,numSteps,weightFactor);
	}
	public void start(float startValue, float toValue, float numSteps, float weightFactor) {
		this.startValue = value = startValue;
		this.toValue = toValue;
		this.weightFactor = weightFactor;
		this.numSteps = numSteps;
		this.curStep = 0;
		this.velocity = 1;
	}
	

	//public void setVelocity() {velocity *= -1;}
	public void setVelocitySign(float sign) {velocity = sign*Math.abs(velocity);}
	public void flipVelocity() {velocity *= -1;}
	public void setVelocity(float velocity) {this.velocity = velocity;}
	public float getVelocity() {return velocity;}


	public void set(float value) {this.value = value;}
	public void setTo(float toValue) {this.toValue = toValue;}
	public void setWeight(float weightFactor) {this.weightFactor = weightFactor;}

	public float get() {return value;}
	public float getTo() {return toValue;}
	public float getWeight() {return weightFactor;}
	
	public void step() {
		if(!isDone()) {
			curStep += velocity;

			
			float useWeight = (velocity > 0 ? weightFactor : -weightFactor);
			
			float f = (float) MathExt.contain(0,curStep/numSteps,1);
			f = (float) Math.pow(f, (useWeight < 0 ? -1/useWeight : useWeight) );

			
			System.out.println();
			System.out.println(f);
			System.out.println(weightFactor);
			System.out.println(useWeight);
			System.out.println();

			value = (1-f)*startValue + f*toValue;
			
			containStep();
		}
	}
	
	private void containStep() {
		if(velocity > 0)
			curStep = MathExt.min(curStep,numSteps);
		else
			curStep = MathExt.max(curStep,0);
	}
	public boolean isDone() {
		return (velocity > 0) ? curStep == numSteps : curStep == 0;
	}
	public boolean equals(float other) {return value == other;}
}
