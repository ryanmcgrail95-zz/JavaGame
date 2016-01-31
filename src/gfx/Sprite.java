package gfx;

import com.jogamp.opengl.util.texture.Texture;

public abstract class Sprite {
	protected static final float[] DEFAULT_BOUNDS = {0,0,1,1};

	public Sprite(String fileName) {
		System.out.println("Loaded sprite,  " + fileName + ".");
	}
	
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
		Texture t = getTexture(frame);
		G2D.drawTexture(x,y,t.getWidth(),t.getHeight(),t,getBounds(frame));
	}
	public void draw(float x, float y, float w, float h, float frame) {
		G2D.drawTexture(x, y, w, h, getTexture(frame), getBounds(frame));
	}

	public abstract int getImageNumber();

	protected int containFrame(int frame) {return frame % getImageNumber();}
	
	public Texture getTexture() {return getTexture(0);};
	public Texture getTexture(float frame) {return getTexture((int) frame);}
	public abstract Texture getTexture(int frame);
	
	public float[] getBounds() {return getBounds(0);};
	public float[] getBounds(float frame) {return getBounds((int) frame);}
	public abstract float[] getBounds(int frame);
}
