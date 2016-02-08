package object.primitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import script.Variable;
import time.Delta;
import io.Mouse;
import ds.vec3;
import ds.lst.CleanList;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.GL;
import gfx.GT;

public abstract class Positionable extends Drawable {
	private static long globalID = 0;
	
	private static CleanList<Positionable> positionableList = new CleanList<Positionable>("Positionable");
	
	private List<Variable> varList = new ArrayList<Variable>();
	private Map<String, Variable> varMap = new HashMap<String, Variable>();
	
	private Variable 	x,y,z,
						vX,vY,vZ,
						xP,yP,zP;
	private boolean
		canMove = true;
	
	private float direction, directionPrevious, zDirection;
	private long id;
	
	public Positionable(float x, float y, float z, boolean hoverable, boolean renderable) {
		super(hoverable, renderable);
		
		this.x = addVar("x").set(x);
		this.y = addVar("y").set(y);
		this.z = addVar("z").set(z);
		
		this.vX = addVar("xVelocity").set(0);
		this.vY = addVar("yVelocity").set(0);
		this.vZ = addVar("zVelocity").set(0);
		
		this.xP = addVar("xPrevious").set(x);
		this.yP = addVar("yPrevious").set(y);
		this.zP = addVar("zPrevious").set(z);
		
		id = globalID++;
	}
	
	public void destroy() {
		if(isDestroyed())
			return;
		
		start("Positionable[" + name + "].destroy()");
		
		super.destroy();
		
		for(Variable v : varList)
			v.destroy();
		
		varList.clear();
			varList = null;
		varMap.clear();
			varMap = null;
			
		end("Positionable[" + name + "].destroy()");
	}
	
	
	public void x(float x)		{setX(x);}
	public void y(float y)		{setY(y);}
	public void z(float z)		{setZ(z);}
	public void setX(float x) 	{this.x.set(x);}
	public void setY(float y) 	{this.y.set(y);}
	public void setZ(float z) 	{this.z.set(z);}
		
	public float x()			{return getX();}
	public float y()			{return getY();}
	public float z()			{return getZ();}
	public float getX() 		{return (float) x.getNumber();}
	public float getY() 		{return (float) y.getNumber();}
	public float getZ() 		{return (float) z.getNumber();}

	public void addX(float aX) 	{x(x()+aX);}
	public void addY(float aY) 	{y(y()+aY);}
	public void addZ(float aZ) 	{z(z()+aZ);}
	

	public void transformTranslation() {GT.transformTranslation(x(),y(),z());}
	public void setPos(float x, float y) {
		x(x);
		y(y);
	}
	public void setPos(float x, float y, float z) {
		x(x);
		y(y);
		z(z);
	}
	public void setPos(Positionable other) {
		x(other.x());
		y(other.y());
		z(other.z());
	}	
	
	public float getXPrevious() {return (float)xP.getNumber();}
	public float getYPrevious() {return (float)yP.getNumber();}
	public float getZPrevious() {return (float)zP.getNumber();}
	
	
	public void vX(float x)	{setXVelocity(x);}
	public void vY(float y)	{setYVelocity(y);}
	public void vZ(float z)	{setZVelocity(z);}
	
	public void setXVelocity(float x) {
		vX.set(x);
		updateDirection();
	}
	public void setYVelocity(float y) {
		vY.set(y);
		updateDirection();
	}
	public void setZVelocity(float z) {
		vZ.set(z);
		updateDirection();
	}
	public void setVelocity(float nX, float nY) {
		setXVelocity(nX);
		setYVelocity(nY);
	}
	public void setVelocity(float x, float y, float z) {
		vX(x);
		vY(y);
		vZ(z);
		updateDirection();
	}

	public float vX() {return getXVelocity();}
	public float vY() {return getYVelocity();}
	public float vZ() {return getZVelocity();}
	public float getXVelocity() {return (float) vX.getNumber();}
	public float getYVelocity() {return (float) vY.getNumber();}
	public float getZVelocity() {return (float) vZ.getNumber();}

