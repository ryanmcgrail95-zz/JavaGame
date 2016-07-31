package io;

import gfx.G2D;
import gfx.GL;
import gfx.RGBA;

public class Controller {
	private static String actionText = "";
	private static RGBA 
		actionColor = RGBA.createi(60,60,165),
		actionLight = RGBA.createi(89,92, 235), 
		actionDark = RGBA.createi(39,38,106);
	
	public static void setActionText(String text) {
		actionText = text;
	}

	
	private static void drawAttackButton(float x, float y, float r) {
		
		GL.setColor(actionDark);
		G2D.fillPolygon(x, y, r, 18);
		GL.setColor(actionColor);
		G2D.fillPolygon(x, y, r/28*23, 18);		
	}
	private static void drawActionButton(float x, float y, float r) {
		GL.setColor(actionDark);
		GL.fillPolygon(x, y, r, 18);
		GL.setColor(actionColor);
		GL.fillPolygon(x, y, r/28*23, 18);
		GL.setColor(RGBA.WHITE);
		G2D.drawStringCentered(x, y, 2,2, actionText, true);
	}
	
	
	public static void draw() {
		drawAttackButton(350,60,28);
		drawActionButton(420,60,28);
		
		setActionText("");
	}


	public static boolean getActionPressed() {
		return IO.getAButtonPressed();
	}


	public static float getDirDown() {
		return Keyboard.getDPadDown();
	}
	public static float getDirPressed() {
		return Keyboard.getDPadPressed();
	}

}
