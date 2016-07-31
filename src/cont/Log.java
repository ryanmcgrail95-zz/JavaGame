package cont;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import ds.StringExt;
import gfx.GL;

public class Log {
	public static enum ID{
		ERROR,
		PML,
		RESOURCE,
		IO};
	private static Map<ID, Boolean> activeIDMap = new HashMap<ID, Boolean>();
	static {
		activeIDMap.put(ID.ERROR, true);
		activeIDMap.put(ID.PML, false);
		activeIDMap.put(ID.RESOURCE, true);
		activeIDMap.put(ID.IO, true);
	}
	
	private static boolean 
		doLog = true;
	
    private static int numSpaces = 0;
    private static Stack<Object> alerts = new Stack<Object>();

	
	public static void enableLogging (boolean doLog) {
		Log.doLog = doLog;
	}
	
	public static void println(ID id) 				{println(id, "");}
	public static void println(ID id, Object str) 	{print(id, str + "\n");}
	public static void print(ID id, Object str) 	{
		if(!doLog)
			return;
    	if(!checkActiveID(id))
    		return;

    	String s = StringExt.repeat("   ", numSpaces);
    	System.out.print(s + str);
	}
	
	private static boolean checkActiveID(ID id) {
		return activeIDMap.get(id);
	}
	
    public static void println(ID id, boolean isRight, Object str) {    	
		if(!doLog)
			return;
    	if(!checkActiveID(id))
    		return;


		if(!isRight)
    		numSpaces--;
    	
		String s = StringExt.repeat("   ", numSpaces);

    	if(isRight)
    		numSpaces++;

    	if(!isRight)
    		System.out.println(s + "<<" + alerts.pop() + ", " + str);
    	else {
    		alerts.push(str);
    		System.out.println(s + ">>" + str);
    	}
    }
}
