package window;

public class SnakeWindow extends Window {

	public static SnakeWindow inst;
	
	public SnakeWindow(int x, int y) {
		super("Snake",x,y,16,16,true);
		setScale(8);
		add(new GUISnake(16,16));
		
		inst = this;
	}
}
