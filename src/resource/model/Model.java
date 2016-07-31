package resource.model;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import resource.Resource;
import time.Stopwatch;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import ds.mat;
import ds.mat4;
import ds.lst.CleanList;
import functions.ArrayMath;
import gfx.GL;
import gfx.RGBA;
import object.primitive.Drawable;

public class Model extends Resource {
		
	private final static int
		SIZE_F = Buffers.SIZEOF_FLOAT,
		SIZE_I = Buffers.SIZEOF_INT,

		POS_SIZE = 3 * SIZE_F,		POS_OFFSET = 0,
		UV_SIZE = 2 * SIZE_F, 		UV_OFFSET = POS_OFFSET + POS_SIZE,
		NORM_SIZE = 3 * SIZE_F,		NORM_OFFSET = UV_OFFSET + UV_SIZE,
		COL_SIZE = 4 * SIZE_F,		COL_OFFSET = NORM_OFFSET + NORM_SIZE,
		
		TOT_SIZE = POS_SIZE+UV_SIZE+NORM_SIZE+COL_SIZE;
	
	private float[][] pointList;
	private float[][] normalList;
	private float[][] uvList;
	private int[][] vertexList;
	private int[] colorList;
	private int vertexNum;
	private Material[] materials;
	private Submodel[] submodelList;
	
	private float[] preMatrix = new float[16];
	
	private static Map<String, Model> modelMap = new HashMap<String, Model>();
	
	private float[][][] triangles;
	
	private String key;
	
	public final static int TRIANGLES = GL2.GL_TRIANGLES, QUADS = GL2.GL_QUADS;
	private int modelType;
	
	private boolean hasColor;
	
	private static CleanList<Model> modList = new CleanList<Model>("Mods");

	public static Model 
		MOD_PINEBRANCHES, MOD_PINETREE, MOD_PINESTUMP,
		MOD_WMBLADES, MOD_WMFRAME, MOD_WMBODY,
		MOD_HOUSEBODY, MOD_HOUSEFRAME,
		MOD_BOWL,
		MOD_FERN,
		MOD_TABLE,
		MOD_BATTLE,
		MOD_BATTLE2,
		MOD_CASTLE,
		MOD_FLOWER;
	
	public static void ini() {
		
		/*MOD_WMBLADES = OBJLoader.load("windmill").fix();
		MOD_WMFRAME = OBJLoader.load("windmillframe").fix();
		MOD_WMBODY = OBJLoader.load("windmillbody").fix();
		
		MOD_PINEBRANCHES = OBJLoader.load("pinebranches").fix();
		MOD_PINETREE = OBJLoader.load("pinetree").fix();
		MOD_PINESTUMP = OBJLoader.load("pinestump").fix();
		
		MOD_HOUSEBODY = OBJLoader.load("housebody").fix();
		MOD_HOUSEFRAME = OBJLoader.load("houseframe").fix();
		
		MOD_BOWL = OBJLoader.load("bowl").fix();
			MOD_BOWL.flipNormals();
		
		MOD_FERN = OBJLoader.load("fern").fix();
			MOD_FERN.flipNormals();*/
		
		/*MOD_CASTLE = OBJLoader.load("Model/output").fix();
			MOD_CASTLE.mirrorUVVertically();*/
	}
	
	
	private class Submodel {
		private int vertexBuffer, subVertexNum;
		private Material mat;
		
		
		public Submodel(int vertexBuffer, int subVertexNum, Material mat) {
			this.vertexBuffer = vertexBuffer;
			this.subVertexNum = subVertexNum;
			this.mat = mat;
		}

		public void destroy(GL2 gl) {
			gl.glDeleteBuffers(1, new int[] {vertexBuffer}, 0);
			
			if(mat != null) {
				mat.destroy();
				mat = null;
			}
		}
		
		public void draw() {
			
			if(mat != null)
				mat.enable();
			
			GL2 gl = GL.getGL2();
			
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
			
			gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			//gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
					
			gl.glVertexPointer( 3, GL2.GL_FLOAT, TOT_SIZE, POS_OFFSET);
			gl.glTexCoordPointer(2, GL2.GL_FLOAT, TOT_SIZE, UV_OFFSET);
			gl.glNormalPointer( GL2.GL_FLOAT, TOT_SIZE, NORM_OFFSET);
			//gl.glColorPointer(4, GL2.GL_FLOAT, TOT_SIZE, COL_OFFSET);
			
			int colorAttrib = gl.glGetAttribLocation(GL.getShaderProgram(), "iColor");
			gl.glVertexAttribPointer(colorAttrib, 4, GL2.GL_FLOAT,false, TOT_SIZE, COL_OFFSET);
			gl.glEnableVertexAttribArray(colorAttrib);
		
			// Draw the buffer
			gl.glPolygonMode( GL2.GL_FRONT, GL2.GL_FILL );
			gl.glDrawArrays(modelType, 0, subVertexNum);
			
			// Unbind the buffer

			// Disable the different kinds of data 
			gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );
			
			gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
			//gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
			gl.glDisableVertexAttribArray(colorAttrib);

		
			if(mat != null)
				mat.disable();
		}
	}
	
	
	
	private Model(String fileName, String key, boolean isTemporary) {
		super(fileName, Resource.R_MODEL, isTemporary);

		//System.out.println(fileName + "---------------------------------------------");

		modelMap.put(this.key = key,this);
		
		mat4.createIdentityArray(preMatrix);
	}
	public Model() {
		super("", Resource.R_MODEL, true);
		
		//System.out.println("NEW MODEL---------------------------------------------");

		//modelMap.put(removeType(""),this);
		
		mat4.createIdentityArray(preMatrix);
	}
	
	public void create(int modelType, List<float[]> pointList, List<float[]> normalList, List<float[]> uvList, List<int[]> vertexList) {
		this.modelType = modelType;
		setAll(pointList, normalList, uvList, new ArrayList<Integer>(), vertexList);
		
		hasColor = false;
		
		modList.add(this);
	}
	
	public void create(int modelType, List<float[]> pointList, List<float[]> normalList, List<float[]> uvList, List<Integer> colorList, List<int[]> vertexList) {		
		this.modelType = modelType;		
		setAll(pointList,normalList,uvList,colorList,vertexList);
		
		hasColor = colorList.size() > 0;
				
		modList.add(this);
	}
	
	private void setAll(List<float[]> pointList, List<float[]> normalList, List<float[]> uvList, List<Integer> colorList, List<int[]> vertexList) {
		int pointLSize = pointList.size(),
			normLSize = normalList.size(),
			uvLSize = uvList.size(),
			colLSize = colorList.size(),
			vertLSize = vertexNum = vertexList.size();
		
		this.pointList = new float[pointLSize][];
		this.normalList = new float[normLSize][];
		this.uvList = new float[uvLSize][];
		this.colorList = new int[colLSize];
		this.vertexList = new int[vertLSize][];
		
		for(int i = 0; i < pointLSize; i++)
			this.pointList[i] = pointList.get(i);
		for(int i = 0; i < normLSize; i++)
			this.normalList[i] = normalList.get(i);
		for(int i = 0; i < uvLSize; i++)
			this.uvList[i] = uvList.get(i);
		for(int i = 0; i < colLSize; i++)
			this.colorList[i] = colorList.get(i);
		for(int i = 0; i < vertLSize; i++)
			this.vertexList[i] = vertexList.get(i);
	}
	
	public void destroy() {
		System.out.println("**Destroyed model " + getFileName());

		modList.remove(this);
		modelMap.remove(key);
	}
	
	public void unload() {
		super.unload();
		
		// Delete Arrays
		// Delete Material
		
		for(int i = 0; i < pointList.length; i++)
			pointList[i] = null;
		for(int i = 0; i < normalList.length; i++)
			normalList[i] = null;
		for(int i = 0; i < uvList.length; i++)
			uvList[i] = null;
		for(int i = 0; i < vertexList.length; i++)
			vertexList[i] = null;
		pointList = null;
		normalList = null;
		uvList = null;
		vertexList = null;
		colorList = null;
		
		// Delete GL Vertex Index/Buffer
		GL2 gl = GL.getGL2();

		if(submodelList != null)
			for(Submodel s : submodelList)
				s.destroy(gl);
		submodelList = null;
		
		// Empty Vertex Buffer
		
		// Delete Materials
		if(materials != null) {
			for(Material m : materials)
				m.destroy();
			materials = null;
		}
		
		destroyTriangles();
	}
	
	public void draw() {
		GL2 gl = (GL2) GL.getGL();
		
		int p,u,n,c;
		int[] color = new int[4];
		
		Material curMat = null;
				
		GL.disableTextures();
		GL.enableBlending();
		GL.enableInterpolation();
		gl.glBegin(GL2.GL_TRIANGLES);
		for(int[] v : vertexList) {			
			p = v[0];
			u = v[1];
			n = v[2];
			c = v[3];
			
			if(p == -1) {
				if(curMat != null)
					curMat.disable();
				gl.glEnd();

				curMat = materials[u];
				curMat.enable();
				gl.glBegin(GL2.GL_TRIANGLES);
			}
			else {
				if(u != -1)
					gl.glTexCoord2fv(uvList[u],0);
				if(n != -1)
					gl.glNormal3fv(normalList[n],0);
				if(c != -1) {
					RGBA.convertInt2RGBA(colorList[c],color);
					GL.setColor(RGBA.create(colorList[c]));
					
					System.out.println("WASTEFUL RGBA MODEL.draw");
					gl.glColor4f(color[0]/255f,color[1]/255f,color[2]/255f,255f);
				}
				gl.glVertex3fv(pointList[p],0);
			}
		}
		gl.glEnd();
		
		GL.disableTextures();
	}
	
	
	
	public void drawFast() {
		if(!this.isLoaded())
			throw new UnsupportedOperationException("Model \"" + getFileName() + "\" is not loaded yet!");
		
		//GL.start("Model[" + getFileName() + "].drawFast()");
		for(Submodel s : submodelList)
			s.draw();		
		//GL.end("Model.drawFast()");
	}
	
	public void add() {
		/*GL2 gl = GL.getGL2();
		
		mat4 m = GL.getModelMatrix();
		float[] matA = m.array();
		
		int p,u,n;
		int[] color;
		for(int[] v : vertexList) {			
			p = v[0];
			u = v[1];
			n = v[2];
			
			if(hasColor) {
				color = RGBA.convertInt2RGBA(colorList[v[3]]);
				GL.setLightColor(color[0],color[1],color[2]);
			}

			if(u != -1)
				gl.glTexCoord2fv(uvList[u],0);
			if(n != -1)
				gl.glNormal3fv(mult(matA,normalList[n]),0);
			gl.glVertex3fv(mult(matA,pointList[p]),0);
		}
		
		m.destroy();*/
	}

	
	public void transform(float[] transformMatrix) {		
		destroyTriangles();
		if(pointList == null)
			ArrayMath.multMM(preMatrix, transformMatrix, preMatrix);
		else {
			float[] curVertex = new float[] {0,0,0,1};

			for(float[] v : pointList) {
				curVertex[0] = v[0];
				curVertex[1] = v[1];
				curVertex[2] = v[2];
				curVertex[3] = 1;
				
				ArrayMath.multMV(transformMatrix, curVertex, curVertex);
				
				v[0] = curVertex[0];
				v[1] = curVertex[1];
				v[2] = curVertex[2];
			}
		}
	}

	public void translate(float tX, float tY, float tZ) {
		transform(ArrayMath.transpose(mat4.createTranslationArray(tX,tY,tZ)));
	}
	
	public void rotateX(float rot) {
		transform(mat4.createRotationXArray(rot));
	}
	public void rotateY(float rot) {
		transform(mat4.createRotationYArray(rot));
	}
	public void rotateZ(float rot) {
		transform(mat4.createRotationZArray(rot));
	}
	
	public void scale(float scaleFactor) {scale(scaleFactor,scaleFactor,scaleFactor);}
	public void scale(float sX, float sY, float sZ) {
		//GL.start("Model["+this.getFileName()+"].scale()");
		transform(mat4.createScaleArray(sX,sY,sZ));
		//GL.end("Model.scale()");
	}
	
	public void flipNormals() {
		for(float[] v : normalList) {
			v[0] *= -1;	v[1] *= -1;	v[2] *= -1;
		}
	}
	
	public void attachMaterials(Material[] mat) {
		materials = mat;
	}
	
	public void mirrorUVVertically() {
		for(float[] uv : uvList)
			uv[1] = 1-uv[1];
	}
	
	
	// Allow Model to Be Created this way in first place
	
	protected void createAndFillVertexBuffer() {	    
		int numBuffers;
		if(materials == null)
			numBuffers = 1;
		else
			numBuffers = materials.length;
				
		int[] bufferInd = new int[numBuffers],
			bufferSizes = new int[numBuffers];
		
		GL2 gl = (GL2) GL.getGL();
		
		
	    // create vertex buffer object if needed
        gl.glGenBuffers(numBuffers, bufferInd, 0 );
 
        // Get Size of Each Buffer
        int curBuff = 0;
        for(int[] v : vertexList)
        	if(v[0] == -1)
        		curBuff = v[1];
        	else
        		bufferSizes[curBuff]++;
        
        
        /*for(int i = 0; i < numBuffers; i++)
        	System.out.println(i + ": " + bufferSizes[i]);*/
        
        
        // create vertex buffer data store without initial copy
        for(int i = 0; i < numBuffers; i++) {
        	gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferInd[i]);
        	gl.glBufferData(GL2.GL_ARRAY_BUFFER, bufferSizes[i]*TOT_SIZE, null, GL2.GL_DYNAMIC_DRAW );
        }
        
	    // map the buffer and write vertex and color data directly into it
        curBuff = 0;
        
	    gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, bufferInd[curBuff] );
	    ByteBuffer bytebuffer = gl.glMapBuffer( GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY );
	    
	    int[] lastPositions = new int[numBuffers];
	    for(int i : lastPositions)
	    	i = 0;
	    
	    //System.out.println("SIZE: " + TOT_SIZE);
	 	    	    
	    float[] array;
	    int[] v, color = {0,0,0,0};
	    int i = 0, k = 0, t = 0;
	    for(k = 0; k < vertexNum; k++) {
	    	v = vertexList[k];
	    	
	    	if(v[0] == -1) {
	    	    //System.out.println(curBuff + ": " + bytebuffer.position() + " / " + bytebuffer.capacity());

	    		curBuff = v[1];
	    		bytebuffer.position(0);
	    		
	    	    gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
	    	    gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, bufferInd[curBuff]);
	    	    bytebuffer = gl.glMapBuffer( GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY );
	    	    	    	    
	    	    bytebuffer.position(lastPositions[curBuff]);
	    	    
	    	    //System.out.println("\t" + curBuff + ": " + bytebuffer.position() + " / " + bytebuffer.capacity());

	    	    continue;
	    	}
	    	
    	    //System.out.println("\t\t" + curBuff + ": " + bytebuffer.position() + " / " + bytebuffer.capacity());
	    		    	
	    	// Add Point
	    	array = pointList[v[0]];
	    	for(i = 0; i < 3; i++)
	    		bytebuffer.putFloat(array[i]);
	    	
	    	// Add UVs
	    	if((i = v[1]) != -1) {
		    	array = uvList[i];
		    	for(i = 0; i < 2; i++)
		    		bytebuffer.putFloat(array[i]);
	    	}
	    	else
	    		for(i = 0; i < 2; i++)
		    		bytebuffer.putFloat(0);
	    	
	    	// Add Normals
	    	array = normalList[v[2]];
	    	for(i = 0; i < 3; i++)
	    		bytebuffer.putFloat(array[i]);
	    	
	    	// Add Color
	    	if(hasColor)
	    		RGBA.convertInt2RGBA(colorList[v[3]],color);
	    	for(i = 0; i < 4; i++)
	    		bytebuffer.putFloat(color[i]/255f);
	    	
	    	lastPositions[curBuff] += TOT_SIZE;
	    }   

	    bytebuffer.position(0);	    
	    gl.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);
	    
	    
	    submodelList = new Submodel[numBuffers];
	    if(materials != null) {
	    	for(int n = 0; n < numBuffers; n++)
	    		submodelList[n] = new Submodel(bufferInd[n], bufferSizes[n], materials[n]);
	    }
	    else
	    	submodelList[0] = new Submodel(bufferInd[0], bufferSizes[0], null);
	}
	
	public int getVertexNumber() {
		int p = 0;
		for(int[] v : vertexList)
			if(v[0] != -1)
				p++;
		return p;
	}
	public static int getNumber() {
		return modList.size();
	}
	
	
	public float[][][] getTriangles() {
		if(triangles != null)
			return triangles;
		
		int triNum = getVertexNumber()/3;

		triangles = new float[triNum][3][3];
		
		int p, vi = 0;
		for(int tri = 0; tri < triNum; tri++)
			for(int v = 0; v < 3; v++) {
				do
					p = vertexList[vi++][0];
				while(p == -1);
				
				for(int i = 0; i < 3; i++)
					triangles[tri][v][i] = pointList[p][i];
			}
		
		return triangles;
	}


	public void load(String fileName, int... args) {
		if(getTemporary())
			return;
		
		

		
		if(fileName != "") {
			// Necessary for Fixing Model
			scale(2f);
			scale(1,-1,-1);

			
			OBJLoader.loadInto(fileName, this);
			
			flipNormals();
		}
		
		transform(preMatrix);
		preMatrix = null;
		
		mirrorUVVertically();

		
		createAndFillVertexBuffer();
	}
	
	public void destroyTriangles() {
		if(triangles == null)
			return;
		
		for(float[][] i : triangles) {
			for(float[] ii : i)
				ii = null;
			i = null;
		}
		
		triangles = null;
	}


	public static Model get(String name, boolean isTemporary) {
		name = removeType(name);
		if(modelMap.containsKey(name))
			return modelMap.get(name);
		else
			return new Model(name + ".obj", name, isTemporary);
	}
}
