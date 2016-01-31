package cont;

import gfx.G2D;
import gfx.GL;
import gfx.RGBA;
import java.awt.image.BufferedImage;
import datatypes.WeightedSmoothFloat;

public class TransitionController {
	private static byte T_NONE = -1, T_FADE = 0, T_IMAGE = 1;
	private static byte trType = T_NONE;
	private static String trRoom = "";
	private static RGBA trColor = RGBA.BLACK;
	private static BufferedImage trImg = null;
	private static WeightedSmoothFloat trFrac = new WeightedSmoothFloat(0);
	
	
	

	public static void update() {
		float before, after;
		before = trFrac.getFraction();

		trFrac.step();
		
		after = trFrac.getFraction();
		if(before < 1 && after >= 1) {
			//RoomController.switchRoom(trRoom);
			trType = T_NONE;
		}
	}
	
	public static void draw() {
		update();
		
		if(trType == T_FADE) {
			GL.setColor(trColor);
			GL.setAlpha(1 - trFrac.get());
			G2D.fillRectangle(0,0,GL.SCREEN_WIDTH,GL.SCREEN_HEIGHT);
			GL.resetColor();
		}
		else if(trType == T_IMAGE) {
			/*
				w = background_get_width(trImg);
		        h = background_get_height(trImg);
		        
		        lX = 320 - w/2*s*t;
		        rX = 320 + w/2*s*t;
		        tY = 240 - h/2*s*t;
		        bY = 240 + h/2*s*t;
		        
		        draw_set_color(c_black);
		        draw_background_scaled_centered_color(trImg,320,240,s*t,s*t);
		        draw_rectangle(0,0,640,tY,false);
		        draw_rectangle(0,0,lX,480,false);
		        draw_rectangle(rX,0,640,480,false);
		        draw_rectangle(0,bY,640,480,false);
		        draw_reset_ca();
			*/
		}
	}
	
	
	public static void startTransitionImage(String roomName, BufferedImage img, double speed) {
		/*if(trTimer == -1) {
			trRoom = roomName;
			trType = T_IMAGE;
			trImg = img;
			trTimer = 1;
			trSpeed = speed;
			trDone = false;
		}*/
	}
	
	public static void startTransitionColor(String roomName, RGBA col, int steps) {
		if(trFrac.get() == 0) {
			trRoom = roomName;
			trType = T_FADE;
			trColor = col;
			trFrac.restart(1,0,  0,-1,  steps);
		}
	}
}
