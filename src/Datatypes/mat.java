package datatypes;

import datatypes.lists.CleanList;
import functions.ArrayMath;

public class mat extends ArrayMath {
	private static CleanList<mat> matList = new CleanList<mat>("Mat");
	
	public mat() {
		matList.add(this);
	}
	
	public void destroy() {
		matList.remove(this);
	}

	public static int getNumber() {
		return matList.size();
	}
}
