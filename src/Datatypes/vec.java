package datatypes;

import datatypes.lists.CleanList;
import functions.Math2D;

public abstract class vec {
	private static CleanList<vec> vecList = new CleanList<vec>("Vecs");
	protected float array[];
	protected final int SIZE;
	
	protected vec(int size) {
		SIZE = size;
		array = new float[size];
		
		for(int i = 0; i < size; i++)
			array[i] = 0;
		
		vecList.add(this);
	}
	
	protected vec(int size, float... values) {
		SIZE = size;
		array = new float[size];
		
		set(values);
		
		vecList.add(this);
	}
	
	public void destroy() {
		vecList.remove(this);
		array = null;
	}
	
	public vec copy() {
		switch(SIZE) {
			case 2: return new vec2(this);
			case 3: return new vec3(this);
			case 4: return new vec4(this);
			default: throw new UnsupportedOperationException();
		}
	}
	
	public final void set(int index, float value) {
		if(index < 0 || index >= SIZE)
			throw new IndexOutOfBoundsException();
		array[index] = value;
	}
	public final void set(float... values) {
		if(values.length != SIZE)
			throw new IndexOutOfBoundsException();
		for(int i = 0; i < SIZE; i++)
			set(i,values[i]);
	}
	
	public final void set(vec other) {
		if(other.SIZE != SIZE)
			throw new UnsupportedOperationException();
		set(other.array);
	}
	
	public final float get(int index) {
		if(index < 0 || index >= SIZE)
			throw new IndexOutOfBoundsException();
		else
			return array[index];
	}
	
	public final float len() {return Math2D.calcLen(array);}

	
	
	// SELF-MODIFYING
	public vec norm() 	{return copy().norme();}
	public vec norme() 	{return dive(len());}

	public vec inve() {return multe(-1);}
	
	public vec adde(float val) {		
		for(int i = 0; i < SIZE; i++)
			set(i, array[i]+val);
		return this;
	}
	
	public vec adde(vec other) {
		if(other.SIZE != SIZE)
			throw new UnsupportedOperationException();

		for(int i = 0; i < SIZE; i++)
			set(i, array[i]+other.array[i]);
		
		return this;
	}

	public vec sube(float val) {return adde(-val);}
	public vec sube(vec other) {
		if(other.SIZE != SIZE)
			throw new UnsupportedOperationException();

		for(int i = 0; i < SIZE; i++)
			set(i, array[i]-other.array[i]);
		
		return this;
	}	
	public vec multe(float val) {
		for(int i = 0; i < SIZE; i++)
			set(i, array[i]*val);	
		return this;
	}
	
	public vec dive(float val) {
		if(val == 0)	throw new ArithmeticException();
		else			return multe(1/val);
	}
	
	public final float dot(float[] other) {		
		float value = 0;
		for(int i = 0; i < SIZE; i++)
			value += array[i]*( other[i] );
		
		return value;
	}
	public final float dot(vec other) {
		if(other.SIZE != SIZE)
			throw new UnsupportedOperationException();
		
		float value = 0;
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
	
	public static int getNumber() {
		return vecList.size();
	}
	
	
	public void x(float x) {set(0,x);}
	public void y(float y) {set(1,y);}
	public void z(float z) {set(2,z);}
	public void w(float w) {set(3,w);}
	public float x() {return get(0);}
	public float y() {return get(1);}
	public float z() {return get(2);}
	public float w() {return get(3);}
}