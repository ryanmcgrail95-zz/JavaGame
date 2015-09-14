package path;

import java.util.Map;

import datatypes.lists.CleanList;

public class Node {
	private CleanList<Node> neighbors;
	private Map<Node,Float> neighborDists;
	
	private void destroy() {
		neighbors.clear();
		neighborDists().
	}
}