package gfx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cont.TextureController;

public class CubeMap {
	private TextureExt left, right, top, bottom, back, front;
	
	
	private static Map<String, CubeMap> cmMap = new HashMap<String, CubeMap>();
	//private final static byte C_GB1 = 0;
	
	
	public CubeMap(String name, TextureExt left, TextureExt right, TextureExt back, TextureExt front, TextureExt top, TextureExt bottom) {
		this.left = left;
		this.right = right;
		this.back = back;
		this.front = front;
		this.top = top;
		this.bottom = bottom;
		
		cmMap.put(name, this);
	}
	
	
	
	
	public static void ini() {
		TextureExt gb1, gb1s;
		gb1 = TextureController.loadTexture("Resources/Images/Blocks/gb1E.bmp", "texGroundBlock1Front", TextureController.M_NORMAL);
        gb1s =  TextureController.loadTexture("Resources/Images/Blocks/gb1S.bmp", "texGroundBlock1Side", TextureController.M_NORMAL);
        
		
		new CubeMap("cmGroundBlock1", gb1s, gb1s, gb1s, gb1, gb1s, gb1s);
			new CubeMap("cmGroundBlocklet1", gb1s, gb1s, gb1s, gb1s, gb1s, gb1s);
	}
	
	
	
	public static CubeMap getMap(String name) {
		return cmMap.get(name);
	}
	
	public void setLeft(TextureExt tex) {
		left = tex;
	}
	public void setRight(TextureExt tex) {
		right = tex;
	}
	public void setTop(TextureExt tex) {
		top = tex;
	}
	public void setBottom(TextureExt tex) {
		bottom = tex;
	}
	public void setBack(TextureExt tex) {
		back = tex;
	}
	public void setFront(TextureExt tex) {
		front = tex;
	}
	
	public TextureExt getLeft() {
		return left;
	}
	public TextureExt getRight() {
		return right;
	}
	public TextureExt getTop() {
		return top;
	}
	public TextureExt getBottom() {
		return bottom;
	}
	public TextureExt getBack() {
		return back;
	}
	public TextureExt getFront() {
		return front;
	}
}
