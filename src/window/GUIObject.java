package window;

public abstract class GUIObject {
	private GUIFrame parent;
	private boolean isDrawable, isDestroyed;
	
	public GUIObject(boolean isDrawable) {
		this.isDrawable = isDrawable;
	}
	
	public void destroy() {
		isDestroyed = true;
	}
		
	public boolean getDrawable() {
		return isDrawable;
	}
	public boolean isDestroyed() {
		return isDestroyed;
	}

	public GUIFrame getParent()	{return parent;}
	public void setParent(GUIFrame parent) {this.parent = parent;}
}
