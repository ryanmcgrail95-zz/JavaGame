package gfx;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jhlabs.image.BicubicScaleFilter;
import com.jhlabs.image.CropFilter;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.*;

import obj.env.blk.GroundBlock;
import obj.itm.Item;
import obj.itm.ItemController;
import obj.prt.Floaties;
import object.actor.NPC;
import object.actor.Partner;
import object.actor.Player;
import object.environment.Fire;
import object.environment.Floor;
import object.environment.Grass;
import object.environment.Heightmap;
import object.environment.Planet;
import object.environment.Tree;
import object.environment.Wall;
import object.environment.Water;
import object.primitive.Drawable;
import object.primitive.Instantiable;
import object.primitive.Updatable;
import resource.shader.Shader;
import resource.sound.SoundController;
import sts.Stat;
import time.Delta;
import window.Window;
import Datatypes.Inventory;
import Datatypes.PrintString;
import Datatypes.mat4;
import Datatypes.vec2;
import Datatypes.vec3;
import Datatypes.vec4;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import cont.CharacterController;
import cont.GameController;
import cont.ImageController;
import cont.Text;
import cont.TextureController;
import functions.Math2D;
import functions.Math3D;
import functions.MathExt;
import gui.PauseMenu;

public final class GOGL {
	private static final float TOP_LAYER = 999;
	private static GLCanvas canv;
	private static GLU glu = new GLU();
	private static GLEventListener listener;
	private static GLProfile gp = GLProfile.get(GLProfile.GL2);
	private static GL2 gl;
	private static boolean canLight = true;
	private static int curProgram;
	private static float[] viewPos;
	
    private static float[] perspectiveMatrix = new float[16];


    public static final int VIEW_FAR = 10000;
	public static final int P_TRIANGLE_STRIP = GL2.GL_TRIANGLE_STRIP;
	
	private static byte PR_ORTHO = 0, PR_PERSPECTIVE = 1;
	private static byte projectionMode;
	
	
	public static RGBA C_WHITE = new RGBA(1,1,1,1);
	public static int 
		BORDER_LEFT = 2, // 3 is PERFECT but doesn't work
		BORDER_TOP = 24, // 25 is PERFECT but doesn't work
			SCREEN_WIDTH = 640, SCREEN_HEIGHT = 480;
	//SCREEN_WIDTH = 640+BORDER_LEFT*2, SCREEN_HEIGHT = 480+BORDER_LEFT;
		
	private static float R = 1, G = 1, B = 1, A = 1;
	private static Player player;
	private static float orthoLayer = 0;
	
	private static Texture circTex;
	
	private static mat4 modelMatrix = new mat4();
	
