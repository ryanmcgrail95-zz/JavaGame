/* TODO: ADD SOURCE QUEUING WITH:
 * http://stackoverflow.com/questions/5885114/how-to-play-a-sound-instantly-after-another-sound-ended
 * alGetSourcei(source, AL_BUFFERS_PROCESSED, &num_done)
 * alSourceQueueBuffers 
 * alSourceUnqueueBuffers
*/


package resource.sound;

import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcraft.oggdecoder.OggData;
import com.jogamp.openal.AL;
import com.jogamp.openal.ALException;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;
import com.jogamp.openal.util.WAVData;
import com.jogamp.openal.util.WAVLoader;
import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import ds.vec3;
import ds.lst.CleanList;
import fl.FileExt;
import functions.Array;
import functions.ArrayMath;
import gfx.Camera;
import gfx.ErrorPopup;
import object.primitive.Positionable;

public class Sound {
	private static Map<String, SoundBuffer> bufferMap = new HashMap<String, SoundBuffer>();
    private static AL al = ALFactory.getAL();

    private final static float[]
    	velArray = {0,0,0},
    	upArray = {0,0,1};
    
    private static Positionable
    	listenerObject;
    private static float listenerX, listenerY, listenerZ;
    
    private static SoundSource curMusic = null, newMusic = null;
    private static SoundBuffer afterMusic = null;
    private static CleanList<SoundSource> sourceList = new CleanList<SoundSource>("Snd Src");
	private static List<SoundBuffer> bufferList = new ArrayList<SoundBuffer>();
	private static List<String> musicList = new ArrayList<String>();
    private static Map<String, List<String>> playlistMap = new HashMap<String, List<String>>();
	
    
	public static void ini() {		
		boolean success = false;
		
		do {
			try {
				ALut.alutInit();
				success = true;
			} catch(Exception e) {
				System.out.println("Failed to initialize ALUT.");
				success = false;
				//ErrorPopup.open("Failed to initialize ALUT.", true);
			}
		} while(!success);
	}
	public static void iniLoad() {

        //LOAD SOUNDS
		//loadSound("button", "Resources/Sounds/FX/button.ogg",80);
		loadSound("Resources/Sounds/FX/blockCrumble.ogg");
        loadSound("Resources/Sounds/FX/footstep.ogg",10); //.15f
        loadSound("Resources/Sounds/FX/spin.ogg");
        loadSound("Resources/Sounds/FX/jump.ogg", 70);
        loadSound("Resources/Sounds/FX/blipMale.ogg", 80);

		loadSound("Resources/Sounds/FX/fireAttack.ogg",90);
        
		loadSound("Resources/Sounds/FX/enemyDie.ogg",110);
		
		loadSound("Resources/Sounds/FX/dodge.ogg",50);
		loadSound("Resources/Sounds/FX/dodgeCrunch.ogg",80);
		loadSound("Resources/Sounds/FX/attack.ogg",200);

		//loadSound("ampTest", "Resources/Sounds/Music/ampTest.ogg", 1, true);
		//loadSound("overworld", "Resources/Sounds/Music/overworld.ogg", .05f, true);
		//loadSound("Resources/Sounds/Music/ffOverworld.ogg", .05f, true);
		//loadSound("ffOverworld", "Resources/Sounds/Music/ffOverworld.ogg", .05f, true);
		//loadDirectory("Resources/Sounds/Music/Ace Attorney/", .05f);
	}
	
	public static SoundBuffer get(String name) {
		return get(name, .05f);
	}
	public static SoundBuffer get(String name, float volume) {
		if(bufferMap.containsKey(name))
			return bufferMap.get(name);
		else
			return loadTemp("Resources/Sounds/Music/" + name + ".ogg", volume);
	}

	public static void playMusic(String name) {playMusic(get(name));}
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

	
	public static void playMusic(String music, float volume, boolean fadeOut, boolean fadeIn) {
		playMusic(get(music, volume), volume, fadeOut, fadeIn);
	}
	private static void playMusic(SoundBuffer music, float volume, boolean fadeOut, boolean fadeIn) {
		if(curMusic == null) {
			curMusic = new SoundSource(music, false);
			if(fadeIn)
				curMusic.setFadeAmount(0);
			if(!musicLock)
				curMusic.play();
			newMusic = null;
		}
		else if(curMusic.getParentBuffer() != music) {
			newMusic = null;
			curMusic.shift(music, fadeOut, fadeIn);
		}
	}

