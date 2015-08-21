package phone;

import object.actor.Player;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import io.Mouse;
import fl.FileExt;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gfx.Camera;
import gfx.FBO;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import resource.sound.Playlist;
import resource.sound.SoundBuffer;
import resource.sound.SoundSource;

public final class CameraApp extends PhoneApp {
	
	private Camera cam;
	
	
	public CameraApp(SmartPhone owner) {
		super(owner);
		
		cam = new Camera(owner.WIDTH, owner.HEIGHT);
	}
	
	
	public void draw() {
		Player p = Player.getInstance();
		float x,y,z,toX,toY,toZ, dis = 10, dir = p.getDirection(), dirZ = -10;
		x = p.x();
		y = p.y();
		z = p.z();
		toX = x+Math3D.calcPolarX(dis,dir,dirZ);
		toY = y+Math3D.calcPolarY(dis,dir,dirZ);
		toZ = z+Math3D.calcPolarZ(dis,dir,dirZ);
		
		cam.setProjection(x,y,z,toX,toY,toZ);
		
		parent.beginRendering();
		GOGL.clearScreen(RGBA.WHITE);
		
		GOGL.drawFBO(0, SmartPhone.HEIGHT,SmartPhone.WIDTH,-SmartPhone.HEIGHT, cam.getFBO());
		
		parent.endRendering();		
	}	
}
