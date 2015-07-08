package obj.itm;

import object.actor.Actor;
import object.environment.Fire;
import sts.Stat;
import Datatypes.Inventory;

public class ActionItem extends Item {
	
	public ActionItem(String name, int amount) {super(name, amount);}	

	public void use() {
		Actor owner = getOwner();
		switch(getName()) {
			case "Logs":
				if(owner.getInventory().findItem("Tinderbox") == null) {
					owner.getStat().addExp(Stat.EXP_FIREMAKING, 40);
					new Fire(owner.getX(),owner.getY());
					owner.moveToLenDir(-32,owner.getDirection());
					destroy();
				}
				break;
		}
	}
}
