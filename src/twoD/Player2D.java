package twoD;

import cont.TextureController;
import functions.Math2D;
import functions.MathExt;
import gfx.GOGL;
import gfx.RGBA;
import gfx.TextureExt;
import io.Keyboard;

public class Player2D extends Actor2D {
	
	float index = 0;
	private float 	MAX_SPEED = 5, 
					GRAVITY_AIR = .4f, //.25
					GRAVITY_WALL = GRAVITY_AIR/2,
					JUMP_HEIGHT = 64, 
					JUMP_SPEED = Math2D.calcSpeed(JUMP_HEIGHT, GRAVITY_AIR),
					WALL_JUMP_YSPEED = Math2D.calcSpeed(JUMP_HEIGHT, GRAVITY_AIR),
					WALL_JUMP_XSPEED = 4; //calcXSpeed(ySpeed, ); //4
	private static Player2D instance;
	
	private static TextureExt runSprite; {
		runSprite = TextureController.loadExt("Resources/Images/ninja_run.gif", TextureController.M_NORMAL);
	}

	
	/*float calcXSpeed() {
		
	}*/
	
	public void draw2() {
		//GOGL.setOrthoPerspective();
		
		index += Math.abs(getXVelocity()/MAX_SPEED)*.25;
		
		float dX, dY, dW, dH;
		dW = dH = 16;
		dX = getX() - dW/2;
		dY = getY()+16;
		
		if(direction == LEFT) {
			dX += 16;
			dW *= -1;
		}
		
		GOGL.setColor(RGBA.WHITE);
		GOGL.drawTexture(dX, dY, dW, -dH, runSprite.getFrame(index));
	}
	
	public Player2D(float x, float y) {
		super(x, y);
		instance = this;
	}
	
	public void update() {		
		float dir = Keyboard.getWASDDir();

		float accF;
		
		if(state == ST_ON_GROUND)
			accF = .25f;
		else
			accF = .16f;
		
		float xSign = 0, ySign = 0;
		

		if(dir != -1) {
			dir = MathExt.round(dir,45);
			
			xSign = Math.signum(Math2D.calcLenX(dir));
			ySign = Math.signum(Math2D.calcLenY(dir));
			
			if(state == ST_ON_GROUND || state == ST_IN_AIR) {
				if(xSign < 0)
					direction = LEFT;
				else if(xSign > 0)
					direction = RIGHT;
				
				xVelocity += xSign*accF;
				xVelocity = MathExt.contain(-MAX_SPEED, xVelocity, MAX_SPEED);
			}
			else if(state == ST_ON_WALL) {
				int sign = ((wallState == RIGHT) ? -1 : 1);
				
				direction = !wallState;
				
				if(xSign == sign) {
					xVelocity = WALL_JUMP_XSPEED*sign;
					yVelocity = WALL_JUMP_YSPEED;
					offWall(Actor2D.ST_IN_AIR);
				}
			}
		}

		if(state == ST_ON_GROUND)
			if(xSign == 0) {
				xSign = Math.signum(xVelocity);
				
				xVelocity -= xSign*accF;
				if(xSign > 0 && xVelocity < 0)
					xVelocity = 0;
				else if(xSign < 0 && xVelocity > 0)
					xVelocity = 0;
			}
		
		if(state == ST_ON_GROUND)
			if(Keyboard.checkPressed('U')) {
				state = ST_IN_AIR;
				yVelocity += JUMP_SPEED;
			}

		
		super.update();
		
		if(state == ST_ON_WALL)
			yVelocity += -GRAVITY_WALL;
		else
			yVelocity += -GRAVITY_AIR;

		
		
		float x,y, w = 640, h = 480;
		x = getX()-w/2;
		y = getY()-h*3/4;
		
		//GOGL.getMainCamera().setUpNormal(0,1,0);
		//GOGL.getMainCamera().setProjection(x,y,0, x+.1f,y,10);
	}
	
	public static Player2D getInstance() {
		return instance;
	}
}
