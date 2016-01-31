package gfx;

import com.jogamp.opengl.util.texture.Texture;

import functions.ArrayMath;

public class MultiTexture extends Sprite {
	private Texture tex;
	private int xNum, yNum;
	private float xSize, ySize;

	public MultiTexture(String fileName, int xNum, int yNum, boolean grayscale) 	{
		super(fileName);
		set(GL.loadTexture(fileName,grayscale), xNum, yNum);
	}
	public MultiTexture(String fileName, int xNum, int yNum) 	{
		super(fileName);
		set(GL.loadTexture(fileName), xNum, yNum);
	}
	public MultiTexture(Texture tex, int xNum, int yNum) 		{
		super("---");
		set(tex, xNum,yNum);
	}
	
	private void set(Texture tex, int xNum, int yNum) {		
		this.tex = tex;
		this.xNum = xNum;
		this.yNum = yNum;
				
		xSize = 1f/xNum;
		ySize = 1f/yNum;
	}
	
	
	public float[] getBounds(int frame) {
		containFrame(frame);
		
		int xP, yP;
		xP = frame % xNum;
		yP = (frame - xP)/xNum;
		
		float x,y;
		x = xP*xSize;
		y = yP*ySize;
		
		return ArrayMath.setTemp4(x,y,x+xSize,y+ySize);
	}
	public int getImageNumber() {return xNum*yNum;}
	public Texture getTexture(int frame) {return tex;}
}
