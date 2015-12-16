package rm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import cont.GameController;
import object.actor.Actor;
import object.environment.Floor;
import object.primitive.Positionable;
import object.primitive.Updatable;
import paper.ActorPM;
import paper.CharacterPM;
import paper.PlayerPM;
import resource.Resource;
import resource.model.Model;
import datatypes.StringExt;
import de.jarnbjo.oggtools.Player;
import fl.FileExt;
import btl.BattleController;

public class Room {
	private final static String BASE_DIRECTORY = "Resources/Rooms/";
	private static String curRoom = "Toad Town Center", prevRoom;
	private static List<Resource> resourceList = new ArrayList<Resource>();


	private Room() {}
	
	
	public static void revertRoom() {
		changeRoom(prevRoom);
	}
	public static void changeRoom(String newRoomName) {
		prevRoom = curRoom;
		curRoom = newRoomName;
		
				
		List<Resource> newResourceList = new ArrayList<Resource>(),
			unloadResourceList = new ArrayList<Resource>(),
			loadResourceList = new ArrayList<Resource>();
		
		// Get New Resource List
		getResourceList(newRoomName, newResourceList);
		
		
		// Determine Unload List
		for(Resource r : resourceList)
			if(!newResourceList.contains(r))
				unloadResourceList.add(r);
		
		// Determine Load List
		for(Resource r : newResourceList)
			if(!resourceList.contains(r))
				loadResourceList.add(r);
		
		// Unload All Unnecessary Resources
		/*System.out.println("Unloading");
		for(Resource r : unloadResourceList) {
			System.out.println("\t" + r.getName());
			r.unload();
		}*/
		
		// Load New Resources
		System.out.println("Loading");
		for(Resource r : loadResourceList) {
			System.out.println("\t" + r.getName());
			r.load();
		}
		
		
		// Clean Up Lists
		resourceList.clear();
		unloadResourceList.clear();
		loadResourceList.clear();
		resourceList = newResourceList;
		
		// Delete all Objects in Room
		//Updatable.transition();
		
		// Set New Room
		instantiateRoom(newRoomName);
	}
	
	
	
	public static void getResourceList(String roomName, List<Resource> resources) {
		String path = BASE_DIRECTORY + roomName + "/resources.dat";
		
		StringExt fileStr = new StringExt(FileExt.readFile2String(path));
		StringExt curLine = new StringExt();
		
		String[] words;
		int wordNum;
		
		Model curModel;
		List<Model> curModels = new ArrayList<Model>();
		
		float scale;
		float rX, rY, rZ;

		while(!fileStr.isEmpty()) {
			curLine.set(fileStr.chompLine());
			
			if(curLine.startsWith("//") || curLine.isWhiteSpace() || curLine.isEmpty())		
				continue;
			
			curLine.chompWhiteSpace();
			words = curLine.split(' ');
			wordNum = words.length;
			
			switch(words[0]) {
				case "Model:":
					curModels.clear();
					for(int i = 1; i < wordNum; i++) {
						curModel = new Model(words[i]);
						resources.add(curModel);
						curModels.add(curModel);
					}
					break;
				case "Character:":
					for(int i = 1; i < wordNum; i++)
						resources.add(new CharacterPM(words[i]));
					break;
					
					
				// VARIABLES
				case "scale:":
					scale = Float.parseFloat(words[1]);
					for(Model m : curModels)
						m.scale(scale);
					break;
				case "rotation:":
					rX = Float.parseFloat(words[1]);
					rY = Float.parseFloat(words[2]);
					rZ = Float.parseFloat(words[3]);
						
					for(Model m : curModels) {
						m.rotateX(rX);
						m.rotateY(rY);
						m.rotateZ(rZ);
					}

					break;
								
				default:
			}
		}
		
		curModels.clear();
	}
	
	public static void instantiateRoom(String roomName) {
		String path = BASE_DIRECTORY + roomName + "/layout.dat";
		
		StringExt fileStr = new StringExt(FileExt.readFile2String(path));
		StringExt curLine = new StringExt();
				
		String[] words;
		int wordNum;
		
		Map<String, Double> varMap = new HashMap<String, Double>();
		
		Stack<Updatable> objectStack = new Stack<Updatable>();
			Updatable curObj;
			ActorPM topActor = null;
			Positionable topPos = null;
			
		float x1, y1, z1, x2, y2, z2;
		
		while(!fileStr.isEmpty()) {
			curLine.set(fileStr.chompLine());
						
			if(curLine.startsWith("//")) {
				System.out.println("COMMENT");
				continue;
			}
			else if(curLine.isWhiteSpace()) {
				System.out.println("WHITE SPACE");
				continue;
			} 
			else if(curLine.isEmpty()) {
				System.out.println("EMPTY");
				continue;
			}
			
			curLine.chompWhiteSpace();
			words = curLine.split(' ');
			wordNum = words.length;
			
			switch(words[0]) {
				case "Player":
					curObj = PlayerPM.create();

					if(wordNum > 1)
						if(words[1].equals("{")) {
							objectStack.push(curObj);
							if(curObj instanceof ActorPM)
								topActor = (ActorPM) curObj;
							if(curObj instanceof Positionable)
								topPos = (Positionable) curObj;
						}
					break;
				
				case "Floor":
					x1 = parseValue(words[2], varMap);
					y1 = parseValue(words[3], varMap);
					x2 = parseValue(words[4], varMap);
					y2 = parseValue(words[5], varMap);
					z1 = parseValue(words[6], varMap);
										
					curObj = new Floor(x1,y1,x2,y2,z1, null);
					break;

				case "BattleController":
					new BattleController();
					break;
					
				case "}":
					objectStack.pop();
					if(!objectStack.isEmpty())
						curObj = objectStack.peek();
					else
						curObj = null;
					if(curObj != null) {
						if(curObj instanceof ActorPM)
							topActor = (ActorPM) curObj;
						if(curObj instanceof Positionable)
							topPos = (Positionable) curObj;
					}
					break;
					
				
				// VARIABLES
				case "character:":
					topActor.setCharacter(words[1]);
					break;
				case "position:":
					topPos.setPos(
						parseValue(words[1], varMap),
						parseValue(words[2], varMap),
						parseValue(words[3], varMap));
					break;
					
				default:
			}
		}
	}

	private static float parseValue(String str, Map<String, Double> varMap) {
		return Float.parseFloat(str);
	}
}
