package object.environment;

import io.IO;
import io.Mouse;
import object.actor.Player;
import object.primitive.Collideable;
import object.primitive.Drawable;
import object.primitive.Environmental;
import object.primitive.Physical;
import paper.PlayerPM;
import resource.model.Model;
import resource.model.ModelCreator;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;
import gfx.TextureExt;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import ds.LinearGrid;
import ds.mat3;
import ds.vec2;
import ds.vec3;
import ds.lst.CleanList;

public class Heightmap extends Drawable implements Collideable {
	private static Heightmap instance;
	private float gridSize, chunkSize;
	private CleanList<Chunk> chunkList;
	private LinearGrid heightGrid;
	private ModelCreator modC = new ModelCreator(Model.TRIANGLES);
	
	private float[][] vertexGrid;
	private int[][] luxGrid;
	private float[][][] normalGrid;
	private int[][][] colorGrid;
	
	private Chunk[][] chunkGrid;
	
	protected int width, height;
	private float lightDis = 250;
	
	public Heightmap(float gridSize, float maxHeight, String fileName) {
		super(false,false);
		
		name = "Heightmap";
		instance = this;
				
		this.gridSize = gridSize;
		this.chunkSize = this.gridSize*16;
		
		loadFromTexture(maxHeight, TextureController.load(fileName, fileName, TextureController.M_BGALPHA));
	}
	
	public Heightmap(float gridSize, float maxHeight, TextureExt tex) {

		super(false,false);
		instance = this;
		
		shouldAdd = true;
		
		doUpdates = false;
		this.gridSize = gridSize;
		this.chunkSize = this.gridSize*16;
		
		loadFromTexture(maxHeight, tex);
	}
	
	public void loadFromTexture(float maxHeight, TextureExt tex) {
		width = tex.getWidth();
		height = tex.getHeight();
		
		chunkList = new CleanList<Chunk>();
		chunkGrid = new Chunk[width][height];

		luxGrid = new int[width][height];
		vertexGrid = new float[width][height];
		colorGrid = new int[width][height][3];
		normalGrid = new float[width][height][4];
			
		int chunkXNum, chunkYNum, cXX, cYY;
		chunkXNum = (int) Math.floor(width/16);
		chunkYNum = (int) Math.floor(height/16);
		
		Chunk c;

		float[][] grid = new float[width][height];

		for(int cX = 0; cX < chunkXNum; cX++) {
			for(int cY = 0; cY < chunkYNum; cY++) {								
				for(int x = 0; x < 16; x++)
					for(int y = 0; y < 16; y++) {
						cXX = cX*16 + x;
						cYY = cY*16 + y;
						
						grid[cXX][cYY] = vertexGrid[cXX][cYY] = maxHeight*tex.getPixelColor(cXX,cYY).getRi();
						
						colorGrid[cXX][cYY] = generateColor(grid[cXX][cYY]);
					}
				
				
				chunkList.add(c = new Chunk(cX,cY));
				chunkGrid[cX][cY] = c;
			}
		}
										
		heightGrid = new LinearGrid(grid);
		calcNormals();
	}
	
	
	public float getWidth() {
		return width*gridSize;
	}
	public float getHeight() {
		return height*gridSize;
	}
	
	public float getZ(float worldX, float worldY) {
		return heightGrid.get(worldX/gridSize,worldY/gridSize);
	}
	protected float getVertexZ(int x, int y) {
		x = MathExt.contain(0,x,width-1);
		y = MathExt.contain(0,y,height-1);
		return vertexGrid[x][y];
	}
	
	
	public void normalize(float[] vector) {
		float sum = 0;
		for(int i = 0; i < vector.length; i++)
			sum += vector[i];
		sum = (float) Math.sqrt(sum);
		for(int i = 0; i < vector.length; i++)
			vector[i] /= sum;
	}
	public float[] calcNormal(int x, int y) {
		float[] normal = {getVertexZ(x-1,y) - getVertexZ(x+1,y), getVertexZ(x,y-1) - getVertexZ(x,y+1), gridSize*2, 0};

		normalize(normal);
		return normal;
	}
	public void calcNormals() {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				normalGrid[x][y] = calcNormal(x,y);
	}
	public float[] getNormal(int x, int y) {
		return normalGrid[MathExt.contain(0,x,width-1)][MathExt.contain(0,y,height-1)];
	}

