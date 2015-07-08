package cont;

import gfx.TextureExt;

import java.util.HashMap;
import java.util.Map;

public final class ItemController {
	private static Map<String, String> itemInfoMap = new HashMap<String, String>();
	private static Map<String, TextureExt> itemSpriteMap = new HashMap<String, TextureExt>();
	private static Map<String, Float> itemValueMap = new HashMap<String, Float>();
	private static Map<String, Byte> itemTypeMap = new HashMap<String, Byte>();
	private static Map<String, Integer> itemStackMap = new HashMap<String, Integer>();
	
	public static final byte IT_ACTION = 0, IT_HEALING = 1;

	//ITEMS
	
		private ItemController() {
		}
		
		public static void ini() {
			addItem("Coin", IT_ACTION, "Resources/Images/Items/coin.png", "A single coin.",1,Integer.MAX_VALUE);
			addItem("Bread", IT_HEALING, "Resources/Images/Items/bread.png", "Normal loaf of bread.",30);
			addItem("Empty Bucket", IT_ACTION, "Resources/Images/Items/emptyBucket.png", "Just an empty bucket.",30);
			addItem("Water Bucket", IT_ACTION, "Resources/Images/Items/waterBucket.png", "Just a bucket with water.",30);
			addItem("Empty Bowl", IT_ACTION, "Resources/Images/Items/emptyBowl.png", "Just an empty bowl.",30);
			addItem("Logs", IT_ACTION, "Resources/Images/Items/logs.png", "Normal logs.",20,64);
			addItem("Rune Bar", IT_ACTION, "Resources/Images/Items/barRune.png", "A rune bar.",14000,64);
		}
		
		
		private static void addItem(String name, byte type, String sprFilename, String info, float value) {
			addItem(name, type, TextureController.loadTexture(sprFilename, name, TextureController.M_BGALPHA), info, value, 1);
		}
		private static void addItem(String name, byte type, String sprFilename, String info, float value, int maxStackSize) {
			addItem(name, type, TextureController.loadTexture(sprFilename, name, TextureController.M_BGALPHA), info, value, maxStackSize);
		}
		private static void addItem(String name, byte type, TextureExt sprite, String info, float value, int maxStackSize) {
			itemSpriteMap.put(name, sprite);
			itemInfoMap.put(name,  info);
			itemValueMap.put(name, value);
			itemTypeMap.put(name, type);
			itemStackMap.put(name, maxStackSize);
		}
	
		public static TextureExt getSprite(String name) {
			return itemSpriteMap.get(name);
		}
		public static String getInfo(String name) {
			return itemInfoMap.get(name);
		}
		public static float getValue(String name) {
			return itemValueMap.get(name);
		}
		public static byte getType(String name) {
			return itemTypeMap.get(name);
		}

		public static int getStackMax(String name) {
			return itemStackMap.get(name);
		}
}
