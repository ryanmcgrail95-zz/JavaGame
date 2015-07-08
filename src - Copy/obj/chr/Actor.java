package obj.chr;

import Datatypes.Inventory;
import Datatypes.vec3;
import Sounds.SoundController;

import com.jogamp.opengl.util.texture.Texture;

import obj.env.Floor;
import obj.env.Heightmap;
import obj.itm.Item;
import obj.prim.Physical;
import obj.prt.Dust;
import sts.Stat;
import cont.CharacterController;
import func.Math2D;
import gfx.GOGL;
import gfx.SpriteMap;
import gfx.TextureExt;

public abstract class Actor extends Physical {
	protected float height = 16;
	protected boolean isMoving = false, isSpeaking = false, canCollide = true;
	
	private Inventory inv;
	private Stat stat;

	private static final byte ANIM_STILL = 0, ANIM_RUN = 1, ANIM_JUMP = 2;
	private byte animation = ANIM_STILL;
	
	private float faceDirection;
	protected float camDirection;
	
	//Air Variables
	protected float inAirDir, inAirDir2, inAirSpeed, inAirSpeed2;
	
	protected final static int A_NOTHING = -1, A_SPEAKING = 0, A_WAIT = 1, A_TOPOINT = 2, A_HOLD = 3, A_HAMMER = 4, A_TOHELPER = 5;
	protected int action = A_NOTHING;
	
	//GO TO VARIABLES
	float goToX, goToY, goToZ, goToVel;
	String goToScript;
	
	protected final static float IMAGE_SPEED = .8f;
	private float upZ = 0;
	protected boolean stepSound = false;
	
		
	private SpriteMap map;
	
	//Image Variables
		protected TextureExt image = null;
		protected float imageIndex = 0, imageNumber = 6;
	
	//Spinning Variables
		//protected boolean isSpinning = false;
		protected final static float SPIN_VEL = 3f; // 2.5
		protected float spinTimer = 0, spinDirection = 0;
		protected int[] spinSound = null;
		
	//CHARACTER VARIABLES
		protected String chrctr;
	
	//JUMPING VARIABLES
		protected boolean isJumping = false;
		protected float jumpHold = 0;
	
	//FOLLOW VARIABLES
		private static final byte FOLLOW_NUM = 30;
		private float[] followXP = new float[FOLLOW_NUM],
				followYP = new float[FOLLOW_NUM],
				followD = new float[FOLLOW_NUM];
		private boolean[] followJP = new boolean[FOLLOW_NUM];
		
		
	public Actor(float x, float y, float z, String characterType) {
		super(x, y, z);
		
		faceDirection = camDirection = 0;
		
		size = 10;		
		chrctr = characterType;
		
		map = CharacterController.getCharacter(characterType).getSpriteMap();
		
		followIni();
		
		inv = new Inventory(this);
		stat = new Stat();
	}
	
	public void die() {
		dropItems();
		destroy();
	}

	//PARENT FUNCTIONS
		public void update(float deltaT) {
			super.update(deltaT);
						
			followUpdate();
			
			containXYSpeed();
			modelUpdate();
			
			motionUpdateFloor(deltaT);
						
			if(image != null) {
				Texture tex = image.getFrame(imageIndex);
				//Texture tex = image.getFrameEffect(imageIndex, TextureExt.E_PIXELIZE);
				shape.setTexture(tex);
			}
			
			updateShadow();
		}
				
	public boolean collide(Physical other) {
		
		return false;
	}	
	
		
	//ACCESSOR AND MUTATOR FUNCTIONS
		public void updateShadow() {
			float grZ, size;
			
			grZ = Floor.findGroundZ(x, y, z);
			size = (float) ((1 - .01*Math.abs(z - grZ))*1.2*8);
	
			//Move Shadow to Floor
			//shape.setShadowPosition(grZ+.3f);
		}
		
		public float getHeight() {
			return height;
		}
		
		public void followIni() {
			//Initializes the variables for an animated object, so other objects can follow it.	
		
			for(byte i = 0; i < FOLLOW_NUM; i += 1) {
			    followXP[i] = x;
			    followYP[i] = y;
			    followD[i] = getDirection();
			    followJP[i] = false;
			}
		}
	
