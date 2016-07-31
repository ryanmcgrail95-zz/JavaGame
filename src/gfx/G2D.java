package gfx;

import functions.ArrayMath;
import functions.Math2D;
import functions.MathExt;
import resource.font.Font;
import resource.font.MergedFont;
import resource.font.SplitFont;

import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import ds.PrintString;
import ds.StringExt;

public class G2D extends GL {
	private static boolean stringShadow;
	private static final float[]
		BOUNDS_STD = {0,0, 1,1};
	
	//public static void enableStringShadow(boolean stringShadow) {
	//	G2D.stringShadow = stringShadow;
	//}
	
	public static void setLineWidth(float w) {gl.glLineWidth(w);}
	public static void drawLine(float x1, float y1, float x2, float y2) {drawLine(x1,y1,x2,y2,1);};
	public static void drawLine(float x1, float y1, float x2, float y2, float w) {
		setLineWidth(w);
		gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(x1-1,y1-1, orthoLayer);
			gl.glVertex3f(x2,y2, orthoLayer);
	    gl.glEnd();
	    setLineWidth(1);
	}
	
	public static void drawLine(List<float[]> pts) {
		gl.glBegin(GL2.GL_LINES);
			int i = 0, si = pts.size();
			for(float[] pt : pts) {
				gl.glVertex3f(pt[0],pt[1], orthoLayer);
				if(i > 0 && i < si-1)
					gl.glVertex3f(pt[0],pt[1], orthoLayer);
				i++;
			}
	    gl.glEnd();
	}
	
	
	public static void drawRectangle(float x, float y, float w, float h) {rectangle(x,y,w,h,false);}
	public static void drawRectangle(float x, float y, float w, float h, float[] bounds) {rectangle(x,y,w,h,bounds,false);}
	public static void fillRectangle() {rectangle(0,0,SCREEN_WIDTH,SCREEN_HEIGHT,true);}
	public static void fillRectangle(float x, float y, float w, float h) {rectangle(x,y,w,h,true);}
	public static void fillRectangle(float x, float y, float w, float h, float[] bounds) {rectangle(x,y,w,h,bounds,true);}
	public static void rectangle(float x, float y, float w, float h, boolean fill) {rectangle(x,y,w,h,BOUNDS_STD,fill);}
	public static void rectangle(float x, float y, float w, float h, float[] bounds, boolean fill) {
		gl.glBegin((fill ? GL2.GL_QUADS : GL2.GL_LINE_LOOP));
			gl.glTexCoord2d(bounds[0], bounds[1]);	gl.glVertex3f(x, y, orthoLayer);
			gl.glTexCoord2d(bounds[2], bounds[1]);	gl.glVertex3f(x+w, y, orthoLayer);
			gl.glTexCoord2d(bounds[2], bounds[3]);	gl.glVertex3f(x+w, y+h, orthoLayer);
			gl.glTexCoord2d(bounds[0], bounds[3]);	gl.glVertex3f(x,y+h, orthoLayer);		
			if(!fill) {
				gl.glTexCoord2d(bounds[0], bounds[1]);	gl.glVertex3f(x, y, orthoLayer);
			}
        gl.glEnd();
	}
	
	
	public static void drawTexture(float x, float y, Texture tex) 									{drawTexture(x,y,tex.getWidth(),tex.getHeight(),tex);}
	public static void drawTexture(float x, float y, float w, float h, Texture tex) 				{drawTexture(x,y,w,h,tex,BOUNDS_STD);}
	public static void drawTexture(float x, float y, float w, float h, Texture tex, float[] bounds) {
		if(tex == null)
			return;
		
		enableTextures();
		enableBlending();
		bind(tex);
		
		if(tex.getMustFlipVertically())	fillRectangle(x,y+h,w,-h, bounds);
		else							fillRectangle(x,y,w,h, bounds);
      	
      	bind(0);
      	disableTextures();
	}

	public static void drawTexture(float x, float y, float w, float h, MultiTexture tex, int frame) {drawTexture(x,y,w,h, tex.getTexture(), tex.getBounds(frame));}
	
	public static void drawTextureScaled(float x, float y, float xS, float yS, Texture tex) 		{drawTexture(x,y,tex.getWidth()*xS,tex.getHeight()*yS,tex);}

	private static Font curFont;
	private static byte F_NONE = -1, F_SHAKE = 0, F_SPIN = 1, F_GHOST = 2;
	private static byte fontEffect = F_NONE;
	private static float spin;
	

	private static float curLineHeight, curWidth, curHeight, curSWidth, curSHeight, curHangFrac;
	//private static Texture curTex;
	private static boolean isSplit;
	
	public static void initialize() {
		spin = 0;
		
		new MergedFont("8bit", .1f);
		new SplitFont("OoT", .25f);
		
		setFont("8bit");
	}
	
	
	public static void setFont(String name) {
		// Set Font
		curFont = Font.get(name);
		
		// Set Font Variables
		curWidth = curFont.getWidth();
		curHeight = curFont.getHeight();
		//curTex = curFont.getTexture();
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
			cTex = curFont.getChar(c).getTexture();
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
			x += MathExt.rndf(-1,1);
			y += MathExt.rndf(-1,1);
		}
		else if(fontEffect == F_SPIN) {
			float l = 2;
			x += Math2D.calcLenX(l, spin);
			y += Math2D.calcLenY(l, spin);
			spin += 60;
		}
		else if(fontEffect == F_GHOST)
			enableShaderDiffuse(2);
		
