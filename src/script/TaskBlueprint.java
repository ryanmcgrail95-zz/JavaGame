package script;

import cont.Log;
import ds.nameid.ID;
import gfx.GL;

public class TaskBlueprint {
	private ID<RegisterBlueprint>
		parameterIndices[],
		outInd;
	private String actionName;
	
	public TaskBlueprint(String actionName, ID<RegisterBlueprint>[] parameterIndices, ID<RegisterBlueprint> outInd) {
		this.actionName = actionName;
		this.parameterIndices = parameterIndices;
		this.outInd = outInd;
	}
	
	public void destroy() {
		for(ID<RegisterBlueprint> id : parameterIndices)
			id = null;
		parameterIndices = null;
		
		outInd = null;
	}
	
	public String getActionName() 		{return actionName;}
	public int getOutputIndex() {
		if(outInd == null)
			return -1;
		else
			return outInd.getIDNum();
	}
	public int[] getParameterIndices() 	{
		int len = parameterIndices.length;
		int[] out = new int[len];

		for(int i = 0; i < len; i++) {
			ID<RegisterBlueprint> id = parameterIndices[i];
			Log.println(Log.ID.PML, id);
			out[i] = id.getIDNum();
		}
		
		return out;
	}
}
