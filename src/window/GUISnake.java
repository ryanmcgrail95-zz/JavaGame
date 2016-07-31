package window;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import cont.TextureController;
import ds.lst.CleanList;
import functions.Math2D;
import functions.MathExt;
import games.Picross;
import games.Snake;
import games.Snake.Cell;
import gfx.G2D;
import gfx.GL;
import gfx.MultiTexture;
import gfx.RGBA;
import io.Controller;
import io.Keyboard;
import io.Mouse;
import object.primitive.Drawable;
import time.Delta;
import time.Timer;

public class GUISnake extends GUIDrawable {

	private final int BOARD_OFFSET_X = 60, BOARD_OFFSET_Y = 52;

	private TileEditor tileEditor;

	private CleanList<Person> personList = new CleanList<Person>("Persons");

	private int[] jiggle = { 0, 1, 1, 0, -1, -1 };
	private int boardTime = 0, gameTime;

	private final static RGBA GRAY_L = RGBA.createi(168, 168, 168), GRAY_D = RGBA.createi(96, 96, 96);
	private static final MultiTexture sprites = new MultiTexture("Resources/Images/snake.png", 8, 8);
	private static final MultiTexture person = new MultiTexture("Resources/Images/person.png", 8, 8);
	private MultiTexture tileSprite = new MultiTexture("Resources/Images/environment.png", 16, 16);

	private float index = 0;
	
	private int mapW, mapH;

	private boolean inEditor = false;

	private Snake board;
	public final static byte CELL_EMPTY = Picross.CELL_EMPTY, CELL_FILLED = Picross.CELL_FILLED,
			CELL_MARKED = Picross.CELL_MARKED;
	private float[] BOUNDS_HEAD = { 0, 0, 6f / 32, 6f / 32 };

	private int xNum, yNum;

	private boolean[][] collisions;

	private boolean hasStarted = false;
	private Timer moveTimer = new Timer(8);

	public GUISnake(int xNum, int yNum) {
		super(0, 0, 160, 144);

		this.xNum = xNum;
		this.yNum = yNum;

		board = new Snake(xNum, yNum, xNum + yNum);
		
		tileEditor = new TileEditor();
		mapW = tileEditor.getMapXNumber();
		mapH = tileEditor.getMapYNumber();

		collisions = tileEditor.getCollisions();

		for (int i = 0; i < 20; i++)
			personList.add(new Person(0, 0));
	}

	public byte draw(float frameX, float frameY) {

		GL.disableBlending();

		GL.setColor(RGBA.WHITE);

		try {
			if (Keyboard.checkPressed('p'))
				tileEditor.saveTiles();
			else if (Keyboard.checkPressed('l'))
				tileEditor.loadTiles();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Keyboard.checkPressed('q'))
			inEditor = !inEditor;

		if (inEditor) {
			runEditor(frameX, frameY);
		} else
			runGame(frameX, frameY);

		GL.setColor(RGBA.WHITE);

		return 0;
	}

	public void runEditor(float frameX, float frameY) {
		float scale = 1 / getParent().getScale(), mouseX = scale * (Mouse.getMouseX() - this.getScreenX()),
				mouseY = scale * (Mouse.getMouseY() - this.getScreenY());

		tileEditor.draw(frameX, frameY, mouseX, mouseY, w(), h());
	}

	public void drawMap(float dX, float dY) {
		int w = tileEditor.getMapWidth(), h = tileEditor.getMapHeight();
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				tileEditor.drawMap(dX + x * w, dY + y * h);
	}