		public boolean draw() {
			GOGL.transformClear();
			GOGL.transformTranslation(x,y,z+3*upZ);
			GOGL.transformRotationZ(faceDirection-45);
				GOGL.draw3DFrustem(size,size,1.5f*size,null,5);
			GOGL.transformClear();
			return true;
		}
		
		public void updatePosition() {
			super.updatePosition();
			
			setFaceDir(getDirection());
			//if(isPlayer())
			//	setCameraDir(camDirection-90+InputController.getWASDDir());
		}
		
		public void setFaceDir(float toDir) {
			
			if(Math.abs(Math2D.calcAngDiff(toDir,faceDirection)) > 160)
				faceDirection = toDir;
			else
				faceDirection += Math2D.calcSmoothTurn(faceDirection, toDir, 12);
		}
		
		public void setCameraDir(float toDir) {
			float camDir, dir;
			dir = camDirection - getDirection();
			dir = Math2D.snap(dir,90);
			
			// Rotate Camera Angle Based on Held Direction
			if(dir == 90 || dir == -90)
				camDir = toDir;
			else if(dir == 0 || dir == 180)
				camDir = toDir + .2f*Math2D.calcSmoothTurn(toDir,toDir-90+dir);
			else if(dir == 45 || dir == 135)
				camDir = toDir + .2f*Math2D.calcSmoothTurn(toDir,toDir-90+dir);
			else
				camDir = toDir - .2f*Math2D.calcSmoothTurn(toDir,180+toDir-90+dir);
			
			camDirection += Math2D.calcSmoothTurn(camDirection, camDir, 10);
		}
		
	//UPDATE FUNTIONS
		public void followUpdate() {
			//Updates the follow variables.
		
			for(byte i = FOLLOW_NUM-1; i > 0; i -= 1) {
			    followXP[i] = followXP[i-1];
			    followYP[i] = followYP[i-1];
			    followD[i] = followD[i-1];
			    followJP[i] = followJP[i-1];
			}
		
			    
			followXP[0] = x;
			followYP[0] = y;	
			followD[0] = getDirection();
			followJP[0] = false;
		}
	
		
	//UNIQUE FUNCTIONS
		
	public void motionUpdateFloor(float deltaT) {
		//if(object_index == objNPC)
		//	    if(action == "talk")
		//	        exit;
		/*if(object_get_parent(object_index) == parHelper)
		    if(global.currentAction == "kooper_5")
		        exit;*/
		
		//var parent;
		//parent = object_get_parent(object_index);
		if(isPlayer()) {
		    if(true)//sprite != mario_hammer && sprite != mario_hammer_up && sprite != mario_eating && sprite != mario_finger && sprite != mario_adjustcap)
		    {
		        float addIndex;
		        addIndex = (float) (deltaT*1.05*(getXYSpeed()/MAX_SPEED)*IMAGE_SPEED*.9);
		        
		        /*if(keyboard_check(vk_shift))
		        {
		            float rndIndex;
		            rndIndex = round(imageIndex);
		            
		            if(rndIndex != 2 && rndIndex != 5)
		                addIndex *= .25;
		            else
		                addIndex *= 2;
		        }*/
		        
		        imageIndex += addIndex;
		    }
		    /*else {
		        if(sprite == mario_hammer || sprite == mario_hammer_up)
		            image_index += delta*1;
		        else
		            image_index += delta*.25;
		    }*/
		}
		else//object_get_parent(parent) == parNonP || parent == parHelper)
		    imageIndex += deltaT*.6f*IMAGE_SPEED;//.6
		
		if(isPlayer()) {
		    //if(curAction != "hammer")
		        if(imageIndex > imageNumber) {
		            imageIndex -= imageNumber;
		            stepSound = false;
		        }
		}
		else
			if(imageIndex > imageNumber) {
	            imageIndex -= imageNumber;
	            stepSound = false;
	        }
	}
		
