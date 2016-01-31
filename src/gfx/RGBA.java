package gfx;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Comparator;

import datatypes.vec4;
import obj.itm.Item;
import functions.ArrayMath;
import functions.Math2D;
import functions.MathExt;

public class RGBA extends ArrayMath {
	
	public static final RGBA
		RED = createf(1,0,0,1),
		GREEN = createf(0,1,0,1),
		BLUE = createf(0,0,1,1),
		MAGENTA = createf(1,0,1,1),
		YELLOW = createf(1,1,0,1),
		CYAN = createf(0,1,1,1),
		WHITE = createf(1,1,1,1),
		BLACK = createf(0,0,0,1),
		GRAY_DARK = createf(.25f,.25f,.25f,1),
		GRAY = createf(.5f,.5f,.5f,1),
		GRAY_LIGHT = createf(.75f,.75f,.75f,1),
		TRANSPARENT = createf(0,0,0,0);

	private int argb;
	
	private RGBA() 													{set(0);}
	private RGBA(int argb) 											{set(argb);}
	private RGBA(Color color) 										{set(color.getRGB());}
	private RGBA(RGBA color) 										{set(color);}

	public static RGBA create(int rgba) 							{return new RGBA(rgba);}
	public static RGBA create(Color color)							{return new RGBA(color);}
	public static RGBA create(RGBA rgba)							{return new RGBA(rgba);}
	public static RGBA createi(int r, int g, int b) 				{return new RGBA().seti(r,g,b);}
	public static RGBA createi(int r, int g, int b, int a) 			{return new RGBA().seti(r,g,b,a);}
	public static RGBA createf(float r, float g, float b) 			{return new RGBA().setf(r,g,b);}
	public static RGBA createf(float r, float g, float b, float a) 	{return new RGBA().setf(r,g,b,a);}
	public static RGBA creater() 									{return createf(MathExt.rnd(), MathExt.rnd(), MathExt.rnd(), 1);}
	
	public RGBA set(int argb) 										{this.argb = argb; return this;}
	public RGBA set(RGBA rgba) 										{return set(rgba.argb);}
	public RGBA set(Color color) 									{return set(color.getRGB());}
	public RGBA seti(int r, int g, int b, int a) 					{return set((a&0x0ff) << 24 | (r&0x0ff) << 16 | (g&0x0ff) << 8 | (b&0x0ff));}
	public RGBA seti(int r, int g, int b)		 					{return seti(r,g,b,255);}
	public RGBA setf(float r, float g, float b, float a) 			{return seti((int) (r*255), (int) (g*255), (int) (b*255), (int) (a*255));}
	public RGBA setf(float r, float g, float b) 					{return setf(r,g,b,1);}
	
	
	// Getting Colors
		public int Ri() 		{return (argb>>16) & 0xFF;}
		public int Gi() 		{return (argb>>8) & 0xFF;}
		public int Bi() 		{return (argb>>0) & 0xFF;}
		public int Ai() 		{return (argb>>24) & 0xFF;}
		
		public int[] RGBAi()	{return new int[] {Ri(),Gi(),Bi(),Ai()};}
		public int[] RGBi() 	{return new int[] {Ri(),Gi(),Bi()};}
		
		public float Rf() 		{return Ri()/255f;}
		public float Gf() 		{return Gi()/255f;}
		public float Bf() 		{return Bi()/255f;}
		public float Af() 		{return Ai()/255f;}
		
		public float[] RGBAf()	{return setTemp4(Rf(),Gf(),Bf(),Af());}
		public float[] RGBf() 	{return setTemp3(Rf(),Gf(),Bf());}

	// Setting Colors
		public void Ri(int r) 	{seti(r,Gi(),Bi(),Ai());}
		public void Gi(int g) 	{seti(Ri(),g,Bi(),Ai());}
		public void Bi(int b) 	{seti(Ri(),Gi(),b,Ai());}
		public void Ai(int a) 	{seti(Ri(),Gi(),Bi(),a);}
	
		public void Rf(float r) {setf(r,Gf(),Bf(),Af());}
		public void Gf(float g) {setf(Rf(),g,Bf(),Af());}
		public void Bf(float b) {setf(Rf(),Gf(),b,Af());}
		public void Af(float a) {setf(Rf(),Gf(),Bf(),a);}
	

	public int Vi() 		{return (int) (Ri()*.27 + Gi()*.71 + Bi()*.07);}
	public float Vf() 		{return (float) (Rf()*.27 + Gf()*.71 + Bf()*.07);}
	public void invert() 	{seti(255-Ri(),255-Gi(),255-Bi(),Ai());}
	
	
	// STATIC
		public static RGBA interpolate(RGBA col1, RGBA col2, float f) {
			float iF;
			
			f = MathExt.contain(0, f, 1);
			iF = 1-f;

			return new RGBA(iF*col1.R + f*col2.R, iF*col1.G + f*col2.G, iF*col1.B + f*col2.B, iF*col1.A + f*col2.A);
		}
		public static RGBA mean(RGBA col1, RGBA col2) {
			return createf(MathExt.squareMean(col1.Rf(),col2.Rf()), MathExt.squareMean(col1.Gf(),col2.Gf()), MathExt.squareMean(col1.Bf(),col2.Bf()), MathExt.squareMean(col1.Af(),col2.Af()));
		}


	public String toString() 	{return Rf() + ", " + Gf() + ", " + Bf() + ", " + Af();}
	public void println() 		{System.out.println(this);}
	
	public static RGBA randomizeAboveValue(float value) {
		RGBA rnd;
		do		rnd = creater();
		while 	(rnd.Vf() < value);
		
		return rnd;
	}
	
	public Color getColor() {return new Color(argb);}

	public static class Comparators {
		public final static Comparator<RGBA> RED = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {return o1.Ri() - o2.Ri();}
        };
        public final static Comparator<RGBA> GREEN = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {return o1.Gi() - o2.Gi();}
        };
        public final static Comparator<RGBA> BLUE = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {return o1.Bi() - o2.Bi();}
        };
        public final static Comparator<RGBA> VALUE = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {return o1.Vi() - o2.Vi();}
        };
	}
	
	public static int convertRGBA2Int(int r, int g, int b, int a) {
		return (a&0x0ff) << 24 | (r&0x0ff) << 16 | (g&0x0ff) << 8 | (b&0x0ff);
	}
	
	public static int[] convertInt2RGBA(int argb) {
		int[] outArray = new int[4];
		convertInt2RGBA(argb,outArray);
		return outArray;
	}
	public static void convertInt2RGBA(int argb, int[] array) {
		array[0] = (argb>>16) & 0xFF;
		array[1] = (argb>>8) & 0xFF;
		array[2] = (argb) & 0xFF;
		array[3] = (argb>>24) & 0xFF;
	}
}
