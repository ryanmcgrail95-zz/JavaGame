package btl;

import java.util.List;

import io.Keyboard;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import functions.Math2D;
import functions.MathExt;
import gfx.G2D;
import gfx.GL;
import gfx.RGBA;
import gfx.TextureExt;
import object.primitive.Drawable;
import object.primitive.Updatable;
import paper.SpriteMap;
import resource.model.Model;
import time.Delta;
import time.Timer;
import datatypes.lists.CleanList;

public class BattleController extends Drawable {
	private static BattleController instance;
	private byte state, prevState;
	private final byte ST_FIRSTSTRIKE = 0, ST_PLAYER = 1, ST_ENEMY = 2, ST_ENEMY_DEATHS = 3, ST_WON_BATTLE = 4;
	
	private Timer timer = new Timer(0,0);
	
	public final float PLAYER_X = -74;
	private float 
		camX = 0, toCamX = 0,
		camZ = 32, toCamZ = 32,
		dir = 8.6f, toDir = 8.6f,
		spd = 9,
		xSpd = spd,
		zSpd = spd,
		dirSpd = spd,
		radSpd = spd;
	
	private int starPoints;
	
	private float alpha, rad = 393, toRad = 393, zPos = 17;
	
	private CleanList<BattleActor> playerActors = new CleanList<BattleActor>("BA Good");
	private CleanList<BattleActor> enemyActors = new CleanList<BattleActor>("BA Evil");
	
	private TextureExt basis = TextureController.loadExt("Resources/Images/battle.png", TextureController.M_NORMAL);
	
	public BattleController() {
		super(false,false);
		
		name = "BattleController";
		
		playerActors.add(new BattlePlayer("mario", PLAYER_X, this));
		playerActors.add(new BattlePlayer("luigi", PLAYER_X - 28, this));
		enemyActors.add(new BattleEnemy("watt", 4, this));
		//enemyActors.add(new BattleEnemy("geno", 36, this));
		//enemyActors.add(new BattleEnemy("luigi", 72, this));
		
		float row1Y = 80, row2Y = 105;
		float[] purple = {.73f,.43f,.61f};
		float[] yellow = {.78f,.65f,.12f};
		
		new BattleFlower(-110,row2Y,19, purple);
		new BattleFlower(-42,row2Y,36, purple);
		new BattleFlower(-20,row2Y,29, yellow);
		
		new BattleFlower(20,row1Y,22, purple);
		new BattleFlower(48,row1Y,29, yellow);
		new BattleFlower(87,row1Y,21, purple);
		
		instance = this;
		
		startPlayerTurn();
	}
	
	
	public void checkDead() {
		boolean isDead = false;
		for(BattleActor b : enemyActors) {
			if(b.getHP() == 0) {
				b.setAnimationHurt();
				if(!isDead) {
					b.die();
					isDead = true;
				}
			}
		}
		
		if(isDead) {
			prevState = state;
			state = ST_ENEMY_DEATHS;
		}
	}
	public void continueDeathAnimations() {
		boolean isDead = false;
		for(BattleActor b : enemyActors) {
			if(b.getHP() == 0) {
				if(!isDead) {
					b.die();
					isDead = true;
				}
			}
		}
		
		if(!isDead)
			if(enemyActors.size() > 0) {
				state = prevState;
				continueTurn();
			}
			else {
				state = ST_WON_BATTLE;
				if(playerActors.size() > 0)
					playerActors.get(0).gotoWinState();
				focusPlayer();
			}
	}
	
	public void continueTurn() {
		checkDead();
		
		if(state == ST_PLAYER)
			continuePlayerTurn();
		else if(state == ST_ENEMY)
			continueEnemyTurn();
	}
		
	private void startPlayerTurn() {
		state = ST_PLAYER;
		for(BattleActor b : playerActors)
			b.resetHasAttacked();
		continuePlayerTurn();
	}
	public void continuePlayerTurn() {	
		//Run this after each player attacks.
		focusNeutral();
		boolean didGo = false;
		for(BattleActor b : playerActors)
			if(!b.hasAttacked()) {
				b.attack();
				didGo = true;
				break;
			}
		playerActors.broke();
		if(!didGo)
			endPlayerTurn();
	}
	private void endPlayerTurn() {
		startEnemyTurn();
	}
	
	private void startEnemyTurn() {
		state = ST_ENEMY;
		
		if(enemyActors.size() == 0)
			endBattleWin();
		
		for(BattleActor b : enemyActors)
			b.resetHasAttacked();
		
		continueEnemyTurn();
	}
	public void continueEnemyTurn() {
		// Run this after each enemy attacks.
		focusNeutral();
		boolean didGo = false;
		for(BattleActor b : enemyActors)
			if(!b.hasAttacked()) {
				b.attack();
				didGo = true;
				break;
			}
		enemyActors.broke();
		if(!didGo)
			endEnemyTurn();
	}
	private void endEnemyTurn() {
		startPlayerTurn();
	}
	
	
	public void destroy() {
		for(BattleActor b : playerActors)
			b.destroy();
		playerActors.destroy();
		playerActors = null;
		
		for(BattleActor b : enemyActors)
			b.destroy();
		enemyActors.destroy();
		enemyActors = null;
		
		super.destroy();

		instance = null;
		timer.destroy();
			timer = null;
	}
	
