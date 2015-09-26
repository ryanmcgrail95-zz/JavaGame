package object.environment;

import com.jogamp.opengl.Threading.Mode;

import object.primitive.Drawable;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.model.Model;
import resource.model.ModelCreator;
import cont.ImageLoader;
import cont.TextureController;
import datatypes.lists.CleanList;
import functions.MathExt;
import gfx.GOGL;
import gfx.RGBA;
import gfx.TextureExt;

public class BlockTerrain extends Drawable {
	private static BlockTerrain instance;
	private static byte blockSize = 40;
	private static int chunkSize = 16*blockSize;
	private CleanList<Chunk> chunkList;
	
	private final static RGBA
		COL_GRASS = new RGBA(134,199,98),
		COL_DIRT = Environmental.COL_TRUNK;
	
	private final static byte ID_GRASS = 0, ID_DIRT = 1;
	
	private final static byte
		SH_FULL = 0,
		SH_HALF = 1,
		SH_SLOPE_UP = 2,
		SH_SLOPE_DOWN = 3,
		SH_SLOPE_LEFT = 4,
		SH_SLOPE_RIGHT = 5;
	
	public BlockTerrain(CleanList<Chunk> chunkList) {
		super(false,false);
		
		//shouldAdd = true;
		
		this.chunkList = chunkList;
		
		instance = this;
	}
	
	public static BlockTerrain getInstance() {
		return instance;
	}
	

	
	public static BlockTerrain createFromHeightmap(Heightmap hm) {
		CleanList<Chunk> chunkList = new CleanList<Chunk>();
		
		float hmW, hmH, hmGS;
		int width, height, sectorXNum, sectorYNum;
			hmW = hm.getWidth();
			hmH = hm.getHeight();
			hmGS = hm.getGridSize();			
			
			width = (int) (hmW/blockSize);
				width = (int) MathExt.snap(width, 16);
			height = (int) (hmH/blockSize);
				height = (int) MathExt.snap(height, 16);
			
			sectorXNum = width/16;
			sectorYNum = height/16;
			
		float maxH,minH;			
		float[][] grid = new float[16][16];
		
		Section[] sections;
		Block[][][] blocks;
		Block[][] surfaceBlocks;
		
		int sPos;
		boolean isFull;
		
		int blockNum;
		
		float fracHeight = 1f/16;
		
		float hX,hY, topZ;
		
		for(int sX = 0; sX < sectorXNum; sX++)
			for(int sY = 0; sY < sectorYNum; sY++) {
				sections = new Section[16];
				
				maxH = -1;
				minH = 10000;
				
				for(int x = 0; x < 16; x++)
					for(int y = 0; y < 16; y++) {
						hX = (sX*16 + x)*blockSize;
						hY = (sY*16 + y)*blockSize;
						grid[x][y] = (hm.getZ(hX,hY))/blockSize;
						maxH = Math.max(maxH,grid[x][y]);
						minH = Math.min(minH,grid[x][y]);
					}
				
				surfaceBlocks = new Block[16][16];
				
				for(int s = 15; s >= 0; s--) {
					sPos = s*16*blockSize;
					
					if((int) sPos > maxH*blockSize)
						continue;
					
					blocks = new Block[16][16][16];					
					blockNum = 0;
					
					isFull = false;
					if((int)((s+1)*16) < minH) {
						isFull = true;
						for(int x = 0; x < 16; x++)
							for(int y = 0; y < 16; y++)
								for(int z = 15; z >= 0; z--) {
									blocks[x][y][z] = new Block(ID_GRASS,SH_FULL,s*16+z);
									if(surfaceBlocks[x][y] == null)
										surfaceBlocks[x][y] = blocks[x][y][z];
								}
					}
					else
						for(int x = 0; x < 16; x++)
							for(int y = 0; y < 16; y++) {
								topZ = MathExt.contain(0,grid[x][y]-s*16,15);
								
								for(int z = 0; z < topZ; z++) {
									blockNum++;
									blocks[x][y][z] = new Block(ID_GRASS,(topZ-z) > .75 ? SH_FULL : SH_HALF, s*16+z);
									
									if(z+1 > topZ)
										if(surfaceBlocks[x][y] == null)
											surfaceBlocks[x][y] = blocks[x][y][z];
								}
							}
					
					if(blockNum >= 16*16*16)
						isFull = true;
					else if(blockNum == 0)
						continue;
					
					sections[s] = new Section(blocks,(byte) s,isFull);
					sections[s].render();
				}
				
				chunkList.add(new Chunk(sections,surfaceBlocks,sX,sY));
			}
				
		return new BlockTerrain(chunkList);
	}

