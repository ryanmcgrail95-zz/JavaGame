package menu;

public abstract class MenuComponent {

	private double x, y;

	
	public MenuComponent(double x, double y) {
		x(x);
		y(y);
	}

	public final void draw() {
		draw(x,y);
	}

	public double x() 		{return x;}
	public void x(double x) {this.x = x;}

	public double y()		{return y;}
	public void y(double y) {this.y = y;}

	public abstract void draw(double x, double y);
}
