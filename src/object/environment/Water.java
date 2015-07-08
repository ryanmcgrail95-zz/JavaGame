package object.environment;

import io.IO;
import io.Mouse;
import gfx.GOGL;
import object.actor.Player;
import object.primitive.Drawable;

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
		
		GOGL.transformTranslation(x, y, seaLevel);
		GOGL.draw3DFloor(-GOGL.VIEW_FAR,-GOGL.VIEW_FAR,GOGL.VIEW_FAR,GOGL.VIEW_FAR,0);
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
			
			Mouse.setFingerCursor();
			
			if(Mouse.getLeftClick())
				pl.getInventory().replaceItem("Empty Bucket", "Water Bucket");
		}
	}
}
