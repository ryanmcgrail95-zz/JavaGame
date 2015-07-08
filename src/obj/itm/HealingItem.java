package obj.itm;

import object.actor.Actor;
import Datatypes.Inventory;

public class HealingItem extends Item {
	public float healthBoost = 10;
	
	public HealingItem(String name, int amt) {super(name, amt);}

	public void use() {
		getOwner().heal(healthBoost);
		remove();
	}

}
