package cont;

import gfx.SpriteMap;

import java.util.HashMap;
import java.util.Map;

public class CharacterController {
	private static Map<String, Chrctr> chrctrMap = new HashMap<String, Chrctr>();
	private static final byte T_NORMAL = 0, T_BOMB = 1;
	
	
	public static class Chrctr {
		String name;
		boolean playable;
		byte modelType;
		double height;
		double hp, attack, defense, spike;
		SpriteMap map;
		
		public Chrctr(String name, boolean playable, byte modelType, double height, double hp, double attack, double defense, double spike) {
			this.name = name;
			this.playable = playable;
			this.modelType = modelType;
			this.height = height;
			this.hp = hp;
			this.attack = attack;
			this.defense = defense;
			this.spike = spike;
			this.map = new SpriteMap(name);
		}
		
		public SpriteMap getSpriteMap() {
			return map;
		}
	}
	
	public static void ini() {
		add("mario",true,T_NORMAL,24,50,6,6,-1);

		/*add("goombario",false,T_NORMAL,15,2,1,0,-1);
		add("kooper",false,T_NORMAL,24,2,1,1,-1);
		//add("watt",false,0,24,2,1,1,-1);

		add("goompa",false,T_NORMAL,15,2,1,0,-1);
		add("goompapa",false,T_NORMAL,15,2,1,0,-1);
		add("goomama",false,T_NORMAL,15,2,1,0,-1);


		add("watt",false,T_NORMAL,15,2,1,0,-1);
		add("goomba",false,T_NORMAL,15,2,1,0,-1);
		add("bobomb",false,T_BOMB,24,2,1,0,-1);
		add("toad",false,T_NORMAL,24,2,1,0,-1);

		add("boo",false,T_NORMAL,36,2,1,0,-1);*/
	}
	
	public static void add(String name, boolean playable, byte modelType, double height, double hp, double attack, double defense, double spike) {
		chrctrMap.put(name, new Chrctr(name, playable, modelType, height, hp, attack, defense, spike));
	}

	public static Chrctr getCharacter(String characterType) {
		return chrctrMap.get(characterType);
	}
}
