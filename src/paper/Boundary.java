package paper;

import java.util.ArrayList;
import java.util.List;

import functions.MathExt;
import gfx.GL;
import object.primitive.Environmental;
import object.primitive.Physical;

public class Boundary extends Environmental {
	float[][] pts;
	
	public Boundary(float[][] pts) {
		super(0,0,false,false);
		this.pts = pts;
	}

	@Override
	public boolean collide(Physical other) {
		float[] pt1, pt2;
		boolean didCollide = false, cur;
		
		List<Integer> nums = new ArrayList<Integer>();
		
		pt1 = pts[0];
		int i = 0, len = pts.length;
		while(i < len) {
			pt2 = pt1;
			pt1 = pts[i];
			
			cur = other.collideLine(pt1[0],pt1[1],pt2[0],pt2[1]);
			
			didCollide = didCollide || cur;
			
			if(cur)
				if(!nums.contains(i)) {
					nums.add(i);
					if(i > 0)
						i--;
					continue;
				}
			i++;
		}
		
		nums.clear();
		
		return didCollide;
	}

	@Override
	public void draw() {
		float[] pt1, pt2;
		boolean didCollide = false;
				
		GL.resetColor();
		GL.setAlpha(.3f);
		
		pt1 = pts[0];
		for(int i = 1; i < pts.length; i++) {
			pt2 = pt1;
			pt1 = pts[i];
			
			GL.draw3DWall(pt1[0],pt1[1],1000,pt2[0],pt2[1],-100);
		}
		
		GL.resetColor();
	}

	public boolean checkOnscreen() 	{return true;}
	public float calcDepth() 		{return 1000;}

	@Override
	public void add() {}
}
