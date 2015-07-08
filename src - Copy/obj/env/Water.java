package obj.env;

import cont.IO;
import gfx.GOGL;
import obj.chr.Player;
import obj.prim.Drawable;

public class Water extends Drawable {
	float seaLevel;
	
	public Water(float seaLevel) {
		super(true);
		this.seaLevel = seaLevel;
	}
	
	public boolean draw() {
		Player pl = Player.getInstance();

		float x, y;
		x = pl.getX();
		y = pl.getY();
		
		//GOGL.enableShader("Water");
			// Pass Sea Level to Shader
			//GOGL.passShaderFloat("seaLevel", seaLevel);
		
		GOGL.transformClear();
		GOGL.transformTranslation(x, y, seaLevel);
		GOGL.draw3DFloor(-1000,-1000,1000,1000,0);
		GOGL.transformClear();
		
		//GOGL.disableShaders();
		
		return false;
	}
	
	public void hover() {
		Player pl = Player.getInstance();
		
		if(pl.getInventory().findItem("Empty Bucket") != null) {
			GOGL.setOrtho();
			GOGL.drawStringS(0,0,"Fill Empty Bucket with Water");
			GOGL.setPerspective();
			
			IO.setCursor(IO.C_POINTING_FINGER);
			
			if(IO.getLeftClick()) {
				pl.getInventory().replaceItem("Empty Bucket", "Water Bucket");
			}
		}
	}
}