		if(isSplit)
			drawTextureScaled(x,y,xS,yS,cTex);
		else
			fillRectangle(x,y,curSWidth,curSHeight,curFont.getBounds(c));
		
		disableShaders();
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
			bind(curFont.getTexture());
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
		
		bind(0);
	}
	public static void drawString(float x, float y, String str, boolean shadow) {
		drawString(x,y,1,1,str,shadow);
	}
	public static void drawString(float x, float y, float xS, float yS, String str, boolean shadow) {
		// INEFFICIENT, MULTIPLE GETHEIGHT/GETWIDTH
		
		RGBA oriColor = getColor();
		if(shadow) {
			setColori(0,0,0,oriColor.Ai());
			drawString(x+1.5f,y+1.5f,xS,yS,str);
			setColor(oriColor);
		}
		drawString(x,y,xS,yS,str);
	}
	public static void drawString(float x, float y, float xS, float yS, float lineSpace, PrintString str) {
		int len = str.length();
		char c;
		
		if(!isSplit) {
			setScale(xS,yS);
			bind(curFont.getTexture());
		}
		
		spin = getTime()*5;
		
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
				
				setColorf(0,0,0,a);
				dX += drawChar(uX,uY,xS,yS,c);
			}
		}
		
		bind(0);
	}
	
	public static void drawStringCentered(float x, float y, String str, boolean shadow) {drawStringCentered(x,y,1,1,str,shadow);}
	public static void drawStringCentered(float x, float y, float xS, float yS, String str, boolean shadow) {
		// INEFFICIENT, MULTIPLE GETHEIGHT/GETWIDTH
		
		RGBA oriColor = getColor();
		if(shadow) {
			setColorf(0,0,0,oriColor.Af());
			drawStringCentered(x+1.5f,y+1.5f,xS,yS,str);
			setColor(oriColor);
		}
		drawStringCentered(x,y,xS,yS,str);
	}
	public static void drawStringCentered(float x, float y, float xS, float yS, String str) {
		y -= getStringHeight(xS,yS,str)/2;
					
		StringExt all = new StringExt(str);
		String line = all.munchLine();
		
		while(!line.equals("")) {
			drawString(x-getStringWidth(xS,yS,line)/2,y,xS,yS,line);
		
			y += curLineHeight*yS;
			
			line = all.munchLine();
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
				maxW = Math.max(dX, maxW);
				
				dX = 0;
				dY += curLineHeight*yS;
			}
			else
				dX += curFont.getCharWidth(c)*xS;
		}
		
		maxW = Math.max(dX, maxW);
		
		return maxW;
	}
	
	public static float getStringHeight(String str) {return getStringHeight(1,1,str);}
	public static float getStringHeight(float xS, float yS, String str) {
		int len = str.length();
		float fH = curFont.getHeight();
		float maxH = fH*yS;
		
		for(int i = 0; i < len; i++)
			if(str.charAt(i) == '\n')
				maxH += fH*yS;
		
		return maxH;
	}


	public static String getCurrentFontName() {
		return curFont.getName();
	}

	public static void rrectangle(float x, float y, float w, float h, float r) {
		rrectangle(x,y,w,h, r, new float[] {0,0,1,1}, true);
	}
	public static void rrectangle(float x, float y, float w, float h, float r, float[] bounds, boolean fill) {
		rectangle(x, y, w, h, bounds, fill);
		
		boolean doInside = true;
		
		if(w/2 < r) {
			r = w/2;
			doInside = false;
		}
		if(h/2 < r) {
			r = h/2;
			doInside = false;
		}
		
		float u1,v1,u2,v2;
		u1 = bounds[0];
		v1 = bounds[1];
		
		// Draw Top
		
		
		
/*		float 
			bX1 = bounds[0],
			bY1 = bounds[1],
			bX2 = bounds[2],
			bY2 = bounds[3],
			bW = bX2-bX1,
			bH = bY2-bY1;
		
		if(w*h != 0) {
			if(fill) {
				gl.glBegin(GL2.GL_QUADS);
				gl.glTexCoord2d(bX1+bW/2,bY1+bH/2);	gl.glVertex3f(x+w/2, y+h/2, orthoLayer);
			}
			else
				gl.glBegin(GL2.GL_LINE_LOOP);
				
	
				gl.glTexCoord2d(bounds[0], bounds[1]);	gl.glVertex3f(x, y, orthoLayer);
				gl.glTexCoord2d(bounds[2], bounds[1]);	gl.glVertex3f(x+w, y, orthoLayer);
				gl.glTexCoord2d(bounds[2], bounds[3]);	gl.glVertex3f(x+w, y+h, orthoLayer);
				gl.glTexCoord2d(bounds[0], bounds[3]);	gl.glVertex3f(x,y+h, orthoLayer);		
			gl.glEnd();
		}*/
	}
}
