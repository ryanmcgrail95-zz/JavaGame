package obj.itm;

import gfx.GOGL;
import Datatypes.Inventory;
import Datatypes.vec3;

import com.jogamp.opengl.util.texture.Texture;

import cont.IO;
import cont.ItemController;
import cont.Messages;
import obj.chr.Player;
import obj.prim.Physical;


public abstract class Item extends Physical {
	private float value;
	private String name;
	private Texture sprite;
	private String info;
	private byte itemType;
	private Inventory parent;
	private int stackNum, stackMax;
	
	
	public Item(String name, float x, float y, float z) {
		super(x,y,z);
		
		this.name = name;
		value = ItemController.getValue(name);
		sprite = ItemController.getSprite(name).getFrame(0);
		info = ItemController.getInfo(name);
		itemType = ItemController.getType(name);
		type = T_ITEM;
		
		stackNum = 1;
		stackMax = ItemController.getStackMax(name);
		
		parent = null;
	}
	
	public Item(Inventory inv, String name) {
		super(0,0,0);
		visible = false;
		doUpdates = false;
		
		this.name = name;
		value = ItemController.getValue(name);
		sprite = ItemController.getSprite(name).getFrame(0);
		info = ItemController.getInfo(name);
		itemType = ItemController.getType(name);
		
		type = T_ITEM;
		
		stackNum = 1;
		stackMax = ItemController.getStackMax(name);

		parent = inv;
	}
	
	public static Item create(Inventory inv, String name) {
		byte itemType = ItemController.getType(name);
		
		Item it = null;
		switch(itemType) {
			case ItemController.IT_ACTION:
				it = new ActionItem(inv, name);
				break;
			case ItemController.IT_HEALING:
				it = new HealingItem(inv, name);
				break;
		}
		
		return it;
	}
	
	
	public static Item create(String name, vec3 pt) {
		return create(name, pt.get(0),pt.get(1),pt.get(2));
	}
	public static Item create(String name, float x, float y, float z) {
		byte itemType = ItemController.getType(name);
		
		Item it = null;
		switch(itemType) {
			case ItemController.IT_ACTION:
				it = new ActionItem(name, x,y,z);
				break;
			case ItemController.IT_HEALING:
				it = new HealingItem(name, x,y,z);
				break;
		}
		
		return it;
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
		IO.setCursor(IO.C_POINTING_FINGER);
		
		GOGL.setOrtho();
		GOGL.drawStringS(0,0, "Pick up a " + name + ".");
		GOGL.setPerspective();
		
		if(IO.getLeftClick())
			Player.getInstance().addItem(this);
	}
	
	public boolean draw() {
		//GOGL.draw3DSphere(x,y,z,20);
				
		GOGL.enableLighting();
		GOGL.transformClear();
		GOGL.transformTranslation(x,y,z);
		drawModel(name);
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
				
				if(!Messages.replace(name, "Picked up some " + name + "s.", 200))
					Messages.add("Picked up a " + name + ".",  200);
				pl.addItem(this);
				return true;
			}
		return false;
	}
	
	public void remove() {
		if(parent != null) {
			parent.removeItem(this,false);
		}
	}
	
	// ACCESSOR/MUTATOR
		public Texture getSprite() {
			return sprite;
		}
		public float getValue() {
			return value;
		}
		public String getName() {
			return name;
		}
		
		public void addStackItem() {
			stackNum++;
		}
		public void removeStackItem() {
			stackNum--;	
		}	
	
		public int getStackNum() {
			return stackNum;
		}
		public int getStackMax() {
			return stackMax;
		}

		public void makeAbstract() {
			visible = false;
			doUpdates = false;
		}
		public void makeReal(vec3 pt) {
			visible = true;
			doUpdates = true;
			
			x = pt.get(0);
			y = pt.get(1);
			z = pt.get(2);
		}
}
