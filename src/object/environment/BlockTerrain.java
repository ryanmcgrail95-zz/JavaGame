package object.environment;

import com.jogamp.opengl.Threading.Mode;

import object.actor.Player;
import object.primitive.Drawable;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.model.Model;
import resource.model.ModelCreator;
import cont.ImageLoader;
import cont.TextureController;
import datatypes.lists.CleanList;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.GOGL;
import gfx.RGBA;
import gfx.TextureExt;

public class BlockTerrain extends Drawable {
	private static BlockTerrain instance;
	
	private ModelCreator modC = new ModelCreator(Model.QUADS);

	private static Chunk currentChunk;
	private static Section currentSection;
	
	private static int
		blockSize = 40,
		chunkSize = 16*blockSize;
	private CleanList<Chunk> chunkList;
	private BlockTerrain me;
	
	
	private final static RGBA
		COL_GRASS = new RGBA(134,199,98),
		COL_DIRT = Environmental.COL_TRUNK;
	
	private final static byte ID_GRASS = 0, ID_DIRT = 1;	
	private final static byte
		SH_FULL = 0,
		SH_HALF = 1,
		SH_SLOPE_RIGHT = 2,
		SH_SLOPE_UP = 3,
		SH_SLOPE_LEFT = 4,
		SH_SLOPE_DOWN = 5,
		SH_SLOPE_UPRIGHT = 6,
		SH_SLOPE_UPLEFT = 7,
		SH_SLOPE_DOWNLEFT = 8,
		SH_SLOPE_DOWNRIGHT = 9;
	
	private final static int
		LUX_DARK = 32,
		LUX_BRIGHT = 255,
		LUX_POINT_BRIGHT = 254;
	private final static int
		LUX_DISTANCE = chunkSize;
	
	public BlockTerrain() {
		super(false,false);
		
		name = "BlockTerrain";
		
		me = this;
		//shouldAdd = true;
		
		this.chunkList = new CleanList<Chunk>();
		
		instance = this;
	}
	
