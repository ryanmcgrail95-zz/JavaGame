package obj.itm;

import datatypes.vec3;
import datatypes.lists.CleanList;
import io.IO;
import io.Mouse;
import functions.Math2D;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import object.actor.Actor;
import object.actor.Player;
import object.environment.Heightmap;

public class Inventory extends ItemContainer {
	private Actor owner;
	private float onScreenF = 0;
	private int startInd;
	
	public Inventory(Actor owner) {
		super(4*7);
		this.owner = owner;
		startInd = -1;
	}
	
	public void draw() {

		int ind;
		float dX,oX,lX,dY,s, eS;
		oX = dX = 640+8 - (onScreenF)*(32+8+4*32+8);
		lX = Math.min(oX, 620);
		dY = 200;
		s = 32;
		eS = s+0;
		
		if(Mouse.checkRectangle(lX-8, dY-8, 640-(lX-8), 8+7*s+8))
			onScreenF += (1 - onScreenF)/15;
		else
			onScreenF += (0 - onScreenF)/15;

		Item it;
		
		GOGL.setColor(0,0,0,.8f);
		GOGL.fillRectangle(dX-8,dY-8,8+4*s+8,8+7*s+8);
		
		GOGL.fillTriangle(dX-8,dY, dX-8,dY+32*7+8, dX-8-(20*(1-onScreenF)), dY-8 + (8+7*32+8)/2);
		
		for(int r = 0; r < 7; r++) {
			dX = oX;
		
			for(int c = 0; c < 4; c++) {
				ind = r*4+c;
				it = get(ind);
				
				if(Mouse.checkRectangle(dX,dY,s,s)) {
					GOGL.setColor(1,1,1,.2f);
					GOGL.fillRectangle(dX,dY,s,s);
					
					
					// Draw 3D Rotating Model
					if(it != null) {						
						Mouse.setFingerCursor();

						GOGL.enableLighting();

						GOGL.setOrtho();
						GOGL.transformClear();
						GOGL.transformTranslation(535,120,0);
						GOGL.transformRotationX(90);
						GOGL.transformRotationX(-30);
						GOGL.transformRotationZ(GOGL.getTime());
						GOGL.transformScale(4);
							Item.drawModel(it.getName());
						GOGL.transformClear();
						GOGL.disableLighting();
					}
					
					
					if(Mouse.getLeftMouse() && it != null) {
						if(startInd == -1)
							startInd = ind;
					}
					else if(startInd > -1)
						if(get(startInd) != null) {
							if(startInd == ind)
								get(startInd).use(Player.getInstance());
							else
								list.swap(startInd, ind);
							startInd = -1;
						}
				}

				if(it != null && startInd != ind) {
					GOGL.setColor(RGBA.WHITE);
					GOGL.drawTexture(dX, dY, s,s, it.getSprite(0));

					if(it.getStackNum() <= 99999)
						GOGL.setColor(new RGBA(1,1,0));
					else if(it.getStackNum() <= 9999999)
						GOGL.setColor(RGBA.WHITE);
					else
						GOGL.setColor(new RGBA(0,1,.5f));
					GLText.drawString(dX,dY, .7f,.7f, getStackString(it.getStackNum(),it.getStackMax()));
					GOGL.setColor(RGBA.WHITE);
				}
				
				dX += eS;
			}
			dY += eS;
		}
		
		
		GOGL.setColor(1,1,1,1);
		if(startInd != -1) {
			if(get(startInd) != null)
				GOGL.drawTexture(Mouse.getMouseX()-s/2, Mouse.getMouseY()-s/2, s,s, get(startInd).getSprite(0));
			
			if(!Mouse.getLeftMouse()) {
				dropOne(get(startInd), Heightmap.getInstance().raycastMouse());
				startInd = -1;
			}
		}
		
	}
	
		

	
	
	public String getStackString(int num, int max) {
		String str;
			
		if(max == 1 || num == 1)
			return "";
		
		if(num <= 0)
			str = "NaN";
		else if(num <= 99999)
			str = ""+num;
		else if(num <= 9999999)
			str = ""+(int)(num/1000.)+"K";
		else
			str = ""+(int)(num/1000000.)+"M";
		
		return str;
	}

	
	public Actor getOwner() {return owner;}

	public int getCoinNum() {
		Item c = findItem("Coin");
		return c == null ? 0 : c.getStackNum();
	}

	public void addCoins(int num) {
		Item c = findItem("Coin");
		
		if(c != null)
			c.addStackItem(num);
		else
			add("Coin",num);
	}
}
