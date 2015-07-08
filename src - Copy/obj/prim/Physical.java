package obj.prim;
import Datatypes.SortedList;
import obj.env.FloorBlock;
import func.Math2D;
import gfx.GOGL;


public abstract class Physical extends Drawable {
	private static SortedList<Physical> physicalList = new SortedList<Physical>();
	protected final static float CHAR_SPEED = 1, MAX_SPEED = CHAR_SPEED*2.6f; //CHAR_SPEED*1.5
	protected final static float GRAVITY_ACCEL = .8f, JUMP_SPEED = Math2D.calcSpeed(24,GRAVITY_ACCEL);
	
	protected float x, xP, y, yP, z, zP;
	private float xVel, yVel, zVel, direction;
	
	protected boolean inAir, onFloor;
	protected FloorBlock curFloor = null;
	protected float floorZ = 0;
	//TYPE VARIABLES
	protected final static byte T_NONE = -1, T_ITEM = 0, T_PLAYER = 1;
	protected byte type = T_NONE;
	
	protected float size;
	
	//MOVING VARIABLES
	protected boolean autoXYVel = true;	
	
	
	public Physical(float nX, float nY, float nZ) {
		super(true);
		
		x = xP = nX;
		y = yP = nY;
		z = zP = nZ;
		
		xVel = yVel = zVel = 0;
		
		physicalList.add(this);
	}
	
	public void destroy() {
		super.destroy();
		
		physicalList.remove(this);
	}
	
