package object.environment;

import functions.Math2D;
import gfx.Camera;
import gfx.GOGL;
import gfx.RGBA;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.model.Model;

public class House extends Environmental {
	
	private float w = 70, h = w*3/2;
	private RGBA color;

	public House(float x, float y) {
		super(x, y, false,false);
		z(calcZ());
		color = RGBA.randomizeAboveValue(.5f);
	}

	public boolean collide(Physical other) {
		return other.collideRectangle(x(),y(),w,h);
	}
	
	public void draw() {
					
		GOGL.enableLighting();
		
		GOGL.transformClear();
		transformTranslation();
		GOGL.transformRotationZ(180);
		GOGL.transformScale(.6f);
		
		GOGL.setLightColori(color.getRi(),color.getGi(),color.getBi());
		Model.MOD_HOUSEBODY.draw();

		GOGL.setLightColori(255,255,255);
		Model.MOD_HOUSEFRAME.draw();
		
		GOGL.transformClear();
		
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();		
	}
	
	private float calcZ() {
		float minZ = 1000000;
		float[] corners = getCornerPoints();
		
		for(int i = 0; i < 8; i += 2)
			minZ = Math.min(minZ, Heightmap.getInstance().getZ(corners[i],corners[i+1]));
		
		return minZ;
	}
	
	public float[] getCornerPoints() {
		float[] pts = new float[8];
		
		float d, x,y;
		for(int i = 0; i < 4; i++) {
			d = i*90;
			
			x = Math2D.calcLenX(w,d) + Math2D.calcLenX(h,d+90);
			y = Math2D.calcLenY(w,d) + Math2D.calcLenY(h,d+90);
			
			pts[2*i] = x() + x;
			pts[2*i+1] = y() + y;
		}
		
		return pts;
	}
}
