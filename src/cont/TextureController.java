package cont;

import fl.FileExt;
import gfx.ErrorPopup;
import gfx.GOGL;
import gfx.TextureExt;
import image.filter.BGEraserFilter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;


public class TextureController {
	public final static byte M_NORMAL = 0, M_BGALPHA = 1;
	private static Map<String, TextureExt> texMap = new HashMap<String, TextureExt>();
	private static float time = 0;
	
	public static float getTime() {
		return time;
	}
	
		
	public static TextureExt load(String fileName, String name, byte method) {
	    TextureExt texExt;
	    
	    	    
	    try {
		    if(fileName.endsWith(".gif"))
		    	texExt = loadMulti(fileName, method);
		    else
		    	texExt = loadSingle(fileName); 
	        //Add Texture to Map
	        	texMap.put(name, texExt);
	        	
	        return texExt;
	    } catch(IOException e) {
	    	ErrorPopup.open("Failed to load texture: " + name + ".", true);
	    }
	    
	    return null;
	}
	
	
	private static TextureExt loadSingle(String fileName) throws IOException {

		//Load Image
		TextureExt texExt = new TextureExt(ImageLoader.load(fileName));
	        
        return texExt;
	}
	
	private static TextureExt loadMulti(String fileName, byte method) throws IOException {
		List<BufferedImage> frames;
		Texture texture;
		
		
		frames = getFrames(FileExt.get(fileName));		

		if(method == M_BGALPHA)
			for(BufferedImage i : frames)
				(new BGEraserFilter()).filter(i,i);
		
		return new TextureExt(frames);
	}
	
	
	
	public static ArrayList<BufferedImage> getFrames(InputStream gif) throws IOException {
	    ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
	    ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
	    ir.setInput(ImageIO.createImageInputStream(gif));
	    
	    for(int i = 0; i < ir.getNumImages(true); i++)
	        frames.add(addAlpha(ir.read(i)));
	    
	    return frames;
	}
	
	
	public static BufferedImage addAlpha(BufferedImage bi) {
	     int w = bi.getWidth();
	     int h = bi.getHeight();
	     int type = BufferedImage.TYPE_INT_ARGB;
	     BufferedImage bia = new BufferedImage(w,h,type);
	     Graphics2D g = bia.createGraphics();
	     g.drawImage(bi, 0, 0, null);
	     g.dispose();
	     return bia;
	}
	
	public static TextureExt getTextureExt(String name) {return texMap.get(name);}
	public static Texture getTexture(String name) 		{return texMap.get(name).getFrame(0);}

	public static void ini() {
		load("Resources/Images/Shadows/shadow.png", "texShadow", TextureController.M_NORMAL);
	
		load("Resources/Images/wall.png", "texBricks", TextureController.M_NORMAL);
        load("Resources/Images/Shadows/blockShadow.png", "texBlockShadow", M_NORMAL);
        
        load("Resources/Images/Items/coin.gif", "texCoin", M_BGALPHA);
        
        load("Resources/Images/bg.png", "texPicross", M_NORMAL);
        load("Resources/Images/loop.png", "loop", M_NORMAL);
        
        //Load Particles
        load("Resources/Images/Particles/dust.gif", "texDust", M_BGALPHA);
        
        load("Resources/Images/gameboy.png", "texGameboy", M_NORMAL);
        load("Resources/Images/iphone.png", "iphone", M_NORMAL);

        //BattleStar Images
        load("Resources/Images/Battle/damageStar.gif", "texDamageStar", M_BGALPHA);
        load("Resources/Images/Battle/damageStar1.gif", "texDamageStar1", M_BGALPHA);
	}
}