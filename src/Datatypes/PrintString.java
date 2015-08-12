package datatypes;

import resource.sound.Sound;


public class PrintString extends StringExt {
	private int len, waitTimer, WAIT_TIMER_MAX = 2, fallNum = 10;
	private String printedStr;
	private float fallAmts[];
	
	
	public PrintString(String str) {
		set(str);
		fallAmts = new float[fallNum];
		
		for(int i = 0; i < fallNum; i++)
			fallAmts[i] = 0;
	}
	
	
	public char charAt(int i) {
		return printedStr.charAt(i);
	}
	
	public int length() {
		return printedStr.length();
	}
	
	public void set(String str) {
		this.str = str;
		printedStr = "";
		len = 0;
		waitTimer = WAIT_TIMER_MAX;
	}
	
	public String get() {
		return printedStr;
	}
	
	public int getFallNumber() {
		return fallNum;
	}
	
	public float getLastAlpha(int i) {
		if(i < 0 || i >= fallNum)
			return 1;
		
		return fallAmts[i];
	}
	
	
	public boolean advance(int times) {
		boolean state = false;
		for(int i = 0; i < times; i++)
			state = state || advance();
		return state;
	}
	
	public boolean advance() {return advance("");}
	public boolean advance(String soundName) {
		for(int i = 0; i < fallNum; i++)
			fallAmts[i] += (1. - fallAmts[i])/4;
		
		if(len < str.length()) {
			if(waitTimer > 0) {
				waitTimer--;
			}
			else {
				if(soundName != "")
					Sound.play(soundName);
				
				waitTimer = WAIT_TIMER_MAX;
				
				printedStr += str.charAt(len);
				len++;
				
				for(int i = fallNum-1; i > 0; i--)
					fallAmts[i] = fallAmts[i-1];
				fallAmts[0] = 0;
			}
		}
		else if(waitTimer > 0)
			waitTimer--;
		
		return len == str.length();
	}
	
	public boolean isNull() {
		return (str.length() == 0);
	}


	public String getFull() {
		return str;
	}
}
