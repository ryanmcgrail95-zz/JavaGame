package object.actor.skeleton;

import datatypes.vec3;

public class BoneAnimation {
	private int numFrames, numIndices;
	private int[] keyframeIndices;
	private vec3[] keyframes;
	


	public vec3 get(float index) {
		vec3 prev, next;
		int curIndex, prevIndex, nextIndex;
		float f, iF;
		
		for(int i = 0; i < numIndices; i++) {
			curIndex = keyframeIndices[i];
			
			if(index >= curIndex) {
				prevIndex = curIndex;
				prev = keyframes[i];

				if(i+1 == numIndices) {
					nextIndex = keyframeIndices[0];
					next = keyframes[0];
					f = (numFrames+curIndex-prevIndex)/((numFrames-prevIndex)+nextIndex);
				}
				else {
					nextIndex = keyframeIndices[i+1];
					next = keyframes[i+1];
					f = (curIndex-prevIndex)/(nextIndex-prevIndex);
				}
				
				iF = 1-f;

				return (vec3) next.mult(f).add((vec3) prev.mult(iF));
			}
		}
				
		return null;
	}
}