	public static BlockTerrain createFromImage(String imgName) {		
		CleanList<Chunk> chunkList = new CleanList<Chunk>();
		
		TextureExt tempTex = TextureController.loadExt(imgName, TextureController.M_NORMAL);
		int width, height, sectorXNum, sectorYNum;
			width = tempTex.getWidth();
			height = tempTex.getHeight();
			
			sectorXNum = width/16;
			sectorYNum = height/16;
			
		int maxH,minH;			
		int[][] grid = new int[16][16];
		
		Section[] sections;
		Block[][][] blocks;
		Block[][] surfaceBlocks;
		surfaceBlocks = new Block[16][16];

		int sPos;
		boolean isFull;
		
		int blockNum;
		
		float fracHeight = 1f/16;
		
		for(int sX = 0; sX < sectorXNum; sX++)
			for(int sY = 0; sY < sectorYNum; sY++) {
				sections = new Section[16];
				
				maxH = -1;
				minH = 10000;
				
				for(int x = 0; x < 16; x++)
					for(int y = 0; y < 16; y++) {
						grid[x][y] = (int) (fracHeight*tempTex.getPixelColor(sX*16+x,sY*16+y).getRi());
						maxH = Math.max(maxH,grid[x][y]);
						minH = Math.min(minH,grid[x][y]);
					}
				
				for(int s = 0; s < 16; s++) {
					sPos = s*16*blockSize;
					
					if((int) sPos > maxH*blockSize)
						break;
					
					
					blocks = new Block[16][16][16];
					
					blockNum = 0;
					
					isFull = false;
					if((int)((s+1)*16) < minH) {
						isFull = true;
						for(int x = 0; x < 16; x++)
							for(int y = 0; y < 16; y++)
								for(int z = 0; z < 16; z++)
									blocks[x][y][z] = new Block(ID_GRASS,SH_FULL,s*16+z);
					}
					else
						for(int x = 0; x < 16; x++)
							for(int y = 0; y < 16; y++)
								for(int z = 0; z < MathExt.contain(0,grid[x][y]-s*16,15); z++) {
									blockNum++;
									blocks[x][y][z] = new Block(ID_GRASS,SH_FULL,s*16+z);
								}
					
					
					// Create Surface Array
					
					if(blockNum >= 16*16*16)
						isFull = true;
					else if(blockNum == 0)
						break;
					
					sections[s] = new Section(blocks,(byte) s,isFull);
					sections[s].render();
				}
				
				chunkList.add(new Chunk(sections,surfaceBlocks,sX,sY));
			}
		
		tempTex.destroy();
		
		return new BlockTerrain(chunkList);
	}
	
	
	public float getSurfaceZ(float x, float y) {
		int chunkXIndex,chunkYIndex;
		chunkXIndex = (int)(x/chunkSize);
		chunkYIndex = (int)(y/chunkSize);
				
		Chunk curChunk = null;
		for(Chunk c : chunkList)
			if(c.xIndex == chunkXIndex && c.yIndex == chunkYIndex) {
				curChunk = c;
				break;
			}
		
		if(curChunk == null)
			return -1;
		
		return curChunk.getSurfaceZ(x-chunkXIndex*chunkSize,y-chunkYIndex*chunkSize);
	}
	
