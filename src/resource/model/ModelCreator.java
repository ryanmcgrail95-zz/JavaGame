package resource.model;

import gfx.GOGL;
import gfx.RGBA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datatypes.vec2;
import datatypes.vec3;
import datatypes.vec4;

public class ModelCreator {
	private List<float[]> pointList = new ArrayList<float[]>();	
	private List<float[]> normalList = new ArrayList<float[]>();
	private List<float[]> uvList = new ArrayList<float[]>();
	private List<int[]> vertexList = new ArrayList<int[]>();
	private List<Integer> colorList = new ArrayList<Integer>();
	private Map<Long,Integer> pointMap = new HashMap<Long,Integer>();
	private Map<Long,Integer> normalMap = new HashMap<Long,Integer>();
	private Map<Long,Integer> colorMap = new HashMap<Long,Integer>();
	private int modelType;
	
	private int color[] = new int[3];

	public ModelCreator(int modelType) {
		this.modelType = modelType;
		begin();
	}
	
	public void setColor(RGBA col) {
		setColor(col.getRi(),col.getGi(),col.getBi());
	}
	public void setColor(int R, int G, int B) {
		color[0] = R;
		color[1] = G;
		color[2] = B;
	}
	
	public void addVertexBT(float x, float y, float z, float nX, float nY, float nZ) {
		Integer ptIndex, nIndex, cIndex;
		long signature = (int)(x/16)*10000 + (int)(y/16)*100 + (int)(z/16);
		long nSignature = (int)(nX*10)*10000 + (int)(nY*10)*100 + (int)(nZ*10);
		long cSignature = color[0]*256*256 + color[1]*256 + color[2];

		ptIndex = pointMap.get(signature);
		if(ptIndex == null) {
			float[] vec = {x,y,z,1};
			pointList.add(vec);
			pointMap.put(signature,ptIndex = pointList.size()-1);
		}
		
		nIndex = normalMap.get(nSignature);
		if(nIndex == null) {
			float[] norm = {nX,nY,nZ,0};
			normalList.add(norm);
			normalMap.put(nSignature,nIndex = normalList.size()-1);
		}
		
		cIndex = colorMap.get(cSignature);
		if(cIndex == null) {
			int col = RGBA.convertRGBA2Int(color[0],color[1],color[2],255);
			colorList.add(col);
			colorMap.put(cSignature,cIndex = colorList.size()-1);
		}
		
		vertexList.add(new int[] {ptIndex,-1,nIndex,cIndex} );
	}
	
	private void begin() {		
		pointList.clear();
		pointMap.clear();
		normalMap.clear();
		normalList.clear();
		uvList.clear();
		vertexList.clear();
		colorList.clear();
	}
	
	public Model endModel() {
		String outText = 
				"Created model:\n"
				+ "type: " + modelType + "\n"
				+ "# vertices: " + vertexList.size() +"\n"
				+ "# points: " + pointList.size() +"\n"
				+ "# normals: " + normalList.size() +"\n"
				+ "# colors: " + colorList.size() +"\n";
		GOGL.println(outText);				
		
		Model mod = new Model(modelType,pointList,normalList,uvList,colorList,vertexList);
		pointList.clear();
		pointMap.clear();
		normalMap.clear();
		normalList.clear();
		uvList.clear();
		vertexList.clear();
		colorList.clear();
		
		return mod;
	}

	
	
	public void add3DModelWall(float x1, float y1, float z1, float x2, float y2, float z2,  float nX, float nY, float nZ) {
		
		float[] mat, v1, v2, v3, v4;
		/*mat = getModelMatrix().array();
		v1 = mult(mat, new float[] {x1,y,z1,1});
		v2 = mult(mat, new float[] {x2,y,z1,1});
		v3 = mult(mat, new float[] {x1,y,z2,1});
		v4 = mult(mat, new float[] {x2,y,z2,1});
		n = mult(mat,new float[] {0,(x1 < x2) ? 1 : -1,0,0});*/
		v1 = new float[] {x1,y1,z1,1};
		v2 = new float[] {x2,y2,z1,1};
		v3 = new float[] {x1,y1,z2,1};
		v4 = new float[] {x2,y2,z2,1};

		if(modelType == Model.TRIANGLES) {
			addVertexBT(v1[0],v1[1],v1[2], nX,nY,nZ);
			addVertexBT(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertexBT(v3[0],v3[1],v3[2], nX,nY,nZ);
	
			addVertexBT(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertexBT(v4[0],v4[1],v4[2], nX,nY,nZ);
			addVertexBT(v3[0],v3[1],v3[2], nX,nY,nZ);
		}
		else if(modelType == Model.QUADS) {
			addVertexBT(v1[0],v1[1],v1[2], nX,nY,nZ);
			addVertexBT(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertexBT(v4[0],v4[1],v4[2], nX,nY,nZ);
			addVertexBT(v3[0],v3[1],v3[2], nX,nY,nZ);
		}
	}

	public void add3DModelFloor(float x1, float y1, float x2, float y2, float z,  float nX, float nY, float nZ) {

		float[] mat, v1, v2, v3, v4;
		/*mat = getModelMatrix().array();
		v1 = mult(mat, new float[] {x1,y1,z,1});
		v2 = mult(mat, new float[] {x2,y1,z,1});
		v3 = mult(mat, new float[] {x1,y2,z,1});
		v4 = mult(mat, new float[] {x2,y2,z,1});
		n = mult(mat,new float[] {0,(x1 < x2) ? 1 : -1,0,0});*/
		v1 = new float[] {x1,y1,z,1};
		v2 = new float[] {x2,y1,z,1};
		v3 = new float[] {x1,y2,z,1};
		v4 = new float[] {x2,y2,z,1};

		if(modelType == Model.TRIANGLES) {
			addVertexBT(v1[0],v1[1],v1[2], nX,nY,nZ);
			addVertexBT(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertexBT(v3[0],v3[1],v3[2], nX,nY,nZ);
	
			addVertexBT(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertexBT(v4[0],v4[1],v4[2], nX,nY,nZ);
			addVertexBT(v3[0],v3[1],v3[2], nX,nY,nZ);
		}
		else if(modelType == Model.QUADS) {
			addVertexBT(v1[0],v1[1],v1[2], nX,nY,nZ);
			addVertexBT(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertexBT(v4[0],v4[1],v4[2], nX,nY,nZ);
			addVertexBT(v3[0],v3[1],v3[2], nX,nY,nZ);
		}
	}
	
	public void add3DBlockModel(float x1, float y1, float z1, float x2, float y2, float z2) {
		add3DBlockModel(x1,y1,z1,x2,y2,z2, true,true,true,true,true,true);
	}
	public void add3DBlockModel(float x1, float y1, float z1, float x2, float y2, float z2, boolean left, boolean right, boolean back, boolean front, boolean top, boolean bottom) {		
		if(left)
			add3DModelWall(x1,y2,z1,x1,y1,z2, -1,0,0);
		if(right)
			add3DModelWall(x2,y1,z1,x2,y2,z2, 1,0,0);
		if(front)
			add3DModelWall(x1,y1,z1,x2,y1,z2, 0,-1,0);
		if(back)
			add3DModelWall(x2,y2,z1,x1,y2,z2, 0,1,0);
		if(bottom)
			add3DModelFloor(x1,y1,x2,y2,z1, 0,0,-1);
		if(top)
			add3DModelFloor(x2,y1,x1,y2,z2, 0,0,1);
	}
}
