package object.actor.skeleton;

import java.util.ArrayList;
import java.util.List;

import object.primitive.Positionable;

public class EnvironmentMap {
	
	private int GRID_SIZE = 64, GRID_WIDTH = 3200, GRID_HEIGHT = GRID_WIDTH, GRID_XNUM = GRID_WIDTH/GRID_SIZE, GRID_YNUM = GRID_HEIGHT/GRID_SIZE;
	private List<Positionable>[][] listArray = new ArrayList[GRID_XNUM][GRID_YNUM];
	
	public EnvironmentMap() {
		for(int r = 0; r < GRID_YNUM; r++)
			for(int c = 0; c < GRID_XNUM; c++)
				listArray[c][r] = new ArrayList<Positionable>();
	}
	
	public void add(Positionable p) {
	}
}
