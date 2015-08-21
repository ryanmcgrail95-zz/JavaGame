package gfx;

import io.IO;
import io.Keyboard;
import io.Mouse;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.ghost4j.document.DocumentException;
import org.ghost4j.renderer.RendererException;

import brain.Idea;

import com.jhlabs.image.BicubicScaleFilter;
import com.jhlabs.image.CropFilter;
import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.EqualizeFilter;
import com.jhlabs.image.InvertFilter;
import com.jhlabs.image.LaplaceFilter;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.*;

import obj.env.blk.GroundBlock;
import obj.itm.Item;
import obj.itm.ItemController;
import obj.itm.Sword;
import obj.prt.Floaties;
import object.actor.Actor;
import object.actor.NPC;
import object.actor.Player;
import object.environment.Chest;
import object.environment.Fern;
import object.environment.Fire;
import object.environment.Floor;
import object.environment.Grass;
import object.environment.Heightmap;
import object.environment.OakTree;
import object.environment.PalmTree;
import object.environment.PineTree;
import object.environment.Radio;
import object.environment.Sign;
import object.environment.Town;
import object.environment.Water;
import object.environment.WindMill;
import object.primitive.Drawable;
import object.primitive.Updatable;
import phone.SmartPhone;
import resource.model.Model;
import resource.shader.Shader;
import resource.sound.Sound;
import resource.sound.SoundBuffer;
import time.Delta;
import time.Timer;
import window.Window;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import cont.GameController;
import cont.ImageLoader;
import cont.Text;
import cont.TextureController;
import datatypes.mat4;
import datatypes.vec;
import datatypes.vec2;
import datatypes.vec3;
import datatypes.vec4;
import fl.FileExt;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;

public final class GOGL {
	private static final float TOP_LAYER = 999;
	private static GLCanvas canv;
	private static GLU glu = new GLU();
	private static GLEventListener listener;
	private static GLProfile gp = GLProfile.get(GLProfile.GL2);
	public static GL2 gl;
	public static GLCapabilities gcap;
	private static boolean canLight = true;
	private static int shaderProgram;
	private static float[] viewPos;
	
	public static final byte F_NEAREST = 0, F_BILINEAR = 1, F_TRILINEAR = 2;
	
	private static Camera currentCamera, mainCamera;
	
	public static FBO currentFBO = null;	
	private static GLAutoDrawable glad;
	
	
	private static Timer time = new Timer(360,true);
	
    private static float[] perspectiveMatrix = new float[16];    
    public static int[] RESOLUTION = {640,480};


    public static final int VIEW_FAR = 10000;
	public static final int 
		P_TRIANGLE_STRIP = GL2.GL_TRIANGLE_STRIP,
		P_LINE_LOOP = GL2.GL_LINE_LOOP,
		P_LINES = GL2.GL_LINES;
	
	private static byte PR_ORTHO = 0, PR_PERSPECTIVE = 1;
	private static byte projectionMode;
		
	
	public static int 
		BORDER_LEFT = 3, // 3 is PERFECT but doesn't work, 2 works but isn't perfect
		BORDER_TOP = 25, // 25 is PERFECT but doesn't work, 24 works but isn't perfect
		SCREEN_WIDTH = 640, SCREEN_HEIGHT = 480;
		
	private static RGBA drawingColor = new RGBA(1,1,1,1);
	private static Player player;
	private static float orthoLayer = 0;
	
	
	
	private static mat4 modelMatrix = new mat4();
	
	
	public static void println(String str) {
		System.out.println(str);
	}
	
	public static void start3D(GameController gameController) {
		
		GLProfile.initSingleton();
	    //gameController.setLayout(new BorderLayout());
	
	    canv = new GLCanvas();
	    	canv.setBackground(Color.WHITE);
	    	
	    listener =  new GLEventListener() {
            
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {}
            public void init( GLAutoDrawable glautodrawable ) {  	
            	
            	gl = glautodrawable.getGL().getGL2();
            	Sound.iniLoad();
            	
            	
        		mainCamera = new Camera(640,480);
        		currentCamera = mainCamera;
        		
        		
            	println("Initializing textures...");
            	TextureController.ini();                
            	
            	println("Initializing models...");
            	Model.ini();
            	
            	Text.ini();
            	
            	GLText.initialize();
            	//CubeMap.ini();
            	            	
            	// Initialize Delta Time Controller
            	Delta.setTargetFPS(60);
            	Delta.setSpeed(1);

            	println("Initializing items...");
            	ItemController.ini();
            	
            	Idea.ini();

                //GroundBlocks                
                                
                viewPos = new float[4];
                float border = 150 + Math2D.calcLenY(getTime())*20;
                setViewPos(0,border,SCREEN_WIDTH-border,SCREEN_HEIGHT-border);
                
            
                                
                Floaties.ini();
                
                         
                // Generate Environment
                float seaLevel = 50;
                
            	println("Creating Heightmap...");
                Heightmap m = new Heightmap(64, 2, "Resources/Heightmaps/hm1.jpg");
                m.smooth(1);
                m.halveResolution();

                
                /*Planet p = new Planet(2000,2000,240, 32, 16, "Resources/Heightmaps/hm1.jpg");
                p.smooth(1);*/
                
                float aX, aY;
                /*for(int i = 0; i < 20; i++) {
                	aX = MathExt.rnd(-200,200);
                	aY = MathExt.rnd(-200,200);
                	Item.create("Rune Bar",1, 2000+aX,2000+aY,0);
                }*/
                
                player = Player.getInstance();
                	player.setX(2400);
                	player.setY(2400);
                	
                	
                                	
                Actor a = new NPC(player.getX(),player.getY()+500,0);
                //a.taskChase(player);
                a.taskRunStore();
                //partner = new Partner(20,20,0, "mario");
                
                new Water(seaLevel);
                //new Sword("Whatever");

            	vec3 pt;
                /*for(int i = 0; i < 10; i++) {
                	pt = m.generateRandomPointBetween(seaLevel, seaLevel+5);
                	new Grass(pt.x(),pt.y(),30,24,8,4, .5f);
                }*/
                
            	println("Creating foliage...");
                for(int i = 0; i < 400; i++) {
                	pt = m.generateRandomPointAbove(seaLevel);
                	new PineTree(pt.x(),pt.y());
                }
                
                for(int i = 0; i < 50; i++) {
                	pt = m.generateRandomPointBetween(seaLevel, seaLevel+10);
                	new PalmTree(pt.x(),pt.y());
                }
                
                for(int i = 0; i < 400; i++) {
                	pt = m.generateRandomPointAbove(seaLevel);
                	new Fern(pt.x(),pt.y());
                }
            
                                
            	println("Creating town...");
                new WindMill(player.getX(),player.getY());
                new Town(player.getX() + 500, player.getY());
                
                player.give("Rune Bar",1);
                player.give("Empty Bucket",1);
                player.give("Bread",1);
		        player.give("Empty Bowl",1);
		        player.addCoins(1000);
		        
		        
		        new Chest(2600,2400,"Bread");
             
		        
               
            	gl.glEnable(gl.GL_DEPTH_TEST);
                //gl.glEnable(gl.GL_ALPHA_TEST);
            	gl.glEnable(GL2.GL_NORMALIZE);
            	
            	println("Initializing GUI...");
            	Window.ini();
                //new Gameboy();
            	
            	//String[] strs = FileExt.getSubfileNames("ResourcesContainer/Resources/Sounds/Music/");
                            	
            	gcap = new GLCapabilities(gp);
            	gcap.setDepthBits(16);
            	
            	println("Initializing Shaders...");
            	Shader.ini(gl);
            	
            	
                /*for(int i = 0; i < 20; i++) {
                	pt = m.generateRandomPointAbove(seaLevel);
                	new Sign(pt.x(),pt.y(),32,24,2,"buttTown");
                }*/
            	
            	new SmartPhone();
            	//new Radio(2500,2500);
            	
            	checkError();
            }
            
            public void dispose( GLAutoDrawable glautodrawable ) {}
            
            public void display( GLAutoDrawable glautodrawable ) { 
            	
            	glad = glautodrawable;
            	
            	time.check();

            	Updatable.updateAll();
            	
            	gl = glautodrawable.getGL().getGL2();
            	            	
            	setProjection();
        		Drawable.display();            	
            	Camera.renderAll();
            	
            	
            	if(Keyboard.checkPressed('x'))
            		mainCamera.getFBO().saveScreenshot();

            	
            	/*screenBuffer.attach(gl,false);
        		GOGL.clearScreen(RGBA.WHITE);
        		Drawable.draw3D();
        		screenBuffer.detach(gl);*/
            	
            	
            	disableBlending();
            	setViewport(0,0,640,480);
            	clearScreen();
            	setColor(RGBA.WHITE);
            	setOrtho();
            	drawFBO(0,480,640,-480,mainCamera.getFBO());
            	enableBlending();
            	
            	Overlay.draw();
            	
    			Keyboard.update();
        	}
        };
                
        canv.addGLEventListener(listener);
	
	    canv.setSize(GOGL.SCREEN_WIDTH,GOGL.SCREEN_HEIGHT);
	    gameController.add(canv, BorderLayout.CENTER);  
	}

