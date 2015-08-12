package object.environment;

public class Town {

	public Town(float x, float y) {
		new House(x, y - 3*130);
		new House(x, y - 130);
		new House(x, y + 130);
	}
}