	public boolean collide(Physical other) {
		
		float flZ = getZ(other.getX(),other.getY());
		
		if(other.getZ() <= flZ+4) {
			other.setZVelocity(0);
			other.didCollideFloor(flZ);
		}
		
		return false;
	}
	
	public Chunk getChunk(float worldX, float worldY) {
		int xx, yy;
		xx = (int) Math.floor(worldX/chunkSize);
		yy = (int) Math.floor(worldY/chunkSize);
		
		return chunkGrid[xx][yy];
	}
	
	public void addEnvironmental(Environmental e) {
		getChunk(e.getX(),e.getY()).addEnvironmental(e);
	}
	
	public void add() {		
	}
	
	public void draw() {
		for(Chunk c : chunkList)
			c.draw();
	}


	/*public void smooth(int rad) {
		for(int x = rad; x < width-rad; x++)
			for(int y = rad; y < height-rad; y++) {
				float v = 0;
				for(int r = -rad; r < rad; r++)
					for(int c = -rad; c < rad; c++) {
						v += getVertexZ(x+c,y+r);
					}
				v /= MathExt.sqr(rad*2);
				heightGrid.set(x,y,v);
			}
		
		calcNormals();
	}*/

	public static Heightmap getInstance() {
		return instance;
	}
	
	public int[] generateColor(float height) {
		int r = 10, g = 10, b = 10;
		int aR = MathExt.rndi(-r,r),
			aG = MathExt.rndi(-g,g),
			aB = MathExt.rndi(-b,b);
		
		int[] color; 
		
		if(height < 140)
			color = new int[] {229,218,186};
		else
			color = new int[] {134,199,98};
		
		color[0] += aR;
		color[1] += aG;
		color[2] += aB;
		
		return color;
	}
	
	public vec3 generateRandomPoint() {
		float x = MathExt.rnd(getWidth()), y = MathExt.rnd(getHeight());
		return new vec3(x,y,getZ(x,y));
	}
	public vec3 generateRandomPointAbove(float minZ) {
		vec3 pt;	
		do {
			pt = generateRandomPoint();
		}
		while(pt.z() < minZ);
		
		return pt;
	}
	public vec3 generateRandomPointBetween(float bottom, float top) {
		vec3 pt;
		do
			pt = generateRandomPoint();
		while(pt.z() < bottom || pt.z() > top);
		
		return pt;
	}

	public vec3 raycastMouse() {return raycastPoint(Mouse.getMouseX(), Mouse.getMouseY());}
	public vec3 raycastPoint(float mouseX, float mouseY) {
		vec3 camNorm;
		vec3 pos;
		float dir, dirZ;
		
		pos = GOGL.getCamera().getPosition();
		camNorm = GOGL.getCamera().getNormal();
		
		dir = Math2D.calcPtDir(0,0, camNorm.x(),camNorm.y());
		dirZ = Math2D.calcPtDir(0,0, Math2D.calcPtDis(0,0,camNorm.x(),camNorm.y()),camNorm.z());
		
		
		vec3 uv = new vec3((mouseX-320)/320,-(mouseY-240)/240,0);
		uv.set(0, uv.get(0)*(640f/480));
		uv.set(2, -2.5f);
		uv.set(2, uv.get(2)+uv.len()*.05f); //.15f
		
		mat3 dirMat = mat3.fromEuler(GOGL.getCamera().getShaderNormal());
		vec3 dirVec = dirMat.mult((vec3) uv.norm());
			float dX, dY, dZ;
			dX = dirVec.get(0);
			dY = dirVec.get(1);
			dZ = dirVec.get(2);
		
		camNorm.set(-dZ,-dX,dY);
		

		if(camNorm.z() > 0)
			return new vec3(0,0,0);
		
		float jumpAmt = 20;
		
		while(pos.z() > getZ(pos.x(),pos.y()))
			pos.adde( camNorm.mult(jumpAmt) );
		
		return pos;
	}


