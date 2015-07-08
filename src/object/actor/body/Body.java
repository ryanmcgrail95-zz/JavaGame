package object.actor.body;

import Datatypes.vec3;
import obj.prt.Dust;
import resource.sound.SoundController;
import io.IO;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;

public class Body {
	
	private Cycle runCycle, walkCycle, standCycle, cycle1, cycle2;
	private float index, frac, direction;
	private final static int A_STAND = 0, A_MOVE = 1;
	public final static byte C_STAND = 0, C_WALK = 1, C_RUN = 2;
	private boolean isMoving;
	
	public Body() {
		index = 0;
		isMoving = false;
		
		runCycle = new RunCycle();
		walkCycle = new WalkCycle();
		standCycle = new StandCycle();
		
		stand();
	}
	
	
	public Cycle getCycle(byte cycle) {
		switch(cycle) {
			case C_STAND:	return standCycle;
			case C_WALK:		return walkCycle;
			case C_RUN:		return runCycle;
		
			default:	return null;
		}
	}
	
	public boolean animate(byte cyc1, byte cyc2) {
		
		Cycle n1, n2;
		n1 = getCycle(cyc1);
		n2 = getCycle(cyc2);
		
		if(n1 == cycle1 && n2 == cycle2)
			return false;
		
		cycle1 = n1;
		cycle2 = n2;
		
		return true;
	}
	public boolean animate(byte cyc1, byte cyc2, float frac) {
		this.frac = frac;
		
		Cycle n1, n2;
		n1 = getCycle(cyc1);
		n2 = getCycle(cyc2);
		
		if(n1 == cycle1 && n2 == cycle2)
			return false;
		
		cycle1 = n1;
		cycle2 = n2;
		
		return true;
	}


	public void stand() {
		
		if(animate(C_STAND, C_WALK))
			frac = 1;
				
		frac += (0 - frac)/5;
		if(frac < .05)
			frac = 0;
		
		if(frac == 0)
			isMoving = false;
	}
	public void walk(float divSpeed) {
		
		isMoving = true;
		animate(C_WALK, C_RUN);
		frac += (0 - frac)/divSpeed;
	}
	public void run(float divSpeed) {
		
		isMoving = true;
		animate(C_WALK, C_RUN);
		frac += (1 - frac)/divSpeed;
	}
	
	
	public vec3 getBody(float index) {
		return cycle2.getBody(index).mult(frac).add(cycle1.getBody(index).mult(1-frac));
	}
	public vec3 getUpperArm(float index) {
		return cycle2.getUpperArm(index).mult(frac).add(cycle1.getUpperArm(index).mult(1-frac));
	}
	public vec3 getLowerArm(float index) {
		return cycle2.getLowerArm(index).mult(frac).add(cycle1.getLowerArm(index).mult(1-frac));
	}
	public vec3 getUpperLeg(float index) {
		return cycle2.getUpperLeg(index).mult(frac).add(cycle1.getUpperLeg(index).mult(1-frac));
	}
	public vec3 getLowerLeg(float index) {
		return cycle2.getLowerLeg(index).mult(frac).add(cycle1.getLowerLeg(index).mult(1-frac));
	}

	
	private void step(float x, float y, float z, float dir) {
		float prevIndex = index;

		float frameNum = 12, speed;
		speed = frac*runCycle.speed + (1-frac)*walkCycle.speed;

		index += speed;
		
		// Footstep
		if(isMoving)
			if(MathExt.between(prevIndex, 6, index) || MathExt.between(prevIndex, 12, index)) {
				SoundController.playSound("Footstep");
	            new Dust(x+Math2D.calcLenX(5,direction+180),y+Math2D.calcLenY(5,direction+180),z, 0, true);
			}

		if(index >= frameNum)
			index -= frameNum;
	}

	public void draw(float x, float y, float z, float dir) {
		float f, iF;
		f = frac;
		iF = 1-f;
		

		float w, h, uLen, useIndex, bLen;
		bLen = 24;
		w = 16;
		h = 4;
		uLen = w - h/2;
		
		float frameNum = 12;
		
		/*float dis = MathExt.min(Math.abs(index-0), Math.abs(index-6), Math.abs(index-12)), disF;
		disF = dis/3;
		
		disF = (float) Math.pow(MathExt.contain(.4f, disF, 1f), 1.2);
		
		index += disF*.5;*/
		
		step(x,y,z,dir);

		
		useIndex = index;
		
		
		//GOGL.enableShader("Main");
		GOGL.passViewMatrix();
		GOGL.passProjectionMatrix();
		
		// Draw Body
		GOGL.transformTranslation(x, y, z+w);
		
		GOGL.transformRotationZ(dir);
		GOGL.transformRotationY(getBody(useIndex).y());
		
		GOGL.passModelMatrix();
		GOGL.draw3DBlock(-h/2,-h/2,0, h, h, bLen);
		
		GOGL.transformClear();
		
		// Draw Arms
		for(int i = -1; i <= 1; i += 2) {
			if(useIndex >= frameNum)
				useIndex -= frameNum;
						
			GOGL.transformTranslation(x, y, z+w);
	
			GOGL.transformRotationZ(dir);
			GOGL.transformTranslation(0,i*h,0);

			GOGL.transformRotationY(getBody(useIndex).y());
			GOGL.transformTranslation(0,0,bLen);
			//GOGL.transformRotationY(-getBody(useIndex));
			
			GOGL.transformRotationY(getUpperArm(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
			
			GOGL.transformTranslation(uLen,0, 0);
			GOGL.transformRotationY(-getUpperArm(useIndex).y() + getLowerArm(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
			
			GOGL.transformClear();
			
			useIndex += frameNum/2;
		}

		
		// Draw Legs
		for(int i = -1; i <= 1; i += 2) {
			if(useIndex >= frameNum)
				useIndex -= frameNum;
						
			GOGL.transformTranslation(x, y, z+w);
	
			GOGL.transformRotationZ(dir);
			GOGL.transformTranslation(0,i*h,0);
			
			GOGL.transformRotationY(getUpperLeg(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
			
			GOGL.transformTranslation(uLen,0, 0);
			GOGL.transformRotationY(-getUpperLeg(useIndex).y() + getLowerLeg(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
			
			GOGL.transformClear();
			
			useIndex += frameNum/2;
		}
		
		GOGL.disableShaders();

	}
}
