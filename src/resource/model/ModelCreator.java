package resource.model;

import functions.Math2D;
import functions.Math3D;
import gfx.RGBA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private float brightness = 1;
	
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
		long cSignature = ((int)(color[0]*brightness))*256*256 + ((int)(color[1]*brightness))*256 + (int)(color[2]*brightness);

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
			int col = RGBA.convertRGBA2Int((int)(color[0]*brightness),(int)(color[1]*brightness),(int)(color[2]*brightness),255);
			colorList.add(col);
			colorMap.put(cSignature,cIndex = colorList.size()-1);
		}
		
		vertexList.add(new int[] {ptIndex,-1,nIndex,cIndex} );
	}
	
	public void addVertex(float x, float y, float z, float nX, float nY, float nZ) {
		Integer ptIndex, nIndex, cIndex;
		long nSignature = (int)(nX*10)*10000 + (int)(nY*10)*100 + (int)(nZ*10);
		long cSignature = ((int)(color[0]*brightness))*256*256 + ((int)(color[1]*brightness))*256 + (int)(color[2]*brightness);

		float[] vec = {x,y,z,1};
		pointList.add(vec);
		ptIndex = pointList.size()-1;
		
		nIndex = normalMap.get(nSignature);
		if(nIndex == null) {
			float[] norm = {nX,nY,nZ,0};
			normalList.add(norm);
			normalMap.put(nSignature,nIndex = normalList.size()-1);
		}
		
		cIndex = colorMap.get(cSignature);
		if(cIndex == null) {
			int col = RGBA.convertRGBA2Int((int)(color[0]*brightness),(int)(color[1]*brightness),(int)(color[2]*brightness),255);
			colorList.add(col);
			colorMap.put(cSignature,cIndex = colorList.size()-1);
		}
		
		vertexList.add(new int[] {ptIndex,-1,nIndex,cIndex} );
	}
	
	private void begin() {		
		clear();
	}
	
	public Model endModel() {
		
		/*String outText = 
				"Created model:\n"
				+ "type: " + modelType + "\n"
				+ "# vertices: " + vertexList.size() +"\n"
				+ "# points: " + pointList.size() +"\n"
				+ "# normals: " + normalList.size() +"\n"
				+ "# colors: " + colorList.size() +"\n";
		System.out.println(outText);*/
		
		Model mod = new Model();
			mod.create(modelType,pointList,normalList,uvList,colorList,vertexList);
			mod.load();
			
		clear();
		
		return mod;
	}

	
	public void add3DModelCylinder(float x, float y, float z, float r, float h, int numPts, boolean caps) {
		float dir, xx,yy, pX=0,pY=0, nX, nY;
		for(int i = 0; i <= numPts; i++) {
			dir = 1f*i/numPts*360;
			
			nX = Math2D.calcLenX(dir);
			nY = Math2D.calcLenY(dir);
			xx = x + r*nX;
			yy = y + r*nY;
					
			if(i != 0)
				add3DModelWall(xx,yy,z+h, pX,pY,z, nX,nY,0);
			
			pX = xx;
			pY = yy;
		}
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
			addVertex(v1[0],v1[1],v1[2], nX,nY,nZ);
			addVertex(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertex(v3[0],v3[1],v3[2], nX,nY,nZ);
	
			addVertex(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertex(v4[0],v4[1],v4[2], nX,nY,nZ);
			addVertex(v3[0],v3[1],v3[2], nX,nY,nZ);
		}
		else if(modelType == Model.QUADS) {
			addVertex(v1[0],v1[1],v1[2], nX,nY,nZ);
			addVertex(v2[0],v2[1],v2[2], nX,nY,nZ);
			addVertex(v4[0],v4[1],v4[2], nX,nY,nZ);
			addVertex(v3[0],v3[1],v3[2], nX,nY,nZ);
		}
		
		v1 = v2 = v3 = v4 = null;
	}

	public void add3DModelWallBT(float x1, float y1, float z1, float x2, float y2, float z2,  float nX, float nY, float nZ) {
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
		
		v1 = v2 = v3 = v4 = null;
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
		
		v1 = v2 = v3 = v4 = null;
	}
	
	public void add3DModelSlopeBT(float x, float y, float z, float s, float h, int d) {				
		float[] mat, v1, v2, v3, v4, n;
		/*mat = getModelMatrix().array();
		v1 = mult(mat, new float[] {x1,y1,z,1});
		v2 = mult(mat, new float[] {x2,y1,z,1});
		v3 = mult(mat, new float[] {x1,y2,z,1});
		v4 = mult(mat, new float[] {x2,y2,z,1});
		n = mult(mat,new float[] {0,(x1 < x2) ? 1 : -1,0,0});*/
		
		float z1,z2,z3,z4;		
		switch(d) {
			case 0:
				n = Math3D.calcPolarCoords(d*90+180,45).getArray();
				z1 = z3 = z;
				z2 = z4 = z+h;
				break;
			case 1:
				n = Math3D.calcPolarCoords(d*90+180,45).getArray();
				z1 = z2 = z+h;
				z3 = z4 = z;
				break;
			case 2:
				n = Math3D.calcPolarCoords(d*90+180,45).getArray();
				z1 = z3 = z+h;
				z2 = z4 = z;
				break;
			case 3:
				n = Math3D.calcPolarCoords(d*90+180,45).getArray();
				z1 = z2 = z;
				z3 = z4 = z+h;
				break;
			default:
				n = new float[] {0,0,0};
				z1 = z2 = z3 = z4 = 0;
		}
		
		float nX,nY,nZ;
		nX = n[0];
		nY = n[1];
		nZ = n[2];
		
		v1 = new float[] {x-s,y-s,z1,1};
		v2 = new float[] {x+s,y-s,z2,1};
		v3 = new float[] {x-s,y+s,z3,1};
		v4 = new float[] {x+s,y+s,z4,1};

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
		
		n = v1 = v2 = v3 = v4 = null;
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

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}

	public void clear() {
		for(int i = 0; i < pointList.size(); i++)
			pointList.set(i, null);
		for(int i = 0; i < normalList.size(); i++)
			normalList.set(i, null);
		for(int i = 0; i < uvList.size(); i++)
			uvList.set(i, null);
		for(int i = 0; i < vertexList.size(); i++)
			vertexList.set(i, null);
		for(int i = 0; i < colorList.size(); i++)
			colorList.set(i, null);

		
		pointList.clear();
		normalList.clear();
		uvList.clear();
		vertexList.clear();
		colorList.clear();
		
		pointMap.clear();
		normalMap.clear();
		colorMap.clear();
	}
}
