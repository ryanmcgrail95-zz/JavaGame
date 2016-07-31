package window;

import java.util.concurrent.Callable;

public abstract class GUIButton extends GUIDrawable {
	private Callable function;
	private float glow;

	public GUIButton(float x, float y, float w, float h, Callable function) {
		super(x, y, w, h);
		
		this.function = function;
	}
	
	public abstract boolean checkMouse();

	public void activate() {
		try {
			function.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isSelected() {
		return getParent().getSelected() == this;
	}
	
	public float getGlow() {return glow;}
	public void glowTo(float num) {
		glow += (num - glow)/5;
	}
}