	public void runGame(float frameX, float frameY) {
		drawMap(frameX, frameY);

		int dir = (int) Controller.getDirPressed();

		if (dir != -1)
			board.setSnakeDirection((int) MathExt.snap(dir, 90));
		if (moveTimer.check()) {
			board.update();
			boardTime++;
		}
		gameTime++;

		moveTimer.setMax(16 - 14 * 1f * board.getSnakeLength() / board.getSnakeLengthMax());

		// Draw Cells
		int v, dX, dY, dBS = 8, dS = 8;
		Cell st;

		index += .1;

		GL.setColor(RGBA.WHITE);
		for (int r = 0; r < yNum; r++)
			for (int c = 0; c < xNum; c++) {
				st = board.getCell(c, r);

				dX = BOARD_OFFSET_X + ((c - 1) * dBS - 3);
				dY = BOARD_OFFSET_Y + ((r - 1) * dBS - 3);

				if (st.hasItem) {
					int ind = 1 + (int) (index % 4) * 8;
					G2D.drawTexture(dX, dY, dS, dS, sprites, ind);
				} else {
					int amt = st.snakeAmount, d = st.snakeDirection,
							wiggleIndex = (amt + boardTime + gameTime / 20) % jiggle.length, wig = jiggle[wiggleIndex],
							jiggleIndex = (amt + gameTime / 10) % (jiggle.length / 2), jig = jiggle[jiggleIndex];

					dX += Math2D.calcLenX(wig, d + 90);
					dY += Math2D.calcLenY(wig, d + 90);

					/*
					 * dX += Math2D.calcLenX(jig, d); dY += Math2D.calcLenY(jig,
					 * d);
					 */

					if (amt == board.getSnakeLength())
						G2D.drawTexture(dX, dY, dS, dS, sprites, 8 * d / 90);
					else if (amt == 1)
						G2D.drawTexture(dX, dY, dS, dS, sprites, 2 + 8 * d / 90);
					else if (amt > 0)
						G2D.drawTexture(dX, dY, dS, dS, sprites, 1);
				}
			}

		for (Person p : personList)
			p.update();

		sortPersons();

		for (Person p : personList)
			p.draw();
	}

	private void sortPersons() {
		personList.sort(Comparators.DEPTH);
	}

	private enum State {
		STILL, MOVING, ATTACKING, ANGRY, SHIVERING, MINING, JUMPING, FOLLOW, APPROACH, WAVING, WOBBLING
	};

	private class Task {
		private State type;
		private float time;
		private int x, y;
		private Person other;

		public Task(State type, float time, int x, int y) {
			this.type = type;
			this.time = time;
			this.x = x;
			this.y = y;
		}

		public Task(State type, float time, int x, int y, Person other) {
			this.type = type;
			this.time = time;
			this.x = x;
			this.y = y;
			this.other = other;
		}

		public State getType() {
			return type;
		}

		public float getTime() {
			return time;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Person getOther() {
			return other;
		}

		public void update() {
			time = Math.max(0, time - Delta.calcDeltaTime());
		}
	}

	public static class Comparators {
		public final static Comparator<Person> DEPTH = new Comparator<Person>() {
			public int compare(Person o1, Person o2) {
				return (int) (o1.getY() - o2.getY());
			}
		};
		public final static Comparator<Node> PATH_DISTANCE = new Comparator<Node>() {
			public int compare(Node o1, Node o2) {
				return (int) (o1.value - o2.value);
			}
		};
	}

	private static LinkedList<Node> nodeList = new LinkedList<Node>();

	private class Node {
		float x, y;
		float value;
		Node parent;

		float gCost, hCost;

		public Node(float x, float y, float value, float gCost, float hCost, Node parent) {
			this.x = x;
			this.y = y;
			this.value = value;

			this.gCost = gCost;
			this.hCost = hCost;

			this.parent = parent;

			nodeList.add(this);
		}

		public void clear() {
			parent = null;
		}

		public boolean equals(Node other) {
			return this.x == other.x && this.y == other.y;
		}
	}

	private class Person {
		int xDir, yDir, lastXDir, lastYDir;
		float x, y, lastX, lastY;
		private Queue<Task> taskList = new LinkedList<Task>();

		public Person(float x, float y) {
			this.x = lastX = x;
			this.y = lastY = y;

			float r = MathExt.rndf();

			/*if (r < .3 && personList.size() > 0)
				_followRandom();
			else*/
			randomAction();
		}

		private void randomAction() {
			float r = MathExt.rndf();

			if (r < .3)
				_moveToRandom();
			else if (r < .4)
				_wobblingRandom();
			else if (r < .5)
				_standRandom();
			else if (r < .6)
				_wavingRandom();
			else if (r < .7)
				_shiveringRandom();
			else if (r < .8)
				_mineRandom();
			else
				_jumpRandom();
		}