	public void checkSideCollisions(Physical other) {
		float x,y,z, sideAmt = blockSize/2, space = 4, xP,yP;
		x = other.getX();
		y = other.getY();
		z = other.getZ();

		xP = other.getXPrevious();
		yP = other.getYPrevious();

		boolean left,right,front,back;
		left = checkPointInGround(xP-sideAmt,yP,z+blockSize/2);
		right = checkPointInGround(xP+sideAmt,yP,z+blockSize/2);
		front = checkPointInGround(xP,yP-sideAmt,z+blockSize/2);
		back = checkPointInGround(xP,yP+sideAmt,z+blockSize/2);
		
		float leftX,rightX,frontY,backY;
		leftX = blockSize*((int)(xP/blockSize));
				rightX = leftX + blockSize;
		frontY = blockSize*((int)(yP/blockSize));
				backY = frontY + blockSize;
		
		if(left)
			if(Math.abs(leftX-x) < space)
				other.setX(leftX+space);
		if(right)
			if(Math.abs(rightX-x) < space)
				other.setX(rightX-space);
		if(front)
			if(Math.abs(frontY-y) < space)
				other.setY(frontY+space);
		if(back)
			if(Math.abs(backY-y) < space)
				other.setY(backY-space);
	}
	
	public float calcFloorZ(float x, float y, float z) {
		z += blockSize/2;

		Block b = getBlock(x,y,z-blockSize);
		boolean inGround, onFloor;
		inGround = checkPointInGround(x,y,z);
		onFloor = b != null;
		
		if(!onFloor || inGround)
			return -1;
		
		return b.getSurfaceZ(MathExt.grid(x,16),MathExt.grid(y,16));
	}
	
	public float calcFloorZP(float x, float y, float zPrevious, float z) {
		float topZ, botZ;
		if(z < zPrevious) {
			topZ = zPrevious; botZ = z;
		}
		else {
			topZ = z; botZ = zPrevious;
		}			
		
		int i = 0;
		float flZ = -1, moveAmt = blockSize/2, extra;
		for(float zz = topZ; zz > botZ; zz -= moveAmt) {
			if(i++ > 10)
				break;
			flZ = calcFloorZ(x,y,zz);
			if(flZ != -1)
				break;
		}
		if(flZ == -1)
			flZ = calcFloorZ(x,y,botZ);
		
		return flZ;
	}
	
	public Block getBlock(float x, float y, float z) {
		int chunkXIndex,chunkYIndex,sectionZIndex;
		chunkXIndex = (int)(x/chunkSize);
		chunkYIndex = (int)(y/chunkSize);
		sectionZIndex = (int)(z/chunkSize);
		
		if(!MathExt.between(0,sectionZIndex,15))
			return null;
		
		Chunk curChunk = null;
		for(Chunk c : chunkList)
			if(c.xIndex == chunkXIndex && c.yIndex == chunkYIndex) {
				curChunk = c;
				break;
			}
		
		if(curChunk == null)
			return null;
		
		Section curSection;
		curSection = curChunk.sections[sectionZIndex];
		
		if(curSection == null)
			return null;
		
		int xInd,yInd,zInd;
		xInd = (int)((x-chunkXIndex*chunkSize)/blockSize);
		yInd = (int)((y-chunkYIndex*chunkSize)/blockSize);
		zInd = (int)((z-sectionZIndex*chunkSize)/blockSize);
		
		if(zInd < 0)
			return null;
		
		return curSection.blocks[xInd][yInd][zInd];
	}
	
	public boolean checkPointInGround(float x, float y, float z) {
		return getBlock(x,y,z) != null;
	}
	
