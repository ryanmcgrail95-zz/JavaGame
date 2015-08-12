package obj.itm;

import gfx.TextureExt;

import com.jogamp.opengl.util.texture.Texture;

import datatypes.Inventory;

public class ItemBlueprint {
	private int value;
	private String name, info;
	private TextureExt sprite;
	private byte type;
	private int stackMax;	
	
	
	public ItemBlueprint(String name, byte type, TextureExt sprite, String info, int value, int stackMax) {
		this.name = name;
		this.type = type;
		this.sprite = sprite;
		this.info = info;
		this.value = value;
		this.stackMax = stackMax;
	}

	
	public String getName() {
		return name;
	}
	public TextureExt getSprite() {
		return sprite;
	}
	public String getInfo() {
		return info;
	}
	
	public int getValue() {
		return value;
	}
	public int getStackMax() {
		return stackMax;
	}
	
	public byte getType() {
		return type;
	}
}
