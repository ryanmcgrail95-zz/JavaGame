package menu;

import ds.lst.CleanList;

public class Menu extends MenuComponent {
	private static CleanList<Menu> menuList = new CleanList<Menu>("Menu");
	private CleanList<MenuComponent> compList = new CleanList<MenuComponent>("Menu");	
	
	private double w, h;
	
	public Menu(double x, double y, double w, double h) {
		super(x,y);
		
		w(w);
		h(h);
		
		menuList.add(this);
	}
	
	public static void ini() {
		//new PartnerList(300,200);
	}

	public void draw(double x, double y) {
		for()
	}
	
	public double w() 		{return w;}
	public void w(double w) {this.w = w;}

	public double h()		{return h;}
	public void h(double h) {this.h = h;}

	
	public static void drawAll() {
		for(Menu u : menuList)
			u.draw();
	}
}
