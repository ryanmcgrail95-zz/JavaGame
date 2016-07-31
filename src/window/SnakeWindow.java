package window;

public class SnakeWindow extends Window {

	public static SnakeWindow inst;
	
	public SnakeWindow(int x, int y) {
		super("Snake",x,y,160,144,true);
		setScale(2);
		add(new GUISnake(16,16));
		
		inst = this;
	}
}
