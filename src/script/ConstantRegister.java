package script;

import cont.Log;
import ds.nameid.ID;

public class ConstantRegister extends Register {
	int idNum;
	
	public ConstantRegister(ID<RegisterBlueprint> id, RegisterBlueprint regBp) {
		super(regBp);
		
		Log.println(Log.ID.PML, true, "ConstantRegister()");

		idNum = id.getIDNum();		
		Log.println(Log.ID.PML, false, "");
	}
}
