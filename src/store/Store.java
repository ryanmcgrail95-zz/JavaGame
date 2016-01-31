package store;

public class Store {
	private StoreBlock[][] layout = new StoreBlock[8][8];
	
	public void draw() {
		for(int c = 0; c < 8; c++)
			for(int r = 0; r < 8; r++)
				layout[c][r].draw();				
	}
}
