package resource.sound;

import com.jogamp.openal.AL;

import ds.vec3;
import functions.Math2D;
import functions.MathExt;

public class SoundSource {
	private SoundBuffer parentBuffer;
	private int[] sourceID = new int[1];
	private int bufferID;
	private float volumePercent = 1, fadeAmount = 1, fadeToAmt = 1, volume, speed = 1;
	private int doLoop;
	private boolean isReversed = false, wasNeverPlayed = true;
	
	// EFFECTS
	private static final byte FX_NONE = 0, FX_DIZZY = 1;
	private byte effect = FX_NONE;
	private float wobbleDir = 0, wobbleDirSpeedDir = 0, wobbleDirSpeedMax = 2 , wobbleSize = .15f;
	
	private boolean fadeIn;
	private SoundBuffer newBuffer;

	
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
		stop();
		
		wasNeverPlayed = true;
		
		parentBuffer = blueprint;
        setBuffer(blueprint);
        al().alSourcef (sourceID[0], AL.AL_PITCH,    1.0f);
        al().alSourcef (sourceID[0], AL.AL_GAIN,     fadeAmount*volumePercent*(volume = blueprint.getVolume()));
        al().alSourcei (sourceID[0], AL.AL_LOOPING,  doLoop);
	}
	
	
	public void setBuffer(SoundBuffer blueprint) {
		setBuffer(isReversed ? blueprint.getReverseBuffer() : blueprint.getBuffer());
	}
	public void setBuffer(int[] buffer) {
		al().alSourcei(sourceID[0], AL.AL_BUFFER, bufferID = buffer[0]);
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
		wasNeverPlayed = false;
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
		
		al().alSourcei(sourceID[0], AL.AL_BUFFER, 0);
		al().alDeleteSources(1, sourceID, 0);
		
		parentBuffer = null;
		sourceID = null;
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
	private boolean fade(float toValue) {
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
	
	public void reverse(boolean doReverse) {
		if(isReversed != doReverse) {
			isReversed = doReverse;
			
			float offset = parentBuffer.getSecLen() - getSecOffset();
			stop();
			if(doReverse)
		        setBuffer(parentBuffer.getReverseBuffer());
			else 
		        setBuffer(parentBuffer.getBuffer());
			setSecOffset(offset);
			play();
		}
	}
	
	public void update() {
		if(effect == FX_DIZZY) {
			wobbleDirSpeedDir += 1;
			wobbleDir += Math.abs(Math2D.calcLenY(wobbleDirSpeedMax,wobbleDirSpeedDir));
		}
		
		fade(fadeToAmt);

		System.out.println(this.getParentBuffer().getName() + ": " + fadeAmount + ", " + fadeToAmt);

		if(fadeAmount < .01 && newBuffer != null) {
			setBuffer(newBuffer);
			
			if(fadeIn) {
				setFadeAmount(0);
				fadeTo(1);
			}
			else {
				setFadeAmount(1);
				fadeTo(1);
			}
			
			newBuffer = null;
		}
		

		
		setALSpeed();
	}
	
	public void setSpeed(float newSpeed) {
		
		float prevSpeed = speed;
		
		speed = newSpeed;
		if(prevSpeed <= 0 && speed > 0)
			reverse(false);
		else if(prevSpeed >= 0 && speed < 0)
			reverse(true);
		setALSpeed();
	}
	private void setALSpeed() {
		float calcSpeed;
		calcSpeed = Math.abs(speed);
		if(effect == FX_DIZZY)
			calcSpeed += Math2D.calcLenY(wobbleSize,wobbleDir);
		
		al().alSourcef(sourceID[0], AL.AL_PITCH, calcSpeed);
	}
	
	
	
	public void setSecOffset(float secondsOffset) {
		al().alSourcef(sourceID[0], AL.AL_SEC_OFFSET, secondsOffset);
	}
	public float getSecOffset() {
		float[] loc = new float[1];
		al().alGetSourcef(sourceID[0], AL.AL_SEC_OFFSET, loc, 0);
		return loc[0];
	}
	
	public void setByteOffset(int byteOffset) {
		al().alSourcei(sourceID[0], AL.AL_BYTE_OFFSET, byteOffset);
	}
	public int getByteOffset() {
		int[] loc = new int[1];
		al().alGetSourcei(sourceID[0], AL.AL_BYTE_OFFSET, loc, 0);
		return loc[0];
	}
	
	public void setOffsetFraction(float f) {
		setSecOffset((int) (f*parentBuffer.getSecLen()));
	}
	public float getOffsetFraction() {
		return 1f*getSecOffset()/parentBuffer.getSecLen();
	}
	
	public int getAmplitude() {return parentBuffer.getAmplitude(getByteOffset());}
	public float getAmplitudeFraction() {return parentBuffer.getAmplitudeFraction(getByteOffset());}

	public float getFilteredAmplitudeFraction() {
		return parentBuffer.getFilteredAmplitudeFraction(getByteOffset());
	}
	public boolean isReversed() {
		return isReversed;
	}
	public void setLoop(boolean isLooping) {
		al().alSourcei(sourceID[0], AL.AL_LOOPING, isLooping ? 1 : 0);
	}
	public float getFadeAmount() {
		return fadeAmount;
	}
	public void fadeTo(float amt) {
		System.out.println("FADETO: " + getParentBuffer().getName() + ", " + amt);
		fadeToAmt = MathExt.contain(0,amt,1);
	}
	public boolean wasNeverPlayed() {
		return wasNeverPlayed;
	}

	public void shift(SoundBuffer newMusic, boolean fadeOut, boolean fadeIn) {		
		fadeTo(0);
		
		if(!fadeOut)
			fadeAmount = 0;
		
		newBuffer = newMusic;
		this.fadeIn = fadeIn;
	}
}
