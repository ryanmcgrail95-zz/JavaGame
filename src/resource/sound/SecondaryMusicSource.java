package resource.sound;

import object.actor.Player;
import object.primitive.Positionable;

public class SecondaryMusicSource extends Positionable {
	
	private Playlist music;
	private float minDis, maxDis;
	
	
	public SecondaryMusicSource(float x, float y, float z, float minDis, float maxDis, String... names) {
		super(x,y,z,false,false);
		music = new Playlist(names);
		this.minDis = minDis;
		this.maxDis = maxDis;
	}
	
	public void setPriority(float frac) {
		if(frac == 1)
			pause();
		else {
			play();
			Sound.duckMusic(music.getSource(), frac);
		}
	}

	@Override
	public void draw() {}

	@Override
	public void update() {
		Player p = Player.getInstance();
		float dis = calcDis(p), f;

		if(dis < minDis)
			f = 0;
		else if(dis < maxDis)
			f = (dis-minDis)/(maxDis-minDis);
		else
			f = 1;
		
		setPriority(f);
	}
	
	public void pause() {
		//music.pause();
	}
	public void play() {
		music.play();
	}

	public SoundSource getSource() {
		return music.getSource();
	}
	

	
	public void setPosition(float x, float y, float z) {
		music.setPosition(x,y,z);
	}
}