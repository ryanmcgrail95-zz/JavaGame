package com.jcraft.oggdecoder;

import java.nio.ByteBuffer;

public class OggData {
	/** The data that has been read from the OGG file */
	public ByteBuffer data;
	
	/** The sampling rate */
	public int rate, format, channels;
	
	/** The number of channels in the sound file */
}