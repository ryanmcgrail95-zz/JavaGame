package cont;
import io.IO;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gfx.GL;
import object.primitive.Updatable;
import resource.sound.Sound;
import rm.Room;
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
    	setSize(GL.SCREEN_WIDTH+2*GL.BORDER_LEFT, GL.SCREEN_HEIGHT+GL.BORDER_LEFT+GL.BORDER_TOP);
    	setResizable(false);
    	    	
    	Sound.ini();
        GL.start3D(this);
	    
        
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
		Delta.setTargetFPS(60); //60
	    Delta.setSpeed(1);
	    long now,
			lastLoopTime = System.nanoTime(),
			targetTime = (long)(1000000000 / Delta.getTargetFPS()),
			runTime;
	    double delta;
	       
	    // keep looping round til the game ends
	    while(isRunning) {
	        // work out how long its been since the last update, this
	        // will be used to calculate how far the entities should
	        // move this loop
	    	now = System.nanoTime();
	        runTime = now - lastLoopTime;
	        lastLoopTime = now;
	        delta = 1. * runTime / ((long)(1000000000 / Delta.getTargetFPS()));
	          
	        // update the frame counter
	        lastFpsTime += runTime;
	        fps++;
	          
	        // update our FPS counter if a second has passed since
	        // we last recorded
	        if (lastFpsTime >= 1000000000) {
	            lastFpsTime = 0;
	            fps = 0;
	        }
	          
	        Delta.setDelta((float) delta);
	          
	        // draw everyting
	        GL.repaint();
	          
	        // we want each frame to take 10 milliseconds, to do this
	        // we've recorded when we started the frame. We add 10 milliseconds
	        // to this and then factor in the current time to give 
	        // us our final value to wait for
	        // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
	        try {
	        	long sleepTime;
	        	sleepTime = (lastLoopTime-System.nanoTime() + (long)(1000000000 / Delta.getTargetFPS()))/1000000;
	        	  
	        	if(sleepTime >= 0)
	        		Thread.sleep(sleepTime);
	        } catch(InterruptedException e) {}
	    }	       
	} 	
	    public void gameLoop2() {
	       long lastLoopTime = System.nanoTime();
	       Delta.setTargetFPS(60); //60
	       Delta.setSpeed(1);
	       
	       // keep looping round til the game ends
	       while(isRunning) {
	          // work out how long its been since the last update, this
	          // will be used to calculate how far the entities should
	          // move this loop
	          long now = System.nanoTime();
	          long updateLength = now - lastLoopTime;
	          lastLoopTime = now;
	          double delta = updateLength / ((1000000000 / Delta.getTargetFPS()));
	          
	          // update the frame counter
	          lastFpsTime += updateLength;
	          fps++;
	          
	          // update our FPS counter if a second has passed since
	          // we last recorded
	          if (lastFpsTime >= 1000000000) {
	             lastFpsTime = 0;
	             fps = 0;
	          }
	          
	          Delta.setDelta((float) delta);
	          
	          // draw everyting
	          if(!Room.isLoading())
	        	  GL.repaint();
	          
	          // we want each frame to take 10 milliseconds, to do this
	          // we've recorded when we started the frame. We add 10 milliseconds
	          // to this and then factor in the current time to give 
	          // us our final value to wait for
	          // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
	          try {
	        	  long sleepTime;
	        	  sleepTime = (lastLoopTime-System.nanoTime() + (long)(1000000000 / Delta.getTargetFPS()))/1000000;
	        	  
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
	
	private static void unload() {
		Updatable.unload();
		
        Sound.unload();
	}

	public static void end() {
		unload();
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
