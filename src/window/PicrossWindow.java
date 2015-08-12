package window;

public class PicrossWindow extends Window {

	public static PicrossWindow inst;
	
	public PicrossWindow(int x, int y) {
		super("Picross",x,y,160,144,true);
		setScale(2);
		add(new GUIPicross());
		
		inst = this;
	}
}
