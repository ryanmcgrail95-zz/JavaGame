package window.paint;

import com.jogamp.opengl.GL2;

import cont.TextureController;
import ds.lst.CleanList;
import time.Stopwatch;
import time.Timer;
import window.GUIDrawable;
import window.Window;
import io.Keyboard;
import io.Mouse;
import resource.sound.Sound;
import resource.sound.SoundBuffer;
import resource.sound.SoundSource;
import functions.Math2D;
import functions.MathExt;
import games.Picross;
import gfx.FBO;
import gfx.G2D;
import gfx.GL;
import gfx.MultiTexture;
import gfx.RGBA;

public class GUIPaint extends GUIDrawable {
	private Layer[] layers;
	private FBO previewFBO;
	
	public GUIPaint(int width, int height) {
		super(0,0, width,height);
		
		layers = new Layer[1];
		layers[0] = new Layer(width, height);
		
		layers[0].clear(RGBA.WHITE);
		layers[0].repaint();
		
		previewFBO = new FBO(GL.getGL(), width,height);
		repaint();
	}

	
	@Override
	public byte draw(float frameX, float frameY) {
		if(checkMouse() && Mouse.getLeftClick()) {
			layers[0].clear(RGBA.randomizeAboveValue(0));
			layers[0].repaint();
			repaint();
		}
				
		G2D.drawFBO(frameX, frameY, layers[0].getFBO());
		
		return 0;
	}
	
	public void repaint() {
		GL2 gl = GL.getGL2();		
		previewFBO.attach(gl);
		for(int i = 0; i < layers.length; i++)
			layers[i].draw(0,0);
		previewFBO.detach(gl);
	}
	
	@Override
	public void destroy() {
		for(int i = 0; i < layers.length; i++)
			layers[i].destroy();
		layers = null;
	}

	@Override
	public boolean checkMouse() {
		return getParent().checkMouse();
	}	
	
	public static void createWindow(int x, int y) {
		int width = 320,
			height = 240;
		
		Window w = new Window("Paint", x,y, width,height, true);
		w.add(new GUIPaint(width, height));
	}
}
