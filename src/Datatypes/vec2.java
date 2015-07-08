package Datatypes;

import functions.Math2D;

public final class vec2 extends vec {
	private float array[];
	
	
	public vec2() {
		super(2);
	}
	
	public vec2(float a, float b) {
		super(2, a, b);
	}

	public vec2(vec2 other) {
		super(2);		
		set(other);
	}
		
	public void set(vec2 other) {
		super.set(other);
	}
		
	public vec2 copy() {
		return new vec2(this);
	}
	
	public vec2 norm() {
		vec2 v = new vec2(this);

		return (vec2) v.norme();
	}
		
	// Operators
	public vec2 add(vec2 other) {
		vec2 newV = new vec2(this);
		newV.adde(other);
		
		return newV;
	}
	
	public float dot(vec2 other) {
		return super.dot(other);
	}
	
	// ACCESSOR/MUTATOR
	public float x() {
		return get(0);
	}
	
	public float y() {
		return get(1);
	}
}