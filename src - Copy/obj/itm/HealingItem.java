package obj.itm;

import Datatypes.Inventory;

public class HealingItem extends Item {
	public float healthBoost;
	
	public HealingItem(String name, float x, float y, float z) {
		super(name, x,y,z);
	}
	public HealingItem(Inventory inv, String name) {
		super(inv, name);
	}

	public void use() {
		remove();
	}

}
