package cont;
import io.IO;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gfx.GOGL;
import object.primitive.Updatable;
import resource.sound.Sound;
import time.Delta;


public class GameController extends JFrame implements WindowListener {	

	private static GameController instance;
	private boolean isRunning = true;
	int lastFpsTime;
	int fps;    
	
    
	public static void main(String args[]) {
		getInstance().start();
	}
	
	public static GameController getInstance() {
		if(instance == null)
			instance = new GameController();
		return instance;
	}
        
    public GameController()  {    	
    	addWindowListener(this);
    	
        // We'll ask the width and height by this 

    	this.setVisible(true);
    	//setSize(GOGL.SCREEN_WIDTH, GOGL.SCREEN_HEIGHT);
    	setSize(GOGL.SCREEN_WIDTH+2*GOGL.BORDER_LEFT, GOGL.SCREEN_HEIGHT+GOGL.BORDER_LEFT+GOGL.BORDER_TOP);
    	setResizable(false);
    	    	
    	Sound.ini();
        GOGL.start3D(this);
	    
        
        IO.ini();
    }
        			
	public void start() {		
		Thread loop = new Thread() {
			public void run() {
				gameLoop();
	        }
	    };
	    loop.start();
    }
    
    
    //ANIMATING SCRIPTS
	    public void gameLoop() {
	       long lastLoopTime = System.nanoTime();
	       final int TARGET_FPS = 60;
	       final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	       
	       // keep looping round til the game ends
	       while(isRunning) {
	          // work out how long its been since the last update, this
	          // will be used to calculate how far the entities should
	          // move this loop
	          long now = System.nanoTime();
	          long updateLength = now - lastLoopTime;
	          lastLoopTime = now;
	          double delta = updateLength / ((double)OPTIMAL_TIME);
	          
	          
	          // update the frame counter
	          lastFpsTime += updateLength;
	          fps++;
	          
	          // update our FPS counter if a second has passed since
	          // we last recorded
	          if (lastFpsTime >= 1000000000) {
	             //System.out.println("(FPS: "+fps+")");
	             lastFpsTime = 0;
	             fps = 0;
	          }
	          
	          Delta.setDelta((float) delta);
	          
	          // draw everyting
	          GOGL.repaint();
	          
	          // we want each frame to take 10 milliseconds, to do this
	          // we've recorded when we started the frame. We add 10 milliseconds
	          // to this and then factor in the current time to give 
	          // us our final value to wait for
	          // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
	          try {
	        	  long sleepTime;
	        	  sleepTime = (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000;
	        	  
	        	  if(sleepTime >= 0)
	        		  Thread.sleep(sleepTime);
	          } catch(InterruptedException e) {}
	       }	       
	    } 	

	public void windowClosing(WindowEvent arg0) {
		end();
	}

	public void windowActivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {}
	public void windowOpened(WindowEvent arg0) {}

	public static void end() {
        Sound.unload();
		System.exit(5);
	}

	/*public static BufferedImage getScreenshot() {
		Rectangle rec = getInstance().getBounds();
		BufferedImage img = null;
	    try {
			img = new Robot().createScreenCapture(rec);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	    
	    // CHANGE ME LATER
	    BufferedImage wtf =  new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    wtf.getGraphics().drawImage(img, 0, 0, null);
	    
	    return wtf;
	}*/
}
