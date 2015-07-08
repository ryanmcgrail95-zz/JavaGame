package object.environment;

import io.IO;
import io.Mouse;
import object.actor.Player;
import object.primitive.Physical;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;
import gfx.TextureExt;
import Datatypes.LinearGrid;
import Datatypes.mat3;
import Datatypes.vec2;
import Datatypes.vec3;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;

public class Heightmap extends Physical {
	private static Heightmap instance;
	private float gridSize;
	private LinearGrid heightGrid;
	private vec3[][] normalGrid;
	protected int width, height;
	
	
	public Heightmap(float gridSize, float maxHeight, String fileName) {
		
		super(0,0,0);
		instance = this;
		
		doUpdates = false;
		this.gridSize = gridSize;
		loadFromTexture(maxHeight, TextureController.loadTexture(fileName, fileName, TextureController.M_BGALPHA));
	}
	
	public Heightmap(float gridSize, float maxHeight, TextureExt tex) {

		super(0,0,0);
		instance = this;
		
		doUpdates = false;
		this.gridSize = gridSize;		
		loadFromTexture(maxHeight, tex);
	}
	
	public void loadFromTexture(float maxHeight, TextureExt tex) {
		width = tex.getWidth();
		height = tex.getHeight();
		
		float[][] grid = new float[width][height];
		normalGrid = new vec3[width][height];
				
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				grid[x][y] = maxHeight*tex.getPixelColor(x,y).getValue();
				
		heightGrid = new LinearGrid(grid);
		
		calcNormals();
	}
	
	
	public float getWidth() {
		return width*gridSize;
	}
	public float getHeight() {
		return height*gridSize;
	}
	public float getZ(float x, float y) {
		return heightGrid.get(x/gridSize,y/gridSize);
	}
	protected float get(int x, int y) {
		return heightGrid.get(x,y);
	}
	
	
	public vec3 calcNormal(int x, int y) {
		vec3 v = new vec3(
				get(x-1,y) - get(x+1,y),
				get(x,y-1) - get(x,y+1),
				gridSize*2);
		
		return v.norm();
	}
	public void calcNormals() {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				normalGrid[x][y] = calcNormal(x,y);
	}
	public vec3 getNormal(int x, int y) {
		return normalGrid[x][y];
	}

	public boolean collide(Physical other) {
		
		float flZ = getZ(other.getX(),other.getY());
		
		if(other.getZ() <= flZ+4) {
			other.setZVelocity(0);
			other.didCollideFloor(flZ);
		}
		
		return false;
	}

	public void land() {
	}
	
	public boolean draw() {
		
		vec3 norm;
				
		GOGL.enableLighting();
		GOGL.setLightColori(128, 135, 35);
		
		float r, pX, pY;
		r = 3000;
		pX = Player.getInstance().getX();
		pY = Player.getInstance().getY();
		int leftX, rightX, topY, botY;
		leftX = (int) Math.max(0,(pX-r)/gridSize);
		rightX = (int) Math.min(width,(pX+r)/gridSize);
		topY = (int) Math.max(0,(pY-r)/gridSize);
		botY = (int) Math.min(height-1,(pY+r)/gridSize);
		
		for(int y = topY; y < botY; y++) {
			GOGL.begin(GOGL.P_TRIANGLE_STRIP);
			
			for(int x = leftX; x < rightX; x++) {
				
				for(int i = 1; i >= 0; i--) {
					norm = getNormal(x,y+i);
				    GOGL.vertex(x*gridSize,(y+i)*gridSize,get(x,y+i),0,0,norm.get(0), norm.get(1), norm.get(2));
				}
			}
			
  			GOGL.end();
		}
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
		return false;
	}

	public void smooth(int rad) {
		for(int x = rad; x < width-rad; x++)
			for(int y = rad; y < height-rad; y++) {
				float v = 0;
				for(int r = -rad; r < rad; r++)
					for(int c = -rad; c < rad; c++) {
						v += get(x+c,y+r);
					}
				v /= MathExt.sqr(rad*2);
				heightGrid.set(x,y,v);
			}
		
		calcNormals();
	}

	public static Heightmap getInstance() {
		return instance;
	}
	
	public vec3 generateRandomPoint() {
		float x = MathExt.rnd(getWidth()), y = MathExt.rnd(getHeight());
		return new vec3(x,y,getZ(x,y));
	}
	public vec3 generateRandomPointAbove(float minZ) {
		vec3 pt;
		
		do
			pt = generateRandomPoint();
		while(pt.get(2) < minZ);
		
		return pt;
	}
	
	public vec3 raycastPoint(float mouseX, float mouseY) {
		vec3 camNorm;
		float pX, pY, pZ;
		float dir, dirZ;
		float nX,nY,nZ;
		
		pX = Camera.getX();
		pY = Camera.getY();
		pZ = Camera.getZ();
		
		camNorm = Camera.getNormal();
		nX = camNorm.get(0);
		nY = camNorm.get(1);
		nZ = camNorm.get(2);
		
		dir = Math2D.calcPtDir(0,0, nX,nY);
		dirZ = Math2D.calcPtDir(0,0, Math2D.calcPtDis(0,0,nX,nY), nZ);
		
		//dir += (320-mouseX)/320*40;
		
		vec3 uv = new vec3((mouseX-320)/320,-(mouseY-240)/240,0);
		uv.set(0, uv.get(0)*(640f/480));
		uv.set(2, -2.5f);
		uv.set(2, uv.get(2)+uv.len()*.05f); //.15f
		
		mat3 dirMat = mat3.fromEuler(Camera.getShaderNormal());
		vec3 dirVec = dirMat.mult(uv.norm());
			dirVec.println();
			float dX, dY, dZ;
			dX = dirVec.get(0);
			dY = dirVec.get(1);
			dZ = dirVec.get(2);
		
		nX = -dZ;
		nY = -dX;
		nZ = dY;
		
		/*nX = Math2D.calcPolarX(1, dir,dirZ);
		nY = Math2D.calcPolarY(1, dir,dirZ);
		nZ = Math2D.calcPolarZ(1, dir,dirZ);*/

		if(nZ > 0)
			return new vec3(0,0,0);
		
		float jumpAmt = 20;
		
		while(pZ > getZ(pX,pY)) {
			pX += jumpAmt*nX;
			pY += jumpAmt*nY;
			pZ += jumpAmt*nZ;
		}
		
		return new vec3(pX,pY,pZ);
	}

	public vec3 raycastMouse() {return raycastPoint(Mouse.getMouseX(), Mouse.getMouseY());}

	public void halveResolution() {
		
		gridSize *= 2;
		
		width /= 2;
		height /= 2;
		
		float[][] grid = new float[width][height];
		normalGrid = new vec3[width][height];
				
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++)
				grid[x][y] = heightGrid.get(2*x, 2*y);
				
		heightGrid = new LinearGrid(grid);
		calcNormals();
	}
}
