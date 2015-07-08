package gfx;

import java.awt.Color;
import java.nio.FloatBuffer;

import functions.Math2D;
import functions.MathExt;

public class RGBA {
	public static final RGBA WHITE = new RGBA(1,1,1,1), RED = new RGBA(1,0,0,1), BLACK = new RGBA(0,0,0,1);
	private float R, G, B, A;
	
	public RGBA(float R, float G, float B, float A) {
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
	public RGBA(float R, float G, float B) {
		if(R > 1 || G > 1 || B > 1) {
			R /= 255;
			G /= 255;
			B /= 255;
		}
		
		this.R = R;
		this.G = G;
		this.B = B;
		this.A = 1;
	}

	public RGBA(int rgba) {
		Color c = new Color(rgba);
				
		R = c.getRed();
		G = c.getGreen();
		B = c.getBlue();
		A = c.getAlpha();
	}

	public float getR() {
		return R;
	}	
	public float getG() {
		return G;
	}
	public float getB() {
		return B;
	}
	public float getA() {
		return A;
	}
	
	public float getValue() {
		return (float) (R*.27 + G*.71 + B*.07);
	}
	
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
		float[] array = new float[4];
		array[0] = R;
		array[1] = G;
		array[2] = B;
		array[3] = A;
		
		return array;
	}

	public void println() {
		System.out.println(R + ", " + G + ", " + B + ", " + A);
	}
}
