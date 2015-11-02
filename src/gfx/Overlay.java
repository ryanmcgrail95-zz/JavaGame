package gfx;


import btl.BattleActor;
import btl.BattleController;
import object.actor.Player;
import object.primitive.Drawable;
import object.primitive.Updatable;
import phone.SmartPhone;
import sts.Stat;
import time.Delta;
import time.Timer;
import window.Window;
import cont.Messages;
import cont.Text;
import io.Controller;

public class Overlay {
	private static int timer = 0, secs = 0;
	private static boolean isEnabled = true;
	
	private static MultiTexture starpointTex = new MultiTexture("Resources/Images/starpoints.png",8,1);
	private static float starpointInd = 0;
	
	public static void draw() {
		if(isEnabled)
			if(Text.isActive())
				Text.draw();
			else if(Window.isWindowOpen())
				Window.drawAll();
			else if(!SmartPhone.isActive()){
				//Controller.draw();
				Messages.draw();
				
				if(Player.getInstance() != null) {
					Player.getInstance().getInventory().draw();
					Player.getInstance().getStat().draw();
				}
				
				BattleController inst = BattleController.getInstance();
				if(inst != null) {
					for(BattleActor b : inst.getEnemyActors()) {
						int[] pt = GOGL.calcScreenPt(b.getX(), b.getY(), b.getZ());
						
						float w = 64, h = 16;
						//GOGL.drawFillBar(pt[0]-w/2,pt[1], w,h, 1f*b.getHP()/b.getMaxHP());
					}
					
					float dS = 16, ddS = dS*.75f;
					float dX = GOGL.SCREEN_WIDTH - 3*ddS, dY = GOGL.SCREEN_HEIGHT - 2*ddS;
					int pts = inst.getStarPoints();
					for(int i = 0; i < pts; i++) {
						GOGL.drawTexture(dX, dY, dS, dS, starpointTex, (int) starpointInd);
						dX -= dS;
					}
					starpointInd += .3f;
					if(starpointInd > 8)
						starpointInd -= 8;
				}
				//Stat.drawOverheads();
				
			
				//GOGL.drawFBO(0,0, FireSprite.getFBO());
				
				timer++;
				if(timer == 60) {
					timer = 0;
					secs++;
				}

				
				float dX,dY;
				dX = 200;
				dY = 0;
				GLText.drawString(dX,dY, "FPS: " + (int) Delta.getFPS()); dY += 20;
				GLText.drawString(dX,dY, "Drawable: " + Drawable.getNumber()); dY += 20;
				GLText.drawString(dX,dY, "Updatable: " + Updatable.getNumber()); dY += 20;
			}
	}

	public static void enable() {
		isEnabled = true;
	}
	public static void disable() {
		isEnabled = false;
	}
}
