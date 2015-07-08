package cont;

import io.Controller;
import io.Mouse;
import object.actor.Actor;
import object.actor.Player;
import time.Timer;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import Datatypes.PrintString;

public final class Text {
	private static PrintString textString;
	private static boolean isTalking = false;
	private static float onscreenFrac;
	
	// FX
	private static float shakeStrength;
	private static Timer bounceTimer, shockTimer;
	
	private Text() {};

	public static void ini() {
		textString = new PrintString("");
		bounceTimer = new Timer(0,20);
		shockTimer = new Timer(0,20);
	}
	
	public static void set(String str) {
		textString.set(str);
	}
	
	private static boolean moveOnscreen() {
		onscreenFrac += (1 - onscreenFrac)/7.;
		
		if(onscreenFrac > .99) {
			onscreenFrac = 1;
			return true;
		}
		else
			return false;
	}
	
	public static void draw() {

		if(textString.isNull())
			return;		
					
		float r = 24, p = 8;
		float boxX, boxY;
		boxX = r;
		boxY = r - (1-onscreenFrac)*124 - Math2D.calcLenY(16,180*bounceTimer.getFraction());
		
		if(isShaking()) {
			boxX += shakeStrength*MathExt.rnd(-1,1);
			boxY += shakeStrength*MathExt.rnd(-1,1);
		}
		else if(isShocked()) {
			float shockStrength = calcShockStrength();
			boxX += shockStrength*MathExt.rnd(-1,1);
			boxY += shockStrength*MathExt.rnd(-1,1);
		}
		
		GOGL.setColor(new RGBA(0,0,0,.8f));
		GOGL.fillRectangle(boxX,boxY, 640-(2*r), 100);
		
		GOGL.forceColor(RGBA.WHITE);
		GLText.drawString(boxX+p,boxY+p, 1,1, textString);

		if(moveOnscreen())
			if(textString.advance()) {
				GOGL.setColor(RGBA.WHITE);
				float bX,bY,tX,tY;
				bX = boxX+(640-2*r);
				bY = boxY+(100);
				tX = bX-6 + Math2D.calcLenX(4,8*GOGL.getTime());
				tY = bY-6;
				GOGL.fillCircle(tX,tY, 8, 3);
				
				if(Controller.getActionPressed()) {
					close();
					
					if(isTalking)
						Camera.unlock();
				}
			}

		
		GOGL.unforceColor();		
	}
	
	
	public static void close() {
		set("");
		onscreenFrac = 0;
	}

	public static boolean isActive() {
		return !textString.isNull();
	}

	public static void talk(Actor a1, Actor a2, String string) {
		
		isTalking = true;

		float cX, cY;
		cX = (a1.getX()+a2.getX())/2;
		cY = (a1.getY()+a2.getY())/2;
		
		float talkDir = 0, talkDis = 32, aX, aY;
		aX = Math2D.calcLenX(talkDis,talkDir-90);
		aY = Math2D.calcLenY(talkDis,talkDir-90);
		
		a1.setPos(cX+aX,cY+aY);
		a1.face(a2);
		
		a2.setPos(cX-aX,cY-aY);
		a2.face(a1);

		Text.set(string);
		Camera.focus(96,talkDir,10, a1,a2);
		Camera.smooth(10);
		Camera.lock();		
	}
	
	
	// Effects
		// Shaking
			public static void shake(float strength) {
				shakeStrength = MathExt.contain(0, strength, 32);
			}
			public static boolean isShaking() {
				return shakeStrength > 0;
			}
			
		// Shock
			public static void shock() {
				shockTimer.reset();
			}
			public static boolean isShocked() {
				return !shockTimer.checkOnce();
			}
			public static float calcShockStrength() {
				return Math2D.calcLenY(16,180*shockTimer.getFraction());
			}
}
