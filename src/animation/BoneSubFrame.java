package animation;

public class BoneSubFrame extends SubFrame {
	private Orientation ori;

	
	public void step(float frameIndex) {
		Orientation bOri = (Orientation) destObj,
				newOri = calcOrientation(frameIndex);
		
		bOri.updateValues(newOri);
	}
	
	public Orientation calcOrientation(float frameIndex) {
		if(nextSubFrame != null)
			return (Orientation) ori.tween(((BoneSubFrame) nextSubFrame).ori, calcInterpolationFactor(frameIndex));
		else
			return ori;
	}
}
