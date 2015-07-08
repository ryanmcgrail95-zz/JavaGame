package fl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class FileExt {
	//private static FileExt inst = new FileExt();
	
	public static InputStream get(String fileName) {
		if((new File("3DGame.jar")).exists())
			fileName = "/" + fileName;
		else
			fileName = "../" + fileName;
		
		InputStream str = FileExt.class.getResourceAsStream(fileName);//.replace('/', '.'));
		
		return str;
	}
}
