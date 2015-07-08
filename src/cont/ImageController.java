package cont;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import fl.FileExt;

public class ImageController {
	private static Map<String, BufferedImage> imgMap = new HashMap<String, BufferedImage>();
		
	public static BufferedImage loadImage(String fileName, String name) {			
		BufferedImage img = loadImage(fileName);
		imgMap.put(name, img);
        
        return img;
	}
	
	public static BufferedImage loadImage(String fileName) {	
		try {
			return ImageIO.read(FileExt.get(fileName));
			//return ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage getImage(String name) {
		return imgMap.get(name);
	}
}
