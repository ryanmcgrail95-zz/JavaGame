package animation;


public abstract class SubFrame {
	protected Interpolatable destObj;
	protected SubFrame prevSubFrame, nextSubFrame;
	protected float index;
	protected int listIndex;
	
	
	public abstract void step(float frameIndex);
	
	public float calcInterpolationFactor(float frameIndex) {
		return (frameIndex - index)/(nextSubFrame.index - index);
	}
	
	public int getListIndex() {
		return listIndex;
	}
}
