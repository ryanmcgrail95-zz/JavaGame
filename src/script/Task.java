package script;

import java.awt.Component;

import cont.Log;
import gfx.GL;

public class Task {
	private String actionName;
	private int[] parameterVarListIndices;
	private int outputVarIndex;

	public Task(String actionName, int[] parameterVarListIndices, int outputVarIndex) {
		this.actionName = actionName;
		this.parameterVarListIndices = parameterVarListIndices;
		this.outputVarIndex = outputVarIndex;
	}
	public Task(TaskBlueprint bp) {
		this.actionName = bp.getActionName();
		this.parameterVarListIndices = bp.getParameterIndices();
		this.outputVarIndex = bp.getOutputIndex();
	}
	
	public String getActionName() 			{return actionName;}
	public Action getAction(PMLMemory mem) 	{return mem.getAction(getActionName());}
	
	public void destroy() {
		/*if(outputVar != null)
			outputVar.destroy();

		for(Register v : parameters)
			v.destroy();
		
		action = null;
		parameters = null;
		outputVar = null;*/
	}
	
	public Register run(PMLMemory mem) {
		Log.println(Log.ID.PML, true, "Task.run()");
		int parNum = parameterVarListIndices.length;
		
		Register outputVar;
		Register[] parameters = new Register[parNum];
		
		if(outputVarIndex != -1)
			outputVar = mem.access(outputVarIndex);
		else
			outputVar = null;
		
		for(int i = 0; i < parNum; i++) {
			Register r = mem.access(parameterVarListIndices[i]);
			
			Log.println(Log.ID.PML, r);
			
			parameters[i] = r;
		}
			
		Register o = getAction(mem).run(outputVar, parameters, mem);
		
		Log.println(Log.ID.PML, false, "");
		
		return o;
	}
	
	public int getParameterIndex(int i) {
		return parameterVarListIndices[i];
	}
}
