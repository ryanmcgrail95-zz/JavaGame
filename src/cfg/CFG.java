package cfg;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import cfg.Symbol.Element;
import cfg.Symbol.Rule;

public class CFG {
	private Symbol rootSymbol;
	private LinkedList<Symbol> symbolList;

	public CFG() {
		symbolList = new LinkedList<Symbol>();
		rootSymbol = addSymbol();
	}

	public Symbol getRootSymbol() {
		return rootSymbol;
	}

	public Symbol addSymbol() {
		Symbol symbol = new Symbol();
		symbolList.add(symbol);
		return symbol;
	}

	public boolean parse(String string) {
		return (new RyanParser()).parse(string);
	}

	private class RyanParser {
		// Loop prevention:
		// L1) If ever reaches previously reached state at same index & same
		// size, terminate.
		// L2) If known size ever exceeds size of string, terminate.
		// L3) If rule contains sequence not present in string, terminate.
		// L4) If rule starts with string, and string not at current index,
		// terminate.

		private class Context {
			public Symbol symbol;
			public int parentRule, parentRuleIndex;

			public Context(Symbol symbol, int parentRule, int parentRuleIndex) {
				super();
				this.symbol = symbol;
				this.parentRule = parentRule;
				this.parentRuleIndex = parentRuleIndex;
			}
		}

		private class State {
			public Symbol symbol;
			public LinkedList<Context> contextChain;
			// If rule is -1, then run through all! Otherwise, proceed along
			// track
			// of current rule.
			public int rule, index, processedSize, unprocessedSize;

			public State(LinkedList<Context> contextChain, Symbol symbol, int rule, int index, int unprocessedSize) {
				super();
				this.contextChain = contextChain;
				this.symbol = symbol;
				this.rule = rule;
				this.index = index;
				this.unprocessedSize = unprocessedSize;
			}

			public int priority() {
				return index + unprocessedSize;
			}
		}

		public final Comparator<State> PRIORITY = new Comparator<State>() {
			public int compare(State o1, State o2) {
				return (int) (o2.priority() - o1.priority());
			}
		};

		private int len;
		Queue<State> states = new PriorityQueue<State>(PRIORITY);
		LinkedList<State> closedStates = new LinkedList<State>();

		public boolean parse(String string) {
			State currentState;
			Symbol currentSymbol;
			Context context;
			int currentIndex;

			len = string.length();

			LinkedList<Context> rootContext = new LinkedList<Context>();
			rootContext.add(new Context(null, -1, -1));
			states.add(new State(rootContext, rootSymbol, -1, 0, 0));

			while (states.size() > 0) {
				System.out.println("---------");

				currentState = states.peek();
				currentSymbol = currentState.symbol;
				currentIndex = currentState.index;
				context = currentState.contextChain.getLast();

				// Attempt to complete rules.
				if (currentState.rule == -1) {
					for (int i = 0, ruleCt = currentSymbol.getRuleCount(); i < ruleCt; i++) {
						Rule rule = currentSymbol.getRule(i);
						int k = 0;
						int index = currentState.index,
								unprocessedSize = currentState.unprocessedSize + rule.getMinSize();

						// L2)
						if (currentIndex + unprocessedSize > len)
							continue;

						// L4)
						String start = rule.getStartString();
						if (start != null)
							if (!string.regionMatches(currentIndex, start, 0, start.length()))
								continue;
							else {
								System.out.println("Early matched " + start + "!");

								index += start.length();
								unprocessedSize -= start.length();
								k = 1;
							}

						int success = 1;
						for (int elemCt = rule.getElementCount(); k < elemCt; k++) {
							Element elem = rule.getElement(k);
							if (elem.isString()) {
								String str = elem.getString();
								if (string.regionMatches(index, str, 0, str.length())) {
									System.out.println("Matched " + str + "!");

									success = 1;
									index += str.length();
									unprocessedSize -= str.length();
								} else {
									System.out.println("Failed to match.");
									success = 0;
									break;
								}
							} else {
								System.out.println("Added symbol..");

								Symbol sym = elem.getSymbol();

								LinkedList<Context> newContext = (LinkedList<Context>) currentState.contextChain
										.clone();

								System.out.println(
										"Added w/ " + i + ", " + k + " / " + index + ", " + unprocessedSize + ".");
								newContext.add(new Context(currentSymbol, i, k));

								states.add(new State(newContext, sym, -1, index, unprocessedSize));
								success = 2;
								break;
							}
						}

						if (success == 1) {
							if (index == len) {
								// TODO: PROPER CLEANUP!!!
								System.out.println("TRUE!");
								return true;
							}
							
							// If parent exists, then re-append parent!
							ascend(currentState, index, unprocessedSize);
						}
					}
				} else {
					Rule rule = currentSymbol.getRule(currentState.rule);
					int success = 1;
					int index = currentState.index, unprocessedSize = currentState.unprocessedSize;

					for (int k = currentState.rule, elemCt = rule.getElementCount(); k < elemCt; k++) {
						Element elem = rule.getElement(k);
						if (elem.isString()) {
							String str = elem.getString();
							if (string.regionMatches(index, str, 0, str.length())) {
								success = 1;
								index += str.length();
								unprocessedSize -= str.length();
							} else {
								success = 0;
								break;
							}
						} else {
							Symbol sym = elem.getSymbol();

							LinkedList<Context> newContext = (LinkedList<Context>) currentState.contextChain.clone();
							newContext.add(new Context(currentSymbol, currentState.rule, k));
							states.add(new State(newContext, sym, -1, index, unprocessedSize));
							
							//descend(currentState, k, newContext, );

							success = 2;
							break;
						}
					}

					if (success == 1) {
						if (index == len) {
							// TODO: PROPER CLEANUP!!!
							return true;
						}

						// If parent exists, then re-append parent!
						ascend(currentState, index, unprocessedSize);
					}
				}

				states.remove();
				System.out.println("SIZE: " + states.size());
			}

			return false;
		}

		private State descend(State currentState, int parentRuleIndex, Symbol sym, int index, int unprocessedSize) {
			LinkedList<Context> newContext = (LinkedList<Context>) currentState.contextChain.clone();
			newContext.add(new Context(currentState.symbol, currentState.rule, parentRuleIndex));

			State state = new State(newContext, sym, -1, index, unprocessedSize);
			states.add(state);

			return state;
		}

		private State ascend(State currentState, int index, int unprocessedSize) {
			Context context = currentState.contextChain.getLast();

			if (context.symbol != null) {
				LinkedList<Context> newContext = (LinkedList<Context>) currentState.contextChain.clone();
				newContext.removeLast();

				State state = new State(newContext, context.symbol, context.parentRule, index, unprocessedSize);
				states.add(state);
				return state;
			}
			return null;
		}
	}

	// TODO: Parse Earley?

}
