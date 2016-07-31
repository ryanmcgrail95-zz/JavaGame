package gfx;


import java.lang.management.ManagementFactory;

import btl.BattleActor;
import btl.BattleController;
import object.actor.Player;
import object.primitive.Drawable;
import object.primitive.Updatable;
import paper.ActorPM;
import paper.CharacterPM;
import paper.PlayerPM;
import phone.SmartPhone;
import resource.Loadbar;
import resource.Resource;
import resource.image.Img;
import resource.model.Model;
import resource.sound.Sound;
import sts.Stat;
import time.Delta;
import time.Timer;
import window.Window;
import cont.ImgIO;
import cont.Messages;
import cont.Text;
import cont.TextureController;
import cont.TransitionController;
import ds.mat;
import ds.vec;
import ds.lst.CleanList;
import io.Controller;
import io.Keyboard;
import menu.Menu;

public class Overlay {
	private static int timer = 0, secs = 0;
	private static boolean isEnabled = true;
	
	private static MultiTexture starpointTex = new MultiTexture("Resources/Images/starpoints.png",Img.AlphaType.NORMAL,8,1);
	private static MultiTexture hpBarTex = new MultiTexture("Resources/Images/hp1.png",Img.AlphaType.NORMAL,27,1);
	private static float starpointInd = 0;
	
	public static void ini() {
		Menu.ini();
	}
	
	public static void draw() {
		if(isEnabled)
			if(Text.isActive())
				Text.draw();
			else { // if(!SmartPhone.isActive()){
 				//Controller.draw();
				//Menu.drawAll();
				
				if(Window.isWindowOpen())
					Window.drawAll();
				GL.setColor(RGBA.WHITE);
				
				Messages.draw();
								
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
					float dX = GL.getInternalWidth() - 3*ddS, dY = GL.getInternalHeight() - 2*ddS;
					int pts = inst.getStarPoints();
					for(int i = 0; i < pts; i++) {
						G2D.drawTexture(dX, dY, dS, dS, starpointTex, (int) starpointInd);
						dX -= dS;
					}
					starpointInd += .3f;
					if(starpointInd > 8)
						starpointInd -= 8;
				}
								
				timer++;
				if(timer == 60) {
					timer = 0;
					secs++;
				}

				
				if(Keyboard.checkDown('o')) {
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
						"RGBAs: " + RGBA.getNumber(),
						"Characters: " + CharacterPM.getNumber()
					);
					
					ActorPM pl = PlayerPM.getInstance();
					
					if(pl != null)
						drawStrings(0, 320,
							""+ pl.x() + ", " + pl.y() + ", " + pl.z(),
							""+ pl.vX() + ", " + pl.vY() + ", " + pl.vZ()
						);
					
					//drawUpdatables(300,0);
					drawCleanLists(500,0);
					//drawResources(300,0);
				}
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
		CleanList<Updatable> list = Updatable.getList();
		for(Updatable u : list) {
			G2D.drawString(x,y, u.getName());
			y += 15;
		}
	}
	public static void drawResources(float x, float y) {
		CleanList<Resource> list = Resource.getList();
		for(Resource u : list) {
			G2D.drawString(x,y, u.getFileName() + ": " + u.getReferences());
			y += 15;
		}
	}
	public static void drawCleanLists(float x, float y) {
		CleanList<CleanList> list = CleanList.getLists();
		for(CleanList c : list) {
			G2D.drawString(x,y, c.getName() + ": " + c.size());
			y += 10;
		}
	}

	public static void enable()		{isEnabled = true;}
	public static void disable()	{isEnabled = false;}
}
