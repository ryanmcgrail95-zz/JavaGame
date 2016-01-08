package testbench;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import collision.C3D;
import datatypes.StringExt;
import datatypes.lists.CleanList;
import fl.FileExt;
import functions.FastMath;
import functions.Math2D;
import functions.MathExt;
import gfx.RGBA;
import time.Stopwatch;
import brain.Idea;
import brain.Name;
import brain.SentenceProcessor;

public class Test {
	
	public static void filter() {
		String fileName = "Resources/Models/outputCleanFenceless.old";
		
		File f = FileExt.getFile(fileName);
		File f2 = FileExt.getFile("Resources/Models/outputCleanFenceless.obj");
		
		try {
			BufferedReader buff = new BufferedReader(new FileReader(f));
			BufferedWriter buffW = new BufferedWriter(new FileWriter(f2));
			
			String line;
			StringExt lineExt = new StringExt(), wordExt = new StringExt();
			String[] words, nums;
			boolean isFace;
			
			boolean hasStarted = false;
						
			int totPs = 0, totUs = 0, totNs = 0, k = 0;

			while((line = buff.readLine()) != null) {
				lineExt.set(line);
								
				words = lineExt.split(' ');
				
				isFace = false;
				if(words.length > 0)
					if(words[0].equals("v")) {
						totPs++;
						isFace = true;
					}
					else if(words[0].equals("usemtl"))
						if(words[1].equals("material_4"))
							hasStarted = true;
						else
							hasStarted = false;
				
				hasStarted = (totPs >= 180 && totPs <= 360);
				
				String nLine;
				if(isFace && hasStarted) {
					nLine = "v";
					
					float[] pos = new float[3];
					float[] color = new float[3];
					for(int i = 1; i < 7; i++) {
						if(i < 4) {
							nLine += " " + words[i];
							pos[i-1] = Float.parseFloat(words[i]);
							continue;
						}
													
						float curNum;
						curNum = Float.parseFloat(words[i]);
						color[i-4] = curNum;
					}
					
					if((pos[2] >= -0.140000 && pos[2] <= -.13) || (pos[2] >= -0.11)) {
						line = nLine;
						
										
						float[] hsv = new float[3]; 
						Color.RGBtoHSB((int) (color[0]*255),(int) (color[1]*255),(int) (color[2]*255), hsv);
						
						
						Color c = Color.WHITE;
						//Color c = new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
						color[0] = c.getRed()/255f;
						color[1] = c.getGreen()/255f;
						color[2] = c.getBlue()/255f;
						
						line += " 1 1 1";
					}
				}
				
				line += "\r\n";
				
				buffW.write(line);
			}
						
			buff.close();
			buffW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void filter2() {
		String fileName = "Resources/Models/flowers.obj";
		
		File f = FileExt.getFile(fileName);
		File fOld = FileExt.getFile("Resources/Models/outputCleanHumpless2.obj");
		File f2 = FileExt.getFile("Resources/Models/outputCleanHumpless.obj");
		
		try {
			BufferedReader buff = new BufferedReader(new FileReader(f));
			BufferedReader buffOld = new BufferedReader(new FileReader(fOld));
			BufferedWriter buffW = new BufferedWriter(new FileWriter(f2));
			
			String line;
			StringExt lineExt = new StringExt(), wordExt = new StringExt();
			String[] words, nums;
			boolean isFace;
			
			String totFile = "";
			
			int totPs = 0, totUs = 0, totNs = 0, k = 0;

			while((line = buffOld.readLine()) != null) {
				lineExt.set(line);
				
				words = lineExt.split(' ');
				
				if(words.length > 0) {
					if(words[0].equals("v"))
						totPs++;
					else if(words[0].equals("vt"))
						totUs++;
					else if(words[0].equals("vn"))
						totNs++;
				}		
				
				buffW.write(line + "\r\n");
			}
			buffOld.close();
			
			while((line = buff.readLine()) != null) {
				lineExt.set(line);
								
				words = lineExt.split(' ');
				
				isFace = false;
				if(words.length > 0)
					if(words[0].equals("f"))
						isFace = true;
				
				if(isFace) {
					System.out.println(line);
					
					line = "f";
					
					for(int i = 1; i < 4; i++) {
						wordExt.set(words[i]);
						nums = wordExt.split('/');
						
						line += " ";
								
						k = 0;
						int curNum;
						for(String num : nums) {
							if(k == 0)
								curNum = totPs;
							else if(k == 1)
								curNum = totUs;
							else
								curNum = totNs;
							line += "" + (curNum + Integer.parseInt(num));
							
							if(k++ < 2)
								line += "/";
						}
					}
				}
				
				line += "\r\n";
				
				totFile += line;
				buffW.write(line);
			}
			
			System.out.println(totFile);
			
			buff.close();
			buffW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testFunc() {
		boolean a, b, c, d, e, f, g, h;
		a = b = g = d = false;
		e = f = c = h = true;
		
		boolean[] startArray;
		startArray = new boolean[] {a,b,c,d,e,f,g,h};
		
		int array = 0;
		int n = 8;
		
		for(int i = 0; i < n; i++)
			array = array | conv(startArray[i],i);
		
		for(int i = 0; i < n; i++)
			System.out.println(unconv(array,i));
	}
	
	public static int conv(boolean value, int index) {
		int v = value ? 1 : 0;
		return (v) << index;
	}
	
	public static int unconv(int value, int index) {
		return (value >> index) & 1;
	}
	
	public static void main(String[] args) {		
		float[] pt = {100,0,10}, 
				dir = {0,0,-1};
		float[] pt1 = {-10,-5,0},
				pt2 = {10,-5,0},
				pt3 = {0,10,0};
		float[][] ray = {pt, dir},
				triangle = {pt3, pt1, pt2};
		
		System.out.println(C3D.raycastTriangle(0,0,10, 0,0,-1, 
			-10,-5,-1,10,-5,0,0,10,0));
		
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
