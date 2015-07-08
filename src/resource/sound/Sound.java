package resource.sound;

import com.jogamp.openal.*;
import com.jogamp.openal.util.*;

import fl.FileExt;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class Sound {
	boolean valid;
	
	
	float volume = 1;
	
    // Buffers hold sound data.
    int[] buffer = new int[1];
    // Sources are points emitting sound.
    List<int[]> source = new ArrayList<int[]>();

    
    int[] format = new int[1];
    int[] size = new int[1];
    ByteBuffer[] data = new ByteBuffer[1];
    int[] freq = new int[1];
    int[] loop = new int[1];
    
    
    

    public Sound(AL al, String fileName) {
        valid = !(loadALData(al, fileName) == AL.AL_FALSE);
	}
    
	public Sound(AL al, String fileName, float volume) {
        valid = !(loadALData(al, fileName) == AL.AL_FALSE);
        this.volume = volume;
	}
	

	private int loadALData(AL al, String fileName) {
        // variables to load into
   

        // Load wav data into a buffer.
        al.alGenBuffers(1, buffer, 0);
        if(al.alGetError() != AL.AL_NO_ERROR)
            return AL.AL_FALSE;


        ALut.alutLoadWAVFile(FileExt.get(fileName), format, data, size, freq, loop);
        al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);

 
        // Do another error check and return.
        if(al.alGetError() == AL.AL_NO_ERROR)
            return AL.AL_TRUE;

        return AL.AL_FALSE;
    }


	    /*static void killALData() {
	        al.alDeleteBuffers(1, buffer, 0);
	        al.alDeleteSources(1, source, 0);
	        ALut.alutExit();
	    }*/
	    
	
	public void playSound(AL al, int[] src) {
        al.alSourcePlay(src[0]);
	}
	public void playSound(AL al, int index) {
        al.alSourcePlay(source.get(index)[0]);
	}

	
	public int[] playSound(AL al, double x, double y, double z, double vX, double vY, double vZ, boolean loop) {
		int[] src = new int[1];
		
		
		// Position of the source sound.
	    float[] sourcePos = {(float) x, (float) y, (float) z};

	    // Velocity of the source sound.
	    float[] sourceVel = {(float) vX, (float) vY, (float) vZ};
	    
	    
		
        // Bind buffer with a source.
        al.alGenSources(1, src, 0);
        
        al.alSourcei (src[0], AL.AL_BUFFER,   buffer[0]   );
        al.alSourcef (src[0], AL.AL_PITCH,    1.0f);
        al.alSourcef (src[0], AL.AL_GAIN,     volume);
        al.alSourcefv(src[0], AL.AL_POSITION, sourcePos, 0);
        al.alSourcefv(src[0], AL.AL_VELOCITY, sourceVel, 0);

        if(loop)
        	al.alSourcei (src[0], AL.AL_LOOPING,  1);
        else
        	al.alSourcei (src[0], AL.AL_LOOPING,  0);

        source.add(src);
        //ages.add(0);
        
        
        playSound(al, src);
        
        return src;
	}
	    
	public void pauseSound(AL al, int index) {
        al.alSourcePause(source.get(index)[0]);
	}

	public void stopSound(AL al, int index) {
        al.alSourceStop(source.get(index)[0]);
	}
	
	
	public void clean(AL al) {
		List<int[]> toRemove = new ArrayList<int[]>();
		
		int[] src;
		for(int i = 0; i < source.size(); i++) {
			
			src = source.get(i);			
			
			int[] state = new int[1];
			    
			al.alGetSourcei(src[0], AL.AL_SOURCE_STATE, state, 0);
			    
			if(state[0] != AL.AL_PLAYING) {
				toRemove.add(src);
			}
		}
		
		for(int[] done : toRemove) {
			al.alDeleteSources(1, done, 0);
			source.remove(done);
		}
	}

	public int getNumPlaying() {
		return source.size();
	}

	public void kill(AL al) {
		al.alDeleteBuffers(1, buffer, 0);
		for(int[] s : source)
			al.alDeleteSources(1, s, 0);
	}

	public void killSource(AL al, int[] src) {
		if(source.contains(src)) {
			al.alDeleteSources(1, src, 0);
			source.remove(src);
		}
	}
}
