package script;

import java.util.HashMap;
import java.util.Map;

public abstract class Action extends PML {
	private final static Map<String, Action> actionMap = new HashMap<String, Action>();
	private Variable[] parameterList;
	private String name;
	private boolean hasOutput;

	// Temp Action
	public Action() {
	}
	public Action(String name, Variable[] parameterList, boolean hasOutput) {
		this.parameterList = parameterList;
		
		this.hasOutput = hasOutput;
		
		// Ensure Action Does NOT Already Exist!!
		if(actionMap.containsKey(name))
			throw new UnsupportedOperationException("Action " + name + " already exists.");
		else
			actionMap.put(this.name = name, this);
	}
			
	public abstract Variable run(Variable output, Variable[] parameters);
	
	public boolean checkParameters(Variable... parameters) {
		return true;
		//if()
	}
	
	public static Action getAction(String name) {
		Action a = actionMap.get(name);
		if(a == null)
			throw new UnsupportedOperationException("No action exists with the name, \"" + name + "\".");
		return a;
	}
	
	public void destroy() {
		actionMap.remove(name);
		
		if(parameterList != null) {
			for(Variable v : parameterList)
				v.destroy();
			
			parameterList = null;
		}
	}
	
	public void setName(String name) 				{this.name = name;}
	public void setParameterList(Variable[] param) 	{this.parameterList = param;}
	public void setHasOutput(boolean output) 		{this.hasOutput = output;}
	
	
	public static void ini() 	{MiscAction.ini();}
	public String getName() 	{return name;}
	public boolean hasOutput() 	{return hasOutput;}
}
