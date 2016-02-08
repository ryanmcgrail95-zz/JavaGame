package paper;

import time.Delta;
import functions.Math2D;
import functions.MathExt;
import gfx.G2D;
import gfx.GL;
import gfx.GT;
import gfx.TextureExt;
import cont.TextureController;

public class ZapPM implements AnimationsPM {
	private final static TextureExt zapTex = TextureController.loadExt("Resources/Images/Characters/extra/zap.gif", TextureController.M_BGALPHA);
	private float index1, index2 = 180;
	private float size = 9, distance = 8, height, exDistance = 3;
		
	public ZapPM(float height) {
		this.height = height;
	}

	public void animateModel() {
		float spd = 5f;
		index1 += Delta.convert(spd);
		if(index1 >= 360)
			index1 -= 360;
		index2 += Delta.convert(spd);
		if(index2 >= 360)
			index2 -= 360;
	}
	
	public float calcDistance(float dir) {
		float exD;
		exD = exDistance * MathExt.contain(0, dir/40, 1);
		return distance + exD;
	}
	
	public float calcScale(float dir) {
		float d, s1, s2, f;
		f = dir/360;
		
		d = 30;
		if(dir < d)
			return .5f + .6f*dir/d;
		else
			return 1.1f*(360-dir)/(360-d);		
	}
	
	public void draw() {
		GL.start("ZapPM.draw()");

		float dX, dY, dW, dH;
		
		float curD, curS;
		GT.transformTranslation(0,height*.5f,0);
		for(int i = 0; i < 5; i++) {			
			if(i == 1 || i == 3) {
				curD = calcDistance(index1);
				curS = calcScale(index1);
			}
			else {
				curD = calcDistance(index2);
				curS = calcScale(index2);
			}
			
			dW = dH = curS*size;
			dW *= -1;
			dX = -dW/2;
			dY = -dH/2;
			
			curD += (1-curS)*size/4.5;
			
			float d = 90 - 1f*i/5*360;
			GT.transformRotationZ(d);
				GT.transformTranslation(curD,0,0);
					GT.transformRotationZ(135);
						G2D.drawTexture(dX,dY, dW, dH, zapTex.getTexture());
					GT.transformRotationZ(-135);
				GT.transformTranslation(-curD,0,0);
			GT.transformRotationZ(-d);
		}
		GT.transformTranslation(0,-height*.5f,0);

		GL.end("ZapPM.draw()");
	}
}
