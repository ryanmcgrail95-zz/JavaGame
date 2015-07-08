package window;

public abstract class GUIObject {
	private GUIFrame parent;
	private boolean isDrawable;
	
	public GUIObject(boolean isDrawable) {
		this.isDrawable = isDrawable;
	}
	
	public abstract void destroy();
	
	public boolean getDrawable() {
		return isDrawable;
	}
	
	public GUIFrame getParent()	{return parent;}
	public void setParent(GUIFrame parent) {this.parent = parent;}
}
