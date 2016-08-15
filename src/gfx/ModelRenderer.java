package gfx;

import collision.C3D;
import object.primitive.Drawable;
import resource.model.Model;

public class ModelRenderer extends Drawable {

	private float dis = 10;
	private Model mod;
	
	public ModelRenderer(String modelName) {
		super(false,false);
		mod = Model.get(modelName, true);
		mod.addReference();
		
		name = "ModelRenderer: " + modelName;
		
		//C3D.splitModel(mod, 10,10,48);

		C3D.splitModel(mod, 10,10,32);

		// 1,1 works PERFECTLY??
		//C3D.splitModel(mod, 1,1,64);
	}
	
	public void destroy() {
		super.destroy();
		mod.removeReference();
	}

	@Override
	public void draw() {
		float x,y,z, toX,toY,toZ;
		toX = toY = toZ = 0;
		
		x = -dis;
		y = z = 0;
		z = 1;
		
		/*GL.clear(RGBA.YELLOW);
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
		GT.transformClear();*/
		
		//start("ModelRenderer.draw()");
		
		GL.setPerspective();
		GT.transformClear();
		GL.resetColor();
			GL.enableShader("Model");
			
			mod.drawFast();
			GL.disableShaders();
		GT.transformClear();
				
		G3D.draw3DFloor(0,0,20,20,0);
		
		//end("ModelRenderer.draw()");
	}

	@Override
	public void add() {}
}
