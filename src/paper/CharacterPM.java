package paper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import resource.Resource;
import ds.StringExt2;
import ds.lst.WeightedRandomList;
import fl.FileExt;
import gfx.GL;

public class CharacterPM extends Resource implements Elemental {
	private final static String BASE_DIRECTORY = "Resources/Images/Characters/";
	private static Map<String, CharacterPM> cMap = new HashMap<String, CharacterPM>();
	
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
		hasWings,
		hasShoe;

	private WeightedRandomList<Attack> attackList;
	
	private String name, directory;
	//private byte element;
	private SpriteMap spriteMap;
	
	private CharacterPM(String name, boolean isTemporary) {
		super(name, Resource.R_CHARACTER, isTemporary);
		this.name = name;
		
		this.attackList = new WeightedRandomList<Attack>("Character");
		
        cMap.put(name, this);
	}
	
	private void loadStats() {
		String name = "NULL";
		int power = 1;
		byte element = EL_NONE,
			type = Attack.AT_JUMP;
		float weight = 1;
		
		String fileName = directory + "stats.dat";
		StringExt2 fileText = new StringExt2(FileExt.readFile2String(fileName));
			
		String line;
		StringExt2 chomper = new StringExt2();
		
		boolean inAttack = false;
		
		while(!fileText.isEmpty()) {
			chomper.set(line = fileText.munchLine());

			String var, value;
			var = chomper.munchWord();
			value = chomper.munchLine();

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
				case "hasShoe":		hasShoe = Boolean.parseBoolean(value);	break;

				case "attackStat":	power = Integer.parseInt(value);	break;		
				case "defenseStat":	defense = Integer.parseInt(value);	break;
			}
		}
	}
	
	public static CharacterPM getCharacter(String name, boolean isTemporary) {
		CharacterPM retC;
		if(cMap.containsKey(name))
			retC = cMap.get(name);
		else
			retC = new CharacterPM(name, isTemporary);
		
		return retC;
	}
	
	public Attack getRandomAttack() {
		return attackList.get();
	}

	public SpriteMap getSpriteMap() {return spriteMap;}
	public int getDefense() 		{return defense;}
	public float getSpriteWidth() 	{return width;}
	public float getSpriteHeight()	{return height;}
	public float getHeight()		{return height*heightFraction;}

	public boolean getHasWings() 	{return hasWings;}
	public boolean getHasSpike() 	{return hasSpike;}
	public boolean getHasShoe() 	{return hasShoe;}

	public int getMaxHP() {
		return maxHP;
	}

	public float getImageSpeed() {
		return imageSpeed;
	}

	public static int getNumber() {
		return cMap.size();
	}

	
	@Override
	public void load(String fileName) {
		GL.println("LOADING CHARACTER " + fileName + "***************************");
		
		this.directory = BASE_DIRECTORY + fileName + "/";
		this.spriteMap = SpriteMap.getSpriteMap(fileName);
		
		if(spriteMap == null)
			throw new UnsupportedOperationException("Spritemap is null.");
		
		loadStats();		
	}

	@Override
	public void unload() {
		super.unload();
		
		cMap.remove(name);
		
		spriteMap.destroy();
		attackList.destroy();

		spriteMap = null;
		attackList = null;
	}
}
