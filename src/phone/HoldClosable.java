package phone;

import ds.WeightedSmoothFloat;
import ds.WeightedSmoothFloat;
import gfx.GOGL;
import object.primitive.Drawable;

public abstract class HoldClosable extends Drawable {
	
	protected WeightedSmoothFloat onscreenFrac;


	public HoldClosable(boolean hoverable, boolean renderable) {
		super(hoverable, renderable);
		name = "HoldClosable";
		
		onscreenFrac = new WeightedSmoothFloat(1,0,-1f,.5f,20);
		onscreenFrac.setFraction(1);
	}
	public void step() {
		onscreenFrac.step();
	}
	
	public void transformOnscreen(float nX, float nY, float nZ, float amt) {
		amt *= (1-getOnscreenFrac());
		GOGL.transformTranslation(nX*amt,nY*amt,nZ*amt);
	}
	
	public boolean checkOnscreen() {return true;}
	public float calcDepth() {return 0;}
	
	
	protected float getOnscreenFrac() {return onscreenFrac.get();}
	protected boolean isOnscreen() {return !onscreenFrac.equals(0);}
	protected void setMeActive(boolean active) {
		float toVal = (active ? 1:0);
		if(onscreenFrac.getTo() != toVal)
			onscreenFrac.flip();
	}
	protected boolean amIActive() {
		return onscreenFrac.getTo() == 1;
	}
}
