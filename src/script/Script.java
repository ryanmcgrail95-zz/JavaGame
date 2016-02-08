package script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cont.GameController;
import ds.StringExt;
import ds.StringExt2;

public class Script extends Action {
	private List<Task> taskList = new ArrayList<Task>();
	private List<Variable> tempVars = new ArrayList<Variable>();
	private List<Variable> vars = new ArrayList<Variable>();
	
	
	int tempNum = 0;

	private final static byte T_ACQUIRE_OBJ = 0;

	
	public Script() {
		super();
	}
	public Script(String name) {
		super(name, null,false);
	}
	public Script(String name, Variable[] parameterList, boolean hasOutput) {
		super(name, parameterList, hasOutput);
	}
	
	@Override
	public Variable run(Variable output, Variable[] parameters) {
		if(parameters != null)
			for(int i = 0; i < parameters.length; i++)
				vars.get(i).set(parameters[i]);
		
		Action a;
		MiscAction ma;
		Task t;
		boolean didRun;
		int taskNum = taskList.size();
		for(int i = 0; i < taskNum; i++) {
			t = taskList.get(i);
			a = t.getAction();
			
			didRun = false;
			
			if(a instanceof MiscAction) {
				ma = ((MiscAction) a);
				if(ma.isReturn()) {
					didRun = true;
					if(output == null)
						return a.run(output, t.getParameters());
					else
						return output.set(a.run(output, t.getParameters()));
				}
				else if(ma.isTaskMove() || ma.isTaskIf()) {
					didRun = true;
					i += t.run().getNumber();
				}
			}
			
			if(!didRun)
				t.run();
		}
		
		return null;
	}	
	
	/*public static Variable getValue(String value) {
		boolean inName = false, period = false;
		int len = value.length();
		String trueName = "";
		char c;
		
		for(int i = 0; i < len; i++) {
			c = value.charAt(i);
			
			if(isWhiteSpace(c)) {
				if(inName)
					break;
			}
			else
				if(!inName)
					inName = true;
			
			if(inName)
				trueName += c;
		}
		
		Variable val = null;
		val = Variable.varMap.get(trueName);
		
		if(val != null)
			return val;
		else if(period)
			return sc.addTemVar(true).set(Float.parseFloat(trueName));
		else
			return sc.addTempVar(true).set(Integer.parseInt(trueName));
	}*/
	
	public static void cleanCode(StringExt2 code) {
		String line, output = "";
		StringExt lineExt = new StringExt();

		// Erase Whitespace, Newlines, Comments, Etc.
		while(!code.isEmpty()) {
			lineExt.set(line = code.munchLine());
						
			if(lineExt.contains("//"))
				output += lineExt.munchTo("//");
			else
				output += lineExt;			
		}
				
		code.set(output);
		
		while(!code.munchFromTo("/*","*/").equals(""));
	}
	
	public void destroy() {
		super.destroy();
		
		for(Task t : taskList)
			t.destroy();
		for(Variable v : tempVars)
			v.destroy();
		for(Variable v : vars)
			v.destroy();
		
		taskList.clear();
		tempVars.clear();
		vars.clear();
	}
	
	public static Variable exec(String code) {
		StringExt2 codeExt = new StringExt2(code);
		cleanCode(codeExt);

		Script tempScr = createScript(codeExt.get());
		Variable v = tempScr.run(null, null);
		tempScr.destroy();
				
		return v;
	}
		
