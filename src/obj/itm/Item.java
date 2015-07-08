package obj.itm;

import io.IO;
import io.Mouse;
import gfx.GOGL;
import gfx.TextureExt;
import Datatypes.Inventory;
import Datatypes.SortedList;
import Datatypes.vec3;

import com.jogamp.opengl.util.texture.Texture;

import cont.Messages;
import object.actor.Actor;
import object.actor.Player;
import object.primitive.Physical;


public abstract class Item extends Physical {
	private ItemBlueprint blueprint;
	private Inventory parent;
	private int stackNum;
	
	
	public Item(String name, int amount) {
		super(0,0,0);
		
		blueprint = ItemController.get(name);
		type = T_ITEM;
		stackNum = amount;
		parent = null;
	}
			
	
	private static Item create(ItemBlueprint type, int amount) {return create(type.getName(),amount);}
	private static Item create(String name, int amount) {
		byte itemType = ItemController.get(name).getType();
		
		Item it;
		switch(itemType) {
			case ItemController.IT_ACTION:	it = new ActionItem(name, amount); break;
			case ItemController.IT_HEALING:	it = new HealingItem(name, amount); break;
			default:						it = null;
		}
		
		return it;
	}
	
	public static Item create(String name, int amount, vec3 pos) {return create(name,amount,pos.x(),pos.y(),pos.z());}
	public static Item create(String name, int amount, float x, float y, float z) {
		Item i = create(name,amount);
		i.makeReal(x,y,z);
		return i;
	}
	public static Item create(String name, int amount, Inventory owner) {
		Item i = create(name,amount);
		i.giveTo(owner);
		return i;
	}
	
	
	public void land() {
		if(getZVelocity() < .5) {
	        setZVelocity(-getZVelocity()*.3f);
	        setXYSpeed(getXYSpeed()*.3f);
	    }
	    else
	    	setVelocity(0);
	}
	
	public void hover() {
		Mouse.setFingerCursor();
		
		GOGL.setOrtho();
		GOGL.drawStringS(0,0, "Pick up a " + getName() + ".");
		GOGL.setPerspective();
		
		if(Mouse.getLeftClick())
			Player.getInstance().give(this);
	}
	
	public boolean draw() {
		//GOGL.draw3DSphere(x,y,z,20);
				
		GOGL.enableLighting();
		GOGL.transformClear();
		GOGL.transformTranslation(x,y,z);
		drawModel(blueprint.getName());
		GOGL.transformClear();
		GOGL.disableLighting();
		
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
	
		
		return false;
	}
	
