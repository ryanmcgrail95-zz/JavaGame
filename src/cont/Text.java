package cont;

import datatypes.PrintString;
import datatypes.StringExt;
import io.Controller;
import object.actor.Actor;
import object.actor.Player;
import time.Timer;
import functions.Math2D;
import functions.MathExt;
import gfx.Camera;
import gfx.G2D;
import gfx.GL;
import gfx.RGBA;

public final class Text {
	private static StringExt fullString;
	private static PrintString textString;
	private static boolean isTalking = false, isBoxAbove = false;
	private static float onscreenFrac;
	
	// FX
	private static float shakeStrength;
	private static Timer bounceTimer, shockTimer;
	
	private Text() {};

	public static void ini() {
		fullString = new StringExt("");
		textString = new PrintString("");
		bounceTimer = new Timer(0,20);
		shockTimer = new Timer(0,20);
	}
	
	public static void set(String str) {
		fullString.set(str);
		getNextSection();
	}
	
	public static void getNextSection() {
		String nextStr = "", line;
		
		if(!fullString.isEmpty())		
			for(int i = 0; i < 4; i++) {
				line = fullString.chompLine();
				nextStr += line + "\n";
			}
		textString.set(nextStr);
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
		
		if(isBoxAbove)
			boxY = r - (1-onscreenFrac)*124 - Math2D.calcLenY(16,180*bounceTimer.getFraction());
		else
			boxY = 480 + (-onscreenFrac)*124 - Math2D.calcLenY(16,180*bounceTimer.getFraction());
		
		if(isShaking()) {
			boxX += shakeStrength*MathExt.rnd(-1,1);
			boxY += shakeStrength*MathExt.rnd(-1,1);
		}
		else if(isShocked()) {
			float shockStrength = calcShockStrength();
			boxX += shockStrength*MathExt.rnd(-1,1);
			boxY += shockStrength*MathExt.rnd(-1,1);
		}
		
		GL.setColor(new RGBA(0,0,0,.8f));
		GL.fillRectangle(boxX,boxY, 640-(2*r), 100);
		
		float s = 1.0f;
		
		GL.forceColor(RGBA.WHITE);
		G2D.drawString(boxX+p,boxY+p, s,s, 8, textString);

		if(moveOnscreen())
			if(textString.advance("blipMale")) {
				GL.setColor(RGBA.WHITE);
				float bX,bY,tX,tY;
				bX = boxX+(640-2*r);
				bY = boxY+(100);
				tX = bX-6 + Math2D.calcLenX(4,8*GL.getTime());
				tY = bY-6;
				GL.fillPolygon(tX,tY, 8, 3);
				
				if(Controller.getActionPressed()) {
					if(fullString.isEmpty()) {
						close();
						if(isTalking)
							GL.getMainCamera().unlock();
					}
					else
						getNextSection();
				}
			}

		
		GL.unforceColor();		
	}
	
	
	public static void close() {
		set("");
		onscreenFrac = 0;
	}

	public static boolean isActive() {
		return !textString.isNull();
	}

	public static void talk(Actor a1, Actor a2, String string) {
		
		Camera cam = GL.getMainCamera();
		
		isTalking = true;

		float cX, cY;
		cX = (a1.getX()+a2.getX())/2;
		cY = (a1.getY()+a2.getY())/2;
		
		float talkDir = 0, talkDis = 32, aX, aY;
		aX = Math2D.calcLenX(talkDis,talkDir-90);
		aY = Math2D.calcLenY(talkDis,talkDir-90);
		
		
		a1.taskClear();
		a1.taskMoveTo(cX+aX,cY+aY);
		a1.taskFace(a2);
				
		a2.taskClear();
		a2.taskMoveTo(cX-aX,cY-aY);
		a2.taskFace(a1);

		Text.set(string);
		
		boolean above = true;
		isBoxAbove = above;
		if(above) {
			cam.focus(96,talkDir,10, a1,a2);
		}
		else {
			cam.focus(96,talkDir,-10, a1,a2);
			cam.setFocusTranslation(0,0,-15);
		}
		cam.smooth(10);
		cam.lock();		
	}
	
	public static void chest(String itemName) {
		
		Camera cam = GL.getMainCamera();
		Actor a = Player.getInstance();
		isTalking = true;
		
		Text.set("You got a " + itemName + "!");
		
		isBoxAbove = false;
		cam.focus(96,a.getDirection(),-10, a);
		cam.setFocusTranslation(0,0,-15);
		cam.smooth(10);
		cam.lock();		
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
