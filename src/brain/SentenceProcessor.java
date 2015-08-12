package brain;

import datatypes.StringExt;

public class SentenceProcessor {
	private String currentSentence;

	private byte isQuestion = -1;
	private final static byte Q_WHO = 1;
	
	private Idea subject;
	
	public void passSentence(String s) {
		currentSentence = s;
		
		interpret();
	}
	
	public void interpret() {
		checkQuestion();
	}
	
	public boolean checkQuestion() {
		if(isQuestion == -1)
			if(currentSentence.startsWith("Who"))
				isQuestion = Q_WHO;
			else
				isQuestion = 0;
		
		return isQuestion != 0;
	}

	public void printInfo() {
		System.out.println("Subject:");
		
		if(subject != null)
			subject.printInfo();
	}
}
