package gfx;

import com.jogamp.opengl.util.texture.Texture;

public class MultiTexture {
	private Texture tex;
	private int xNum, yNum;
	private float xSize, ySize;
	
	public MultiTexture(String fileName, int xNum, int yNum) {
		set(GOGL.loadTexture(fileName), xNum, yNum);
	}
	public MultiTexture(Texture tex, int xNum, int yNum) {
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
		frame %= xNum*yNum;
		
		int xP, yP;
		xP = frame % xNum;
		yP = (frame - xP)/xNum;
		
		float x,y;
		x = xP*xSize;
		y = yP*ySize;
		
		return new float[] {x,y,x+xSize,y+ySize};
	}
	public Texture getTexture() {
		return tex;
	}
}
