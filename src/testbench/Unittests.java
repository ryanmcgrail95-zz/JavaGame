package testbench;

import java.util.ArrayList;
import java.util.List;

import ds.lst.WeightedRandomList;
import ds.nameid.ID;
import ds.nameid.NameIDMap;
import functions.ArrayMath;
import functions.Math2D;
import functions.MathExt;
import script.PML;
import script.PMLMemory;
import script.Script;

public class Unittests {

	public static void testWrap() {
		
		System.out.println( "wrap(0,-1,10): " + MathExt.wrap(0,-1,10) );
		System.out.println( "wrap(0,11,10): " + MathExt.wrap(0,11,10) );
	}
	
	public static void testSinCos() {		
		int n = 100;
		double d, co, si, dd;
		for(int i = 0; i < n; i++) {
			d = 360./n*i;
			
			si = Math2D.sind(d);
			co = Math2D.cosd(d);
			
			dd = (Math2D.atan2(Math2D.sind(d), Math2D.cosd(d)))/Math.PI*180;
			if(dd < 0)
				dd += 360;
			
			System.out.println((int) i + ": " + (int) d + " -- " + (int) dd);
		}
	}
	
	public static void testWeightedRandom() {
		WeightedRandomList<Integer> list = new WeightedRandomList<Integer>("woohoo");
		list.add(0, .1f);
		list.add(1, .4f);
		list.add(2, .5f);
		
		int n = 10000000, counts[] = new int[3];
		for(int i = 0; i < n; i++)
			counts[list.get()]++;
		
		for(int count : counts)
			System.out.println((100. * count / n) + "%");
	}
	
	public static void testRandom() {
		System.out.println("Test Random ----------------------");
		
		int n = 10000000, sign;
		List<List<Integer>> counts = new ArrayList<List<Integer>>();
		
		for(int i = 0; i < 3; i++)
			counts.add(new ArrayList<Integer>());
		
		for(int i = 0; i < n; i++) {
			sign = MathExt.rndSign();
			if(sign == -1)
				counts.get(0).add(sign);
			else if(sign == 1)
				counts.get(1).add(sign);
			else
				counts.get(2).add(sign);
		}
		
		for(List<Integer> count : counts) {
			System.out.println((100. * count.size() / n) + "%");
			count.clear();
		}
	}
	
	public static void testPML() {
		PMLMemory mem = new PMLMemory();
		
		PML.ini(mem);

		//GL.enableLogging(true);
		//Script.exec(mem, "$a = 5; $b = 3; $b = $a; $i=0; ##fact($x){if($x <= 1) return $x; else return #fact($x-1) + #fact($x-2);} ##yo($x){return $x^4;} while($i < 10){#println(#yo($i += 1));} if(2 > 51){#println(\"wew\", 5);#println(2);}else #println(\"ewe\");	#println( $b + \"what\" + $a , $b, #mean(3, 8, 2), #min(-4, 100, -9,  -3) );");	
	}
	
	public static void testNameIDMap() {
		NameIDMap<String> map = new NameIDMap<String>();
		
		ID<String> 	a = map.add("a"),
					b = map.add("b"),
					c = map.add("c-id", "c");
		
		assert map.size() == 3;
		
		map.println();
			//map.remove();
			map.remove(0);
			map.remove(b);
			
			System.out.println();
		map.println();
		
		
		map.destroy();
	}
	
	public static void testVec() {
		float[] a = new float[4],
				b = new float[4];
		
		ArrayMath.set(a, 1,2,3,4);
			assert ArrayMath.equals(a, new float[] {1,2,3,4});
		ArrayMath.set(b, 5,6,7,8);
			assert ArrayMath.equals(b, new float[] {5,6,7,8});

		assert ArrayMath.dot(a,b) == (5+12+21+32);
				
		System.out.println("testVec passed!");
	}
	
	public static void testMat() {
		float[] a = new float[16],
				b = new float[16],
				v = new float[4],
	
				aV = new float[] {
					1,2,3,4,
					5,6,7,8,
					9,10,11,12,
					13,14,15,16},
				bV = new float[] {
					4,1,6,2,
					7,2,5,7,
					5,11,15,14,
					0,12,5,22},
				vV = new float[] {
						5,9,3,2
					},
				abV = new float[] {
					33,86,81,146,
					97,190,205,326,
					161,294,329,506,
					225,398,453,686},
				ab = new float[16],
				av = new float[4],
				avV = new float[] {
						40, 116, 192, 268
					};

		ArrayMath.set(a, aV);
			assert ArrayMath.equals(a, aV);
		ArrayMath.set(b, bV);
			assert ArrayMath.equals(b, bV);
		ArrayMath.set(v, vV);
			assert ArrayMath.equals(v, vV);

		ArrayMath.multMM(a,b, ab);
			ArrayMath.assertEquals(ab, abV);
		ArrayMath.multMV(a,v, av);		
			ArrayMath.assertEquals(av, avV);		
		
		System.out.println("testMat passed!");
	}
	
	public static void testChar() {
		char[] cs = new char[256];
		
		for(int i = 0; i < 256; i++) {
			cs[i] = (char) i;
			System.out.println(cs[i]);
		}
	}
	
	
	
	public static void main(String[] args) {
		
		//TestRandomList.run();
		TestCFG.run();
		
		
		//testChar();
		//testWrap();
		//testSinCos();
		//testWeightedRandom();
		//testRandom();
		//testPML();
		/*testNameIDMap();
		testVec();
		testMat();*/	
	}
}
