package window.paint;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2;
import com.sun.prism.impl.BufferUtil;

import gfx.FBO;
import gfx.G2D;
import gfx.GL;
import gfx.RGBA;

public class Layer {
	private int width, height;
	private Channel[] rgbaChannels;
	private FBO previewFBO;
	
	public Layer(int width, int height) {
		this.width = width;
		this.height = height;
		
		rgbaChannels = new Channel[4];
		for(int i = 0; i < 4; i++)
			rgbaChannels[i] = new Channel(width, height);		
		previewFBO = new FBO(GL.getGL(), width,height);
		
		repaint();
	}
	
	public void crop(int x, int y, int width, int height) {
		for(int i = 0; i < 4; i++)
			rgbaChannels[i].crop(x, y, width, height);		
	}
	
	public void scale(double xscale, double yscale) {
		for(int i = 0; i < 4; i++)
			rgbaChannels[i].scale(xscale, yscale);
	}
	
	public void destroy() {
		for(int i = 0; i < 4; i++)
			rgbaChannels[i].destroy();
		rgbaChannels = null;
	}

	public void draw(float frameX, float frameY) {
		G2D.drawFBO(frameX, frameY, previewFBO);
	}
		
	public void clear(RGBA color) {
		char
			r = (char) color.Ri(),
			g = (char) color.Gi(),
			b = (char) color.Bi(),
			a = (char) color.Ai();
				
		rgbaChannels[0].clear(r);		
		rgbaChannels[1].clear(g);
		rgbaChannels[2].clear(b);
		rgbaChannels[3].clear(a);		
	}
	
	public FBO getFBO() {
		return previewFBO;
	}
	
	public void repaint() {
		ByteBuffer buff = ByteBuffer.allocate(4 * width * height);
		
		GL2 gl = GL.getGL2();		
		previewFBO.attach(gl);
		GL.clear();
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++) {		
				buff.put((byte) rgbaChannels[0].get(x,y));
				buff.put((byte) rgbaChannels[1].get(x,y));
				buff.put((byte) rgbaChannels[2].get(x,y));
				buff.put((byte) rgbaChannels[3].get(x,y));
			}
		buff.position(0);
		
		gl.glDrawPixels(width, height, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, buff);
		previewFBO.detach(gl);
		
		buff.clear();
	}

	private static enum BlendMode{ ADD };
	
	public static int paintPixel(int x, int y, int r, int g, int b, int a, BlendMode mode) {
		
	}
}