	/*public void halveResolution() {
		
		gridSize *= 2;
		
		width /= 2;
		height /= 2;
		
		float[][] grid = new float[width][height];
		
		vertexGrid = new float[width][height][4];
		normalGrid = new float[width][height][4];
		colorGrid = new int[width][height][3];
				
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				vertexGrid[x][y][0] = x*gridSize;
				vertexGrid[x][y][1] = y*gridSize;
				grid[x][y] = vertexGrid[x][y][2] = heightGrid.get(2*x, 2*y);
				vertexGrid[x][y][3] = 1;
				
				colorGrid[x][y] = generateColor(grid[x][y]);
			}
				
		heightGrid = new LinearGrid(grid);
		calcNormals();
	}*/
	
	public void updateLighting() {		
		PlayerPM p = PlayerPM.getInstance();
		float px, py, pz;
		px = p.getX();
		py = p.getY();
		pz = p.getZ();
		
		int lX, rX, tY, bY;
		lX = (int) Math.floor((px-lightDis)/chunkSize);
		rX = (int) Math.floor((px+lightDis)/chunkSize);
		tY = (int) Math.floor((py-lightDis)/chunkSize);
		bY = (int) Math.floor((py+lightDis)/chunkSize);
		
		int xInd, yInd;
		float worldX, worldY, worldZ;
		
		Chunk c;
		
		for(int xx = lX; xx <= rX; xx++)
			for(int yy = tY; yy <= bY; yy++) {
				c = chunkGrid[xx][yy];
				c.markDirty();
				
				if(c.checkOnscreen())
					for(int x = 0; x < 16; x++)
						for(int y = 0; y < 16; y++) {
							xInd = xx*16+x;
							yInd = yy*16+y;
							
							worldX = xInd*gridSize;
							worldY = yInd*gridSize;
							worldZ = getVertexZ(xInd,yInd);
							
							luxGrid[xInd][yInd]	= (int) (Math.max(32,lightDis - Math3D.calcPtDis(px,py,pz,  worldX,worldY,worldZ))/lightDis*255);
						}
			}
	}

	public void update() {
		updateLighting();
		
		for(Chunk c : chunkList)
			c.update();
	}

	public float getGridSize() {
		return gridSize;
	}
	
	
	private class Chunk {
		private int xIndex, yIndex;
		private Model mod;
		private boolean isDirty, hasChecked, isOnscreen;
		private CleanList<Environmental> envList;
		
		public Chunk(int cX, int cY) {
			xIndex = cX;
			yIndex = cY;
			
			isDirty = true;
		}

		public void addEnvironmental(Environmental e) {
			envList.add(e);
		}

		public void markDirty() {
			isDirty = true;
		}
		
		public boolean checkOnscreen() {
			if(hasChecked)
				return isOnscreen;
				
			Camera cam = GOGL.getMainCamera();
			
			float wX, wY;
			wX = getWorldX();
			wY = getWorldY();
			
			isOnscreen = cam.checkOnscreen(wX,wY) ||
					cam.checkOnscreen(wX+chunkSize,wY) ||
					cam.checkOnscreen(wX,wY+chunkSize) || 
					cam.checkOnscreen(wX+chunkSize,wY+chunkSize);
			hasChecked = true;
			
			return isOnscreen;
		}

		public void update() {
			if(isDirty)
				render();
			isDirty = false;
		}
		public void draw() {
			if(mod != null)
				if(checkOnscreen())
					mod.drawFast();
			hasChecked = false;
		}
		
		public float getWorldX() {
			return xIndex*chunkSize;
		}
		public float getWorldY() {
			return yIndex*chunkSize;
		}
		
