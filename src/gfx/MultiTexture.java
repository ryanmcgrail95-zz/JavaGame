package gfx;

import java.io.IOException;

import com.jogamp.opengl.util.texture.Texture;
import cont.ImgIO;
import cont.TextureController;
import functions.ArrayMath;
import functions.MathExt;

public class MultiTexture extends Sprite {
	private Texture tex;
	private int xNum, yNum;
	private float xSize, ySize;

	public MultiTexture(String fileName, AlphaType alphaModel, int xNum, int yNum) {
		super(fileName, MathExt.indexEnum(alphaModel, alphaTypeList));
		
		set(tex,xNum,yNum);
	}
	
	public MultiTexture(String fileName, int xNum, int yNum) {
		super(fileName, MathExt.indexEnum(AlphaType.NORMAL, alphaTypeList));
		
		set(tex,xNum,yNum);
	}
	
	private void set(Texture tex) {set(tex,xNum,yNum);}	
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
	
	@Override
	public void load(String fileName, int... args) {
		try {
			set(GL.createTexture(ImgIO.load(fileName, (byte) args[0])[0]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unload() {
		super.unload();

		TextureController.destroy(tex);
	}
}
