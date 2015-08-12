package window;

public abstract class Button extends GUIDrawable {
	private float glow;

	public Button(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	
	public abstract boolean checkMouse();
	
	
	public float getGlow() {return glow;}
	public void glowTo(float num) {
		glow += (num - glow)/5;
	}
}
