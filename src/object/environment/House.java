package object.environment;

import gfx.Camera;
import gfx.GOGL;
import gfx.RGBA;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.model.Model;

public class House extends Environmental {
	
	RGBA color;

	public House(float x, float y) {
		super(x, y, false,false);
		color = RGBA.randomizeAboveValue(.5f);
		
		color.println();
	}

	public boolean collide(Physical other) {
		float w, h;
		w = 70;
		h = w*3/2;
		return other.collideRectangle(x(),y(),w,h);
	}
	
	public void draw() {
					
		GOGL.enableLighting();
		
		GOGL.transformClear();
		transformTranslation();
		GOGL.transformRotationZ(180);
		GOGL.transformScale(.6f);
		
		GOGL.setLightColori(color.getRi(),color.getGi(),color.getBi());
		//GOGL.setLightColori(55,17,8);
		Model.MOD_HOUSEBODY.draw();

		GOGL.setLightColori(255,255,255);
		Model.MOD_HOUSEFRAME.draw();
		
		GOGL.transformClear();
		
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();		
	}
}
