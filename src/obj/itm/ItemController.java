package obj.itm;

import gfx.TextureExt;

import java.util.HashMap;
import java.util.Map;

import cont.TextureController;

public final class ItemController {
	private static Map<String, ItemBlueprint> itemMap = new HashMap<String, ItemBlueprint>();
	
	public static final byte IT_ACTION = 0, IT_HEALING = 1;

	//ITEMS
	
		private ItemController() {
		}
		
		public static void ini() {
			addItem("Coin", IT_ACTION, "Resources/Images/Items/coin.png", "A single coin.",1,Integer.MAX_VALUE);
			addItem("Bread", IT_HEALING, "Resources/Images/Items/bread.png", "Normal loaf of bread.",24);
			addItem("Empty Bucket", IT_ACTION, "Resources/Images/Items/emptyBucket.png", "Just an empty bucket.",30);
			addItem("Water Bucket", IT_ACTION, "Resources/Images/Items/waterBucket.png", "Just a bucket with water.",30);
			addItem("Empty Bowl", IT_ACTION, "Resources/Images/Items/emptyBowl.png", "Just an empty bowl.",30);
			addItem("Logs", IT_ACTION, "Resources/Images/Items/logs.png", "Normal logs.",179);
			addItem("Rune Bar", IT_ACTION, "Resources/Images/Items/barRune.png", "A rune bar.",14000,64);
		}
		
		
		private static void addItem(String name, byte type, String sprFilename, String info, int value) {
			addItem(name, type, TextureController.load(sprFilename, name, TextureController.M_BGALPHA), info, value, 1);
		}
		private static void addItem(String name, byte type, String sprFilename, String info, int value, int maxStackSize) {
			addItem(name, type, TextureController.load(sprFilename, name, TextureController.M_BGALPHA), info, value, maxStackSize);
		}
		private static void addItem(String name, byte type, TextureExt sprite, String info, int value, int stackMax) {
			ItemBlueprint blueprint = new ItemBlueprint(name, type, sprite, info, value, stackMax);
			itemMap.put(name, blueprint);
		}
	
		public static ItemBlueprint get(String name) {
			return itemMap.get(name);
		}
}
