package window;

public abstract class GUIDrawable extends GUIObject {
	private float x, y, w, h;

	public GUIDrawable(float x, float y, float w, float h) {
		super(true);
		x(x);
		y(y);
		w(w);
		h(h);
	}
	
	public abstract void draw(float x, float y);
	
	public float x()	 	{return x;}
	public void x(float x) 	{this.x = x;}
	public float y()	 	{return y;}
	public void y(float y) 	{this.y = y;}
	public float w()	 	{return w;}
	public void w(float w) 	{this.w = w;}
	public float h()	 	{return h;}
	public void h(float h) 	{this.h = h;}
	
	public float getScreenX() {
		if(getParent() == null)
			return x();
		else
			return x() + getParent().getScreenX();
	}
	public float getScreenY() {
		if(getParent() == null)
			return y();
		else
			return y() + getParent().getScreenY();
	}
}