	public static void setViewport(float x, float y, float w, float h) {
		y = SCREEN_HEIGHT-h-y;
		gl.glViewport((int)x, (int)y, (int)w, (int)h);
		//canv.setSize(w,h);
		
		viewPos[2] = w;
		viewPos[3] = h;
	}

	public static void setFogCoord(float x, float y, float z) {
		float[] array = new float[3];
		array[0] = x;
		array[1] = y;
		array[2] = z;
		gl.glFogCoordfv(array, 0);
	}
	
	public static void beginCheckingVisibility() {
		gl.glBeginQuery(GL2.GL_SAMPLES_PASSED, 0);
	}
	public static int endCheckingVisibility() {
		int[] value = new int[1];
		gl.glEndQuery(GL2.GL_SAMPLES_PASSED);
		gl.glGetQueryObjectiv(GL2.GL_SAMPLES_PASSED, 0, value, 0);
		
		return value[0];
	}
	
	public static void enableBlending() {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		//gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
	}
	public static void disableBlending() {
		gl.glDisable(GL.GL_BLEND);
	}

	
	public static void enableFog(float start, float end, RGBA col) {
		gl.glEnable(GL2.GL_FOG);
		gl.glFogi(GL2.GL_FOG_COORD_SRC, GL2.GL_FRAGMENT_DEPTH);
		gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);

		gl.glFogfv(GL2.GL_FOG_COLOR, col.getRGBArray(), 0);
		
