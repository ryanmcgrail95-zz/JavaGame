package resource.font;

import gfx.TextureExt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.util.texture.Texture;

import cont.GameController;
import cont.TextureController;

public class SplitFont extends Font {
	private String fontDir;
	private Map<Character, TextureExt> fontMap;
	private int width, height;
	
	
	// CONSTRUCTOR
		public SplitFont(String name, float hangFrac) {
			super(name, T_SPLIT, hangFrac);
			fontDir = "Resources/Fonts/" + name + "/";
			loadCharacters();
			
			width = getCharWidth('A');
			height = getCharHeight('A');
			
			map.put(name,this);
		}
	
	
	// INITIALIZATION METHODS
		private void loadCharacters() {
			fontMap = new HashMap<Character, TextureExt>();
			
			// Load Letters
			for(int i = 0; i < 26; i++) {
				addChar('A'+i);
				addChar('a'+i);
			}
			
			// Load Digits
			for(int i = 0; i < 10; i++)
				addChar('0'+i);
			
			// Load Symbols
			addChar('.');
			addChar('!');
			addChar('\'');
			addChar('?');
			addChar(';');
			addChar(':');
			addChar('"');
			addChar('-');
			addChar('+');
			addChar('<');
			addChar('>');
			addChar('(');
			addChar(')');
			addChar(',');
			addChar('/');
		}
		
		// Adding Characters
			private void addChar(char c) {
				addChar(c, fontDir+((byte) c)+".png");
			}
			private void addChar(int i) {
				addChar((char) i);
			}
			private void addChar(char c, String fileName) {
				TextureExt cTex;

				cTex = TextureController.load(fileName, fileName, TextureController.M_BGALPHA);
				fontMap.put(c, cTex);
			}


	// PUBLIC
		public TextureExt getChar(char c) {
			if(fontMap.get(c) == null) {
				System.out.println("Failed to load character "+c+".");
				System.exit(2);
			}

			return fontMap.get(c);
		}
		public int getCharWidth(char c) 	{return getChar(c).getWidth();}
		public int getCharHeight(char c)	{return getChar(c).getHeight();}
		public int getWidth() 				{return width;}
		public int getHeight() 				{return height;}


		public float[] getBounds(int frame) {return null;}
		public Texture getTexture() 		{return null;}
}