	/*public static Variable eval(final String str) {
	    class Parser {
	        int pos = -1, c;

	        void eatChar() {
	            c = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        void eatSpace() {
	            while (Character.isWhitespace(c)) eatChar();
	        }

	        Variable parse() {
	            eatChar();
	            Variable v = parseExpression();
	            if (c != -1) throw new RuntimeException("Unexpected: " + (char)c);
	            return v;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor | term brackets
	        // factor = brackets | number | factor `^` factor
	        // brackets = `(` expression `)`

	        Variable parseExpression() {
	            Variable v = parseTerm();
	            for (;;) {
	                eatSpace();
	                if (c == '+') { // addition
	                    eatChar();
	                    v.adde(parseTerm());
	                } else if (c == '-') { // subtraction
	                    eatChar();
	                    v.sube(parseTerm());
	                } else {
	                    return v;
	                }
	            }
	        }

	        Variable parseTerm() {
	            Variable v = parseFactor();
	            for (;;) {
	                eatSpace();
	                if (c == '=') {
	                	eatChar();
	                	if(!v.canBeSet())
	                		throw new UnsupportedOperationException();
	                	v.set(parseExpression());
	                	v = v.copy();
	                }
	                else {
	                	if(v.canBeSet())
		                	v = v.copy();
	                	
		                if (c == '/') { // division
		                    eatChar();
		                    v.dive(parseFactor());
		                } else if (c == '*' || c == '(') { // multiplication
		                    if (c == '*') eatChar();
		                    v.multe(parseFactor());
		                } else {
		                    return v;
		                }
	                }
	            }
	        }

	        Variable parseFactor() {
	            Variable v;
	            boolean isVar = false, negate = false;
	            eatSpace();
	            if (c == '+' || c == '-') { // unary plus & minus
	                negate = c == '-';
	                eatChar();
	                eatSpace();
	            }
	            if (c == '(') { // brackets
	                eatChar();
	                v = parseExpression();
	                if (c == ')') eatChar();
	            } else { // numbers/variables
	                StringBuilder sb = new StringBuilder();
	                if(Character.isAlphabetic(c))
	                	do {
	                		sb.append((char) c);
	                		eatChar();
	                	} while(Character.isAlphabetic(c));
                	else
		                while ((c >= '0' && c <= '9') || c == '.') {
		                    sb.append((char)c);
		                    eatChar();
		                }
	                if (sb.length() == 0) throw new RuntimeException("Unexpected: " + (char)c);
	                
	                v = parseValue(sb.toString());
	            }
	            eatSpace();
	            if (c == '^') { // exponentiation
	            	if(v.canBeSet())
	            		v = v.copy();
	            	eatChar();
	                v.powe(parseFactor());
	            }
	            if (negate) {
	            	if(v.canBeSet())
	            		v = v.copy();
	            	v.multe(-1); // unary minus is applied after exponentiation; e.g. -3^2=-9
	        	}
	            return v;
	        }
	        
	        Variable parseValue(final String sb) {
	        	if(Character.isAlphabetic(sb.charAt(0)))
	        		return Variable.get(sb);
	        	else if(sb.contains("."))
	        		return new Variable().set(Double.parseDouble(sb));
	        	else
	        		return new Variable().set(Integer.parseInt(sb));
	        }
	    }
	    return new Parser().parse();
	}*/
	
