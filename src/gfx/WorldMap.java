package gfx;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import object.actor.Player;
import object.primitive.Drawable;
import phone.HoldClosable;
import resource.sound.Sound;

public class WorldMap extends HoldClosable {
	private static WorldMap instance;
	private Camera topCam;
	private FBO map;
	private float scale = .15f; //.2
	private int size = 300;
	
	private float openFrac;
	
	public WorldMap() {
		super(false,true);
		topCam = new Camera(Camera.PR_ORTHOPERSPECTIVE,size,size);
		map = new FBO(GOGL.gl,1024,1024);
		map.clear(GOGL.gl,RGBA.WHITE);
		instance = this;
		
		topCam.setEnabled(false);
	}
	
	public void update() {
		//Player p = Player.getInstance();
		//topCam.focus(400-p.z(), 90, 89.5f, p);
	}
	
	public void render() {
		float cX,cY;
		cX = topCam.getX()*scale;
		cY = topCam.getY()*scale;
		
		cX = map.getWidth()-cX;
		cY = map.getHeight()-cY;
		
		float sS = scale*size;
		
		map.attach(GOGL.gl);
			GOGL.setColor(RGBA.WHITE);
			GOGL.drawFBO(cX-sS/2,cY-sS/2,sS,sS,topCam.getFBO());
		map.detach(GOGL.gl);
	}
	
	public void add() {}
	public void draw() {
		if(GOGL.getCamera() != GOGL.getMainCamera())
			return;

		step();
		
		openFrac += (onscreenFrac.getTo() - openFrac)/20f;
		
		if(isOnscreen()) {
			GOGL.setPerspective();

			GOGL.transformClear();			
			GOGL.transformBeforeCamera(48);
			GOGL.transformRotationY(90);
			GOGL.transformRotationZ(-90);
			transformOnscreen(0,1,0,40);
			GOGL.transformScale(.25f/3);
			GOGL.transformScale(1,-1,1);
			
			float s = 400;
			ScrollDrawer.draw(0,0, s,s, openFrac, map.getTexture());
			
			GOGL.transformClear();
		}
	}

	public static void setActive(boolean active) {
		instance.setMeActive(active);
		instance.topCam.setEnabled(active);
	}
	public static boolean isActive() {
		if(instance == null)
			return false;
		else 
			return instance.amIActive();
	}
}
