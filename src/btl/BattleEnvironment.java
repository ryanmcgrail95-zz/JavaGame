package btl;

import gfx.GL;
import object.primitive.Drawable;

public class BattleEnvironment extends Drawable {

	public BattleEnvironment(String modelName) {
		super(false,false);
		/* new ModelRenderer(modelName);
		 * 
		 * 		float row1Y = 80, row2Y = 105;
		float[] purple = {.73f,.43f,.61f};
		float[] yellow = {.78f,.65f,.12f};
		
		new BattleFlower(-110,row2Y,19, purple);
		new BattleFlower(-42,row2Y,36, purple);
		new BattleFlower(-20,row2Y,29, yellow);
		new BattleFlower(20,row1Y,22, purple);
		new BattleFlower(48,row1Y,29, yellow);
		new BattleFlower(87,row1Y,21, purple);

		 */
	}
	
	public void draw() {
		GL.enableShader("battleFloor");
			GL.draw3DFloor(-300, -200, 300,300, 0);
		GL.disableShaders();
	}

	public void add() {}
}
