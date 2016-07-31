package script;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cont.Log;
import ds.nameid.ID;
import ds.nameid.NameIDMap;
import gfx.GL;

public class PMLMemory {
	Stack<Level> levelStack = new Stack<Level>();
	Stack<Level> prevStack = new Stack<Level>();
	private NameIDMap<Action> actionMap = new NameIDMap<Action>();
	
	private class Level {
		private Register[] regList;
		
		public Level(int regNum, ConstantRegister[] constList) {
			regList = new Register[regNum];
			
			for(int i = 0; i < regNum; i++)
				regList[i] = new Register();
			if(constList != null)
				for(ConstantRegister cr : constList) {
					Log.println(Log.ID.PML, "Adding constant: " + cr);
					regList[cr.idNum].set(cr);
				}
		}

		public Register access(int index) {
			return regList[index];
		}
				
		public void destroy() {
			for(Register r : regList)
				r.destroy();			
		}
	}


	public void pushLevel(int regNum, ConstantRegister[] constList) {
		Log.println(Log.ID.PML, true, "PMLMemory.pushLevel(" + regNum + ")");

		if(levelStack.size() > 0)
			prevStack.push(levelStack.peek());

		levelStack.push(new Level(regNum, constList));
		
		Log.println(Log.ID.PML, false, "");
	}

	public void popLevel() {		
		levelStack.pop().destroy();

		if(levelStack.size() > 0)
			prevStack.pop();
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public ID addAction(Action a) {
		return actionMap.add(a.getName(), a);
	}
	public Action getAction(String str) {
		Log.println(Log.ID.PML, true, "PMLMemory.getAction("+str+")");
		
		if(actionMap.contains(str)) {
			Action a = actionMap.get(str);
			
			Log.println(Log.ID.PML, false, "");
			return a;
		}
		else
			throw new NullPointerException("Action \"" + str + "\" does not exist in memory.");
	}

	public Register access(int i) {
		return levelStack.peek().access(i);
	}

	public Register accessPrevious(int i) {
		return prevStack.peek().access(i);
	}
}
