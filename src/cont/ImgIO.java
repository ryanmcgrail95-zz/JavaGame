package cont;

import image.filter.AlphaCompositorFilter;
import image.filter.AlphaHistogramFilter;
import image.filter.AlphaRipperFilter;
import image.filter.BGEraserFilter;
import image.filter.GrayscaleAlphaFilter;
import image.filter.xBRZ;
import image.filter.xBRZ.ScaleSize;
import resource.image.Img;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

import fl.FileExt;
import functions.MathExt;
import gfx.Sprite;
import hqx.Hqx_2x;
import hqx.Hqx_3x;
import hqx.Hqx_4x;
import hqx.RgbYuv;

public final class ImgIO implements Img {
	
	private static FilterType filter = FilterType.NONE; //FilterType.HQX_4X;

	private static BGEraserFilter 
		bgEraser = new BGEraserFilter();
	private static GrayscaleAlphaFilter 
		gaFilter = new GrayscaleAlphaFilter();
	private static AlphaRipperFilter 
		arFilter = new AlphaRipperFilter();
	private static AlphaCompositorFilter
		acFilter = new AlphaCompositorFilter();
	
	private ImgIO() {}
	
	public static BufferedImage load(String fileName) throws IOException {
		return load(fileName, (byte) MathExt.indexEnum(AlphaType.NORMAL, alphaTypeList))[0];
	}
	
	private static BufferedImage addAlpha(BufferedImage img) {
		if(img.getType() != BufferedImage.TYPE_INT_ARGB && img.getType() != BufferedImage.TYPE_INT_ARGB_PRE) {
	        final BufferedImage temp = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
	        Graphics g = temp.getGraphics();
	        	g.drawImage(img, 0, 0, null);
	        	g.dispose();
	        img.flush();
	        return temp;
	    }
		else
			return img;
	}
	
	
	private static BufferedImage[] loadNonGIF(InputStream nongif) throws IOException {
		return new BufferedImage[] {addAlpha(ImageIO.read(nongif))};
	}

	private static BufferedImage[] loadGIF(InputStream gif) throws IOException {

		// Create Streams & Reader
		ImageInputStream imgStr = ImageIO.createImageInputStream(gif);
	    ImageReader ir = new GIFImageReader(new GIFImageReaderSpi());
	    	ir.setInput(imgStr);
	    	
	    // Get # of Frames, Create Array of BufferedImages
	    int frameNum = ir.getNumImages(true);
	    BufferedImage[] frames = new BufferedImage[frameNum];
	    
	    // Add Alpha to Images
	    for(int i = 0; i < frameNum; i++)
	        frames[i] = addAlpha(ir.read(i));
	    
	    // Close Reader & Stream
	    ir.dispose();
	    imgStr.close();
	    
	    return frames;
	}
	
	public static BufferedImage[] load(String fileName, byte method) throws IOException {
		InputStream str;
		if((str = FileExt.get(fileName)) == null)
			return null;
		
		BufferedImage frames[];
						
		if(fileName.endsWith(".gif"))
			frames = loadGIF(str);
		else
			frames = loadNonGIF(str);
		str.close();
			
		
		AlphaType alphaType = alphaTypeList[method];
		
		if(alphaType == AlphaType.BG_ALPHA) {
			for(BufferedImage img : frames)
				bgEraser.filter(img,img);
		}
		else if(alphaType == AlphaType.GRAYSCALE_MASK) {
			for(BufferedImage img : frames)
				gaFilter.filter(img,img);
		}
			
		if(filter == FilterType.NONE)
			return frames;
		
		int filterScale, fltrType = -1;
		if((filterScale = MathExt.indexEnum(filter, FilterType.HQX_2X, FilterType.HQX_3X, FilterType.HQX_4X)) != -1) {
			filterScale += 2;
			fltrType = 0;
		}
		else if((filterScale = MathExt.indexEnum(filter, FilterType.XBRZ_2X, FilterType.XBRZ_3X, FilterType.XBRZ_4X, FilterType.XBRZ_5X)) != -1) {
			filterScale += 2;
			fltrType = 1;
		}
		else
			throw new UnsupportedOperationException("Error, invalid image filter!");
		
		BufferedImage alpha, prepI, prepA;
		for(int i = 0; i < frames.length; i++) {
			alpha = new BufferedImage(frames[i].getWidth(),frames[i].getHeight(),frames[i].getType());
			arFilter.filter(frames[i], alpha);
			
			prepI = prepareImage(frames[i]);
			prepA = prepareImage(alpha);
						
			if(fltrType == 0) {
				prepI = scaleHQX(prepI, filterScale);
				prepA = scaleHQX(prepA, filterScale);
			}
			else {				
				prepI = scaleXBR(prepI, filterScale);
				prepA = scaleXBR(prepA, filterScale);
			}
			
			frames[i] = unprepareImage(prepI,filterScale);
			alpha = unprepareImage(prepA,filterScale);
			
			acFilter.filter(frames[i], alpha);
			alpha.flush();
		}		
		
		return frames;
	}

