package cont;

import fl.FileExt;
import gfx.ErrorPopup;
import gfx.GL;
import gfx.TextureExt;
import image.filter.BGEraserFilter;
import image.filter.GrayscaleAlphaFilter;
import resource.image.Img;
import time.Stopwatch;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import ds.lst.CleanList;


public class TextureController implements Img {
	private static CleanList<Texture> texList = new CleanList<Texture>("Tex");
	private static CleanList<TextureExt> texExtList = new CleanList<TextureExt>("TexExt");
	private static Map<String, TextureExt> texMap = new HashMap<String, TextureExt>();
	private static float time = 0;
		

	/*public static start(Stopwatch s, String e) {
		System.out.println(e);
		s.start();
	}
	
	public static stop(Stopwatch s) {
		s.stop();
	}*/
	
	
	public static float getTime() {
		return time;
	}
	

	public static TextureExt loadExt(String fileName) { 
	    return loadExt(fileName, AlphaType.NORMAL);
	}
	public static TextureExt loadExt(String fileName, AlphaType method) { 
	    return new TextureExt(fileName, method);
	}
	
	
	public static TextureExt load(String fileName, String name, AlphaType method) {
	    TextureExt texExt = loadExt(fileName, method);

	    texMap.put(name, texExt);
	    
	    return texExt;
	}
	
	
	public static TextureExt getTextureExt(String name) {return texMap.get(name);}
	public static Texture getTexture(String name) 		{return texMap.get(name).getTexture();}

	public static void ini() {
		load("Resources/Images/Shadows/shadow.png", "texShadow", TextureController.AlphaType.NORMAL);
	
		load("Resources/Images/wall.png", "texBricks", TextureController.AlphaType.NORMAL);
        load("Resources/Images/Shadows/blockShadow.png", "texBlockShadow", AlphaType.NORMAL);

        load("Resources/Images/fireMask.png", "fireMask", AlphaType.GRAYSCALE_MASK);
        load("Resources/Images/fireAni.png", "fireAni", AlphaType.GRAYSCALE_MASK);
        
        load("Resources/Images/Items/coin.gif", "texCoin", AlphaType.BG_ALPHA);
        
        load("Resources/Images/bg.png", "texPicross", AlphaType.NORMAL);
        load("Resources/Images/loop.png", "loop", AlphaType.NORMAL);
        
        load("Resources/Images/Battle/dodgeStar.png", "texDodgeStar", AlphaType.BG_ALPHA);
        
        //Load Particles
        load("Resources/Images/Particles/dust.gif", "texDust", AlphaType.BG_ALPHA);
        
        load("Resources/Images/gameboy.png", "texGameboy", AlphaType.NORMAL);
        load("Resources/Images/iphone.png", "iphone", AlphaType.NORMAL);

        //BattleStar Images
        load("Resources/Images/Battle/star.png", "texDamageStar", AlphaType.BG_ALPHA);
        //load("Resources/Images/Battle/damageStar1.gif", "texDamageStar1", M_BGALPHA);
        
        load("Resources/Images/Backgrounds/mountains.png", "bacMountains", AlphaType.NORMAL);
	}

	public static int getNumber() {
		return texList.size();
	}

	public static int getNumberExt() {
		return texExtList.size();
	}

	public static void add(Texture tex) {texList.add(tex);}
	public static void add(TextureExt tex) {texExtList.add(tex);}
	public static void remove(Texture tex) {texList.remove(tex);}
	public static void remove(TextureExt tex) {
		texExtList.remove(tex);
	}

	public static void destroy(Texture t) {
		remove(t);
		t.destroy(GL.getGL());
	}
}