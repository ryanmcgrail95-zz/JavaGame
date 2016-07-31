package object.primitive;
import functions.ArrayMath;
import functions.Math2D;
import gfx.Camera;
import gfx.CameraFX;
import gfx.GL;
import gfx.Gameboy;
import gfx.Overlay;
import gfx.RGBA;
import gfx.WorldMap;
import io.Mouse;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Comparator;

import com.jogamp.opengl.util.texture.Texture;

import cont.Messages;
import cont.Text;
import cont.TextureController;
import ds.StringExt2;
import ds.lst.CleanList;
import obj.prt.Floaties;
import object.actor.Player;
import object.environment.Tree;
import paper.Background;
import phone.SmartPhone;
import time.Timer;
import window.Window;

public abstract class Drawable extends Updatable {
	private static CleanList<Drawable> renderList = new CleanList<Drawable>("Render");
	private static CleanList<Drawable> drawList = new CleanList<Drawable>("Draw");
	private static CleanList<Drawable> hoverList = new CleanList<Drawable>("Hover");	
	private static CleanList<Drawable> onscreenList = new CleanList<Drawable>("OS Draw");
	private static CleanList<Drawable> onscreenAddList = new CleanList<Drawable>("OS Add");
	private static CleanList<Drawable> onscreenHoverList = new CleanList<Drawable>("OS Hover");
	
	private static int colR = 1, colG = 0, colB = 0;
	
	// Mouse-Selection Variables
	protected boolean isSelected = false, shouldAdd = false;
	private static Timer selectTimer;


	protected int idRGB;
	protected boolean visible = true, isHoverable = false, isRenderable = false;		

	
	public Drawable(boolean hoverable, boolean renderable) {	
		super();
		drawList.add(this);
		
		name = "Drawable";
		
		if(hoverable)
			generateHoverColor();
		if(renderable)
			renderList.add(this);
	}
	
	private void generateHoverColor() {
				
		idRGB = RGBA.convertRGB2Int(colR, colG, colB);
		
		colR++;
		if(colR > 255) {
			colR = 0;	colG++;
			
			if(colG > 255) {
				colG = 0;	colB++;
			}
		}
			
		isHoverable = true;
	}

	//PARENT FUNCTIONS
	public abstract void draw();
	public abstract void add();
	
	public void render() {}
	public void hover() {}
	
	public void update() {
		//System.out.println(getClass().getName());
	}
		
	public void destroy() {
		super.destroy();
		drawList.remove(this);
		if(isHoverable)
			hoverList.remove(this);
		if(isRenderable)
			renderList.remove(this);
	}
		
		
	//PERSONAL FUNCTIONS
	public void setVisible(boolean visible) {this.visible = visible;}
	public boolean checkOnscreen() {
		return true;
	}
	public float calcDepth() {
		return 0;
	}
	
	//GLOBAL FUNCTIONS
	public static void presort() {		
		onscreenList.clear();
		onscreenAddList.clear();
		onscreenHoverList.clear();
		
		byte addHover = 2;
		if(GL.getCamera() == GL.getMainCamera())
			if(selectTimer.check()) {
				if(/*Window.checkMouseAll() ||*/ !canSelectHoverable())
					addHover = 1;
				else
					addHover = 0;
			}
						
		// Sort by Depth
		int dSi = drawList.size(), i, k;
		float depth;
		for(Drawable d : drawList)
			if(d.visible) {
				if(d.checkOnscreen()) {
					depth = d.calcDepth();
					if(depth >= 0 && depth < GL.getCamera().getMaxDistance()) {
						if(d.shouldAdd)
							onscreenAddList.add(d);
						else
							onscreenList.add(d);
							
						if(addHover == 0) {
							if(d.isHoverable)
								onscreenHoverList.add(d);
						}
						else if(addHover == 1)
							d.isSelected = false;
					}
				}
			}
		
		onscreenList.sort(Drawable.Comparators.DEPTH);
	}
	
		public static void display() {
			
			//GL.start("Drawable.display()");
			
			if(selectTimer == null)
				selectTimer = new Timer(5);
			
			
			//presort(GOGL.getMainCamera());
			
			//onscreenHoverList
			int hSi = hoverList.size(), dSi = onscreenList.size();
			        	
			GL.setOrtho();
			for(Drawable d : renderList)
				d.render();
			Window.renderAll();
			
        	GL.setViewport(0,0, GL.getInternalWidth(),GL.getInternalHeight());
			GL.setPerspective();
			

			/*if(canSelectHoverable() && onscreenHoverList.size() > 0) {
				GL.allowLighting(false);
				GL.clear();
				
				for(Drawable d : onscreenHoverList) {
					d.isSelected = false;
					
					GL.forceColor(d.idRGB);
					d.draw();
				}
			
				int idRGB = Mouse.getPixelRGB();

				int cR, cG, cB;
				for(Drawable d : onscreenHoverList) {
					if(idRGB == d.idRGB) {
						d.isSelected = true;
						d.hover();
						break;
					}
				}
				onscreenHoverList.broke();
				
				GL.allowLighting(true);
				GL.unforceColor();
			}*/
	}

	public static int getNumber() {return drawList.size();}
	public static int getHoverableNumber() {return hoverList.size();}
	public static int getOnscreenNumber() {return onscreenList.size();}
	public static int getOnscreenHoverableNumber() {return onscreenHoverList.size();}

	public static class Comparators {
		public final static Comparator<Drawable> DEPTH = new Comparator<Drawable>() {
            public int compare(Drawable o1, Drawable o2) {
                return (int) (o1.calcDepth() - o2.calcDepth());
            }
        };
	}
	
	
	private static boolean canSelectHoverable() {
		return true; //return !SmartPhone.isActive() && !WorldMap.isActive();
	}

	public static void draw3D() {
		//GL.start("Drawable.draw3D()");

		Camera cam = GL.getCamera();
		
		//Draw BG
		
		if(cam.checkBG()) {
			GL.setOrtho(-1000);
			Background.draw();
		}
		
		cam.project();
		
		/*CleanList<Drawable> drawList = cam.getDrawList();
		if(drawList.size() > 0) {
			for(Drawable d : drawList)
				d.draw();
			return;
		}*/
				
		for(Drawable d : onscreenList)
			d.draw();
		
		//GL.end("Drawable.draw3D()");
	}

	public static void printList() {
		for(Drawable d : drawList)
			System.out.println(d.getName());
	}
}
