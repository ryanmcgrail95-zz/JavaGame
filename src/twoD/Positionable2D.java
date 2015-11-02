package twoD;

import functions.Math2D;
import object.primitive.Drawable;

public abstract class Positionable2D extends Drawable {
	
	protected float x, y;
	private float xPrevious, yPrevious;
	protected float xVelocity, yVelocity;

	public Positionable2D(float x, float y, boolean hoverable, boolean renderable) {
		super(hoverable, renderable);
		
		this.x = x;
		this.y = y;
	}
	
	public void update() {
		xPrevious = x;
		yPrevious = y;
		
		x += xVelocity;
		y += yVelocity;
	}
	
	
	public float getX()	{return x;}
	public float getY() {return y;}
	
	public void setX(float x) 	{this.x = x;}
	public void setY(float y)	{this.y = y;}
	
	public void translateXY(float aX, float aY) {
		this.x += aX;
		this.y += aY;
	}
	public void translateRT(float radius, float theta) {
		this.x += Math2D.calcLenX(radius,theta);
		this.y += Math2D.calcLenY(radius,theta);
	}
	
	public float getXPrevious() {
		return xPrevious;
	}
	public float getYPrevious() {
		return yPrevious;
	}
	
	public float getXVelocity() {return xVelocity;}
	public float getYVelocity() {return yVelocity;}
	public void setXVelocity(float xVel)	{xVelocity = xVel;}
	public void setYVelocity(float yVel)	{yVelocity = yVel;}
}