	public void addXVelocity(float nX) {vX(vX()+nX);}
	public void addYVelocity(float nY) {vY(vY()+nY);}
	public void addZVelocity(float nZ) {vZ(vZ()+nZ);}

	
	public void setCanMove(boolean canMove) {this.canMove = canMove;}
	public boolean getCanMove() {return canMove;}
	
	public void update() {
		super.update();
	}
	public void updatePosition() {
				
		//setPrevious();
		directionPrevious = direction;
	
		if(canMove) {
			addX( Delta.convert(vX()) );
			addY( Delta.convert(vY()) );
		}
		addZ( Delta.convert(vZ()) );
	}
	
	private void setPrevious() {
		xP.set(x());
		yP.set(y());
		zP.set(z());
	}
	
	public void walkingAlongWall(float wallX, float wallY) {
		float norm, dX = vX(), dY = vY(), amt, f = .2f;
		if((norm = Math2D.calcPtDis(0,0,vX(),vY())) != 0) {
			dX /= norm;
			dY /= norm;
		}
		
		if((norm = Math2D.calcPtDis(0,0,wallX,wallY)) != 0) {
			wallX /= norm;
			wallY /= norm;
		}
		
		amt = 1 - f*Math.abs(dX*wallX + dY*wallY);
		
		setXYSpeed(getXYSpeed() * amt);
	}
	
	public void step(float dis, float dir) {step(dis,dir,0);}
	public void step(float dis, float dir, float dirZ) {
		vec3 coords = (vec3) Delta.convert(Math3D.calcPolarCoords(dis,dir,dirZ));

		setPrevious(); //???
		
		addX( coords.x() );
		addY( coords.y() );
		addZ( coords.z() );

		coords.destroy();
	}
	
	public void stop() {
		setVelocity(0,0,0);
	}
	
	public static void stopAll() {
		for(Positionable p : positionableList)
			p.setVelocity(0,0,p.getZVelocity());
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
	
	//public float getSpeed() {return Math3D.calcPtDis(0,0,0,vX(),vY(),vZ());}	


	public void containXYSpeed(float maxSpeed) {	
		if(getXYSpeed() > maxSpeed)
			setXYSpeed(maxSpeed);
	}
	/*public void containSpeed(float maxSpeed) {
		float spd, f;
		spd = getSpeed();
				
		if(spd == 0)
			return;
		
		f = (float) Math.sqrt(MathExt.contain(0, spd, maxSpeed)/spd);
		
		vX( f*vX() );
		vY( f*vY() );
		vZ( f*vZ() );
	}*/
	
	public float getXYSpeed() {return Math2D.calcPtDis(0,0,vX(),vY());}
	public void setXYSpeed(float newSpd) {
		vX( Math2D.calcLenX(newSpd,direction) );
		vY( Math2D.calcLenY(newSpd,direction) );
	}
	public void addXYSpeed(float addAmt) {
		setXYSpeed(getXYSpeed() + addAmt);
	}
	
	private void updateDirection() {
		if(getXYSpeed() > .1)
			direction = calcDirection();
	}
	public void setDirection(float direction) {
		this.direction = direction;
		
		float spd = getXYSpeed();

		vX( Math2D.calcLenX(spd,direction) );
		vY( Math2D.calcLenY(spd,direction) );		
	}
	public float getDirection() {return direction;}
	public float getDirectionPrevious() {return directionPrevious;}
	
	private float calcDirection() {
		return Math2D.calcPtDir(0,0,vX(),vY());
	}
	
	// CHECKING
	public boolean collideCircle(float x, float y, int r) {return checkCircle(x,y,r,true);}
	public boolean checkCircle(float x, float y, float r) {return checkCircle(x,y,r,false);}
	public boolean checkCircle(float x, float y, float r, boolean eject) {
		float dis, dir;
		dis = calcPtDis(x(),y());
		
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
	
	public Variable addVar(String name) {
		Variable v = new Variable(name, false,true);
		varMap.put(name, v);
		varList.add(v);
		return v;
	}
	
	public Variable setVar(String name, double value) {return varMap.get(name).set(value);}
	public Variable setVar(String name, String value) {return varMap.get(name).set(value);}
	public Variable setVar(String name, Object value) {return varMap.get(name).set(value);}

	public Variable getVar(String name) {
		return varMap.get(name);
	}
}
