package phone;

import com.jogamp.opengl.util.texture.Texture;

import cont.TextureController;
import datatypes.SmoothFloat;
import datatypes.vec2;
import io.Mouse;
import functions.Math2D;
import functions.MathExt;
import gfx.FBO;
import gfx.GLText;
import gfx.GOGL;
import gfx.RGBA;
import object.primitive.Drawable;
import resource.sound.Playlist;
import resource.sound.Sound;
import resource.sound.SoundBuffer;
import resource.sound.SoundSource;
import window.GUIFrame;
import window.GUIListener;
import window.TextButton;

public class SmartPhone extends Drawable {

	private static SmartPhone instance;
	private float onscreenFrac, onscreenToFrac;
	public static final int WIDTH = 480, HEIGHT = 270;
	private int x = 640/2 - WIDTH/2, y = 480/2 - HEIGHT/2;
	private float scale = 1;
	private FBO fbo;
	private PhoneApp currentApp;
	
	public float volumeFrac = 1;
	
	
	public SmartPhone() {
		super(false,true);
		
		onscreenFrac = onscreenToFrac = 0;
		
		fbo = new FBO(GOGL.gl,WIDTH,HEIGHT);
		
		instance = this;
		
		//currentApp = new CameraApp(this);
		currentApp = new MusicApp(this);
	}
	
	
	public float getX() {return x;}
	public float getY() {return y;}
	public float getScale() {return scale;}
	
	
	public void beginRendering() 	{fbo.attach(GOGL.gl);}
	public void endRendering()		{fbo.detach(GOGL.gl);}
	
	public void render() {
		currentApp.draw();
	}
	public void draw() {
		
		if(GOGL.getCamera() != GOGL.getMainCamera())
			return;
		
		onscreenFrac += (onscreenToFrac - onscreenFrac)/5;
		if(Math.abs(onscreenFrac-onscreenToFrac) < .001)
			onscreenFrac = onscreenToFrac;
		
		if(onscreenFrac != 0) {
			if(onscreenToFrac == 1) {
				Sound.setMusicVolume(0);
				volumeFrac = 1;
			}
			else {
				Sound.setMusicVolume(1);
				volumeFrac = 0;
			}


			GOGL.transformClear();			
			GOGL.transformBeforeCamera(48);		
			GOGL.transformRotationY(90);
			GOGL.transformRotationZ(-90);
			GOGL.transformTranslation(0,35*(1-onscreenFrac),0);
			GOGL.transformScale(.25f/3);
			
			Texture tex = TextureController.getTexture("iphone");
			float s = 2.5f,w,h,aX,aY,a;
			w = s*tex.getWidth();
			h = s*tex.getHeight();
			aX = 0;
			aY = -3;
									
			GOGL.disableBlending();
			GOGL.drawFBO(-WIDTH/2,-HEIGHT/2,scale*WIDTH,scale*HEIGHT,fbo);
			GOGL.enableBlending();

			GOGL.transformTranslation(0,0,1);
			GOGL.transformRotationZ(-90);

			GOGL.resetColor(); 
			GOGL.setTextureFiltering(tex, GOGL.F_NEAREST);
			GOGL.drawTexture(-w/2+aX,-h/2+aY, w,h,tex);
			
			GOGL.transformClear();
		}
		else {
			volumeFrac = 0;
			Sound.setMusicVolume(1);
		}
	}

	public boolean checkOnscreen() {return true;}
	public float calcDepth() {return 0;}
	public void update() {}
	

	public static void setActive(boolean active) {instance.onscreenToFrac = (active ? 1:0);}
	public static boolean isActive() {return instance.onscreenToFrac == 1;}
}
