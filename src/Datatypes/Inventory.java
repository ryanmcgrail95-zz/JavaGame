package Datatypes;

import io.IO;
import io.Mouse;
import functions.Math2D;
import gfx.GOGL;
import gfx.RGBA;
import obj.itm.Item;
import obj.itm.ItemBlueprint;
import obj.itm.ItemController;
import object.actor.Actor;
import object.environment.Heightmap;

public class Inventory {
	private Actor owner;
	private float onScreenF = 0;
	private SortedList<Item> itemList;
	private static final byte S_VALUE = 0, S_NAME = 1;
	private byte sortType = S_VALUE;
	private int startInd;
	
	public Inventory(Actor owner) {
		this.owner = owner;
		
		startInd = -1;
		
		itemList = new SortedList<Item>();
		for(int i = 0; i < 4*7; i++)
			itemList.add(null);
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
				it = itemList.get(ind);
				
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
							Item.drawModel(itemList.get(ind).getName());
						GOGL.transformClear();
						GOGL.disableLighting();
					}
					
					
					if(Mouse.getLeftMouse() && it != null) {
						if(startInd == -1)
							startInd = ind;
					}
					else if(itemList.get(startInd) != null) {
						if(startInd == ind)
							itemList.get(startInd).use();
						else
							itemList.swap(startInd, ind);
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
					GOGL.drawString(dX,dY, .7f,.7f, getStackString(it.getStackNum(),it.getStackMax()));
					GOGL.setColor(RGBA.WHITE);
				}
				
				dX += eS;
			}
			dY += eS;
		}
		
		
		GOGL.setColor(1,1,1,1);
		if(startInd != -1) {
			if(itemList.get(startInd) != null)
				GOGL.drawTexture(Mouse.getMouseX()-s/2, Mouse.getMouseY()-s/2, s,s, itemList.get(startInd).getSprite(0));
			
			if(!Mouse.getLeftMouse()) {
				dropOne(itemList.get(startInd), Heightmap.getInstance().raycastMouse());
				startInd = -1;
			}
		}
		
	}
	
		
	private void drop(Item it, vec3 pt) {
		if(it == null)
			return;
		
		it.makeReal(pt);
		removeItem(it, false);		
	}
	
	private void dropOne(Item it, vec3 pt) {
		if(it == null)
			return;
		
		if(it.getStackNum() == 1) {
			drop(it, pt);
			return;
		}

		it.removeStackItem();
		Item.create(it.getName(),1, pt);
	}

	public int size() {
		return itemList.size();
	}
	
	public void add(String name, int amount) {Item.create(name,amount,this);}
	public void add(ItemBlueprint blueprint, int amount) {Item.create(blueprint.getName(),amount,this);}
	public void add(Item obj) {obj.giveTo(this);}
	
	public void removeItem(Item obj, boolean shouldDestroy) {
		for(int i = 0; i < 4*7; i++)
			if(itemList.get(i) == obj)
				itemList.set(i,null);
		
		if(shouldDestroy)
			obj.destroy();
	}
	
	public void sort() {
		switch(sortType) {
			case S_VALUE:
				sortValue(true);
				break;
				
			case S_NAME:
				sortName(true);
				break;
		}
	}
	
	public void sortName(boolean forward) {
		String str;
		char curC;
		int si = itemList.size();
		
		for(int i = 0; i < si; i++)
			itemList.setString(i, itemList.get(i).getName());
		
		itemList.sort(SortedList.M_STRING, forward);
	}
	
	public void sortValue(boolean forward) {
		int si = itemList.size();
		
		for(int i = 0; i < si; i++)
			itemList.setValue(i, itemList.get(i).getValue());
		
		itemList.sort(SortedList.M_NUMBER, forward);
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

	public void dropAll(vec3 pt) {
		for(int i = 0; i < 4*7; i++)
			drop(itemList.get(i), pt);
	}

	public Item findItem(String name) {
		Item it;
		for(int i = 0; i < 4*7; i++) {
			it = itemList.get(i);
			if(it != null)
				if(it.getName() == name)
					return it;
		}
		
		return null;
	}
	
	public boolean replaceItem(String oriIt, String newIt) {
		Item it = findItem(oriIt);
		int num;
		
		if(it == null)
			return false;
		else {
			num = it.getNumber();
			removeItem(it, true);
			add(newIt,num);
			return true;
		}
	}
	
	public Actor getOwner() {
		return owner;
	}

	public int getCoinNum() {
		Item c = findItem("Coin");
		
		if(c != null)
			return c.getStackNum();
		else
			return 0;
	}

	public void addCoins(int num) {
		Item c = findItem("Coin");
		
		if(c != null)
			c.addStackItem(num);
		else
			add("Coin",num);
	}
	
	public SortedList<Item> getItemList() {return itemList;
	}
}
