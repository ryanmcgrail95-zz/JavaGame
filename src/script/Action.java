package script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cont.Log;
import ds.nameid.ID;
import ds.nameid.NameIDMap;
import gfx.GL;

public abstract class Action extends PML {
	protected String 					name;
	protected int						regNum;
	protected int[] 					argIndList;
	protected ConstantRegister[] 		constList;	
	protected boolean 					hasOutput;

	//private List<Integer>
	
	// Temp Action
	public Action(PMLMemory mem, String name, int regNum, int[] argIndList, ConstantRegister[] constList, boolean hasOutput) {
		Log.println(Log.ID.PML, true, "Action()");
		this.name = name;
		
		this.regNum = regNum;
		this.argIndList = argIndList;
		this.constList = constList;
		
		this.hasOutput = hasOutput;
		
		mem.addAction(this);		
		
		Log.println(Log.ID.PML, false, "");
	}
			
	public Register run(Register output, Register[] parameters, PMLMemory stack) {
		boolean created = false;
		
		Log.println(Log.ID.PML, true, "Action["+name+"].run()");

		if(regNum > 0)
			stack.pushLevel(regNum, constList);
		
		call(output, parameters, stack);

		if(regNum > 0)
			stack.popLevel();

		Log.println(Log.ID.PML, false, "");

		return output;
	}
	public abstract void call(Register output, Register[] parameters, PMLMemory stack);
	
	public boolean checkParameters(Register... parameters) {
		return true;
	}
	
	public void destroy() {
		//TODO: add destroy
	}
	
	public void setName(String name) 				{this.name = name;}
	public void setHasOutput(boolean output) 		{this.hasOutput = output;}
	
	
	public static void ini(PMLMemory mem) 	{MiscAction.ini(mem);}
	public String getName() 	{return name;}
	public boolean hasOutput() 	{return hasOutput;}
}