		private void _moveTo(float toX, float toY) {
			PriorityQueue<Node> openQueue = new PriorityQueue<Node>(Comparators.PATH_DISTANCE);
			List<Node> closedList = new LinkedList<Node>();
			Stack<Node> path = new Stack<Node>();
			Node target = null;

			int[] xs = { 1, 0, -1, 0 }, ys = { 0, -1, 0, 1 };

			boolean alreadyDone;

			openQueue.add(new Node(x, y, 0, 0, 0, null));

			Node current;
			int steps = 1000;
			float curX, curY, newX, newY, g, h;

			int xi, yi, xi2, yi2;

			while ((current = openQueue.peek()) != null && steps-- > 0) {
				openQueue.remove();
				closedList.add(current);
				
				curX = current.x;
				curY = current.y;

				xi = Math.round(curX / 8);
				yi = Math.round(curY / 8);
								
				for (int i = 0; i < 4; i++) {
					xi2 = xi + xs[i];
					yi2 = yi + ys[i];
					newX = 8 * xi2;
					newY = 8 * yi2;

					if(xi2 < 0 || xi2 >= mapW || yi2 < 0 || yi2 >= mapH)
						continue;
					if (collisions[xi2][yi2])
						continue;

					if (toX == newX && toY == newY) {
						target = new Node(newX, newY, 0, 0, 0, current);
						break;
					}

					alreadyDone = false;
					for (Node n : nodeList)
						if (n.x == newX && n.y == newY)
							alreadyDone = true;

					if (!alreadyDone) {
						g = current.gCost + 10;
						h = 10 * (Math.abs(newX - toX) + Math.abs(newY - toY));

						openQueue.add(new Node(newX, newY, g + h, g, h, current));
					}
				}

				if (target != null)
					break;
			}

			if(target != null) {
				while (target != null) {
					path.push(target);
					target = target.parent;
				}

				System.out.println("------");
				while(path.size() > 0) {				
					current = path.pop();
					System.out.println("(" + current.x + ", " + current.y + ")");
					addTask(new Task(State.MOVING, 0, (int) Math.round(current.x), (int) Math.round(current.y)));					
				}
				System.out.println("______");

				printTasks();
			}
			
			for (Node n : nodeList)
				n.clear();
			nodeList.clear();
			openQueue.clear();
			closedList.clear();
			path.clear();
		}

		private void _moveToRandom() {
			_moveTo(MathExt.rndi(0, 16)*8, MathExt.rndi(0,16)*8);
		}

		private void _followRandom() {
			Person p = personList.getRandom();

			addTask(new Task(State.FOLLOW, 0, 0, 0, p));
		}

		private void _standRandom() {
			addTask(new Task(State.STILL, MathExt.rndf() * 30, 0, 0));
		}

		private void _mineRandom() {
			addTask(new Task(State.MINING, MathExt.rndf() * 30, MathExt.rndSign(), 0));
		}

		private void _jumpRandom() {
			addTask(new Task(State.JUMPING, MathExt.rndf() * 30, MathExt.rndSign(), 0));
		}

		private void _wavingRandom() {
			addTask(new Task(State.WAVING, MathExt.rndf() * 30, MathExt.rndSign(), 0));
		}

		private void _shiveringRandom() {
			addTask(new Task(State.SHIVERING, MathExt.rndf() * 30, MathExt.rndSign(), 0));
		}

		private void _wobblingRandom() {
			addTask(new Task(State.WOBBLING, MathExt.rndf(120, 240), MathExt.rndSign(), 0));
		}

		private boolean approachPoint(float toX, float toY, boolean run) {
			return approachPoint(toX, toY, Math.abs(toX - x) > Math.abs(toY - y), run);
		}

		private boolean approachPoint(float toX, float toY, boolean horiFirst, boolean run) {
			xDir = (int) Math.signum(toX - x);
			yDir = (int) Math.signum(toY - y);
			
			float oriX = x, oriY = y;

			float spd = (run) ? .5f : .25f;
			boolean hasArrived = false;
			

			if (horiFirst) {
				if (x != toX && (y % 8) == 0)
					x = MathExt.toLinear(x, toX, spd);
				else if (y != toY)
					y = MathExt.toLinear(y, toY, spd);
				else
					hasArrived = true;
			} else {
				if (y != toY && (x % 8) == 0)
					y = MathExt.toLinear(y, toY, spd);
				else if (x != toX)
					x = MathExt.toLinear(x, toX, spd);
				else
					hasArrived = true;
			}

			if (!hasArrived) {
				if ((Math.ceil(oriX / 8) * 8 == Math.floor(x / 8) * 8
						|| Math.floor(oriX / 8) * 8 == Math.ceil(x / 8) * 8) && (horiFirst || x != toX))
					lastX = x;
				if ((Math.ceil(oriY / 8) * 8 == Math.floor(y / 8) * 8
						|| Math.floor(oriY / 8) * 8 == Math.ceil(y / 8) * 8) && (!horiFirst || y != toY))
					lastY = y;
			} else {
				lastX = x - 8 * lastXDir;
				lastY = y - 8 * lastYDir;
			}

			return hasArrived;
		}

