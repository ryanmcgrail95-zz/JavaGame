package gfx;

import io.Keyboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.*;

import object.environment.Chest;
import object.environment.Fern;
import object.environment.Floor;
import object.environment.Heightmap;
import object.environment.PineTree;
import object.primitive.Drawable;
import object.primitive.Updatable;
import paper.Background;
import paper.PlayerPM;
import phone.SmartPhone;
import resource.model.Model;
import resource.shader.Shader;
import resource.sound.Sound;
import resource.sound.SoundBuffer;
import rm.Room;
import script.PML;
import time.Delta;
import time.Timer;
import twoD.Player2D;
import twoD.Terrain2D;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import cont.GameController;
import cont.ImageLoader;
import cont.Text;
import cont.TextureController;
import datatypes.mat4;
import datatypes.vec;
import datatypes.vec3;
import datatypes.vec4;
import functions.ArrayMath;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;

public class GL {
	private static final float TOP_LAYER = 999;
	private static GLCanvas canv;
	private static GLU glu = new GLU();
	private static GLEventListener listener;
	private static GLProfile gp = GLProfile.get(GLProfile.GL2);
	protected static GL2 gl;
	private static GLCapabilities gcap;
	private static boolean canLight = true;
	private static int shaderProgram;
	private static float[] viewPos;
	
	
	// CURL VARIABLES
	private static Texture prevScreenTex;
	private static float curlTime = -1;
		
	private static Camera currentCamera, mainCamera, marioCamera;
	
	public static FBO currentFBO = null;	
	private static GLAutoDrawable glad;
	
	private static Timer time = new Timer(360);
	
    private static float[] perspectiveMatrix = new float[16];    

    public static final int VIEW_FAR = 10000;
	public static final int 
		P_TRIANGLES = GL2.GL_TRIANGLES,
		P_TRIANGLE_STRIP = GL2.GL_TRIANGLE_STRIP,
		P_LINE_LOOP = GL2.GL_LINE_LOOP,
		P_LINES = GL2.GL_LINES;
	
	private static byte PR_ORTHO = 0, PR_PERSPECTIVE = 1, PR_ORTHOPERSP = 2;
	private static byte projectionMode;
		
	
	public static int 
		BORDER_LEFT = 3, // 3 is PERFECT but doesn't work, 2 works but isn't perfect
		BORDER_TOP = 25, // 25 is PERFECT but doesn't work, 24 works but isn't perfect
		SCREEN_WIDTH = 640, 
		SCREEN_HEIGHT = 436,
		WINDOW_WIDTH = 640,
		WINDOW_HEIGHT = 436;
    private static int[] resolutionArray = {SCREEN_WIDTH,SCREEN_HEIGHT};
		
	private static RGBA drawingColor = RGBA.createf(1,1,1,1);
	private static float orthoLayer = 0;
	
	private static Texture rainbowTex, metalTex;
	
	
	protected static mat4 modelMatrix = new mat4();
	
	
	public static void print(String str) {
		System.out.print(str);
	}
	public static void println(String str) {
		//ErrorPopup.open(str, false);
		System.out.println(str);
	}
	
	
	public static void stM(String name) {
		//print(name + "(" + checkError() + " -- ");
	}	
	public static void eM() {
		//println(checkError() + ")");
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
            	
            	
           		currentCamera = mainCamera = new Camera(Camera.PR_PERSPECTIVE,SCREEN_WIDTH,SCREEN_HEIGHT);
           			mainCamera.enableBG(true);
           		marioCamera = new Camera("marioCam", mainCamera);
           		
       		PML.ini();
           		
        	TextureController.ini();
        	CubeMap.ini();
            	
        	Model.ini();
            	
            Text.ini();
            	
            	G2D.initialize();        	            	

                //GroundBlocks                
                                
                viewPos = new float[4];
                float border = 150 + Math2D.calcLenY(getTime())*20;
                setViewPos(0,border,SCREEN_WIDTH-border,SCREEN_HEIGHT-border);
    
                prevScreenTex = createTexture(640,436);
                //FireSprite.ini();
		        
                rainbowTex = loadTexture("Resources/Images/rainbow.png");
                metalTex = loadTexture("Resources/Images/metal.png");
                
		        //iniModelRenderer(Model.MOD_CASTLE);
		        	iniPMBattle();
		        	//iniPMRoom();
		        //ini3D();
		        //ini2D();
                //iniPage();
                
            	gl.glEnable(gl.GL_DEPTH_TEST);
                gl.glEnable(gl.GL_ALPHA_TEST);
                gl.glAlphaFunc(GL2.GL_GREATER, 0);
            	
                gl.glEnable(GL2.GL_NORMALIZE);
            	
            	gcap = new GLCapabilities(gp);
            	gcap.setDepthBits(16);
        
            println("Initializing Shaders...");
            	Shader.ini(gl);
            	          
            	println("Finished Initialization");
            	//checkError();
            }
            public void dispose( GLAutoDrawable glautodrawable ) {}
            public void display( GLAutoDrawable glautodrawable ) { 
            	
            	            	
            	/*checkError();
            	
            	if(ErrorPopup.isOpen()) {
            		clear();
            		return;
            	}*/
            	
            	
            	if(Keyboard.checkPressed('1'))
            		setResolution(256,174);
            	else if(Keyboard.checkPressed('2'))
            		setResolution(320,218);
            	else if(Keyboard.checkPressed('3'))
            		setResolution(640,436);
            	
            	if(Keyboard.checkPressed('r')) {
            		Room.resetRoom();
            		return;
            	}
            	
            	if(Keyboard.checkPressed('6'))
            		Delta.setTargetFPS(90);
            	if(Keyboard.checkPressed('7'))
            		Delta.setTargetFPS(30);
            	if(Keyboard.checkPressed('8'))
            		Delta.setTargetFPS(60);
            	
            	if(Keyboard.checkPressed('9'))
            		setWindowSize(320,218);
            	else if(Keyboard.checkPressed('0'))
            		setWindowSize(640,436);
            	
    			
            	glad = glautodrawable;
            	
            	time.check();

            	//if(!Room.isLoading())
            		Updatable.updateAll();
            	
            	gl = glautodrawable.getGL().getGL2();
            	            	
            	setProjection();
            	
        		Drawable.display();           
            	Camera.renderAll();
            	
            	if(Keyboard.checkPressed('x'))
            		mainCamera.getFBO().saveScreenshot();

            	/*screenBuffer.attach(gl,false);
        		clearScreen(RGBA.WHITE);
        		Drawable.draw3D();
        		screenBuffer.detach(gl);*/
                 	
            	
            	disableBlending();
            	setViewport(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
            	//clear();
            	setColor(RGBA.WHITE);
            	setOrtho();
            	
            	if(curlTime >= 0) {
	            	enableShader("PageCurl");
	        		passShaderFloat("iGlobalTime", curlTime);
	        		
	        		if(curlTime < 1)
	        			curlTime += Delta.convert(.008f);
	        		else
	        			curlTime = 1;
	        		
	        		bind(prevScreenTex,0);
	        		bind(mainCamera.getFBO().getTexture(),1);
	        		G2D.fillRectangle(0,WINDOW_HEIGHT,WINDOW_WIDTH,-WINDOW_HEIGHT);
            	}
            	else
            		drawFBO(0,WINDOW_HEIGHT,WINDOW_WIDTH,-WINDOW_HEIGHT,mainCamera.getFBO());
            	disableShaders();
            	
            	if(curlTime >= 0)
            		drawFBO(0,WINDOW_HEIGHT,WINDOW_WIDTH,-WINDOW_HEIGHT,marioCamera.getFBO());
            	
            	enableBlending();
            	
            	Overlay.draw();
        	
    			Keyboard.update();
        	}
        };
                
