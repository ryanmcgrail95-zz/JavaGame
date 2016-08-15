package cfg;

import java.util.LinkedList;

import ds.lst.RandomList;
import functions.MathExt;

public class Symbol {
	private RandomList<Rule> ruleList;
	private Symbol parent;

	public class Element {
		private boolean type;
		private String string;
		private Symbol symbol;
		
		public Element(Object obj) {
			if(obj instanceof String) {
				string = (String) obj;
				type = true;
			}
			else if(obj instanceof Symbol) {
				symbol = (Symbol) obj;
				type = false;
			}
		}
		
		public boolean isString() {
			return type;
		}
		public String getString() {
			return string;
		}
		
		public boolean isSymbol() {
			return !type;
		}
		public Symbol getSymbol() {
			return symbol;
		}
	}

	public class Rule {
		private LinkedList<Element> elementList;
		private int minSize;
		private String startString;
		
		public Rule(Object... objects) {
			elementList = new LinkedList<Element>();

			minSize = 0;
			if(objects.length > 0) {
				if(objects[0] instanceof String)
					startString = (String) objects[0];
				
				for(Object obj : objects) {
					elementList.add(new Element(obj));
					
					if(obj instanceof String)
						minSize += ((String) obj).length();
				}
			}
		}
		
		public int getMinSize() {
			return minSize;
		}
		
		public int getElementCount() {
			return elementList.size();
		}
		
		public Element getElement(int i) {
			return elementList.get(i);
		}
		
		public String getStartString() {
			return startString;
		}
	}
	
	public Symbol() {
		ruleList = new RandomList<Rule>("ruleList");
	}
	
	public void addRule(Object... objects) {
		ruleList.add(new Rule(objects));
	}
	
	public Rule getRandomRule() {
		return ruleList.get();
	}
	
	public int getRuleCount() {
		return ruleList.size();
	}
	
	public Rule getRule(int i) {
		return ruleList.get(i);
	}
}
