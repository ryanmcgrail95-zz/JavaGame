package object.environment;

import cont.Messages;
import io.IO;
import io.Mouse;
import gfx.GOGL;
import object.actor.Player;
import object.primitive.Drawable;

public class Water extends Drawable {
	float seaLevel;
	
	public Water(float seaLevel) {
		super(true,false);				
		name = "water";

		this.seaLevel = seaLevel;
		
		shouldAdd = true;
	}
	
	public void draw() {}
	public void add() {
		Player pl = Player.getInstance();

		float x, y;
		x = pl.getX();
		y = pl.getY();
		
		//GOGL.enableShader("Water");
			// Pass Sea Level to Shader
			//GOGL.passShaderFloat("seaLevel", seaLevel);
		
		GOGL.setLightColor(32,128,255);
		GOGL.transformTranslation(x, y, seaLevel);
		GOGL.add3DFloor(-GOGL.VIEW_FAR,-GOGL.VIEW_FAR,GOGL.VIEW_FAR,GOGL.VIEW_FAR,0);
		GOGL.transformClear();
		
		//GOGL.disableShaders();
	}
	
	public void hover() {
				
		Player pl = Player.getInstance();
		
		if(pl.getInventory().findItem("Empty Bucket") != null) {
			Messages.setActionMessage("Fill Empty Bucket with Water");
			
			Mouse.setFingerCursor();
			
			if(Mouse.getLeftClick())
				pl.getInventory().replaceItem("Empty Bucket", "Water Bucket");
		}
	}

	public void update() {}
	public boolean checkOnscreen() {return true;}
	public float calcDepth() {return 0;}
}
