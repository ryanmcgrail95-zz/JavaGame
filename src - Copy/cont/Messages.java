package cont;

import gfx.GOGL;
import gfx.RGBA;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import com.jogamp.opengl.util.texture.Texture;

public class Messages {
	private static List<Message> messageList = new ArrayList<Message>();
	
	private static class Message {
		private String text;
		private float steps;
		
		
		public Message(String text, float steps) {
			this.text = text;
			this.steps = steps;
			
			messageList.add(this);
		}
		
		public void incrementTime(float time) {
			steps -= time;
		}
		
		public boolean isDone() {
			return (steps <= -1);
		}

		public String getText() {
			return text;
		}
		public float getTime() {
			return steps;
		}

		public void set(String newStr) {
			text = newStr;
		}
		public void setTime(float newSteps) {
			steps = newSteps;
		}
	}
	
	
	public static void add(String text, float steps) {
		// Adds a message that will appear in the upper left-hand corner. Useful for debugging or a Skyrim-esque
		// status system. The message will stay visible for the number of steps given; I recommend using 
		// time_to_steps() in conjunction with this.
		
		new Message(text, steps);
	}
	
	public static Message find(String substr) {
		for(Message msg : messageList)
			if(msg.text.contains(substr))
				return msg;
			
		return null;
	}
	
	public static boolean replace(String substr, String newStr, float newSteps) {
		Message msg;
		msg = find(substr);
		
		if(msg == null)
			return false;
		else {
			msg.set(newStr);
			msg.setTime(newSteps);
			return true;
		}
	}
	
	public static void draw() {
		List<Message> destroyList = new ArrayList<Message>();
		Message curMessage;
		int dY = 0;
		float sX, sY;
		sX = 1.5f;
		sY = 1.5f;

		for(int i = 0; i < messageList.size(); i += 1) {
			curMessage = messageList.get(i);
			
		    //Iterate Timers for Messages
			curMessage.incrementTime(1);
		    
		    //Sort Through and Eliminate Messages Whose Timers are -1
		    if(curMessage.isDone()) {
		        destroyList.add(curMessage);   
		    }
		    else {
		    	//draw_set_font(fntRetro);

		    	float alpha, curTime;
		    	curTime = curMessage.getTime();
		    	if(curTime < 20)
		    		alpha = curTime/20;
		    	else
		    		alpha = 1;

	    	    //Draw Message
		    	GOGL.drawStringS(0,dY, curMessage.getText(), new RGBA(1f,1f,1f,alpha));
	    	    
	    	    //Move Next Message Down
	    	    dY += 12;
		    }
		}
		
		
		//Remove Old Messages
		for(Message m : destroyList)
			messageList.remove(m);
	}
}
