package gfx;

import object.primitive.Drawable;
import resource.model.Model;

public class ModelRenderer extends Drawable {

	private float dis = 10;
	private Model mod;
	
	public ModelRenderer(Model mod) {
		super(false,false);
		this.mod = mod;
	}

	@Override
	public void draw() {
		float x,y,z, toX,toY,toZ;
		toX = toY = toZ = 0;
		
		x = -dis;
		y = z = 0;
		z = 1;
		
		GOGL.clear(RGBA.YELLOW);
		GOGL.getMainCamera().setProjection(x,y,z,toX,toY,toZ);
		
		GOGL.transformClear();
		GOGL.transformTranslation(0,0,0);
		
			GOGL.transformRotationZ(GOGL.getTime());

			GOGL.transformScale(2);
			GOGL.transformRotationX(90);
			mod.draw();
		GOGL.transformClear();
	}

	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}
}
