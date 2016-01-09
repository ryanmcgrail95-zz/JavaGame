package object.primitive;
import datatypes.vec3;
import datatypes.lists.CleanList;
import object.environment.FloorBlock;
import resource.model.Model;
import time.Delta;
import functions.FastMath;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;


public abstract class Physical extends Positionable {
	private static CleanList<Physical> physicalList = new CleanList<Physical>("Phys");
	//protected final static float CHAR_SPEED = 1, MAX_SPEED = CHAR_SPEED*2.6f; //CHAR_SPEED*1.5
	protected final static float GRAVITY_ACCEL = .8f, //.5f
			JUMP_SPEED = Math2D.calcSpeed(32,GRAVITY_ACCEL);
	protected float maxSpeed;
		
	protected boolean inAir;
	protected FloorBlock curFloor = null;
	protected float floorZ = 0, toFloorZ = 0;
	//TYPE VARIABLES
	protected final static byte T_NONE = -1, T_ITEM = 0, T_PLAYER = 1;
	protected byte type = T_NONE;
	
	protected float size;
	
	//MOVING VARIABLES
	protected boolean autoXYVel = true;	
	
	
	public Physical(float x, float y, float z) {
		super(x,y,z,true,false);
		
		maxSpeed = MathExt.INFINITY;
		
		physicalList.add(this);
	}
	
	public void destroy() {
		super.destroy();
		
		physicalList.remove(this);
	}
	
	public abstract boolean collide(Physical other);	
	protected boolean collideAll() {
		boolean didCol = false;

		Physical p;
		for(int i = 0; i < physicalList.size(); i++) {
			p = physicalList.get(i);
			
			didCol = didCol || p.collide(this);
		}
		
		Environmental.collideAll(this);
		return didCol;
	}
	
	public void update() {
		super.update();
		
		updatePosition();
		collideAll();
		
		floorZ = MathExt.to(floorZ, toFloorZ, 5);
	}
	
	public void updatePosition() {
		//Contain Velocity
		containXYSpeed(maxSpeed);
				
		super.updatePosition();
		
		float zVel = getZVelocity(),
			gravAcc = GRAVITY_ACCEL;
		
		if(Math.abs(zVel) < 1)
			gravAcc *= .05 + .95*Math.abs(zVel);
		
		addZVelocity( -gravAcc );
	}

	
	//ACCESSOR AND MUTATOR FUNCTIONS		
		public boolean getInAir() {
			return inAir;
		}

		public static float getGravity() {
			return GRAVITY_ACCEL;
		}
		
		
		public void didCollideFloor(float floorZ) {
			boolean isItem = (type == T_ITEM);
			
			this.toFloorZ = floorZ;
			inAir = false;
			
			setZ(floorZ);

			land();
		}
		
		
		public void setCurrentFloor(FloorBlock floorBlock) {this.curFloor = floorBlock;}
		public FloorBlock getCurrentFloor() {return curFloor;}
		public float getFloorZ() {return floorZ;}
		
		public boolean isPlayer() {return (type == T_PLAYER);}
		public boolean isItem() {return (type == T_ITEM);}
		
		public abstract void land();
		
		protected boolean collideSize(Physical p) {
			return (calcDis2D(p) <= size+p.size);
		}
		
		protected boolean collideModel(Model m) {
			return false;
		}
		
		public boolean collideLine(float x1, float y1, float x2, float y2) {
			boolean didCol;
			float vX, vY, lineDis, segDis, curDir, wallDir, outDir;
			
			vX = getXVelocity();
			vY = getYVelocity();
			
			lineDis = Math2D.calcLineDis(x(), y(), x1, y1, x2, y2, false);
			segDis = Math2D.calcLineDis(x(), y(), x1, y1, x2, y2, true);
			
			curDir = calcLineDir(x1,y1,x2,y2,false);
			wallDir = Math2D.calcLineDir(x()-10000*vX,y()-10000*vY,x1,y1,x2,y2,false);
			
			//if(Math.abs(Math2D.calcAngDiff(wallDir,direction)) >= 90)
			outDir = wallDir+180;
			
			
			didCol = (segDis <= size);
			
			if(didCol)
				step(size-lineDis,outDir);
			
			return didCol;
		}
		
		public boolean collideSegment(float x1, float y1, float x2, float y2) {return collideSegment(x1,y1, x2,y2, false);}
		public boolean collideSegment(float x1, float y1, float x2, float y2, boolean oneWay) {
			boolean didCol;
			float dis, dir;
			
			dis = calcLineDis(x1, y1, x2, y2, true);
			didCol = (dis <= size);
			
			if(oneWay)
				if(FastMath.calcAngleDiff(calcLineDir(x1,y1,x2,y2,true),Math2D.calcPtDir(x1,y1,x2,y2)-90) > 90)
					didCol = false;
					
			if(didCol) {
				dir = calcLineDir(x1,y1,x2,y2,true)+180;
				
				step(size-dis,dir);
			}
			
			return didCol;
		}
		
		public boolean collideRectangle(float x, float y, float w, float h) {
			return (collideSegment(x+w,y-h,x-w,y-h,true) ||
					collideSegment(x-w,y+h,x+w,y+h,true) || 
					collideSegment(x-w,y-h,x-w,y+h,true) || 
					collideSegment(x+w,y+h,x+w,y-h,true));
		}
		
		
	// Velocity Functions

		
		//protected final static float CHAR_SPEED = 1, MAX_SPEED = CHAR_SPEED*2.6f; //CHAR_SPEED*1.5
}
