package gfx;


import btl.BattleActor;
import btl.BattleController;
import object.actor.Player;
import object.primitive.Drawable;
import object.primitive.Updatable;
import paper.CharacterPM;
import phone.SmartPhone;
import resource.model.Model;
import resource.sound.Sound;
import sts.Stat;
import time.Delta;
import time.Timer;
import window.Window;
import cont.Messages;
import cont.Text;
import cont.TextureController;
import cont.TransitionController;
import datatypes.mat;
import datatypes.vec;
import datatypes.lists.CleanList;
import io.Controller;

public class Overlay {
	private static int timer = 0, secs = 0;
	private static boolean isEnabled = true;
	
	private static MultiTexture starpointTex = new MultiTexture("Resources/Images/starpoints.png",8,1);
	private static MultiTexture hpBarTex = new MultiTexture("Resources/Images/hp1.png",27,1);
	private static float starpointInd = 0;
	
	public static void draw() {
		if(isEnabled)
			if(Text.isActive())
				Text.draw();
			/*else if(Window.isWindowOpen())
				Window.drawAll();*/
			else { // if(!SmartPhone.isActive()){
				//Controller.draw();
				Messages.draw();
				
				/*if(Player.getInstance() != null) {
					Player.getInstance().getInventory().draw();
					Player.getInstance().getStat().draw();
				}*/
				
				BattleController inst = BattleController.getInstance();
				if(inst != null) {
					for(BattleActor b : inst.getEnemyActors()) {
						int[] pt = GL.calcScreenPt(b.getX(), b.getY(), b.getZ());
						
						float w = 64, h = 32, f;
						f = (float) ((hpBarTex.getImageNumber()-1)*(1. - 1.*b.getHP()/b.getMaxHP()));
						G2D.drawTexture(pt[0]-w/2,pt[1], w,h, hpBarTex, (int) f);
						//GL.drawFillBar(pt[0]-w/2,pt[1], w,h, 1f*b.getHP()/b.getMaxHP());
					}
					
					float dS = 16, ddS = dS*.75f;
					float dX = GL.SCREEN_WIDTH - 3*ddS, dY = GL.SCREEN_HEIGHT - 2*ddS;
					int pts = inst.getStarPoints();
					for(int i = 0; i < pts; i++) {
						G2D.drawTexture(dX, dY, dS, dS, starpointTex, (int) starpointInd);
						dX -= dS;
					}
					starpointInd += .3f;
					if(starpointInd > 8)
						starpointInd -= 8;
				}
				//Stat.drawOverheads();
				
			
				//GL.drawFBO(0,0, FireSprite.getFBO());
				
				timer++;
				if(timer == 60) {
					timer = 0;
					secs++;
				}

				
				float dX,dY;
				dX = 0;
				dY = 0;
				drawStrings(dX,dY,
					"FPS: " + (int) Delta.getFPS(),
					"Drawable: " + Drawable.getNumber(),
					"Updatable: " + Updatable.getNumber(),
					"Timers: " + Timer.getNumber(),
					"Textures: " + TextureController.getNumber(),
					"TextureExts: " + TextureController.getNumberExt(),
					"Models: " + Model.getNumber(),
					"Cleanlists: " + CleanList.getNumber(),
					"CleanList Loops: " + CleanList.getLoops(),
					"CleanList Size: " + CleanList.totalSize(),
					"Sounds: " + Sound.getBufferNumber(),
					"Sounds (Playing): " + Sound.getSourceNumber(),
					"Vectors: " + vec.getNumber(),
					"Matrices: " + mat.getNumber(),
					"FBOs: " + FBO.getNumber(),
					//"RGBAs: " + RGBA.getNumber(),
					"Characters: " + CharacterPM.getNumber()
				);
				
				drawUpdatables(300,0);
				drawCleanLists(500,0);
			}
		
		TransitionController.draw();
	}
	
	public static void drawStrings(float x, float y, String... strs) {
		for(String s : strs) {
			G2D.drawString(x,y, s);
			y += 15;
		}
	}
	
	public static void drawUpdatables(float x, float y) {
		for(Updatable u : Updatable.getList()) {
			G2D.drawString(x,y, u.getClass().getName());
			y += 15;
		}
	}
	public static void drawCleanLists(float x, float y) {
		for(CleanList c : CleanList.getLists()) {
			G2D.drawString(x,y, c.getName() + ": " + c.size());
			y += 15;
		}
	}

	public static void enable() {
		isEnabled = true;
	}
	public static void disable() {
		isEnabled = false;
	}
}