	public static void update() {
		
		if(true)
			return;
		
		// UPDATE MUSIC
		// If newMusic is not NULL, fade out current music then fade in new music.
		// Otherwise, fade in new music;
		
		if(curMusic != null)
			if(curMusic.wasNeverPlayed())
				curMusic.play();
		
		if(afterMusic != null) {
			if(!curMusic.isALPlaying()) {
				curMusic.destroy();
				curMusic = new SoundSource(afterMusic, true);
				curMusic.setFadeAmount(1);
				curMusic.play();
				
				afterMusic = null;
			}
		}
		
		if(newMusic != null) {
			
			curMusic.fadeTo(0);
			if(curMusic.getFadeAmount() <= .01) {
				// Stop Old Music
				curMusic.destroy();

				// Switch Music
				curMusic = newMusic;
				newMusic = null;

				curMusic.setFadeAmount(0);
				curMusic.play();
				curMusic.fadeTo(1);
			}
		}
		
		if(curMusic != null)
			curMusic.update();
		if(newMusic != null)
			newMusic.update();
	}
	
	
	public static void duckMusic(SoundSource other, float frac) {
		curMusic.setFadeAmount(frac);
		other.setFadeAmount(1-frac);
	}
	public static void setMusicVolume(float volumePerc) {
		curMusic.setFadeAmount(volumePerc);
	}
	
	
	
	public static void clean() {
		for(SoundSource src : sourceList) {
			src.update();
			
			if(src.isDestroyed())
				sourceList.remove(src);
			else if(src.isALStopped()) {
				src.destroy();
				sourceList.remove(src);
			}
		}
	}
		
	public static SoundBuffer loadSound(String fileName) {return loadSound(fileName,1,false);}
	public static SoundBuffer loadSound(String fileName, float volume) {return loadSound(fileName,volume,false);}
	public static SoundBuffer loadTemp(String fileName, float volume) {return loadSound(fileName,volume,false,true);}
	public static SoundBuffer loadSound(String fileName, boolean loop) {return loadSound(fileName,1,loop);}
	public static SoundBuffer loadSound(String fileName, float volume, boolean loop) {return loadSound(fileName,volume,loop,false);}
	public static SoundBuffer loadSound(String fileName, float volume, boolean loop, boolean isTemporary) {
		SoundBuffer snd = new SoundBuffer(fileName, false);

		snd.setVolume(volume);
		
		bufferMap.put(snd.getName(), snd);
		bufferList.add(snd);
		
		if(loop)
			musicList.add(snd.getName());
		
		return snd;
	}
	
	public static void loadDirectory(String dirName, float volume) {
		File[] fileList = FileExt.getSubfiles(dirName);
		
		String albumName = FileExt.getFile(dirName).getName();
		Texture albumImg = null;
		
		List<String> playlist = new ArrayList<String>();
		SoundBuffer buff;
		
		String fName, fPath, suffix;
		// Find Album Cover
		for(File file : fileList) {
			fName = file.getName();
			fPath = file.getPath();
			suffix = FileExt.getSuffix(fName);

			if(suffix.equals("jpg")|| suffix.equals("png")) {
				albumImg = TextureController.load(fPath, fName, TextureController.M_NORMAL).getFrame(0);
				break;
			}
		}

		// Load Sounds
		for(File file : fileList) {
			fName = file.getName();
			fPath = FileExt.fixSlashes(file.getPath());
			
			if(suffix.equals("ogg") || suffix.equals("wav")) {
				buff = loadSound(fPath, volume, true);
					buff.setAlbumName(albumName);
					buff.setAlbumImage(albumImg);
					playlist.add(buff.getName());
			}
		}
		
		playlistMap.put(albumName, playlist);
	}
	
	
	public static void updateListener(Camera camera) {
		listenerObject = camera;
		//updateListener(camera.getPosition(), velArray, camera.getNormal(), upArray);
	}
	public static void updateListener(double cX, double cY, double cZ, double vX, double vY, double vZ, double nDirX, double nDirY, double nDirZ, double nUpX, double nUpY, double nUpZ) {
		// Position of the listener.
	    float[] listenerPos = {(float) cX, (float) cY, (float) cZ},
	    		listenerVel = {(float) vX, (float) vY, (float) vZ},
	    		listenerOri = {(float) nDirX, (float) nDirY, (float) nDirZ,  (float) nUpX, (float) nUpY, (float) nUpZ};	
	    updateListener(listenerPos,listenerVel,listenerOri);
	}
	public static void updateListener(float[] pos, float[] vel, float[] norm, float[] up) {
	    updateListener(pos,vel, Array.concat(norm,up));
	}
	public static void updateListener(float[] pos, float[] vel, float[] norm) {
		al.alListenerfv(AL.AL_POSITION,	pos, 0);
	    al.alListenerfv(AL.AL_VELOCITY, vel, 0);
	    al.alListenerfv(AL.AL_ORIENTATION, norm, 0);
	}
	
