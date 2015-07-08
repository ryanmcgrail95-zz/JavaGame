package gfx;

import java.awt.Color;
import java.nio.FloatBuffer;

import func.Math2D;

public class RGBA {
	private float R;
	private float G;
	private float B;
	private float A;
	
	public RGBA(double R, double G, double B, double A) {
		this.R = (float) R;
		this.G = (float) G;
		this.B = (float) B;
		this.A = (float) A;
	}
	public RGBA(float R, float G, float B, float A) {
		this.R = R;
		this.G = G;
		this.B = B;
		this.A = A;
	}
	public RGBA(float R, float G, float B) {
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
		return G;
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
			return new RGBA(Math2D.rnd(), Math2D.rnd(), Math2D.rnd(), 1);
		}
		public static RGBA interpolate(RGBA col1, RGBA col2, float f) {
			float iF;
			
			f = Math2D.contain(0, f, 1);
			iF = 1-f;

			return new RGBA(iF*col1.R + f*col2.R, iF*col1.G + f*col2.G, iF*col1.B + f*col2.B, iF*col1.A + f*col2.A);
		}
		public static RGBA mean(RGBA col1, RGBA col2) {
			return new RGBA(Math.sqrt(Math2D.sqr(col1.R)+Math2D.sqr(col2.R)), Math.sqrt(Math2D.sqr(col1.G)+Math2D.sqr(col2.G)), Math.sqrt(Math2D.sqr(col1.B)+Math2D.sqr(col2.B)), Math.sqrt(Math2D.sqr(col1.A)+Math2D.sqr(col2.A)));
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
