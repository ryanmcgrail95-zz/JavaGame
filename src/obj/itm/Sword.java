package obj.itm;

import object.actor.Player;
import functions.Math2D;
import gfx.GOGL;

public class Sword extends WeaponItem {
	float handleLen, handleWidth;
	float bladeLen, bladeTipLen, bladeWidth, bladeThickness;
	float tipLength;
	
	public Sword(String name) {
		super(name);

		
		handleLen = 4;
		handleWidth = .3f;
		
		bladeLen = 15;
		bladeTipLen = 5;
		bladeWidth = .8f;
		bladeThickness = .3f;
		
		visible = true;
	}
	
	public boolean checkOnscreen() {
		return true;
	}
	
	public float calcDepth() {
		return 0;
	}
	
	public void draw() {

		GOGL.transformClear();
		
		GOGL.transformClear();		
	}

	public void drawSword() {
			drawHandle();
		GOGL.transformTranslation(0,0,handleLen);
			drawBladeBody();
		GOGL.transformTranslation(0,0,bladeLen);
			drawBladeTip();
	}
	
	public void drawHandle() {
		GOGL.begin(GOGL.P_TRIANGLE_STRIP);

		float d, xN,yN, xAmt, yAmt;
		for(int i = 0; i < 7; i++) {
			d = i/6f*360;
			xN = Math2D.calcLenX(d);	xAmt = xN*handleWidth;
			yN = Math2D.calcLenY(d);	yAmt = yN*handleWidth;
			GOGL.vertex(xAmt,yAmt,handleLen,xN,yN,0);
			GOGL.vertex(xAmt,yAmt,0,xN,yN,0);
		}
		
		GOGL.end();
	}
	
	public void drawBladeBody() {
		GOGL.begin(GOGL.P_TRIANGLE_STRIP);

		float d, xN,yN, xAmt, yAmt;
		for(int i = 0; i < 7; i++) {
			d = i/6f*360;
			xN = Math2D.calcLenX(d);	xAmt = xN*bladeWidth;
			yN = Math2D.calcLenY(d);	yAmt = yN*bladeThickness;
			GOGL.vertex(xAmt,yAmt,bladeLen,xN,yN,0);
			GOGL.vertex(xAmt,yAmt,0,xN,yN,0);
		}
		
		GOGL.end();
	}
	
	public void drawBladeTip() {
		GOGL.begin(GOGL.P_TRIANGLE_STRIP);

		float d, xN,yN, xAmt, yAmt;
		float wBot=bladeWidth, wTop;
		float zBot=0, zTop, zAmt = .1f;
		for(float z = zAmt; z <= 1; z += zAmt) {
			
			zTop = z * bladeTipLen;
			wTop = (1-z) * bladeWidth;
			
			for(int i = 0; i < 7; i++) {
				d = i/6f*360;
				xN = Math2D.calcLenX(d);
				yN = Math2D.calcLenY(d);	yAmt = yN*bladeThickness;
				GOGL.vertex(xN*wTop,yAmt,zTop,xN,yN,0);
				GOGL.vertex(xN*wBot,yAmt,zBot,xN,yN,0);
			}
			
			zBot = zTop;
			wBot = wTop;
		}
		
		GOGL.end();
	}
}
