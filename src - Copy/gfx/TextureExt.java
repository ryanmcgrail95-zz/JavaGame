package gfx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.jhlabs.image.*;
import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import fl.FileExt;
import func.Math2D;

public class TextureExt {
	public final static byte E_NONE = -1, E_GRAYSCALE = 0, E_INVERT = 1, E_TWIRL = 2, E_CHROME = 3, E_CRYSTALLIZE = 4, E_POINTILLIZE = 5, E_BLUR = 6, E_OIL = 7, E_PIXELIZE = 8, E_RAINBOW = 9, E_UNDERWATER = 10, E_DIFFUSE = 11, E_SHIVER = 12, E_EDGE = 13, E_DISSOLVE = 14;
	
	private List<BufferedImage> imgList = new ArrayList<BufferedImage>();
	private List<Texture> frameList = new ArrayList<Texture>();
	private int imageNumber = 0;
	
	private static BufferedImage sprGlow; {
		try {
			sprGlow = ImageIO.read(FileExt.get("Resources/Images/glow.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TextureExt(Texture texture, BufferedImage img) {
		//PROBLEM
		addFrame(texture, img);
	}
	
	public TextureExt(List<Texture> textures, List<BufferedImage> imgs) {
		
		//PROBLEM?
		frameList = textures;
		imgList = imgs;
		imageNumber = frameList.size();
	}
	
	public int getImageNumber() {
		return imageNumber;
	}
	
	public void addFrame(Texture frame, BufferedImage img) {
		frameList.add(frame);
		imgList.add(img);
		imageNumber++;
	}
	
	public Texture getFrame(int imageIndex) {
		return frameList.get(imageIndex % imageNumber);
	}
	
	public Texture getFrame(double imageIndex) {
		return getFrame((int) Math.floor(imageIndex));
	}
	
	public BufferedImage getImage(int imageIndex) {
		return imgList.get(imageIndex % imageNumber);
	}
	
	public BufferedImage getImage(double imageIndex) {
		return getImage((int) Math.floor(imageIndex));
	}
	
	public Texture getFrameEffect(double imageIndex, byte effect) {		
		return GOGL.createTexture(applyImageEffect(getImage(imageIndex), effect), false);
	}
	
	public BufferedImage applyImageEffect(BufferedImage curImg, byte effect) {
		BufferedImage outImg;
		int w = curImg.getWidth(), h = curImg.getHeight();

		outImg = new BufferedImage(w,h,curImg.getType());
		Graphics2D g = (Graphics2D) outImg.getGraphics();
		float time = TextureController.getTime();

		
		switch(effect) {
			case E_GRAYSCALE:
				GrayscaleFilter gs = new GrayscaleFilter();
				gs.filter(curImg, outImg);
				break;
				
			case E_INVERT:
				InvertFilter i = new InvertFilter();
				i.filter(curImg, outImg);
				break;
				
			case E_TWIRL:
				TwirlFilter t = new TwirlFilter();
				t.setAngle((float) (1080/180*3.14159));
				t.filter(curImg, outImg);
				break;
				
			case E_CHROME:
				ChromeFilter c = new ChromeFilter();
				c.filter(curImg, outImg);
				break;
				
			case E_CRYSTALLIZE:
				CrystallizeFilter cry = new CrystallizeFilter();
				cry.setGridType(CrystallizeFilter.RANDOM);
				cry.setFadeEdges(true);
				cry.setScale(8);
				cry.filter(curImg, outImg);
				break;
				
			case E_POINTILLIZE:
				PointillizeFilter poi = new PointillizeFilter();
				poi.setScale(4f);
				poi.setFuzziness(.8f);
				poi.setEdgeColor(new Color(0f,0f,0f,0f).getRGB());
				//poi.setFadeEdges(true);
				poi.setGridType(poi.RANDOM);
				poi.filter(curImg, outImg);
				break;
				
			case E_BLUR:
				BlurFilter bl = new BlurFilter();
				bl.filter(curImg, outImg);
				break;
				
			case E_OIL:
				OilFilter oil = new OilFilter();
				oil.setLevels(8);
				oil.setRange(1);
				oil.filter(curImg, outImg);
				break;
				
			case E_PIXELIZE:
				BlockFilter pix = new BlockFilter();
				pix.setBlockSize(4);
				pix.filter(curImg, outImg);
				break;
				
			case E_RAINBOW:		
				float dir = TextureController.getTime()*3.6f;
				
				g.drawImage(curImg, 0,0, null);

			    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 1f));
			    g.drawImage(sprGlow,(int)(-w/2+Math2D.calcLenX(w/2,dir)),(int)(-h/2+Math2D.calcLenY(h/2,dir)), 2*w, 2*h, null);

			    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
			    g.drawImage(curImg, 0,0, null);
			    
				break;
				
			case E_UNDERWATER:
				BufferedImage waterImg = new BufferedImage(w,h, curImg.getType());
								
				CausticsFilter cau = new CausticsFilter();
				cau.setTime(time/100);
				cau.setBgColor((new Color(.7f,.9f,1f,0f)).getRGB());
				cau.setSamples(1);
				cau.setAmount(1f);
				//cau.setDispersion(0f);
				cau.setScale(10);
				cau.filter(waterImg, waterImg);
				
				TextureController.eraseBackground(waterImg);
				
				BlurFilter blCau = new BlurFilter();
				blCau.filter(waterImg, waterImg);
		
				g.drawImage(curImg, 0,0, null);
		
			    //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			    //g.drawImage(sprGlow,(int)(-w/2+Math2D.lengthdirX(w/2,dir)),(int)(-w/2+Math2D.lengthdirY(w/2,dir)), 2*w, 2*w, null);
		
			    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .8f));
			    g.drawImage(waterImg, 0,0, null);
			    
			    g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN, 1f));
			    g.drawImage(curImg, 0,0, null);
			    
			    	
				SwimFilter rp = new SwimFilter();
				rp.setTime(time/10);
				rp.filter(outImg, outImg);
				
			    break;
			    
			case E_DIFFUSE:
				DiffuseFilter d = new DiffuseFilter();
				d.setScale(2);
				d.filter(curImg, outImg);
				break;
			
			case E_SHIVER:
				RippleFilter r = new RippleFilter();
				int sh = 16;
				int y = (int) ((h + (time - h)) % w);
								
				BufferedImage segImg = new BufferedImage(w,sh, curImg.getType());
				Graphics g2 = segImg.getGraphics();
				g2.drawImage(curImg, 0,0,w,sh, 0,y,w,y+sh, null);
				
				r.setYAmplitude(0);
				r.setXAmplitude(1);
				r.setXWavelength(1);
				r.filter(segImg, segImg);
				
				g.drawImage(curImg, 0,0, null);
				g.drawImage(segImg, 0, y, null);
				
				break;
			
			case E_EDGE:
				LaplaceFilter e = new LaplaceFilter();
				e.filter(curImg, outImg);
				TextureController.eraseBackground(outImg);
				break;
				
			case E_DISSOLVE:
				BufferedImage noiseImg = new BufferedImage(w,h,curImg.getType());
								
				
				PointillizeFilter noi = new PointillizeFilter();
				noi.setScale(1);
				noi.setGridType(noi.RANDOM);
				Graphics noiG = noiseImg.getGraphics();
				noi.filter(noiseImg, noiseImg);
				
				//TextureController.eraseBackground(noiseImg);
				
				DiffuseFilter dN = new DiffuseFilter();
				dN.filter(noiseImg, noiseImg);

				
				g.drawImage(curImg, 0,0, null);
			    
			    g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN, 1f));
			    g.drawImage(noiseImg, 0,0, null);
			    
			    
				break;
				
			default:
				outImg = curImg;
		}

		return outImg;
	}

	public RGBA getPixelColor(int x, int y) {
		return getPixelColor(0,x,y);
	}
	public RGBA getPixelColor(int index, int x, int y) {
		return new RGBA(imgList.get(index).getRGB(x, y));
	}

	public int getWidth() {
		return getWidth(0);
	}
	public int getWidth(int index) {
		BufferedImage img = imgList.get(index);
		
		if(img == null)
			return 0;
		else
			return img.getWidth();
	}
	
	public int getHeight() {
		return getHeight(0);
	}
	public int getHeight(int index) {
		BufferedImage img = imgList.get(index);
		
		if(img == null)
			return 0;
		else
			return img.getHeight();
	}

}