	public boolean collide(Physical other) {
		
		checkSideCollisions(other);
		
		float flZ = calcFloorZP(other.getX(),other.getY(),other.getZPrevious(),other.getZ());
		
		float z = other.getZ();
		
		boolean collideFloor = false;
		if(z < flZ+blockSize/2)
			collideFloor = true;
		
		if(flZ != -1 && collideFloor) {//other.getZ() <= flZ+4) {
			other.setZVelocity(0);
			other.didCollideFloor(flZ);
			//other.didCollideFloor(flZ);
		}
		
		return false;
	}
	

	public static class Block {
		private int zInd;
		private byte id, shape;
		
		public Block(byte id, byte shape, int zInd) {
			this.id = id;
			this.shape = shape;
			this.zInd = zInd;
		}
		
		public void add(ModelCreator modC, float x, float y, float z, boolean left, boolean right, boolean front, boolean back, boolean bottom, boolean top) {
			float x1,y1,z1,x2,y2,z2;
			x2 = (x1 = x) + blockSize;
			y2 = (y1 = y) + blockSize;
			z1 = z;
			
			switch(shape) {
				case SH_FULL: z2 = z1 + blockSize; 		break;
				case SH_HALF: z2 = z1 + blockSize/2; 	break;
				default: z2 = z1+blockSize;
			}
			
			
			RGBA leftColor,rightColor,frontColor,backColor,bottomColor,topColor;
			leftColor = rightColor = frontColor = backColor = bottomColor = topColor = COL_GRASS;
			
			switch(id) {
				case ID_GRASS:
					leftColor = rightColor = frontColor = backColor = bottomColor = COL_DIRT;
					topColor = COL_GRASS;
						break;
				case ID_DIRT:
					leftColor = rightColor = frontColor = backColor = bottomColor = topColor = COL_DIRT;
						break;
			}
			
			
			if(left) {
				modC.setColor(leftColor);
				modC.add3DModelWall(x1,y2,z1,x1,y1,z2, -1,0,0);
			}
			if(right) {
				modC.setColor(rightColor);
				modC.add3DModelWall(x2,y1,z1,x2,y2,z2, 1,0,0);
			}
			if(front) {
				modC.setColor(frontColor);
				modC.add3DModelWall(x1,y1,z1,x2,y1,z2, 0,-1,0);
			}
			if(back) {
				modC.setColor(backColor);
				modC.add3DModelWall(x2,y2,z1,x1,y2,z2, 0,1,0);
			}
			if(bottom) {
				modC.setColor(bottomColor);
				modC.add3DModelFloor(x1,y1,x2,y2,z1, 0,0,-1);
			}
			if(top) {
				modC.setColor(topColor);
				modC.add3DModelFloor(x2,y1,x1,y2,z2, 0,0,1);
			}
		}
		
		public boolean checkFull() {
			return shape == SH_FULL;
		}

		public float getSurfaceZ(float relaX, float relaY) {
			switch(shape) {
				case SH_FULL: return (zInd+1)*blockSize;
				case SH_HALF: return (zInd+.5f)*blockSize;
				case SH_SLOPE_UP: return (zInd+1)*blockSize - relaY;
				case SH_SLOPE_DOWN: return (zInd)*blockSize + relaY;
				case SH_SLOPE_RIGHT: return (zInd+1)*blockSize - relaX;
				case SH_SLOPE_LEFT: return (zInd)*blockSize + relaX;
				default: return -1;
			}
		}
	}
	
	public static class Section {
		private byte blockNum = 16;
		private Block[][][] blocks;
		private byte zIndex;
		private boolean isFull;
		private Model mod;
	
		public Section(Block[][][] blocks, byte zIndex, boolean isFull) {
			this.blocks = blocks;
			this.zIndex = zIndex;
			this.isFull = isFull;
		}
		
