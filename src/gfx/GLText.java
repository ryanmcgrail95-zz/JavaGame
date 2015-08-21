package gfx;

import resource.font.Font;
import resource.font.MergedFont;
import resource.font.SplitFont;

import com.jogamp.opengl.util.texture.Texture;

import datatypes.PrintString;
import datatypes.StringExt;
import functions.Math2D;
import functions.MathExt;

public class GLText {
	private static Font curFont;
	private static byte F_NONE = -1, F_SHAKE = 0, F_SPIN = 1, F_GHOST = 2;
	private static byte fontEffect = F_NONE;
	private static float spin;
	

	private static float curLineHeight, curWidth, curHeight, curSWidth, curSHeight, curHangFrac;
	private static Texture curTex;
	private static boolean isSplit;
	
	public static void initialize() {
		spin = 0;
		
		new MergedFont("8bit", .1f);
		curFont = new SplitFont("OoT", .25f);
	}
	
	
	public static void setFont(String name) {
		curFont = Font.get(name);
		curWidth = curFont.getWidth();
		curHeight = curFont.getHeight();
		curTex = curFont.getTexture();
		isSplit = curFont.isSplit();
		curLineHeight = curFont.getHeight();
		curHangFrac = curFont.getHangFrac();
	}
	public static void setScale(float xS, float yS) {
		if(!isSplit) {
			curSWidth = xS*curWidth;
			curSHeight = yS*curHeight;
		}
	}

	public static float drawChar(float x, float y, float xS, float yS, char c) {					
		Texture cTex = null;
		
		if(isSplit) {
			cTex = curFont.getChar(c).getFrame(0);
			curWidth = cTex.getWidth();
			curHeight = cTex.getHeight();
			curSWidth = xS*curWidth;
			curSHeight = yS*curHeight;
		}

		
		if(c != '"' && c != '\'' && c != '`') {
			y -= curSHeight;
			y += curLineHeight;
		}
		
		if(c == 'j' || c == 'y' || c == 'g' || c == 'p' || c == 'q')
			y += curLineHeight*yS*curHangFrac; //.25
			
		if(fontEffect == F_SHAKE) {
			x += MathExt.rnd(-1,1);
			y += MathExt.rnd(-1,1);
		}
		else if(fontEffect == F_SPIN) {
			float l = 2;
			x += Math2D.calcLenX(l, spin);
			y += Math2D.calcLenY(l, spin);
			spin += 60;
		}
		else if(fontEffect == F_GHOST)
			GOGL.enableShaderDiffuse(2);
		
		if(isSplit)
			GOGL.drawTextureScaled(x,y,xS,yS,cTex);
		else
			GOGL.fillRectangle(x,y,curSWidth,curSHeight,curFont.getBounds(c));
		
		GOGL.disableShaders();
		return curSWidth;
	}
	
	public static void drawString(float x, float y, String str) {
		drawString(x,y,1,1,str);
	}
	public static void drawString(float x, float y, float xS, float yS, String str) {
		int len = str.length();
		char c;
		
		if(!isSplit) {
			setScale(xS,yS);
			GOGL.bind(curTex);
		}
		
		float fH = curFont.getHeight();		
		float dX = x, dY = y;
		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);

			if(c == ' ')
				dX += curFont.getWidth()*xS;
			else if(c == '\n') {
				dX = x;
				dY += fH*yS;				
			}
			else
				dX += drawChar(dX,dY,xS,yS,c);
		}
		
		GOGL.bind(0);
	}
	public static void drawString(float x, float y, String str, boolean shadow) {
		drawString(x,y,1,1,str,shadow);
	}
	public static void drawString(float x, float y, float xS, float yS, String str, boolean shadow) {
		// INEFFICIENT, MULTIPLE GETHEIGHT/GETWIDTH
		
		if(shadow) {
			RGBA oriColor = GOGL.getColor();
			GOGL.setColor(0,0,0,oriColor.getA());
			drawString(x+1.5f,y+1.5f,xS,yS,str);
			GOGL.setColor(oriColor);
		}
		drawString(x,y,xS,yS,str);
	}
	public static void drawString(float x, float y, float xS, float yS, float lineSpace, PrintString str) {
		int len = str.length();
		char c;
		
		if(!isSplit) {
			setScale(xS,yS);
			GOGL.bind(curTex);
		}
		
		spin = GOGL.getTime()*5;
		
		float dX = x, dY = y, a, aa, uX, uY;
		int n = str.getFallNumber();

		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);

			if(c == ' ')
				dX += curWidth*xS;
			else if(c == '\n') {
				dX = x;
				dY += (curLineHeight+lineSpace)*yS;
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
		
		GOGL.bind(0);
	}
	
	public static void drawStringCentered(float x, float y, float xS, float yS, String str, boolean shadow) {
		// INEFFICIENT, MULTIPLE GETHEIGHT/GETWIDTH
		
		if(shadow) {
			RGBA oriColor = GOGL.getColor();
			GOGL.setColor(0,0,0,oriColor.getA());
			drawStringCentered(x+1.5f,y+1.5f,xS,yS,str);
			GOGL.setColor(oriColor);
		}
		drawStringCentered(x,y,xS,yS,str);
	}
	public static void drawStringCentered(float x, float y, float xS, float yS, String str) {
		y -= getStringHeight(xS,yS,str)/2;
		
		int len = str.length();
		char c;
					
		StringExt all = new StringExt(str);
		String line = all.chompLine();
		
		while(line != "") {
			drawString(x-getStringWidth(xS,yS,line)/2,y,xS,yS,line);
		
			y += curLineHeight*yS;
			
			line = all.chompLine();
		}
	}

	public static float getStringWidth(String str) {return getStringWidth(1,1,str);}
	public static float getStringWidth(float scale, String str) {return getStringWidth(scale,scale,str);}
	public static float getStringWidth(float xS, float yS, String str) {
		int len = str.length();
		char c;
		
		float dX = 0, dY = 0, maxW = 0;
		
		for(int i = 0; i < len; i++) {
			c = str.charAt(i);

			if(c == ' ')
				dX += curWidth*xS;
			else if(c == '\n') {
				if(maxW < dX)
					maxW = dX;
				
				dX = 0;
				dY += curLineHeight*yS;
			}
			else
				dX += curFont.getCharWidth(c)*xS;
		}
		
		if(maxW < dX)
			maxW = dX;
		
		return maxW;
	}
	
	public static float getStringHeight(String str) {return getStringHeight(1,1,str);}
	public static float getStringHeight(float xS, float yS, String str) {
		int len = str.length();
		char c;
		
		float fH = curFont.getHeight();
		float maxH = fH*yS;
		
		for(int i = 0; i < len; i++) {
			if(str.charAt(i) == '\n')
				maxH += fH*yS;
		}
		
		return maxH;
	}
}
