package testbench;

import ds.lst.RandomList;

public class TestRandomList {

	public static void run() {
		RandomList<Integer> list = new RandomList<Integer>("randomList");
		int b = 10;
		long counts[] = new long[b];
		

		for(int i = 0; i < b; i++) {
			list.add(i);
			counts[i] = 0;
		}		
		
		long n = 10000000;
		for(long i = 0; i < n; i++) {
			counts[list.get()]++;
		}

		for(int i = 0; i < b; i++) {
			System.out.println((100.*counts[i]/n) + "%");
		}
	}
}
