package resource;

import gfx.GL;
import ds.lst.CleanList;

public final class Loadbar {
	private static CleanList<Loadbar> loadbars = new CleanList<Loadbar>("Loadbars");
	
	private int numSteps;
	private float percentage, w = 64, h = 16;
	
	public Loadbar(int numSteps) {
		this.percentage = 0;
		this.numSteps = numSteps;
		loadbars.add(this);
	}
	
	public void destroy() {
		loadbars.remove(this);
	}
	
	public void step() {
		// Increase Percentage by Predetermined Amount
		percentage += 1f/numSteps;
		
		// Throw Exception if Bar Fills Too Much
		if(percentage > 1)
			throw new UnsupportedOperationException("Loadbar exceeded number of steps.");
	}
	
	private void draw(float x, float y) {
		GL.drawFillBar(x,y,w,h,percentage);
	}
	
	public static void drawAll(float x, float y) {
		for(Loadbar l : loadbars) {
			l.draw(x,y);
			y += l.h*1.25;
		}
	}
}
