package paper;

import object.primitive.Environmental;
import object.primitive.Physical;

public abstract class EventZone extends Environmental {
	private float x2, y2, z2;
	public EventZone(float x1, float y1, float z1, float x2, float y2, float z2) {
		super(x1, y1, z1, false, false);
		
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}

	public float x() {return (x1()+x2())/2;}
	public float y() {return (y1()+y2())/2;}
	public float z() {return (z1()+z2())/2;}

	public float x1() {return super.x();}
	public float y1() {return super.y();}
	public float z1() {return super.z();}
	public float x2() {return x2;}
	public float y2() {return y2;}
	public float z2() {return z2;}
	
	public boolean collide(Physical other) {
		if(other.checkBox(x1(),y1(),z1(), x2(),y2(),z2())) {
			event(other);			
			return true;
		}

		return false;
	}

	public abstract void event(Physical other);
	
	
	public void draw() {	
	}

	@Override
	public void add() {
	}
}