		public void render() {
			float bX,bY,bZ;
			
			ModelCreator modC = new ModelCreator(Model.QUADS);
						
			if(isFull)
				modC.add3DBlockModel(0,0,0,chunkSize,chunkSize,chunkSize);
			else {
				boolean left,right,top,bottom,front,back, auto = true;
				
				for(int x = 0; x < 16; x++) {
					bX = x*blockSize;
					for(int y = 0; y < 16; y++) {
						bY = y*blockSize;
						for(int z = 0; z < 16; z++) {
							if(blocks[x][y][z] != null) {
								if(x == 0) {
									left = auto;
									right = blocks[x+1][y][z] == null;
										if(!right)
											right = !blocks[x+1][y][z].checkFull();
								}
								else if(x == 15) {
									left = blocks[x-1][y][z] == null;
										if(!left)
											left = !blocks[x-1][y][z].checkFull();
									right = auto;
								}
								else {
									left = blocks[x-1][y][z] == null;
										if(!left)
											left = !blocks[x-1][y][z].checkFull();
									right = blocks[x+1][y][z] == null;
										if(!right)
											right = !blocks[x+1][y][z].checkFull();
								}
								
								if(y == 0) {
									front = auto;
									back = blocks[x][y+1][z] == null;
										if(!back)
											back = !blocks[x][y+1][z].checkFull();
								}
								else if(y == 15) {
									front = blocks[x][y-1][z] == null;
										if(!front)
											front = !blocks[x][y-1][z].checkFull();
									back = auto;
								}
								else {
									front = blocks[x][y-1][z] == null;
										if(!front)
											front = !blocks[x][y-1][z].checkFull();
									back = blocks[x][y+1][z] == null;
										if(!back)
											back = !blocks[x][y+1][z].checkFull();
								}
								
								if(z == 0) {
									bottom = auto;
									top = blocks[x][y][z+1] == null;
								}
								else if(z == 15) {
									bottom = blocks[x][y][z-1] == null;
									top = auto;
								}
								else {
									bottom = blocks[x][y][z-1] == null;
									top = blocks[x][y][z+1] == null;
								}
								
								bZ = z*blockSize;
							
							
								blocks[x][y][z].add(modC,bX,bY,bZ,left,right,front,back,bottom,top);
							}
						}
					}
				}
			}
			mod = modC.endModel();
		}
		
		public void draw(float dX, float dY) {
			float bX,bY,bZ, dZ = zIndex*blockSize*16;
			
			GOGL.transformTranslation(dX,dY,dZ);
				if(mod != null)
					mod.drawFast();
					//mod.add();
			GOGL.transformClear();
		}
	}
	
	public static class Chunk {
		private int xIndex,yIndex;
		private Section[] sections = new Section[16];
		private Block[][] surfaceBlocks;
		
		public Chunk(Section[] sections, Block[][] surfaceBlocks, int xIndex, int yIndex) {
			this.sections = sections;
			this.surfaceBlocks = surfaceBlocks;
			this.xIndex = xIndex;
			this.yIndex = yIndex;
		}
		
		public void draw() {
			float sX,sY;
			sX = xIndex*16*blockSize;
			sY = yIndex*16*blockSize;
			
			for(Section s : sections)
				if(s != null)
					s.draw(sX,sY);
		}
		
		public float getSurfaceZ(float relaX, float relaY) {
			int bXInd, bYInd;
			bXInd = (int)(relaX/blockSize);
			bYInd = (int)(relaY/blockSize);
			
			Block b = surfaceBlocks[bXInd][bYInd];
			if(b != null)
				return b.getSurfaceZ(relaX-bXInd*blockSize,relaY-bYInd*blockSize);
			else
				return -1;
		}
	}
	
	public void add() {
		GOGL.setLightColor(RGBA.WHITE);
		GOGL.transformClear();
		for(Chunk c : chunkList)
			c.draw();
	}

	
	public void draw() {
		GOGL.setLightColor(RGBA.WHITE);
		GOGL.enableLighting();
		GOGL.transformClear();
		for(Chunk c : chunkList)
			c.draw();
		GOGL.disableLightings();
	}
	public void update() {}
}
