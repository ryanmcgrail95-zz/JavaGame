package testbench;

import datatypes.StringExt;
import datatypes.lists.CleanList;
import functions.Math2D;
import time.Stopwatch;
import brain.Idea;
import brain.Name;
import brain.SentenceProcessor;

public class Test {

	public static void testFunc() {
		StringExt testStr;
		testStr = new StringExt("What?\n\nOMG");
		
		System.out.println(testStr.chompLine());
		testStr.println();
	}
	
	public static void main(String[] args) {
		
		testFunc();
		
		/*int trialNum = 1000;
		float[] times = new float[2];
		
		Stopwatch s = new Stopwatch();
		
		for(int n = 0; n < trialNum; n++) {
			s.start();
			
			for(int i = 0; i < 10000; i++)
				Math2D.calcPtDis(0,0,10,10);
			
			s.stop();
			
			times[0] += s.getMilli();
			
			s.start();
			
			for(int i = 0; i < 10000; i++)
				Math2D.calcPtDir(0,0,10,10);
			
			s.stop();
			times[1] += s.getMilli();
		}
		
		System.out.println(times[0]/trialNum);
		System.out.println(times[1]/trialNum);
		*/
		/*Idea.ini();
		
		Idea boy, man;
		boy = new Idea("boy");
		man = new Idea("man");
				
		System.out.println(boy.compareTo(man));*/
		
		/*SentenceProcessor s = new SentenceProcessor();
		String sentence = "Who is he?";
		
		Idea.ini();
		
		s.passSentence(sentence);	
		s.printInfo();*/
	}
}
