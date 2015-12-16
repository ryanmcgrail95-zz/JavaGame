package resource.model;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import resource.Resource;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.texture.Texture;

import datatypes.mat4;
import datatypes.vec4;
import datatypes.lists.CleanList;
import fl.FileExt;
import gfx.GL;
import gfx.RGBA;

public class Model extends Resource {
	
	private static Model instance;
	
	private float[][] pointList;
	private float[][] normalList;
	private float[][] uvList;
	private int[][] vertexList;
	private int[] colorList;
	private int vertexNum;
	private int vertexBuffer;
	private Material[] materials;
	
	private mat4 preMatrix = new mat4();
	
	private static Map<String, Model> modelMap = new HashMap<String, Model>();
	
	private float[][][] triangles;
	
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
	
	
	public Model(String fileName) {
		super(fileName, Resource.R_MODEL);
		instance = this;
		modelMap.put(removeType(fileName),this);
	}
	public Model() {
		super("", Resource.R_MODEL);
		instance = this;
		modelMap.put(removeType(""),this);
	}
	
	public void create(int modelType, List<float[]> pointList, List<float[]> normalList, List<float[]> uvList, List<int[]> vertexList) {
		this.modelType = modelType;
		setAll(pointList, normalList, uvList, new ArrayList<Integer>(), vertexList);
		
		hasColor = false;
		vertexBuffer = createAndFillVertexBuffer();
		
		modList.add(this);
	}
	
	public void create(int modelType, List<float[]> pointList, List<float[]> normalList, List<float[]> uvList, List<Integer> colorList, List<int[]> vertexList) {		
		this.modelType = modelType;		
		setAll(pointList,normalList,uvList,colorList,vertexList);
		
		hasColor = colorList.size() > 0;
		
		vertexBuffer = createAndFillVertexBuffer();
		
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
		
		// Delete GL Vertex Index/Buffer
		GL2 gl = GL.getGL2();
		gl.glDeleteBuffers(1, new int[] {vertexBuffer}, 0);

		
		// Empty Vertex Buffer
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
					gl.glColor4f(color[0]/255f,color[1]/255f,color[2]/255f,255f);
				}
				gl.glVertex3fv(pointList[p],0);
			}
		}
		gl.glEnd();
		
		GL.disableTextures();
	}
	
	
	
	public void drawFast() {
		
		
		GL2 gl = GL.getGL2();
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		//gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		
		gl.glVertexPointer( 3, GL2.GL_FLOAT, 11 * Buffers.SIZEOF_FLOAT, 0 );
		gl.glNormalPointer( GL2.GL_FLOAT, 11 * Buffers.SIZEOF_FLOAT, 5 * Buffers.SIZEOF_FLOAT );
		gl.glColorPointer(3, GL2.GL_FLOAT, 11 * Buffers.SIZEOF_FLOAT, 8 * Buffers.SIZEOF_FLOAT );

	
		// Draw the buffer
		gl.glPolygonMode( GL2.GL_FRONT, GL2.GL_FILL );
		gl.glDrawArrays(modelType, 0, vertexNum);
		
		// Unbind the buffer

		// Disable the different kinds of data 
		gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, 0 );
		
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		//gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
	}
	
	
	public float[] mult(float[] mat, float[] vec) {
		float[] outVec = {0,0,0,0};
		for(int i = 0; i < 4; i++)
			for(int ii = 0; ii < 4; ii++)
				outVec[i] += mat[4*i+ii]*vec[ii];
		return outVec;
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

	
	public void transform(mat4 transformMatrix) {
		destroyTriangles();
		if(pointList == null)
			preMatrix.multe(transformMatrix);
		else
			for(float[] v : pointList) {
				vec4 curVertex = new vec4(v[0],v[1],v[2],1);
				curVertex.multe(transformMatrix);
				
				v[0] = curVertex.x();
				v[1] = curVertex.y();
				v[2] = curVertex.z();
				
				curVertex.destroy();
			}
	}

	public void translate(float tX, float tY, float tZ) {
		mat4 tr = mat4.createTranslationMatrix(tX,tY,tZ);
		transform(tr);
		tr.destroy();
	}
	
	public void rotateX(float rot) {
		mat4 tr = mat4.createRotationXMatrix(rot);
		transform(tr);
		tr.destroy();
	}
	public void rotateY(float rot) {
		mat4 tr = mat4.createRotationYMatrix(rot);
		transform(tr);
		tr.destroy();
	}
	public void rotateZ(float rot) {
		mat4 tr = mat4.createRotationZMatrix(rot);
		transform(tr);
		tr.destroy();
	}
	
	public void scale(float scaleFactor) {scale(scaleFactor,scaleFactor,scaleFactor);}
	public void scale(float sX, float sY, float sZ) {
		mat4 tr = mat4.createScaleMatrix(sX,sY,sZ);
		transform(tr);
		tr.destroy();
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
	
	protected int createAndFillVertexBuffer() {
		int[] bufferInd = new int[1];    
		
		GL2 gl = (GL2) GL.getGL();
		
		int error, n = 0;
		while((error = gl.glGetError()) != 0);
		
	    // create vertex buffer object if needed
        gl.glGenBuffers(1, bufferInd, 0 );
 
        // create vertex buffer data store without initial copy
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferInd[0] );
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
                          vertexNum * ( 3 * 3 + 2 ) * Buffers.SIZEOF_FLOAT, // # vertices * # of #s in each datatype * float size * 4 datatypes
                          null,
                          GL2.GL_DYNAMIC_DRAW );
        	 
	    // map the buffer and write vertex and color data directly into it
	    gl.glBindBuffer( GL2.GL_ARRAY_BUFFER, bufferInd[0] );
	    ByteBuffer bytebuffer = gl.glMapBuffer( GL2.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY );
	    //FloatBuffer floatbuffer = bytebuffer.order( ByteOrder.nativeOrder() ).asFloatBuffer();
	 	    
	    float[] array;
	    int[] v, color = {0,0,0,0};
	    int i = 0, k = 0;
	    for(k = 0; k < vertexNum; k++) {
	    	v = vertexList[k];
	    	
	    	if(v[0] == -1)
	    		continue;
	    	
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
	    	for(i = 0; i < 3; i++)
	    		bytebuffer.putFloat(color[i]/255f);
	    	//floatbuffer.put(, 0, 3); i += 3;
	    	//floatbuffer.put(normalList[(int) v[2]], 0, 3); i += 3;
	    }	    
	    
	    bytebuffer.position(0);	    
	    gl.glUnmapBuffer( GL2.GL_ARRAY_BUFFER );

	    return bufferInd[0];
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


	public void load(String fileName) {
		if(fileName != "") {
			OBJLoader.loadInto(fileName, this);	
			
			// Necessary for Fixing Model
			scale(2f);
			scale(1,-1,-1);
			flipNormals();
		}
		
		preMatrix.println();
		transform(preMatrix);
		preMatrix.destroy();
		preMatrix = null;
		
		mirrorUVVertically();
		
		//this.scale(390);
		//this.rotateY(90);
	}

	public void unload() {
		destroy();
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


	public static Model get() {
		return instance;
	}
	public static Model get(String name) {
		return modelMap.get(name);
	}
}
