package gfx;

import com.jogamp.opengl.util.texture.Texture;

import cont.Log;
import functions.MathExt;
import resource.Resource;
import resource.image.Img;

public abstract class Sprite extends Resource implements Img {
	protected static final float[] DEFAULT_BOUNDS = {0,0,1,1};
	private boolean doRepeatX, doMirrorX;
	private boolean doRepeatY, doMirrorY;

	public Sprite(String fileName, int... args) {
		super(fileName,R_TEXTURE,true,args);
		
		doRepeatX = false;
		doRepeatY = false;
		doMirrorX = false;
		doMirrorY = false;
	}
	
	public boolean getDoRepeatX() 					{return doRepeatX;}
		public void setDoRepeatX(boolean doRepeatX) {this.doRepeatX = doRepeatX;}
	public boolean getDoRepeatY() 					{return doRepeatY;}
		public void setDoRepeatY(boolean doRepeatY) {this.doRepeatX = doRepeatY;}
	public boolean getDoMirrorX() 					{return doMirrorX;}
		public void setDoMirrorX(boolean doMirrorX) {this.doMirrorX = doMirrorX;}
	public boolean getDoMirrorY() 					{return doMirrorY;}
		public void setDoMirrorY(boolean doMirrorY) {this.doMirrorX = doMirrorY;}
	
	public int getWidth() {return getWidth(0);}
	public int getWidth(float frame) {
		float[] bounds = getBounds(frame);
		float w = getTexture(frame).getWidth(),
			x1 = bounds[0], 
			x2 = bounds[2];
		return (int) (w*(x2-x1));
	}

	public int getHeight() {return getHeight(0);}
	public int getHeight(float frame) {
		float[] bounds = getBounds(frame);
		float h = getTexture(frame).getHeight(),
			y1 = bounds[1], 
			y2 = bounds[3];
		return (int) (h*(y2-y1));
	}
	
	public void draw(float x, float y, float frame) {
		//GL.start("Sprite.draw(...)");
		
		Texture t = getTexture(frame);
		G2D.drawTexture(x,y,t.getWidth(),t.getHeight(),t,getBounds(frame));

		//GL.end("Sprite.draw(...)");
	}
	public void draw(float x, float y, float w, float h, float frame) {
		//GL.start("Sprite.draw(...)");
		G2D.drawTexture(x, y, w, h, getTexture(frame), getBounds(frame));
		//GL.end("Sprite.draw(...)");
	}
	
	public void draw(float x, float y, float w, float h, float frame, int startFrame, int length) {
		//GL.start("Sprite.draw(...)");
		
		frame = Math.round(MathExt.wrap(0, frame, length));		
		
		int f = (int) frame;
		f = startFrame + ((f < length) ? f : 0);
				
		G2D.drawTexture(x, y, w, h, getTexture(f), getBounds(f));
		//GL.end("Sprite.draw(...)");
	}

	public abstract int getImageNumber();

	protected int containFrame(int frame) {
		int imgNum = getImageNumber();
		if(imgNum == 0)
			throw new UnsupportedOperationException("Image " + getFileName() + " has no frames!");
		else
			return frame % imgNum;
	}
	
	public Texture getTexture() {return getTexture(0);};
	public Texture getTexture(float frame) {return getTexture((int) frame);}
	public abstract Texture getTexture(int frame);
	
	public float[] getBounds() {return getBounds(0);};
	public float[] getBounds(float frame) {return getBounds((int) frame);}
	public abstract float[] getBounds(int frame);
}
