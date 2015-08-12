package window;

import datatypes.Inventory;
import io.Mouse;
import obj.itm.Item;
import obj.itm.ItemBlueprint;
import obj.itm.ItemController;
import object.actor.Player;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import window.RectButton;

public class ItemButton extends RectButton {
	private ItemBlueprint itemType;
	private int numAvailable;
	
	public ItemButton(String name, int numAvailable, int x, int y) {
		super(x,y, 44,44);
		this.numAvailable = numAvailable;
		itemType = ItemController.get(name);
	}
	
	public byte draw(float frameX, float frameY) {
		
		float dX,dY, s, iX,iY;
		dX = frameX+x();
		dY = frameY+y();
		s = .7f;
		
		iX = dX+5;
		iY = dY+1;
		
		String cost = ""+itemType.getValue();
		
		// Draw Item Window
		GOGL.fillVGradientRectangle(dX,dY, w(),h()*3/4, new RGBA(23,19,17),new RGBA(68,56,46), 4);
		// Draw Price
		GOGL.fillVGradientRectangle(dX,dY+h()*3/4, w(),h()/4, new RGBA(41,35,28),new RGBA(68,56,46), 4);
		// Draw Item
		GOGL.setColor(RGBA.WHITE);
		GOGL.drawTexture(iX,iY,itemType.getSprite().getFrame(0));
		// Draw Text
		GOGL.setColor(new RGBA(255,255,0));
		GOGL.fillCircle(dX+8,dY+h()*7/8, h()*1/8, 8);
		GLText.drawString(iX,iY,s,s,""+numAvailable,true);
		GLText.drawString(dX+w()-4-GLText.getStringWidth(s,s,cost),dY+h()*3/4+2, s,s, cost,true);
		// Draw Outline
		GOGL.setColor(new RGBA(41,35,28));
		GOGL.drawRectangle(dX,dY,w(),h());
		
		
		if(checkMouse()) {
			glowTo(1);
			Mouse.setFingerCursor();
			
			if(Mouse.getLeftClick())
				buyOne();
		}
			glowTo(0);
			
		GOGL.setColor(new RGBA(1,1,1,getGlow()*.5f));
		GOGL.fillRectangle(dX,dY,w(),h());
		
		return -1;
	}

	private void buyOne() {
		Player pl = Player.getInstance();
		Inventory inv = pl.getInventory();
		
		if(numAvailable > 0) {
			if(pl.getInventory().getCoinNum() >= itemType.getValue()) {
				numAvailable--;
				pl.buyItem(itemType,itemType.getValue());
			}
		}
	}
	
	public void destroy() {}
}
