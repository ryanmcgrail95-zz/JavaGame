package cont;

import fl.FileExt;
import gfx.GOGL;
import gfx.TextureExt;

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
	
	
	public static void update(float deltaT) {
		time += 1*deltaT;
		
		if(time > 360)
			time -= 360;
	}
	
	public static float getTime() {
		return time;
	}
	
		
	public static TextureExt loadTexture(String fileName, String name, byte method) {
	    TextureExt texExt;
	    
	    
	    System.out.println(fileName);
	    
	    if(fileName.endsWith(".gif"))
	    	texExt = loadMultiTexture(fileName, method);
	    else
	    	texExt = loadSingleTexture(fileName);
	    	        
        //Add Texture to Map
        	texMap.put(name, texExt);
        	
        return texExt;
	}
	
	
	private static TextureExt loadSingleTexture(String fileName) {
		//Load Image
		//TextureLoader loader = new TextureLoader(fileName, null);
        //ImageComponent2D image = loader.getImage();     
		
		//File f = new File("bin/"+fileName);
		//System.out.println(f.getAbsolutePath());
		
		BufferedImage img = ImageController.loadImage(fileName);
		
		//ImageComponent2D image = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA, img);
	
	    //Get Texture from Image
	        //Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
	        //texture.setImage(0, image);
	        	        		
	        TextureExt texExt = new TextureExt(GOGL.createTexture(img, false), img);//texture);
	        
        return texExt;
	}
	
	private static TextureExt loadMultiTexture(String fileName, byte method) {
		List<BufferedImage> frames;
		List<Texture> texs = new ArrayList<Texture>();
		Texture texture;
		
		try {
			frames = getFrames(FileExt.get(fileName));
			
			
			
			for(BufferedImage i : frames) {
				if(method == M_BGALPHA)
					eraseBackground(i);
								
				//image = new ImageComponent2D(ImageComponent2D.FORMAT_RGBA8, i);
								
			    //Get Texture from Image
					texture = GOGL.createTexture(i, false);
					//texture = new Texture2D(Texture2D.BASE_LEVEL, Texture2D.RGBA, image.getWidth(), image.getHeight());
			        //texture.setImage(0, image);
			        
		        texs.add(texture);
			}
			
			return new TextureExt(texs, frames);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public static ArrayList<BufferedImage> getFrames(InputStream gif) throws IOException {
	    ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
	    ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
	    ir.setInput(ImageIO.createImageInputStream(gif));
	    
	    for(int i = 0; i < ir.getNumImages(true); i++)
	        frames.add(addAlpha(ir.read(i)));
	    
	    return frames;
	}
	
	public static void eraseBackground(BufferedImage img) {
		Graphics g;
		int bg, erase;
		int w, h;
						
		g = img.getGraphics();
		w = img.getWidth();
		h = img.getHeight();
		
		bg = img.getRGB(0, h-1);
		erase = (new Color(0f, 0f, 0f, 0f)).getRGB();
		
		for(int x = 0; x < w; x++)
			for(int y = 0; y < h; y++)
				if(img.getRGB(x, y) == bg) {
					img.setRGB(x, y, erase);
				}
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
	
	public static TextureExt getTextureExt(String name) {
		return texMap.get(name);
	}
	
	public static Texture getTexture(String name) {
		return texMap.get(name).getFrame(0);
	}

	public static void ini() {
        TextureController.loadTexture("Resources/Images/Shadows/shadow.png", "texShadow", TextureController.M_NORMAL);

		TextureController.loadTexture("Resources/Images/wall.png", "texBricks", TextureController.M_NORMAL);
        TextureController.loadTexture("Resources/Images/Shadows/blockShadow.png", "texBlockShadow", TextureController.M_NORMAL);
        
        TextureController.loadTexture("Resources/Images/Items/coin.gif", "texCoin", TextureController.M_BGALPHA);
        
        //Load Particles
        TextureController.loadTexture("Resources/Images/Particles/dust.gif", "texDust", TextureController.M_BGALPHA);
        

        //BattleStar Images
        TextureController.loadTexture("Resources/Images/Battle/damageStar.gif", "texDamageStar", TextureController.M_BGALPHA);
        TextureController.loadTexture("Resources/Images/Battle/damageStar1.gif", "texDamageStar1", TextureController.M_BGALPHA);
	}
}