package object.environment;

import io.IO;
import io.Mouse;
import object.actor.Player;
import object.primitive.Collideable;
import object.primitive.Drawable;
import object.primitive.Physical;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;
import gfx.TextureExt;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import datatypes.LinearGrid;
import datatypes.mat3;
import datatypes.vec2;
import datatypes.vec3;

public class Heightmap extends Drawable implements Collideable {
	private static Heightmap instance;
	private float gridSize;
	private LinearGrid heightGrid;
	private vec3[][] normalGrid;
	protected int width, height;
	
	
	public Heightmap(float gridSize, float maxHeight, String fileName) {
		
		super(false,false);
		instance = this;
		
		doUpdates = false;
		this.gridSize = gridSize;
		loadFromTexture(maxHeight, TextureController.load(fileName, fileName, TextureController.M_BGALPHA));
	}
	
	public Heightmap(float gridSize, float maxHeight, TextureExt tex) {

		super(false,false);
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
	
		float maxH, minH;
		maxH = 0;
		minH = 1000000;
		
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				grid[x][y] = maxHeight*tex.getPixelColor(x,y).getRi();

				maxH = Math.max(maxH,grid[x][y]);
				minH = Math.min(minH,grid[x][y]);
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
		
		return (vec3) v.norm();
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
	
	public void draw() {
		
		vec3 norm;
				
		GOGL.enableLighting();
		GOGL.setLightColori(134,199,98); //GOGL.setLightColori(73,214,127);
		
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

	public void update() {}
}
