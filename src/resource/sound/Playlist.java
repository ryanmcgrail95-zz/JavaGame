package resource.sound;

import io.Keyboard;
import io.Mouse;

import java.util.ArrayList;
import java.util.List;

import object.primitive.Updatable;
import datatypes.lists.CleanList;

public class Playlist extends Updatable {
	private SoundSource source;
	private List<String> list;
	private int index;
	private boolean isLooping = false;
	private float speed = 1;
	
	public Playlist(String... list) {
		name = "Playlist";

		this.list = new ArrayList<String>();
		
		List<String> playlist;
		for(String s : list) {
			playlist = Sound.getPlaylist(s);
			if(playlist == null)
				this.list.add(s);
			else for(String pS : playlist)
				this.list.add(pS);
		}
		
		source = Sound.play(this.list.get(index = 0));
		source.pause();
	}
	
	public SoundSource getSource() {return source;}
	

	public void update() {
		if(source.isPlaying() && !source.isALPlaying()) {
			if(!isLooping) {
				if(speed >= 0)
					playNextSong();
				else
					playPreviousSong();
			}
		}
	}
	
	public List<String> getList() {return list;}
	public int size() {return list.size();}

	public void play() 				{source.play();}
	public void play(int i) 		{source.swapSoundBuffer(list.get(index = getIndex(i)));}
	public void playNextSong() 		{play(getNextIndex());}
	public void playPreviousSong() 	{play(getPreviousIndex());}

	public void setPosition(float x, float y, float z) 	{source.setPosition(new float[] {x,y,z});}	
	public void setSpeed(float speed) 					{source.setSpeed(this.speed = speed);}
	public void setLoop(boolean isLooping) 				{source.setLoop(this.isLooping = isLooping);}
	public void setVolume(float volumeFrac) 			{source.setVolume(volumeFrac);}

	public String getName(int i) 	{return list.get(getIndex(i));}
	public String getName() 		{return list.get(getIndex());}
	public String getNextName() 	{return list.get(getNextIndex());}
	public String getPreviousName() {return list.get(getPreviousIndex());}


	public int getIndex(int i) 		{
		while(i >= list.size())
			i -= list.size();
		while(i < 0)
			i += list.size();
		return i;
	}
	public int getIndex() 			{return index;}
	public int getNextIndex() 		{return getIndex(index+1);}
	public int getPreviousIndex() 	{return getIndex(index-1);}

	public SoundBuffer getSoundBuffer(int i) 	{return Sound.get(list.get(getIndex(i)));}
	public SoundBuffer getSoundBuffer() 		{return getSoundBuffer(getIndex());}
	public SoundBuffer getNextSoundBuffer() 	{return getSoundBuffer(getNextIndex());}
	public SoundBuffer getPreviousSoundBuffer() {return getSoundBuffer(getPreviousIndex());}

	public void add(String name) {list.add(name);}

	public void setIndex(int newIndex) {
		this.index = newIndex;
	}
}
