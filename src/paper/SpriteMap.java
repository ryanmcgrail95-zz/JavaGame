package paper;

import gfx.TextureExt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cont.TextureController;
import datatypes.StringExt;

public final class SpriteMap implements AnimationsPM {
	private final static String DIRECTORY = "Resources/Images/Characters/";
	private final static Map<String, SpriteMap> map = new HashMap<String, SpriteMap>();

	private Map<Integer, TextureExt> texMap = new HashMap<Integer, TextureExt>();
	private String name;
	
	
	private TextureExt load(String fn) {
		return TextureController.loadExt(DIRECTORY + name + "/"+ fn + ".gif", TextureController.M_BGALPHA);
	}
	private TextureExt load(String fn, TextureExt backup) {
		TextureExt main = load(fn);
		return (main == null) ? backup : main;
	}

	
	private SpriteMap(String name) {
		this.name = name;
		
		TextureExt still, stillUp, run, runUp, jump, jumpUp, jumpFall, spin, land, hurt, burned;
		TextureExt jumpLand, jumpLandHurt, prepareJump, dodge;
    	
        still = load("s");
        stillUp = load("su", still);

        run = load("r", still);
        runUp = load("ru", run);

        jump = load("j", still);
        jumpUp = load("ju", jump);
        jumpFall = load("jf", jump);
        
        spin = load("sp", still);
        
        land = load("ln", still);
        hurt = load("h", still);
        burned = load("burned", hurt);
        
        jumpLand = load("jln", land);
        jumpLandHurt = load("jlh", hurt);
        prepareJump = load("pj", still);
        dodge = load("d", still);
         
        
        addStill(still, stillUp);
        addRun(run, runUp);
        addJump(jump, jumpUp);
        put(S_JUMP_FALL, jumpFall);
        put(S_JUMP_LAND_HURT, jumpLandHurt);
        addSpin(spin);
        addLand(land);
    	put(S_JUMP_LAND, jumpLand);
    	put(S_PREPARE_JUMP, prepareJump);
    	put(S_DODGE, dodge);
        put(S_HURT, hurt);
        put(S_BURNED, burned);
        
        map.put(name, this);
	}
	
	public TextureExt get(int type) {
		return texMap.get(type);
	}
	private void put(int type, TextureExt tex) {
		texMap.put(type, tex);
	}
	
	public static SpriteMap getSpriteMap(String name) {
		if(map.containsKey(name))
			return map.get(name);
		else
			return new SpriteMap(name);
	}
	
	//STILL
		private void addStill(TextureExt spr, TextureExt sprUp) {
			texMap.put(S_STILL, spr);
			texMap.put(S_STILL_UP, sprUp);
		}		
		private void addStill(TextureExt spr) {
			texMap.put(S_STILL, spr);
			texMap.put(S_STILL_UP, spr);
		}
		
		public TextureExt getStill() 	{return get(S_STILL);}
		public TextureExt getStillUp() 	{return get(S_STILL_UP);}
	
	//RUN
		private void addRun(TextureExt spr, TextureExt sprUp) {
			texMap.put(S_RUN, spr);
			texMap.put(S_RUN_UP, sprUp);
		}
		private void addRun(TextureExt spr) {
			texMap.put(S_RUN, spr);
			texMap.put(S_RUN_UP, spr);
		}
		
		public TextureExt getRun() 		{return texMap.get(S_RUN);}
		public TextureExt getRunUp() 	{return texMap.get(S_RUN_UP);}
	
	//JUMP
		private void addJump(TextureExt spr, TextureExt sprUp) {
			texMap.put(S_JUMP, spr);
			texMap.put(S_JUMP_UP, sprUp);
		}
		private void addJump(TextureExt spr) {
			texMap.put(S_JUMP, spr);
			texMap.put(S_JUMP_UP, spr);
		}
		public TextureExt getJump() 	{return texMap.get(S_JUMP);}
		public TextureExt getJumpUp() 	{return texMap.get(S_JUMP_UP);}
		
	//SPIN
		private void addSpin(TextureExt spr) {
			texMap.put(S_SPIN, spr);
		}
		public TextureExt getSpin() 	{return texMap.get(S_SPIN);}
		
	//SPIN
		private void addLand(TextureExt spr) {
			texMap.put(S_LAND, spr);
		}
		public TextureExt getLand() 	{return texMap.get(S_LAND);}
		
		
		public void destroy() {
			map.remove(name);
			
			Set<Entry<Integer, TextureExt>> s = texMap.entrySet();
			for(Entry<Integer, TextureExt> e : s)
				e.getValue().destroy();
			texMap.clear();
			
			s.clear();
			texMap = null;
		}	
}
