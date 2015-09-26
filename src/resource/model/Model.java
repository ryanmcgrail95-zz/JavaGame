package resource.model;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;

import datatypes.mat4;
import datatypes.vec2;
import datatypes.vec3;
import datatypes.vec4;
import fl.FileExt;
import gfx.GOGL;
import gfx.RGBA;

public class Model {
	private float[][] pointList;
	private float[][] normalList;
	private float[][] uvList;
	private int[][] vertexList;
	private int[] colorList;
	private int vertexNum;
	private int vertexBuffer;
	private Material materials;
	
	public final static int TRIANGLES = GL2.GL_TRIANGLES, QUADS = GL2.GL_QUADS;
	private int modelType;
	
	private boolean hasColor;

	public static Model MOD_PINEBRANCHES, MOD_PINETREE, MOD_PINESTUMP;
	public static Model MOD_WMBLADES, MOD_WMFRAME, MOD_WMBODY;
	public static Model MOD_HOUSEBODY, MOD_HOUSEFRAME;
	public static Model MOD_BOWL;
	public static Model MOD_FERN;
	
	public static void ini() {
		
		MOD_WMBLADES = OBJLoader.load("windmill").fix();
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
			MOD_FERN.flipNormals();
	}
	
	public Model(int modelType, List<float[]> pointList, List<float[]> normalList, List<float[]> uvList, List<int[]> vertexList) {
		this.modelType = modelType;
		setAll(pointList, normalList, uvList, new ArrayList<Integer>(), vertexList);
		
		hasColor = false;
		vertexBuffer = createAndFillVertexBuffer();
	}
	
	public Model(int modelType, List<float[]> pointList, List<float[]> normalList, List<float[]> uvList, List<Integer> colorList, List<int[]> vertexList) {		
		this.modelType = modelType;		
		setAll(pointList,normalList,uvList,colorList,vertexList);
		
		hasColor = true;		
		vertexBuffer = createAndFillVertexBuffer();
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
		
		// Delete GL Vertex Index/Buffer
		// Empty Vertex Buffer
	}
	
	public Model fix() {
		scale(2f);
		flipNormals();
		
		return this;
	}
	
	public void draw() {
		GL2 gl = GOGL.gl;
		
		int p,u,n;
		
		//materials.enable();
		
		gl.glBegin(GL2.GL_TRIANGLES);
		for(int[] v : vertexList) {			
			p = v[0];
			u = v[1];
			n = v[2];
			
			if(u != -1)
				gl.glTexCoord2fv(uvList[u],0);
			if(n != -1)
				gl.glNormal3fv(normalList[n],0);
			gl.glVertex3fv(pointList[p],0);
		}
		gl.glEnd();
		
		//materials.disable();
	}
	
	
	
	public void drawFast() {
		GL2 gl = GOGL.gl;
		
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexBuffer);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
		
	
		gl.glVertexPointer( 3, GL.GL_FLOAT, 11 * Buffers.SIZEOF_FLOAT, 0 );
		gl.glNormalPointer( GL.GL_FLOAT, 11 * Buffers.SIZEOF_FLOAT, 5 * Buffers.SIZEOF_FLOAT );
		gl.glColorPointer(3, GL.GL_FLOAT, 11 * Buffers.SIZEOF_FLOAT, 8 * Buffers.SIZEOF_FLOAT );

	
		// Draw the buffer
		gl.glPolygonMode( GL.GL_FRONT, GL2.GL_FILL );
		gl.glDrawArrays(modelType, 0, vertexNum);
		
		// Unbind the buffer

		// Disable the different kinds of data 
		gl.glBindBuffer( GL.GL_ARRAY_BUFFER, 0 );
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
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
		GL2 gl = GOGL.gl;
		
		float[] matA = GOGL.getModelMatrix().array();
		
		int p,u,n;
		int[] color;
		for(int[] v : vertexList) {			
			p = v[0];
			u = v[1];
			n = v[2];
			
			if(hasColor) {
				color = RGBA.convertInt2RGBA(colorList[v[3]]);
				GOGL.setLightColor(color[0],color[1],color[2]);
			}

			if(u != -1)
				gl.glTexCoord2fv(uvList[u],0);
			if(n != -1)
				gl.glNormal3fv(mult(matA,normalList[n]),0);
			gl.glVertex3fv(mult(matA,pointList[p]),0);
		}
	}

	
	public void transform(mat4 transformMatrix) {
		for(float[] v : pointList) {
			vec4 curVertex = new vec4(v[0],v[1],v[2],1);
			curVertex.multe(transformMatrix);
			
			v[0] = curVertex.x();
			v[1] = curVertex.y();
			v[2] = curVertex.z();
		}
	}

	public void translate(float tX, float tY, float tZ) {
		transform(mat4.createTranslationMatrix(tX,tY,tZ));
	}
	
	public void rotateX(float rot) {
		transform(mat4.createRotationMatrixX(rot));
	}
	public void rotateY(float rot) {
		transform(mat4.createRotationMatrixY(rot));
	}
	public void rotateZ(float rot) {
		transform(mat4.createRotationMatrixZ(rot));
	}
	
	public void scale(float scaleFactor) {scale(scaleFactor,scaleFactor,scaleFactor);}
	public void scale(float sX, float sY, float sZ) {
		transform(mat4.createScaleMatrix(sX,sY,sZ));
	}
	
	public void flipNormals() {
		for(float[] v : normalList) {
			v[0] *= -1;
			v[1] *= -1;
			v[2] *= -1;
		}
	}
	
	public void attachMaterials(Material mat) {
		materials = mat;
	}
	
	
	protected int createAndFillVertexBuffer() {
		int[] bufferInd = new int[1];    
		
		GL2 gl = GOGL.gl;
		
		int error, n = 0;
		while((error = gl.glGetError()) != 0);
		
	    // create vertex buffer object if needed
        gl.glGenBuffers(1, bufferInd, 0 );
 
        // create vertex buffer data store without initial copy
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferInd[0] );
        gl.glBufferData(GL.GL_ARRAY_BUFFER,
                          vertexNum * ( 3 * 3 + 2 ) * Buffers.SIZEOF_FLOAT, // # vertices * # of #s in each datatype * float size * 4 datatypes
                          null,
                          GL2.GL_DYNAMIC_DRAW );
        	 
	    // map the buffer and write vertex and color data directly into it
	    gl.glBindBuffer( GL.GL_ARRAY_BUFFER, bufferInd[0] );
	    ByteBuffer bytebuffer = gl.glMapBuffer( GL.GL_ARRAY_BUFFER, GL2.GL_WRITE_ONLY );
	    //FloatBuffer floatbuffer = bytebuffer.order( ByteOrder.nativeOrder() ).asFloatBuffer();
	 	    
	    float[] array;
	    int[] v, color = {0,0,0,0};
	    int i = 0, k = 0;
	    for(k = 0; k < vertexNum; k++) {
	    	v = vertexList[k];
	    	
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
	    gl.glUnmapBuffer( GL.GL_ARRAY_BUFFER );

	    return bufferInd[0];
	}
}
