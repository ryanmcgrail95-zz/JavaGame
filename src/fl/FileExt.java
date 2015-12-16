package fl;

import gfx.ErrorPopup;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class FileExt {
	//private static FileExt inst = new FileExt();
	
	private FileExt() {}
	
	public static InputStream get(String fileName) {
		return FileExt.class.getResourceAsStream(fixNameGet(fileName));
	}
	
	public static File getFile(String fileName) {
		return new File(fixNameGetFile(fileName));
	}
	
	public static String readFile2String(String fileName) {
		String str = "", line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(getFile(fileName)));
			
			while((line = reader.readLine()) != null)
				str += line+"\n";
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return str;
	}
	
	public static String fixSlashes(String fileName) {
		return fileName.replace("\\", "/");
	}
	private static String fixNameGet(String fileName) {
		String fName;
		
		fileName = fixSlashes(fileName);
		fName = (fileName.startsWith("ResourcesContainer/")) ? fileName.replace("ResourcesContainer/", "") : fileName;
		
		if((new File("JavaGame.jar")).exists())
			fName = "../" + fName;
		else 
			fName = "../" + fName;
				
		return fName;
	}

	private static String fixNameGetFile(String fileName) {
		fileName = fixSlashes(fileName);
		return fileName.startsWith("Resources/") ? "ResourcesContainer/"+fileName : fileName;
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
	

	
	public static String getSuffix(String fName) {
		String suffix = "";
		char c;
		
		for(int i = fName.length()-1; i >= 0; i--) {
			c = fName.charAt(i);
			if(c == '.')
				return suffix;
			else
				suffix = c + suffix;
		}
		return "";
	}
}
