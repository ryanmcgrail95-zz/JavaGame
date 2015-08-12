package obj.itm;

import datatypes.Inventory;
import object.actor.Actor;

public class HealingItem extends Item {
	public float healthBoost = 10;
	
	public HealingItem(String name, int amt) {super(name, amt);}

	public void use(Actor user) {
		user.heal(healthBoost);
		remove();
	}
}
