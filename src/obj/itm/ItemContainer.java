package obj.itm;

import object.actor.Actor;
import object.primitive.Positionable;
import ds.vec3;
import ds.lst.CleanList;

public class ItemContainer {
	public static final byte S_VALUE = 0, S_NAME = 1;
	protected CleanList<Item> list;
	private Positionable owner;
	
	public ItemContainer(int size) {
		list = new CleanList<Item>();
		for(int i = 0; i < size; i++)
			list.add(null);
	}
	
	public int size() {return list.size();}
	public CleanList<Item> getItemList() {return list;}
	
	public void sort(byte sortType) {
		switch(sortType) {
			case S_VALUE:	list.sort(Item.Comparators.VALUE);	break;
			case S_NAME:	list.sort(Item.Comparators.NAME);	break;
		}
	}
	
	public Item get(int index) {return list.get(index);}
	
	public Item findItem(String name) {
		for(Item it : list)
			if(it != null)
				if(it.getName() == name)
					return it;
		return null;
	}
	
	public boolean replaceItem(String oriIt, String newIt) {
		Item it = findItem(oriIt);
		
		if(it == null)
			return false;
		else {
			int num = it.getStackNum();
			removeItem(it, true);
			add(newIt,num);
			return true;
		}
	}
	
	
	public void drop(Item it, vec3 pt) {
		if(it == null)
			return;
		it.makeReal(pt);
		removeItem(it, false);		
	}
	public void dropOne(Item it, vec3 pt) {
		if(it == null)
			return;
		if(it.getStackNum() == 1)
			drop(it, pt);
		else {
			it.removeStackItem();
			Item.create(it.getName(),1, pt);
		}
	}
	public void dropAll(vec3 pt) {
		for(Item it : list)
			drop(it, pt);
	}
	
	public void add(String name, int amount) {
		ItemBlueprint b = ItemController.get(name);
		
		int each = Math.min(amount, b.getStackMax()),
			n = (int) Math.ceil(1f*amount/each);
		
		for(int i = 0; i < n; i++)
			Item.create(name,each,this);
	}
	public void add(ItemBlueprint blueprint, int amount) {Item.create(blueprint.getName(),amount,this);}
	public void add(Item obj) {obj.giveTo(this);}
	
	public void removeItem(Item obj, boolean shouldDestroy) {
		for(int i = 0; i < 4*7; i++)
			if(list.get(i) == obj)
				list.set(i,null);
		
		if(shouldDestroy)
			obj.destroy();
	}

	public void destroy() {
		for(Item it : list)
			if(it != null)
				it.destroy();
	}

	public void giveAllTo(Actor user) {
		for(Item it : list)
			if(it != null)
				user.give(it);
	}
}
