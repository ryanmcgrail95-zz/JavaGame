package gfx;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;

import com.jogamp.opengl.util.texture.Texture;

import fl.FileExt;

public final class PDFLoader {
	
	private PDFLoader() {}
	
	public static List<BufferedImage> loadImages(String fileName) throws IOException, RendererException, DocumentException {
		PDFDocument document = new PDFDocument();
		document.load(FileExt.get(fileName));
		
		SimpleRenderer renderer = new SimpleRenderer();
		renderer.setResolution(300);
		
		List<Image> imgs;
		imgs = renderer.render(document);
		
		List<BufferedImage> bImgs = new ArrayList<BufferedImage>();
		for(Image i : imgs)
			bImgs.add( (BufferedImage) i );
		
		return bImgs;
	}
	
	public static List<Texture> loadTextures(String fileName) throws IOException, RendererException, DocumentException {
		List<BufferedImage> imgs = loadImages(fileName);
		List<Texture> textureList = new ArrayList<Texture>();
		
		for(BufferedImage i : imgs)
			textureList.add(GOGL.createTexture(i, false));
		
		return textureList;
	}
	
	public static TextureExt loadTextureExt(String fileName) throws IOException, RendererException, DocumentException {
		return new TextureExt(loadImages(fileName));
	}
}
