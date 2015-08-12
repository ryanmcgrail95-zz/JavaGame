package gfx;

import java.awt.event.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.swing.JFrame;

import java.nio.*;

public class Main extends JFrame implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, ActionListener{


/* GL related variables */
private final GLCanvas canvas;
private GL2 gl;
private GLU glu;

private int winW = 600, winH = 600;

private FBO fbo;
private int texRender_RB;
private int texRender_32x32;

int times = 0;

public static void main(String args[]) {
    new Main();
}

/* creates OpenGL window */
public Main() {
    super("Problem Child");
    canvas = new GLCanvas();
    canvas.addGLEventListener(this);
    getContentPane().add(canvas);
    setSize(winW, winH);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
}

/* gl display function */
public void display(GLAutoDrawable drawable) {	
	
	System.out.println(times++);

	
	fbo.attach(gl);
	
		clear(gl);
		
		gl.glColor4f(1.f, 1.f, 1.f, 1f);
		gl.glBegin(GL2.GL_QUADS);
		{
		    gl.glVertex3f(0.0f, 0.0f, 1.0f);
		    gl.glVertex3f(0.0f, 1.0f, 1.0f);
		    gl.glVertex3f(1.0f, 1.0f, 1.0f);
		    gl.glVertex3f(1.0f, 0.0f, 1.0f);
		}
		gl.glEnd();
		
		gl.glColor4f(1.f, 0.f, 0.f, 1f);
		gl.glBegin(GL2.GL_TRIANGLES);
		{
		    gl.glVertex3f(0.0f, 0.0f, 1.0f);
		    gl.glVertex3f(0.0f, 1.0f, 1.0f);
		    gl.glVertex3f(1.0f, 1.0f, 1.0f);
		}
		gl.glEnd();
	fbo.detach(gl);

    
	clear(gl);

    
    gl.glColor4f(1.f, 1.f, 1.f, 1f);
    fbo.bind(gl);
	    gl.glBegin(GL2.GL_QUADS);
	    {
	        gl.glTexCoord2f(0.0f, 0.0f);
	        	gl.glVertex3f(0.0f, 1.0f, 1.0f);
	        gl.glTexCoord2f(1.0f, 0.0f);
	        	gl.glVertex3f(1.0f, 1.0f, 1.0f);
	        gl.glTexCoord2f(1.0f, 1.0f);
	        	gl.glVertex3f(1.0f, 0.0f, 1.0f);
	        gl.glTexCoord2f(0.0f, 1.0f);
	        	gl.glVertex3f(0.0f, 0.0f, 1.0f);
	    }
    gl.glEnd();
    fbo.unbind(gl);
}

public void clear(GL2 gl) {
	gl.glClearColor(1f,0f,0f, 1f);
	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);	
}

/* initialize GL */
public void init(GLAutoDrawable drawable) {
    gl = drawable.getGL().getGL2();
    glu = new GLU();


    gl.glOrtho(0, 2,2, 0, -1000, 1000);

    
    fbo = new FBO(gl,640,480);
}


/* mouse and keyboard callback functions */
public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    winW = width;
    winH = height;

    gl.glViewport(0, 0, width, height);
}

//Sorry about these, I just had to delete massive amounts of code to boil this thing down and these are hangers-on
public void mousePressed(MouseEvent e) {}
public void mouseDragged(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}
public void keyPressed(KeyEvent e) {}
public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) { }
public void keyTyped(KeyEvent e) { }
public void keyReleased(KeyEvent e) { }
public void mouseMoved(MouseEvent e) { }
public void actionPerformed(ActionEvent e) { }
public void mouseClicked(MouseEvent e) { }
public void mouseEntered(MouseEvent e) { }
public void mouseExited(MouseEvent e) { }

@Override
public void dispose(GLAutoDrawable arg0) {
	// TODO Auto-generated method stub
	
}

}