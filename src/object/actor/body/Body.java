package object.actor.body;

import datatypes.mat4;
import datatypes.vec3;
import obj.itm.Sword;
import obj.prt.Dust;
import resource.sound.Sound;
import io.IO;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;

public class Body {
	
	private Cycle runCycle, walkCycle, standCycle, cycle1, cycle2;
	private float index, frac, toFrac, prevDir;
	private final static int A_STAND = 0, A_MOVE = 1;
	public final static byte C_STAND = 0, C_WALK = 1, C_RUN = 2;
	private boolean isMoving;
	private float sideRotAngle;
	private Sword sword;
	
	public Body() {
		index = 0;
		isMoving = false;
		
		runCycle = new RunCycle();
		walkCycle = new WalkCycle();
		standCycle = new StandCycle();
		
		sword = new Sword("Whatever");
		
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
		
		toFrac = 0;
		
		if(frac == 0)
			isMoving = false;
	}
	public void walk(float divSpeed) {
		
		isMoving = true;
		animate(C_WALK, C_RUN);
		toFrac = 0;
	}
	public void run(float divSpeed) {
		
		isMoving = true;
		animate(C_WALK, C_RUN);
		toFrac = 1;
	}
	
	
	public vec3 getBody(float index) {
		return (vec3) cycle2.getBody(index).mult(frac).add(cycle1.getBody(index).mult(1-frac));
	}
	public vec3 getUpperArm(float index) {
		return (vec3) cycle2.getUpperArm(index).mult(frac).add(cycle1.getUpperArm(index).mult(1-frac));
	}
	public vec3 getLowerArm(float index) {
		return (vec3) cycle2.getLowerArm(index).mult(frac).add(cycle1.getLowerArm(index).mult(1-frac));
	}
	public vec3 getUpperLeg(float index) {
		return (vec3) cycle2.getUpperLeg(index).mult(frac).add(cycle1.getUpperLeg(index).mult(1-frac));
	}
	public vec3 getLowerLeg(float index) {
		return (vec3) cycle2.getLowerLeg(index).mult(frac).add(cycle1.getLowerLeg(index).mult(1-frac));
	}
	
	public void step(float x, float y, float z, float dir) {
		float prevIndex = index;

		frac += (toFrac - frac)/5;
		if(frac < .05)
			frac = 0;
		
		float frameNum = 12, speed;
		speed = frac*runCycle.speed + (1-frac)*walkCycle.speed;

		index += speed;
		
		// Footstep
		if(isMoving)
			if(MathExt.between(prevIndex, 6, index) || MathExt.between(prevIndex, 12, index)) {
				Sound.play("footstep");
				
				float fX,fY,fZ, backLen = -2;
				fX = x+Math2D.calcLenX(backLen,dir+180);
				fY = y+Math2D.calcLenY(backLen,dir+180);
				fZ = z;
				
				float sideAmt = Math2D.calcLenX(5,index/12*360);
				fX += Math2D.calcLenX(sideAmt, dir+90);
				fY += Math2D.calcLenY(sideAmt, dir+90);
				
	            new Dust(fX,fY,fZ+8, 0, true);
	            new Footstep(fX,fY,fZ, dir+180, 20, 40);	            
			}

		if(index >= frameNum)
			index -= frameNum;
	}

	public void draw(float x, float y, float z, float waistH, float dir) {
		float f, iF;
		f = frac;
		iF = 1-f;
		
		
		mat4 mat;
		
		float a = 0, sideAmt = 0, side = -2;
		if(cycle1 == walkCycle) {
			a = Math2D.calcLenX(3,index/6*360);
			sideAmt = Math2D.calcLenY(side,index/12*360);
		}
		x += Math2D.calcLenX(a, dir);
		y += Math2D.calcLenY(a, dir);
		
		
		x += Math2D.calcLenX(sideAmt, dir+90);
		y += Math2D.calcLenY(sideAmt, dir+90);
		
		
		/*if(cycle1 == walkCycle)
			turnRot = Math2D.calcAngDiff(prevDir,dir)*10;
		else*/
		prevDir = dir;
		

		float w, h, uLen, useIndex, bLen;
		bLen = 24;
		w = 16;
		h = 4;
		uLen = w - h/2;
		
		float frameNum = 12, sideRot, waistRot;
		waistRot = 1f*sideRotAngle;
		sideRot = .5f*sideRotAngle - sideAmt/side*2;
		
		/*float dis = MathExt.min(Math.abs(index-0), Math.abs(index-6), Math.abs(index-12)), disF;
		disF = dis/3;
		
		disF = (float) Math.pow(MathExt.contain(.4f, disF, 1f), 1.2);
		
		index += disF*.5;*/
		
		float mX,mY,mZ;
		mX = x;
		mY = y; 
		mZ = z;
		
		waistH += w;
		
		
		
		useIndex = index;
		
		
		
		// Draw Body
		GOGL.transformClear();
		GOGL.transformTranslation(mX,mY,mZ);
		GOGL.transformRotationZ(dir);
		GOGL.transformRotationX(sideRot);
		GOGL.transformTranslation(0,0,waistH);
		
		mat = GOGL.getModelMatrix();

		GOGL.transformRotationX(waistRot);
		
		GOGL.transformRotationY(getBody(useIndex).y());
		GOGL.draw3DBlock(-h/2,-h/2,0, h, h, bLen);
				
		// Draw Arms
		for(int i = -1; i <= 1; i += 2) {
			if(useIndex >= frameNum)
				useIndex -= frameNum;
						
			GOGL.setModelMatrix(mat);
			GOGL.transformTranslation(0,i*h,0);
			GOGL.transformRotationX(waistRot);

			GOGL.transformRotationY(getBody(useIndex).y());
			GOGL.transformTranslation(0,0,bLen);
			
			GOGL.transformRotationY(getUpperArm(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
			
			GOGL.transformTranslation(uLen,0, 0);
			GOGL.transformRotationY(-getUpperArm(useIndex).y() + getLowerArm(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
			
			GOGL.transformTranslation(uLen,0, 0);			
			if(i == 1)
				sword.drawSword();
						
			useIndex += frameNum/2;
		}

		
		// Draw Legs
		for(int i = -1; i <= 1; i += 2) {
			if(useIndex >= frameNum)
				useIndex -= frameNum;
						
			GOGL.setModelMatrix(mat);
			GOGL.transformTranslation(0,i*h,0);

			GOGL.transformRotationY(getUpperLeg(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
			
			GOGL.transformTranslation(uLen,0, 0);
			GOGL.transformRotationY(-getUpperLeg(useIndex).y() + getLowerLeg(useIndex).y());
			GOGL.draw3DBlock(0,-h/2,-h/2, w, h, h);
						
			useIndex += frameNum/2;
		}
		GOGL.transformClear();
		
		GOGL.disableShaders();

	}


	public void setSideRotation(float sideRotAngle) {
		this.sideRotAngle = sideRotAngle;
	}
}