		private Task getTask() {
			return taskList.peek();
		}

		private void addTask(Task t) {
			taskList.add(t);
		}

		private void completeTask() {
			taskList.remove();
		}

		public void printTasks() {
			System.out.println("Tasks[" + taskList.size() + "] [");
			
			Task t = getTask();
			System.out.println(t.getX() + ", " + t.getY());
			System.out.println("]");
		}
		
		private int getXDir() {
			return lastXDir;
		}

		private int getYDir() {
			return lastYDir;
		}

		private float getX() {
			return x;
		}

		private float getY() {
			return y;
		}

		private float getLastX() {
			return lastX;
		}

		private float getLastY() {
			return lastY;
		}

		public void update() {
			Task t = getTask();

			if (t != null) {
				State s = t.getType();

				if (s == State.MOVING) {
					if (approachPoint(t.getX(), t.getY(), false)) {
						completeTask();
						
						if(taskList.size() == 0)
							randomAction();
					}

					if (xDir != 0 || yDir != 0) {
						lastXDir = xDir;
						lastYDir = yDir;
					}
				} /*else if (s == State.FOLLOW) {
					Person other = t.getOther();
					approachPoint(other.getLastX(), other.getLastY(), false);
				} else if (s == State.APPROACH) {
					Person other = t.getOther();
					approachPoint(other.getX() - 8 * other.getXDir(), other.getY() - 8 * other.getYDir(), true);
				}*/ else {
					xDir = t.getX();
					yDir = t.getY();

					if (t.getTime() == 0) {
						completeTask();
						
						if(taskList.size() == 0)
							randomAction();
					}
				}

				t.update();
			}
		}

		public void draw() {
			Task current = getTask();
			State state = (current != null) ? current.getType() : State.STILL;

			float dx = x, dy = y, dw = 8, dh = 8;

			if (xDir == -1) {
				dx += dw;
				dw = -dw;
			}

			dy -= 1;

			float index = (gameTime / 8);

			person.draw(dx, dy + 1, dw, dh, 7);

			if (state == State.JUMPING) {
				person.draw(dx, dy, dw, dh, index, 20, 4);
			} else if (state == State.WAVING) {
				person.draw(dx, dy, dw, dh, index, 2, 2);
			} else if (state == State.WOBBLING) {
				person.draw(dx, dy, dw, dh, index / 2, 28, 4);
			} else if (state == State.ATTACKING) {
				person.draw(dx, dy, dw, dh, index, 40, 4);
			} else if (state == State.ANGRY) {
				person.draw(dx, dy, dw, dh, index, 48, 2);
			} else if (state == State.SHIVERING) {
				person.draw(dx, dy, dw, dh, index, 12, 4);
			} else if (state == State.MINING)
				person.draw(dx, dy, dw, dh, index, 16, 4);
			else if (state == State.MOVING || state == State.FOLLOW || state == State.APPROACH) {
				if (state == State.FOLLOW)
					GL.forceColor(RGBA.RED);

				if (xDir != 0)
					person.draw(dx, dy, dw, dh, index, 24, 4);
				else if (yDir != 0)
					person.draw(dx, dy, dw, dh, index, 32, 4);
				else
					person.draw(dx, dy, dw, dh, index, 0, 2);
			} else if (state == State.STILL)
				person.draw(dx, dy, dw, dh, index, 0, 2);
			else
				System.out.println("Unhandled state: " + state);

			GL.unforceColor();
		}
	}

	@Override
	public void destroy() {
		personList.clear();
	}

	@Override
	public boolean checkMouse() {
		return false;
	}
}
