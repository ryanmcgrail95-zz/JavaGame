package object.environment;

import io.IO;
import io.Mouse;
import object.actor.Player;
import object.primitive.Collideable;
import object.primitive.Drawable;
import object.primitive.Physical;
import resource.model.Model;
import functions.Math2D;
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

public class Heightmap2 extends Drawable implements Collideable {
	private static Heightmap2 instance;
	private float gridSize;
	private LinearGrid heightGrid;
	
	private float[][][] vertexGrid, normalGrid;
	private int[][][] colorGrid;
	
	protected int width, height;	
	
	public Heightmap2(float gridSize, float maxHeight, String fileName) {
		super(false,false);
		
		name = "Heightmap";
		instance = this;
		
		shouldAdd = true;
		visible = false;
		
		doUpdates = false;
		this.gridSize = gridSize;
		
		loadFromTexture(maxHeight, TextureController.load(fileName, fileName, TextureController.M_BGALPHA));
	}
	
	public Heightmap2(float gridSize, float maxHeight, TextureExt tex) {

		super(false,false);
		instance = this;
		
		shouldAdd = true;
		
		doUpdates = false;
		this.gridSize = gridSize;		
		loadFromTexture(maxHeight, tex);
	}
	
	public void loadFromTexture(float maxHeight, TextureExt tex) {
		width = tex.getWidth();
		height = tex.getHeight();
		
		float[][] grid = new float[width][height];

		vertexGrid = new float[width][height][4];
		colorGrid = new int[width][height][3];
		
		normalGrid = new float[width][height][4];
	
		float maxH, minH;
		maxH = 0;
		minH = 1000000;
		
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				vertexGrid[x][y][0] = x*gridSize;
				vertexGrid[x][y][1] = y*gridSize;
				grid[x][y] = vertexGrid[x][y][2] = maxHeight*tex.getPixelColor(x,y).getRi();
				vertexGrid[x][y][3] = 1;
				
				colorGrid[x][y] = generateColor(grid[x][y]);
				
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
	protected float getVertexZ(int x, int y) {
		return vertexGrid[MathExt.contain(0,x,width-1)][MathExt.contain(0,y,height-1)][2];
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
	
	public void add() {
		
		float[] v1, v2, v3, v4;
		float[] vN1, vN2, vN3, vN4;
		int[] vC1, vC2, vC3, vC4;
		
		GL2 gl = GOGL.gl;
				
		//GOGL.enableLighting();
		//GOGL.setLightColori(73,214,127);
		
		float r, pX, pY;
		r = 3000;
		pX = Player.getInstance().getX();
		pY = Player.getInstance().getY();
		int leftX, rightX, topY, botY;
		leftX = (int) Math.max(0,(pX-r)/gridSize);
		rightX = (int) Math.min(width,(pX+r)/gridSize);
		topY = (int) Math.max(0,(pY-r)/gridSize);
		botY = (int) Math.min(height-1,(pY+r)/gridSize);
		
		
		float vX1,vY1,vZ1, vX2,vY2,vZ2, vX3,vY3,vZ3, vX4,vY4,vZ4;		
		for(int y = topY; y < botY; y++) {
			vY1 = vY2 = y*gridSize;
			vY3 = vY4 = (y+1)*gridSize;
			
			for(int x = leftX; x < rightX; x++) {
				vX1 = vX3 = x*gridSize;
				vX2 = vX4 = (x+1)*gridSize;

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

				GOGL.setLightColor(vC1[0],vC1[1],vC1[2]);
				//gl.glColor3i(vC1[0],vC1[1],vC1[2]);
					gl.glNormal3f(vN1[0], vN1[1], vN1[2]);
					gl.glVertex3f(vX1,vY1,vZ1);
				GOGL.setLightColor(vC2[0],vC2[1],vC2[2]);
				//gl.glColor3i(vC2[0],vC2[1],vC2[2]);
					gl.glNormal3f(vN2[0], vN2[1], vN2[2]);
					gl.glVertex3f(vX2,vY2,vZ2);
				GOGL.setLightColor(vC3[0],vC3[1],vC3[2]);
				//gl.glColor3i(vC3[0],vC3[1],vC3[2]);
					gl.glNormal3f(vN3[0], vN3[1], vN3[2]);
					gl.glVertex3f(vX3,vY3,vZ3);		

				GOGL.setLightColor(vC2[0],vC2[1],vC2[2]);
					//gl.glColor3i(vC2[0],vC2[1],vC2[2]);				
					gl.glNormal3f(vN2[0], vN2[1], vN2[2]);
					gl.glVertex3f(vX2,vY2,vZ2);
				GOGL.setLightColor(vC4[0],vC4[1],vC4[2]);
				//gl.glColor3i(vC4[0],vC4[1],vC4[2]);
					gl.glNormal3f(vN4[0], vN4[1], vN4[2]);
					gl.glVertex3f(vX4,vY4,vZ4);
				GOGL.setLightColor(vC3[0],vC3[1],vC3[2]);
				//gl.glColor3i(vC3[0],vC3[1],vC3[2]);
					gl.glNormal3f(vN3[0], vN3[1], vN3[2]);
					gl.glVertex3f(vX3,vY3,vZ3);
			}			
		}
				
		
		GOGL.setColor(1,1,1);
	}
	
	public void draw() {
		
		/*vec3 norm;

		GL2 gl = GOGL.gl;
				
		//GOGL.enableLighting();
		//GOGL.setLightColori(134,199,98); //GOGL.setLightColori(73,214,127);
		
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
		}		*/
	}


	public void smooth(int rad) {
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
	}

	public static Heightmap2 getInstance() {
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


	public void halveResolution() {
		
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
	}

	public void update() {}

	public float getGridSize() {
		return gridSize;
	}
	
	
	
	
	
	
	private class Section {
		
	}
	
	private class Chunk {
		
	}
}
