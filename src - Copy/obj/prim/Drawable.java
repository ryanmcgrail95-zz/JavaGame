package obj.prim;
import gfx.GOGL;
import gfx.RGBA;
import gfx.Shape;

import java.awt.Graphics2D;

import ds.SortedList;
import cont.IO;
import cont.Messages;
import obj.chr.Player;
import time.Timer;

public abstract class Drawable extends Instantiable {
	private static SortedList<Drawable> drawList = new SortedList<Drawable>();
	private static SortedList<Drawable> hoverList = new SortedList<Drawable>();
	private static int colR = 1, colG = 0, colB = 0;
	
	// Mouse-Selection Variables
	protected boolean isSelected = false;
	private static Timer selectTimer = new Timer(10);


	protected int R, G, B;
	protected Shape shape;
	protected double rotX = 0, rotY = 0, rotZ = 0;
	protected boolean visible = true;
	
	public Drawable() {
		super();
		
		drawList.add(this);
	}
	
	public Drawable(boolean hoverable) {
		
		R = colR;
		G = colG;
		B = colB;
		
		drawList.add(this);
		
		if(hoverable)
			generateHoverColor();
	}
	
	private void generateHoverColor() {
		colR++;
		if(colR > 255) {
			colG++;
			
			if(colG > 255)
				colB++;
		}
			
		hoverList.add(this);
	}

		//PARENT FUNCTIONS
		public void update(float deltaT) {		
			super.update(deltaT);
		}
		
		public boolean draw() {
			return false;
		}
		public void hover() {
		}
		
		public void destroy() {
			drawList.remove(this);
			hoverList.remove(this);
			
			super.destroy();
			if(shape != null)
				shape.destroy();
		}
		
		
	//PERSONAL FUNCTIONS
		public void setVisible(boolean visible) {
			this.visible = visible;
		}
	
		
		
	//GLOBAL FUNCTIONS
		public static void drawAll() {
			int hSi = hoverList.size(), dSi = drawList.size();
			Drawable d;
			
			IO.resetCursor();
			GOGL.setPerspective();

			if(selectTimer.check()) {
				
				GOGL.allowLighting(false);
				GOGL.clearScreen();
				
				for(int i = 0; i < hSi; i++) {
					d = hoverList.get(i);
					d.isSelected = false;
					
					GOGL.forceColor(new RGBA(d.R/255f,d.G/255f,d.B/255f));
					
					if(d.visible)
						d.draw();
				}
			
				RGBA idColor = GOGL.getPixelColor((int) IO.getMousePickX(), (int) (IO.getMousePickY()));			
				float cR, cG, cB;
				for(int i = 0; i < hSi; i++) {
					d = hoverList.get(i);
					
					cR = d.R/255f;
					cG = d.G/255f;
					cB = d.B/255f;
					
					if(cR == idColor.getR() && cG == idColor.getG() && cB == idColor.getB()) {
						d.isSelected = true;
						break;
					}
				}
				
				GOGL.allowLighting(true);
				GOGL.unforceColor();
			}
			
			GOGL.clearScreen();


			//Draw BG
			
			
			for(int i = 0; i < dSi; i++) {
				d = drawList.get(i);
								
				if(d.visible) {
					d.draw();
					
					if(d.isSelected)
						d.hover();
				}
			}
			Shape.drawAll();
			
			GOGL.setOrtho();
			GOGL.setColor(1,1,1,1);
			
				Messages.draw();
				Player.getInstance().getInventory().draw();
				Player.getInstance().getStat().draw();
			
				
				
			// Drawing 3D Metal Bar
				/*GOGL.enableLighting();
				GOGL.setOrtho(999);
				GOGL.transformClear();
				GOGL.transformTranslation(320,240,0);
				GOGL.transformRotationX(90);
				GOGL.transformRotationX(-30);
				GOGL.transformRotationZ(GOGL.getTime());
				GOGL.transformScale(10,7,10);
				GOGL.transformRotationZ(45);
					GOGL.draw3DFrustem(0,0,1,3.5f,2f,.8f,4);
					GOGL.draw3DFrustem(4f,1f,4);
				GOGL.transformClear();
				GOGL.disableLighting();*/
				
			// Drawing Anvil
				/*GOGL.enableLighting();
				GOGL.setOrtho(999);
				GOGL.transformClear();
				GOGL.transformTranslation(320,240,0);
				GOGL.transformRotationX(90);
				GOGL.transformRotationX(-30);
				GOGL.transformRotationZ(GOGL.getTime());
				GOGL.transformScale(10,7,10);
				GOGL.transformRotationZ(45);
				
					GOGL.transformRotationZ(-45);
						GOGL.transformTranslation(2,0,0);
					GOGL.transformRotationZ(45);
					
					GOGL.draw3DFrustem(0,0,3f,1f,3f,2f,8);
					
					GOGL.transformRotationZ(-45);
						GOGL.transformTranslation(-2,0,0);
					GOGL.transformRotationZ(45);
										
					GOGL.draw3DFrustem(0,0,2.5f,4f,4f,3f,4);
					GOGL.draw3DFrustem(0,0,1.5f,3f,2f,1f,4);
					GOGL.draw3DFrustem(4f,1.5f,4);
				GOGL.transformClear();
				GOGL.disableLighting();*/
				
				
				/*GOGL.fillRectangle(30,30,640-60,100);
					GOGL.prStr.advance();
					GOGL.drawString(45,45,prStr);*/
		}
		
		public static void clean() {
			drawList.clean();
			hoverList.clean();
		}
		
		public static void sort() {
			int si = drawList.size();
			float dist;
			
			for(int i = 0; i < si; i++) {
				dist = drawList.get(i).calcCamDis();
				drawList.setValue(i, dist);
			}
			
			drawList.sortReverse();
		}

		private float calcCamDis() {
			return 0;
		}

		public static int getNumber() {
			return drawList.size();
		}
}
