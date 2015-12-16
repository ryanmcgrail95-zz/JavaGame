package paper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import resource.Resource;
import datatypes.StringExt;
import datatypes.lists.WeightedRandomList;
import fl.FileExt;

public class CharacterPM extends Resource implements Elemental {
	private final static String BASE_DIRECTORY = "Resources/Images/Characters/";
	private static Map<String, CharacterPM> map = new HashMap<String, CharacterPM>();
	private int numReferences;
	
	private int
		maxHP = 2,
		defense;
	
	private float 
		width, 
		height,
		heightFraction,
		imageSpeed = AnimationsPM.IMAGE_SPEED;
	
	private boolean 
		hasSpike,
		hasWings;

	private WeightedRandomList<Attack> attackList = new WeightedRandomList<Attack>("Character");
	
	private String name, directory;
	//private byte element;
	private SpriteMap spriteMap;
	
	public CharacterPM(String name) {
		super(name, Resource.R_CHARACTER);
		this.name = name;
	}
	
	private void loadStats() {
		String name = "NULL";
		int power = 1;
		byte element = EL_NONE,
			type = Attack.AT_JUMP;
		float weight = 1;
		
		String fileName = directory + "stats.dat";
		File f = FileExt.getFile(fileName);
		
		if(f == null)
			return;
		if(!f.exists())
			return;
		
		try {
			BufferedReader buff = new BufferedReader(new FileReader(f));
			
			String line;
			StringExt chomper = new StringExt();
			
			boolean inAttack = false;
			
			while((line = buff.readLine()) != null) {
				chomper.set(line);
				
				String var, value;
				var = chomper.chompWord();
				value = chomper.chompWord();
				
				var = var.replace(":", "");
				
								
				byte bVal;
				switch(var) {
					case "attack":
						inAttack = true; 
						power = 1;
						element = EL_NONE;
						type = Attack.AT_JUMP;
						weight = 1;
						name = "NULL";	
						break;
					case "}":
						if(inAttack) {
							attackList.add(new Attack(name, power, type, element), weight);
							inAttack = false;
						}
						break;
						
					case "maxHP":
						maxHP = Integer.parseInt(value);
						break;
				
					case "element":
						switch(value) {
							case "none": bVal = EL_NONE;		break;
							case "fire": bVal = EL_FIRE;		break;
							case "ice": bVal = EL_ICE;			break;
							case "icyfire": bVal = EL_ICYFIRE;	break;
							default: bVal = EL_NONE;
						}
						element = bVal;
						break;
				
					case "attackType":
						switch(value) {
							case "jump": bVal = Attack.AT_JUMP;		break;
							case "hammer": bVal = Attack.AT_HAMMER;	break;
							default: bVal = Attack.AT_JUMP;
						}
						type = bVal;
						break;

					case "name":	name = value;	break;

					case "imageSpeed":	imageSpeed = Float.parseFloat(value);	break;

					case "width":	width = Float.parseFloat(value);	break;
					case "height":	height = Float.parseFloat(value);	break;
					case "heightFraction":	heightFraction = Float.parseFloat(value);	break;

					case "hasSpike":	hasSpike = Boolean.parseBoolean(value);	break;
					case "hasWings":	hasWings = Boolean.parseBoolean(value);	break;

					case "attackStat":	power = Integer.parseInt(value);	break;		
					case "defenseStat":	defense = Integer.parseInt(value);	break;
				}
			}
			
			buff.close();
		} catch (IOException e) {
		}
	}
	
	public static CharacterPM getCharacter(String name) {
		CharacterPM retC;
		if(map.containsKey(name))
			retC = map.get(name);
		else
			retC = new CharacterPM(name);
		
		retC.numReferences++;
		
		return retC;
	}
	
	public Attack getRandomAttack() {
		return attackList.get();
	}

	public SpriteMap getSpriteMap() {return spriteMap;}
	public int getDefense() {return defense;}
	public float getSpriteWidth() 	{return width;}
	public float getSpriteHeight()	{return height;}
	public float getHeight()		{return height*heightFraction;}

	public boolean getHasWings() {return hasWings;}
	public boolean getHasSpike() {return hasSpike;}

	public int getMaxHP() {
		return maxHP;
	}

	public float getImageSpeed() {
		return imageSpeed;
	}

	public static int getNumber() {
		return map.size();
	}

	
	@Override
	public void load(String fileName) {
		this.directory = BASE_DIRECTORY + fileName + "/";
		
		System.out.println(directory);
		
		this.spriteMap = SpriteMap.getSpriteMap(fileName);
		
		loadStats();
		
        map.put(fileName, this);
	}

	@Override
	public void unload() {
		map.remove(name);
		spriteMap.destroy();
		attackList.clear();
	}
}
