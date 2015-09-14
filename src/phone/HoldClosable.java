package phone;

import gfx.GOGL;
import object.primitive.Drawable;

public abstract class HoldClosable extends Drawable {
	
	protected float onscreenFrac, onscreenToFrac;


	public HoldClosable(boolean hoverable, boolean renderable) {
		super(hoverable, renderable);
		
		onscreenFrac = onscreenToFrac = 0;
	}
	public void step() {
		onscreenFrac += (onscreenToFrac - onscreenFrac)/5;
		if(Math.abs(onscreenFrac-onscreenToFrac) < .001)
			onscreenFrac = onscreenToFrac;
	}
	
	public void transformOnscreen(float nX, float nY, float nZ, float amt) {
		amt *= (1-getOnscreenFrac());
		GOGL.transformTranslation(nX*amt,nY*amt,nZ*amt);
	}
	
	public boolean checkOnscreen() {return true;}
	public float calcDepth() {return 0;}
	
	
	protected float getOnscreenFrac() {return onscreenFrac;}
	protected boolean isOnscreen() {return onscreenFrac != 0;}
	protected void setMeActive(boolean active) {onscreenToFrac = (active ? 1:0);}
	protected boolean amIActive() {return onscreenToFrac == 1;}
}