	private static BufferedImage scaleXBR(BufferedImage i, int scale) {
		xBRZ x = new xBRZ();

		// If Invalid Scale, Throw Error!
		if(scale < 2 || scale > 5)
			throw new UnsupportedOperationException();
				
	    // Obtain pixel data for source image
	    final int[] data = ((DataBufferInt) i.getRaster().getDataBuffer()).getData();

	    // Create the destination image, twice as large for 2x algorithm
	    final BufferedImage biDest = new BufferedImage(i.getWidth() * scale, i.getHeight() * scale, BufferedImage.TYPE_INT_ARGB);
	    // Obtain pixel data for destination image
	    final int[] dataDest2 = ((DataBufferInt) biDest.getRaster().getDataBuffer()).getData();
	    
	    // Get xBR Scaler
	    ScaleSize s = null;
	    switch(scale) {
	    	case 2: s = xBRZ.ScaleSize.Times2; break;
	    	case 3: s = xBRZ.ScaleSize.Times3; break;
	    	case 4: s = xBRZ.ScaleSize.Times4; break;
	    	case 5: s = xBRZ.ScaleSize.Times5; break;
    	}

	    // Scale Image into biDest!
		x.scaleImage(s, data,dataDest2, i.getWidth(),i.getHeight(), new xBRZ.ScalerCfg(), 0,i.getHeight()-1);

		// Destroy Old Image
	    i.flush();
	    
		return biDest;
	}
	
	private static BufferedImage prepareImage(BufferedImage i) {
		if(true) //false)
			return i;
		
		BufferedImage output;
		Graphics g;
		int w = i.getWidth(),
			h = i.getHeight();

		output = new BufferedImage(1+3*w+1,1+3*h+1,BufferedImage.TYPE_INT_ARGB);
		g = output.getGraphics();
		
		int xS,yS, dX,dY,dW,dH;
		for(int r = 0; r < 3; r++)
			for(int c = 0; c < 3; c++) {
				xS = (c == 1) ? 1 : -1;
				yS = (r == 1) ? 1 : -1;
				
				dX = 1+c*w + ((xS == -1)?w:0);
				dY = 1+r*h + ((yS == -1)?h:0);
				
				g.drawImage(i, dX,dY,w*xS,h*yS, null);
			}
			
		g.dispose();		
		
		i.flush();
		return output;
	}
	
	private static BufferedImage unprepareImage(BufferedImage i, int filterScale) {
		if(true) //false)
			return i;
		
		BufferedImage output;
		Graphics g;
		int iW = i.getWidth(),
			iH = i.getHeight(),
			w = (iW-2*filterScale)/3,
			h = (iH-2*filterScale)/3;
			
		output = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		g = output.getGraphics();
		
		g.drawImage(i, (w-iW)/2,(h-iH)/2, iW,iH, null);
		
		g.dispose();
		
		i.flush();
		return output;
	}
	
	private static BufferedImage scaleHQX(BufferedImage i, int scale) {
		if(scale < 1 || scale > 4)
			throw new UnsupportedOperationException();
		else if(scale == 1)
			return i;
		
				
	    // Obtain pixel data for source image
	    final int[] data = ((DataBufferInt) i.getRaster().getDataBuffer()).getData();

	    // Initialize lookup tables
	    RgbYuv.hqxInit();

	    // Create the destination image, twice as large for 2x algorithm
	    final BufferedImage biDest2 = new BufferedImage(i.getWidth() * scale, i.getHeight() * scale, BufferedImage.TYPE_INT_ARGB);
	    // Obtain pixel data for destination image
	    final int[] dataDest2 = ((DataBufferInt) biDest2.getRaster().getDataBuffer()).getData();
	    
	    switch(scale) {
	    	case 2:		Hqx_2x.hq2x_32_rb(data, dataDest2, i.getWidth(), i.getHeight());
	    	    		break;
	    	case 3:		Hqx_3x.hq3x_32_rb(data, dataDest2, i.getWidth(), i.getHeight());
	    				break;
	    	case 4:		Hqx_4x.hq4x_32_rb(data, dataDest2, i.getWidth(), i.getHeight());
	    				break;
	    }
	    
	    i.flush();
	    
	    // Release the lookup table
	    //RgbYuv.hqxDeinit();
	    
		return biDest2;
	}

	public static void setFilter(FilterType filter) {
		ImgIO.filter = filter;
	}
}
