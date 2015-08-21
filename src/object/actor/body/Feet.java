package object.actor.body;

import java.util.ArrayList;
import java.util.List;

import datatypes.vec2;
import datatypes.vec3;
import time.Timer;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.GOGL;
import gfx.RGBA;

public class Feet {
	
	private Timer genTimer;
	private Loop curFeet;
	
	public Feet() {		
		genTimer = new Timer(2);
		curFeet = new Loop();
		randomizeFeet();
	}

	
	private class Loop {
		private List<vec3> points;
		private List<vec2> angs;
		
		public Loop() {
			points = new ArrayList<vec3>();
			angs = new ArrayList<vec2>();
		}
		
		public void clear() {
			points.clear();
			angs.clear();
		}
		public void addPt(float x, float y, float z) {
			points.add(new vec3(x,y,z));
			angs.add(new vec2());
		}
		public void addPt(float x, float y, float z, float dir, float zDir) {
			points.add(new vec3(x,y,z));
			angs.add(new vec2(dir,zDir));
		}
		public void genAngs() {
			
			int si = points.size();
			
			int curP;
			vec3 ptP, pt, ptN, ptData;
			ptP = points.get(si-1);			
			pt = points.get(0);
			ptN = points.get(1);

			for(int i = 0; i < si; i++) {
				
				ptData = Math3D.calcPtData(ptP,ptN);
				
				//angs.set(i, new vec2(ptData.get(1),ptData.get(2)+90));
				angs.set(i, new vec2(90,0));

				curP = (i+2);
				if(curP >= si)
					curP -= si;
				
				ptP = pt;
				pt = ptN;
				ptN = points.get(curP);
			}
		}
		
		public void draw() {
			
			if(points.isEmpty())
				return;
			
			int si = points.size();
			int uI;
			float x,y,z, d=0,dZ, aX,aY,aZ;
			vec3 curPt;
			vec2 curAng;
			
			GOGL.begin(GOGL.P_TRIANGLE_STRIP);
				for(int i = 0; i <= si; i++) {
					
					uI = i;
					if(i == si)
						uI = 0;
					
					curPt = points.get(uI);
					curAng = angs.get(uI);
					
					x = curPt.get(0);
					y = curPt.get(1);
					z = curPt.get(2);
					
					d = curAng.get(0);
					dZ = curAng.get(1);
					
					aX = Math3D.calcPolarX(3,d,dZ);
					aY = Math3D.calcPolarY(3,d,dZ);
					aZ = Math3D.calcPolarZ(3,d,dZ);
					
					GOGL.vertex(x+aX, y+aY, z+aZ);
					GOGL.vertex(x-aX, y-aY, z-aZ);
				}
			GOGL.end();
		}
	}
	
	private void addHalfLoopV(float cX, float cY, float cZ, float radH, float radV, float dir, int nSteps, float maxBend) {
		float d;
		float uX=0,uY=0,uZ=0;
		
		float pX,pY,pZ;
		
		float r,co,si, dis;
		float bend;
				
		for(int i = 0; i < nSteps; i++) {
			
			pX=uX;
			pY=uY;
			pZ=uZ;
			
			d = -45 + 90f*i/nSteps;
			
			co = Math2D.calcLenX(d);
			si = Math2D.calcLenY(d);
			r = radH*Math2D.calcLenX(2*d);	
			dis = r*co;
			
			bend = MathExt.sqr(r/radH)*maxBend;
			
			uX = cX + Math2D.calcLenX(dis,dir) + Math2D.calcLenX(bend, dir+90);
			uY = cY + Math2D.calcLenY(dis,dir) + Math2D.calcLenY(bend, dir+90);;
			uZ = cZ + r*si;
			
			curFeet.addPt(uX,uY,uZ,Math2D.calcPtDir(pX,pY,uX,uY)+90,0);
		}
	}
	
	private void addHalfLoopH(float cX, float cY, float cZ, float radR, float radT, float dir, int nSteps) {
		float d;
		float uX=0,uY=0,uZ=0;
		
		float pX,pY,pZ;
		
		float rX,rY,co,si, disX,disY;
				
		for(int i = 0; i < nSteps; i++) {
			
			pX=uX;
			pY=uY;
			pZ=uZ;
			
			d = -45 + 90f*i/nSteps;
			
			co = Math2D.calcLenX(d);
			si = Math2D.calcLenY(d);

			rX = radR*Math2D.calcLenX(2*d);	
			rY = radT*Math2D.calcLenX(2*d);	
			
			disX = rX*co;
			disY = rY*si;
			
			uX = cX + Math2D.calcLenX(disX,dir)+Math2D.calcLenX(disY,dir+90);
			uY = cY + Math2D.calcLenY(disY,dir)+Math2D.calcLenY(disX,dir+90);
			uZ = cZ;
			
			curFeet.addPt(uX,uY,uZ,Math2D.calcPtDir(pX,pY,uX,uY)+90,90);
		}
	}
	
