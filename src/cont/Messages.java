package cont;

import gfx.G2D;
import gfx.GL;
import gfx.RGBA;
import java.util.ArrayList;
import java.util.List;
import datatypes.PrintString;
import datatypes.lists.CleanList;

public class Messages {
	private static CleanList<Message> messageList = new CleanList<Message>("Msgs");
	private static PrintString actionString = new PrintString("");
	
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

		public String getText() 		{return text;}
		public float getTime() 			{return steps;}

		public void set(String newStr) {text = newStr;}
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
		int dY = 0;
		float curTime;

		if(!actionString.isEmpty()) {
			actionString.advance(2);
			G2D.drawString(0,dY,1,1,0,actionString);
			dY += 12;
		}

		for(Message curMessage : messageList) {			
		    //Iterate Timers for Messages
			curMessage.incrementTime(1);
		    
		    //Sort Through and Eliminate Messages Whose Timers are -1
		    if(curMessage.isDone())
		    	messageList.remove();
		    else {
		    	//draw_set_font(fntRetro);

		    	curTime = curMessage.getTime();
		    	
	    	    //Draw Message
		    	GL.setColor(RGBA.createf(1f,1f,1f,(curTime < 20) ? curTime/20 : 1));
		    	G2D.drawStringCentered(0,dY, curMessage.getText(), true);
	    	    
	    	    //Move Next Message Down
	    	    dY += 12;
		    }
		}
	}
	
	public static void setActionMessage(String str) {
		if(actionString.getFull().compareTo(str) != 0)
			actionString.set(str);
	}
}
