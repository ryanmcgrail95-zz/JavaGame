package animation;

import java.util.ArrayList;
import java.util.List;

public class AnimationController {
	private Animation curAnimation;
	private List<Frame> frames;
	private List<SubFrame> subFrames;
	private Frame curFrame;
	private float frameIndex;
	
	
	public void setAnimation(Animation newAnimation) {
		curAnimation = newAnimation;
		frames = curAnimation.getFrames();
		subFrames = curAnimation.getSubFrames();
	}
	
	public void step(float speed) {
		float prevIndex = frameIndex, curFrameInd = curFrame.getIndex();
		
		frameIndex += speed;
		if(prevIndex < curFrameInd && curFrameInd <= frameIndex) {
			curFrame = curFrame.getNextFrame();
			
			List<SubFrame> curSubs = curFrame.getSubFrames();
			for(SubFrame sf : curSubs)
				subFrames.set(sf.getListIndex(), sf);
		}
		
		for(SubFrame s : subFrames)
			s.step(frameIndex);
	}
	
	
	public void setIndex(float newFrameIndex) {
		// Search through previous frames to find start point for everything.
		frameIndex = newFrameIndex;
		
		int sfLeft = subFrames.size();
		for(int i = 0; i < sfLeft; i++)
			subFrames.set(i, null);
		
		
		// Search through Frames for Start Frame (First Frame Before index)
		Frame startFrame = frames.get(frames.size()-1), curFrame;
		for(Frame f : frames) {
			if(f.getIndex() > newFrameIndex)
				break;
			startFrame = f;
		}
		curFrame = startFrame;


		// Loop through, Fill in
		List<SubFrame> curSubs;
		int pos;
		do {
			curSubs = curFrame.getSubFrames();
			for(SubFrame sf : curSubs) {
				pos = sf.getListIndex();
				// If Not Found, Fill In
				if(subFrames.get(pos) == null) {
					subFrames.set(pos, sf);
					sfLeft--;
				}
			}
			
			curFrame = curFrame.getPrevFrame();
		} while(sfLeft == 0 || curFrame != startFrame);
		
		
		this.curFrame = startFrame;
	}

}
