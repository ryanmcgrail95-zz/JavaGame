package cont;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Sounds.SoundController;
import gfx.GOGL;
import obj.prim.Updatable;


public class GameController extends Frame implements Runnable, WindowListener//, GLEventListener 
{	
	private static GameController me;		
	private boolean isRunning = true;
	double dir = 0;
	int lastFpsTime;
	int fps;    
	
	
	BufferedImage bg;
	    
    
	
    
	public static void main(String args[]) {
		me = new GameController();
		me.start();
	}
    
    
	public static GameController getInstance() {
		return me;
	}
    
    public GameController()  {    	
    	addWindowListener(this);
    	
        // We'll ask the width and height by this 

    	this.setVisible(true);
    	setSize(GOGL.SCREEN_WIDTH, GOGL.SCREEN_HEIGHT);
         // We'll redraw the applet each time the mouse has moved. 
         //addMouseMotionListener(this);     	
    	
    	//Load JOAL and JOGL
    	
    	
    	SoundController.ini();
        GOGL.start3D(this);
        
        IO.ini();//addKeyListener(input);
         
         
     
         /*new Floor(-96,-96,96,96,0,null);
         new GroundBlock(0,0,0);
         
         Player en = new Player(0,0,0);
         en.setSprite(r);
         en.setControllable(false, true);
         
         
         
         //Graphics3D.setBackgroundImage(ImageController.getImage("bgMountains"));
          * 
          */
    }
    
    
    public void update() {
    }
	
		
	public void start() 
    {		
		Thread loop = new Thread()
	    {
			public void run()
	        {
				gameLoop();
	        }
	    };
	    loop.start();
    }
    
    
    //ANIMATING SCRIPTS
	    public void gameLoop()
	    {
	       long lastLoopTime = System.nanoTime();
	       final int TARGET_FPS = 60;
	       final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;   
	
	       // keep looping round til the game ends
	       while(isRunning)
	       {
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
	          if (lastFpsTime >= 1000000000)
	          {
	             System.out.println("(FPS: "+fps+")");
	             lastFpsTime = 0;
	             fps = 0;
	          }
	          
	          // update the game logic
	          doGameUpdates(delta);
	          
	          // draw everyting
	          GOGL.repaint();
	          
	          // we want each frame to take 10 milliseconds, to do this
	          // we've recorded when we started the frame. We add 10 milliseconds
	          // to this and then factor in the current time to give 
	          // us our final value to wait for
	          // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
	          try
	          {
	        	  long sleepTime;
	        	  sleepTime = (lastLoopTime-System.nanoTime() + OPTIMAL_TIME)/1000000;
	   
	        	  
	        	  if(sleepTime >= 0)
	        		  Thread.sleep(sleepTime);
	          } catch(InterruptedException e)
	          {  
	          }
	       }	       
	    } 	
	
	    private void doGameUpdates(double delta)
	    {
	    	//updateGUI();
	    	
	    	
	          // all time-related values must be multiplied by delta!
	          //s.velocity += Gravity.VELOCITY * delta;
	          //s.position += s.velocity * delta;
	    }
	
 
    public void stop() {
    	//Graphics3D.stop();
    }

    public void run()
	{
	}
    
    




	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowClosed(WindowEvent arg0) {
	}



	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);		
	}



	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	public static void close() {
		System.exit(0);
	}
    
    
}