	public static void unload() {
		for(SoundSource s : sourceList)
			s.destroy();
		for(SoundBuffer s : bufferList)
			s.destroy();
		
		try {
			ALut.alutExit();
		}
		catch(Exception e) {}
	}

	
	public static SoundSource play(String name) {
		return play(name, false);
	}
	public static SoundSource play(String name, boolean doLoop) {
		return play(name, null, doLoop);
	}
	public static SoundSource play(String name, Positionable sourceObject) {
		return play(name, sourceObject, false);
	}
	public static SoundSource play(String name, Positionable sourceObject, boolean doLoop) {
        SoundSource src = new SoundSource(get(name), sourceObject, doLoop);
        src.play();
        
        sourceList.add(src);
        
        return src;
	}

	
	public static SoundSource play(SoundBuffer sb) {
		return play(sb, false);
	}
	public static SoundSource play(SoundBuffer sb, boolean doLoop) {
		return play(sb, null, doLoop);
	}
	public static SoundSource play(SoundBuffer sb, Positionable sourceObject) {
		return play(sb, sourceObject, false);
	}
	public static SoundSource play(SoundBuffer sb, Positionable sourceObject, boolean doLoop) {
        SoundSource src = new SoundSource(sb, sourceObject, doLoop);
        src.play();
        
        sourceList.add(src);
        
        return src;
	}

	
	
	public static AL al() {return al;}
	
	public static class Loader {
		public static void loadInto(SoundBuffer s) {
			ByteBuffer[] data = new ByteBuffer[1];
		    int[] 	buffer = new int[1],
		    		reverseBuffer = new int[1],
		    		format = new int[1],
		    		size = new int[1],
		    		freq = new int[1],
		    		loop = new int[1];

	        // Load wav data into a buffer.
	        al.alGenBuffers(1, buffer, 0);
	        al.alGenBuffers(1, reverseBuffer, 0);


	        String name, fileName;
	        name = s.getName();
	        fileName = s.getFileName();

	        
	        InputStream inputStream = FileExt.get(fileName);
	        BufferedInputStream buffStream = new BufferedInputStream(inputStream);
	        
	        try {
	        	if(fileName.endsWith(".wav")) {
				    WAVData wd = WAVLoader.loadFromStream(buffStream);
				    format[0] = wd.format;
				    data[0] = wd.data;
				    size[0] = wd.size;
				    freq[0] = wd.freq;
				    loop[0] = wd.loop ? AL.AL_TRUE : AL.AL_FALSE;
	        	}
	        	else {
	        		OggData od = (new OggDecoder()).getData(buffStream);
	        		format[0] = od.format;
	        		data[0] = od.data;
				    size[0] = od.data.capacity();
				    freq[0] = od.rate;
	        		loop[0] = AL.AL_FALSE;
	        	}
	        	
	        	buffStream.close();
	        	inputStream.close();
			} catch (Exception e) {
				ErrorPopup.open("Error opening file \""+fileName+"\".", false);
			    throw new ALException(e);
			}
	        	        
	        
	        al.alBufferData(buffer[0], format[0], data[0], size[0], freq[0]);
			reverseBuffer(format,data);
			al.alBufferData(reverseBuffer[0], format[0], data[0], size[0], freq[0]);
			reverseBuffer(format,data);
	        	
	        
			s.setAll(buffer, reverseBuffer, format, data[0], size, freq);
	        	        
	        // Do another error check and return.
	        if(al.alGetError() != AL.AL_NO_ERROR) {
	            System.err.println("Failed to load " + fileName + ". Exiting program.");
	            System.exit(2);
	        }	        
		}
	}
	
	
	public static void reverseBuffer(int[] format, ByteBuffer[] data) {
		int bytes = 0, channels = 0;
		switch(format[0]) {
			case AL.AL_FORMAT_MONO8:
				bytes = 1;
				channels = 1;	break;
			case AL.AL_FORMAT_MONO16:
				bytes = 2;
				channels = 1;	break;
			case AL.AL_FORMAT_STEREO8:
				bytes = 1;
				channels = 2;	break;
			case AL.AL_FORMAT_STEREO16:
				bytes = 2;
				channels = 2;	break;
		}
		
		
		data[0].rewind();
		int len = data[0].remaining(), revI = len-1;

		ByteBuffer buff = data[0],
			newBuffer = ByteBuffer.allocate(len);		
				
		if(bytes == 2)
	        for (int i = 0; i < len; i += 2) {
	        	newBuffer.put(revI-1,buff.get(i));
	        	newBuffer.put(revI,buff.get(i+1));
	        	
	        	revI -= 2;
	        }
        
        buff.clear();
        data[0] = newBuffer;
	}
	public static List<String> getPlaylist(String s) {
		return playlistMap.get(s);
	}
	
	public static List<String> getMusicList() {
		return musicList;
	}
	public static boolean isPlaying(String name) {
		return getSourceNumber(name) > 0;
	}	
	public static boolean isPlaying(SoundBuffer sb) {
		return isPlaying(sb.getName());
	}

	public static int getSourceNumber(String name) {
		int sourceNumber = 0;
		for(SoundSource src : sourceList)
			if(src.getParentBuffer().getName() == name)
				sourceNumber++;
		return sourceNumber;
	}	
	public static int getSourceNumber(SoundBuffer sb) {
		return getSourceNumber(sb.getName());
	}

	
	public static int getBufferNumber() {
		return bufferList.size();
	}
	public static int getSourceNumber() {
		return sourceList.size();
	}
	public static void fadeMusic(float frac) {
		curMusic.fadeTo(frac);
	}

	public static Positionable getListenerObject() {
		return listenerObject;
	}
}