	public static void drawModel(String name) {
		float s = 2.5f, w = 1.5f;
		switch(name) {
			case "Rune Bar": // Drawing 3D Metal Bar
				GOGL.setLightColori(67,89,99);
				GOGL.transformScale(.25f);
				GOGL.transformScale(10,7,10);
				GOGL.transformRotationZ(45);
					GOGL.draw3DFrustem(0,0,1,3.5f,2f,.8f,4);
					GOGL.draw3DFrustem(4f,1f,4);
				break;
				
			case "Water Bucket":
				s = 2.5f; w = 1.5f;
				GOGL.transformScale(.25f);
				GOGL.transformScale(10);
				GOGL.transformRotationZ(45);
					GOGL.transformRotationX(-90);
					GOGL.setLightColori(101,97,96);
					GOGL.draw3DFrustem(0,-4,-w/2, s,s,w,6, false);
					GOGL.transformRotationX(90);
					GOGL.setLightColori(118,118,136);
					GOGL.draw3DFrustem(0,0,3.5f, s*.9f,s*.9f,0,6, true);
					GOGL.setLightColori(128,103,57);
					GOGL.draw3DFrustem(2f,3f,4f,6,false);					
				break;
			case "Empty Bucket":
				s = 2.5f; w = 1.5f;
				GOGL.transformScale(.25f);
				GOGL.transformScale(10);
				GOGL.transformRotationZ(45);
					GOGL.transformRotationX(-90);
					GOGL.setLightColori(101,97,96);
					GOGL.draw3DFrustem(0,-4,-w/2, s,s,w,6, false);
					GOGL.transformRotationX(90);
					GOGL.setLightColori(128,103,57);
					GOGL.draw3DFrustem(2f,3f,4f,6,false);					
				break;
				
			case "Empty Bowl":
				GOGL.transformScale(.25f);
				GOGL.transformScale(10);
				GOGL.transformRotationZ(45);
					GOGL.setLightColori(128,103,57);
					GOGL.draw3DFrustem(1.5f,2f,.5f,6);
					GOGL.draw3DFrustem(0,0,.5f, 1.5f,4f,2f,6,false);
					GOGL.draw3DFrustem(0,0,.5f, 2f,4f,2f,6,false);					
				break;

			case "Logs":
				s = 2.1f;
				w = 8f;
				GOGL.transformScale(.25f);
				GOGL.transformScale(10);
				GOGL.transformRotationZ(45);
				GOGL.transformRotationX(-90);
					GOGL.setLightColori(56, 40, 30);
					GOGL.draw3DFrustem(s,-1,-w/2, 2f,2f,w,6);
					GOGL.draw3DFrustem(-s,-1,-w/2, 2f,2f,w,6);
					GOGL.draw3DFrustem(0,-1-2.3f,-w/2, 2f,2f,w,6);
				break;
		}
		
		GOGL.setColor(1,1,1,1);
	}
	
	public abstract void use();
	
	public boolean collide(Physical other) {
		if(!doUpdates)
			return false;
		
		if(other.isPlayer()) 
			if(collideSize(other)) {
				Player pl = (Player) other;
				
				if(!Messages.replace(getName(), "Picked up some " + getName() + "s.", 200))
					Messages.add("Picked up a " + getName() + ".",  200);
				pl.give(this);
				return true;
			}
		return false;
	}
	
	public void remove() {
		if(parent != null) {
			parent.removeItem(this,false);
			parent = null;
		}
	}
	public void destroy() {
		remove();
		super.destroy();
	}
	
	// ACCESSOR/MUTATOR
	public TextureExt getSprite() 			{return blueprint.getSprite();}
	public Texture getSprite(float frame) 	{return getSprite().getFrame(frame);}
	public float getValue() 				{return blueprint.getValue();}
	public String getName() 				{return blueprint.getName();}		
	public int getStackMax() 				{return blueprint.getStackMax();}
		
	// STACK
	public void setStackNum(int num) 	{stackNum = num;}
	public int getStackNum() 			{return stackNum;}
	public void addStackItem(int num) 	{stackNum += num;}
	public void addStackItem() 			{stackNum++;}
	public void removeStackItem() 		{stackNum--;}	
	public boolean isFull() 			{return stackNum == getStackMax();}

		
		public void giveTo(Actor a) {giveTo(a.getInventory());}
		public void giveTo(Inventory inv) {			
			SortedList<Item> list = inv.getItemList();
			int size = list.size();
			Item it;
			
			remove();
			
			for(int i = 0; i < size; i++) {
				it = list.get(i);
				
				if(it != null)
					if(it.getName().equals(getName()))
						if(!it.isFull()) {
							it.addStackItem();
							destroy();
							return;
						}
			}
			
			for(int i = 0; i < size; i++) {
				it = list.get(i);

				if(it == null) {
					parent = inv;
					list.set(i, this);
					setVisible(false);
					setDoUpdates(false);
					return;
				}
			}
			
			// Otherwise, Can't be Owned!!
			makeReal(inv.getOwner().getPosition());
		}
		public void makeReal(vec3 pt) {makeReal(pt.x(),pt.y(),pt.z());}
		public void makeReal(float x, float y, float z) {
			remove();
			visible = true;
			doUpdates = true;
			
			this.x = x;
			this.y = y;
			this.z = z;
		}

	public Actor getOwner() {return parent.getOwner();}
}