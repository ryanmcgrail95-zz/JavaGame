package testbench;

import cfg.CFG;
import cfg.Symbol;

public class TestCFG {

	public static void run() {
		CFG cfg = new CFG();
		
		Symbol root = cfg.getRootSymbol();

		root.addRule(root, "b");
		root.addRule("a", root);
		root.addRule();
		
		assert(cfg.parse("aaabb") == true);
		assert(cfg.parse("aababb") == true);
	}
}
