package func;

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
	
	public void advance() {
		for(int i = 0; i < fallNum; i++)
			fallAmts[i] += (1. - fallAmts[i])/4;
		
		if(len < str.length()) {
			if(waitTimer > 0) {
				waitTimer--;
			}
			else {
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
	}
}
