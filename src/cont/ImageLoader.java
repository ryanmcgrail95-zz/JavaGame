package cont;

import image.filter.AlphaHistogramFilter;
import image.filter.GrayscaleAlphaFilter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import fl.FileExt;

public final class ImageLoader {
	
	private static byte A_NORMAL = 0, A_GRAYSCALE = 1;
	
	private ImageLoader() {}
	
	public static BufferedImage load(String fileName) throws IOException {
		return load(fileName, A_NORMAL);
	}
	public static BufferedImage loadGrayscaleAlpha(String fileName) throws IOException {	
		return load(fileName, A_GRAYSCALE);
	}
	
	public static BufferedImage load(String fileName, byte method) throws IOException {
		InputStream str = FileExt.get(fileName);
		BufferedImage i = ImageIO.read(str);
		
		if(method == A_GRAYSCALE) {
			i = TextureController.addAlpha(i);
			(new GrayscaleAlphaFilter()).filter(i,i);
			//(new AlphaHistogramFilter()).filter(i,i);
		}
		
		str.close();
		
		return i;
	}
}