		public boolean containsPoint(float worldX, float worldY) {
			float 	relX = worldX-getWorldX(),
					relY = worldY-getWorldY();
			return (0 <= relX && relX < chunkSize && 0 <= relY && relY < chunkSize);
		}
		
		
		public void render() {
			
			if(mod != null)
				mod.destroy();
			
			int x, y;
			
			float[] vN1, vN2, vN3, vN4;
			int[] vC1, vC2, vC3, vC4;
			float vL1, vL2, vL3, vL4;
			float vX1,vY1,vZ1, vX2,vY2,vZ2, vX3,vY3,vZ3, vX4,vY4,vZ4;		
			int xAmt, yAmt;
			
			if((xIndex+1)*16 == width)
				xAmt = 15;
			else
				xAmt = 16;
			
			if((yIndex+1)*16 == height)
				yAmt = 15;
			else
				yAmt = 16;
			
			for(int yy = 0; yy < yAmt; yy++) {
				vY1 = vY2 = getWorldY() + yy*gridSize;
				vY3 = vY4 = getWorldY() + (yy+1)*gridSize;
				y = yIndex*16+yy;
				
				for(int xx = 0; xx < xAmt; xx++) {
					x = xIndex*16+xx;
					
					vX1 = vX3 = getWorldX() + xx*gridSize;
					vX2 = vX4 = getWorldX() + (xx+1)*gridSize;

					vN1 = getNormal(x,y);
					vN2 = getNormal(x+1,y);
					vN3 = getNormal(x,y+1);
					vN4 = getNormal(x+1,y+1);
										
					vZ1 = getVertexZ(x,y);
					vZ2 = getVertexZ(x+1,y);
					vZ3 = getVertexZ(x,y+1);
					vZ4 = getVertexZ(x+1,y+1);
					
					vC1 = colorGrid[MathExt.contain(0,x,width-1)][MathExt.contain(0,y,height-1)];
					vC2 = colorGrid[MathExt.contain(0,x+1,width-1)][MathExt.contain(0,y,height-1)];
					vC3 = colorGrid[MathExt.contain(0,x,width-1)][MathExt.contain(0,y+1,height-1)];
					vC4 = colorGrid[MathExt.contain(0,x+1,width-1)][MathExt.contain(0,y+1,height-1)];

					vL1 = luxGrid[x][y]/255f;
					vL2 = luxGrid[x+1][y]/255f;
					vL3 = luxGrid[x][y+1]/255f;
					vL4 = luxGrid[x+1][y+1]/255f;
			
					modC.setColor(vC1[0],vC1[1],vC1[2]);
						modC.setBrightness(vL1);
						modC.addVertexBT(vX1,vY1,vZ1,	vN1[0],vN1[1],vN1[2]);
					modC.setColor(vC2[0],vC2[1],vC2[2]);
						modC.setBrightness(vL2);
						modC.addVertexBT(vX2,vY2,vZ2,	vN2[0],vN2[1],vN2[2]);
					modC.setColor(vC3[0],vC3[1],vC3[2]);
						modC.setBrightness(vL3);
						modC.addVertexBT(vX3,vY3,vZ3,	vN3[0],vN3[1],vN3[2]);
					
					modC.setColor(vC2[0],vC2[1],vC2[2]);
						modC.setBrightness(vL2);
						modC.addVertexBT(vX2,vY2,vZ2,	vN2[0],vN2[1],vN2[2]);
					modC.setColor(vC4[0],vC4[1],vC4[2]);
						modC.setBrightness(vL4);
						modC.addVertexBT(vX4,vY4,vZ4,	vN4[0],vN4[1],vN4[2]);
					modC.setColor(vC3[0],vC3[1],vC3[2]);
						modC.setBrightness(vL3);
						modC.addVertexBT(vX3,vY3,vZ3,	vN3[0],vN3[1],vN3[2]);
				}			
			}
			
			mod = modC.endModel();
		}
	}
}
