package gfx;

import java.util.ArrayList;
import java.util.List;
import sun.java2d.pipe.ShapeSpanIterator;
import com.jogamp.opengl.util.texture.Texture;
import cont.TextureController;

public class Shape { //extends BranchGroup {
	private int r=255, g=255, b=255;
	private float sx=0, sy=0, sz=0, shadowZ=0, rotX, rotY, rotZ;
	
	private float xScale = 1, yScale = 1, zScale = 1;
	
	boolean toDestroy = false;
	
	String name;
	
	
	private static byte T_NORMAL = 0, T_FLOOR = 1;
	
	//private TransformGroup t3dG;
	
	private static List<Shape> floorList = new ArrayList<Shape>();
	private static List<Shape> otherList = new ArrayList<Shape>();
	private static List<Shape> shapeList = new ArrayList<Shape>();
	
	private List<Sh3D> shapes = new ArrayList<Sh3D>();	
	private List<Sh3D> shadows = new ArrayList<Sh3D>();

	
	
	public Wall addWall(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {		
		Wall wall = new Wall(x1, y1, z1, x2, y2, z2, tex);
		
		shapes.add(wall);
		return wall;
	}
	
	public Sprite addSprite(float x, float y, float z, float w, float h, Texture tex) {
		Sprite spr = new Sprite(x, y, z, w, h, tex);
		
		shapes.add(spr);
		return spr;
	}
	
	public Floor addFloor(float x1, float y1, float x2, float y2, float z, Texture tex) {
		float w, l;
		w = (x2 - x1)/2;
		l = (y2 - y1)/2;
	
		Floor floor = new Floor(-w, -l, w, l, 0, tex);
		floor.setPosition((x1+x2)/2, (y1+y2)/2, z);
		
		shapes.add(floor);
		return floor;
	}
	
	public void addBlock(float x1, float y1, float z1, float x2, float y2, float z2, CubeMap map) {
		if(map != null) {
			addWall(x1, y1, z1, x2, y1, z2, map.getBack().getFrame(0));
			addWall(x2, y1, z1, x2, y2, z2, map.getRight().getFrame(0));
			addWall(x1, y1, z1, x1, y2, z2, map.getLeft().getFrame(0));
			addWall(x1, y2, z1, x2, y2, z2, map.getFront().getFrame(0));
			addFloor(x1, y1, x2, y2, z2, map.getBottom().getFrame(0));
			addFloor(x1, y1, x2, y2, z1, map.getTop().getFrame(0));
		}
		else {
			addWall(x1, y1, z1, x2, y1, z2, null);
			addWall(x2, y1, z1, x2, y2, z2, null);
			addWall(x1, y1, z1, x1, y2, z2, null);
			addWall(x1, y2, z1, x2, y2, z2, null);
			addFloor(x1, y1, x2, y2, z2, null);
			addFloor(x1, y1, x2, y2, z1, null);
		}
	}
	
	public void addBlock(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
		addWall(x1, y1, z1, x2, y1, z2, tex);
		addWall(x2, y1, z1, x2, y2, z2, tex);
		addWall(x1, y1, z1, x1, y2, z2, tex);
		addWall(x1, y2, z1, x2, y2, z2, tex);
		addFloor(x1, y1, x2, y2, z2, tex);
		addFloor(x1, y1, x2, y2, z1, tex);
	}
	
	public Floor addShadow(float x, float y, float z) {
		Floor shadow;
    	float s = 9.6f;
    	
    	shadow = new Floor(-s,-s,s,s,TextureController.getTexture("texShadow"));
    	shadow.setPosition(x,y,z);
    	
    	shadows.add(shadow);
    	
    	return shadow;
	}
	
	public Floor addBlockShadow(float x, float y, float z, float size) {
    	Floor shadow;
    	float s = size/16*20;//20;
    	
    	shadow = new Floor(-s,-s,s,s,TextureController.getTexture("texBlockShadow"));
    	shadow.setPosition(x,y,z);
    	
    	shadows.add(shadow);
    	
    	return shadow;
	}
	
	private class Sh3D {
		float x, y, z, alpha;
		float rotX = 0, rotY = 0, rotZ = 0;
		boolean isShadow;
		Texture tex;
		
		public Sh3D(boolean isShadow) {
			alpha = 1;
			this.isShadow = isShadow;
		}
		
		public void draw() {
		}
		
		public void setPosition(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public void setRotation(float rotX2, float rotY2, float rotZ2) {
			this.rotX = rotX2;
			this.rotY = rotY2;
			this.rotZ = rotZ2;
		}
	}
	
	private class Wall extends Sh3D {
		float x1, y1, z1, x2, y2, z2; 
		
		public Wall(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
			super(false);
			
			this.x1 = x1;	this.y1 = y1;	this.z1 = z1;
			this.x2 = x2;	this.y2 = y2;	this.z2 = z2;
			this.tex = tex;
		}
		
		public void draw() {
			//gl.glNormal3f(0,0,1);
			
			
			if(tex != null) {
				GOGL.bind(tex);
				GOGL.enableBlending();
			}
			
			//gl.gl
			
			GOGL.setColor(1, 1, 1, alpha);
			
			GOGL.transformTranslation(sx+xScale*x,sy+yScale*y,sz+zScale*z);
			GOGL.transformRotationZ(rotZ);
			GOGL.transformRotationY(rotY);
			GOGL.transformRotationX(rotX);
			GOGL.transformScale(xScale, yScale, zScale);
			
			GOGL.draw3DWall(x1,y1,z1,x2,y2,z2);
			
			GOGL.transformClear();
			
			
			GOGL.setColor(RGBA.WHITE);
						
			
			if(tex != null) {
				GOGL.unbind();
				GOGL.disableBlending();
			}
		}
	}
	
	private class Floor extends Sh3D {
		float x1, y1, x2, y2, z1;
		
		public Floor(float x1, float y1, float x2, float y2, float z1, Texture tex) {
			super(false);
			
			this.x1 = x1;	this.y1 = y1;
			this.x2 = x2;	this.y2 = y2;	this.z1 = z1;
			this.tex = tex;
		}
		
		public Floor(float x1, float y1, float x2, float y2, Texture tex) {
			super(true);
			
			this.x1 = x1;	this.y1 = y1;
			this.x2 = x2;	this.y2 = y2;	this.z1 = 0;
			this.tex = tex;
		}
		
		public void draw() {
			//gl.glNormal3f(0,0,1);
			
			
			if(tex != null) {
				GOGL.bind(tex);				
				GOGL.enableBlending();
			}	
			
			
			GOGL.setColor(1f, 1f, 1f, (float) alpha);
			
			if(!isShadow) {
				GOGL.transformTranslation(sx,sy,sz);
				GOGL.transformRotationZ(rotZ);
				GOGL.transformRotationY(rotY);
				GOGL.transformRotationX(rotX);
				GOGL.transformTranslation(x*xScale,y*yScale,z*zScale);
			}
			else
				GOGL.transformTranslation(sx+x,sy+y,shadowZ);	
			
			GOGL.transformScale(xScale, yScale, zScale);
			GOGL.draw3DFloor(x1,y1,x2,y2,z1);
			GOGL.transformClear();			
			GOGL.setColor(1,1,1,1);


			
			if(tex != null) {
				GOGL.unbind();
				GOGL.disableBlending();
			}
		}
	}
	
	
	
	private class Sprite extends Sh3D {
		float w, h; 
		
		public Sprite(float x, float y, float z, float w, float h, Texture tex) {
			super(false);
			
			this.x = x;	this.y = y;	this.z = z;
			this.w = w;	this.h = h;
			this.tex = tex;
		}
		
		public void draw() {
			//gl.glNormal3f(0,0,1);
			
			
			if(tex != null) {
				GOGL.bind(tex);
				GOGL.enableBlending();				
			}
			
			
			GOGL.setColor(1f, 1f, 1f, (float) alpha);
							
				GOGL.transformTranslation(sx+xScale*x,sy+yScale*y,sz+zScale*z);
				GOGL.transformRotationZ(Camera.getDirection()+90);
				//gl.glRotated(rotY, 0, 1, 0);
				//gl.glRotated(rotX, 1, 0, 0);
				
				GOGL.transformScale(xScale, yScale, zScale);
				GOGL.draw3DWall(-w/2, 0, h/2, w/2, 0, -h/2);					
				GOGL.transformClear();
			
			GOGL.setColor(1,1,1,1);			
			
			if(tex != null) {
				GOGL.unbind();
				GOGL.disableBlending();
			}
		}
	}
	
	
	
	
	/*public Shape(Shape3D shape) {
		setCapability(ALLOW_DETACH);
		setCapability(ALLOW_CHILDREN_READ);
		setCapability(ALLOW_CHILDREN_WRITE);
		setCapability(ALLOW_CHILDREN_EXTEND);
		setCapability(ALLOW_BOUNDS_READ);
		setCapability(ALLOW_BOUNDS_WRITE);
		
		
		t3dG = new TransformGroup();
		t3dG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		shapes.add(shape);
		
		t3dG.addChild(shape);
		addChild(t3dG);
		
		Graphics3D.addShape(this);
	}*/
	
	/*public Shape(List<Shape3D> shapes) {
		setCapability(ALLOW_DETACH);
		setCapability(ALLOW_CHILDREN_READ);
		setCapability(ALLOW_CHILDREN_WRITE);
		setCapability(ALLOW_CHILDREN_EXTEND);
		setCapability(ALLOW_BOUNDS_READ);
		setCapability(ALLOW_BOUNDS_WRITE);
		
		
		t3dG = new TransformGroup();
		t3dG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		shapes = shapes;
		
		for(Shape3D s : shapes)
			t3dG.addChild(s);
		addChild(t3dG);
		
		Graphics3D.addShape(this);
	}*/

	
	public Shape(byte type, String name) {	
		if(type == T_FLOOR)
			floorList.add(this);
		else
			otherList.add(this);
		
		this.name = name;
		//Graphics3D.addShape(this);
	}
	
	
	public List<Sh3D> getShapes() {
		return shapes;
	}
	
	public void setShadowPosition(float z) {
		if(toDestroy)
			return;
		
		shadowZ = z;
	}
	
	public void setScale(float x, float y, float z) {
		xScale = x;
		yScale = y;
		zScale = z;
	}
	
	public void setPosition(float x, float y, float z) {
		if(toDestroy)
			return;
		
		sx = x;
		sy = y;
		sz = z;
		
		
		/*Transform3D t3d = new Transform3D();
		t3dG.getTransform(t3d);
		
		t3d.setTranslation(new Vector3d(x, y, z));
		
		t3dG.setTransform(t3d);*/
	}
	
	public void setRotation(float rotX, float rotY, float rotZ) {
		for(Sh3D s : shapes)
			s.setRotation(rotX, rotY, rotZ);
		
		
		/*this.rotX = rotX;
		this.rotY = rotY;
		this.rotX = rotZ;
		
		
		Transform3D t3d = new Transform3D(),
					t3dX = new Transform3D(),
					t3dY = new Transform3D(),
					t3dZ = new Transform3D();
		
		t3d.setTranslation(new Vector3d(x,y,z));
		t3dX.rotX(rotX/180*Math.PI);
		t3dY.rotY(rotY/180*Math.PI);
		t3dZ.rotZ(rotZ/180*Math.PI);

		
		t3d.mul(t3dX);
		t3d.mul(t3dY);
		t3d.mul(t3dZ);
		
	    t3dG.setTransform(t3d);*/
	}

	public void destroy() {
		shapes.clear();
		
		shapeList.remove(this);
		floorList.remove(this);
		otherList.remove(this);
		
		//detach();
		//removeAllChildren();
	}
	
	public void setTexture(Texture tex) {
		for(Sh3D s : shapes)
			s.tex = tex;
		//	s.getAppearance().setTexture(tex);
	}
	
	
	
	public static Shape createWall(String name, float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
		Shape sh = new Shape(T_NORMAL, name);
		sh.addWall(x1, y1, z1, x2, y2, z2,  tex);
		
		return sh;
	}
	
	public static Shape createFloor(String name, float x1, float y1, float x2, float y2, float z, Texture tex) {
		Shape sh = new Shape(T_FLOOR, name);
		sh.addFloor(x1, y1, x2, y2, z, tex);
		
		return sh;
	}
	
	public static Shape createShadow(String name, float x, float y, float z) {
    	Shape sh = new Shape(T_NORMAL, name);
    	sh.addShadow(x, y, z);
    	
    	return sh;
    }
    
    public static Shape createBlockShadow(String name, float x, float y, float z, float size) {
    	Shape sh = new Shape(T_NORMAL, name);
    	sh.addBlockShadow(x, y, z, size);
    	
    	return sh;
    }
	
	
	
	public void draw() {
		GOGL.setColor((float) r/255, (float) g/255, (float) b/255, 1);
		
		for(Sh3D s : shadows)
			s.draw();
		for(Sh3D s : shapes)
			s.draw();
	}

	public static void depthSort(List<Shape> list) {
		int shapeNum = list.size(), moveNum = 0;
		float[] disList = new float[shapeNum];
		
		int k = 0;
		for(Shape s : list) {
			disList[k] = GOGL.getCamera().calcParaDistance(s.sx, s.sy);
			//System.out.println(s.name + ", " + disList[k]);
			k++;
		}
				
		boolean didMove;
		do {
			didMove = false;
			
			for(int i = 1; i < shapeNum; i++)
				if(disList[i] > disList[i-1]) {
					float tempDis = disList[i];
					disList[i] = disList[i-1];
					disList[i-1] = tempDis;
					
					Shape tempS = list.get(i);
					list.set(i, list.get(i-1));
					list.set(i-1, tempS);
					
					moveNum++;
					
					didMove = true;
				}
		} while(didMove);
		
		
		//System.out.println(" ");
		/*for(Shape s : list)
			System.out.println(s.name);*/
		
		//System.out.println(" " + moveNum);
		//for(int i = 1; i < shapeNum; i++)
		//	System.out.println(shapeList.get(i).shapes.size() + shapeList.get(i).shadows.size());
	}
	
	public static void drawAll() {
		depthSort(otherList);
		//depthSort(List);
		
		for(Shape s : floorList)
			s.draw();
		for(Shape s : otherList)
			s.draw();
	}

	public void setColor(int R, int G, int B) {
		this.r = R;
		this.g = G;
		this.b = B;
	}

	public static Shape createBlock(String name, float x1, float y1, float z1, float x2, float y2, float z2, CubeMap map) {
		Shape sh = new Shape(T_NORMAL, name);
		
		float w, l, h;
		w = (x2 - x1)/2;
		l = (y2 - y1)/2;
		h = (z1 - z2)/2;
		
		sh.addBlock(-w, -l, h, w, l, -h, map);
		
		sh.sx = (x1 + x2)/2;
		sh.sy = (y1 + y2)/2;
		sh.sz = (z1 + z2)/2;
		
		return sh;
	}
	
	public static Shape createSprite(String name, float x, float y, float z, float w, float h, Texture tex) {
		Shape sh = new Shape(T_NORMAL, name);
		//sh.addSprite(x, y, z, w, h, tex);
		sh.addSprite(0,0,0, w, h, tex);
		sh.setPosition(x, y, z);
		
		return sh;
	}

	public void setAlpha(float alpha) {
		for(Sh3D s : shapes)
			s.alpha = alpha;
	}

	public static int getNumber() {
		return shapeList.size();
	}

	public void setScale(float scale) {
		setScale(scale, scale, scale);
	}

	public void setShapePosition(float x, float y, float z) {
		for(Sh3D s : shapes)
			s.setPosition(x, y, z);	
	}	
}
