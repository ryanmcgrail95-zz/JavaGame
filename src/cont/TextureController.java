package cont;

import fl.FileExt;
import gfx.ErrorPopup;
import gfx.GOGL;
import gfx.TextureExt;
import image.filter.BGEraserFilter;
import image.filter.GrayscaleAlphaFilter;

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
import javax.imageio.stream.ImageInputStream;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;


public class TextureController {
	public final static byte M_NORMAL = 0, M_BGALPHA = 1, M_MASK = 2;
	private static Map<String, TextureExt> texMap = new HashMap<String, TextureExt>();
	private static float time = 0;
	
	public static float getTime() {
		return time;
	}
	
	public static TextureExt loadExt(String fileName, byte method) {
	    TextureExt texExt;
	    	    	    
	    try {
		    if(fileName.endsWith(".gif"))
		    	texExt = loadMulti(fileName, method);
		    else
		    	texExt = loadSingle(fileName, method); 
	        	
	        return texExt;
	    } catch(IOException e) {
	    	ErrorPopup.open("Failed to load texture: " + fileName + ".", true);
	    }
	    
	    return null;
	}
	
	
	public static TextureExt load(String fileName, String name, byte method) {
	    TextureExt texExt = loadExt(fileName, method);

	    texMap.put(name, texExt);
	    
	    return texExt;
	}
	
	
	private static TextureExt loadSingle(String fileName, byte method) throws IOException {

		//Load Image
		BufferedImage img = ImageLoader.load(fileName);
		img = addAlpha(img);
		
		if(method == M_BGALPHA)
			(new BGEraserFilter()).filter(img,img);
		
		TextureExt texExt = new TextureExt(img);
		
        return texExt;
	}
	
	private static TextureExt loadMulti(String fileName, byte method) throws IOException {
		List<BufferedImage> frames;
		Texture texture;
		
		InputStream str;
		if((str = FileExt.get(fileName)) == null)
			return null;
		
		frames = getFrames(str);
		BufferedImage img;
		
		if(method == M_BGALPHA) {
			for(int i = 0; i < frames.size(); i++) {
				img = addAlpha(frames.get(i));
				(new BGEraserFilter()).filter(img,img);
				frames.set(i,img);
			}
		}
		else if(method == M_MASK) {
			for(BufferedImage i : frames)
				(new GrayscaleAlphaFilter()).filter(i,i);
		}
		
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

        load("Resources/Images/fireMask.png", "fireMask", M_MASK);
        load("Resources/Images/fireAni.png", "fireAni", M_MASK);
        
        load("Resources/Images/Items/coin.gif", "texCoin", M_BGALPHA);
        
        load("Resources/Images/bg.png", "texPicross", M_NORMAL);
        load("Resources/Images/loop.png", "loop", M_NORMAL);
        
        //Load Particles
        load("Resources/Images/Particles/dust.gif", "texDust", M_BGALPHA);
        
        load("Resources/Images/gameboy.png", "texGameboy", M_NORMAL);
        load("Resources/Images/iphone.png", "iphone", M_NORMAL);

        //BattleStar Images
        load("Resources/Images/Battle/star.png", "texDamageStar", M_BGALPHA);
        load("Resources/Images/Battle/damageStar1.gif", "texDamageStar1", M_BGALPHA);
        
        load("Resources/Images/Backgrounds/mountains.png", "bacMountains", M_NORMAL);
	}
}