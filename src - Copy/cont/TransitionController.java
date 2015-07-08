package cont;

import gfx.GOGL;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import rm.RoomController;

public class TransitionController {
	private static byte T_NONE = -1, T_FADE = 0, T_IMAGE = 1;
	private static byte trType = T_NONE;
	private static String trRoom = "";
	private static Color trColor = Color.BLACK;
	private static BufferedImage trImg = null;
	private static double trTimer, trSpeed = 0;
	private static boolean trDone = true;
	
	
	

	public static void update(float deltaT) {
		if(trTimer > -1) {
			trTimer -= trSpeed;
			
			if(trTimer <= 0 && !trDone) {
				RoomController.switchRoom(trRoom);
				trDone = true;
			}
		}
		else if(trTimer < -1) {
			trTimer = -1;
			trType = T_NONE;
		}
	}
	
	public static void draw(Graphics2D g) {
		if(trType == T_FADE) {
			Color curColor = new Color(trColor.getRed(),trColor.getGreen(),trColor.getBlue(), (float) (1 - Math.abs(trTimer)));
			
			g.setColor(curColor);
			g.fillRect(0,0,GOGL.SCREEN_WIDTH,GOGL.SCREEN_HEIGHT);
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
		if(trTimer == -1) {
			trRoom = roomName;
			trType = T_IMAGE;
			trImg = img;
			trTimer = 1;
			trSpeed = speed;
			trDone = false;
		}
	}
	
	public static void startTransitionColor(String roomName, Color toColor, double speed) {
		if(trTimer == -1) {
			trRoom = roomName;
			trType = T_FADE;
			trColor = toColor;
			trTimer = 1;
			trSpeed = speed;
			trDone = false;
		}
	}
}
