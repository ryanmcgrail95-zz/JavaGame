package resource.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import cont.GameController;

public class SoundController {
	private static List<Sound> soundList = new ArrayList<Sound>();
	private static Map<String, Sound> soundMap = new HashMap<String, Sound>();
    private static AL al = ALFactory.getAL();

    private static float listenerX, listenerY, listenerZ;
    
    
	
	public static void ini() {
		
		//Initialize OpenAL
		ALut.alutInit();
        

        //LOAD SOUNDS
		loadSound("BlockCrumble", "Resources/Sounds/FX/sndBlockCrumble.wav");
        loadSound("Footstep", "Resources/Sounds/FX/sndFootstep.wav");
        loadSound("Spin", "Resources/Sounds/FX/sndSpin.wav");
        loadSound("Jump", "Resources/Sounds/FX/sndJump.wav", 50); 
        
		loadSound("Overworld", "Resources/Sounds/Music/overworld.wav", .125f);
		
		
		loopSound("Overworld");
        
        //Ensure that OpenAL Devices are Closed After Program Ends
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(
            new Thread(
                new Runnable() {
                    public void run() {
                        kill();
                    }
                }
            )
        );
	}
	
	
	
	public static void clean() {		
		for(Sound s : soundList)
			s.clean(al);
	}
	
	public static int getNumPlaying() {
		int numPlaying = 0;
		
		for(Sound s : soundList)
			numPlaying += s.getNumPlaying();
		
		return numPlaying;
	}
	
	
	public static void loadSound(String name, String fileName) {
		Sound snd = new Sound(al, fileName);
		
		soundMap.put(name, snd);
		soundList.add(snd);
	}
	
	public static void loadSound(String name, String fileName, float volume) {
		Sound snd = new Sound(al, fileName, volume);
		
		soundMap.put(name, snd);
		soundList.add(snd);
	}
	
	
	public static int[] playSound(String name, double x, double y, double z, double vX, double vY, double vZ, boolean loop) {
		Sound snd = soundMap.get(name);
		
		if(snd != null)
			return snd.playSound(al, x, y, z, vX, vY, vZ, loop);
		else {
			System.out.println("Failed to play sound \"" + name + "\".");
			return null;
		}
	}
	
	public static int[] playSound(String name) {
		return playSound(name, listenerX, listenerY, listenerZ, 0, 0, 0, false);
	}
	
	public static int[] loopSound(String name) {
		return playSound(name, listenerX, listenerY, listenerZ, 0, 0, 0, true);
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
	
	public static void kill() {
		for(Sound s : soundList)
			s.kill(al);
		ALut.alutExit();
	}



	public static void killSource(String name, int[] source) {
		soundMap.get(name).killSource(al, source);
	}
}
