package btl;

import func.Math2D;
import gfx.GOGL;
import gfx.Shape;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import obj.prim.Drawable;

public class DamageStar extends Drawable {
	float x, y, z;
	float starPos, starSc, starToSc, starC, starToC, starTimer, starTimes;
	float numPos, numSc, numA;
	boolean starArrive, numArrive;
	int dir = 1;
	
	Shape shNum, shStar, shOpStar;
	Texture numTex, starTex;
	
	public DamageStar(float x, float y, float z, int num) {
		super();
		
		this.x = x;
		this.y = y;
		this.z = z;

		starPos = 0;
		starSc = 0;
		starArrive = false;
		starToSc = 1.5f;
		starTimes = 0;

		//dir = 1;

		numPos = 0;
		numSc = 1;
		numA = 1;
		numArrive = false;

		starTimer = 80;
		starC = 1;
		starToC = 0;

		numTex = TextureController.getTexture("texDamageStar" + num);
		starTex = TextureController.getTexture("texDamageStar");
		
		float s, r;
		r = 14;
		s = 15;
		
		
		shOpStar = Shape.createWall("", 0,-s,s, 0,s,-s, starTex);
		//shOpStar.addWall(-16,0,16,16,0,-16, starTex);
		shStar = Shape.createWall("", 0,-s,s, 0,s,-s, starTex);
		shNum = Shape.createWall("", 0,-s,s, 0,s,-s, numTex);
	}
	
	public void destroy() {
		super.destroy();
		
		shOpStar.destroy();
		shStar.destroy();
		shNum.destroy();
	}
	
	public void update(float deltaT) {
		super.update(deltaT);
		
		starC += (starToC - starC)/15;
		if(Math.abs(starToC - starC) < .05) {
		    starC = starToC;
		    starToC = Math.abs(starToC - 1);
		}


		if(starTimer > -1) {
		    if(starTimes < 2) {
		        if(starToSc != 1)
		            starSc += (starToSc - starSc)/6.5;
		        else {
		            starSc += Math.min(-.01,-(starToSc + (starToSc - starSc)))/7;
		            
		            if(starSc < starToSc)
		                starSc = starToSc;
		        }
		    
		        if(Math.abs(starToSc - starSc) < .05) {
		            starToSc = 1;// + (starSc - starToSc)*.6;
		            starTimes += 1;
		        }
		    }
		}

		if(!starArrive) {
		    starPos += (1 - starPos)/5;
		    numPos += (1 - numPos)/1.5;

		    if(starPos > .95) {
		        starPos = 1;
		        starArrive = true;
		    }
		    if(numPos > .95) {
		        numPos = 1;
		        numArrive = true;
		    }
		}
		else {
		    if(starTimer > -1) {
		        starTimer -= 1;
		        
		        if(starTimer < 15)
		            starSc += (1.2 - starSc)/12;
		    }
		    else {
		        starSc += (0 - starSc)/5;
		        numA += (0 - numA)/8;
		        
		        if(numA < .05)
		            destroy();
		    }
		}

		float r, s, sX, sY, sZ, nX, nY, nZ, cDir, cX, cY;
		cDir = GOGL.getCamDir();
		cX = Math2D.calcLenY(1, cDir);
		cY = Math2D.calcLenX(1, cDir);
		
		r = 14;
		s = 15;
		
		sY = 0;//-7f;
		//sX = dir*starPos*r;
		sX = -3f;
		sZ = starPos*r;

		nY = 0;//-7.3f;
		//nX = dir*numPos*r
		nX = -8f;
		nZ = numPos*r;

		
		shOpStar.setRotation(0,0,cDir);
		shStar.setRotation(0,0,cDir);
		shNum.setRotation(0,0,cDir);
		
		
		shOpStar.setScale(starSc);
		shStar.setScale(starSc);
		shNum.setScale(numSc,-numSc,numSc);
		
		shOpStar.setShapePosition(sX,sY,0);
		shStar.setShapePosition(sX,sY,0);
		shNum.setShapePosition(nX,nY,0);
		
		shOpStar.setPosition(x,y,z + sZ + 16);
		shStar.setPosition(x,y,z + sZ + 16);
		shNum.setPosition(x,y,z + nZ + 16);
		
		shStar.setAlpha(starC);
		shNum.setAlpha(numA);
	}
}
