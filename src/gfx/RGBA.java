package gfx;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.Comparator;

import datatypes.vec4;
import obj.itm.Item;
import functions.Math2D;
import functions.MathExt;

public class RGBA{
	public static final RGBA
		RED = new RGBA(1,0,0,1),
		GREEN = new RGBA(0,1,0,1),
		BLUE = new RGBA(0,0,1,1),
		MAGENTA = new RGBA(1,0,1,1),
		YELLOW = new RGBA(1,1,0,1),
		CYAN = new RGBA(0,1,1,1),
		WHITE = new RGBA(1,1,1,1),
		BLACK = new RGBA(0,0,0,1),
		GRAY_DARK = new RGBA(.25f,.25f,.25f,1),
		GRAY = new RGBA(.5f,.5f,.5f,1),
		GRAY_LIGHT = new RGBA(.75f,.75f,.75f,1);

	private float R,G,B,A;
	
	public RGBA(float R, float G, float B, float A) {set(R,G,B,A);}
	public RGBA(float R, float G, float B) 			{set(R,G,B,1);}
	public RGBA(int rgba) 							{set(rgba);}
	public RGBA(Color color) 						{set(color.getRGB());}
	public RGBA(RGBA color) 						{set(color);}

	public void set(int rgba) {
		Color c = new Color(rgba);
		
		this.R = c.getRed()/255f;
		this.G = c.getGreen()/255f;
		this.B = c.getBlue()/255f;
		this.A = c.getAlpha()/255f;
	}
	public void set(RGBA color) {
		set(color.R,color.G,color.B,color.A);
	}
	public void set(float R, float G, float B, float A) {
		if(R > 1 || G > 1 || B > 1) {
			R /= 255;
			G /= 255;
			B /= 255;
		}
		
		this.R = R;
		this.G = G;
		this.B = B;
		this.A = A;
	}
	
	public float R() {return R;}	
	public float G() {return G;}
	public float B() {return B;}
	public float A() {return A;}
	public float getR() {return R;}	
	public float getG() {return G;}
	public float getB() {return B;}
	public float getA() {return A;}
		
	public int getRi() {return (int) Math.round(R*255);}	
	public int getGi() {return (int) Math.round(G*255);}
	public int getBi() {return (int) Math.round(B*255);}
	public int getAi() {return (int) Math.round(A*255);}	
	
	public float getValue() {return (float) (R*.27 + G*.71 + B*.07);}
	public void invert() {
		R = 1-R;
		G = 1-G;
		B = 1-B;
	}
	
	
	// STATIC
		public static RGBA randomize() {
			return new RGBA(MathExt.rnd(), MathExt.rnd(), MathExt.rnd(), 1);
		}
		public static RGBA interpolate(RGBA col1, RGBA col2, float f) {
			float iF;
			
			f = MathExt.contain(0, f, 1);
			iF = 1-f;

			return new RGBA(iF*col1.R + f*col2.R, iF*col1.G + f*col2.G, iF*col1.B + f*col2.B, iF*col1.A + f*col2.A);
		}
		public static RGBA mean(RGBA col1, RGBA col2) {
			return new RGBA(MathExt.squareMean(col1.R,col2.R), MathExt.squareMean(col1.G,col2.G), MathExt.squareMean(col1.B,col2.B), MathExt.squareMean(col1.A,col2.A));
		}

	public float[] getArray() {
		return new float[] {R,G,B,A};
	}

	public void println() {
		System.out.println(R + ", " + G + ", " + B + ", " + A);
	}
	
	public static RGBA randomizeAboveValue(float value) {
		RGBA rnd;
		do
			rnd = RGBA.randomize();
		while (rnd.getValue() < value);
		
		return rnd;
	}
	
	public Color getColor() {
		return new Color(R,G,B,A);
	}

	public static class Comparators {
		public final static Comparator<RGBA> RED = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {
                return (int) (o1.getR() - o2.getR());
            }
        };
        public final static Comparator<RGBA> GREEN = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {
                return (int) (o1.getG() - o2.getG());
            }
        };
        public final static Comparator<RGBA> BLUE = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {
                return (int) (o1.getB() - o2.getB());
            }
        };
        public final static Comparator<RGBA> VALUE = new Comparator<RGBA>() {
            public int compare(RGBA o1, RGBA o2) {
                return (int) (o1.getValue() - o2.getValue());
            }
        };
	}

	public float[] getRGBArray() {
		return new float[] {R,G,B};
	}
	public void setA(float alpha) {
		A = alpha;
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
