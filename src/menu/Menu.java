package menu;

import ds.lst.CleanList;

public abstract class Menu {
	private static CleanList<Menu> menuList = new CleanList<Menu>("Menu");

	private float x, y;
	
	
	public Menu(float x, float y) {
		this.x = x;
		this.y = y;
		
		menuList.add(this);
	}
	
	public static void ini() {
		new PartnerList(300,200);
	}
	
	public abstract void draw();
	
	public float getX() {return x;}
	public float getY() {return y;}
	
	public static void drawAll() {
		for(Menu u : menuList)
			u.draw();
	}
}