        canv.addGLEventListener(listener);
	
	    canv.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
	    gameController.add(canv, BorderLayout.CENTER);  
	}

	protected static void setWindowSize(int w, int h) {
		canv.setSize(WINDOW_WIDTH = w, WINDOW_HEIGHT = h);
		GameController.getInstance().setSize(w,h+28);
	}

	public static void setResolution(int[] array) 	{setResolution(array[0],array[1]);}
	public static void setResolution(int w, int h) {
		Background.changeResolution(SCREEN_WIDTH,w);
		mainCamera.setResolution(SCREEN_WIDTH = w, SCREEN_HEIGHT = h);
	}

	public static void setViewport(float x, float y, float w, float h) {
		y = WINDOW_HEIGHT-h-y;
		gl.glViewport((int)x, (int)y, (int)w, (int)h);

		viewPos[0] = x;
		viewPos[1] = y;
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
	
	public static void beginPageCurl() {
		curlTime = 0;
		getScreenshot(prevScreenTex);
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
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); 
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE); //GL2.GL_REPLACE
	}
	public static void disableBlending() {
		gl.glDisable(GL2.GL_BLEND);
	}

	
	public static void enableFog(float start, float end, RGBA col) {		
		gl.glEnable(GL2.GL_FOG);
		gl.glFogi(GL2.GL_FOG_COORD_SRC, GL2.GL_FRAGMENT_DEPTH);
		gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);

		gl.glFogfv(GL2.GL_FOG_COLOR, col.RGBf(), 0);
		
		gl.glFogf(GL2.GL_FOG_START, start);
		gl.glFogf(GL2.GL_FOG_END, end);
	}
	public static void disableFog() {
		gl.glDisable(GL2.GL_FOG);
	}
		
	private static void setProjection() {
		
			//stM("setPerspective");
		
		float camSpeed = 5;
		Sound.updateListener(currentCamera);
		
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        // Perspective.
        float widthHeightRatio = 1f*SCREEN_WIDTH/SCREEN_HEIGHT; //getViewWidth()/getViewHeight();
        glu.gluPerspective(getCamera().getFOV(), widthHeightRatio, 1, VIEW_FAR); //1000
        currentCamera.gluLookAt(glu);
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, perspectiveMatrix,0);

        // Change back to model view matrix.
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();
			
        	//eM();
	}
	
	public static void repaint() {
		canv.display();
	}
	
	public static GLCanvas getCanvas() {return canv;}

	
	public static Texture createTexture(int width, int height) {
		return createTexture(new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB), true);
	}
	public static Texture createTexture(BufferedImage img, boolean mipmap) {
		if (img == null) 
			return null;
		
		//HORRIBLE BUG HERE
		//return AWTTextureIO.newTexture(AWTTextureIO.newTextureData(gp, img, 24, 24, mipmap));
		Texture tex = (AWTTextureIO.newTexture(gp, img, mipmap));
		tex.setMustFlipVertically(false);
		
		TextureController.add(tex);
		
		return tex;
	}
	
	public static final void bind(Texture tex) {
		if(tex != null)
			bind(tex.getTextureObject());
	}
	public static final void bind(int tex) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
		disableInterpolation();
	}
	
	public static void unbind() {
		gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	public static void bind(Texture tex, int target) {
		if(tex != null)
			bind(tex.getTextureObject(),target);
	}
	public static void bind(int tex, int target) {
		//gl.glEnable(GL2.GL_TEXTURE0+target);
		//gl.glBindTexture(GL2.GL_TEXTURE0+target, tex);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glActiveTexture(GL2.GL_TEXTURE0+target);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, tex);
	}
	public static void unbind(int target) {
		//gl.glBindTexture(GL2.GL_TEXTURE0+target, 0);
		//gl.glDisable(GL2.GL_TEXTURE0+target);
		gl.glActiveTexture(GL2.GL_TEXTURE0+target);
		gl.glBindTexture(GL2.GL_TEXTURE_2D,0);
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}
		
	public static int[] calcScreenPt(float x, float y, float z) {
	    float[] screenCoords = new float[3];
	    int[] viewport = new int[16];
	    float[] modelView;
	    
	    float SCREEN_WIDTH_HALF = SCREEN_WIDTH/2;
	    
	    modelView = GT.getModelViewMatrix();
	    gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport,0);
	    boolean result = glu.gluProject((float) x, (float) y, (float) z, modelView,0, perspectiveMatrix,0, viewport,0, screenCoords,0);
	    if (result) {
	    	int pX = (int) (SCREEN_WIDTH_HALF + SCREEN_WIDTH_HALF*(screenCoords[0]-SCREEN_WIDTH_HALF)/
	    						(SCREEN_WIDTH_HALF+BORDER_LEFT));
	        return new int[] {pX, (SCREEN_HEIGHT-BORDER_TOP) - (int) screenCoords[1]};
	    }
	    return new int[] {-1,-1};
	}
	public static boolean isPtOnscreen(float x, float y, float z) {		
		int[] pt = calcScreenPt(x,y,z);
		return Math2D.checkRectangle(pt[0],pt[1], 0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
	}
	
	public static void setViewPos(float x, float y, float w, float h) {
		viewPos[0] = x;
		viewPos[1] = y;
		viewPos[2] = w;
		viewPos[3] = h;
	}
	
	
	public static boolean checkOrtho() {return projectionMode == PR_ORTHO;}
	public static boolean checkPerspective() {return projectionMode == PR_PERSPECTIVE;}

	
	public static void setOrthoLayer(float layer) {orthoLayer = layer;}
	public static float getOrthoLayer() {return orthoLayer;}
	
	public static void setOrtho() {setOrtho(TOP_LAYER);}
	public static void setOrtho(float w, float h) {setOrtho(w,h,TOP_LAYER);}
	public static void setOrtho(float useLayer) {setOrtho(viewPos[0],viewPos[1],viewPos[2],viewPos[3],useLayer);}
	public static void setOrtho(float w, float h, float useLayer) {
		setOrtho(viewPos[0],viewPos[1],w,h,useLayer);
	}
	public static void setOrtho(float x, float y, float w, float h, float useLayer) {
				
		projectionMode = PR_ORTHO;		
		orthoLayer = useLayer;

		if(currentFBO != null) {
			currentFBO.setOrtho(gl);
			return;
		}
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glOrtho(x,w,h,y, -1000, 1000);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
		setHidden(false);
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
		setHidden(true);
	}
	
	
	public static void setOrthoPerspective() {	
		orthoLayer = 0;		
		projectionMode = PR_ORTHOPERSP;
				
		Sound.updateListener(currentCamera);
		
		if(currentFBO != null) {
			currentFBO.setOrthoPerspective(gl);
			return;
		}
		
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        
        // Perspective.
        int x1,y1,x2,y2;
        y2 = (int) (SCREEN_HEIGHT/2f);
        y1 = -y2;
        x2 = (int) (SCREEN_WIDTH/2f);
        x1 = -x2;
        gl.glOrtho(x1,x2,y1,y2, -1000,VIEW_FAR);
        currentCamera.gluLookAt(glu);
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, perspectiveMatrix,0);

        // Change back to model view matrix.
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
		gl.glEnable(GL2.GL_DEPTH);
	}
	
	
	
	// DRAWING FUNCTIONS

	public static void resetColor() {setColor(RGBA.WHITE);}
	
	public static RGBA getColor() {return RGBA.create(drawingColor);}

	public static void setColor(RGBA col) {setColorf(col.Rf(),col.Gf(),col.Bf(),col.Af());}
	public static void setColori(int r, int g, int b) {setColorf(r/255f, g/255f, b/255f, 1f);}
	public static void setColori(int r, int g, int b, int a) {setColorf(r/255f, g/255f, b/255f, a/255f);}
	public static void setColorf(float r, float g, float b) {setColorf(r,g,b,1);}
	public static void setColorf(float r, float g, float b, float a) {
			//stM("setColorf");

		drawingColor.setf(r,g,b,a);
		
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glColor4f(r,g,b,a);
		passShaderVec4("uColor",drawingColor.RGBAf());
		
			//eM();
	}
	
	public static void setLightColori(int r, int g, int b) {setLightColor(r/255f, g/255f, b/255f);}
	public static void setLightColor(RGBA col) {
		setLightColor(col.Rf(),col.Gf(),col.Bf());
	}
	public static void setLightColor(float r, float g, float b) {
		
		if(!canLight)
			return;
		
		drawingColor.setf(r,g,b,1);
		
		float[] black = {0, 0, 0, 1}, 
				white = {1, 1, 1, 1}, 
				color = drawingColor.RGBAf(), 
				gray = {.5f,.5f,.5f,1};
		
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glColor3f(drawingColor.Rf(),drawingColor.Gf(),drawingColor.Bf());
		
//		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, white, 0);
//		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 128f);
		//gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		//gl.glColor3f(R,G,B);
		
		//gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, white,0);
		
		//gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 128f);
		
		passShaderVec4("uColor",drawingColor.RGBAf());
	}
	
	
	public static void drawFBO(float x, float y, FBO fbo) {drawFBO(x, y, fbo.getWidth(), fbo.getHeight(), fbo);}
	public static void drawFBO(float x, float y, float w, float h, FBO fbo) {
		fbo.bind(gl);
			enableInterpolation();
			fillRectangle(x, y, w, h);
		fbo.unbind(gl);
	}
		
	public static void drawFBODepth(float x, float y, FBO fbo) {drawFBODepth(x, y, fbo.getWidth(), fbo.getHeight(), fbo);}
	public static void drawFBODepth(float x, float y, float w, float h, FBO fbo) {
		fbo.bindDepth(gl);
			fillRectangle(x, y, w, h);
		fbo.unbind(gl);
	}
		

	public static void drawPixel(float x, float y) {
		gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3f(x,y, orthoLayer);
	    gl.glEnd();
	}
	
	public static void setLineWidth(float w) {gl.glLineWidth(w);}
	
	public static void drawLine(float x1, float y1, float x2, float y2) {drawLine(x1,y1,x2,y2,1);};
	public static void drawLine(float x1, float y1, float x2, float y2, float w) {
		setLineWidth(w);
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(x1,y1, orthoLayer);
			gl.glVertex3f(x2,y2, orthoLayer);
	    gl.glEnd();
	    setLineWidth(1);
	}
	
	public static void drawLine(List<float[]> pts) {
		gl.glBegin(GL2.GL_LINES);
			int i = 0, si = pts.size();
			for(float[] pt : pts) {
				gl.glVertex3f(pt[0],pt[1], orthoLayer);
				if(i > 0 && i < si-1)
					gl.glVertex3f(pt[0],pt[1], orthoLayer);
				i++;
			}
	    gl.glEnd();
	}
	
	
	/*public static void addRectangle(float x, float y, float w, float h) {
		float[] mat, v1, v2, v3, v4;
		mat = getModelMatrix().array();
		v1 = mult(mat, new float[] {x,y,orthoLayer,1});
		v2 = mult(mat, new float[] {x+w,y,orthoLayer,1});
		v3 = mult(mat, new float[] {x,y+h,orthoLayer,1});
		v4 = mult(mat, new float[] {x+w,y+h,orthoLayer,1});

		gl.glTexCoord2f(0, 0);	gl.glVertex3d(v1[0],v1[1],v1[2]);
		gl.glTexCoord2f(1, 0);	gl.glVertex3d(v2[0],v2[1],v2[2]);
		gl.glTexCoord2f(0, 1);	gl.glVertex3d(v3[0],v3[1],v3[2]);
		
		gl.glTexCoord2f(1, 0);	gl.glVertex3d(v2[0],v2[1],v2[2]);
		gl.glTexCoord2f(1, 1);	gl.glVertex3d(v4[0],v4[1],v4[2]);
		gl.glTexCoord2f(0, 1);	gl.glVertex3d(v3[0],v3[1],v3[2]);
	}*/

	public static void drawRectangle(float x, float y, float w, float h) {rectangle(x,y,w,h,false);}
	public static void drawRectangle(float x, float y, float w, float h, float[] bounds) {rectangle(x,y,w,h,bounds,false);}
	public static void fillRectangle(float x, float y, float w, float h) {rectangle(x,y,w,h,true);}
	public static void fillRectangle(float x, float y, float w, float h, float[] bounds) {rectangle(x,y,w,h,bounds,true);}
	public static void rectangle(float x, float y, float w, float h, boolean fill) {rectangle(x,y,w,h,ArrayMath.setTemp4(0,0,1,1),fill);}
	public static void rectangle(float x, float y, float w, float h, float[] bounds, boolean fill) {
		gl.glBegin((fill ? GL2.GL_QUADS : GL2.GL_LINE_LOOP));
			gl.glTexCoord2d(bounds[0], bounds[1]);	gl.glVertex3f(x, y, orthoLayer);
			gl.glTexCoord2d(bounds[2], bounds[1]);	gl.glVertex3f(x+w, y, orthoLayer);
			gl.glTexCoord2d(bounds[2], bounds[3]);	gl.glVertex3f(x+w, y+h, orthoLayer);
			gl.glTexCoord2d(bounds[0], bounds[3]);	gl.glVertex3f(x,y+h, orthoLayer);		
        gl.glEnd();
	}

	public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {triangle(x1,y1,x2,y2,x3,y3,false);}
	public static void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {triangle(x1,y1,x2,y2,x3,y3,true);}
	public static void triangle(float x1, float y1, float x2, float y2, float x3, float y3, boolean fill) {
		gl.glBegin((fill ? GL2.GL_TRIANGLES : GL2.GL_LINE_LOOP));
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
			gl.glTexCoord2d(.5, .5);	gl.glVertex3f(x, y, orthoLayer);
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
		float frac, topY,bottomY = y;
		RGBA topColor, bottomColor = col1;
		
		for(int i = 0; i < nSteps; i++) {
			frac = (i+1)/nSteps;
			
			topY = bottomY;
			bottomY = y + h*frac;
			
			topColor = bottomColor;
			bottomColor = RGBA.interpolate(col1, col2, frac);
			
			gl.glBegin(gl.GL_QUADS);
				setColor(topColor);
				gl.glVertex3f(x, topY, orthoLayer);
				gl.glVertex3f(x+w, topY, orthoLayer);
				
				setColor(bottomColor);
				gl.glVertex3f(x+w, bottomY, orthoLayer);
				gl.glVertex3f(x,bottomY, orthoLayer);
			gl.glEnd();   
		}
	}

	
	// 3D Functions
			
		// Wall-Drawing Function
			
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2) {
				draw3DWall(x1,y1,z1,x2,y2,z2,null);
			}
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
				draw3DWall(x1,y1,z1, x2,y2,z2, tex, ArrayMath.setTemp4(0,0,1,1));
			}
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2, MultiTexture tex, int frame) {
				draw3DWall(x1,y1,z1, x2,y2,z2, tex.getTexture(), tex.getBounds(frame));				
			}
			public static void draw3DWall(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex, float[] bounds) {
				
				if(tex != null) {
					gl.glEnable(GL2.GL_TEXTURE_2D);
					tex.bind(gl);
					tex.enable(gl);
				}

				float dir, nX, nY;
				dir = Math2D.calcPtDir(x1,y1,x2,y2)-90;
					nX = Math2D.calcLenX(dir);
					nY = Math2D.calcLenY(dir);

					
				// Begin Model
				gl.glBegin(GL2.GL_QUADS);
					// Add Four Corners
					gl.glNormal3f(nX,nY,0);
					gl.glTexCoord2f(bounds[0],bounds[1]);	
						gl.glVertex3d(x1, y1, z1);

					gl.glNormal3f(nX,nY,0);
					gl.glTexCoord2f(bounds[2],bounds[1]);
						gl.glVertex3d(x2, y2, z1);
					
					gl.glNormal3f(nX,nY,0);
					gl.glTexCoord2f(bounds[2],bounds[3]);
						gl.glVertex3d(x2, y2, z2);
					
					gl.glNormal3f(nX,nY,0);
					gl.glTexCoord2f(bounds[0],bounds[3]);	
						gl.glVertex3d(x1, y1, z2);
				// End (and Draw) Model
				gl.glEnd();
				
				// Unbind Texture
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

			/*public static void add3DVertWall(float x, float y1, float z1, float y2, float z2) {

				float[] mat, v1, v2, v3, v4, n;
				mat = getModelMatrix().array();
				v1 = mult(mat, new float[] {x,y1,z1,1});
				v2 = mult(mat, new float[] {x,y2,z1,1});
				v3 = mult(mat, new float[] {x,y1,z2,1});
				v4 = mult(mat, new float[] {x,y2,z2,1});
				n = mult(mat,new float[] {0,(y1 > y2) ? 1 : -1,0,0});

				gl.glTexCoord2f(0, 0); 	
					gl.glVertex3d(v1[0],v1[1],v1[2]);
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(v2[0],v2[1],v2[2]);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(v3[0],v3[1],v3[2]);

				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(v2[0],v2[1],v2[2]);
				gl.glTexCoord2f(1, 1);
					gl.glVertex3d(v4[0],v4[1],v4[2]);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(v3[0],v3[1],v3[2]);
			}
			
			
			public static void add3DHoriWall(float y, float x1, float z1, float x2, float z2) {
				
				float[] mat, v1, v2, v3, v4, n;
				mat = getModelMatrix().array();
				v1 = mult(mat, new float[] {x1,y,z1,1});
				v2 = mult(mat, new float[] {x2,y,z1,1});
				v3 = mult(mat, new float[] {x1,y,z2,1});
				v4 = mult(mat, new float[] {x2,y,z2,1});
				n = mult(mat,new float[] {0,(x1 < x2) ? 1 : -1,0,0});
				gl.glNormal3f(n[0],n[1],n[2]);

				gl.glTexCoord2f(0, 0); 	
					gl.glVertex3d(v1[0],v1[1],v1[2]);
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(v2[0],v2[1],v2[2]);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(v3[0],v3[1],v3[2]);
	
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(v2[0],v2[1],v2[2]);
				gl.glTexCoord2f(1, 1);
					gl.glVertex3d(v4[0],v4[1],v4[2]);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(v3[0],v3[1],v3[2]);
			}


			public static void add3DFloor(float x1, float y1, float x2, float y2, float z) {

				float[] mat, v1, v2, v3, v4, n;
				mat = getModelMatrix().array();
				v1 = mult(mat, new float[] {x1,y1,z,1});
				v2 = mult(mat, new float[] {x2,y1,z,1});
				v3 = mult(mat, new float[] {x1,y2,z,1});
				v4 = mult(mat, new float[] {x2,y2,z,1});
				n = mult(mat,new float[] {0,(x1 < x2) ? 1 : -1,0,0});
				gl.glNormal3f(n[0],n[1],n[2]);


				gl.glTexCoord2f(0, 0); 	
					gl.glVertex3d(v1[0],v1[1],v1[2]);
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(v2[0],v2[1],v2[2]);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(v3[0],v3[1],v3[2]);
		
				gl.glTexCoord2f(1, 0);
					gl.glVertex3d(v2[0],v2[1],v2[2]);
				gl.glTexCoord2f(1, 1);
					gl.glVertex3d(v4[0],v4[1],v4[2]);
				gl.glTexCoord2f(0, 1);
					gl.glVertex3d(v3[0],v3[1],v3[2]);
			}*/
			
			

			
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
			public static void draw3DFrustem(float radBot, float radTop, float h, int numPts, boolean ends) {draw3DFrustem(0,0,0,radBot,radTop, h, null, numPts,ends);}
			public static void draw3DFrustem(float radBot, float radTop, float h, Texture tex, int numPts) {draw3DFrustem(0,0,0,radBot,radTop, h, tex, numPts);}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h) {draw3DFrustem(x,y,z,radBot,radTop, h, null);}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex) {draw3DFrustem(x,y,z,radBot,radTop, h, tex, 10);}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, int numPts) {draw3DFrustem(x,y,z,radBot,radTop, h, null, numPts);}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, int numPts, boolean ends) {draw3DFrustem(x,y,z,radBot,radTop, h, null, numPts, ends);}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex, int numPts) {draw3DFrustem(x,y,z,radBot,radTop,h,tex,numPts,true);}
			public static void draw3DFrustem(float x, float y, float z, float radBot, float radTop, float h, Texture tex, int numPts, boolean ends) {				
				if(numPts < 3)
					return;
				
				bind(tex);
				
				float dir, zDir, xN, yN, zN;
				
				// Draw Top	
				if(ends && radTop > 0) {
					gl.glBegin(GL2.GL_TRIANGLE_FAN);
					gl.glNormal3f(0,0,1);
					gl.glTexCoord2d(.5,.5);
						gl.glVertex3f(x,y,z+h);
					for(int i = 0; i <= numPts; i++) {
						dir = 360.f*i/(numPts);

						xN = Math2D.calcLenX(dir);
						yN = Math2D.calcLenY(dir);	

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


	// this function is called when you want to activate the shader.
    // Once activated, it will be applied to anything that you draw from here on
    // until you call the dontUseShader(GL) function.
    public static void enableShader(String shaderName) {
    		//stM("enableShader");
    	shaderProgram = Shader.getShader(shaderName).getProgram();
	    
	    gl.glUseProgram(shaderProgram);
		gl.glUniform2iv(gl.glGetUniformLocation(shaderProgram, "iResolution"), 1, resolutionArray, 0);
    	gl.glUniform1f(gl.glGetUniformLocation(shaderProgram, "iGlobalTime"), getTime()/50f);        
    	
    	gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "tex0"), 0);
    	gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, "tex1"), 1);
    	
    	passShaderVec4("uColor",drawingColor.RGBAf());
    		//eM();
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
    
    public static void passShaderVec4(String name, float a, float b, float c, float d) {passShaderVec4(name, ArrayMath.setTemp4(a,b,c,d));}
    public static void passShaderVec4(String name, float[] vec) {
    	if(shaderProgram != 0)
    		gl.glUniform4fv(gl.glGetUniformLocation(shaderProgram, name), 1, vec, 0);
    }
    
    public static void passShaderVec2(String name, float[] vec) {
    	if(shaderProgram != 0)
    		gl.glUniform2fv(gl.glGetUniformLocation(shaderProgram, name), 1, vec, 0);
    }

    
    /*public static boolean passProjectionMatrix() {
    	return passShaderMat4("projectionMatrix", getPerspectiveMatrix(), true);
    }
    public static boolean passViewMatrix() {
    	return passShaderMat4("viewMatrix", getViewMatrix(), true);
    }
    public static boolean passModelMatrix() {
    	return passShaderMat4("modelMatrix", getModelMatrix(), true);
    }*/


    public static void enableShaderDiffuse(float radius) {

    	enableShader("Diffuse");
    	gl.glUniform1f(gl.glGetUniformLocation(shaderProgram, "iRadius"), radius);
    }	
	 
    public static void enableShaderMetal(float x, float y, float z) {
    	enableShader("Metal");
    	bind(metalTex, 1);
    	passShaderVec4("iPosition", x,y,z,1);
    }

    public static void disableShaders() {
    		//stM("disableShaders");
		
    	gl.glUseProgram(shaderProgram = 0);
        bind(0,1);
        bind(0,0);
        
        	//eM();
    }
    
    public static void enableShaderGaussian(float blurRadius) {
    	enableShader("Gaussian");
	    	
    	int shaderprogram = Shader.getShader("Gaussian").getProgram();
		
		int res = getShLoc("iResolution");
		int globTime = getShLoc("iGlobalTime");
		gl.glUniform1f(getShLoc("iRadius"), blurRadius); //20
		gl.glUniform2iv(res, 1, resolutionArray, 0);			
    }
    
    private static int getShLoc(String varName) {return gl.glGetUniformLocation(shaderProgram, varName);}
    
    public static void enableShaderRainbow() {
    	enableShader("Rainbow");
    	bind(rainbowTex, 1);
    }

    
    public static void enableShaderFireballGaussian(float blurRadius) {
    	enableShader("FireballGaussian");
	    	
    	int shaderprogram = Shader.getShader("Gaussian").getProgram();

	    float[] resI = {
	    		SCREEN_WIDTH, SCREEN_HEIGHT
	    };
		
		int res = getShLoc("iResolution");
		int globTime = getShLoc("iGlobalTime");
		gl.glUniform1f(getShLoc("iRadius"), blurRadius); //20
		gl.glUniform2fv(res, 1, resI, 0);			
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
		setColorf(0,0,0,1);
		drawRectangle(x,y,w,h);
		
		int nSteps = 3;
		
		RGBA clearWhite, fullWhite, clearBlack, halfBlack, fullBlack;
		clearWhite = RGBA.createf(1,1,1,0); fullWhite = RGBA.createf(1,1,1,.8f);
		clearBlack = RGBA.createf(0,0,0,0); halfBlack = RGBA.createf(0,0,0,.5f); fullBlack = RGBA.createf(0,0,0,.7f);
		
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


	public static void clear() {clear(RGBA.BLACK);}
	public static void clear(RGBA color) {
		gl.glClearColor(color.Rf(),color.Gf(),color.Bf(),color.Af());
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	}


	
	public static Color getPixelColor(int x, int y) {return getPixelRGBA(x,y).getColor();}
	public static RGBA getPixelRGBA(int x, int y) {
		
		y = 480-y + 8;
		
		ByteBuffer RGB = ByteBuffer.allocateDirect(3);
		gl.glReadPixels(x, y-10, 1 , 1 , GL2.GL_RGB , GL2.GL_UNSIGNED_BYTE, RGB);
		
		return RGBA.createf(RGB.get(0)/255f,RGB.get(1)/255f,RGB.get(2)/255f);
	}

	public static void getScreenshot(Texture tex) {getScreenshot(tex.getTextureObject());}
	public static void getScreenshot(int tex) {getScreenshot(0,0,SCREEN_WIDTH,SCREEN_HEIGHT,tex);}
	public static void getScreenshot(int x, int y, int w, int h, int tex) {
		
		bind(tex);

		gl.glCopyTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, x,SCREEN_HEIGHT-y-h, w,h, 0);
		//gl.glCopyTexImage2D(tex.getTarget(), 0, GL2.GL_DEPTH_COMPONENT, x1, y2, 640,480, 0);
		//tex.setMustFlipVertically(true);
		
		unbind();		
	}

		
	public static void enableLight(int num, float dir, float dirZ) {enableLight(num, Math3D.calcPolarCoords(dir, dirZ));}
	public static void enableLight(int num, vec3 normal) {enableLight(num, normal.x(),normal.y(),normal.z());}
	public static void enableLight(int num, float nX, float nY, float nZ) {enableLight(num, new float[] {nX,nY,nZ, 0});}
	public static void enableLight(int num, float[] array) {

		int light = GL2.GL_LIGHT0 + MathExt.contain(0, num, 7);
				
		gl.glLightfv(light, GL2.GL_POSITION, array, 0);
		
		float[] black = {0, 0, 0, 1}, 
				white = {1, 1, 1, 1}, 
				color = drawingColor.RGBAf(), 
				gray = {.1f,.1f,.1f,1};
				
		gl.glLightfv(light, GL2.GL_AMBIENT, RGBA.BLACK.RGBAf(), 0);
		gl.glLightfv(light, GL2.GL_DIFFUSE, RGBA.WHITE.RGBAf(), 0);  //white
		gl.glLightfv(light, GL2.GL_SPECULAR, RGBA.WHITE.RGBAf(), 0); //white
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
	public static void disableLightings() {
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
	}

	public static void draw3DBlock(float x1, float y1, float z1, float x2, float y2, float z2) {draw3DBlock(x1,y1,z1,x2,y2,z2,(Texture) null);}
	public static void draw3DBlock(float x1, float y1, float z1, float x2, float y2, float z2, Texture tex) {
		draw3DWall(x1,y1,z1,x1,y2,z2,tex);
		draw3DWall(x2,y1,z1,x2,y2,z2,tex);
		draw3DWall(x1,y1,z1,x2,y1,z2,tex);
		draw3DWall(x2,y2,z1,x1,y2,z2,tex);
		draw3DFloor(x1,y1,x2,y2,z1,tex);
		draw3DFloor(x1,y1,x2,y2,z2,tex);
	}
	
	public static void draw3DBlock(float x1, float y1, float z1, float x2, float y2, float z2, CubeMap cm) {
		draw3DWall(x1,y1,z1,x1,y2,z2,cm.getLeft().getTexture());
		draw3DWall(x2,y1,z1,x2,y2,z2,cm.getRight().getTexture());
		draw3DWall(x1,y1,z1,x2,y1,z2,cm.getFront().getTexture());
		draw3DWall(x2,y2,z1,x1,y2,z2,cm.getBack().getTexture());
		draw3DFloor(x1,y1,x2,y2,z1,cm.getBottom().getTexture());
		draw3DFloor(x1,y1,x2,y2,z2,cm.getTop().getTexture());
	}

	/*public static void add3DBlock(float x1, float y1, float z1, float x2, float y2, float z2) {
		add3DVertWall(x1,y2,z1,y1,z2);
		add3DVertWall(x2,y1,z1,y2,z2);
		add3DHoriWall(y1,x1,z1,x2,z2);
		add3DHoriWall(y2,x2,z1,x1,z2);
		add3DFloor(x1,y1,x2,y2,z1);
		add3DFloor(x2,y1,x1,y2,z2);
	}*/

	public static void begin(int type) 	{gl.glBegin(type);}
	public static void end() 			{gl.glEnd();}
		
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
			disableLightings();
	}

	
	

	public static String checkError() {
		String errors = "";
		
		if(gl == null)
			return "";
		
		int error = gl.glGetError();
		//while((error = gl.glGetError()) != 0) {
			errors += error; // + ", ";
		//}
		//if(error != 0)
		//	ErrorPopup.open("A fatal OpenGL error has occurred.\nPlease contact the developer.\n(ERROR CODE #" + error + ")", false);*/
				
		return errors;
	}
	
	public static void forceColor(RGBA color) 	{enableFog(-1000,-1000,color);}
	public static void unforceColor() 			{disableFog();}
	
	public static void blurFBO(FBO fbo) {
		float alpha = .25f, initialRadius = 1, finalRadius = 32, numRadii = 5, numDirections = 8;
		
    	enableBlending();
		fbo.attach(gl);
			setAlpha(alpha);
			float x,y;
			for(float r = initialRadius; r < finalRadius; r += (finalRadius-initialRadius)/numRadii)
				for(int d = 0; d < 360; d += 360/numDirections) {
					x = Math2D.calcLenX(r, d);
					y = Math2D.calcLenY(r, d);
					drawFBO(x,y, fbo);
				}
			setAlpha(1);
		fbo.detach(gl);
    	disableBlending();
	}

	public static void setAlpha(float alpha) {
		drawingColor.Af(alpha);
		setColor(drawingColor);
	}

	public static void setHidden(boolean yes) {
        if(yes) {
        	gl.glEnable(GL2.GL_DEPTH_TEST);
        	gl.glDepthFunc(GL2.GL_LEQUAL);
        }
        else
        	gl.glDisable(GL2.GL_DEPTH_TEST);
	}

	public static void setViewportResolution() {
		setViewport(0,0,resolutionArray[0],resolutionArray[1]);
	}

	public static Texture loadTexture(String fileName) {return loadTexture(fileName, false);}
	public static Texture loadTexture(String fileName, boolean grayscale) {
		try {
			if(grayscale)
				return createTexture(ImageLoader.loadGrayscaleAlpha(fileName),false);
			else
				return createTexture(ImageLoader.load(fileName),false);
		} catch (IOException e) {
			return null;
		}
	}
	
    // CULLING
	    public static void enableCulling() 		{gl.glEnable(GL2.GL_CULL_FACE);}
	    public static void disableCulling() 	{gl.glDisable(GL2.GL_CULL_FACE);}
	
	// TEXTURING
	    public static void enableTextures() 	{gl.glEnable(GL2.GL_TEXTURE_2D);}
	    public static void disableTextures() 	{gl.glDisable(GL2.GL_TEXTURE_2D);}
   
	public static void enableInterpolation() {enableInterpolation(true);};
	public static void disableInterpolation() {enableInterpolation(false);};
	public static void enableInterpolation(boolean yes) {
		int type = yes ? GL2.GL_LINEAR : GL2.GL_NEAREST;
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, type);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, type);
	}	
	
	public static void setTextureRepeat(boolean yes) {
		int repeat = yes ? GL2.GL_REPEAT : GL2.GL_CLAMP_TO_EDGE;
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, repeat);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, repeat);
	}
	
	public static void drawWaveform(float dX, float dY, float w, float h, int startOffset, int endOffset, int jumpNum, int skipNum, SoundBuffer snd) {drawWaveform(dX,dY,w,h,startOffset,endOffset,jumpNum,skipNum,snd,false);}
	public static void drawWaveform(float dX, float dY, float w, float h, int startOffset, int endOffset, int jumpNum, int skipNum, SoundBuffer snd, boolean shrinkSides) {
		
		List<float[]> pts = new ArrayList<float[]>();
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
				
				pts.add(new float[] {dX + n*xSize, (float) (dY + h/2*avg*(1 - 2*Math.abs( 1f*(i-startOffset)/len - .5f)) )} );
				
				n++;
			}
		}
		else
			for(int i = startOffset; i+jumpSize < endOffset; i += jumpSize) {
				avg = 0;
				for(int k = 0; k < jumpSize; k += (1+skipNum)*packetSize)
					avg += snd.getAmplitudeFraction(i);
				avg /= jumpNum/(1+skipNum);
				
				pts.add(new float[] {dX + n*xSize, (float) (dY + h/2*avg)} );
				
				n++;
			}

		
		drawLine(pts);
		
		pts.clear();
	}
	
	


	//public static void addVertex(float x, float y, float z) {
		/*vec4 p;
		int pointInd = pointList.size();
		for(int i = 0; i < pointInd; i++) {
			p = pointList.get(i);
			if(x == p.x())
				if(y == p.y())
					if(z == p.z()) {
						pointInd = i;
						break;
					}
		}
		
		if(pointInd == pointList.size()) {
			vec4 vec = new vec4(x,y,z,1);
			pointList.add(vec);
			pointMap.put(x/,vec);
		}
		vertexList.add(new vec3(pointInd,-1,-1));
		
		modelVertex++;*/
	//}
	/*public static void addVertex(float x, float y, float z, float nX,float nY,float nZ, float uX,float uY) {
		pointList.add(new vec4(x,y,z,1));
		normalList.add(new vec4(nX,nY,nZ,0));
		uvList.add(new vec2(uX,uY));
		vertexList.add(new vec3(modelVertex,modelVertex,modelVertex));
		
		modelVertex++;
	}*/
	
	public static void perspective() {
		gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu = GLU.createGLU(gl);
        currentCamera.gluLookAt(glu);
	}

	public static void setCamera(Camera cam) {currentCamera = cam;}
	public static Camera getCamera() {return currentCamera;}
	public static Camera getMainCamera() {return mainCamera;}
	public static Camera getMarioCamera() {return marioCamera;}

	
	public static void iniPage() {
        new PageTransition();
        //new Floor(-64,-64,64,64,0,null);
        //PlayerPM.create(0,0,10);
	}

	public static void iniPMBattle() {
		Sound.iniLoad();
        
        //Model m = new Model("PleasantPath3.obj");
        //Model m = new Model("Peachs_Castle_1.obj");
        //m.load();

        //new ModelRenderer(m);
        //Room.changeRoom("Battle");
        
        Room.changeRoom("Toad Town Center");
	}
	public static void iniPMRoom() {
        Sound.iniLoad();

        new Floor(-64,-64,64,64,0,null);
        PlayerPM.create(0,0,10);
	}

	public static void ini3D() {
        Sound.iniLoad();


    	new WorldMap();

        //Floaties.ini();
                 
        // Generate Environment
        float seaLevel = 50;
        
    
        Heightmap m = new Heightmap(64, 2, "Resources/Heightmaps/hm1.jpg");
    	//BlockTerrain b = BlockTerrain.createFromHeightmap(m);
        //m.smooth(1);
        //m.halveResolution();

                        
        //player = Player.create(2400,2400,300);
        	
                        	
        //Actor a = new NPC(player.getX(),player.getY()+500,0);
        //a.taskChase(player);
        //a.taskRunStore();
        
        //new Water(seaLevel);
        //new Sword("Whatever");

    	vec3 pt;
        /*for(int i = 0; i < 10; i++) {
        	pt = m.generateRandomPointBetween(seaLevel, seaLevel+5);
        	new Grass(pt.x(),pt.y(),30,24,8,4, .5f);
        }*/
        
        for(int i = 0; i < 400; i++) {
        	pt = m.generateRandomPointAbove(seaLevel);
        	new PineTree(pt.x(),pt.y());
        }
                        
        for(int i = 0; i < 400; i++) {
        	pt = m.generateRandomPointAbove(seaLevel);
        	new Fern(pt.x(),pt.y());
        }
    
        /*new WindMill(player.getX(),player.getY());
        new Town(player.getX() + 500, player.getY());*/
        
        
        new Chest(2600,2400,"Bread");
        
    	new SmartPhone();
    	//new Radio(2500,2500);
	}
	
	public static void ini2D() {
		new Player2D(100,200);
		Terrain2D.ini();
	}

	public static float getViewX() {return viewPos[0];}
	public static float getViewY() {return viewPos[1];}

	public static int getScreenWidth() 	{return SCREEN_WIDTH;}
	public static int getScreenHeight() {return SCREEN_HEIGHT;}

	public static float getPageCurl() {
		return curlTime;
	}

	public static com.jogamp.opengl.GL getGL() {return gl;}
	public static GL2 getGL2() {return gl.getGL2();}

	public static void endPageCurl() {
		curlTime = -1;
	}
	public static int getShaderProgram() {
		return shaderProgram;
	}
}