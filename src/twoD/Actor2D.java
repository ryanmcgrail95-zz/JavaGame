package twoD;

import gfx.GOGL;
import gfx.RGBA;

public abstract class Actor2D extends Positionable2D {
	protected byte state;
	protected boolean wallState;
	
	protected boolean direction = RIGHT;
	
	private double xScale = 1, yScale = 1;
	
	protected final static byte ST_ON_GROUND = 0, ST_IN_AIR = 1, ST_ON_WALL = 2, ST_ON_LADDER = 3;
	public static final boolean LEFT = false, RIGHT = true;
	
	public Actor2D(float x, float y) {
		super(x, y, false, false);
	}

	public void update() {
		super.update();
		
		Terrain2D.getInstance().collide(this);
	}
	
	@Override
	public float calcDepth() {
		return 1;
	}
	
	public void draw() {
		//GOGL.setOrthoPerspective();
		
		float dX, dY, dW, dH;
		dW = dH = 16;
		dX = getX() - dW/2;
		dY = getY();
		
		GOGL.setColor(RGBA.BLACK);
		GOGL.fillRectangle(dX, dY, dW, dH);
	}

	public void add() {}
	
	public void land(float flY) {
		y = flY;
		yVelocity = 0;
		state = ST_ON_GROUND;
	}

	public void slideOnWall(boolean wall) {
		state = ST_ON_WALL;
		wallState = wall;
	}

	public void offWall(byte newState) {
		if(state == ST_ON_WALL)
			state = newState;
	}
}
