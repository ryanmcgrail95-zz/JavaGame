package testbench;

import java.awt.Color;

import datatypes.StringExt;
import datatypes.lists.CleanList;
import functions.FastMath;
import functions.Math2D;
import functions.MathExt;
import gfx.RGBA;
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
		int color1 = RGBA.convertRGBA2Int(1, 2, 3, 4), color2 = (new Color(1,2,3,4)).getRGB();
		int[] rgba = RGBA.convertInt2RGBA(color2);
		
		for(int i : rgba)
			System.out.println(i);
		
		if(true)
			return;
		
		/*System.out.println(testV(ang1,ang2));
		System.out.println(Math2D.calcAngDiff(ang1,ang2));*/
		
		int trialNum = 1000;
		float[] times = new float[2];
		
		Stopwatch s = new Stopwatch();
		
		boolean b;
		float newV, v = 30, f = 20, x1 = 1, x2 = 5, y1 = 10, y2 = 2;
		float[][][] vec = new float[10][10][10000];
		for(int i = 0; i < 10000; i++)
			vec[0][0][i] = i;
		
		for(int n = 0; n < trialNum; n++) {
			s.start();
			
			for(int i = 0; i < 10000; i++)
				v = vec[0][0][i];
			
			s.stop();
			
			times[0] += s.getMilli();
			
			s.start();
			
			for(int i = 0; i < 10000; i++)
				v = i/10000*10000;
			
			s.stop();
			times[1] += s.getMilli();
		}
		
		System.out.println(times[0]/trialNum);
		System.out.println(times[1]/trialNum);
		
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
