package gfx;

import com.jogamp.opengl.util.texture.Texture;

import fnt.Font;
import func.Math2D;
import func.PrintString;
import func.StringExt;

public class GLText {
	private static Font curFont;
	private static byte F_NONE = -1, F_SHAKE = 0, F_SPIN = 1, F_GHOST = 2;
	private static byte fontEffect = F_NONE;
	private static float spin;
	
	
	public static void initialize() {
		
		spin = 0;
		curFont = new Font("OoT");
	}
	
	public static float drawChar(float x, float y, float xS, float yS, char c) {
		TextureExt cTexExt = curFont.getChar(c);
		
		if(cTexExt == null) {
			System.out.println("Failed to draw character "+c+".");
			return 0;
		}
		
		Texture cTex = cTexExt.getFrame(0);
		if(cTex == null)
			return 0;
		
		if(c != '"' && c != '\'' && c != '`') {
			y -= cTex.getHeight()*yS;
			y += curFont.getHeight()*yS;
		}
		
		if(c == 'j' || c == 'y' || c == 'g' || c == 'p' || c == 'q')
			y += curFont.getHeight()*yS*.25;
			
		if(fontEffect == F_SHAKE) {
			x += Math2D.rnd(-1,1);
			y += Math2D.rnd(-1,1);
		}
		else if(fontEffect == F_SPIN) {
			float l = 2;
			x += Math2D.calcLenX(l, spin);
			y += Math2D.calcLenY(l, spin);
			spin += 60;
		}
		else if(fontEffect == F_GHOST) {
			GOGL.enableShaderDiffuse(2);
		}
		
		GOGL.drawTextureScaled(x,y,xS,yS,cTex);
		GOGL.disableShaders();
		
		return cTex.getWidth()*xS;
	}
	
	public static void drawString(float x, float y, float xS, float yS, String str) {
		int len = str.length();
		char c;
		
		
		float fW = curFont.getWidth(), fH = curFont.getHeight();
		float dX = x, dY = y;
		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);

			if(c == ' ')
				dX += fW*xS;
			else if(c == '\n') {
				dX = x;
				dY += fH*yS;
			}
			else
				dX += drawChar(dX,dY,xS,yS,c);
		}
	}
	public static void drawString(float x, float y, float xS, float yS, PrintString str) {
		int len = str.length();
		char c;
		
		spin = GOGL.getTime()*5;
		
		float fW = curFont.getWidth(), fH = curFont.getHeight();
		float dX = x, dY = y, a, aa, uX, uY;
		int n = str.getFallNumber();

		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);

			if(c == ' ')
				dX += fW*xS;
			else if(c == '\n') {
				dX = x;
				dY += fH*yS;
			}
			else {				
				if(i+n >= len) {
					a = str.getLastAlpha(len-(i+1));
					aa = (float) Math.sqrt(a);
					uX = dX + (1-aa)*3;
					uY = dY - (1-aa)*10;
				}
				else {
					a = 1;
					uX = dX;
					uY = dY;
				}
				
				GOGL.setColor(0,0,0,a);
				dX += drawChar(uX,uY,xS,yS,c);
			}
		}
	}
	
	public static void drawStringCentered(float x, float y, float xS, float yS, String str) {
		y -= getStringHeight(xS,yS,str)/2;
		
		int len = str.length();
		char c;
		
		float fH = curFont.getHeight();
			
		StringExt all = new StringExt(str);
		String line = all.chompLine();
		
		while(line != "") {
			drawString(x-getStringWidth(xS,yS,line)/2,y,xS,yS,line);
		
			y += fH*yS;
			
			line = all.chompLine();
		}
	}

	public static float getStringWidth(float xS, float yS, String str) {
		int len = str.length();
		char c;
		
		float fW = curFont.getWidth(), fH = curFont.getHeight();
		float dX = 0, dY = 0, maxW = 0;
		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);

			if(c == ' ')
				dX += fW*xS;
			else if(c == '\n') {
				if(maxW < dX)
					maxW = dX;
				
				dX = 0;
				dY += fH*yS;
			}
			else
				dX += curFont.getChar(c).getWidth()*xS;
		}
		
		if(maxW < dX)
			maxW = dX;
		
		return maxW;
	}
	
	public static float getStringHeight(float xS, float yS, String str) {
		int len = str.length();
		char c;
		
		float fH = curFont.getHeight();
		float maxH = fH*yS;
		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);

			if(c == '\n')
				maxH += fH*yS;
		}
		
		return maxH;
	}
}