	public static void start3D(GameController gameController) {
		
		GLProfile.initSingleton();
	    //gameController.setLayout(new BorderLayout());
	
	    canv = new GLCanvas();
	    	canv.setBackground(Color.WHITE);
	    	
	    listener =  new GLEventListener() {
            
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {}
            public void init( GLAutoDrawable glautodrawable ) {  	
            	
            	TextureController.ini();                
            	
            	
            	Text.ini();
            	
            	GLText.initialize();
            	CubeMap.ini();
            	
            	// Initialize Delta Time Controller
            	Delta.setTargetFPS(60);
            	Delta.setSpeed(1);
            	
            	ItemController.ini();
            	Window.ini();

                //GroundBlocks                
                
                CharacterController.ini();
                
                viewPos = new float[4];
                float border = 150 + Math2D.calcLenY(getTime())*20;
                setViewPos(0,border,SCREEN_WIDTH-border,SCREEN_HEIGHT-border);
            
                                
                Floaties.ini();
                
                         
                // Generate Environment
                float seaLevel = 50;
                
                Heightmap m = new Heightmap(64, 2, "Resources/Heightmaps/hm1.jpg");
                m.smooth(1);
                m.halveResolution();

                
                /*Planet p = new Planet(2000,2000,240, 32, 16, "Resources/Heightmaps/hm1.jpg");
                p.smooth(1);*/
                
                float aX, aY;
                for(int i = 0; i < 20; i++) {
                	aX = MathExt.rnd(-200,200);
                	aY = MathExt.rnd(-200,200);
                	Item.create("Rune Bar",1, 2000+aX,2000+aY,0);
                }
                
                player = Player.getInstance();
                	player.setX(2400);
                	player.setY(2400);
                                	
                new NPC(player.getX(),player.getY(),0);
                //partner = new Partner(20,20,0, "mario");

                new Water(seaLevel);
                	
                for(int i = 0; i < 10; i++) {
                	aX = MathExt.rnd(-200,200);
                	aY = MathExt.rnd(-200,200);
	                new Grass(2000+aX,2000+aY,30,24,8,4, .5f);
                }
                
                for(int i = 0; i < 50; i++) {
                	/*aX = MathExt.rnd(-200,200);
                	aY = MathExt.rnd(-200,200);
	                new Tree(2000+aX,2000+aY);*/
                	vec3 pt;
                	float pX,pY;
                	pt = m.generateRandomPointAbove(seaLevel);
                	new Tree(pt.x(),pt.y());
                }
                
                player.give("Rune Bar",1);
                player.give("Empty Bucket",1);
                player.give("Bread",1);
		        player.give("Empty Bowl",1);
		        player.addCoins(1000);
                
                            
                gl = glautodrawable.getGL().getGL2();
                gl.glEnable(gl.GL_DEPTH_TEST);
                gl.glEnable(gl.GL_ALPHA_TEST);
                
            	Shader.ini(gl);
            	//GalaxyBackground bg = new GalaxyBackground(gl);
            }
            
            public void dispose( GLAutoDrawable glautodrawable ) {
            }
            
            public void display( GLAutoDrawable glautodrawable ) { 
            	
            	Updatable.updateAll(1);
            	
            	gl = glautodrawable.getGL().getGL2();
            	setProjection();
            	draw();
            }
        };
        
        canv.addGLEventListener(listener); 
	
	    canv.setSize(gameController.getSize());
	    gameController.add(canv, BorderLayout.CENTER);
	}
	
	public static void setFogCoord(float x, float y, float z) {
		float[] array = new float[3];
		array[0] = x;
		array[1] = y;
		array[2] = z;
		gl.glFogCoordfv(array, 0);
	}
	
	public static void enableFog(float start, float end, RGBA col) {
		gl.glEnable(GL2.GL_FOG);
		gl.glFogi(GL2.GL_FOG_COORD_SRC, GL2.GL_FRAGMENT_DEPTH);
		gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);

		float[] array = new float[3];
		array[0] = col.getR();
		array[1] = col.getG();
		array[2] = col.getB();
		gl.glFogfv(GL2.GL_FOG_COLOR, array, 0);
		
