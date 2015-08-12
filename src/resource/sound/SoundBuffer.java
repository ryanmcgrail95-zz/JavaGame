package resource.sound;

import com.jogamp.openal.*;
import com.jogamp.openal.util.*;

import fl.FileExt;
import functions.Math2D;
import functions.MathExt;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class SoundBuffer {
	private float volume = 1;
    private int[] buffer = new int[1],
    		format = new int[1],
    		size = new int[1],
    		freq = new int[1];
    private int sourceNum = 0;
    
    
	public SoundBuffer(int[] buffer, int[] format, int[] size, int[] freq, float volume) {
		setAll(buffer,format,size,freq);
		this.volume = volume;
	}
	public SoundBuffer(int[] buffer, int[] format, int[] size, int[] freq) {
		setAll(buffer,format,size,freq);
	}
	
	public void setAll(int[] buffer, int[] format, int[] size, int[] freq) {
		this.buffer = buffer;
		this.format = format;
		this.size = size;
		this.freq = freq;
	}
	
	private static AL al() {
		return Sound.al();
	}


	public void destroy() {
		al().alDeleteBuffers(1, buffer, 0);
	}
	
	
	public void reverse() {
	}

	
	public int[] getBuffer() 	{return buffer;}
	public float getVolume() 	{return volume;}
	public void setVolume(float volume) {
		this.volume = volume;
	}
}