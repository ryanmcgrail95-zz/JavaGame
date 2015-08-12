package fl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class FileExt {
	//private static FileExt inst = new FileExt();
	
	private FileExt() {}
	
	public static InputStream get(String fileName) {		
		return FileExt.class.getResourceAsStream(determineName(fileName));
	}
	
	public static File getFile(String fileName) {
		return new File(fileName);
	}
	
	public static File[] getSubfiles(String dirName) {
		return getFile(dirName).listFiles();
	}
	
	public static String[] getSubfileNames(String dirName) {
		File[] files = getSubfiles(dirName);
		String[] fileNames = new String[files.length];
		
		int i = 0;
		for(File f : files)
			fileNames[i++] = dirName + f.getName();
		
		return fileNames;
	}
	
	private static String determineName(String fileName) {
		//return (new File("JavaGame.jar")).exists() ? "/" + fileName : "ResourcesContainer/" + fileName;
		return (new File("JavaGame.jar")).exists() ? "/" + fileName : "../" + fileName;
	}
}
