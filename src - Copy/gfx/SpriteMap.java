package gfx;

import java.util.HashMap;
import java.util.Map;

import cont.TextureController;
import func.StringExt;

public class SpriteMap {
	private boolean hasUp;
	private Map<Byte, TextureExt> texMap = new HashMap<Byte, TextureExt>();
	private final static Byte S_STILL = 0, S_STILL_UP = 1, 
							  S_RUN = 2, S_RUN_UP = 3,
							  S_JUMP = 4, S_JUMP_UP = 5,
							  S_SPIN = 6, S_LAND = 7;
	
	
	public SpriteMap(String name) {		
		TextureExt still, stillUp, run, runUp, jump, jumpUp, spin, land;
	
		String capName = (new StringExt(name)).capitalize();
		
        still = TextureController.loadTexture("Resources/Images/Characters/" + name + "/s.gif", "tex" + capName + "Still", TextureController.M_BGALPHA);
        stillUp = TextureController.loadTexture("Resources/Images/Characters/" + name + "/su.gif", "tex" + capName + "StillUp", TextureController.M_BGALPHA);
        run = TextureController.loadTexture("Resources/Images/Characters/" + name + "/r.gif", "tex" + capName + "Run", TextureController.M_BGALPHA);
        runUp = TextureController.loadTexture("Resources/Images/Characters/" + name + "/ru.gif", "tex" + capName + "RunUp", TextureController.M_BGALPHA);
        jump = TextureController.loadTexture("Resources/Images/Characters/" + name + "/j.gif", "tex" + capName + "Jump", TextureController.M_BGALPHA);
        jumpUp = TextureController.loadTexture("Resources/Images/Characters/" + name + "/ju.gif", "tex" + capName + "JumpUp", TextureController.M_BGALPHA);
        spin = TextureController.loadTexture("Resources/Images/Characters/" + name + "/sp.gif", "tex" + capName + "Spin", TextureController.M_BGALPHA);
        land = TextureController.loadTexture("Resources/Images/Characters/" + name + "/ln.gif", "tex" + capName + "Land", TextureController.M_BGALPHA);
 
        addStill(still, stillUp);
        addRun(run, runUp);
        addJump(jump, jumpUp);
        addSpin(spin);
        addLand(land);
	}
	
	
	//STILL
		public void addStill(TextureExt spr, TextureExt sprUp) {
			texMap.put(S_STILL, spr);
			texMap.put(S_STILL_UP, sprUp);
		}		
		public void addStill(TextureExt spr) {
			texMap.put(S_STILL, spr);
			texMap.put(S_STILL_UP, spr);
		}
		public TextureExt getStill() {
			return texMap.get(S_STILL);
		}
		public TextureExt getStillUp() {
			return texMap.get(S_STILL_UP);
		}
	
	//RUN
		public void addRun(TextureExt spr, TextureExt sprUp) {
			texMap.put(S_RUN, spr);
			texMap.put(S_RUN_UP, sprUp);
		}
		public void addRun(TextureExt spr) {
			texMap.put(S_RUN, spr);
			texMap.put(S_RUN_UP, spr);
		}
		public TextureExt getRun() {
			return texMap.get(S_RUN);
		}
		public TextureExt getRunUp() {
			return texMap.get(S_RUN_UP);
		}
	
	//JUMP
		public void addJump(TextureExt spr, TextureExt sprUp) {
			texMap.put(S_JUMP, spr);
			texMap.put(S_JUMP_UP, sprUp);
		}
		public void addJump(TextureExt spr) {
			texMap.put(S_JUMP, spr);
			texMap.put(S_JUMP_UP, spr);
		}
		public TextureExt getJump() {
			return texMap.get(S_JUMP);
		}
		public TextureExt getJumpUp() {
			return texMap.get(S_JUMP_UP);
		}
		
	//SPIN
		public void addSpin(TextureExt spr) {
			texMap.put(S_SPIN, spr);
		}
		public TextureExt getSpin() {
			return texMap.get(S_SPIN);
		}
		
	//SPIN
		public void addLand(TextureExt spr) {
			texMap.put(S_LAND, spr);
		}
		public TextureExt getLand() {
			return texMap.get(S_LAND);
		}
}
