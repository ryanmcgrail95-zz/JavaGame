package window;

import ds.lst.CleanList;
import io.Mouse;

public abstract class GUIDrawable extends GUIObject {
	private CleanList<GUIListener> listeners = new CleanList<GUIListener>("Listener");
	private float x, y, w, h;

	public GUIDrawable(float x, float y, float w, float h) {
		super(true);
		x(x);
		y(y);
		w(w);
		h(h);
	}
	
	public abstract byte draw(float x, float y);
	
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
	

	public void addListener(GUIListener l) {
		listeners.add(l);
	}
	public abstract boolean checkMouse();
	
	public void updateEvents() {
		if(checkMouse()) {
			for(GUIListener l : listeners)
				l.hover();
			if(Mouse.getLeftClick())
				for(GUIListener l : listeners)
					l.click();
		}
	}
	
	public float centerX() {return x() + w()/2;}
	public float centerY() {return y() + h()/2;}
}
