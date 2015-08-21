package animation;

import gfx.GOGL;

import java.util.ArrayList;
import java.util.List;

import datatypes.mat4;

public class Bone {
	private List<Bone> children = new ArrayList<Bone>();
	private Bone parent;
	private Orientation ori;
	private float length;
	
	
	public Bone(Bone parent, float length) {
		this.parent = parent;
		if(parent != null)
			parent.children.add(this);
		this.length = length;
	}
		
	public Bone add(float length) {
		return new Bone(this,length);
	}
	
	public void draw() {
		if(parent == null)
			GOGL.transformTranslation(ori.getPosition());
		GOGL.transformRotation(ori.getRotation());
		
		drawBone();
		GOGL.transformTranslation(0,0,length);

		mat4 mat = GOGL.getModelMatrix();
		for(Bone b : children) {
			GOGL.setModelMatrix(mat);
			b.draw();
		}
	}
	
	private void drawBone() {
		GOGL.draw3DFrustem(8,0,length,3);
	}
}
