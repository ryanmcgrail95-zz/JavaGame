package object.primitive;

import time.Delta;
import io.Mouse;
import datatypes.vec3;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.GL;
import gfx.GT;

public abstract class Positionable extends Drawable {
	
	private vec3 position, positionPrevious, velocity;
	private float direction, directionPrevious, zDirection;
	
	public Positionable(float x, float y, float z, boolean hoverable, boolean renderable) {
		super(hoverable, renderable);
		position = new vec3(x,y,z);
		positionPrevious = new vec3(x,y,z);
		velocity = new vec3(0,0,0);
	}
	
	public void destroy() {
		super.destroy();
		
		position.destroy();
		positionPrevious.destroy();
		velocity.destroy();
		
		position = positionPrevious = velocity = null;
	}
	
	
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
	

	public void transformTranslation() {GT.transformTranslation(position);}
	public void setPos(float x, float y) {
		x(x);
		y(y);
	}
	public void setPos(float x, float y, float z) {
		position.set(x,y,z);
	}
	public void setPos(Positionable other) {
		position.set(other.getX(),other.getY(),other.getZ());
	}
	public vec3 getPos() 	{return position;}
	
	
	public float getXPrevious() {return positionPrevious.x();}
	public float getYPrevious() {return positionPrevious.y();}
	public float getZPrevious() {return positionPrevious.z();}
	
	public void setXVelocity(float x) {
		velocity.x(x);
		updateDirection();
	}
	public void setYVelocity(float y) {
		velocity.y(y);
		updateDirection();
	}
	public void setZVelocity(float z) {
		velocity.z(z);
		updateDirection();
	}
	public void setVelocity(float nX, float nY) {
		setXVelocity(nX);
		setYVelocity(nY);
	}
	public void setVelocity(float x, float y, float z) {
		velocity.set(x,y,z);
		updateDirection();
	}

	public float getXVelocity() {return velocity.x();}
	public float getYVelocity() {return velocity.y();}
	public float getZVelocity() {return velocity.z();}

	public void addXVelocity(float nX) {velocity.x(velocity.x()+nX);}
	public void addYVelocity(float nY) {velocity.y(velocity.y()+nY);}
	public void addZVelocity(float nZ) {velocity.z(velocity.z()+nZ);}

	
	public void update() {
		super.update();
	}
	public void updatePosition() {
		positionPrevious.set(position);
		directionPrevious = direction;
		
		position.adde( Delta.convert(velocity) );
	}
	
	public void step(float dis, float dir) {step(dis,dir,0);}
	public void step(float dis, float dir, float dirZ) {
		vec3 coords = (vec3) Delta.convert(Math3D.calcPolarCoords(dis,dir,dirZ));
		position.adde( coords );
		coords.destroy();
	}
	
	public void stop() {
		velocity.set(0,0,0);
	}
	
	
	
	public boolean checkOnscreen() {
		return GL.getCamera().checkOnscreen(x(),y());
	}
	public float calcDepth() {
		return GL.getCamera().calcParaDistance(x(),y());
	}

	public float calcDis2D(Positionable other) {return calcPtDis(other.x(), other.y());}
	public float calcDis3D(Positionable other) {return calcPtDis(other.x(), other.y(), other.z());}
	public float calcPtDis(float pX, float pY) {return Math2D.calcPtDis(x(),y(), pX,pY);}
	public float calcPtDis(float pX, float pY, float pZ) {return Math3D.calcPtDis(x(),y(),z(), pX,pY,pZ);}
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
	
	private void updateDirection() {
		if(getXYSpeed() > .1)
			direction = velocity.getDirection();
	}
	public void setDirection(float direction) {
		velocity.setDirection(this.direction = direction);
	}
	public float getDirection() {return direction;}
	public float getDirectionPrevious() {return directionPrevious;}
	
	
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
