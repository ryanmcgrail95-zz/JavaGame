package resource.sound;

import io.Keyboard;

import java.util.ArrayList;
import java.util.List;

import object.primitive.Updatable;
import datatypes.lists.CleanList;

public class Playlist extends Updatable {
	private SoundSource source;
	private List<String> list;
	private int index;
	
	private float speed = 0;
	
	public Playlist(String... list) {
		this.list = new ArrayList<String>();
		
		for(String s : list)
			this.list.add(s);
		
		source = Sound.play(list[index = 0]);
		source.pause();
	}
	
	public SoundSource getSource() {
		return source;
	}
	
	public void play() {
		source.play();
	}

	public void update() {
		if(source.isPlaying() && !source.isALPlaying()) {
			index++;
			if(index >= list.size())
				index = 0;
			
			source.swapSoundBuffer(list.get(index));
		}
	}
}
