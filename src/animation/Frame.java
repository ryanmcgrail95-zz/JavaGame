package animation;

import java.util.ArrayList;
import java.util.List;

public final class Frame {
	private List<SubFrame> frames = new ArrayList<SubFrame>();
	private Frame prevFrame, nextFrame;
	private float index;
	
	
	public float getIndex() {return index;}
	
	public List<SubFrame> getSubFrames() {return frames;}
	public Frame getPrevFrame() {return prevFrame;}
	public Frame getNextFrame() {return nextFrame;}

	public void setPrevFrame(Frame prevFrame) {
		this.prevFrame = prevFrame;
	}
	public void setNextFrame(Frame nextFrame) {
		this.nextFrame = nextFrame;
	}
}
