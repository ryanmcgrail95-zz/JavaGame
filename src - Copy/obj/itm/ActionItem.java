package obj.itm;

import ds.Inventory;

public class ActionItem extends Item {
	
	
	public ActionItem(String name, float x, float y, float z) {
		super(name, x,y,z);
	}
	public ActionItem(Inventory inv, String name) {
		super(inv, name);
	}
	
	public void use() {
		
	}
}
