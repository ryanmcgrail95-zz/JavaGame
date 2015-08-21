package obj.itm;

import object.actor.Actor;
import object.environment.Fire;
import sts.Stat;

public class ActionItem extends Item {
	
	public ActionItem(String name, int amount) {super(name, amount);}	

	public void use(Actor user) {
		switch(getName()) {
			case "Logs":
				if(user.getInventory().findItem("Tinderbox") == null) {
					user.getStat().addExp(Stat.EXP_FIREMAKING, 40);
					new Fire(user.getX(),user.getY());
					user.moveToLenDir(-32,user.getDirection());
					destroy();
				}
				break;
		}
	}
}