	public void modelUpdate() {
		/*if(isPlayer())
			if(isSpinning && spinTimer % 5 == 0)
				d3d_instance_create(x+lengthdir_x(5,direction+180),y+lengthdir_y(5,direction+180),z,objSmoke);*/
		
		
		if(checkSpriteRun()) {
	        if(imageIndex > 4 && imageIndex < 6 && !stepSound) {
	        	SoundController.playSound("Footstep");
	            stepSound = true;
	            new Dust(x+Math2D.calcLenX(5,getDirection()+180),y+Math2D.calcLenY(5,getDirection()+180),z, 0, true);
	        }
	        else if(imageIndex > 0 && imageIndex < 1)
	            	stepSound = false;
	        
			
			float toUpZ;
			toUpZ = Math.abs(Math2D.calcLenY(2,imageIndex*30));
			upZ += (toUpZ - upZ)/1.5;
			/*
		        if(image_index >= 0 && image_index < 2)
		            	flyZ += -flyZ/1.5;
		        else if((image_index >= 2 && image_index < 3) || (image_index >= 5 && image_index < 6))
		            	flyZ += (1 - flyZ)/1.5; //1
		        else if(image_index >= 3 && image_index < 5)
		            	flyZ += (2 - flyZ)/1.5; //2
			*/
		}
		else {
			upZ += -upZ/1.5;
			imageIndex = 0;
		}
	}
	
	
	
	public boolean checkSpriteRun() {
		return (animation == ANIM_RUN);
	}
	
	public void setSpriteStill() {
		animation = ANIM_STILL;
	}
	
	public void setSpriteRun() {
		animation = ANIM_RUN;
	}
	
	public void setSpriteJump() {
		animation = ANIM_JUMP;
	}
	
	public void setSpriteLand() {
	}
	
	
		
	public void land() {
	    setZVelocity(0);

	    if(inAir) {
	    	inAir = false;
	    	
			float cDir = GOGL.getCamDir();
	        new Dust(x+Math2D.calcLenX(7,cDir-90),y+Math2D.calcLenY(7,cDir-90),z+4, 0, false);
	        new Dust(x+Math2D.calcLenX(7,cDir+90),y+Math2D.calcLenY(7,cDir+90),z+4, 0, false);
	    }
	}
	
	public float getFollowX() {
		return followXP[FOLLOW_NUM-1];
	}
	public float getFollowY() {
		return followYP[FOLLOW_NUM-1];
	}
	public float getFollowDirection() {
		return followD[FOLLOW_NUM-1];
	}
	public boolean getFollowJump() {
		return followJP[FOLLOW_NUM-1];
	}
	
	protected void jump() {
		float jumpSpeed;
		/*switch(chrctr)
		{
		    case "mario": jumpSpeed = JUMP_SPEED; break;
		    
		    case "luigi": jumpSpeed = 1.25f*JUMP_SPEED; break;
		    
		    case "peach":
		    case "darkpeach": jumpSpeed = .4f*JUMP_SPEED; break;
		    
		    default: jumpSpeed = JUMP_SPEED; break;
		}*/
		
		jumpSpeed = JUMP_SPEED;
		
		setZVelocity(jumpSpeed);
		z += getZVelocity();
		//buttonPrevious[0] = 1;
		isJumping = true;
		SoundController.playSound("Jump");
		    
		
		inAir = true;
		setSpriteJump();
		imageIndex = 0;
		
		
		jumpHold = 1;
		/*if(heldVDirection == 0 && heldHDirection == 0)
		    inAirDir = -1;
		else
		    inAirDir = Math2D.calcPtDir(0,0,heldHDirection,heldVDirection);*/
		if(getXYSpeed() == 0)
			inAirDir = -1;
		else
			inAirDir = getDirection();
				   
		inAirSpeed = getXYSpeed();
	}
	
	// Inventory
	public Inventory getInventory() {
		return inv;
	}
	public void addItem(Item it) {
		inv.addItem(it);
	}
	private void dropItems() {
		inv.dropAll(new vec3(x,y,Heightmap.getInstance().getZ(x,y)));
	}
	
	public Stat getStat() {
		return stat;
	}
}