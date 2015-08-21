package object.environment;

import gfx.GOGL;
import gfx.RGBA;
import object.primitive.Environmental;
import object.primitive.Physical;
import resource.sound.SecondaryMusicSource;
import time.Timer;

public class Radio extends Environmental {
	private SecondaryMusicSource music;
	private Timer ampTimer = new Timer(2);
	private float amp;
	
	public Radio(float x, float y) {
		super(x,y,true,false);
		music = new SecondaryMusicSource(x,y,z(), 100,400, "overworld","courtBegins","godot");
	}

	public boolean collide(Physical other) {
		return false;
	}

	public void draw() {
		music.setPos(x(),y(),z());
		
		GOGL.transformClear();
		transformTranslation();

			float w = 16, l = 4, h = 10, aF = .3f, s = 5;
			GOGL.draw3DBlock(-w/2,-l/2,h,w/2,l/2,0);
			
			if(ampTimer.check())
				amp = Math.abs(music.getSource().getAmplitudeFraction());
			
			GOGL.setColor(RGBA.BLACK);
			GOGL.transformTranslation(0,-l/2*1.1f,h/2);
			GOGL.transformRotationX(90);
			GOGL.fillPolygon(-s,0,(1-aF + aF*amp) * 4, 8);
			GOGL.fillPolygon(s,0,(1-aF + aF*amp) * 4, 8);
		
		GOGL.transformClear();
		GOGL.resetColor();
	}
}
