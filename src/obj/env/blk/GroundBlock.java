package obj.env.blk;

import functions.Math2D;
import gfx.CubeMap;
import gfx.GL;
import gfx.GT;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import object.primitive.Drawable;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.sound.Sound;

public class GroundBlock extends Environmental {	
	private static List<GroundBlock> groundBlockList = new ArrayList<GroundBlock>();
	private List<Blocklet> blockletList = new ArrayList<Blocklet>();
	private CubeMap myCubeMap = CubeMap.getMap("cmGroundBlock1");
	private final static Texture shadow = TextureController.getTexture("texBlockShadow");
	
	int sNum = 3;
	
	//Hammer
	private int requiredHammer = 0;
	
	//Destroy Variables
	private boolean wasDestroyed = false;
	private float destroyTimer = -1;
	
	private float size = 16, shSize = size*1.3f;
	
	
	
	public GroundBlock(float x, float y, float z) {
		super(x,y,z,false,false);
		
		/*shape = Shape.createBlock("GBlock", -size,-size,size,size,size,-size, CubeMap.getMap("cmGroundBlock1"));
		shape.addBlockShadow(0, 0, size, size);
		
		shape.setPosition(x, y, z+size);*/
		//shape.setShadowPosition(z+.3f);

		
		groundBlockList.add(this);
	}	
	
	private class Blocklet {
		private float bX, bY, bZ, bSize, vel, dir, zVel;
		private float bounceNum = 0;
		private float alpha = 1;
		
		private float rotVel, rot = 0;
		
		public Blocklet(float nx, float ny, float nz, float size, float dir) {
			super();
			
			this.bX = nx;
			this.bY = ny;
			this.bZ = nz;
			this.vel = .5f; //2
			this.dir = dir;
			this.zVel = 3f;
			this.rotVel = 5;
			this.bSize = size;
			
			/*shape = Shape.createBlock("Blocklet", -size,-size,size,size,size, -size, CubeMap.getMap("cmGroundBlocklet1"));
			shape.addBlockShadow(0, 0, 0, size);
			shape.setShadowPosition(z+.3f);*/

			blockletList.add(this);
		}
				
		public void draw() {
			GT.transformTranslation(bX, bY, bZ+bSize);
				GT.transformRotation(rot, 0, dir);
				GL.setAlpha(alpha);
				GL.draw3DBlock(-bSize,-bSize,bSize,bSize,bSize,-bSize,myCubeMap.getLeft().getTexture());
			GT.transformClear();
		}
		
		public void update() {
			bX += Math2D.calcLenX(vel, dir);
			bY += Math2D.calcLenY(vel, dir);
				
			int MAX_T = 200, ALPH_T = 150;
			
			if(destroyTimer < MAX_T-ALPH_T)
				alpha = 1;
			else
				alpha = (MAX_T-destroyTimer)/ALPH_T;
			
			if(bZ < z()) {
				//.6
				
				float fr = .8f;
				
				zVel = (float) (fr*Math.sqrt(Math.abs(zVel)));
				vel *= fr;
				bZ = z();
				
				bounceNum++;
			}
			if(bZ <= z() && bounceNum >= 3) {//Math.abs(zVel) < .1) {
				bZ = z();
				zVel = 0;
				vel = 0;
			}
			else {
				rot += rotVel;
				bZ += zVel;
				zVel -= Physical.getGravity()/2;
			}
		}

		public int calcDepth() {
			return (int) (100 * GL.getMainCamera().calcParaDistance(bX,bY));
		}
		
		//if(destroyTimer > 149)
	    //draw_set_alpha(abs(destroyTimer - 200)/50);
	}
	

	public static void hammerAll(Physical other) {
		for(GroundBlock g : groundBlockList)
			if(g.collide(other))
				g.activate();
	}
	
	public boolean collide(Physical other) {
		
		if(wasDestroyed)
			return false;
		
		//if(global.currentAction != "" || (z+24 < other.z || z > other.z+24))
		//    exit;
		

		return other.collideRectangle(x(),y(),size,size);
		
		/*if(didCollide) {
			if(wasDestroyed)
				return false;
			else
				wasDestroyed = true;
		}*/
	}
	
	public void activate() {
		wasDestroyed = true;
	}
	
	
	//Parent Functions
		public void destroy() {
			super.destroy();
			
			blockletList.clear();
			
			groundBlockList.remove(this);
		}
	
		public void update() {			
			if(wasDestroyed) {
				if(destroyTimer == -1) {	
					Sound.play("blockCrumble");
					
					float bX, bY, bZ, s = (32/sNum)/2;
					
					for(int varZ = 0; varZ < sNum; varZ++)
						for(int varY = 0; varY < sNum; varY++)
							for(int varX = 0; varX < sNum; varX++) {
								bX = x()-16 + varX*32/sNum;
								bY = y()-16 + varY*32/sNum;
								bZ = z() + varZ*32/sNum;
								
								new Blocklet(bX+s,bY+s,bZ+s, s, (float) (Math.random()*360));
							}
				}
					
				for(Blocklet b : blockletList)
					b.update();
				
				destroyTimer += 2.5;
				if(destroyTimer >= 200)
					destroy();
			}
			
		}
	
		@Override
		public void add() {}

		@Override
		public void draw() {
			if(!wasDestroyed) {
				transformTranslation();
				GL.draw3DFloor(-shSize,-shSize,shSize,shSize,.1f, shadow);
				GL.draw3DBlock(-size,-size,2*size,size,size,0, myCubeMap);
				GT.transformClear();
			}
			else if(!blockletList.isEmpty()) {
				sortBlocklets();
				for(Blocklet b : blockletList)
					b.draw();
			}
		}
		
		public void sortBlocklets() {
			blockletList.sort(Comparators.DEPTH);
		}
		
		public static class Comparators {
			public final static Comparator<Blocklet> DEPTH = new Comparator<Blocklet>() {
	            public int compare(Blocklet o1, Blocklet o2) {
	                return (int) -(o1.calcDepth() - o2.calcDepth());
	            }
	        };
		}
}