	public static BlockTerrain getInstance() {
		return instance;
	}
	

	
	public static BlockTerrain createFromHeightmap(Heightmap2 hm) {		
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
		
		boolean isSurface;
		Block curBlock;
		
		int k = 0;
		
		BlockTerrain bt = new BlockTerrain();

		
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
									blocks[x][y][z] = bt.new Block(ID_GRASS,SH_FULL,s*16+z);
									if(surfaceBlocks[x][y] == null)
										surfaceBlocks[x][y] = blocks[x][y][z];
								}
					}
					else
						for(int x = 0; x < 16; x++)
							for(int y = 0; y < 16; y++) {
								topZ = MathExt.contain(0,grid[x][y]-s*16,15);
								
								if(topZ-(int)topZ < .5)
									topZ = (int) topZ;
								
								for(int z = 0; z < topZ; z++) {
									blockNum++;
									
									isSurface = false;
									if(z+1 >= topZ)
										if(surfaceBlocks[x][y] == null)
											isSurface = true;
									
									if(!isSurface)
										blocks[x][y][z] = bt.new Block(ID_GRASS,SH_FULL, s*16+z);
									else
										surfaceBlocks[x][y] = blocks[x][y][z] = bt.new Block(ID_GRASS,(topZ-z) > .75 ? SH_FULL : SH_HALF, s*16+z);
								}
							}
					
					
					
										
					if(blockNum >= 16*16*16)
						isFull = true;
					else if(blockNum == 0)
						continue;
					
					sections[s] = bt.new Section(blocks,(byte) s);
				}
				
				bt.addChunk(bt.new Chunk(sections,surfaceBlocks,sX,sY));
			}

		//b.smooth();
		bt.update();
		 
		return bt;
	}

	private void addChunk(Chunk c) {
		chunkList.add(c);
	}

	public BlockTerrain createFromImage(String imgName) {		
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
					
					sections[s] = new Section(blocks,(byte) s);
					sections[s].update(sX*chunkSize,sY*chunkSize);
				}
				
				chunkList.add(new Chunk(sections,surfaceBlocks,sX,sY));
			}
		
		tempTex.destroy();
		
		return new BlockTerrain();
	}
	
	
	public void smooth() {
		for(Chunk c : chunkList)
			c.smooth();
	}
	
	public float getSurfaceZ(float worldX, float worldY) {
		Chunk curChunk = getChunk(worldX,worldY);
		if(curChunk == null)
			return -1;
		
		return curChunk.getSurfaceZ(worldX - curChunk.getWorldX(), worldY- curChunk.getWorldY());
	}
	
	public void checkSideCollisions(Physical other) {
		float x,y,z, sideAmt = blockSize/2, space = 4, xP,yP;
		x = other.getX();
		y = other.getY();
		z = other.getZ();

		xP = other.getXPrevious();
		yP = other.getYPrevious();
		Block lB,rB,fB,bB;
		lB = getBlock(xP-sideAmt,yP,z+blockSize/2);
		rB = getBlock(xP+sideAmt,yP,z+blockSize/2);
		fB = getBlock(xP,yP-sideAmt,z+blockSize/2);
		bB = getBlock(xP,yP+sideAmt,z+blockSize/2);
		
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
		
		if(left && (lB.checkShape(SH_FULL) || lB.checkShape(SH_SLOPE_RIGHT)))
			if(Math.abs(leftX-x) < space)
				other.setX(leftX+space);
		if(right && (rB.checkShape(SH_FULL) || rB.checkShape(SH_SLOPE_LEFT)))
			if(Math.abs(rightX-x) < space)
				other.setX(rightX-space);
		if(front && (fB.checkShape(SH_FULL) || fB.checkShape(SH_SLOPE_DOWN)))
			if(Math.abs(frontY-y) < space)
				other.setY(frontY+space);
		if(back && (bB.checkShape(SH_FULL) || bB.checkShape(SH_SLOPE_UP)))
			if(Math.abs(backY-y) < space)
				other.setY(backY-space);
	}
	
	public float calcFloorZ(float x, float y, float z) {
		
		// Check for Normal Block
		z += blockSize/2;

		Block b = getBlock(x,y,z-blockSize);
		boolean inGround, onFloor;
		inGround = checkPointInGround(x,y,z);
		onFloor = b != null;
				
		float[] pos = getBlockWithPosition(x,y,z);
		float 
			xRel = x - pos[0],
			yRel = y - pos[1];
		
		if(!onFloor || inGround) {			
			b = getBlock(x,y,z);
			inGround = checkPointInGround(x,y,z+blockSize);
			onFloor = b != null;
			
			if(!onFloor || inGround)
				return -1;
			
			return b.getSurfaceZ(xRel,yRel);
		}

		return b.getSurfaceZ(xRel,yRel);
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
	
	public Block getSurfaceBlock(int xInd, int yInd) {
		int chunkXIndex,chunkYIndex,sectionZIndex;
		chunkXIndex = (int)(xInd/16);
		chunkYIndex = (int)(yInd/16);
		
		xInd -= chunkXIndex*16;
		yInd -= chunkYIndex*16;
		
		Chunk curChunk = null;
		for(Chunk c : chunkList)
			if(c.xIndex == chunkXIndex && c.yIndex == chunkYIndex) {
				curChunk = c;
				break;
			}
		chunkList.broke();

		if(curChunk == null)
			return null;
				
		return curChunk.getSurfaceBlock(xInd,yInd);
	}
	
	
	public Chunk getChunk(float worldX, float worldY) {
		if(currentChunk != null)
			if(currentChunk.containsPoint(worldX, worldY))
				return currentChunk;
		
		int chunkXIndex,chunkYIndex;
		chunkXIndex = (int) Math.floor(worldX/chunkSize);
		chunkYIndex = (int) Math.floor(worldY/chunkSize);
		
		Chunk curChunk = null;
		for(Chunk c : chunkList)
			if(c.xIndex == chunkXIndex && c.yIndex == chunkYIndex) {
				curChunk = currentChunk = c;
				break;
			}
		chunkList.broke();
		
		return curChunk;
	}
	public Section getSection(float worldX, float worldY, float worldZ) {
		if(currentSection != null)
			if(currentSection.containsPoint(worldX, worldY, worldZ))
				return currentSection;
		
		Chunk curChunk = getChunk(worldX,worldY);
		if(curChunk == null)
			return null;
		
		int sectionZIndex;
		sectionZIndex = (int) Math.floor(worldZ/chunkSize);
		if(sectionZIndex < 0)
			return null;
		
		Section curSection = curChunk.sections[sectionZIndex];
		if(curSection != null)
			currentSection = curSection;
		return curSection;
	}
	
	public Block getBlock(float worldX, float worldY, float worldZ) {
		Section curSection = getSection(worldX,worldY,worldZ);
		if(curSection == null)
			return null;
		
		int[] indices = getRelativeBlockIndices(worldX,worldY,worldZ);
		return curSection.blocks[indices[0]][indices[1]][indices[2]];
	}

	public float[] getSectionCoordinates(float worldX, float worldY, float worldZ) {
		int[] indices = getSectionIndices(worldX, worldY, worldZ);
		return new float[] {
			indices[0]*chunkSize,
			indices[1]*chunkSize,
			indices[2]*chunkSize
		};
	}
	
	public int[] getSectionIndices(float worldX, float worldY, float worldZ) {
		int[] outArray = new int[3];
			getSectionIndices(outArray, worldX, worldY, worldZ);
		return outArray;
	}
	public void getSectionIndices(int[] outArray, float worldX, float worldY, float worldZ) {
		outArray[0] = (int) Math.floor(worldX/chunkSize);
		outArray[1] = (int) Math.floor(worldY/chunkSize);
		outArray[2] = (int) Math.floor(worldZ/chunkSize);
	}
	
	private int[] getRelativeBlockIndices(float worldX, float worldY, float worldZ) {
		float[] sectionCoords = getSectionCoordinates(worldX,worldY,worldZ);
		
		return new int[] {
			(int) Math.floor((worldX - sectionCoords[0])/blockSize),
			(int) Math.floor((worldY - sectionCoords[1])/blockSize),
			(int) Math.floor((worldZ - sectionCoords[2])/blockSize)
		};
	}
	
	public void setBlockLux(int lux, float worldX, float worldY, float worldZ) {
		Section curSection = getSection(worldX, worldY, worldZ);
		if(curSection == null)
			return;
		
		int[] indices = getRelativeBlockIndices(worldX, worldY, worldZ);

		if(indices[2] < 0)
			return;
		
		int centInd, startInd, toInd;
		centInd = curSection.getIndexZ();
		startInd = Math.max(0,centInd-1);
		toInd = Math.min(15,centInd+1);
		
		float relX, relY;		
		for(Chunk c : chunkList) {
			relX = worldX - c.getWorldX();
			relY = worldY - c.getWorldY();
			
			if(Math2D.calcLen(relX-chunkSize/2,relY-chunkSize/2) < LUX_DISTANCE)
				c.markLightingDirtyFromAToB(startInd,toInd);
			
			// ONLY MARK RIGHT SECTIONS DIRTY
		}
		
		curSection.lux[indices[0]][indices[1]][indices[2]] = lux;
	}
	public int getBlockLux(float worldX, float worldY, float worldZ) {
		Section curSection = getSection(worldX, worldY, worldZ);
		if(curSection == null)
			return 0;
		
		int[] indices = getRelativeBlockIndices(worldX, worldY, worldZ);

		if(indices[2] < 0)
			return 0;
		
		return curSection.lux[indices[0]][indices[1]][indices[2]];
	} 

	
	public float[] getBlockWithPosition(float x, float y, float z) {
		return new float[] {
			(int) (x/blockSize)*blockSize,
			(int) (y/blockSize)*blockSize,
			(int) (z/blockSize)*blockSize
		};
	}

	
	public boolean checkPointInGround(float worldX, float worldY, float worldZ) {
		return getBlock(worldX,worldY,worldZ) != null;
	}
	
	public boolean collide(Physical other) {
		
		//Working!!
		checkSideCollisions(other);
		
		float flZ = calcFloorZP(other.getX(),other.getY(),other.getZPrevious(),other.getZ());
		
		float z = other.getZ();
		
		boolean collideFloor = false;
				
		
		if(z < flZ+blockSize/2 && Math.abs(other.getZVelocity()) < 1)
			collideFloor = true;
		if(z > flZ-blockSize/2 && z < flZ)
			collideFloor = true;
		
		if(flZ != -1 && collideFloor) {//other.getZ() <= flZ+4) {
			other.setZVelocity(0);
			other.didCollideFloor(flZ);
			//other.didCollideFloor(flZ);
		}
		
		return false;
	}
	
	public void addLightSource(float worldX, float worldY, float worldZ) {
		setBlockLux(LUX_BRIGHT, worldX, worldY, worldZ);
	}
	public void removeLightSource(float worldX, float worldY, float worldZ) {
		setBlockLux(LUX_DARK, worldX, worldY, worldZ);
	}
	
	public boolean addPointLightSource(float worldX, float worldY, float worldZ) {
		if(getBlockLux(worldX,worldY,worldZ) != LUX_BRIGHT) {
			setBlockLux(LUX_POINT_BRIGHT, worldX, worldY, worldZ);
			return true;
		}
		return false;
	}
	public boolean removePointLightSource(float worldX, float worldY, float worldZ) {
		if(getBlockLux(worldX,worldY,worldZ) != LUX_BRIGHT) {
			setBlockLux(LUX_DARK, worldX, worldY, worldZ);
			return true;
		}
		return false;
	}
	
	

	public class Block {
		private int zInd;
		private byte id, shape;
		
		public Block(byte id, byte shape, int zInd) {
			this.id = id;
			this.shape = shape;
			this.zInd = zInd;
		}
		
		public boolean checkShape(byte shape) {
			return this.shape == shape;
		}

		public void add(ModelCreator modC, int[][][] luxes, float worldX, float worldY, float worldZ, float x, float y, float z, int xInd, int yInd, int zInd, boolean left, boolean right, boolean front, boolean back, boolean bottom, boolean top) {
			float x1,y1,z1,x2,y2,z2;
			x2 = (x1 = x) + blockSize;
			y2 = (y1 = y) + blockSize;
			z1 = z;

			int slope = -1;
			
			switch(shape) {
				case SH_FULL: z2 = z1 + blockSize;		break;
				case SH_HALF: z2 = z1 + blockSize/2; 	break;
				
				case SH_SLOPE_RIGHT:
				case SH_SLOPE_UP:
				case SH_SLOPE_LEFT:
				case SH_SLOPE_DOWN: slope = shape-SH_SLOPE_RIGHT;
									z2 = z1;
									break;
				
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
				modC.setBrightness(luxes[xInd][yInd+1][zInd+1]/255f);
				modC.add3DModelWall(x1,y2,z1,x1,y1,z2, -1,0,0);
			}
			if(right) {
				modC.setColor(rightColor);
				modC.setBrightness(luxes[xInd+2][yInd+1][zInd+1]/255f);
				modC.add3DModelWall(x2,y1,z1,x2,y2,z2, 1,0,0);
			}
			if(front) {
				modC.setColor(frontColor);
				modC.setBrightness(luxes[xInd+1][yInd][zInd+1]/255f);
				modC.add3DModelWall(x1,y1,z1,x2,y1,z2, 0,-1,0);
			}
			if(back) {
				modC.setColor(backColor);
				modC.setBrightness(luxes[xInd+1][yInd+2][zInd+1]/255f);
				modC.add3DModelWall(x2,y2,z1,x1,y2,z2, 0,1,0);
			}
			if(bottom) {
				modC.setColor(bottomColor);
				modC.setBrightness(luxes[xInd+1][yInd+1][zInd]/255f);
				modC.add3DModelFloor(x1,y1,x2,y2,z1, 0,0,-1);
			}
			if(top) {
				modC.setColor(topColor);
				modC.setBrightness(luxes[xInd+1][yInd+1][zInd+2]/255f);
				if(slope == -1)
					modC.add3DModelFloor(x2,y1,x1,y2,z2, 0,0,1);
				else
					modC.add3DModelSlopeBT(x+blockSize/2,y+blockSize/2,z, blockSize/2,blockSize,slope);
			}
		}
		
		public boolean checkFull() {
			return shape == SH_FULL;
		}
		
		public void setShape(byte newShape) {
			this.shape = newShape;
		}

		public float getSurfaceZ(float relaX, float relaY) {
			switch(shape) {
				case SH_FULL: return (zInd+1)*blockSize;
				case SH_HALF: return (zInd+.5f)*blockSize;
				
				case SH_SLOPE_UP: return (zInd+1)*blockSize - relaY;
				case SH_SLOPE_DOWN: return (zInd)*blockSize + relaY;
				case SH_SLOPE_RIGHT: return (zInd)*blockSize + relaX;
				case SH_SLOPE_LEFT: return (zInd+1)*blockSize - relaX;
				
				
				default: return -1;
			}
		}

		public byte getShape() {
			return shape;
		}
	}
	
	public class Section {
		private Chunk parent;
		private Block[][][] blocks;
		private byte zIndex;
		private boolean isFull;
		private Model mod;
		
		private boolean[][][] shouldRender;
		
		private byte minZInd, maxZInd;
		
		private int[][][] lux;
		private boolean isLightingDirty, isModelDirty, isBlocksDirty;
		
		public Section(Block[][][] blocks, byte zIndex) {
			this.blocks = blocks;
			this.zIndex = zIndex;
			this.isFull = isFull;
			
			shouldRender = new boolean[16][16][16];
			
			lux = new int[16][16][16];
			for(int x = 0; x < 16; x++)
				for(int y = 0; y < 16; y++)
					for(int z = 0; z < 16; z++)
						lux[x][y][z] = LUX_DARK;
			
			markBlocksDirty();
		}
		
		public int getIndexZ() {
			return zIndex;
		}

		public void setParent(Chunk parent) {
			this.parent = parent;
		}
		
		public boolean containsPoint(float worldX, float worldY, float worldZ) {
			if(!parent.containsPoint(worldX, worldY))
				return false;
			float relZ = worldZ - getWorldZ();
			return (0 <= relZ && relZ < chunkSize);
		}
		
		public void updateBlocks() {
			int num = 0;
			minZInd = 15;
			maxZInd = 0;
			for(int x = 0; x < 16; x++)
				for(int y = 0; y < 16; y++)
					for(int z = 0; z < 16; z++)
						if(blocks[x][y][z] != null) {
							num++;
							minZInd = (byte) Math.min(z,minZInd);
							maxZInd = (byte) Math.max(z,maxZInd);
							
							if(x > 0 && x < 15 && y > 0 && y < 15 && z > 0 && z < 15)
								shouldRender[x][y][z] = !(blocks[x-1][y][z] != null && blocks[x+1][y][z] != null && blocks[x][y-1][z] != null && blocks[x][y+1][z] != null && blocks[x][y][z+1] != null && blocks[x][y][z-1] != null);
							else
								shouldRender[x][y][z] = true;
						}
						else
							shouldRender[x][y][z] = false;
			
			isFull = (num == 16*16*16);
		}

		public void updateLighting(int[][][] luxes, float secX, float secY) {
			if(isFull)
				return;
			for(int x = 0; x < 16; x++) {
				for(int y = 0; y < 16; y++) {
					for(int z = 0; z < 16; z++) {
						if(lux[x][y][z] >= LUX_POINT_BRIGHT)
							continue;
						else if(blocks[x][y][z] != null)
							lux[x][y][z] = LUX_DARK;
						else {
							lux[x][y][z] = (int) Math.max(LUX_DARK, .8f*luxes[x+1][y+1][z+1]);
						}
					}
				}
			}
		}

		
		private void render(int[][][] luxes, float secX, float secY) {
			float bX,bY,bZ;
			
			float secZ = getWorldZ();
			
			if(mod != null)
				mod.destroy();
									
			if(isFull)
				modC.add3DBlockModel(0,0,0,chunkSize,chunkSize,chunkSize);
			else {
				boolean left,right,top,bottom,front,back, auto = true;
				
				for(int z = minZInd; z <= maxZInd; z++) {
					bZ = z*blockSize;
					for(int x = 0; x < 16; x++) {
						bX = x*blockSize;
						for(int y = 0; y < 16; y++) {
							bY = y*blockSize;
							
							if(shouldRender[x][y][z] == true) {
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
								
														
								blocks[x][y][z].add(modC,luxes, secX+bX,secY+bY,secZ+bZ, bX,bY,bZ, x,y,z, left,right,front,back,bottom,top);
							}
						}
					}
				}
			}
			
			mod = modC.endModel();
		}
		
		public boolean checkDirty() {
			return isBlocksDirty || isLightingDirty || isModelDirty;
		}
		
		public void update(float secX, float secY) {			
			if(checkDirty()) {
				float secZ, worldX, worldY, worldZ;
				secZ = getWorldZ();
				
				int curValue;
				int xO,yO,zO;
				
				int amt;
				if(isFull)
					amt = 17;
				else
					amt = 1;
				
				int[][][] luxes = new int[18][18][18];
				for(int x = -1; x < 17; x += amt) {
					worldX = secX + x*blockSize;
					for(int y = -1; y < 17; y += amt) {						
						worldY = secY + y*blockSize;
						for(int z = -1; z < 17; z += amt) {
							worldZ = secZ + z*blockSize;
							
							if(x == -1 || x == 16 || y == -1 || y == 16 || z == -1 || z == 16)
								curValue = me.getBlockLux(worldX,worldY,worldZ);
							else
								curValue = lux[x][y][z];
							
							if(curValue >= LUX_POINT_BRIGHT) {
								for(int dd = 0; dd < 6; dd++) {
									switch(dd) {
										case 0:	xO = x+1;
												yO = y;
												zO = z;		break;
										case 1:	xO = x-1;
												yO = y;
												zO = z;		break;
										case 2:	xO = x;
												yO = y+1;
												zO = z;		break;
										case 3:	xO = x;
												yO = y-1;
												zO = z;		break;
										case 4:	xO = x;
												yO = y;
												zO = z+1;	break;
										case 5:	xO = x;
												yO = y;
												zO = z-1;	break;
										default: xO = yO = zO = 0;
									}
									if(!(xO < -1 || xO > 16 || yO < -1 || yO > 16 || zO < -1 || zO > 16))
										luxes[xO+1][yO+1][zO+1] = LUX_BRIGHT;
								}		
							}
							else if(curValue != LUX_DARK) {
								for(int dd = 0; dd < 6; dd++) {
									switch(dd) {
										case 0:	xO = x+1;
												yO = y;
												zO = z;		break;
										case 1:	xO = x-1;
												yO = y;
												zO = z;		break;
										case 2:	xO = x;
												yO = y+1;
												zO = z;		break;
										case 3:	xO = x;
												yO = y-1;
												zO = z;		break;
										case 4:	xO = x;
												yO = y;
												zO = z+1;	break;
										case 5:	xO = x;
												yO = y;
												zO = z-1;	break;
										default: xO = yO = zO = 0;
									}
											
									if(!(xO < -1 || xO > 16 || yO < -1 || yO > 16 || zO < -1 || zO > 16))
										if(luxes[xO+1][yO+1][zO+1] < curValue)
											luxes[xO+1][yO+1][zO+1] = curValue;
								}	
							}
						}
					}
				}
				
				if(isBlocksDirty)
					updateBlocks();
					

				if(isLightingDirty)
					updateLighting(luxes, secX, secY);	
				
				if(isModelDirty)
					render(luxes, secX,secY);
				
				isModelDirty = isLightingDirty = isBlocksDirty = false;
			}
		}
	
		public void draw(float dX, float dY) {
			float bX,bY,bZ, dZ = zIndex*blockSize*16;
			
			GOGL.transformTranslation(dX,dY,dZ);
				if(mod != null)
					mod.drawFast();
					//mod.add();
			GOGL.transformClear();
		}

		public void markBlocksDirty() {
			isModelDirty = true;
			isBlocksDirty = true;
			isLightingDirty = true;
		}
		
		public void markLightingDirty() {
			isModelDirty = true;
			isLightingDirty = true;
		}
		
		public void markModelDirty() {
			isModelDirty = true;
		}

		public Block getBlockWithIndex(int x, int y, int z) {
			return blocks[x][y][z];
		}

		public float getWorldZ() {
			return zIndex*chunkSize;
		}
	}
	
	public class Chunk {
		private int xIndex,yIndex;
		private Section[] sections = new Section[16];
		private Block[][] surfaceBlocks;
		private boolean isDirty;
		
		public Chunk(Section[] sections, Block[][] surfaceBlocks, int xIndex, int yIndex) {
			this.sections = sections;
			this.surfaceBlocks = surfaceBlocks;
			this.xIndex = xIndex;
			this.yIndex = yIndex;
			isDirty = true;
			
			for(Section s : sections)
				if(s != null)
					s.setParent(this);
		}
		
		public void markLightingDirtyFromAToB(int startInd, int toInd) {
			Section s;
			for(int i = startInd; i < toInd; i++)
				if((s = sections[i]) != null)
					s.markLightingDirty();
		}

		public boolean containsPoint(float worldX, float worldY) {
			float relX, relY;
			relX = worldX - getWorldX();
			relY = worldY - getWorldY();
			
			return (0 <= relX && relX < chunkSize && 0 <= relY && relY < chunkSize);
		}

		public void smooth() {
			Block curBlock, l, r, u, d;
			int rX,rY;
			float h, lH,rH,uH,dH,	lDiff, rDiff, uDiff, dDiff;
			int sX,sY;
			
			sX = getIndexX();
			sY = getIndexY();
			
			for(int x = 0; x < 16; x++)
				for(int y = 0; y < 16; y++) {
						if((curBlock = getSurfaceBlock(x,y)) != null) {
							h = (int) (curBlock.getSurfaceZ(blockSize/2,blockSize/2)/blockSize) * blockSize - blockSize/2;
							
							rX = sX*16+x;
							rY = sY*16+y;
							
							if((l = me.getSurfaceBlock(rX-1,rY)) != null)	lH = l.getSurfaceZ(blockSize, blockSize/2);
								else lH = -1;
							if((r = me.getSurfaceBlock(rX+1,rY)) != null)	rH = r.getSurfaceZ(0, blockSize/2);
								else rH = -1;
							if((u = me.getSurfaceBlock(rX,rY-1)) != null)	uH = u.getSurfaceZ(blockSize/2, blockSize);
								else uH = -1;
							if((d = me.getSurfaceBlock(rX,rY+1)) != null)	dH = d.getSurfaceZ(blockSize/2, 0);
								else dH = -1;
							
							
							lDiff = lH-h;
							rDiff = rH-h;
							uDiff = uH-h;
							dDiff = dH-h;
							
							
							// If UD Infeasible, Try LR
							if(Math.abs(uDiff) >= blockSize/2 || Math.abs(dDiff) >= blockSize/2) {
								if(lDiff == -blockSize/2 && rDiff == blockSize/2)
									curBlock.setShape(SH_SLOPE_RIGHT);
								else if(lDiff == blockSize/2 && rDiff == -blockSize/2)
									curBlock.setShape(SH_SLOPE_LEFT);
							}
							if(Math.abs(lDiff) >= blockSize/2 || Math.abs(rDiff) >= blockSize/2) {
								if(uDiff == -blockSize/2 && dDiff == blockSize/2)
									curBlock.setShape(SH_SLOPE_DOWN);
								else if(uDiff == blockSize/2 && dDiff == -blockSize/2)
									curBlock.setShape(SH_SLOPE_UP);
							}							
						}
				}
		}
		
		public void findSurfaceBlocks() {
			int sectionStartIndex = -1;
			for(int i = 15; i >= 0; i--)
				if(sections[i] != null) {
					sectionStartIndex = i;
					break;
				}
			
			// Completely empty chunk, No Blocks Whatsoever
			if(sectionStartIndex == -1)
				return;
			
			Section s;
			Block b;
			for(int x = 0; x < 16; x++)
				for(int y = 0; y < 16; y++)
					if(surfaceBlocks[x][y] == null) 
						for(int i = sectionStartIndex; i >= 0; i--) {
							s = sections[i];
							for(int z = 15; z >= 0; z--) {
								if((b = s.getBlockWithIndex(x,y,z)) != null) {
									surfaceBlocks[x][y] = b;
									break;
								}
							}
						}
		}

		public int getIndexX() {return xIndex;}
		public int getIndexY() {return yIndex;}

		public float getWorldX() {
			return xIndex*chunkSize;
		}
		public float getWorldY() {
			return yIndex*chunkSize;
		}

		public Block getSurfaceBlock(int xInd, int yInd) {
			return surfaceBlocks[xInd][yInd];
		}

		public void draw() {
			float sX,sY;
			sX = xIndex*16*blockSize;
			sY = yIndex*16*blockSize;
			
			for(Section s : sections)
				if(s != null)
					s.draw(sX,sY);
		}
		
		public void markModelDirty() {
			for(Section s : sections)
				if(s != null)
					s.markModelDirty();
		}
		public void markLightingDirty() {
			for(Section s : sections)
				if(s != null)
					s.markLightingDirty();
		}
		public void markBlocksDirty() {
			for(Section s : sections)
				if(s != null)
					s.markBlocksDirty();
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

		public void update() {
			float secX, secY;
			secX = getWorldX();
			secY = getWorldY();

			for(Section s : sections)
				if(s != null)
					s.update(secX,secY);
		}
	}

	public void update() {
		Player p = Player.getInstance();
		boolean didAdd = addPointLightSource(p.getX(),p.getY(),p.getZ());
		
		for(Chunk c : chunkList)
			c.update();
		
		if(didAdd)
			removePointLightSource(p.getX(),p.getY(),p.getZ());
	}
	
	public void draw() {
		GOGL.setLightColor(RGBA.WHITE);
		GOGL.enableLighting();
		GOGL.transformClear();
		for(Chunk c : chunkList)
			c.draw();
		GOGL.disableLightings();
	}

	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}
}
