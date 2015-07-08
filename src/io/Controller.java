package io;

import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;

public class Controller {
	private static String actionText = "";
	private static RGBA actionColor = new RGBA(60,60,165), actionLight = new RGBA(89,92, 235), actionDark = new RGBA(39,38,106);
	
	public static void setActionText(String text) {
		actionText = text;
	}

	
	private static void drawAttackButton(float x, float y, float r) {
		
		GOGL.setColor(actionDark);
		GOGL.fillCircle(x, y, r, 18);
		GOGL.setColor(actionColor);
		GOGL.fillCircle(x, y, r/28*23, 18);		
	}
	private static void drawActionButton(float x, float y, float r) {
		GOGL.setColor(actionDark);
		GOGL.fillCircle(x, y, r, 18);
		GOGL.setColor(actionColor);
		GOGL.fillCircle(x, y, r/28*23, 18);
		GOGL.setColor(RGBA.WHITE);
		GLText.drawStringCentered(x, y, 2,2, actionText, true);
	}
	
	
	public static void draw() {
		drawAttackButton(350,60,28);
		drawActionButton(420,60,28);
		
		setActionText("");
	}


	public static boolean getActionPressed() {
		return IO.getAButtonPressed();
	}
}
