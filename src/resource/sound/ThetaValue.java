package resource.sound;

import functions.Math2D;
import object.primitive.Updatable;
import time.Delta;

public class ThetaValue extends Updatable {
	private float
		value,
		valueCenter,
		valueAmplitude,
		theta,
		thetaVelocity;
	
	public ThetaValue(float valueCenter, float valueAmplitude, float theta, float thetaVelocity) {
		this.valueCenter = valueCenter;
		this.valueAmplitude = valueAmplitude;
		this.theta = theta;
		this.thetaVelocity = thetaVelocity;		
	}
	
	public float get() {
		return value;
	}

	@Override
	public void update() {
		theta += Delta.convert(thetaVelocity);
		value = valueCenter + Math2D.calcLenY(valueAmplitude, theta);
	}
}
