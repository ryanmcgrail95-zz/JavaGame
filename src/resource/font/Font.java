package resource.font;

import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.util.texture.Texture;

import gfx.TextureExt;

public abstract class Font {
	protected final static byte T_SPLIT = 0, T_MERGED = 1;
	protected static Map<String, Font> map = new HashMap<String, Font>();
	private byte type;
	private float hangFrac;
	
	protected Font(byte type, float hangFrac) {
		this.type = type;
		this.hangFrac = hangFrac;
	}
	
	public boolean isSplit() {return type == T_SPLIT;}
	public boolean isMerged() {return type == T_MERGED;}
	
	
	public static Font get(String name) {
		return map.get(name);
	}
	
	public float getHangFrac() {
		return hangFrac;
	}
	
	// SplitFont Methods
	public abstract TextureExt getChar(char c);
	public abstract int getCharWidth(char c);
	public abstract int getCharHeight(char c);
	public abstract int getWidth();
	public abstract int getHeight();
	
	// MergedFont Methods
	public abstract float[] getBounds(int frame);
	public abstract Texture getTexture();
}
