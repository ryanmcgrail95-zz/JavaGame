package animation;

import java.util.ArrayList;
import java.util.List;

public abstract class Animation {
	private List<Frame> frames = new ArrayList<Frame>();
	private List<SubFrame> subFrames = new ArrayList<SubFrame>();

	
	protected abstract void save(String fileName);
	
	protected abstract void loadFile(String fileName);
	public void load(String fileName) {
		loadFile(fileName);
		
		fixFrames();
		fixSubFrames();
	}
	
	public void fixFrames() {
		Frame prevFrame = frames.get(frames.size()-1);
		for(Frame f : frames) {
			f.setPrevFrame(prevFrame);
			prevFrame = f;
		}
		Frame f;
		prevFrame = frames.get(0);
		for(int i = frames.size()-1; i >= 0; i--) {
			f = frames.get(i);
			f.setNextFrame(prevFrame);
			prevFrame = f;
		}
	}
	public void fixSubFrames() {
		
	}
	

	public List<Frame> getFrames() {return frames;}
	public List<SubFrame> getSubFrames() {return subFrames;}
}
