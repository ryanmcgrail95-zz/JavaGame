package menu;

import java.util.ArrayList;
import java.util.List;

import gfx.G2D;

public abstract class ListMenu<T> extends Menu {
	private List<T> itemList;
	
	public ListMenu(float x, float y) {
		super(x, y);
		
		itemList = new ArrayList<T>();
	}
	
	public void add(T item) {
		itemList.add(item);
	}
	
	public void draw() {
		draw(getX(), getY());
	}
	public void draw(float x, float y) {
		G2D.rrectangle(x,y, 200,100, 8);
		for(T item : itemList)
			y += drawItem(item, x,y);
	}

	protected float drawItem(T obj, float x, float y) {
		G2D.drawString(x,y, obj.toString());
		return 15;
	}
}
