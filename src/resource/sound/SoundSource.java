package resource.sound;

import com.jogamp.openal.AL;

import datatypes.vec3;
import functions.MathExt;

public class SoundSource {
	private SoundBuffer parentBuffer;
	private int[] 	sourceID = new int[1],
					bufferID = new int[1];
	private float volumePercent = 1, fadeAmount = 1, volume;
	private int doLoop;
	
	private final static byte S_STOPPED = 0, S_PAUSED = 1, S_PLAYING = 2;
	private byte playState = S_PAUSED;
	
	
	public SoundSource(SoundBuffer blueprint, boolean doLoop) {
		create(blueprint,Sound.getListenerPosition(),Sound.getListenerVelocity(),doLoop);
	}
	public SoundSource(SoundBuffer blueprint, float[] pos, float[] vel, boolean doLoop) {
		create(blueprint,pos,vel,doLoop);
	}	
	public SoundSource(SoundBuffer blueprint, float x, float y, float z, float vX, float vY, float vZ, boolean doLoop) {
		float[] pos = {x,y,z}, vel = {vX,vY,vZ};
		create(blueprint, pos,vel, doLoop);
	}
		
	public static AL al() {
		return Sound.al();
	}
	
	public void create(SoundBuffer blueprint, vec3 pos, vec3 vel, boolean doLoop) {create(blueprint,pos.getArray(),vel.getArray(), doLoop);}
	public void create(SoundBuffer blueprint, float[] pos, float[] vel, boolean doLoop) {

		generateSource();
		
		this.doLoop = doLoop ? 1 : 0;
		setSoundBuffer(blueprint);

        setPosition(pos);
        setVelocity(vel);
	}
	
	
	public void generateSource() {
		al().alGenSources(1, sourceID, 0);
	}
	
	public void setSoundBuffer(SoundBuffer blueprint) {
		parentBuffer = blueprint;		
        al().alSourcei (sourceID[0], AL.AL_BUFFER,   blueprint.getBuffer()[0]);
        al().alSourcef (sourceID[0], AL.AL_PITCH,    1.0f);
        al().alSourcef (sourceID[0], AL.AL_GAIN,     fadeAmount*volumePercent*(volume = blueprint.getVolume()));
        al().alSourcei (sourceID[0], AL.AL_LOOPING,  doLoop);
	}
	
	public void swapSoundBuffer(String name) {swapSoundBuffer(Sound.get(name));}
	public void swapSoundBuffer(SoundBuffer newBuffer) {
		boolean wasPlaying = isPlaying();
		
		stop();
		setSoundBuffer(newBuffer);
		if(wasPlaying)
			play();
	}
	
	public void setPosition(float[] pos) {
		al().alSourcefv(sourceID[0], AL.AL_POSITION, pos, 0);        
	}
	public void setVelocity(float[] vel) {
        al().alSourcefv(sourceID[0], AL.AL_VELOCITY, vel, 0);
	}
	
	
	
	public void play() 	{
		if(isPlaying())	return;
		playState = S_PLAYING;
		al().alSourcePlay(sourceID[0]);
	}
	public void pause() {
		if(isPaused())	return;
		playState = S_PAUSED;
		al().alSourcePause(sourceID[0]);
	}
	public void stop() 	{
		if(isStopped())	return;
		playState = S_STOPPED;
		al().alSourceStop(sourceID[0]);
	}

	public void destroy() {
		stop();
		al().alDeleteSources(1, sourceID, 0);
	}
	
	
	
	public float getVolume() {return volumePercent;}
	public void setVolume(float volumePerc) {
		volumePercent = MathExt.contain(0,volumePerc,1);
		al().alSourcef(sourceID[0], AL.AL_GAIN, fadeAmount*volumePercent*volume);
	}
	
	public void setFadeAmount(float value) {
		fadeAmount = MathExt.contain(0,value,1);
		al().alSourcef(sourceID[0], AL.AL_GAIN, fadeAmount*volumePercent*volume);
	}
	public boolean fade(float toValue) {
		toValue = MathExt.contain(0,toValue,1);
		
		float sign = Math.signum(toValue-fadeAmount);
		fadeAmount += .1*sign;
		
		if(sign != Math.signum(toValue-fadeAmount))
			fadeAmount = toValue;
		
		al().alSourcef(sourceID[0], AL.AL_GAIN, fadeAmount*volumePercent*volume);
		
		return fadeAmount == toValue;
	}

	
	public int getState() {
		int[] state = new int[1];
		al().alGetSourcei(sourceID[0], AL.AL_SOURCE_STATE, state, 0);
		return state[0];
	}
	public boolean isPlaying() 	{return playState == S_PLAYING;}
	public boolean isPaused() 	{return playState == S_PAUSED;}
	public boolean isStopped() 	{return playState == S_STOPPED;}
	public boolean isALPlaying() 	{return (getState() == AL.AL_PLAYING);}
	public boolean isALPaused() 	{return (getState() == AL.AL_PAUSED);}
	public boolean isALStopped() 	{return (getState() == AL.AL_STOPPED);}
	public SoundBuffer getParentBuffer() {
		return parentBuffer;
	}
	public boolean isLooping() {
		return doLoop == 1;
	}
	
	public void setSpeed(float speed) {
		al().alSourcef(sourceID[0], AL.AL_PITCH, speed);
	}
	
	public float getLocation() {
		float[] loc = new float[1];
		al().alGetSourcef(sourceID[0], AL.AL_SEC_OFFSET, loc, 0);
		return loc[0];
	}
	public void setLocation(float secondsOffset) {
		al().alSourcef(sourceID[0], AL.AL_SEC_OFFSET, secondsOffset);
	}
}
