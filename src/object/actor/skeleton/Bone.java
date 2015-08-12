package object.actor.skeleton;

import gfx.GOGL;

import java.util.ArrayList;
import java.util.List;

import datatypes.mat4;
import datatypes.vec3;

public class Bone {
	private float len;
	
	// Animation
	private BoneAnimation curAni;

	// Children
	private List<Bone> children;
	private int childNum;
	
	
	public Bone(float len) {
		children = new ArrayList<Bone>();
		childNum = 0;
		this.len = len;
	}
	public Bone(Bone parent, float len) {
		parent.add(this);
		children = new ArrayList<Bone>();
		childNum = 0;
		this.len = len;
	}
	
	public void add(Bone child) {
		if(children.contains(child))
			return;
		children.add(child);
		childNum++;
	}
	
		
	public void draw(float index, boolean check) {
		
		mat4 modelMat;
		vec3 curRot = curAni.get(index);
		
		GOGL.transformRotation(curRot);
		GOGL.transformTranslation(len,0,0);
		modelMat = GOGL.getModelMatrix();
		
		for(int i = 0; i < childNum; i++) {
			if(i != 0)
				GOGL.setModelMatrix(modelMat);
				
			children.get(i).draw(index, check);
		}
	}
}