		gl.glFogf(GL2.GL_FOG_START, start);
		gl.glFogf(GL2.GL_FOG_END, end);
	}
	
	public static void disableFog() {
		gl.glDisable(GL2.GL_FOG);
	}
		
	private static void setProjection() {
		
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
		
		SoundController.updateListener(camX,camY,camZ, 0,0,0, nX,nY,nZ, 0,0,1);
		
		
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        
        // Perspective.
        float widthHeightRatio = getViewWidth()/getViewHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, VIEW_FAR); //1000
        glu.gluLookAt(camX, camY, camZ, toX, toY, toZ, 0, 0, 1);
                        
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, perspectiveMatrix,0);

        // Change back to model view matrix.
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();	
	}
	
	public static void repaint() {
		canv.display();
	}
	
	public static GLCanvas getCanvas() {
		return canv;
	}

	public static void draw() {
		
		/*float[] trans = {
				camX, camY, camZ;
		}
		
		mRenderer.getModelMatCalculator().updateModelMatrix(
                pose.getTranslationAsFloats(),
                pose.getRotationAsFloats());*/
		
		float border = /*200*/ 0, bX, bY;
		bX = border;	bY = bX/SCREEN_WIDTH*SCREEN_HEIGHT;		
		setViewPos(BORDER_LEFT, bY+BORDER_LEFT, SCREEN_WIDTH-bX-BORDER_LEFT, SCREEN_HEIGHT-BORDER_TOP);
		
		Drawable.drawAll();
	}

	public static Texture createTexture(BufferedImage img, boolean mipmap) {
		if (img == null) 
			return null;
		
		//HORRIBLE BUG HERE
		//return AWTTextureIO.newTexture(AWTTextureIO.newTextureData(gp, img, 24, 24, mipmap));
		return AWTTextureIO.newTexture(gp, img, mipmap);
	}
	
	public static void bind(Texture tex) {
		
		if(tex != null) {
			gl.glEnable(gl.GL_TEXTURE_2D);
			tex.enable(gl);
			tex.bind(gl);			
		}
	}
	public static void unbind(Texture tex) {
		if(tex != null) {
			tex.disable(gl);
			gl.glDisable(gl.GL_TEXTURE_2D);
		}
	}
	
	public static void enableBlending() {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE); /*GL2.GL_REPLACE);*/
	}
	public static void disableBlending() {
		gl.glDisable(GL.GL_BLEND);
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
	public static float getViewWidth() {
		return viewPos[2]-viewPos[0];
	}
	public static float getViewHeight() {
		return viewPos[3]-viewPos[1];
	}
	
	
	public static void setOrtho() {
		setOrtho(TOP_LAYER);
	}
	
	public static boolean checkOrtho() {
		return projectionMode == PR_ORTHO;
	}
	
	public static boolean checkPerspective() {
		return projectionMode == PR_PERSPECTIVE;
	}
	
	public static void setOrtho(float useLayer) {
				
		if(checkOrtho())
			return;
		
		projectionMode = PR_ORTHO;
		
		orthoLayer = useLayer;
		//gl.glViewport((int) viewPos[0],(int) viewPos[1],(int) viewPos[2],(int) viewPos[3]);
		gl.glViewport((int) viewPos[0],(int) viewPos[1],(int) getViewWidth(),(int) getViewHeight());

		gl.glMatrixMode (gl.GL_PROJECTION);
		gl.glLoadIdentity();
		//gl.glOrtho(viewPos[0],viewPos[2], viewPos[3],viewPos[1], -1000, 1000);
		gl.glOrtho(0,SCREEN_WIDTH,SCREEN_HEIGHT,0, -1000, 1000);
		gl.glMatrixMode (gl.GL_MODELVIEW);
		gl.glLoadIdentity();
		//gl.glDisable(gl.GL_LIGHTING);
		gl.glDisable(gl.GL_DEPTH);
		gl.glDepthFunc(gl.GL_LEQUAL);
	}
	
	public static void setPerspective() {
		
		if(checkPerspective())
			return;
		
		
		orthoLayer = 0;
			
		projectionMode = PR_PERSPECTIVE;
		
		//gl.glViewport((int) viewPos[0],(int) viewPos[1],(int) viewPos[2],(int) viewPos[3]);
		gl.glViewport((int) viewPos[0],(int) viewPos[1],(int) getViewWidth(),(int) getViewHeight());
		//gl.glViewport((int) viewPos[0],(int) viewPos[1],(int) getViewWidth(),(int) getViewHeight());
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
    	setProjection();
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
		gl.glEnable(GL2.GL_DEPTH);
	}
	
	
	public static void setOrthoPerspective() {	
		orthoLayer = 0;		
		projectionMode = PR_PERSPECTIVE;
		
		gl.glViewport((int) viewPos[0],(int) viewPos[1],(int) getViewWidth(),(int) getViewHeight());
		
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
		
		SoundController.updateListener(camX,camY,camZ, 0,0,0, nX,nY,nZ, 0,0,1);
		
		
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        
        // Perspective.
        float widthHeightRatio = getViewWidth()/getViewHeight();
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

	}
	
	
	// DRAWING FUNCTIONS
	
	public static void setColori(int r, int g, int b) {		
		setColor(r/255f, g/255f, b/255f, 1);
	}
	public static void setColor(float r, float g, float b) {
		setColor(r,g,b,1);
	}
	public static void setColor(float nR, float nG, float nB, float nA) {
		R = nR;
		G = nG;
		B = nB;
		A = nA;
		
		
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
		gl.glColor4f(R,G,B,A);
		passShaderVec4("uColor",R,G,B,A);
	}
	
	public static void setLightColori(int r, int g, int b) {
		setLightColor(r/255f, g/255f, b/255f);
	}
	public static void setLightColor(float nR, float nG, float nB) {
		
		if(!canLight)
			return;
		
		R = nR;
		G = nG;
		B = nB;
		A = 1;
		
		float[] black = {
			0, 0, 0, 1
		}, white = {
			1, 1, 1, 1
		}, color = {
			R, G, B, 1
		}, gray = {
			.5f,.5f,.5f,1
		};
		
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, color, 0);
		gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glColor3f(R,G,B);
		
//		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, white, 0);
//		gl.glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 128f);
		//gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE);
		//gl.glColor3f(R,G,B);
		
		//gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, white,0);
		
		//gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 128f);
		
		passShaderVec4("uColor",R,G,B,A);
	}
	public static void setColor(RGBA col) {
		setColor(col.getR(),col.getG(),col.getB(),col.getA());
	}
	
	
	public static void drawTexture(float x, float y, TextureExt texExt) {
		drawTextureScaled(x,y,1,1,texExt);
	}
	public static void drawTextureScaled(float x, float y, float xS, float yS, TextureExt texExt) {
		Texture tex = texExt.getFrame(0);
		drawTextureScaled(x,y,xS,yS,tex);
	}
	
	public static void drawTexture(float x, float y, BufferedImage img) {
		drawTexture(x, y, img.getWidth(), img.getHeight(), img);
	}
	
	public static void drawTexture(float x, float y, float w, float h, BufferedImage img) {
		Texture tex = createTexture(img, false);
		
		drawTexture(x, y, w, h, tex);
		tex.destroy(gl);
	}

	public static void drawTexture(float x, float y, Texture tex) {
		drawTexture(x,y,tex.getWidth(),tex.getHeight(),tex);
	}
	public static void drawTextureScaled(float x, float y, float xS, float yS, Texture tex) {
		drawTexture(x,y,tex.getWidth()*xS,tex.getHeight()*yS,tex);
	}
	public static void drawTexture(float x, float y, float w, float h, Texture tex) {

		if(tex != null) {
			gl.glEnable(gl.GL_TEXTURE_2D);
			bind(tex);
			enableBlending();
		}
		
		fillRectangle(x,y,w,h);
      	
        if(tex != null) {
	      	gl.glDisable(GL.GL_TEXTURE_2D);
	      	tex.disable(gl);
        }
	}

	public static void drawRectangle(float x, float y, float w, float h) {				
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glTexCoord2d(0.0, 0.0);
				gl.glVertex3f(x, y, orthoLayer);
			gl.glTexCoord2d(1.0, 0.0);
				gl.glVertex3f(x+w, y, orthoLayer);
			gl.glTexCoord2d(1.0, 1.0);
				gl.glVertex3f(x+w, y+h, orthoLayer);
			gl.glTexCoord2d(0.0, 1.0);
				gl.glVertex3f(x,y+h, orthoLayer);
        gl.glEnd();      	      	
	}
	
	public static void drawLine(float x1, float y1, float x2, float y2) {
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(x1,y1, orthoLayer);
			gl.glVertex3f(x2,y2, orthoLayer);
	    gl.glEnd();
	}

	public static void fillRectangle(float x, float y, float w, float h) {				
		enableBlending();

		gl.glBegin(gl.GL_QUADS);
			gl.glTexCoord2d(0.0, 0.0);
				gl.glVertex3f(x, y, orthoLayer);
			gl.glTexCoord2d(1.0, 0.0);
				gl.glVertex3f(x+w, y, orthoLayer);
			gl.glTexCoord2d(1.0, 1.0);
				gl.glVertex3f(x+w, y+h, orthoLayer);
			gl.glTexCoord2d(0.0, 1.0);
				gl.glVertex3f(x,y+h, orthoLayer);
        gl.glEnd();      	      	
	}

	public static void fillTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		enableBlending();

		gl.glBegin(GL2.GL_TRIANGLES);
			gl.glTexCoord2d(0.0, 0.0);
				gl.glVertex3f(x1, y1, orthoLayer);
			gl.glTexCoord2d(1.0, 0.0);
				gl.glVertex3f(x2, y2, orthoLayer);
			gl.glTexCoord2d(1.0, 1.0);
				gl.glVertex3f(x3, y3, orthoLayer);
        gl.glEnd();      	      	
	}
	
	public static void fillCircle(vec4 vec, float r, int numPts) {
		fillCircle(vec.get(0), vec.get(1), r, numPts);
	}

	public static void fillCircle(float x, float y, float r, int numPts) {
		gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glTexCoord2d(0.5, .5);
				gl.glVertex3f(x, y, orthoLayer);
				
			float dir, xN, yN;
			
			for(int i = 0; i <= numPts; i++) {
				dir = 360.f*i/numPts;
				
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


	public static float drawChar(float x, float y, char c) {
		return GLText.drawChar(x,y,1,1,c);
	}
	public static float drawChar(float x, float y, float xS, float yS, char c) {
		return GLText.drawChar(x,y, xS,yS, c);
	}
	
	
	// Text Functions
	
	public static void drawString(float x, float y, String str) {
		GLText.drawString(x,y,1,1,str);
	}
	public static void drawString(float x, float y, float xS, float yS, String str) {
		GLText.drawString(x,y, xS,yS, str);
	}
	public static void drawString(float x, float y, PrintString str) {
		GLText.drawString(x,y,1,1,str);
	}
	public static void drawString(float x, float y, float xS, float yS, PrintString str) {
		GLText.drawString(x,y, xS,yS, str);
	}
	public static void drawStringCentered(float x, float y, String str) {
		GLText.drawStringCentered(x,y,1,1,str);
	}
	public static void drawStringCentered(float x, float y, float xS, float yS, String str) {
		GLText.drawStringCentered(x,y,xS,yS,str);
	}

	public static float getStringWidth(String str) {
		return GLText.getStringWidth(1,1,str);
	}
	public static float getStringWidth(float xS, float yS, String str) {
		return GLText.getStringWidth(xS,yS,str);
	}
	
	public static float getStringHeight(String str) {
		return GLText.getStringHeight(1,1,str);
	}
	public static float getStringHeight(float xS, float yS, String str) {
		return GLText.getStringHeight(xS,yS,str);
	}

	
	// 3D Functions
	
		// Transformation Functions
			public static void transformClear() {
				gl.glLoadIdentity();
				modelMatrix.setIdentity();
			}
			public static void setModelMatrix(mat4 mat) {
				modelMatrix = mat;
			}
			public static void transformTranslation(float x, float y, float z) {
				gl.glTranslatef(x,y,z);
				modelMatrix.multe(mat4.translation(x,y,z));
			}
			public static void transformScale(float s) {
				transformScale(s,s,s);
			}
			public static void transformScale(float xS, float yS, float zS) {
				gl.glScalef(xS, yS, zS);
				modelMatrix.multe(mat4.scale(xS,yS,zS));
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
				
				unbind(tex);
			}
			
			public static void draw3DFloor(float x1, float y1, float x2, float y2, float z) {
				draw3DFloor(x1,y1,x2,y2,z,null);
			}
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
				
				unbind(tex);
			}
			
		// Tetrahedron-Drawing Function
			public static void draw3DTetrahedron(float r, float h) {
				draw3DFrustem(r,h,null);
			}
			public static void draw3DTetrahedron(float r, float h, Texture tex) {
				draw3DTetrahedron(0,0,0,r,h,tex);
			}
			public static void draw3DTetrahedron(float x, float y, float z, float r, float h) {
				draw3DTetrahedron(x,y,z,r,h,null);
			}
			public static void draw3DTetrahedron(float x, float y, float z, float r, float h, Texture tex) {
				draw3DFrustem(x,y,z,r,0,h,tex,4);
			}
	
		// Frustem-Drawing Functions
			public static void draw3DFrustem(float r, float h) {
				draw3DFrustem(r,h,null);
			}
			public static void draw3DFrustem(float r, float h, int numPts) {
				draw3DFrustem(r,r,h,numPts);
			}
			public static void draw3DFrustem(float r, float h, Texture tex) {
				draw3DFrustem(r,r,h,tex);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h) {
				draw3DFrustem(radBot,radTop, h, null);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h, Texture tex) {
				draw3DFrustem(0,0,0,radBot,radTop, h, tex);
			}
			public static void draw3DFrustem(float radBot, float radTop, float h, int numPts) {
				draw3DFrustem(0,0,0,radBot,radTop, h, null, numPts);
			}
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
						
							nX = Math2D.calcPolarX(1,dir,dirZ);
							nY = Math2D.calcPolarY(1,dir,dirZ);
							nZ1 = Math2D.calcPolarZ(1,dir,dirZ);
							nZ2 = Math2D.calcPolarZ(1,dir,dirZ);
							
						dir1 = 360.f*i1/(numPts);
							xN = Math2D.calcLenX(1,dir1);
							yN = Math2D.calcLenY(1,dir1);

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
				
				unbind(tex);
	
			}
	
		// Sphere-Drawing Functions
			public static void draw3DSphere(float r) {
				draw3DSphere(0,0,0,r,null);
			}	
			public static void draw3DSphere(float r, int numPts) {
				draw3DSphere(0,0,0,r,null,numPts);
			}	
			public static void draw3DSphere(float r, Texture tex) {
				draw3DSphere(0,0,0,r,tex);
			}	
			public static void draw3DSphere(float r, Texture tex, int numPts) {
				draw3DSphere(0,0,0,r,tex,numPts);
			}	
			public static void draw3DSphere(float x, float y, float z, float r) {
				draw3DSphere(x,y,z,r,null);
			}	
			public static void draw3DSphere(float x, float y, float z, float r, int numPts) {
				draw3DSphere(x,y,z,r,null,numPts);
			}
			public static void draw3DSphere(float x, float y, float z, float r, Texture tex) {
				draw3DSphere(x,y,z,r,tex,10);
			}
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
							
							xN = Math2D.calcPolarX(1,dir,zDir);
							yN = Math2D.calcPolarY(1,dir,zDir);
							zN = Math2D.calcPolarZ(1,dir,zDir);
							
							gl.glTexCoord2d(i/(numPts-1), (d+ii)/(numPts-1));
								gl.glVertex3f(x+xN*r,y+yN*r,z+zN*r);
						}
					}
					
			        gl.glEnd();
				}
				
				unbind(tex);
			}

	public static void drawHud() {
		int instNum, upNum, drawNum, shapeNum;
		instNum = Instantiable.getNumber();
		upNum = Updatable.getNumber();
		drawNum = Drawable.getNumber();
		shapeNum = Shape.getNumber();
		
		setOrtho(-3);
		
		/*System.out.println("");
		System.out.println(instNum);
		System.out.println(upNum);
		System.out.println(drawNum);
		System.out.println(shapeNum);*/
		
		//MessageController.draw(gl);
		
		setPerspective();
	}
	


	// this function is called when you want to activate the shader.
    // Once activated, it will be applied to anything that you draw from here on
    // until you call the dontUseShader(GL) function.
    public static void enableShader(String shaderName) {
    	
    	Shader shader = Shader.getShader(shaderName);
    	curProgram = shader.getProgram();
    	
	    float[] resI = {
	    		SCREEN_WIDTH, SCREEN_HEIGHT
	    };
	    
	    gl.glUseProgram(curProgram);
		gl.glUniform2fv(gl.glGetUniformLocation(curProgram, "iResolution"), 1, resI, 0);			
    	gl.glUniform1f(gl.glGetUniformLocation(curProgram, "iGlobalTime"), getTime()/50f);        
    	passShaderVec4("uColor",R,G,B,A);
    }
    
    public static float getTime() {
    	return TextureController.getTime();
    }
    
    public static boolean passShaderFloat(String name, float val) {
    	if(curProgram == 0)
    		return false;
    	
    	gl.glUniform1f(gl.glGetUniformLocation(curProgram, name), val);
    	return true;
    }
    public static boolean passShaderMat4(String name, mat4 mat, boolean transpose) {

    	if(curProgram == 0)
    		return false;
    	
    	float[] pass = mat.array();
       	
		gl.glUniformMatrix4fv(gl.glGetUniformLocation(curProgram, name), 1, transpose, pass, 0);
		return true;
    }
    public static boolean passShaderVec4(String name, float a, float b, float c, float d) {

    	if(curProgram == 0)
    		return false;
    	
    	float[] pass = {
    		a, b, c, d
    	};
       	
		gl.glUniform4fv(gl.glGetUniformLocation(curProgram, name), 1, pass, 0);
		return true;
    }
    
    public static boolean passProjectionMatrix() {
    	return passShaderMat4("projectionMatrix", GOGL.getPerspectiveMatrix(), true);
    }
    public static boolean passViewMatrix() {
    	return passShaderMat4("viewMatrix", GOGL.getViewMatrix(), true);
    }
    public static boolean passModelMatrix() {
    	return passShaderMat4("modelMatrix", GOGL.getModelMatrix(), true);
    }

    public static void enableShaderGrayscale() {
    	gl.glUseProgram(Shader.getShader("Grayscale").getProgram());
    }

    public static void enableShaderDiffuse(float radius) {

    	enableShader("Diffuse");
    	gl.glUniform1f(gl.glGetUniformLocation(curProgram, "iRadius"), radius);
    }	
	    

    // when you have finished drawing everything that you want using the shaders, 
    // call this to stop further shader interactions.
    public static void disableShaders() {
        gl.glUseProgram(0);
        curProgram = 0;
    }
    
    
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
    
    public static void drawGalaxyBG() {
    	enableShader("Galaxy");
    	
		fillRectangle(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		disableShaders();
    }

    public static mat4 getPerspectiveMatrix() {
    	//WHAT
    	return getPerspectiveMatrix(45, 640f/480, 1,1000);
    }
    
    public static mat4 getPerspectiveMatrix(float fovy, float aspect, float near, float far) {
    	float x1,y1,x2,y2;
    	float a = Math2D.calcLenX(96,getTime());
    	
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
    }
    
    public static mat4 getViewMatrix() {

 		float eyeX, eyeY, eyeZ, upX, upY, upZ, fX, fY, fZ, toX, toY, toZ;
 		eyeX = Camera.getX();
 		eyeY = Camera.getY();
 		eyeZ = Camera.getZ();
 		toX = Camera.getToX();
 		toY = Camera.getToY();
 		toZ = Camera.getToZ();
    	vec4 camNorm = new vec4(toX-eyeX, toY-eyeY, toZ-eyeZ, 0);
    	    	
 		// Get Camera Position, Looking Normal, and Up Normal
 		
 		camNorm.set(camNorm.norm());
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
    }

	public static void drawFillBar(float x, float y, float w, float h, float frac) {
				
		// Inside
		setColor(0,1,0,1);
		fillRectangle(x, y, w*frac, h);
		
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

	public static void clearScreen() {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f );
    	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glAlphaFunc(gl.GL_GREATER, 0);
	}

	public static RGBA getPixelColor(int x, int y) {
		
		y = 480-y + 8;
		
		ByteBuffer RGB = ByteBuffer.allocateDirect(3);
		gl.glReadPixels(x, y-10, 1 , 1 , GL2.GL_RGB , GL2.GL_UNSIGNED_BYTE, RGB);
		
		float R, G, B;
		R = RGB.get(0)/255f;   //get the first byte
		G = RGB.get(1)/255f; //the second
		B = RGB.get(2)/255f;  //and third
		
		return new RGBA(R,G,B);
	}


	public static BufferedImage getScreenFragment(int x, int y, int w, int h) {
		CropFilter c = new CropFilter(x,y,w,h);
		
		return c.filter(getScreenshot(), null);
	}
	
	public static BufferedImage getScreenshot() {
		// BUG, OFF BY ONE WITH WIDTH/HEIGHT CAUSING WEIRD IMAGES
		
		
		int x1 = 0, y1 = 0, x2 = 640, y2 = 480;
		
		
		int bL = BORDER_LEFT, //8
			bT = BORDER_TOP; //32
		
		int screenW, screenH;
		screenW = 640-2*bL;
		screenH = 480-bT-bL;
		
		y1 = 480-y1;
		y2 = 480-y2;
		
		x1 = (int) (bL + 1f*x1/640*screenW);
		x2 = (int) (bL + 1f*x2/640*screenW);
		y1 = (int) (bL + 1f*y1/480*screenH);
		y2 = (int) (bL + 1f*y2/480*screenH);
		
		int width, height, cHeight;
		width = Math.abs(x2-x1);
		height = Math.abs(y2-y1);
		
		
		BufferedImage screenFragment = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics g = screenFragment.getGraphics();
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(3*(width)*(height));
		gl.glReadPixels(x1,y2, width,height, GL2.GL_RGB , GL2.GL_BYTE, buffer);
						
		int R,G,B;
		for (int h = 0; h < height; h++) {
	        for (int w = 0; w < width; w++) {
	            // The color are the three consecutive bytes, it's like referencing
	            // to the next consecutive array elements, so we got red, green, blue..
	            // red, green, blue, and so on..
	        		        	
	        	R = buffer.get()*2;
	        	G = buffer.get()*2;
	        	B = buffer.get()*2;
	        	
	            g.setColor(new Color(R,G,B));
	            g.drawRect(w,height - h, 1, 1); // height - h is for flipping the image
	        }
	    }
		
		BicubicScaleFilter scale = new BicubicScaleFilter(640,480);
	    return scale.filter(screenFragment, null);
	}

	
	public static void enableLight(int num, float nX, float nY, float nZ) {
		float[] array = {
			nX,nY,nZ, 0
		};
			
		//array = (new vec4(toX-camX, toY-camY, toZ-camZ, 0)).norm().mult(-1).getArray();

		int light;
		switch(num) {
			case 0:
				light = GL2.GL_LIGHT0;
				break;
			case 1:
				light = GL2.GL_LIGHT1;
				break;
			case 2:
				light = GL2.GL_LIGHT2;
				break;
			case 3:
				light = GL2.GL_LIGHT3;
				break;
			case 4:
				light = GL2.GL_LIGHT4;
				break;
			case 5:
				light = GL2.GL_LIGHT5;
				break;
			case 6:
				light = GL2.GL_LIGHT6;
				break;
			case 7:
				light = GL2.GL_LIGHT7;
				break;
			default:
				light = GL2.GL_LIGHT0;
				break;
		}
				
		gl.glLightfv(light, GL2.GL_POSITION, array, 0);
		
		float[] black = {
			0, 0, 0, 1
		}, white = {
			1, 1, 1, 1
		}, color = {
			R, G, B, 1
		}, gray = {
			.5f,.5f,.5f,1
		};
		gl.glLightfv(light, GL2.GL_AMBIENT, black, 0);
		gl.glLightfv(light, GL2.GL_DIFFUSE, white, 0);
		gl.glLightfv(light, GL2.GL_SPECULAR, white, 0);
		//gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, gray, 0);
		gl.glEnable(light);
	}
	
	public static void enableLighting() {
		
		if(!canLight)
			return;
		
		//enableLight(0,0,0,1);
		enableLight(1,1,0,0);
		enableLight(2,0,1,1);

		
		gl.glEnable(GL2.GL_LIGHTING);
	}
	public static void disableLighting() {
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_COLOR_MATERIAL);
	}

	public static void draw3DBlock(float x1, float y1, float z1, float x2, float y2, float z2) {
		draw3DBlock(x1,y1,z1,x2,y2,z2,null);
	}
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
	public static void vertex(float x, float y, float z) {
		gl.glVertex3f(x,y,z);
	}

	public static void allowLighting(boolean allow) {
		
		setLightColor(1,1,1);
		
		canLight = allow;
		
		if(!allow)
			disableLighting();
	}

	

	public static void drawStringS(float x, float y, String text, RGBA rgba) {
		// Draw String w/ Shadows
		GOGL.setColor(0,0,0,rgba.getA());
		drawString(x+1.5f,y+1.5f,text);
		GOGL.setColor(rgba);
		drawString(x,y,text);
		GOGL.setColor(C_WHITE);
	}
	
	public static void drawStringS(float x, float y, String text) {
		drawStringS(x,y,text,RGBA.WHITE);
	}
	
	public static RGBA getColor() {
		return new RGBA(R,G,B,A);
	}

	public static void forceColor(RGBA color) {
		enableFog(-1000,-1000,color);
	}
	public static void unforceColor() {
		disableFog();
	}

	public static mat4 getModelMatrix() {
		return modelMatrix;
	}
}