		gl.glFogf(GL2.GL_FOG_START, start);
		gl.glFogf(GL2.GL_FOG_END, end);
	}
	public static void disableFog() {
		gl.glDisable(GL2.GL_FOG);
	}
		
	private static void setProjection() {
		
		float camSpeed = 5;		
		Sound.updateListener(currentCamera);
		
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        // Perspective.
        float widthHeightRatio = 640f/480; //getViewWidth()/getViewHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, VIEW_FAR); //1000
        currentCamera.gluLookAt(glu);
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, perspectiveMatrix,0);

        // Change back to model view matrix.
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();
	}
	
	public static void repaint() {canv.display();}
	
	public static GLCanvas getCanvas() {return canv;}

	
	public static Texture createTexture(int width, int height) {
		return createTexture(new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB), true);
	}
	public static Texture createTexture(BufferedImage img, boolean mipmap) {
		if (img == null) 
			return null;
		
		//HORRIBLE BUG HERE
		//return AWTTextureIO.newTexture(AWTTextureIO.newTextureData(gp, img, 24, 24, mipmap));
		Texture tex = AWTTextureIO.newTexture(gp, img, mipmap);
		tex.setMustFlipVertically(false);
		
		return tex;
	}
	
	public static void bind(Texture tex) {
		if(tex != null)
			bind(tex.getTextureObject());
	}
	public static void bind(int tex) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);			
	}
	public static void unbind() {
		gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
		
	public static int[] calcScreenPt(float x, float y, float z) {
	    float[] screenCoords = new float[3];
	    int[] viewport = new int[16];
	    float[] modelView = new float[16];
	    gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelView,0);
	    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport,0);
	    boolean result = glu.gluProject((float) x, (float) y, (float) z, modelView,0, perspectiveMatrix,0, viewport,0, screenCoords,0);
	    if (result) {
	    	int pX = (int) (320 + (screenCoords[0]-320)/320*(320+GOGL.BORDER_LEFT));
	        return new int[] {pX, (480-GOGL.BORDER_TOP) - (int) screenCoords[1]};
	    }
	    return new int[] {-1,-1};
	}
	public static boolean isPtOnscreen(float x, float y, float z) {		
		int[] pt = calcScreenPt(x,y,z);
		return Math2D.checkRectangle(pt[0],pt[1], 0,0,640,480);
	}
	
	public static void setViewPos(float x1, float y1, float x2, float y2) {
		viewPos[0] = x1;
		viewPos[1] = y1;
		viewPos[2] = x2;
		viewPos[3] = y2;
	}
	
	
	public static void setTextureFiltering(Texture tex, byte type) {
		bind(tex);
		switch(type) {
			case F_NEAREST:/* point sampling of nearest neighbor */
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
				break;
			case F_BILINEAR:/* bilinear interpolation */
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
				break;
			case F_TRILINEAR:/* trilinear interpolation on pyramid */
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
				break;
		}
		unbind();
	}
	
	
	public static boolean checkOrtho() {return projectionMode == PR_ORTHO;}
	public static boolean checkPerspective() {return projectionMode == PR_PERSPECTIVE;}

	
	public static void setOrthoLayer(float layer) {orthoLayer = layer;}
	public static float getOrthoLayer() {return orthoLayer;}
	
	public static void setOrtho() {setOrtho(TOP_LAYER);}
	public static void setOrtho(float w, float h) {setOrtho(w,h,TOP_LAYER);}
	public static void setOrtho(float useLayer) {setOrtho(viewPos[2],viewPos[3],useLayer);}
	public static void setOrtho(float w, float h, float useLayer) {
		
		projectionMode = PR_ORTHO;		
		orthoLayer = useLayer;

		if(currentFBO != null) {
			currentFBO.setOrtho(gl);
			return;
		}
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(0,w,h,0, -1000, 1000);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
		disableDepth();
	}
	
	public static void setPerspective() {
				
		orthoLayer = 0;
		projectionMode = PR_PERSPECTIVE;
		
		if(currentFBO != null) {
			currentFBO.setPerspective(gl);
			return;
		}
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
    	setProjection();
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
		enableDepth();
	}
	
	
	/*public static void setOrthoPerspective() {	
		orthoLayer = 0;		
		projectionMode = PR_PERSPECTIVE;
				
		float camSpeed = 5;
		float camX, camY, camZ, toX, toY, toZ;
		camX = Camera.getX();
		camY = Camera.getY();
		camZ = Camera.getZ();
		toX = Camera.getToX();
		toY = Camera.getToY();
		toZ = Camera.getToZ();
				
		//Update Listener Source
		float n, nX, nY, nZ;
		n = Math3D.calcPtDis(camX,camY,camZ, toX,toY,toZ);
		nX = (toX-camX)/n;
		nY = (toY-camY)/n;
		nZ = (toZ-camZ)/n;
		
		Sound.updateListener(camX,camY,camZ, 0,0,0, nX,nY,nZ, 0,0,1);
		
		
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        
        // Perspective.
        float widthHeightRatio = viewPos[2]/viewPos[3];
        int x1,y1,x2,y2;
        y2 = (int) (SCREEN_HEIGHT/2f);
        y1 = -y2;
        x2 = (int) (SCREEN_WIDTH/2f);
        x1 = -x2;
        gl.glOrtho(x1,x2,y1,y2, -1000,VIEW_FAR);
        glu.gluLookAt(camX, camY, camZ, toX, toY, toZ, 0, 0, 1);                        
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, perspectiveMatrix,0);

        // Change back to model view matrix.
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
		gl.glEnable(GL2.GL_DEPTH);
	}*/
	
	
	
	// DRAWING FUNCTIONS

	public static void resetColor() {
		setColor(RGBA.WHITE);
	}
	
	public static RGBA getColor() {return drawingColor;}

	public static void setColori(int r, int g, int b) {setColor(r/255f, g/255f, b/255f, 1);}
	public static void setColor(float r, float g, float b) {setColor(r,g,b,1);}
	public static void setColor(RGBA col) {setColor(col.getR(),col.getG(),col.getB(),col.getA());}
	public static void setColor(float r, float g, float b, float a) {
		drawingColor.set(r,g,b,a);
		
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glColor4f(drawingColor.getR(),drawingColor.getG(),drawingColor.getB(),drawingColor.getA());
		passShaderVec4("uColor",drawingColor.getArray());
	}
	
	public static void setLightColori(int r, int g, int b) {setLightColor(r/255f, g/255f, b/255f);}
	public static void setLightColor(float r, float g, float b) {
		
		if(!canLight)
			return;
		
		drawingColor.set(r,g,b,1);
		
		float[] black = {0, 0, 0, 1}, 
				white = {1, 1, 1, 1}, 
				color = drawingColor.getArray(), 
				gray = {.5f,.5f,.5f,1};
		
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glColor3f(drawingColor.getR(),drawingColor.getG(),drawingColor.getB());
		
//		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, white, 0);
//		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 128f);
		//gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		//gl.glColor3f(R,G,B);
		
		//gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, white,0);
		
		//gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 128f);
		
		passShaderVec4("uColor",drawingColor.getArray());
	}
	
	
	public static void drawFBO(float x, float y, FBO fbo) {drawFBO(x, y, fbo.getWidth(), fbo.getHeight(), fbo);}
	public static void drawFBO(float x, float y, float w, float h, FBO fbo) {
		fbo.bind(gl);
			GOGL.fillRectangle(x, y, w, h);
		fbo.unbind(gl);
	}
	
	public static void drawFBODepth(float x, float y, FBO fbo) {drawFBODepth(x, y, fbo.getWidth(), fbo.getHeight(), fbo);}
	public static void drawFBODepth(float x, float y, float w, float h, FBO fbo) {
		fbo.bindDepth(gl);
			GOGL.fillRectangle(x, y, w, h);
		fbo.unbind(gl);
	}
	
	public static void drawTexture(float x, float y, TextureExt texExt) {drawTextureScaled(x,y,1,1,texExt);}
	public static void drawTextureScaled(float x, float y, float xS, float yS, TextureExt texExt) {drawTextureScaled(x,y,xS,yS,texExt.getFrame(0));}
	public static void drawTexture(float x, float y, Texture tex) {drawTexture(x,y,tex.getWidth(),tex.getHeight(),tex);}
	public static void drawTextureScaled(float x, float y, float xS, float yS, Texture tex) {drawTexture(x,y,tex.getWidth()*xS,tex.getHeight()*yS,tex);}
	public static void drawTexture(float x, float y, float w, float h, Texture tex) {

		if(tex != null) {
			enableTextures();
			enableBlending();
			bind(tex);
		}
		
		if(tex.getMustFlipVertically())
			fillRectangle(x,y+h,w,-h);
		else
			fillRectangle(x,y,w,h);
      	
        if(tex != null) {
	      	tex.disable(gl);
	      	disableTextures();
        }
	}

	
	public static void drawTexture(float x, float y, float w, float h, MultiTexture tex, int frame) {
		drawTexture(x,y,w,h, tex.getTexture(), tex.getBounds(frame));
	}
	public static void drawTexture(float x, float y, float w, float h, Texture tex, float[] bounds) {
		enableTextures();
		enableBlending();
		bind(tex);
		
		fillRectangle(x,y,w,h,bounds);
      	
      	tex.disable(gl);
      	disableTextures();
	}
	
	public static void drawPixel(float x, float y) {
		gl.glBegin(GL.GL_POINTS);
			gl.glVertex3f(x,y, orthoLayer);
	    gl.glEnd();
	}
	
	public static void setLineWidth(float w) {
		gl.glLineWidth(w);
	}
	
	public static void drawLine(float x1, float y1, float x2, float y2) {drawLine(x1,y1,x2,y2,1);};
	public static void drawLine(float x1, float y1, float x2, float y2, float w) {
		setLineWidth(w);
		gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(x1,y1, orthoLayer);
			gl.glVertex3f(x2,y2, orthoLayer);
	    gl.glEnd();
	    setLineWidth(1);
	}
	
	public static void drawLine(List<vec2> pts) {
		gl.glBegin(GL.GL_LINES);
			int si = pts.size();
			for(int i = 0; i < si; i++) {
				vec2 pt = pts.get(i);
				
				gl.glVertex3f(pt.x(),pt.y(), orthoLayer);
				if(i > 0 && i < si-1)
					gl.glVertex3f(pt.x(),pt.y(), orthoLayer);
			}
	    gl.glEnd();
	}
	

	public static void drawRectangle(float x, float y, float w, float h) {rectangle(x,y,w,h,false);}
	public static void drawRectangle(float x, float y, float w, float h, float[] bounds) {rectangle(x,y,w,h,bounds,false);}
	public static void fillRectangle(float x, float y, float w, float h) {rectangle(x,y,w,h,true);}
	public static void fillRectangle(float x, float y, float w, float h, float[] bounds) {rectangle(x,y,w,h,bounds,true);}
	public static void rectangle(float x, float y, float w, float h, boolean fill) {rectangle(x,y,w,h,new float[] {0,0,1,1},fill);}
	public static void rectangle(float x, float y, float w, float h, float[] bounds, boolean fill) {
		gl.glBegin((fill ? GL2.GL_QUADS : GL.GL_LINE_LOOP));
			gl.glTexCoord2d(bounds[0],bounds[1]);	gl.glVertex3f(x, y, orthoLayer);
			gl.glTexCoord2d(bounds[2], bounds[1]);	gl.glVertex3f(x+w, y, orthoLayer);
			gl.glTexCoord2d(bounds[2], bounds[3]);	gl.glVertex3f(x+w, y+h, orthoLayer);
			gl.glTexCoord2d(bounds[0], bounds[3]);	gl.glVertex3f(x,y+h, orthoLayer);		
        gl.glEnd();
	}

	public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {triangle(x1,y1,x2,y2,x3,y3,false);}
	public static void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {triangle(x1,y1,x2,y2,x3,y3,true);}
	public static void triangle(float x1, float y1, float x2, float y2, float x3, float y3, boolean fill) {
		gl.glBegin((fill ? GL.GL_TRIANGLES : GL.GL_LINE_LOOP));
			gl.glTexCoord2d(0.0, 0.0);	gl.glVertex3f(x1, y1, orthoLayer);
			gl.glTexCoord2d(1.0, 0.0);	gl.glVertex3f(x2, y2, orthoLayer);
			gl.glTexCoord2d(1.0, 1.0);	gl.glVertex3f(x3, y3, orthoLayer);
        gl.glEnd();      	      	
	}

	public static void drawPolygon(vec vec, float r, int numPts) {drawPolygon(vec.get(0), vec.get(1), r, numPts);}
	public static void drawPolygon(float x, float y, float r, int numPts) {polygon(x,y, r, numPts, false);}
	public static void drawPolygon(float x, float y, float r, int numPts, float rotation) {polygon(x,y, r, numPts, rotation, false);}
	public static void fillPolygon(vec vec, float r, int numPts) {fillPolygon(vec.get(0), vec.get(1), r, numPts);}
	public static void fillPolygon(float x, float y, float r, int numPts) {polygon(x,y, r, numPts, true);}
	public static void fillPolygon(float x, float y, float r, int numPts, float rotation) {polygon(x,y, r, numPts, rotation, true);}
	public static void polygon(float x, float y, float r, int numPts, boolean fill) {polygon(x,y,r,numPts,0,fill);}
	public static void polygon(float x, float y, float r, int numPts, float rotation, boolean fill) {
		if(fill) {
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glTexCoord2d(.5, .5);
				gl.glVertex3f(x, y, orthoLayer);
		}
		else
			gl.glBegin(GL2.GL_LINE_LOOP);
				
			float dir, xN, yN;			
			for(int i = 0; i <= numPts; i++) {
				dir = rotation + 360.f*i/numPts;
				xN = Math2D.calcLenX(dir);
				yN = Math2D.calcLenY(dir);
				
				gl.glTexCoord2d(.5+.5*xN, .5+.5*yN);
					gl.glVertex3f(x+xN*r, y+yN*r, orthoLayer);
			}
        gl.glEnd();
	}
	
	public static void fillVGradientRectangle(float x, float y, float w, float h, RGBA col1, RGBA col2, int nSteps) {
		float frac1,frac2 = 0, topY,bottomY = y;
		RGBA topColor, bottomColor = col1;
		
		for(int i = 0; i < nSteps; i++) {
			frac1 = frac2;
			frac2 = (i+1)/nSteps;
			
			topY = bottomY;
			bottomY = y + h*frac2;
			
			topColor = bottomColor;
			bottomColor = RGBA.interpolate(col1, col2, frac2);
			
			gl.glBegin(gl.GL_QUADS);
				GOGL.setColor(topColor);
				gl.glVertex3f(x, topY, orthoLayer);
				gl.glVertex3f(x+w, topY, orthoLayer);
				
				GOGL.setColor(bottomColor);
				gl.glVertex3f(x+w, bottomY, orthoLayer);
				gl.glVertex3f(x,bottomY, orthoLayer);
			gl.glEnd();   
		}
	}

	
	// 3D Functions
	
		// Transformation Functions
	public static void setModelMatrix(mat4 mat) {
		modelMatrix = mat.copy();			
		gl.glLoadMatrixf(mat.transpose().array(),0);
	}
	public static mat4 getModelMatrix() 		{return modelMatrix.copy();}

	public static void transformClear() {
		gl.glLoadIdentity();
		modelMatrix.setIdentity();
	}
	
	public static void transformTranslation(vec3 pos) {transformTranslation(pos.x(),pos.y(),pos.z());}
	public static void transformTranslation(float x, float y, float z) {
		gl.glTranslatef(x,y,z);
		modelMatrix.multe(mat4.translation(x,y,z));
	}
	public static void transformScale(float s) {transformScale(s,s,s);}
	public static void transformScale(float xS, float yS, float zS) {
		gl.glScalef(xS, yS, zS);
		modelMatrix.multe(mat4.scale(xS,yS,zS));
	}
	
	public static void transformRotation(float dir, float dirZ) {
		GOGL.transformRotationZ(dir);
		GOGL.transformRotationY(-dirZ);
	}
	public static void transformRotationX(float ang) {
		gl.glRotatef(ang, 1,0,0);
		modelMatrix.multe(mat4.rotationX(ang));
	}
	public static void transformRotationY(float ang) {
		gl.glRotatef(ang, 0,1,0);
		modelMatrix.multe(mat4.rotationY(ang));
	}
	public static void transformRotationZ(float ang) {
		gl.glRotatef(ang, 0,0,1);
		modelMatrix.multe(mat4.rotationZ(ang));
	}
	public static void transformRotation(vec3 rot) {
		transformRotationZ(rot.z());
		transformRotationY(rot.y());
		transformRotationX(rot.x());
	}
	public static void transformRotationNormal(vec3 normal) {
		float dir, dirZ;
		
		dir = Math2D.calcPtDir(0,0, normal.x(),normal.y());
		dirZ = Math2D.calcPtDir(0,0, Math2D.calcPtDis(0,0, normal.x(),normal.y()),normal.z());
		
		transformRotation(dir, dirZ);
	}
	
	public static void transformBeforeCamera(float len) {
		GOGL.transformTranslation(currentCamera.getPosition());
		GOGL.transformRotationNormal(currentCamera.getNormal());
		GOGL.transformTranslation(len,0,0);
	}
	
	public static void transformSprite() {
		GOGL.transformRotationNormal(currentCamera.getNormal());
		GOGL.transformRotationY(90);
		GOGL.transformRotationZ(-90);
	}
		
			
		// Wall-Drawing Function
			
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2) {
				draw3DWall(x1,y1,z1,x2,y2,z2,null);
			}
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
				
				if(tex != null) {
					gl.glEnable(GL2.GL_TEXTURE_2D);
					tex.bind(gl);
					tex.enable(gl);
				}

				float dir, nX, nY;
				dir = Math2D.calcPtDir(x1,y1,x2,y2)-90;
					nX = Math2D.calcLenX(1,dir);
					nY = Math2D.calcLenY(1,dir);

				
				gl.glBegin(GL2.GL_QUADS);
				gl.glNormal3f(nX,nY,0);
				gl.glTexCoord2f(0, 0); 	
					gl.glVertex3d(x1, y1, z1);
				gl.glNormal3f(nX,nY,0);
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(x2, y2, z1);
				gl.glNormal3f(nX,nY,0);
				gl.glTexCoord2f(1, 1);
					gl.glVertex3d(x2, y2, z2);
				gl.glNormal3f(nX,nY,0);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(x1, y1, z2);
				gl.glEnd();
				
				unbind();
			}
			
			public static void draw3DFloor(float x1, float y1, float x2, float y2, float z) {draw3DFloor(x1,y1,x2,y2,z,null);}
			public static void draw3DFloor(float x1, float y1, float x2, float y2, float z, Texture tex) {
				
				bind(tex);
				
				gl.glBegin(gl.GL_QUADS);
				gl.glTexCoord2f(0, 0); 	
					gl.glVertex3d(x1, y1, z);
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(x2, y1, z);
				gl.glTexCoord2f(1, 1);
					gl.glVertex3d(x2, y2, z);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(x1, y2, z);
				gl.glEnd();
				
				unbind();
			}
			
		// Tetrahedron-Drawing Function
			public static void draw3DTetrahedron(float r, float h) {draw3DFrustem(r,h,null);}
			public static void draw3DTetrahedron(float r, float h, Texture tex) {draw3DTetrahedron(0,0,0,r,h,tex);}
			public static void draw3DTetrahedron(float x, float y, float z, float r, float h) {draw3DTetrahedron(x,y,z,r,h,null);}
			public static void draw3DTetrahedron(float x, float y, float z, float r, float h, Texture tex) {draw3DFrustem(x,y,z,r,0,h,tex,4);}
	
		// Frustem-Drawing Functions
			public static void draw3DFrustem(float r, float h) {draw3DFrustem(r,h,null);}
			public static void draw3DFrustem(float r, float h, int numPts) {draw3DFrustem(r,r,h,numPts);}
			public static void draw3DFrustem(float r, float h, Texture tex) {draw3DFrustem(r,r,h,tex);}
			public static void draw3DFrustem(float radBot, float radTop, float h) {draw3DFrustem(radBot,radTop, h, null);}
			public static void draw3DFrustem(float radBot, float radTop, float h, Texture tex) {draw3DFrustem(0,0,0,radBot,radTop, h, tex);}
			public static void draw3DFrustem(float radBot, float radTop, float h, int numPts) {draw3DFrustem(0,0,0,radBot,radTop, h, null, numPts);}
			public static void draw3DFrustem(float radBot, float radTop, float h, int numPts, boolean ends) {
				draw3DFrustem(0,0,0,radBot,radTop, h, null, numPts,ends);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h, Texture tex, int numPts) {
				draw3DFrustem(0,0,0,radBot,radTop, h, tex, numPts);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h) {
				draw3DFrustem(x,y,z,radBot,radTop, h, null);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex) {
				draw3DFrustem(x,y,z,radBot,radTop, h, tex, 10);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, int numPts) {
				draw3DFrustem(x,y,z,radBot,radTop, h, null, numPts);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, int numPts, boolean ends) {
				draw3DFrustem(x,y,z,radBot,radTop, h, null, numPts, ends);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex, int numPts) {
				draw3DFrustem(x,y,z,radBot,radTop,h,tex,numPts,true);
			}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex, int numPts, boolean ends) {
				
				if(numPts < 3)
					return;
				
				bind(tex);
				
				float dir, zDir, xN, yN, zN;
				
				// Draw Top	
				if(ends && radTop > 0) {
					gl.glBegin(gl.GL_TRIANGLE_FAN);
					gl.glNormal3f(0,0,1);
					gl.glTexCoord2d(.5,.5);
						gl.glVertex3f(x,y,z+h);
					for(int i = 0; i <= numPts; i++) {
						dir = 360.f*i/(numPts);
												
						xN = Math2D.calcLenX(1,dir);
						yN = Math2D.calcLenY(1,dir);	
						
						gl.glNormal3f(0,0,1);
						gl.glTexCoord2d(.5+.5*xN,.5+.5*yN);
							gl.glVertex3f(x+xN*radTop,y+yN*radTop,z+h);
					}
			        gl.glEnd();
				}
				
				if(ends && radBot > 0) {
					gl.glBegin(gl.GL_TRIANGLE_FAN);	
					gl.glNormal3f(0,0,-1);
					gl.glTexCoord2d(.5,.5);
						gl.glVertex3f(x,y,z);
					for(int i = 0; i <= numPts; i++) {
						dir = 360.f*i/(numPts);
												
						xN = Math2D.calcLenX(1,dir);
						yN = Math2D.calcLenY(1,dir);	
						
						gl.glNormal3f(0,0,-1);
						gl.glTexCoord2d(.5+.5*xN,.5+.5*yN);
							gl.glVertex3f(x+xN*radBot,y+yN*radBot,z);
					}
			        gl.glEnd();
				}
				
				if(h > 0) {
					float dir1, dir2, dirZ, dirZ1, dirZ2, nX,nY,nZ1,nZ2, i1, i2;
					gl.glBegin(gl.GL_QUADS);			
					for(int i = 0; i < numPts; i++) {
						i1 = (i+1);
						i2 = (i);
						
						dir = 360.f*(i+.5f)/(numPts);
						dirZ = Math2D.calcPtDir(radBot,radTop,0,h)-90;
						
							nX = Math3D.calcPolarX(dir,dirZ);
							nY = Math3D.calcPolarY(dir,dirZ);
							nZ1 = Math3D.calcPolarZ(dir,dirZ);
							nZ2 = Math3D.calcPolarZ(dir,dirZ);
							
						dir1 = 360.f*i1/(numPts);
							xN = Math2D.calcLenX(dir1);
							yN = Math2D.calcLenY(dir1);

						dir2 = 360.f*i2/(numPts);
							
				
						gl.glNormal3f(nX,nY,nZ2);
						gl.glTexCoord2d(i1/(numPts),0);
							gl.glVertex3f(x+xN*radTop,y+yN*radTop,z+h);
						gl.glNormal3f(nX,nY,nZ1);
						gl.glTexCoord2d(i1/(numPts),1);
							gl.glVertex3f(x+xN*radBot,y+yN*radBot,z);
							
							
						xN = Math2D.calcLenX(1,dir2);
						yN = Math2D.calcLenY(1,dir2);
						
						gl.glNormal3f(nX,nY,nZ1);
						gl.glTexCoord2d(i2/(numPts),1);
							gl.glVertex3f(x+xN*radBot,y+yN*radBot,z);
						gl.glNormal3f(nX,nY,nZ2);
						gl.glTexCoord2d(i2/(numPts),0);
							gl.glVertex3f(x+xN*radTop,y+yN*radTop,z+h);
					}
			        gl.glEnd();
				}
				
				unbind();
			}
			
	
		// Sphere-Drawing Functions
			public static void draw3DSphere(float r) {draw3DSphere(0,0,0,r,null);}
			public static void draw3DSphere(float r, int numPts) {draw3DSphere(0,0,0,r,null,numPts);}
			public static void draw3DSphere(float r, Texture tex) {draw3DSphere(0,0,0,r,tex);}
			public static void draw3DSphere(float r, Texture tex, int numPts) {draw3DSphere(0,0,0,r,tex,numPts);}
			public static void draw3DSphere(float x, float y, float z, float r) {draw3DSphere(x,y,z,r,null);}
			public static void draw3DSphere(float x, float y, float z, float r, int numPts) {draw3DSphere(x,y,z,r,null,numPts);}
			public static void draw3DSphere(float x, float y, float z, float r, Texture tex) {draw3DSphere(x,y,z,r,tex,10);}
			public static void draw3DSphere(float x, float y, float z, float r, Texture tex, int numPts) {		
			
				if(numPts < 3)
					return;
				
				bind(tex);
				
				float dir, zDir, xN, yN, zN;
				
				for(int d = 0; d < numPts-1; d++) {
					
					gl.glBegin(GL2.GL_TRIANGLE_STRIP);
					
					for(int i = 0; i < numPts; i++) {
						dir = 360.f*i/(numPts-1);
						
						for(int ii = 0; ii < 2; ii++) {
							zDir = 360.f*(d+ii)/(numPts-1);
							
							xN = Math3D.calcPolarX(dir,zDir);
							yN = Math3D.calcPolarY(dir,zDir);
							zN = Math3D.calcPolarZ(dir,zDir);
							
							gl.glTexCoord2d(i/(numPts-1), (d+ii)/(numPts-1));
								gl.glVertex3f(x+xN*r,y+yN*r,z+zN*r);
						}
					}
					
			        gl.glEnd();
				}
				
				unbind();
			}


	// this function is called when you want to activate the shader.
    // Once activated, it will be applied to anything that you draw from here on
    // until you call the dontUseShader(GL) function.
    public static void enableShader(String shaderName) {
    	
    	shaderProgram = Shader.getShader(shaderName).getProgram();
    	
	    float[] resI = {
	    	SCREEN_WIDTH, SCREEN_HEIGHT
	    };
	    
	    gl.glUseProgram(shaderProgram);
		gl.glUniform2fv(gl.glGetUniformLocation(shaderProgram, "iResolution"), 1, resI, 0);			
    	gl.glUniform1f(gl.glGetUniformLocation(shaderProgram, "iGlobalTime"), getTime()/50f);        
    	passShaderVec4("uColor",drawingColor.getArray());
    }
    
    public static float getTime() {return time.get();}
    
    public static void passShaderFloat(String name, float val) {

    	if(shaderProgram != 0)
    		gl.glUniform1f(gl.glGetUniformLocation(shaderProgram, name), val);
    }
    
    public static void passShaderMat4(String name, mat4 mat, boolean doTranspose) {passShaderMat4(name,mat.array(),doTranspose);}
    public static void passShaderMat4(String name, float[] mat, boolean doTranspose) {
    	if(shaderProgram != 0)
    		gl.glUniformMatrix4fv(gl.glGetUniformLocation(shaderProgram, name), 1, doTranspose, mat, 0);
    }
    
    public static void passShaderVec4(String name, vec4 vec) {passShaderVec4(name, vec.getArray());}
    public static void passShaderVec4(String name, float a, float b, float c, float d) {passShaderVec4(name, new float[] {a,b,c,d});}
    public static void passShaderVec4(String name, float[] vec) {
    	if(shaderProgram != 0)
    		gl.glUniform4fv(gl.glGetUniformLocation(shaderProgram, name), 1, vec, 0);
    }
    
    /*public static boolean passProjectionMatrix() {
    	return passShaderMat4("projectionMatrix", GOGL.getPerspectiveMatrix(), true);
    }
    public static boolean passViewMatrix() {
    	return passShaderMat4("viewMatrix", GOGL.getViewMatrix(), true);
    }
    public static boolean passModelMatrix() {
    	return passShaderMat4("modelMatrix", GOGL.getModelMatrix(), true);
    }*/


    public static void enableShaderDiffuse(float radius) {

    	enableShader("Diffuse");
    	gl.glUniform1f(gl.glGetUniformLocation(shaderProgram, "iRadius"), radius);
    }	
	    

    public static void disableShaders() {
        gl.glUseProgram(shaderProgram = 0);
    }
    
    public static void enableCulling() 		{gl.glEnable(GL.GL_CULL_FACE);}
    public static void disableCulling() 	{gl.glDisable(GL.GL_CULL_FACE);}

    public static void enableTextures() 	{gl.glEnable(GL.GL_TEXTURE_2D);}
    public static void disableTextures() 	{gl.glDisable(GL.GL_TEXTURE_2D);}
    
    public static void drawGaussian() {
    	/*
    	 * enableShader(gl, ShaderController.getShader("Gaussian"));
	    	
	    	int shaderprogram = ShaderController.getShader("Gaussian").getProgram();
	
		    float[] resI = {
		    		640, 480
		    };
			
			int res = gl.glGetUniformLocation(shaderprogram, "iResolution");
			int globTime = gl.glGetUniformLocation(shaderprogram, "iGlobalTime");
			gl.glUniform1f(gl.glGetUniformLocation(shaderprogram, "iRadius"), 20);
			gl.glUniform2fv(res, 1, resI, 0);			
    	 */
    }
    

    /*public static mat4 getPerspectiveMatrix() {
    	//WHAT
    	return getPerspectiveMatrix(45, 640f/480, 1,1000);
    }
    
    public static mat4 getPerspectiveMatrix(float fovy, float aspect, float near, float far) {
    	float x1,y1,x2,y2;
    	
    	y2 = near * (float) Math.tan(Math.toRadians(fovy));
    	y1 = -y2;
    	x1 = y1*aspect;
    	x2 = y2*aspect;
    	
    	return getFrustem(x1,x2,y1,y2, near, far);
    }
    public static mat4 getFrustem(float l, float r, float b, float t, float n, float f) {
    	return (new mat4(2*n/(r-l),0,(r+l)/(r-l),0,
    					0,2*n/(t-b),(t+b)/(t-b),0,
    					0,0,(f+n)/(f-n),2*f*n/(f-n),
    					0,0,-1,0));
    }
    
    public static mat4 getOrthoProjMatrix() {
    	return getOrthoProjMatrix(0, getViewWidth(), getViewHeight(),0, -1000, 1000);
    }
    public static mat4 getOrthoProjMatrix(float l, float r, float b, float t, float n, float f) {
    	return (new mat4(2/(r-l),0,0,-(r+l)/(r-l),
    					0,2/(t-b),0,-(t+b)/(t-b),
    					0,0,-2/(f-n),-(f+n)/(f-n),
    					0,0,0,1));
    }*/
    
    /*public static mat4 getViewMatrix() {

 		float eyeX, eyeY, eyeZ, upX, upY, upZ, fX, fY, fZ, toX, toY, toZ;
 		eyeX = Camera.getX();
 		eyeY = Camera.getY();
 		eyeZ = Camera.getZ();
 		toX = Camera.getToX();
 		toY = Camera.getToY();
 		toZ = Camera.getToZ();
    	vec4 camNorm = new vec4(toX-eyeX, toY-eyeY, toZ-eyeZ, 0);
    	    	
 		// Get Camera Position, Looking Normal, and Up Normal
 		
    	if(camNorm.len() > 0)
    		camNorm.norme();
 		fX = camNorm.get(0);
 		fY = camNorm.get(1);
 		fZ = camNorm.get(2);
 		upX = 0;
 		upY = 0;
 		upZ = 1;
 		
 		// Forward Direction
 		float lX, lY, lZ;
 		lX = fX;
 		lY = fY;
 		lZ = fZ;

 		// S = L x U
 		float sX, sY, sZ, sN;
 		sX = lY*upZ - lZ*upY;
 		sY = lZ*upX - lX*upZ;
 		sZ = lX*upY - lY*upX;
 		sN = (float) Math.sqrt(sX*sX + sY*sY + sZ*sZ);
 		sX /= sN;
 		sY /= sN;
 		sZ /= sN;

 		// U' = S x L
 		float uX, uY, uZ;
 		uX = sY*lZ - sZ*lY;
 		uY = sZ*lX - sX*lZ;
 		uZ = sX*lY - sY*lX;

 		// Create View Matrix
 		mat4 viewMat = new mat4(
 				sX,uX,-lX,-eyeX,
 				sY,uY,-lY,-eyeY,
 				sZ,uZ,-lZ,-eyeZ,
 				0,0,0,1);
 		//mat4 viewMat = new mat4(sX,uX,-lX,0,sY,uY,-lY,0,sZ,uZ,-lZ,0,0,0,0,1);
 		// Transpose View Matrix
 		
 		return viewMat;
    }*/

	public static void drawFillBar(float x, float y, float w, float h, float frac) {drawFillBar(x,y,w,h,RGBA.GREEN,frac,RGBA.WHITE,0);}
	public static void drawFillBar(float x, float y, float w, float h, RGBA fillColor, float frac, RGBA possColor, float possFrac) {
			
		// Inside
		setColor(possColor);
		fillRectangle(x, y, w*MathExt.contain(0,possFrac,1), h);

		// Inside
		setColor(fillColor);
		fillRectangle(x, y, w*MathExt.contain(0,frac,1), h);
		
		// Outline
		setColor(0,0,0,1);
		drawRectangle(x,y,w,h);
		
		int nSteps = 3;
		
		RGBA clearWhite, fullWhite, clearBlack, halfBlack, fullBlack;
		clearWhite = new RGBA(1,1,1,0); fullWhite = new RGBA(1,1,1,.8f);
		clearBlack = new RGBA(0,0,0,0); halfBlack = new RGBA(0,0,0,.5f); fullBlack = new RGBA(0,0,0,.7f);
		
		// Shading
			// Dark Top
			fillVGradientRectangle(x,y, w,h/4, halfBlack, clearBlack, nSteps);
			
			// Light Band
			fillVGradientRectangle(x,y+h/7, w,h/5, clearWhite, fullWhite, nSteps);
			fillVGradientRectangle(x,y+h/7+h/5, w,h/5, fullWhite, clearWhite, nSteps);
			
			// Dark Bottom
			fillVGradientRectangle(x,y+h/2, w,h/4, clearBlack, fullBlack, nSteps);
			fillVGradientRectangle(x,y+h/2+h/4, w,h/4, fullBlack, halfBlack, nSteps);
	}


	public static void clearScreen() {clearScreen(RGBA.BLACK);}
	public static void clearScreen(RGBA color) {
		gl.glClearColor(color.getR(),color.getG(),color.getB(),color.getA());
    	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}


	
	public static Color getPixelColor(int x, int y) {return getPixelRGBA(x,y).getColor();}
	public static RGBA getPixelRGBA(int x, int y) {
		
		y = 480-y + 8;
		
		ByteBuffer RGB = ByteBuffer.allocateDirect(3);
		gl.glReadPixels(x, y-10, 1 , 1 , GL2.GL_RGB , GL2.GL_UNSIGNED_BYTE, RGB);
		
		return new RGBA(RGB.get(0)/255f,RGB.get(1)/255f,RGB.get(2)/255f);
	}

		
	public static void getScreenshot(int x, int y, int w, int h, int tex) {
		
		bind(tex);

		gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_RGBA, x,SCREEN_HEIGHT-y-h, w,h, 0);
		//gl.glCopyTexImage2D(tex.getTarget(), 0, GL2.GL_DEPTH_COMPONENT, x1, y2, 640,480, 0);
		//tex.setMustFlipVertically(true);
		
		unbind();		
	}

	
	public static void enableLight(int num, float dir, float dirZ) {enableLight(num, Math3D.calcPolarCoords(dir, dirZ));}
	public static void enableLight(int num, vec3 normal) {enableLight(num, normal.x(),normal.y(),normal.z());}
	public static void enableLight(int num, float nX, float nY, float nZ) {enableLight(num, new float[] {nX,nY,nZ, 0});}
	public static void enableLight(int num, float[] array) {

		int light;
		switch(num) {
			case 0:	light = GL2.GL_LIGHT0;	break;
			case 1:	light = GL2.GL_LIGHT1;	break;
			case 2:	light = GL2.GL_LIGHT2;	break;
			case 3: light = GL2.GL_LIGHT3;	break;
			case 4: light = GL2.GL_LIGHT4;	break;
			case 5: light = GL2.GL_LIGHT5;	break;
			case 6: light = GL2.GL_LIGHT6;	break;
			case 7: light = GL2.GL_LIGHT7;	break;
			default: light = GL2.GL_LIGHT0;	break;
		}
				
		gl.glLightfv(light, GL2.GL_POSITION, array, 0);
		
		float[] black = {
			0, 0, 0, 1
		}, white = {
			1, 1, 1, 1
		}, color = drawingColor.getArray(), 
		gray = {
			.1f,.1f,.1f,1
		};
				
		gl.glLightfv(light, GL2.GL_AMBIENT, RGBA.BLACK.getArray(), 0);
		gl.glLightfv(light, GL2.GL_DIFFUSE, RGBA.WHITE.getArray(), 0);  //white
		gl.glLightfv(light, GL2.GL_SPECULAR, RGBA.WHITE.getArray(), 0); //white
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, gray, 0);
		gl.glEnable(light);
	}
	
	public static void enableLighting() {
		
		if(!canLight)
			return;
		
		enableLight(0,0,1,0);
		enableLight(1,1,0,0);
		enableLight(2,0,1,1);

		gl.glEnable(GL2.GL_LIGHTING);
	}
	public static void disableLighting() {
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
	}

	public static void draw3DBlock(float x1, float y1, float z1, float x2, float y2, float z2) {draw3DBlock(x1,y1,z1,x2,y2,z2,null);}
	public static void draw3DBlock(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
		draw3DWall(x1,y1,z1,x1,y2,z2,tex);
		draw3DWall(x2,y1,z1,x2,y2,z2,tex);
		draw3DWall(x1,y1,z1,x2,y1,z2,tex);
		draw3DWall(x2,y2,z1,x1,y2,z2,tex);
		draw3DFloor(x1,y1,x2,y2,z1,tex);
		draw3DFloor(x1,y1,x2,y2,z2,tex);
	}
	
	
	public static void begin(int type) {
		gl.glBegin(type);
	}
	public static void end() {
		gl.glEnd();
	}
	public static void vertex(float x, float y, float z, float tX, float tY, float nX, float nY, float nZ) {
		gl.glNormal3f(nX,nY,nZ);
		gl.glTexCoord2f(tX,tY);
			gl.glVertex3f(x,y,z);
	}
	public static void vertex(float x, float y, float z, float nX, float nY, float nZ) {
		gl.glNormal3f(nX,nY,nZ);
			gl.glVertex3f(x,y,z);
	}
	public static void vertex(float x, float y, float z) {
		gl.glVertex3f(x,y,z);
	}

	public static void allowLighting(boolean allow) {
		
		setLightColor(1,1,1);
		
		canLight = allow;
		
		if(!allow)
			disableLighting();
	}

	
	public static void drawStringS(float x, float y, String text) {drawStringS(x,y,text,RGBA.WHITE);}
	public static void drawStringS(float x, float y, String text, RGBA rgba) {
		// Draw String w/ Shadows
		GOGL.setColor(0,0,0,rgba.getA());
		GLText.drawString(x+1.5f,y+1.5f,text);
		GOGL.setColor(rgba);
		GLText.drawString(x,y,text);
		GOGL.setColor(RGBA.WHITE);
	}
	

	public static void checkError() {
		int error = gl.glGetError();
		if(error != 0) {
			System.err.println("ERROR # " + error);
			System.exit(error);
		}
	}
	
	public static void forceColor(RGBA color) 	{enableFog(-1000,-1000,color);}
	public static void unforceColor() 			{disableFog();}
	
	public static GL2 getGL() {
		return gl;
	}
	
	public static void blurFBO(FBO fbo) {
		float alpha = .25f;
		float startR = 1,
				endR = 32, 
				numR = 5;
		float numD = 8;
		
		
    	GOGL.enableBlending();
		fbo.attach(gl);
			setAlpha(alpha);
			float x,y;
			for(float r = startR; r < endR; r += (endR-startR)/numR)
				for(int d = 0; d < 360; d += 360/numD) {
					x = Math2D.calcLenX(r, d);
					y = Math2D.calcLenY(r, d);
					drawFBO(x,y, fbo);
				}
			setAlpha(1);
		fbo.detach(gl);
    	GOGL.disableBlending();
	}

	public static void setAlpha(float alpha) {
		drawingColor.setA(alpha);
		setColor(drawingColor);
	}

	public static void enableDepth() {
        gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
	}
	public static void disableDepth() {
        gl.glDisable(GL.GL_DEPTH_TEST);
	}

	public static void setViewportResolution() {
		setViewport(0,0,RESOLUTION[0],RESOLUTION[1]);
	}

	public static Texture loadTexture(String fileName) {
		try {
			return createTexture(ImageLoader.loadGrayscaleAlpha(fileName),false);
		} catch (IOException e) {
			return null;
		}
	}
	

	public static void drawWaveform(float dX, float dY, float w, float h, int startOffset, int endOffset, int jumpNum, int skipNum, SoundBuffer snd) {drawWaveform(dX,dY,w,h,startOffset,endOffset,jumpNum,skipNum,snd,false);}
	public static void drawWaveform(float dX, float dY, float w, float h, int startOffset, int endOffset, int jumpNum, int skipNum, SoundBuffer snd, boolean shrinkSides) {
		
		List<vec2> pts = new ArrayList<vec2>();
		float xSize;
		double avg;
		
		
		int len, packetNum = snd.getPacketNum(), packetSize = snd.getPacketSize(), offsetMax = packetNum*packetSize;
		int jumpSize = jumpNum*packetSize;
		
		
		startOffset = (int) MathExt.snap(startOffset, packetSize);

		startOffset = (int) MathExt.contain(0, startOffset, offsetMax);
		endOffset = (int) MathExt.contain(0, endOffset, offsetMax);
		
		len = endOffset-startOffset;
		
		
		xSize = w / ((endOffset - startOffset)/jumpSize);
		
		if(startOffset > endOffset)
			return;
		
		int n = 0;
		
		if(shrinkSides) {
			for(int i = startOffset; i+jumpSize < endOffset; i += jumpSize) {
				avg = 0;
				for(int k = 0; k < jumpSize; k += (1+skipNum)*packetSize)
					avg += snd.getAmplitudeFraction(i);
				avg /= jumpNum/(1+skipNum);
				
				pts.add(new vec2(dX + n*xSize, (float) (dY + h/2*avg*(1 - 2*Math.abs( 1f*(i-startOffset)/len - .5f)) )));
				
				n++;
			}
		}
		else
			for(int i = startOffset; i+jumpSize < endOffset; i += jumpSize) {
				avg = 0;
				for(int k = 0; k < jumpSize; k += (1+skipNum)*packetSize)
					avg += snd.getAmplitudeFraction(i);
				avg /= jumpNum/(1+skipNum);
				
				pts.add(new vec2(dX + n*xSize, (float) (dY + h/2*avg)));
				
				n++;
			}

		
		drawLine(pts);
		
		pts.clear();
	}

	public static void perspective() {
		gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu = GLU.createGLU(gl);
        glu.gluPerspective(currentCamera.getFOV(), RESOLUTION[0]/RESOLUTION[1], 1, 10000);
        currentCamera.gluLookAt(glu);
	}

	public static void setCamera(Camera cam) {currentCamera = cam;}
	public static Camera getCamera() {return currentCamera;}
	public static Camera getMainCamera() {return mainCamera;}
}