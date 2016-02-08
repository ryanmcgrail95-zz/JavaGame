package gfx;

import fl.FileExt;
import functions.Math2D;
import functions.Math3D;

import java.io.IOException;
import java.nio.IntBuffer;

import resource.sound.Sound;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import ds.lst.CleanList;

public class FBO {
	
	private static CleanList<FBO> fboList = new CleanList<FBO>("FBOs");
	private int fbo, tex, texDepth, rb;
	private int width, height;
	
	public FBO(GL gl, int width, int height) {
		this.width = width;
		this.height = height;
		ini(gl);
		
		fboList.add(this);
	}
	
	private void ini(GL gl) {
		
		GL2 gl2 = gl.getGL2();
		
		fbo = genFBO(gl);
	    gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, fbo);
	
	    tex = genTexture(gl);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
	    //gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_RGBA, w,h, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
	    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_RGBA, width,height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
	    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, tex, 0);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	    
	    // Renderbuffer
	    /*rb = genRB(gl);
	    gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, rb);
	    gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, w,h);
	    gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, rb);	    
	    gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, 0);*/
	    
	    texDepth = genTexture(gl);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, texDepth);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_DEPTH_TEXTURE_MODE, GL2.GL_INTENSITY);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE, GL2.GL_COMPARE_R_TO_TEXTURE);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);
	    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT24, width,height, 0, GL2.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
	    gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL.GL_TEXTURE_2D, texDepth, 0);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	    	    
	    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);	   
	    
	    //GOGL.checkError();
	    /*gl2.glDrawBuffer(GL2.GL_NONE);
	    gl2.glReadBuffer(GL2.GL_NONE);*/
	}

	public void attach(GL gl) {
		attach(gl,true);
	}
	public void attach(GL gl, boolean ortho) {
				
		G2D.currentFBO = this;

 		if(ortho)
 			setOrtho(gl);
 		else
 			setPerspective(gl);


	    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, fbo);	    
	}
	
	
	
	public void setOrtho(GL gl) {
		GL2 gl2 = gl.getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(0,width,0,height, -1000,1000);
		//gl2.glOrtho(GOGL.getViewX(),GOGL.getViewX()+width,GOGL.getViewY(),GOGL.getViewY()+height, -1000,1000);
        gl.glViewport(0,0,width,height);
		gl2.glMatrixMode (GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
	}
	public void setPerspective(GL gl) {
		GLU glu = GLU.createGLU(gl);
		GL2 gl2 = gl.getGL2();
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		float camX, camY, camZ, toX, toY, toZ;
		Camera camera = G3D.getCamera();
		camX = camera.getX();
		camY = camera.getY();
		camZ = camera.getZ();
		toX = camera.getToX();
		toY = camera.getToY();
		toZ = camera.getToZ();
        // Perspective.
        float widthHeightRatio = 1f*G3D.SCREEN_WIDTH/G3D.SCREEN_HEIGHT; //getViewWidth()/getViewHeight();
        glu.gluPerspective(camera.getFOV(), widthHeightRatio, 1, 10000);
        G3D.getCamera().gluLookAt(glu); 
        
        gl.glViewport(0,0,width,height);
		gl2.glMatrixMode (GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
	}
	
	public void setOrthoPerspective(GL gl) {
		GLU glu;
		GL2 gl2 = gl.getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        
        // Perspective.
        /*int x1,y1,x2,y2;
        y2 = (int) (480/2f);
        y1 = -y2;
        x2 = (int) (640/2f);
        x1 = -x2;
        gl2.glOrtho(x1,x2,y1,y2, -1000,10000);*/
		gl2.glOrtho(-width/2,width/2,-height/2,height/2, -1000,10000);

        glu = GLU.createGLU(gl);
        G3D.getCamera().gluLookAt(glu);
        gl.glViewport(0,0,width,height);
	    gl2.glMatrixMode(GL2.GL_MODELVIEW);
	    gl2.glLoadIdentity();
	}

	public void detach(GL gl) {
	    gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		G2D.currentFBO = null;
	}
	
	public void bind(GL gl) {
	    gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, tex);
	}	
	public void unbind(GL gl) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
	    gl.glDisable(GL.GL_TEXTURE_2D);
	}
	
	
	public void bindDepth(GL gl) {
	    gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texDepth);
	}
	
	
	public int getTexture() {return tex;}
	
	
	private int genRB(GL gl) {
	    int[] array = new int[1];
	    IntBuffer ib = IntBuffer.wrap(array);
	    gl.glGenRenderbuffers(1, ib);
	    return ib.get(0);
	}
	private int genFBO(GL gl) {
	    int[] array = new int[1];
	    IntBuffer ib = IntBuffer.wrap(array);
	    gl.glGenFramebuffers(1, ib);
	    return ib.get(0);
	}
	private int genTexture(GL gl) {
	    final int[] tmp = new int[1];
	    gl.glGenTextures(1, tmp, 0);
	    return tmp[0];
	}
	
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	
	public void save(String fileName) {
		try {
			Texture t = new Texture(tex, GL.GL_TEXTURE_2D, width, height, width, height, false);
			AWTTextureIO.write(t, FileExt.getFile(fileName));
		} catch(IOException e) {
		}
	}
	
	public void saveScreenshot() {
		save("Resources/Screenshots/" + System.currentTimeMillis() + ".png");
	}

	
	public void clear(GL gl, RGBA color) {
		attach(gl);
		G2D.clear(color);
		detach(gl);
	}

	public void destroy(GL gl) {
		gl.glDeleteFramebuffers(1, new int[] {fbo}, 0);
		gl.glDeleteTextures(1, new int[] {tex}, 0);
		gl.glDeleteTextures(1, new int[] {texDepth}, 0);
		
		fboList.remove(this);
	}

	public static int getNumber() {
		return fboList.size();
	}
}
