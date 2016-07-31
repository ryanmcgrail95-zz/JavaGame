package window;

import gfx.GL;

import java.util.concurrent.Callable;

import gfx.G2D;
import gfx.RGBA;
import io.Mouse;

public class RectButton extends GUIButton {
	
	public RectButton(float x, float y, float w, float h, Callable function) {
		super(x, y, w, h, function);
	}
	
	public boolean checkMouse() {		
		return getParent().checkRectangle(x(),y(),w(),h());
	}
	
	public byte draw(float frameX, float frameY) {
		
		float dX,dY, s, iX,iY;
		dX = frameX+x();
		dY = frameY+y();
		s = .7f;
		
		iX = dX+5;
		iY = dY+1;
				
		// Draw Item Window
		//GL.fillVGradientRectangle(dX,dY, w(),h()*3/4, new RGBA(23,19,17),new RGBA(68,56,46), 4);
		// Draw Price
		//GL.fillVGradientRectangle(dX,dY+h()*3/4, w(),h()/4, new RGBA(41,35,28),new RGBA(68,56,46), 4);
		// Draw Item
		GL.setColor(RGBA.WHITE);
		// Draw Text
		//GL.setColor(new RGBA(255,255,0));
		G2D.fillPolygon(dX+8,dY+h()*7/8, h()*1/8, 8);
		//G2D.drawString(dX+w()-4-GLText.getStringWidth(s,s,cost),dY+h()*3/4+2, s,s, cost,true);
		// Draw Outline
		//GL.setColor(new RGBA(41,35,28));
		GL.drawRectangle(dX,dY,w(),h());
		
		
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
		GL.fillRectangle(dX,dY,w(),h());
		
		return -1;
	}

	public void destroy() {}
}
