package window;

import java.util.concurrent.Callable;

import gfx.G2D;
import gfx.GL;
import gfx.RGBA;
import io.Mouse;

public class TextButton extends RectButton {
	private String text;
	private float scale;
	
	public TextButton(String text, int x, int y, int w, int h, Callable function) {
		super(x, y, w, h, function);
		this.text = text;
		
		float xS,yS;
		xS = 1f*w/G2D.getStringWidth(text);
		yS = 1f*h/G2D.getStringHeight(text);
		scale = Math.min(xS,yS);
	}

	@Override
	public byte draw(float frameX, float frameY) {
		float dX,dY;
		dX = frameX+x();
		dY = frameY+y();
		
		// Draw Text
		GL.setColor(RGBA.WHITE);
		G2D.fillRectangle(dX,dY,w(),h());
		GL.setColor(RGBA.BLACK);
		G2D.drawStringCentered(dX+w()/2,dY+h()/2,scale,scale,text,false);
		// Draw Outline
		G2D.drawRectangle(dX,dY,w(),h());
		
		
		if(checkMouse()) {
			glowTo(1);
			Mouse.setFingerCursor();
			
			if(Mouse.getLeftClick())
				activate();
		}
		else if(isSelected())
			glowTo(1);
		else
			glowTo(0);
			
		GL.setAlpha(getGlow()*.5f);
		G2D.fillRectangle(dX,dY,w(),h());
		
		
		GL.resetColor();
		
		return -1;
	}

	@Override
	public void destroy() {
	}
}
