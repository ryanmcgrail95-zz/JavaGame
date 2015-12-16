package datatypes;

import datatypes.lists.CleanList;

public class mat {
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
