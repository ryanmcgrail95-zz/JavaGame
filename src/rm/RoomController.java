package rm;

import java.util.HashMap;
import java.util.Map;

public class RoomController {
	private static Map<String, Room> roomMap = new HashMap<String, Room>();
	private static boolean inBattle = false;
	private static Room curRoom;
	
	
	public static boolean isInBattle() {
		return inBattle;
	}

	public static void switchRoom(String trRoom) {
		curRoom = roomMap.get(trRoom);
	}
}
