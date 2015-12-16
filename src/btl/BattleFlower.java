package btl;

import functions.Math2D;
import functions.MathExt;
import gfx.GL;
import gfx.GT;
import object.primitive.Drawable;
import resource.model.Model;
import time.Delta;

public class BattleFlower extends Drawable {
	private float[] color;
	private float x, y, z;
	private float upDir = 0, upTimer = -1;
	
	public BattleFlower(float x, float y, float z, float[] color) {
		super(false, false);
		this.color = color;
		this.x = x;
		this.y = y;
		this.z = z;
		resetTimer();
	}

	public float calcDepth() {
		return 2;
	}
	
	private void resetTimer() {
		upTimer = MathExt.rndi(50,100);
	}
	
	public void draw() {
		float upZ = 0;
		
		
		if(upDir > 0) {
			upDir -= Delta.convert(25);
			if(Math2D.calcLenX(upDir) < 0)
				upZ = 2;
			
			if(upDir <= 0) {
				upDir = 0;
				resetTimer();
			}
		}
		
		
		if(upTimer > -1) {
			upTimer -= Delta.convert(1);
			if(upTimer <= 0) {
				upDir = 720;
				upTimer = -1;
			}
		}
		
		GT.transformTranslation(x,y,z);
		GT.transformTranslation(0,0,upZ);
		GT.transformScale(390);
		GL.setColorf(color[0],color[1],color[2],1);
			Model.get("flower").draw();
		GL.resetColor();
		GT.transformClear();
	}
	
	public void add() {}
}
