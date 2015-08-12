package resource.sound;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;
import com.jogamp.openal.util.WAVData;
import com.jogamp.openal.util.WAVLoader;

import cont.GameController;
import datatypes.vec3;
import fl.FileExt;
import functions.Math2D;
import gfx.GOGL;

public class Sound {
	private static Map<String, SoundBuffer> bufferMap = new HashMap<String, SoundBuffer>();
    private static AL al = ALFactory.getAL();

    private static float listenerX, listenerY, listenerZ;
    
    private static SoundSource curMusic = null, newMusic = null;
    private static List<SoundSource> sourceList = new ArrayList<SoundSource>();
	private static List<SoundBuffer> bufferList = new ArrayList<SoundBuffer>();
    
	
	public static void ini() {
		
		//Initialize OpenAL
		ALut.alutInit();

        //LOAD SOUNDS
		loadSound("BlockCrumble", "Resources/Sounds/FX/sndBlockCrumble.wav");
        loadSound("Footstep", "Resources/Sounds/FX/sndFootstep.wav",.5f);
        loadSound("Spin", "Resources/Sounds/FX/sndSpin.wav");
        loadSound("Jump", "Resources/Sounds/FX/sndJump.wav", 50); 

        loadSound("blipMale", "Resources/Sounds/FX/sndBlipMale.wav", 80); 
        
		loadSound("Overworld", "Resources/Sounds/Music/overworld.wav", .05f, true);
		loadSound("Godot", "Resources/Sounds/Music/godot.wav", .05f, true);		
		loadSound("courtBegins", "Resources/Sounds/Music/courtBegins.wav", .05f, true);
				
		playMusic("Overworld");
	}
	
	public static SoundBuffer get(String name) {
		return bufferMap.get(name);
	}

	private static void playMusic(String name) {playMusic(get(name));}
	private static void playMusic(SoundBuffer music) {
		if(curMusic == null) {
			curMusic = new SoundSource(music, true);
			curMusic.setFadeAmount(0);
			curMusic.play();
			newMusic = null;
		}
		else if(curMusic.getParentBuffer() != music)
			newMusic = new SoundSource(music, true);
	}

	public static void update() {
		
		
		// UPDATE MUSIC
		// If newMusic is not NULL, fade out current music then fade in new music.
		// Otherwise, fade in new music;
		if(newMusic != null) {
			
			curMusic.fade(0);
			if(curMusic.getVolume() == 0) {
				// Stop Old Music
				curMusic.destroy();

				// Switch Music
				curMusic = newMusic;
				newMusic = null;

				curMusic.setFadeAmount(0);
				curMusic.play();
			}
		}
		else
			curMusic.fade(1);	
	}
	
	
	public static void duckMusic(SoundSource other, float frac) {
		curMusic.setFadeAmount(frac);
		other.setFadeAmount(1-frac);
	}
	public static void setMusicVolume(float volumePerc) {
		curMusic.setFadeAmount(volumePerc);
	}
	
	
	
	public static void clean() {
		List<SoundSource> toRemove = new ArrayList<SoundSource>();

		for(SoundSource src : sourceList)
			if(src.isStopped())
				toRemove.add(src);
		for(SoundSource src : toRemove) {
			src.destroy();
			sourceList.remove(src);
		}
	}
		
		
	public static void loadSound(String name, String fileName) {loadSound(name,fileName,1,false);}
	public static void loadSound(String name, String fileName, float volume) {loadSound(name,fileName,volume,false);}
	public static void loadSound(String name, String fileName, boolean loop) {loadSound(name,fileName,1,loop);}
	public static void loadSound(String name, String fileName, float volume, boolean loop) {
		SoundBuffer snd = SoundLoader.load(fileName);

		snd.setVolume(volume);
		
		bufferMap.put(name, snd);
		bufferList.add(snd);
	}
	
	
	public static void updateListener(double cX, double cY, double cZ, double vX, double vY, double vZ, double nDirX, double nDirY, double nDirZ, double nUpX, double nUpY, double nUpZ) {
		// Position of the listener.
	    float[] listenerPos = {(float) cX, (float) cY, (float) cZ};

	    // Velocity of the listener.
	    float[] listenerVel = {(float) vX, (float) vY, (float) vZ};

	    // Orientation of the listener. (first 3 elements are "at", second 3 are "up")
	    float[] listenerOri = {(float) nDirX, (float) nDirY, (float) nDirZ,  (float) nUpX, (float) nUpY, (float) nUpZ};
		
		al.alListenerfv(AL.AL_POSITION,	listenerPos, 0);
	    al.alListenerfv(AL.AL_VELOCITY,    listenerVel, 0);
	    al.alListenerfv(AL.AL_ORIENTATION, listenerOri, 0);
	}
	
	public static void unload() {
		for(SoundSource s : sourceList)
			s.destroy();
		for(SoundBuffer s : bufferList)
			s.destroy();
		try {
			ALut.alutExit();
		}
		catch(Exception e) {
		}
	}

	
	public static SoundSource play(String name) {
		return play(name, listenerX, listenerY, listenerY, 0,0,0, false);
	}
	public static SoundSource play(String name, boolean doLoop) {
		return play(name, listenerX, listenerY, listenerY, 0,0,0, doLoop);
	}
	public static SoundSource play(String name, double x, double y, double z, double vX, double vY, double vZ) {
		return play(name, x,y,z, vX,vY,vZ, false);
	}
	public static SoundSource play(String name, double x, double y, double z, double vX, double vY, double vZ, boolean doLoop) {
		
	    float[] sourcePos = {(float) x, (float) y, (float) z};
	    float[] sourceVel = {(float) vX, (float) vY, (float) vZ};

        SoundSource src = new SoundSource(get(name), sourcePos, sourceVel, doLoop);
        src.play();
        
        sourceList.add(src);
        
        return src;
	}
	
	
	
	public static AL al() {return al;}

	public static vec3 getListenerPosition() {return new vec3(listenerX,listenerY,listenerZ);}
	public static vec3 getListenerVelocity() {return new vec3(0,0,0);}
	
	
	private static class SoundLoader {
		public static SoundBuffer load(String fileName) {return load(fileName,1);}
		public static SoundBuffer load(String fileName, float volume) {
			ByteBuffer[] data = new ByteBuffer[1];
		    int[] 	buffer = new int[1],
		    		format = new int[1],
		    		size = new int[1],
		    		freq = new int[1],
		    		loop = new int[1];

	        // Load wav data into a buffer.
	        al.alGenBuffers(1, buffer, 0);

	        //if(fileName.endsWith(".wav"))
			try {
			    WAVData wd = WAVLoader.loadFromStream(FileExt.get(fileName));
			    format[0] = wd.format;
			    data[0] = wd.data;
			    size[0] = wd.size;
			    freq[0] = wd.freq;
			    loop[0] = wd.loop ? AL.AL_TRUE : AL.AL_FALSE;
			} catch (Exception e) {
			    throw new ALException(e);
			}
	        
	        
	        al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);
	        	
	        SoundBuffer buf = new SoundBuffer(buffer, format, size, freq);
	        
	        // Do another error check and return.
	        if(al.alGetError() != AL.AL_NO_ERROR) {
	            System.err.println("Failed to load " + fileName + ". Exiting program.");
	            System.exit(2);
	        }
	        
	        return buf;
		}
	}
}
