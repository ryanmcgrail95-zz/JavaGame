package paper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import datatypes.StringExt;
import fl.FileExt;

public class CharacterPM {
	private final static String DIRECTORY = "Resources/Images/Characters/";
	private static Map<String, CharacterPM> map = new HashMap<String, CharacterPM>();
	public final static byte 
		EL_NONE = 0, 
		EL_FIRE = 1,
		EL_ICE = 2,
		EL_ICYFIRE = 3;
	public final static byte
		AT_JUMP = 0,
		AT_HAMMER = 1;
	
	private int 
		attack, 
		defense;
	
	private float 
		width, 
		height,
		heightFraction;
	
	private byte attackType;

	private boolean hasWings;
	
	private String name, directory;
	private byte element;
	private SpriteMap spriteMap;
	
	private CharacterPM(String name) {
		this.name = name;
		this.directory = DIRECTORY + name + "/";
		this.spriteMap = SpriteMap.getSpriteMap(name);
				
		loadStats();
		
        map.put(name, this);
	}
	
	private void loadStats() {
		element = EL_NONE;
		
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
			while((line = buff.readLine()) != null) {
				chomper.set(line);
				
				String var, value;
				var = chomper.chompWord();
				value = chomper.chompWord();
				
				var = var.replace(":", "");
								
				byte bVal;
				switch(var) {
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
							case "jump": bVal = AT_JUMP;		break;
							case "hammer": bVal = AT_HAMMER;	break;
							default: bVal = AT_JUMP;
						}
						attackType = bVal;
						break;

						
					case "width":	width = Float.parseFloat(value);	break;
					case "height":	height = Float.parseFloat(value);	break;
					case "heightFraction":	heightFraction = Float.parseFloat(value);	break;

					case "hasWings":	hasWings = Boolean.parseBoolean(value);	break;

					case "attackStat":	attack = Integer.parseInt(value);	break;		
					case "defenseStat":	defense = Integer.parseInt(value);	break;
				}
			}
			
			buff.close();
		} catch (IOException e) {
		}
	}
	
	public static CharacterPM getCharacter(String name) {
		if(map.containsKey(name))
			return map.get(name);
		else
			return new CharacterPM(name);
	}
	
	public byte getElement() {return element;}
	public SpriteMap getSpriteMap() {return spriteMap;}
	public byte getAttackType() {return attackType;}
	public int getAttack() 	{return attack;}
	public int getDefense() {return defense;}
	public float getSpriteWidth() 	{return width;}
	public float getSpriteHeight()	{return height;}
	public float getHeight()		{return height*heightFraction;}

	public boolean getHasWings() {
		return hasWings;
	}
}
