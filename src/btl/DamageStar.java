package btl;

import functions.Math2D;
import functions.MathExt;
import gfx.GL;
import gfx.G2D;
import gfx.GT;
import gfx.MultiTexture;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import object.primitive.Drawable;
import time.Delta;

public class DamageStar extends Drawable {
	float x, y, z;
	float starPos, starSc, starToSc, starC, starToC, starTimer, starTimes;
	float numPos, numSc, numA;
	boolean starArrive, numArrive;
	int dir = 1;
	int num;
	
	float eStarSc, eStarScTimer = 0;
	
	private float timer = 100;
	
	boolean isPlayer;
	
	private static final MultiTexture numTex = new MultiTexture("Resources/Images/Battle/num.png",3,1);
	private static final Texture starTex = TextureController.getTexture("texDamageStar");
	
	public DamageStar(float x, float y, float z, int num, boolean isPlayer) {
		super(false,false);
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.isPlayer = isPlayer;

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

		starTimer = 40; //80
		starC = 1;
		starToC = 0;
		
		this.num = num-1;
		
		float s, r;
		r = 14;
		s = 15;		
	}
		
	public void update() {}
	
	public void draw() {		
		timer -= Delta.convert(3.8f);
		if(timer < 0)
			timer = 0;
		
		starC = MathExt.to(starC, starToC, 15);
		if(Math.abs(starToC - starC) < .05) {
		    starC = starToC;
		    starToC = Math.abs(starToC - 1);
		}


		if(starTimer > -1) {
		    if(starTimes < 2) {
		        if(starToSc != 1)
		            starSc = MathExt.to(starSc, starToSc, 6.5f);
		        else {
		            starSc += Delta.convert((float) (Math.min(-.01,-(starToSc + (starToSc - starSc)))/7f));
		        	//starSc += (float) (Math.min(-.01,Delta.convert(-(starToSc + (starToSc - starSc)))/7f));
		            
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
		    starPos = MathExt.to(starPos, 1, 4);
		    numPos = MathExt.to(numPos, 1, 2);

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
		        starTimer -= Delta.convert(1);
		        
		        if(starTimer < 15)
		            starSc = MathExt.to(starSc, 1.2f, 8);
		    }
		    
		    //Shrinking
		    else { 	
		        starSc = MathExt.to(starSc, 0, 4);
		        numA = MathExt.to(numA, 0, 6);
		        
		        if(numA < .05)
		            destroy();
		    }
		}

		float r, s, sX, sY, sZ, nX, nY, nZ, cDir, cX, cY;
		cDir = GT.getMainCamera().getDirection();
		cX = Math2D.calcLenY(1, cDir);
		cY = Math2D.calcLenX(1, cDir);
		
		r = 20; // 14
		s = 15;
		
		float mF = 1.3f;
		
		sY = (isPlayer ? 1 : -1) * starPos*r* mF;
		sX = 0;//-7f;
		//sX = -3f;
		sZ = starPos*r;

		nY = (isPlayer ? 1 : -1) * numPos*r * mF;
		nX = -2;//-7.3f;
		//nX = -8f;
		nZ = numPos*r;
		

		float dStarSc = starSc, dNumSc = numSc;
		if(timer > 0) {
			float f = timer/100;
			float a = Math2D.calcLenY(f*360*3), aM;
			
			a = (.5f + .5f*a);
			aM = Math2D.calcLenY(f*180);
			a *= 2*aM;
			
			dStarSc = f*a
					+ (1-f)*starSc;
		}
		
		/*if(eStarScTimer-- <= 0) {
			eStarScTimer = 2;
			eStarSc = MathExt.rndSign()*MathExt.rnd(.025f);
		}
		
		dStarSc *= 1 + eStarSc;*/
		
		float nFM = 60, nFME = 100-nFM, nF = (timer-nFME)/nFM;
		nF = (float) Math.pow(MathExt.contain(0,nF,1), 2);
		dNumSc *= 1 + .6f*Math2D.calcLenY(180 * nF);

		// Draw Op Star
		GT.transformClear();
		GT.transformTranslation(x,y,z + sZ + 16);
		GT.transformRotationZ(cDir);
		GT.transformTranslation(sX,sY,0);
		GT.transformScale(dStarSc);
		
			GT.draw3DWall(0,-s,s, 0,s,-s, starTex);
		
		// Draw Star
		GT.transformClear();
		GT.transformTranslation(x,y,z + sZ + 16);
		GT.transformRotationZ(cDir);
		GT.transformTranslation(sX,sY,0);
		GT.transformScale(dStarSc);
	
			GT.setAlpha(starC);
			GT.draw3DWall(0,-s,s, 0,s,-s, starTex);

		// Draw Num
		float nS = s*.6f;
			
		GT.transformClear();
		GT.transformTranslation(x,y,z + nZ + 16);
		GT.transformRotationZ(cDir);
		GT.transformTranslation(nX,nY,0);
		GT.transformScale(dNumSc,-dNumSc,dNumSc);

			GT.setAlpha(numA);
			GT.draw3DWall(0,-nS,nS, 0,nS,-nS, numTex,num);

		GT.transformClear();
		
		GL.setAlpha(1);		
	}
	
	public float calcDepth() {
		return 10;
	}

	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}
}