	/*private void addFigure8() {
		int n = 20;
		float cX = 0, cY = 0, cZ = 0, d, frontRadius, backRadius, uRX, uRY;
		float uX=0,uY=0,uZ=0;
		
		float centerX, centerY, centerZ;
		centerX = 0;
		centerY = 0;
		centerZ = 0;
		
		frontRadius = Math2D.rnd(12,24); //16
		backRadius = (32+16)-frontRadius; // 32
		
		centerX = 16-frontRadius;
		centerY = Math2D.rnd(-4,4);
		
		float pX,pY,pZ;
		
		float rX,rY,co,si, disX, disY;
		float bend, maxFrontBend, maxBackBend, maxBend, dir;
		
		dir = 0;
		
		maxFrontBend = Math2D.rnd(-16,16);
		maxBackBend = Math2D.rnd(-16,16);

		
		for(int i = 0; i < n; i++) {
			
			pX=uX;
			pY=uY;
			pZ=uZ;
			
			d = 180f*i/n;

			if(d <= 90) {
				d = -45 + d;
				uRX = frontRadius;
				maxBend = maxFrontBend;
			}
			else {
				d = -45 + d-90 + 180;
				uRX = backRadius;
				maxBend = maxBackBend;
			}

			uRY = frontRadius;
			
			co = Math2D.calcLenX(d);
			si = Math2D.calcLenY(d);
			rX = uRX*Math2D.calcLenX(2*d);
			rY = uRY*Math2D.calcLenX(2*d);
			
			disX = rX*co;
			disY = rY*co;
			
			bend = (float) Math2D.sqr(rX/uRX)*maxBend;
			
			uX = centerX + Math2D.calcLenX(disX,dir) + Math2D.calcLenX(bend, dir+90);
			uY = centerY + Math2D.calcLenY(disX,dir) + Math2D.calcLenY(bend, dir+90);;
			uZ = centerZ + rX*si;
			
			curFeet.addPt(uX,uY,uZ,Math2D.calcPtDir(pX,pY,uX,uY)+90,0);
		}
	}*/
	
	private void addSides() {
		addHalfLoopH(0,0,0, 24,16, 90, 10);
	}
	
	private void addFigure8() {
		float frontRadius, backRadius;		
		float centerX, centerY, centerZ;
		float maxFrontBend, maxBackBend;
		
		centerX = 0;
		centerY = 0;
		centerZ = 0;
		
		frontRadius = MathExt.rnd(12,24); //16
		backRadius = (32+16)-frontRadius; // 32
		
		centerX = 16-frontRadius;
		centerY = MathExt.rnd(-4,4);
				
		maxFrontBend = MathExt.rnd(-16,16);
		maxBackBend = MathExt.rnd(-16,16);
	
		addHalfLoopV(centerX,centerY,centerZ, frontRadius,frontRadius, 0, 10, maxFrontBend);
		addHalfLoopV(centerX,centerY,centerZ, backRadius,backRadius, 180, 10, maxBackBend);
		
		
		float pX,pZ, d,nZ;
		for(vec3 pt : curFeet.points) {
			pX = pt.get(0);
			pZ = pt.get(2);
			
			if(pZ > 0) {
				d = (frontRadius-pX)/(frontRadius+backRadius);
				nZ = pZ + pZ*MathExt.sqr(d);
				
				pt.set(2, nZ);
			}
		}
	}
	
	private void addThree() {
		float frontRadius, backRadius, thirdRadius;		
		float centerX, centerY, centerZ;
		float maxFrontBend, maxBackBend, maxThirdBend;
		
		centerX = 0;
		centerY = 0;
		centerZ = 0;
		
		frontRadius = MathExt.rnd(12,24); //16
		backRadius = (32+16)-frontRadius; // 32
		thirdRadius = MathExt.mean(frontRadius,backRadius);
		
		centerX = 16-frontRadius;
		centerY = MathExt.rnd(-4,4);
				
		maxFrontBend = MathExt.rnd(-16,16);
		maxBackBend = MathExt.rnd(-16,16);
		maxThirdBend = MathExt.mean(maxFrontBend,maxBackBend);
	
		addHalfLoopV(centerX,centerY,centerZ, frontRadius,frontRadius, 0, 10, maxFrontBend);
		addHalfLoopV(centerX,centerY,centerZ, backRadius,backRadius, 180, 10, maxBackBend);
		addHalfLoopV(centerX,centerY,centerZ, thirdRadius,thirdRadius, MathExt.rnd(360), 10, maxThirdBend);
	}
	
	private void addBean() {
		float frontRadius, backRadius;		
		float centerX, centerY, centerZ;
		float maxFrontBend, maxBackBend;
		
		centerX = 0;
		centerY = 0;
		centerZ = 0;
		
		frontRadius = MathExt.rnd(12,24); //16
		backRadius = (32+16)-frontRadius; // 32
		
		centerX = 16-frontRadius;
		centerY = MathExt.rnd(-4,4);
				
		maxFrontBend = MathExt.rnd(-16,16);
		maxBackBend = MathExt.rnd(-16,16);
	
		addHalfLoopV(centerX,centerY,centerZ, frontRadius,frontRadius, 0, 10, maxFrontBend);
		addHalfLoopV(centerX,centerY,centerZ, backRadius,backRadius, 180, 10, maxBackBend);
		
		float pX,pZ,within = frontRadius;
		
		for(vec3 pt : curFeet.points) {
			pX = pt.get(0);
			pZ = pt.get(2);
			
			if(Math.abs(pX) < within) {
				if(pZ > 0)
					pt.set(2, MathExt.interpolate(pZ,frontRadius/2, within-Math.abs(pX)/within));
				else
					pt.set(2, -frontRadius/2);
			}
		}
	}

	
	private void randomizeFeet() {
		float rnd = MathExt.rnd(3);
				
		if(!genTimer.check())
			return;
		
		curFeet.clear();
		//addSides();
		switch((int) Math.floor(rnd)) {

			case 0: addThree();
				break;
				
			//case 1: addBean();
			//	break;
		
			default: addFigure8();
		}
	}
	
	public void draw(float x, float y, float z, float dir) {
		
		randomizeFeet();
		
		
		GOGL.transformTranslation(x, y, z);
		GOGL.transformRotationZ(dir);
		
			GOGL.setColor(RGBA.RED);
			curFeet.draw();
			GOGL.setColor(RGBA.WHITE);
			
		GOGL.transformClear();
	}
}
