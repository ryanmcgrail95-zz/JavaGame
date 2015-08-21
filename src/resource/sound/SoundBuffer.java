package resource.sound;

import com.jogamp.openal.*;
import com.jogamp.openal.util.*;
import com.jogamp.opengl.util.texture.Texture;

import datatypes.vec2;
import fl.FileExt;
import functions.Math2D;
import functions.MathExt;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class SoundBuffer {
	private float volume = 1;
	private ByteBuffer data;
	private String name,fileName;
    private int[] buffer = new int[1],
    		reverseBuffer = new int[1],
    		format = new int[1],
    		size = new int[1],
    		freq = new int[1];
    private int avg, min = 10000000, max = -1000000;

    private String albumName;
	private Texture albumImg;
    
	public SoundBuffer(String name,String fileName, int[] buffer, int[] reverseBuffer, int[] format, ByteBuffer data, int[] size, int[] freq, float volume) {
		setAll(name,fileName, buffer,reverseBuffer,format,data,size,freq);
		this.volume = volume;
	}
	public SoundBuffer(String name,String fileName, int[] buffer, int[] reverseBuffer,  int[] format,  ByteBuffer data, int[] size, int[] freq) {
		setAll(name,fileName, buffer,reverseBuffer,format,data,size,freq);
	}
	
	
	public void setAll(String name,String fileName, int[] buffer, int[] reverseBuffer,  int[] format,  ByteBuffer data, int[] size, int[] freq) {
		this.name = name;
		this.fileName = fileName;
		this.buffer = buffer;
		this.reverseBuffer = reverseBuffer;
		this.format = format;
		this.data = data;
		this.size = size;
		this.freq = freq;
		
		getValues();
	}
	
	private static AL al() {return Sound.al();}


	public void destroy() {
		al().alDeleteBuffers(1, buffer, 0);
	}

	
	public int getFormat() {return format[0];}

	public ByteBuffer getData() {return data;}
	public int[] getBuffer() 	{return buffer;}
	public int[] getReverseBuffer() {return reverseBuffer;}
	
	public float getVolume() 	{return volume;}
	public void setVolume(float volume) {
		this.volume = volume;
	}
	
	public void setAlbumImage(Texture albumImage) {this.albumImg = albumImage;}
	public Texture getAlbumImage() {return albumImg;}
	
	
	public void getValues() {
		int packetNum, packetSize;
		packetNum = getPacketNum();
		packetSize = getPacketSize();
		
		int a;
		int n = 0;
		for(int i = 0; i < packetNum; i += packetSize) {
			a = Math.abs(getAmplitude(i));
			
			min = Math.min(min,a);
			max = Math.max(max,a);

			avg += 1f*a/Short.MAX_VALUE;
		}
		
		avg /= packetNum;
	}

	
	public int getAmplitude(int byteOffset) {		
		if(getBytes() == 1)
			return getData().get(byteOffset);
		else {
			return getData().getShort(byteOffset+1);
		}
	}
	
	public float getAmplitudeFraction(int byteOffset) {
		return 1f*getAmplitude(byteOffset)/Short.MAX_VALUE;
	}
	
	public int getFilteredAmplitude(int byteOffset) {
		int i = getAmplitude(byteOffset);
		
		return (int) ( 1f*Math.max(0,i - avg)/(max-avg)*Short.MAX_VALUE );
	}
	public float getFilteredAmplitudeFraction(int byteOffset) {
		return 1f*getFilteredAmplitude(byteOffset)/Short.MAX_VALUE;
	}
	
	
	
	public int getChannels() {
		int[] channels = new int[1];
		al().alGetBufferi(buffer[0], AL.AL_CHANNELS, channels, 0);
		return channels[0];
	}
	
	
	public int getPacketSize() {return getChannels()*getBytes();}
	public int getPacketNum() {return getByteLen() / getPacketSize();}
	
	public int getBits() {
		int[] bits = new int[1];
		al().alGetBufferi(buffer[0], AL.AL_BITS, bits, 0);
		return bits[0];
	}
	public int getBytes() {return getBits()/8;}
	public int getByteLen() {
		int[] sizeInBytes = new int[1];
		al().alGetBufferi(buffer[0], AL.AL_SIZE, sizeInBytes, 0);
		return sizeInBytes[0];
	}

	public float getSecLen() {
		int[] frequency = new int[1];

		al().alGetBufferi(buffer[0], AL.AL_FREQUENCY, frequency, 0);

		return 1f*getPacketNum()/frequency[0];
	}
	
	public String getName() {return name;}
	public String getFileName() {return fileName;}
	
	public void setAlbumName(String albumName) {this.albumName = albumName;}
	public String getAlbumName() {return albumName;}
}