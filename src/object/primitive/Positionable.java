package object.primitive;

import io.Mouse;
import datatypes.vec3;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;

public abstract class Positionable extends Drawable {
	
	private vec3 position, positionPrevious, velocity;
	private float direction, zDirection;
	
	public Positionable(float x, float y, float z, boolean hoverable, boolean renderable) {
		super(hoverable, renderable);
		position = new vec3(x,y,z);
		positionPrevious = new vec3(x,y,z);
		velocity = new vec3(0,0,0);
	}
	
	public void destroy() {super.destroy();}
	
	
	public void x(float x)		{position.x(x);}
	public void y(float y)		{position.y(y);}
	public void z(float z)		{position.z(z);}
	public void setX(float x) 	{position.x(x);}
	public void setY(float y) 	{position.y(y);}
	public void setZ(float z) 	{position.z(z);}
		
	public float x()			{return position.x();}
	public float y()			{return position.y();}
	public float z()			{return position.z();}
	public float getX() 		{return position.x();}
	public float getY() 		{return position.y();}
	public float getZ() 		{return position.z();}

	public void addX(float aX) 	{position.x(position.x()+aX);}
	public void addY(float aY) 	{position.y(position.y()+aY);}
	public void addZ(float aZ) 	{position.z(position.z()+aZ);}
	

	public void transformTranslation() {GOGL.transformTranslation(position);}
	public void setPos(float x, float y) {
		x(x);
		y(y);
	}
	public void setPos(float x, float y, float z) {
		position.set(x,y,z);
	}
	public vec3 getPos() 	{return position;}
	
	
	public float getXPrevious() {return positionPrevious.x();}
	public float getYPrevious() {return positionPrevious.y();}
	public float getZPrevious() {return positionPrevious.z();}
	
	public void setXVelocity(float x) {velocity.x(x);}
	public void setYVelocity(float y) {velocity.y(y);}
	public void setZVelocity(float z) {velocity.z(z);}
	public void setVelocity(float nX, float nY) {
		setXVelocity(nX);
		setYVelocity(nY);
	}
	public void setVelocity(float x, float y, float z) {
		velocity.set(x,y,z);
	}

	public float getXVelocity() {return velocity.x();}
	public float getYVelocity() {return velocity.y();}
	public float getZVelocity() {return velocity.z();}

	public void addXVelocity(float nX) {velocity.x(velocity.x()+nX);}
	public void addYVelocity(float nY) {velocity.y(velocity.y()+nY);}
	public void addZVelocity(float nZ) {velocity.z(velocity.z()+nZ);}

	
	public void updatePosition() {
		positionPrevious.set(position);
		
		position.adde(velocity);
	}
	
	public void step(float dis, float dir) {step(dis,dir,0);}
	public void step(float dis, float dir, float dirZ) {
		position.adde( Math3D.calcPolarCoords(dis,dir,dirZ) );
	}
	
	public void stop() {
		velocity.set(0,0,0);
	}
	
	
	
	public boolean checkOnscreen() {
		if(Mouse.getRightMouse())
			return true;
		else
			return GOGL.getCamera().checkOnscreen(x(),y());
	}
	public float calcDepth() {
		if(Mouse.getRightMouse())
			return 0;
		else
			return GOGL.getCamera().calcParaDistance(x(),y());
	}

	public float calcDis(Positionable other) {return calcPtDis(other.x(), other.y());}
	public float calcPtDis(float pX, float pY) {return Math2D.calcPtDis(x(),y(), pX,pY);}
	public float calcLineDis(float x1, float y1, float x2, float y2, boolean segment) {return Math2D.calcLineDis(x(), y(), x1, y1, x2, y2, true);}
	
	public float calcDir(Positionable other) {return calcPtDir(other.x(), other.y());}
	public float calcPtDir(float pX, float pY) {return Math2D.calcPtDir(x(),y(), pX,pY);}
	public float calcLineDir(float x1, float y1, float x2, float y2, boolean segment) {return Math2D.calcLineDir(x(), y(), x1, y1, x2, y2, true);}	
	
	public float getSpeed() {return velocity.len();}	


	public void containXYSpeed(float maxSpeed) {
		float spd, f;
		spd = getXYSpeed();
				
		if(spd == 0)
			return;
		
		f = (float) (MathExt.contain(-maxSpeed, spd, maxSpeed)/spd);
		velocity.setXYLen(spd*f);
	}
	public void containSpeed(float maxSpeed) {
		float spd, f;
		spd = getSpeed();
				
		if(spd == 0)
			return;
		
		f = (float) Math.sqrt(MathExt.contain(0, spd, maxSpeed)/spd);
		velocity.multe(f);
	}
	
	public float getXYSpeed() {return velocity.xyLen();}
	public void setXYSpeed(float newSpd) {
		velocity.setXYLen(newSpd);
	}
	public void addXYSpeed(float addAmt) {
		setXYSpeed(getXYSpeed() + addAmt);
	}
	
	public void setDirection(float direction) {
		velocity.setDirection(this.direction = direction);
	}
	public float getDirection() {
		if(getXYSpeed() == 0)	return direction;
		else					return velocity.getDirection();
	}
	
	
	// CHECKING
	public boolean collideCircle(float x, float y, int r) {return checkCircle(x,y,r,true);}
	public boolean checkCircle(float x, float y, float r) {return checkCircle(x,y,r,false);}
	public boolean checkCircle(float x, float y, float r, boolean eject) {
		float dis, dir;
		dis = calcPtDis(x,y);
		
		if(!eject)
			return dis < r;
		else if(dis < r) {
			dir = calcPtDir(x,y)+180;
			step(r-dis,dir);
			return true;
		}
		else
			return false;
	}
}