	public void setTimer(float time) {
		timer.setMax(time);
		timer.set(time);
	}
	public float getTimer() {
		return timer.get();
	}
	public boolean checkTimer() {
		return timer.checkOnce();
	}
	
	public void endBattleWin() {
	}
	
	public void endBattleLoss() {
	}


	public BattleActor getPlayer() {
		return playerActors.get(0);
	}
	public BattleActor getEnemy() {
		return enemyActors.get(0);
	}


	public void remove(BattleActor act) {
		if(act.isPlayer)
			playerActors.remove(act);
		else
			enemyActors.remove(act);
	}
	
	public float calcDepth() {
		return 1;
	}


	@Override
	public void update() {
		/*if(Keyboard.checkDown('H'))
			rad++;
		else if(Keyboard.checkDown('J'))
			rad--;
		
		if(Keyboard.checkDown('I'))
			camZ++;
		else if(Keyboard.checkDown('K'))
			camZ--;
		
		if(Keyboard.checkDown('O'))
			dir++;
		else if(Keyboard.checkDown('L'))
			dir--;
		
		if(Keyboard.checkDown('N'))
			camX--;
		else if(Keyboard.checkDown('M'))
			camX++;
			
		
		System.out.println(rad + ", " + camX + ", " + camZ + ", "+ dir);*/
		updateCamera();
	}

	public void focusPlayer() {
		toRad = 205;
		toCamX = -74;
		toCamZ = 18;
		toDir = 8.6f;
		xSpd = spd;
		zSpd = spd;
		dirSpd = spd;
		radSpd = spd;
	}
	
	public void focusEnemy() {
		toRad = 374;
		toCamX = 27;
		toCamZ = 32;
		toDir = 8.6f;
		xSpd = spd;
		zSpd = spd;
		dirSpd = spd;
		radSpd = spd;
	}
	
	public void focusNeutral() {
		toRad = 393;
		toCamX = 0;
		toCamZ = 32;
		toDir = 8.6f;
		xSpd = spd;
		zSpd = spd;
		dirSpd = spd;
		radSpd = spd;
	}
	
	public void focusPlayerPrepareJump(float midX) {
		toRad = 286;
		toCamX = midX; //6
		toCamZ = 18;
		toDir = 8.6f;
		xSpd = spd;
		zSpd = spd;
		dirSpd = spd;
		radSpd = spd;
	}
	public void focusEnemyJump(float enX) {
		toRad = 290;
		toCamX = enX*.7f; //-37
		toCamZ = 28;
		toDir = 8.6f;
		xSpd = .7f*spd;
		zSpd = spd;
		dirSpd = spd;
		radSpd = spd;
	}
	public void focusPlayerJump(float midX, float z) {
		toRad = 286;
		toCamX = midX; //6
		toCamZ = 18 + z*.25f;
		toDir = 8.6f;
		xSpd = spd;
		zSpd = .2f*spd;
		dirSpd = spd;
		radSpd = spd;
	}
	
	public void focusSpike(float focX, float uSpd) {
		toRad = 290;
		toCamX = focX;
		toCamZ = 21;
		toDir = 8.6f;
		
		xSpd = uSpd*.5f;
		zSpd = uSpd;
		dirSpd = uSpd;
		radSpd = uSpd;
	}
	
	private void updateCamera() {
		rad = MathExt.to(rad, toRad, radSpd);
		camX = MathExt.to(camX, toCamX, xSpd);
		camZ = MathExt.to(camZ, toCamZ, zSpd);
		dir = MathExt.to(dir, toDir, dirSpd);
		
		float toX,toY,toZ, x,y,z, r, d;
		toX = camX;
		toY = 0;
		toZ = camZ + zPos; //45?
		
		x = toX;
		y = toY - Math2D.calcLenX(rad,dir);
		z = toZ + Math2D.calcLenY(rad,dir);
		
		GL.getMainCamera().setProjection(x,y,z,toX,toY,toZ);
	}

	@Override
	public void draw() {
		float dH, dY;
		dH = -1f*basis.getHeight()/basis.getWidth()*640;
		dY = GL.getScreenHeight()/2f - dH/2;
		
		
		GL.setPerspective();
		GL.setColor(RGBA.WHITE);
			Model.get("Battle-Pleasant_Path_1_Flowerless").draw();
			
		
		if(Keyboard.checkDown('a')) {		
			GL.setOrtho();
			GL.setColor(RGBA.WHITE);
			G2D.drawTexture(0,dY, 640,dH, basis.getTexture());
			
			
			float d, rX, rY, x, y;
			d = GL.getTime();
			rX = 325;
			rY = 300;
					
			x = Math2D.calcLenX(rX,d);
			y = Math2D.calcLenY(rY,d);
			
			G2D.fillPolygon(x,y, 32, 16);
		}
	}


	@Override
	public void add() {
	}

	public List<BattleActor> getEnemyActors() {
		return enemyActors;
	}

	public static BattleController getInstance() {
		return instance;
	}


	public void addStarPoints(int num) {starPoints += num;}
	public int getStarPoints() {return starPoints;}
}