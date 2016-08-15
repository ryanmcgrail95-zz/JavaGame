package window.paint;

public class Channel {
	private int area, width, height;
	private char[] data;
	
	public Channel(int width, int height) {
		this.width = width;
		this.height = height;
		this.area = width*height;
		data = new char[area];
	}
	
	public void crop(int x, int y, int width, int height) {
		char[] newData = new char[area = width * height];

		for(int yi = 0; yi < height; yi++)
			for(int xi = 0; xi < width; xi++)
				newData[yi*width + xi] = data[(y+yi)*width + (x+xi)];
		
		data = newData;
		this.width = width;
		this.height = height;
	}
	
	public void scale(double xscale, double yscale) {
		
	}
	
	public void rotate() {
		
	}
	
	public void clear(char value) {
		for(int i = 0; i < area; i++)
			data[i] = value;
	}

	public void destroy() {
		data = null;
	}

	public int get(int x, int y) {
		return (int) data[y*width + x];
	}
}
