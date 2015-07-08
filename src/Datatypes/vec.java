package Datatypes;

import functions.Math2D;

public abstract class vec {
	protected float array[];
	protected final int SIZE;
	
	protected vec(int size) {
		SIZE = size;
		array = new float[size];
		
		for(int i = 0; i < size; i++)
			array[i] = 0;
	}
	
	protected vec(int size, float... values) {
		SIZE = size;
		array = new float[size];
		
		set(values);
	}
	
	public final void set(int index, float value) {
		if(index < 0 || index >= SIZE)
			return;
		else
			array[index] = value;
	}
	
	protected final void set(float... values) {
		if(values.length == SIZE)
			for(int i = 0; i < SIZE; i++)
				set(i,values[i]);
	}
	
	protected final void set(vec other) {
		if(other.SIZE == SIZE)
			set(other.array);
	}
	
	public final float get(int index) {
		if(index < 0 || index >= SIZE)
			return 0;
		else
			return array[index];
	}
	
	public final float len() {
		return Math2D.calcLen(array);
	}
	
	
	// SELF-MODIFYING
	protected vec norme() {
		return dive(len());
	}
	protected vec adde(float val) {		
		for(int i = 0; i < SIZE; i++)
			set(i, array[i]+val);
		
		return this;
	}
	protected vec sube(float val) {		
		for(int i = 0; i < SIZE; i++)
			set(i, array[i]-val);
		
		return this;
	}
	protected vec adde(vec other) {
		if(other.SIZE == SIZE)
			for(int i = 0; i < SIZE; i++)
				set(i, array[i]+other.array[i]);
		
		return this;
	}
	protected vec sube(vec other) {
		if(other.SIZE == SIZE)
			for(int i = 0; i < SIZE; i++)
				set(i, array[i]-other.array[i]);
		
		return this;
	}
	
	protected vec multe(float val) {		
		for(int i = 0; i < SIZE; i++)
			set(i, array[i]*val);
		
		return this;
	}
	protected vec dive(float val) {
		if(val == 0)
			return this;
		else
			return multe(1/val);
	}
	protected float dot(vec other) {
		float value = 0;
		
		if(other.SIZE == SIZE)
			for(int i = 0; i < SIZE; i++)
				value += array[i]*other.array[i];
		
		return value;
	}
		

	public final void print() {
		for(int i = 0; i < SIZE; i++) {
			if(i != 0)
				System.out.print(' ');
			System.out.print(get(i));
		}
	};
	public final void println() {
		print();
		System.out.print("\n");
	}
	
	public final float[] getArray() {
		float[] array = new float[SIZE];
		
		for(int i = 0; i < SIZE; i++)
			array[i] = get(i);
		
		return array;
	}
}