	public static void clean() {
		physicalList.clean();
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
	
	public void update(float deltaT) {
		super.update(deltaT);
		
		updatePosition();
		
		collideAll();
	}
	
	public void updatePosition() {
		xP = x;
		yP = y;
		zP = z;
		
		
		//Contain Velocity
		containXYSpeed();

				
		//Add Velocity to Position
		if(autoXYVel) {
			x += xVel;
			y += yVel;
		}
		z += zVel;
		
		zVel -= GRAVITY_ACCEL;
		
		direction = getDirection();
	}
	
	
	public float calcDis(Physical other) {
		return Math2D.calcPtDis(x, y, other.x, other.y);
	}
	
	public float calcDir(Physical other) {
		return Math2D.calcPtDir(x, y, other.x, other.y);
	}

	
	//ACCESSOR AND MUTATOR FUNCTIONS
		public void setX(float x) {
			this.x = x;
		}
		public float getX() {
			return x;
		}
			public float getXPrevious() {
				return xP;
			}
		
		public void setY(float y) {
			this.y = y;
		}
		public float getY() {
			return y;
		}
			public float getYPrevious() {
				return yP;
			}
		
		public void setZ(float z) {
			this.z = z;
		}
		public float getZ() {
			return z;
		}
			public float getZPrevious() {
				return zP;
			}
		
		public float getXVelocity() {
			return xVel;
		}
		public float getYVelocity() {
			return yVel;
		}
		public float getZVelocity() {
			return zVel;
		}
		
		public boolean getInAir() {
			return inAir;
		}

		public static float getGravity() {
			return GRAVITY_ACCEL;
		}
		
		
		public void didCollideFloor(float floorZ) {
			boolean isItem = (type == T_ITEM);

		    /*if(object_get_parent(object_index) == parPlayer)
		        if(enterPipe == 3)
		            enterPipe = 0;*/
			    
			this.floorZ = floorZ;
			onFloor = true;
			
			z = floorZ;

			land();
			        
			/*if(type == T_PLAYER)
			    if(global.currentAction == "stomp_drop" || global.currentAction == "stomp_up")
			    { 
			        if(stompTimer == -1) {
			            sound_play(sndStomp12);
			            stompTimer = 5;
			            global.cameraUp = 2;
			        }
			        if(stompTimer == 0) {
			            sprite = sprStill;
			            global.currentAction = "";
			            zRotate = 0
			        }
			        if(stompTimer == 3)
			            global.cameraUp = 0;
			    }*/
		}
		
		
		public void setCurrentFloor(FloorBlock floorBlock) {
			this.curFloor = floorBlock;
		}
		
		public FloorBlock getCurrentFloor() {
			return curFloor;
		}
		public float getFloorZ() {
			return floorZ;
		}
		
		public boolean isPlayer() {
			return (type == T_PLAYER);
		}
		public boolean isItem() {
			return (type == T_ITEM);
		}
		
		public abstract void land();
		
		protected boolean collideSize(Physical p) {
			return (calcDis(p) <= size+p.size);
		}
		
		public boolean collideLine(float x1, float y1, float x2, float y2) {
			boolean didCol;
			float vX, vY, lineDis, segDis, curDir, wallDir, outDir;
			
			vX = xVel;
			vY = yVel;
			
			lineDis = Math2D.calcLineDis(x, y, x1, y1, x2, y2, false);
			segDis = Math2D.calcLineDis(x, y, x1, y1, x2, y2, true);
			
			curDir = Math2D.calcLineDir(x,y,x1,y1,x2,y2,false);
			wallDir = Math2D.calcLineDir(x-10000*vX,y-10000*vY,x1,y1,x2,y2,false);
			
			//if(Math.abs(Math2D.calcAngDiff(wallDir,direction)) >= 90)
			outDir = wallDir+180;
			
			
			didCol = (segDis <= size);
			
			if(didCol) {	
				x += Math2D.calcLenX(size-lineDis,outDir);
				y += Math2D.calcLenY(size-lineDis,outDir);
			}
			
			return didCol;
		}
		
		public boolean collideSegment(float x1, float y1, float x2, float y2) {
			boolean didCol;
			float dis, dir;
			
			dis = Math2D.calcLineDis(x, y, x1, y1, x2, y2, true);
			didCol = (dis <= size);
			
			if(didCol) {
				dir = Math2D.calcLineDir(x,y,x1,y1,x2,y2,true)+180;
				
				x += Math2D.calcLenX(size-dis,dir);
				y += Math2D.calcLenY(size-dis,dir);
			}
			
			return didCol;
		}
		
		private float calcCamDis() {
			return GOGL.calcPerpDistance(x,y);
		}
		
		
	// Velocity Functions
		public void setVelocity(float nV) {
			setXVelocity(nV);
			setYVelocity(nV);
			setZVelocity(nV);
		}
		public void setVelocity(float nX, float nY) {
			setXVelocity(nX);
			setYVelocity(nY);
		}
		public void setXVelocity(float nX) {
			xVel = nX;
		}
		public void setYVelocity(float nY) {
			yVel = nY;
		}
		public void setZVelocity(float nZ) {
			zVel = nZ;
		}
		public void setXYSpeed(float newSpd) {
			float dir = getDirection();
			
			xVel = Math2D.calcLenX(newSpd, dir);
			yVel = Math2D.calcLenY(newSpd, dir);
		}
		public float getXYSpeed() {
			return Math2D.calcLen(xVel,yVel);
		}
		public void addXYSpeed(float addAmt) {
			setXYSpeed(getXYSpeed() + addAmt);
		}
		public float getSpeed() {
			return Math2D.calcLen(xVel,yVel,zVel);
		}
		
		
		public void containXYSpeed() {
			float spd, f;
			spd = getXYSpeed();
					
			if(spd == 0)
				return;
			
			f = (float) (Math2D.contain(0, spd, MAX_SPEED)/spd);
			xVel *= f;
			yVel *= f;
		}
		public void containSpeed() {
			float spd, f;
			spd = getSpeed();
					
			if(spd == 0)
				return;
			
			f = (float) Math.sqrt(Math2D.contain(0, spd, MAX_SPEED)/spd);
			xVel *= f;
			yVel *= f;
			zVel *= f;
		}

		public void setDirection(float newDir) {
			float xySpd;
			xySpd = getXYSpeed();
			
			xVel = Math2D.calcLenX(xySpd, newDir);
			yVel = Math2D.calcLenY(xySpd, newDir);
		}
		public float getDirection() {
			if(getXYSpeed() == 0)
				return direction;
			else
				return Math2D.calcPtDir(0,0,xVel,yVel);
		}
		
		
		public void centerCamera(float camDis, float camDir, float camZDir) {
			float cX, cY, cZ, camX, camY, camZ;
			cX = x;
			cY = y;
			cZ = floorZ+32;
			camX = cX + Math2D.calcPolarX(camDis, camDir+180, camZDir);
			camY = cY + Math2D.calcPolarY(camDis, camDir+180, camZDir);
			camZ = cZ + Math2D.calcPolarZ(camDis, camDir, camZDir+180);
			
			//Graphics3D.setProjection(20, 20, 0, 0, 0, 0);
			GOGL.setProjectionPrep(camX, camY, camZ,  cX, cY, cZ);
		}
}
