package Datatypes;

import cont.IO;
import cont.ItemController;
import func.Math2D;
import gfx.GOGL;
import obj.chr.Actor;
import obj.env.Heightmap;
import obj.itm.Item;

public class Inventory {
	private Actor owner;
	private float onScreenF = 0;
	private SortedList<Item> inv;
	private static final byte S_VALUE = 0, S_NAME = 1;
	private byte sortType = S_VALUE;
	private int startInd;
	
	public Inventory(Actor owner) {
		this.owner = owner;
		
		startInd = -1;
		
		inv = new SortedList<Item>();
		for(int i = 0; i < 4*7; i++)
			inv.add(null);
	}
	
	public void draw() {
		float mX,mY;
		mX = IO.getMouseX();
		mY = IO.getMouseY();
		
		int ind;
		float dX,oX,lX,dY,s, eS;
		oX = dX = 640+8 - (onScreenF)*(32+8+4*32+8);
		lX = Math.min(oX, 620);
		dY = 200;
		s = 32;
		eS = s+0;
		
		if(Math2D.isInsideRectangle(mX,mY,  lX-8, dY-8, 640, dY+7*s+8))
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
				it = inv.get(ind);
				
				if(Math2D.isInsideRectangle(mX,mY, dX,dY,dX+s,dY+s)) {
					GOGL.setColor(1,1,1,.2f);
					GOGL.fillRectangle(dX,dY,s,s);
					
					
					// Draw 3D Rotating Model
					if(it != null) {						
						IO.setCursor(IO.C_POINTING_FINGER);
						
						GOGL.enableLighting();
						GOGL.setOrtho();
						GOGL.transformClear();
						GOGL.transformTranslation(535,120,0);
						GOGL.transformRotationX(90);
						GOGL.transformRotationX(-30);
						GOGL.transformRotationZ(GOGL.getTime());
						GOGL.transformScale(4);
							Item.drawModel(inv.get(ind).getName());
						GOGL.transformClear();
						GOGL.disableLighting();
					}
					
					
					if(IO.getLeftMouse() && it != null) {
						if(startInd == -1)
							startInd = ind;
					}
					else if(inv.get(startInd) != null) {
						if(startInd == ind)
							inv.get(startInd).use();
						else
							inv.swap(startInd, ind);
						startInd = -1;
					}
				}

				if(it != null && startInd != ind) {
					GOGL.setColor(1,1,1,1);
					GOGL.drawTexture(dX, dY, s,s, it.getSprite());
					GOGL.drawString(dX,dY, .7f,.7f, getStackString(it.getStackNum(),it.getStackMax()));
				}
				
				dX += eS;
			}
			dY += eS;
		}
		
		
		GOGL.setColor(1,1,1,1);
		if(startInd != -1) {
			if(inv.get(startInd) != null)
				GOGL.drawTexture(mX-s/2, mY-s/2, s,s, inv.get(startInd).getSprite());
			
			if(!IO.getLeftMouse()) {
				dropOne(inv.get(startInd), Heightmap.getInstance().raycastPoint(IO.getMouseX(),IO.getMouseY()));
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
		Item.create(it.getName(), pt);
	}

	public int size() {
		return inv.size();
	}
	
	public boolean addItem(String name) {
		addItem(Item.create(this, name));
		
		return false;
	}
	public boolean addItem(Item obj) {
		Item it;
		
		for(int i = 0; i < 4*7; i++) {
			it = inv.get(i);
			
			if(it != null)
				if(it.getName().equals(obj.getName())) {
					
					it.addStackItem();
					obj.destroy();
					return true;
				}
		}
		
		for(int i = 0; i < 4*7; i++) {
			if(inv.get(i) == null) {
				inv.set(i,obj);
				obj.setVisible(false);
				obj.setDoUpdates(false);
				return true;
			}
		}
		
		return false;
	}
	public void removeItem(Item obj, boolean shouldDestroy) {
		for(int i = 0; i < 4*7; i++)
			if(inv.get(i) == obj)
				inv.set(i,null);
		
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
		int si = inv.size();
		
		for(int i = 0; i < si; i++)
			inv.setString(i, inv.get(i).getName());
		
		inv.sort(SortedList.M_STRING, forward);
	}
	
	public void sortValue(boolean forward) {
		int si = inv.size();
		
		for(int i = 0; i < si; i++)
			inv.setValue(i, inv.get(i).getValue());
		
		inv.sort(SortedList.M_NUMBER, forward);
	}
	
	public String getStackString(int num, int max) {
		String str;
			
		if(max == 1 || num == 1)
			return "";
		
		if(num <= 0)
			str = "NaN";
		else if(num <= 999)
			str = ""+num;
		else if(num <= 999999)
			str = ""+(int)(num/1000.)+"K";
		else
			str = ""+(int)(num/1000000.)+"M";
		
		return str;
	}

	public void dropAll(vec3 pt) {
		for(int i = 0; i < 4*7; i++)
			drop(inv.get(i), pt);
	}

	public Item findItem(String name) {
		Item it;
		for(int i = 0; i < 4*7; i++) {
			it = inv.get(i);
			if(it != null)
				if(it.getName() == name)
					return it;
		}
		
		return null;
	}
	
	public boolean replaceItem(String oriIt, String newIt) {
		Item it = findItem(oriIt);
		
		if(it == null)
			return false;
		else {
			removeItem(it, true);
			addItem(newIt);
			return true;
		}
	}
}
