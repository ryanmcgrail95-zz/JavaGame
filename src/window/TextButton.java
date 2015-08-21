package window;

import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import io.Mouse;

public class TextButton extends RectButton {
	private String text;
	private float scale;
	
	public TextButton(int x, int y, int w, int h, String text) {
		super(x, y, w, h);
		this.text = text;
		
		float xS,yS;
		xS = 1f*w/GLText.getStringWidth(text);
		yS = 1f*h/GLText.getStringHeight(text);
		scale = Math.min(xS,yS);
	}

	@Override
	public byte draw(float frameX, float frameY) {
		float dX,dY;
		dX = frameX+x();
		dY = frameY+y();
		
		// Draw Text
		GOGL.setColor(RGBA.WHITE);
		GOGL.fillRectangle(dX,dY,w(),h());
		GOGL.setColor(RGBA.BLACK);
		GLText.drawStringCentered(dX+w()/2,dY+h()/2,scale,scale,text,false);
		// Draw Outline
		GOGL.drawRectangle(dX,dY,w(),h());
		
		GOGL.resetColor();
		
		return -1;
	}

	@Override
	public void destroy() {
	}
}
