package ds;

public class BitArray {

	private int[] arrays;
	private final static int INT_BYTE_NUM = 4*8;
	private int size, capacity, arrayNum;
	
	public BitArray(boolean... bits) {
		size = bits.length;
		arrayNum = (int) Math.ceil(bits.length/INT_BYTE_NUM);
		capacity = INT_BYTE_NUM*arrayNum;
		
		arrays = new int[arrayNum];
		
		int bitsLeft = size, curBit = 0;
		for(int i = 0; i < arrayNum; i++) {
			for(int k = 0; k < Math.min(bitsLeft,INT_BYTE_NUM); k++)
				arrays[i] = arrays[i] | (bits[curBit++] ? 1 : 0) << k;
			bitsLeft -= INT_BYTE_NUM;
		}
	}
	
	public boolean getBit(int index) {
		int intIndex;
		intIndex = (int) (index/8);
		index %= 8;
		
		return ((arrays[intIndex] >> index) & 1) == 1;
	}
}
