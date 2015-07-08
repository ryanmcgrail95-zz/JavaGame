package fnt;

import gfx.TextureExt;

import java.util.HashMap;
import java.util.Map;

import cont.TextureController;

public class Font {
	private String fontDir;
	private boolean isValid;
	private Map<Character, TextureExt> fontMap;
	private float width = 0, height = 0;
	
	public Font(String name) {
		fontDir = "Resources/Fonts/" + name + "/";
		isValid = initialize();
	}
	
	
	private boolean initialize() {
		fontMap = new HashMap<Character, TextureExt>();
		
		for(int i = 0; i < 26; i++) {
			addChar('A'+i);
			addChar('a'+i);
		}
		
		for(int i = 0; i < 10; i++)
			addChar('0'+i);
		
		
		addChar('.');
		addChar('!');
		addChar(';');
		addChar(':');
		addChar('"');
		addChar('-');
		addChar('+');
		addChar('(');
		addChar(')');
		addChar(',');
		addChar('/');
		
		width = getChar('A').getWidth();
		height = getChar('A').getHeight();
		
		return true;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	private void addChar(char c) {
		addChar(c, fontDir+((byte) c)+".png");
	}
	private void addChar(int i) {
		addChar((char) i);
	}
	
	private void addChar(char c, String fileName) {
		TextureExt cTex = TextureController.loadTexture(fileName, fileName, TextureController.M_BGALPHA);
		
		fontMap.put(c, cTex);
	}

	public TextureExt getChar(char c) {
		if(fontMap.get(c) == null) {
			System.out.println("Failed to get "+c+".");
			System.exit(2);
		}

		return fontMap.get(c);
	}
}