	public static Script createScript(String str) {
		Stack<Script> scs = new Stack<Script>();
		Stack<Variable> os = new Stack<Variable>();
		
	    class Parser {
	        int pos = -1, c, lc, strLen = str.length();
	        
	        Script sc;
	        Variable o = null;
			
	        void scpu(String name)		{sc = scs.push(new Script(name));}
	        void scpo() {
	        	scs.pop();
	        	sc = scs.peek();
	        }

	        Variable opu(Variable v) 	{return o = os.push(v);}
	        void opo() {
	        	os.pop();
	        	o = os.isEmpty() ? null : os.peek();
	        }

	        
	        boolean nibble(String substr) {
	        	int len = substr.length();
	        	if(str.regionMatches(pos, substr, 0, len)) {
	        		pos += len;
	        		lc = c;
	        		c = (pos < strLen) ? str.charAt(pos) : -1;
	        		return true;
	        	}
	        	else
	        		return false;
	        }
	        
	        boolean nibble(char c) {return nibble(""+c);}
	        
	        int nibbleMisc(int... cs) {
	        	for(int cc : cs)
	        		if(nibble((char) cc))
	        			return cc;
	        	return -1;
	        }
	        
	        String eatTo(String substr) {
	        	String outStr;
	        	int len = substr.length();
	        	for(int i = pos; i < strLen-len; i++) {
		        	if(str.regionMatches(i, substr, 0, len)) {
		        		outStr = str.substring(pos,i);
		        				        		
		        		pos = i+len;
		        		lc = c;
		        		c = (pos < strLen) ? str.charAt(pos) : -1;
		        				        		
		        		return outStr;
		        	}
	        	}
	        	
	        	return null;
	        }
	        
	        String eatTo(char c) {return eatTo("" + c);}
	        
	        boolean didEat(int c) {return lc == c;}

	        void eatChar() {
	        	lc = c;
	            c = (++pos < strLen) ? str.charAt(pos) : -1;
	        }
	        
	        void eat(char c) {
	            if(!nibble(c))
	            	throw new RuntimeException("Expected: " + (char)c);	            
	        }
	        
	        boolean done() {return c == -1;}

	        void eatSpace() {
	            while(Character.isWhitespace(c)) eatChar();
	        }

	        Script parse() {
	        	scpu("");
	        	
	            eatChar();
	            Variable v = parseStatements();
	            if (c != -1) throw new RuntimeException("Unexpected: " + (char)c);
	            return sc;
	        }
	        
	        // New Script
	        
	        

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor | term brackets
	        // factor = brackets | number | factor `^` factor
	        // brackets = `(` expression `)`
	        
	        Variable parseStatements() {
	        	Variable v = parseStatement();

	            for(;;) {
	                eatSpace();
	                if((nibble(';') || didEat('}')) && !done())
		                v = parseStatement();
	                else
	                	return v;
	            }
	        }

	        Variable parseStatement() {
                eatSpace();
                if(nibble("if")) {
                	Variable bool, skipIf = sc.addConstant(), skipElse = sc.addConstant();
                	
                	eatSpace();
                	eat('(');
                	bool = parseExpression();
                	eat(')');
                	
                	sc.addTask(MiscAction.createIfTask(sc.addTempBuffer(), bool));
                	
                	//If False, Skip to After If
                	sc.addTask(MiscAction.createTaskMoveTask(skipIf));
                	
                	int num1 = sc.taskList.size(), num2, num3;
                	eatSpace();
                	if(nibble('{')) {
                		parseStatements();
                		eat('}');
                	}
                	num2 = sc.taskList.size();

                	eatSpace();
                	if(nibble("else")) {
                    	sc.addTask(MiscAction.createTaskMoveTask(skipElse));
                		eatSpace();
                		if(nibble('{')) {
                    		parseStatements();
                    		eat('}');
                    	}
                		else
                			parseStatement();
                	}
                	else
                		skipElse.destroy();
                	num3 = sc.taskList.size();
                	
                	skipIf.set(1+(num2-num1));
                	if(skipElse != null)
                		skipElse.set(-1+(num3-num2));
                	
                	return null;
                }
                else
                	return parseExpression();
	        }

	        
	        void parseParameters(List<Variable> parameters) {
        		Variable v = parseExpression();
	        	if(v == null)
	        		return;
	        	else {
	        		for(;;) {
		        		eatSpace();
			            if(nibble(',')) { //Next 
			            	parameters.add(v);
			                v = parseExpression();
		                } 
			            else {
			            	parameters.add(v);
			            	return;
		                }
		            }
	        	}
	        }
	        	        

	        Variable parseExpression() {
	            Variable v = parseTerm(), pv, dst;
	            int n;
	            boolean eq;
	            
	            for(;;) {
	                eatSpace();
	                if(nibble('<')) {
	                	eq = nibble('=');
	                	
	                	if((pv = parseTerm()).isConstant() && v.isConstant()) {
	                		v.set(eq ? v.getNumber() <= pv.getNumber() : v.getNumber() < pv.getNumber());
	                    	sc.removeTempVar(pv);
	                    }
	                    else {
	                    	dst = (!v.isConstant()) ? v : pv;
	                
	                    	if(eq)
	                    		dst = v;
	                    	else if(!dst.isTempBuffer())
                    			dst = sc.addTempBuffer();
		                    sc.addTask(MiscAction.createCompareTask(dst, v,pv, C_LESS_EQUAL));
		                    v = dst;
	                    }
	                }
	                else if(nibble('+')) // addition	                    
	                    v = parseOperation(v, "+");
	                else if(nibble('-')) // addition	                    
	                    v = parseOperation(v, "-");
	                else
	                    return v;
	            }
	        }

	        Variable parseOperation(Variable v, String operation) {
	        	Variable pv, dst;
	        	Task t;
	        	boolean eq = nibble('=');
	        	
                if((pv = parseTerm()).isConstant() && v.isConstant()) {
                	v.operation(operation+"=",pv);
                	sc.removeTempVar(pv);
                }
                else {
                	dst = (!v.isConstant()) ? v : pv;
                	dst = eq ? v : (!dst.isTempBuffer() ? sc.addTempBuffer() : dst);
                    switch(operation) {
                    	case "+=":	dst.destroy();
                    				dst = v;
                    	case "+":	t = MiscAction.createAddTask(dst, v,pv);		break;
                    	case "-":	t = MiscAction.createSubtractTask(dst, v,pv);	break;
                    	case "*":	t = MiscAction.createMultiplyTask(dst, v,pv);	break;
                    	case "/":	t = MiscAction.createDivideTask(dst, v,pv);		break;
                    	default: 	throw new RuntimeException("Operation does not exist.");
                    }
                	sc.addTask(t);
                    v = dst;
                }
                
                return v;
	        }
	        
	        Variable parseTerm() {
	            Variable v = parseFactor(), dst, pv;
	            int n;
	            boolean eq = false;
	            
	            if(v == null)
	            	return v;
	            for(;;) {
	                eatSpace();
	                if(nibble('=')) {
	                	if(v.isConstant())
	                		throw new UnsupportedOperationException();
	                	sc.addTask(MiscAction.createSetTask(v, pv = parseExpression()));
	                	
	                	dst = sc.addTempBuffer();
	                	sc.addTask(MiscAction.createSetTask(dst, v));
	                	v = dst;
	                }
	                else {
	                	if(nibble('/')) { // division
	                		v = parseOperation(v, "/");
		                } else if((n = nibbleMisc('*','(')) != -1) { // multiplication
		                	if(n == '*')
		                		eq = nibble('=');
			                
		                	if((pv = parseTerm()).isConstant() && v.isConstant()) {
		                    	v.multe(pv);
		                    	sc.removeTempVar(pv);
		                    }
		                    else {
		                		dst = (!v.isConstant()) ? v : pv;
		                		if(eq)
		                			dst = v;
		                		else if(!dst.isTempBuffer())
		                    		dst = sc.addTempBuffer();
			                    sc.addTask(MiscAction.createMultiplyTask(dst, v,pv));
			                    v = dst;
		                    }
		                } else {
		                    return v;
		                }
	                }
	            }
	        }
	        
	        

	        Variable parseFactor() {
	            Variable v, dst;
	            int n;
	            boolean isVar = false, negate = false;
	            eatSpace();
	            if((n = nibbleMisc('+','-')) != -1) { // unary plus & minus
	                negate = n == '-';
	                eatSpace();
	            }
	            if(nibble('(')) { // brackets
	                v = parseExpression();
	                eat(')');
	            } else if(c == -1 || c == ')' || c == '}')
	            	return null;
	            else { // numbers/variables
	                StringBuilder sb = new StringBuilder();
	                String s;
	                byte type;
	                if(c == '$' || c == '#' || c == '"' || c == '@' || c == '&' || Character.isAlphabetic(c)) {
	                	if(c == '$') {
	                		type = P_VARIABLE;
	                		eatChar();
	                	}
	                	else if(c == '#') {
	                		eatChar();
	                		if(nibble('#'))
	                			type = P_NEW_ACTION;
                			else
    	                		type = P_ACTION;
	                	}
	                	else if(c == '@') {
	                		type = P_OBJECT;
	                		eatChar();
	                	}
	                	else if(c == '&') {
	                		type = P_NEW_OBJECT;
	                		eatChar();
	                	}
	                	else if(c == '\"') {
	                		type = P_STRING;	
	                		eatChar();
	                	}
	                	else
	                		type = P_CONSTANT;
	                	
	                	if(type == P_STRING)
	                		s = eatTo('\"');
	                	else {
		                	do {
		                		sb.append((char) c);
		                		eatChar();
		                	} while(Character.isAlphabetic(c));

		                	s = sb.toString();
	                	}
	                }
                	else {
                		type = V_NUMBER;
		                while ((c >= '0' && c <= '9') || c == '.') {
		                    sb.append((char)c);
		                    eatChar();
		                }

		                s = sb.toString();
                	}
	                
	                v = parseValue(s, type);	                
	            }
	            eatSpace();
	            if(nibble('^')) { // exponentiation
	            	Variable f = parseFactor();
	            	if(v.isConstant() && f.isConstant()) {
	            		v.powe(f);
	            		sc.removeTempVar(f);
	            	}
	            	else {
	            		dst = (!v.isConstant()) ? v : f;
	            		if(!dst.isTempBuffer())
	            			dst = sc.addTempBuffer();
	            		sc.addTask(MiscAction.createExponentTask(dst, v,f));
	            		v = dst;
	            	}
	            }
	            if(negate) {
	            	if(v.isTempBuffer())
	            		sc.addTask(MiscAction.createMultiplyTask(v, v, sc.addConstant(-1)));
            		else {
	            		if(v.isConstant() && !v.isTrueVariable())
	                		v.multe(-1);
		                else {
		            		dst = sc.addTempVar(false,false);
		            		sc.addTask(MiscAction.createMultiplyTask(dst, v,sc.addConstant(-1)));
		                    v = dst;
		                }
            		}
	        	}
	            return v;
	        }
	        
	        Variable parseValue(final String sb, byte type) {
	        	if(type == P_NEW_OBJECT) {
	        		Variable output = sc.addTrueVar(), outputVar, name = sc.addConstant(sb);
	        		Task m = MiscAction.createCreateTask(output, name);
	        		sc.addTask(m);

	        		if(nibble(".$")) {
	        			StringBuilder s = new StringBuilder();
	                	do {
	                		s.append((char) c);
	                		eatChar();
	                	} while(Character.isAlphabetic(c));
	                	
	                	Variable v = parseValue(s.toString(), P_VARIABLE);
	                	opo();
	                	return v;
	        		}
	        		else if(nibble("(")) {
	        			List<Variable> args = new ArrayList<Variable>();
	        				        			
	        			parseParameters(args);
	        			eat(')');	        			

	        			Variable[] argsA = new Variable[args.size()];
	        			args.toArray(argsA);
	        			
		        		m.addParameters(argsA);		        		
	        		}
	        		
	        		else {	
	        			eatSpace();
	        			if(nibble('{')) {
	        				opu(output);
		        			parseStatements();
		        			opo();
		        			eat('}');
		        			return null;
		        		}
	        		}
	        		
	        		return output;
	        	}
	        	else if(type == P_VARIABLE) {
	        		if(o == null)
	        			return sc.addVar(Variable.get(sc,sb, false,true));
	        		else {
	        			Variable outputVar = sc.addTrueVar();
	        			sc.addTask(MiscAction.createAccessVarTask(outputVar, o, sc.addConstant(sb)));
	        			return outputVar;
	        		}
	        	}
	        	else if(type == P_NUMBER)
	        		return sc.addTempVar(true,false).set(Double.parseDouble(sb));
	        	else if(type == P_NEW_ACTION) {
	        		scpu(sb);
	        		
        			List<Variable> args = new ArrayList<Variable>();
        			Variable[] argsA = new Variable[args.size()];
        			
        			args.toArray(argsA);
        			
	        		eat('(');
        			parseParameters(args);
        			eat(')');
        			
	        		sc.setParameterList(argsA);
	        		
	        		eatSpace();
	        		eat('{');
	        		parseStatements();
	        		eat('}');
	        		
	        		scpo();
	    
	        		return null;
	        	}
        		else if(type == P_ACTION) {
        			Action a = Action.getAction(sb);
        			
        			if(a instanceof MiscAction)
        				if(((MiscAction) a).isReturn())
        					sc.setHasOutput(true);
        			        			
        			List<Variable> parameters = new ArrayList<Variable>();
        			
        			eat('(');
        			parseParameters(parameters);
        			eat(')');
        			
        			Variable[] parArray = new Variable[parameters.size()];
        			parameters.toArray(parArray);
        			
        			Variable output = null;
        			if(a.hasOutput())
        				output = sc.addTempBuffer();
        			Task t = new Task(a, parArray, output);
        			sc.addTask(t);
        			        			
        			return output;
        		}
        		else if(type == P_STRING)
        			return sc.addConstant(sb);
        		else if(type == P_CONSTANT) {
        			switch(sb) {
        				case "TRUE": 	return sc.addConstant(true);
        				case "FALSE": 	return sc.addConstant(false);
        				default: 		return null;
        			}
        		}
        		else
        			return null;
	        }
	    }
	    return new Parser().parse();	    

	    /*System.out.println("Created new script: ");
	    System.out.println("\tTasks: " + sc.taskList.size());
	    	for(Task t : sc.taskList)
	    		System.out.println("\t\t" + t.getAction().getName());
	    System.out.println("\tVariables: " + sc.tempVars.size());*/
	}
	
	public void addTask(Task t) {
		taskList.add(t);
	}
	
	private Variable addConstant(boolean value) {return addConstant().set(value);}
	private Variable addConstant(double value) 	{return addConstant().set(value);}
	private Variable addConstant(String value) 	{return addConstant().set(value);}
	private Variable addConstant() 				{return addTempVar(true,false);}
	
	private Variable addVar(Variable v) {
		vars.add(v);
		return v;
	}
	private Variable addTempVar(boolean isConstant, boolean isTrueVariable) {
		Variable v = Variable.get(this, "temp" + tempNum++, isConstant, isTrueVariable);
		tempVars.add(v);
		return v;
	}
	private Variable addTrueVar() 				{return addTempVar(false,true);}
	private Variable addTempBuffer() 			{return addTempVar(false,false);}
	
	private void removeTempVar(Variable tempVar) {
		tempVar.destroy();
		tempVars.remove(tempVar);
	}
	
	public static void ini() {
		
	}
}
