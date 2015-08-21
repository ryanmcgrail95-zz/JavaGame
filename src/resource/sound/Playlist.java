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
	public void playNextSong() 		{source.swapSoundBuffer(list.get(index = getNextIndex()));}
	public void playPreviousSong() 	{source.swapSoundBuffer(list.get(index = getPreviousIndex()));}

	public void setPosition(float x, float y, float z) 	{source.setPosition(new float[] {x,y,z});}	
	public void setSpeed(float speed) 					{source.setSpeed(this.speed = speed);}
	public void setLoop(boolean isLooping) 				{source.setLoop(this.isLooping = isLooping);}
	public void setVolume(float volumeFrac) 			{source.setVolume(volumeFrac);}

	public String getName(int i) 	{return list.get(i);}
	public String getName() 		{return list.get(getIndex());}
	public String getNextName() 	{return list.get(getNextIndex());}
	public String getPreviousName() {return list.get(getPreviousIndex());}

	public int getIndex() 			{return index;}
	public int getNextIndex() 		{return (index+1 == list.size() ? 0 : index+1);}
	public int getPreviousIndex() 	{return (index == 0 ? list.size()-1 : index-1);}

	public SoundBuffer getSoundBuffer(int i) 	{return Sound.get(list.get(i));}
	public SoundBuffer getSoundBuffer() 		{return getSoundBuffer(getIndex());}
	public SoundBuffer getNextSoundBuffer() 	{return getSoundBuffer(getNextIndex());}
	public SoundBuffer getPreviousSoundBuffer() {return getSoundBuffer(getPreviousIndex());}
}
