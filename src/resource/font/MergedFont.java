package resource.font;

import gfx.MultiTexture;
import gfx.TextureExt;

import java.util.Map;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;

public class MergedFont extends Font {
	private MultiTexture tex;
	private int width, height;
	
	public MergedFont(String name, float hangFrac) {
		super(name, T_MERGED, hangFrac);
		tex = new MultiTexture("Resources/Fonts/"+name+".png",16,16);
		width = 8;
		height = 8;
		
		map.put(name,this);
	}

	public TextureExt getChar(char c) 	{return null;}


	public int getCharWidth(char c) 	{return width;}
	public int getCharHeight(char c) 	{return height;}
	public int getWidth() 				{return width;}
	public int getHeight()				{return height;}

	public float[] getBounds(int c) 	{return tex.getBounds(c);}
	public Texture getTexture() 		{return tex.getTexture();}
}
