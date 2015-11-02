package object.actor;

import io.Controller;
import object.primitive.Physical;
import resource.sound.SecondaryMusicSource;
import resource.sound.Sound;
import resource.sound.SoundSource;
import cont.Text;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GOGL;

public class NPC extends Actor {
	private String text, toScript;
	private boolean isSpeaking = false, noWait = false, canTalk = false;

	
	public NPC(float x, float y, float z) {
		super(x,y,z);
		
		name = "NPC";
	}
	
	
	public void talk(Actor other) {
		Text.talk(this,Player.getInstance(), "Hello...! How are you do?\nHmmm...\nOkay.\nHuh?\nI wish I were cool.");
	}
	public boolean collide(Physical other) {
		
		if(other.isPlayer()) {
			if(calcDis(other) < 32) {
				/*if(other.getZVelocity() < 0)
					((Actor) other).bounceOffHead();*/
				
				canTalk = true;
				Controller.setActionText("Talk");
				
				if(!Text.isActive() && Controller.getActionPressed()) {
					other.stop();
					stop();
					talk(Player.getInstance());
				}
			}
			else
				canTalk = false;
		}
				
		return false;
	}
	
	public void draw() {
		
		super.draw();
		
		/*if(canTalk) {
			
			GOGL.enableLighting();
			
			float upDir = GOGL.getTime()*5, dir = GOGL.getTime()*4;
			
			GOGL.transformTranslation(getX(),getY(),getZ()+height+8);
			GOGL.transformTranslation(0,0,Math2D.calcLenY(2,upDir));
			GOGL.transformRotationZ(dir);
			GOGL.draw3DFrustem(0,3,13,6);
			GOGL.transformScale(.75f,1.4f,1);
			GOGL.draw3DFrustem(0,5,10,6);
			GOGL.transformClear();
			
			GOGL.disableLighting();
		}*/
	}
	
	protected void control() {
		
		Actor pl = Player.getInstance();
		
		//follow(Player.getInstance());
		
		//turn(MathExt.rnd(-2,2));
		//move(false);
		
		stop();

		
		/*if(calcDis(pl) < 32) {
			face(pl);
			attack(pl);			
		}*/
	}
}
