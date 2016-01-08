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
		
		GL.clear(RGBA.YELLOW);
		GL.getMainCamera().setProjection(x,y,z,toX,toY,toZ);
		
		GT.transformClear();
		GT.transformTranslation(0,0,0);
		
			GT.transformRotationZ(GL.getTime());

			GT.transformScale(2);
			GT.transformRotationX(-90);
			
			GL.enableCulling();
			GL.enableShader("Model");
			mod.draw();
			GL.disableShaders();
		GT.transformClear();
	}

	@Override
	public void add() {
		// TODO Auto-generated method stub
		
	}
}
