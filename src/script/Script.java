package script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import cont.GameController;
import cont.Log;
import ds.StringExt;
import ds.StringExt2;
import ds.nameid.ID;
import ds.nameid.NameIDMap;
import gfx.GL;

public class Script extends Action {
	private Task[] taskList;
	
	private final static byte T_ACQUIRE_OBJ = 0;

	public Script(PMLMemory mem, String name, int regNum, int[] argInds, ConstantRegister[] constList, boolean hasOutput, Task[] taskList) {
		super(mem, name, regNum, argInds, constList, hasOutput);
		
		Log.println(Log.ID.PML, true, "Script()");
		
		this.taskList = taskList;
		
		Log.println(Log.ID.PML, false, "");
	}
	
	@Override
	public void call(Register output, Register[] parameters, PMLMemory mem) {
		// If have parameters, set the first |parameters| elements
		if(parameters != null)
			for(int i = 0; i < parameters.length; i++)
				mem.access(i).set(parameters[i]);
		
		String actionName;
		MiscAction ma;
		Task t;
		boolean didRun;
		int taskNum = taskList.length;
		for(int i = 0; i < taskNum; i++) {
			t = taskList[i];
			actionName = t.getActionName();
			
			didRun = false;
			
			if(actionName == "_return") {
				didRun = true;
				
				output.set(mem.access(t.getParameterIndex(0)));
				return;
			}
			else if(actionName == "_move" || actionName == "_if") {
				didRun = true;
				i += t.run(mem).getNumber();
			}
			
			if(!didRun)
				t.run(mem);
		}
	}	
	
	/*public static Register getValue(String value) {
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
		
		Register val = null;
		val = Register.varMap.get(trueName);
		
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
		
		for(Task t : taskList) {
			t.destroy();
			t = null;
		}
		
		taskList = null;
	}
	
	public static Register exec(PMLMemory mem, String code) {
		Log.println(Log.ID.PML, true, "exec()");
		
		StringExt2 codeExt = new StringExt2(code);
		cleanCode(codeExt);

		NameIDMap<RegisterBlueprint> varMap = new NameIDMap<RegisterBlueprint>();
		
		Script tempScr = compile(mem, "__TEST__", codeExt.get(), varMap, null);
		
		Register output = null;
		tempScr.run(output, null, mem);
		tempScr.destroy();
		varMap.destroy();
		
		mem.destroy();
		
		Log.println(Log.ID.PML, false, "");
				
		return output;
	}
		
	/*public static Register eval(final String str) {
	    class Parser {
	        int pos = -1, c;

	        void eatChar() {
	            c = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        void eatSpace() {
	            while (Character.isWhitespace(c)) eatChar();
	        }

	        Register parse() {
	            eatChar();
	            Register v = parseExpression();
	            if (c != -1) throw new RuntimeException("Unexpected: " + (char)c);
	            return v;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor | term brackets
	        // factor = brackets | number | factor `^` factor
	        // brackets = `(` expression `)`

	        Register parseExpression() {
	            Register v = parseTerm();
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

	        Register parseTerm() {
	            Register v = parseFactor();
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

	        Register parseFactor() {
	            Register v;
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
	        
	        Register parseValue(final String sb) {
	        	if(Character.isAlphabetic(sb.charAt(0)))
	        		return Register.get(sb);
	        	else if(sb.contains("."))
	        		return new Register().set(Double.parseDouble(sb));
	        	else
	        		return new Register().set(Integer.parseInt(sb));
	        }
	    }
	    return new Parser().parse();
	}*/
	
	public static Script compile(PMLMemory mem, String name, String code, NameIDMap<RegisterBlueprint> regIDMap, ID<RegisterBlueprint>[] argList) {
		Log.println(Log.ID.PML, true, "compile()");

		
		StringExt str = new StringExt(code);
		
		Stack<Script> scs = new Stack<Script>();
		Stack<ID<RegisterBlueprint>> os = new Stack<ID<RegisterBlueprint>>();
		
		
		List<TaskBlueprint> taskList = new ArrayList<TaskBlueprint>();
		boolean hasOutput = false;
		
		
	    class Parser {
	        int pos = -1, cc, lc, strLen = str.length();
	        
	        ID<RegisterBlueprint> o = null;
			
	        ID<RegisterBlueprint> opu(ID<RegisterBlueprint> v) {return o = os.push(v);}
	        void opo() {
	        	os.pop();
	        	o = os.isEmpty() ? null : os.peek();
	        }

	        
	        char lc() {	       	
	        	Character c = StringExt.NULL_CHAR;
	        	int i = -1, consumedLen;
	        	while(str.isValidChar(i))
	        		if(!Character.isWhitespace(c = str.charAt(i--)))
	        			break;
	        	
	        	return c;
	        }
	        char c() {	        	
	        	return str.charAt(0);
	        }

	  	        	        
	        boolean done() {return c() == StringExt.NULL_CHAR;}

	        Script parse() {
	        	ID<RegisterBlueprint> v = parseStatements(), r;
	            
	        	if (c() != StringExt.NULL_CHAR) throw new RuntimeException("Unexpected: " + (char) c());
	        	
	    		Log.println(Log.ID.PML, true, "Finalizing script...");

	        	int regNum = regIDMap.size();
	        	
	        	List<ConstantRegister> constList = new ArrayList<ConstantRegister>();
	        	for(int i = 0; i < regNum; i++)
	        		if(isConstant(r = regIDMap.getID(i)))
	        			constList.add(new ConstantRegister(r, r.get()));
	        	
	        	int[] argArray = null;
	        	if(argList != null) {
	        		argArray = new int[argList.length];
		        	for(int i = 0; i < argList.length; i++)
		        		argArray[i] = i;
	        	}
	        	
	        	ConstantRegister[] constArray = new ConstantRegister[constList.size()];	        	
	        	constList.toArray(constArray);
	        	constList.clear();

	        	Task[] taskArray = new Task[taskList.size()];
	        	for(int i = 0; i < taskList.size(); i++) {
	        		taskArray[i] = new Task(taskList.get(i));
	        		taskList.get(i).destroy();
	        	}
	        	taskList.clear();

	        	
	        	Script s = new Script(mem, name, regNum, argArray, constArray, hasOutput, taskArray);

	            Log.println(Log.ID.PML, false, "");

	            return s;
	        }
	        
	        // New Script
	        
	        

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor | term brackets
	        // factor = brackets | number | factor `^` factor
	        // brackets = `(` expression `)`
	        
	        ID<RegisterBlueprint> parseStatements() {
	        	Log.println(Log.ID.PML, true, "parseStatements()");

	        	ID<RegisterBlueprint> v = parseStatement();
	            	            
	            for(;;) {	                
	                //Log.println(Log.ID.PML, true, lc() + " ... " + c() + " ... " + str);
	               	        
	                // Either needs to have just processed ;, or on }
	               
	            	Log.println(Log.ID.PML, "c: \"" + c() + "\", \"" + lc() + "\"");
	            	
	                if(c() == '}')
	                	break;
	                else if((lc() == ';' || lc() == '}') && !done()) {
		                str.munchSpace();
	                	v = parseStatement();
	                }
	                else
	                	break;
	                
    	        	//Log.println(Log.ID.PML, false, "");
	            }
	            
	            Log.println(Log.ID.PML, false, "");
	            
            	return v;
	        }

	        ID<RegisterBlueprint> parseStatement() {
	        	Log.println(Log.ID.PML, true, "parseStatement()");
	        	
	        	ID<RegisterBlueprint> out;
	        	
                str.munchSpace();
                
                if(str.nibble("return")) {
        			addTask("_return", addTempBuffer(), parseExpression());
                	str.chomp(';');
                	
                	out = null;
	        	}
                else if(str.nibble("if")) {
                	ID<RegisterBlueprint> bool, skipIf = addConstant(), skipElse = null;
                	boolean hasElse;
                	
                    str.munchSpace();
                	str.chomp('(');
                	bool = parseExpression();
                	str.chomp(')');
                	
                	addTask("_if", addTempBuffer(), bool);
                	
                	//If False, Skip to After If
                	addTask("_move", addTempBuffer(), skipIf);
                	
                	int num1 = taskList.size(), num2, num3;
                    str.munchSpace();
                	if(str.nibble('{')) {
                		parseStatements();
                		str.chomp('}');
                	}
                	else
                		parseStatement();
                	num2 = taskList.size();
                	
            		setRegister(skipIf, 1+(num2-num1));


                    str.munchSpace();
                    hasElse = false;
                	if(str.nibble("else")) {
                		hasElse = true;
                		skipElse = addConstant();
                		
                    	addTask("_move", addTempBuffer(), skipElse);
                        str.munchSpace();
                		if(str.nibble('{')) {
                			parseStatements();
                    		str.chomp('}');
                    	}
                		else
                			parseStatement();
                	}
                	num3 = taskList.size();
                	
                	if(hasElse)
                		setRegister(skipElse, -1+(num3-num2));
                	else
                		setRegister(skipIf, num2-num1);
                	
                	out = null;
                }
                else if(str.nibble("while")) {
                	ID<RegisterBlueprint> bool, reset = addConstant(), skip = addConstant();

                	int num1 = taskList.size(), num2, num3;
                	boolean hasElse;
                	
                	str.munchSpace();
                	str.chomp('(');
        	        	bool = parseExpression();
    	        	str.chomp(')');
                	
                	addTask("_if", addTempBuffer(), bool);
                	
                	//If False, Skip to After While
                	addTask("_move", addTempBuffer(), skip);
                	num2 = taskList.size();

                	str.munchSpace();
                	if(str.nibble('{')) {
                		parseStatements();
                		str.chomp('}');
                	}
            		else
            			parseStatement();
                	
                	addTask("_move",addTempBuffer(), reset);
                	num3 = taskList.size();
                	
        	        setRegister(reset, -(num3-num1));
                	setRegister(skip, num3-num2);
        	        	        	
                	out = null;
                }
                else {
                	ID<RegisterBlueprint> v = parseExpression();
                	
                	Log.println(Log.ID.PML, true, "chomping ;");
                	if(lc() != '}') {
                		str.chomp(';');
                		Log.println(Log.ID.PML, false, "YEP!");
                	}
                	else
                		Log.println(Log.ID.PML, false, "didn't need to!");
                	
                	out = v;
                }
                
	        	Log.println(Log.ID.PML, false, ""+out);	        	
	        	return out;
	        }
	        
	        ID<RegisterBlueprint> addTask(String actionName, ID<RegisterBlueprint> outInd, ID<RegisterBlueprint>... parameterIndices) {
	        	Log.println(Log.ID.PML, true, "addTask("+actionName+")");
	        	
	        		for(ID<RegisterBlueprint> r : parameterIndices) {
	        			Log.println(Log.ID.PML, r);
	        			if(r == null)
	        				throw new NullPointerException("Null parameter given!");
	        		}
	        		
	        		taskList.add(new TaskBlueprint(actionName, parameterIndices, outInd));
	        	
	        	Log.println(Log.ID.PML, false, "");
	        	
	        	return outInd;
	        }
	        
	        String parseVarName() {
	        	Log.println(Log.ID.PML, true, "Building Name...");

	        	StringBuilder sb = new StringBuilder();
	        	
            	do {
            		sb.append(c());
            		str.munchChar();
            	} while(Character.isAlphabetic(c()));

            	String s = sb.toString();
            	
	        	Log.println(Log.ID.PML, false, s);
	        	
	        	return s;
	    	};
	        
	        void parseScript(String subName) {
	        	String substr = "";
	    		
	    		str.munchSpace();
	    		str.chomp('(');
	    		str.munchSpace();
	    		
	    		NameIDMap<RegisterBlueprint> subRegIDMap = new NameIDMap<RegisterBlueprint>();
	    		List<ID<RegisterBlueprint>> subArgList = new ArrayList<ID<RegisterBlueprint>>();
	    			    		
	    		// Get Parameters
	    		if(!str.nibble(')'))
	    			while(c() != StringExt.NULL_CHAR) {	    				
	    				str.munchSpace();
	    				str.chomp('$');
	    				
	    				ID<RegisterBlueprint> id = subRegIDMap.add(parseVarName(), new RegisterBlueprint(false, true));
	    				subArgList.add(id);

	    				str.munchSpace();
	    				
	    				if(str.nibble(')'))
	    					break;
	    				else
	    					str.nibble(',');
	    			}
	    		
	    		ID<RegisterBlueprint> subArgArray[] = new ID[subArgList.size()];
	    		subArgList.toArray(subArgArray);
	    		subArgList.clear();
	    		
	    		
	    		str.munchSpace();
	    		str.chomp('{');
	    		
	    		
	    		// Get Internal String
	    		int brackets = 1;
	    		while(brackets > 0) {
	    			substr += c();
	    			str.munchChar();
	    			
	    			if(c() == '{')
	    				brackets++;
	    			else if(c() == '}')
	    				brackets--;
	    		}
	    		
	    		str.chomp('}');
	    		
	    		compile(mem, subName, substr, subRegIDMap, subArgArray);
	        }

	        boolean isConstant(ID<RegisterBlueprint> i) 	{return i.get().get().isConstant();}
	        boolean isTrueVariable(ID<RegisterBlueprint> i) {return i.get().get().isTrueVariable();}
	        boolean isTempBuffer(ID<RegisterBlueprint> i) 	{return i.get().get().isTempBuffer();}
	        
	       	        
	        void parseParameters(List<ID<RegisterBlueprint>> parameters) {
	        	Log.println(Log.ID.PML, true, "parseParameters()");
	        	
	        	ID<RegisterBlueprint> v = parseExpression();
	        	if(v == null) {
		        	Log.println(Log.ID.PML, false, "(nothing)");
	        		return;
	        	}
	        	else {
	        		for(;;) {
	                    str.munchSpace();
			            if(str.nibble(',')) { //Next 
			            	parameters.add(v);
			                v = parseExpression();
		                } 
			            else {
			            	// TODO: add proper checking for v!
			            	if(v != null)
			            		parameters.add(v);
				        	Log.println(Log.ID.PML, false, "");
			            	return;
		                }
		            }
	        	}
	        }
	        	        

	        ID<RegisterBlueprint> parseExpression() {
	        	Log.println(Log.ID.PML, true, "parseExpression()");
	        	
	        	ID<RegisterBlueprint> v = parseTerm(), pv, dst;
	            char n;
	            boolean eq;
	            
	            for(;;) {
	                str.munchSpace();
	                if(str.nibble('<')) {
	                	eq = str.nibble('=');
	                	
	                	pv = parseTerm();
	                	/*if(isConstant(pv) && isConstant(v)) {
	                		v.set(eq ? v.getNumber() <= pv.getNumber() : v.getNumber() < pv.getNumber());
	                    	sc.removeTempVar(pv);
	                    }
	                    else {*/
	                    	dst = (!isConstant(v)) ? v : pv;
	                
	                    	if(eq)
	                    		dst = v;
	                    	else if(!isTempBuffer(dst))
                    			dst = addTempBuffer();
	                    	//TODO: addTask(MiscAction.createCompareTask(dst, v,pv, C_LESS_EQUAL));
		                    v = dst;
	                    //}
	                }
	                else if((n = str.nibbleMisc('+', '-')) != StringExt.NULL_CHAR) // addition                 
	                    v = parseOperation(str.nibble('='), v, parseTerm(), n);
	                else
	                	break;
	            }
	            
	            Log.println(Log.ID.PML, false, ""+v);
	            
	            return v;
	        }

	        ID<RegisterBlueprint> parseOperation(boolean eq, ID<RegisterBlueprint> v, ID<RegisterBlueprint> pv, char operation) {
	        	Log.println(Log.ID.PML, true, "parseOperation(" + operation + ")");
	        	
	        	ID<RegisterBlueprint> dst;
	        	
                /*if((pv = parseTerm()).isConstant() && v.isConstant()) {
                	v.operation(operation+"=",pv);
                	sc.removeTempVar(pv);
                }
                else {*/
                	//dst = (!isConstant(v)) ? v : pv;
                	//dst = eq ? v : (!isTempBuffer(dst) ? sc.addTempBuffer() : dst);
                
	        	
	        		dst = addTempBuffer();
	        		
	        		addTask("_"+operation, dst, v,pv);
	        		if(eq)
	        			addTask("_get", v,	 dst);
	        	
                    v = dst;
                //}
            
	        	Log.println(Log.ID.PML, false, "");

                return v;
	        }
	        
	        ID<RegisterBlueprint> parseTerm() {
	        	ID<RegisterBlueprint> v = parseFactor(), dst, pv;
	            
	        	boolean didEq, eq = false;
	            String n;
	            char nc;
	            
	            if(v == null)
	            	return v;
	            for(;;) {
	                str.munchSpace();
	                
	                didEq = false;
	                
	                if(!str.startsWith("=="))
		                if(str.nibble('=')) {
		                	if(isConstant(v))
		                		throw new UnsupportedOperationException();

		                	didEq = true;
		                	
		                	pv = parseExpression();
		                	dst = addTempBuffer();

		                	addTask("_get", v, pv);
		                	addTask("_get", dst, v);

		                	v = dst;
		                	
		                }
	                
	                if(!didEq)
	                	if((n = str.nibbleMisc("<", ">", "==", "!=")) != StringExt.NULL_STRING) {
	                		if(n != "==" && n != "!=")
	                			if(str.nibble("="))
	                				n += "=";
	                		
	                		v = addTask("_"+n, addTempBuffer(),		v, parseTerm());
	                	}
	                	else if(str.nibble('/')) // division
	                		v = parseOperation(str.nibble('='), v, parseTerm(), '/');
		                else if((nc = str.nibbleMisc('*', '(')) != StringExt.NULL_CHAR) { // multiplication
		                	eq = false;
		                	if(nc == '*')
		                		eq = str.nibble('=');
			                
		                	v = parseOperation(eq, v, parseTerm(), '*');
		                } 
		                else if(str.nibble('%'))
		                	v = parseOperation(str.nibble('='), v, parseTerm(), '%');
		                else
		                    return v;
	            }
	        }
	        
	        

	        ID<RegisterBlueprint> parseFactor() {
	        	Log.println(Log.ID.PML, true, "parseFactor()");

	        	ID<RegisterBlueprint> v, dst;
	            char n;
	            boolean isVar = false, negate = false;
                str.munchSpace();
	            if((n = str.nibbleMisc('+','-')) != StringExt.NULL_CHAR) { // unary plus & minus
	                negate = n == '-';
	                str.munchSpace();
	            }
	            if(str.nibble('(')) { // brackets
	                v = parseExpression();
	                str.chomp(')');
	            } else if(c() == StringExt.NULL_CHAR || c() == ')' || c() == '}') {
    	        	Log.println(Log.ID.PML, false, "Returned null");
	            	return null;
	            }
	            else { // numbers/variables
	                StringBuilder sb = new StringBuilder();
	                String s;
	                byte type;
	                
	                if((n = str.nibbleMisc('$','#','"','@','&')) != StringExt.NULL_CHAR || Character.isAlphabetic(c())) {
        	        	Log.println(Log.ID.PML, true, "Getting value...");
	                	if(n == '$')		type = P_VARIABLE;
	                	else if(n == '#') {
	                		if(str.nibble('#'))
	                			type = P_NEW_ACTION;
                			else
    	                		type = P_ACTION;
	                	}
	                	else if(n == '@')	type = P_OBJECT;
	                	else if(n == '&')	type = P_NEW_OBJECT;
	                	else if(n == '\"')	type = P_STRING;	
	                	else				type = P_CONSTANT;
	                	
	                	if(type == P_STRING)
	                		s = str.munchTo('\"');
	                	else {
	        	        	s = parseVarName();
	                	}
	                	
	    	        	Log.println(Log.ID.PML, false, "");
	                }
                	else {
        	        	Log.println(Log.ID.PML, true, "Building Number...");

        	        		type = V_NUMBER;       	        	
	        	        	Log.println(Log.ID.PML, c());
			                while ((c() >= '0' && c() <= '9') || c() == '.') {
			                    sb.append(c());
			                    str.munchChar();		
			                    
			                    Log.println(Log.ID.PML, c());
			                }
			                s = sb.toString();
	        	        	Log.println(Log.ID.PML, false, "Got \"" + s + "\"");
                	}
	                
	                v = parseValue(s, type);	                
	            }
	            
	            str.munchSpace();
	            if(str.nibble('^')) // exponentiation
		            v = parseOperation(str.nibble('='), v, parseFactor(), '^');
	            if(negate)
	            	v = parseOperation(str.nibble('='), v, addConstant(-1), '*');
	            //if(inverse)
	            //	v = parseOperation(nibble('='), v, sc.addConstant(-1), "*");

	        	Log.println(Log.ID.PML, false, ""+v);

	            return v;
	        }
	        
	    	
	    	protected ID<RegisterBlueprint> addRegister(String varName, boolean isConstant, boolean isTrue) {
	    		return regIDMap.add(varName, new RegisterBlueprint(isConstant, isTrue));
	    	}	    	
	    	protected ID<RegisterBlueprint> addRegister(boolean isConstant, boolean isTrue) {
	    		return regIDMap.add(new RegisterBlueprint(isConstant, isTrue));
	    	}
	    	
	    	protected ID<RegisterBlueprint> setRegister(ID<RegisterBlueprint> regID, boolean value) {regID.get().get().set(value);	return regID;}
	    	protected ID<RegisterBlueprint> setRegister(ID<RegisterBlueprint> regID, double value) 	{regID.get().get().set(value);	return regID;}
	    	protected ID<RegisterBlueprint> setRegister(ID<RegisterBlueprint> regID, String value) 	{regID.get().get().set(value);	return regID;}
	    	protected ID<RegisterBlueprint> setRegister(ID<RegisterBlueprint> regID, Object value) 	{regID.get().get().set(value);	return regID;}
	    	

	    	ID<RegisterBlueprint> addConstant() {return addRegister(true,false);}
	    	ID<RegisterBlueprint> addConstant(boolean value)	{return setRegister(addConstant(), value);}
	    	ID<RegisterBlueprint> addConstant(double value) 	{return setRegister(addConstant(), value);}
	    	ID<RegisterBlueprint> addConstant(String value)		{return setRegister(addConstant(), value);}
	    	ID<RegisterBlueprint> addConstant(Object value)		{return setRegister(addConstant(), value);}
	    	
	    	ID<RegisterBlueprint> addTrueVariable(String name) {return addRegister(name, false,true);}
	    	ID<RegisterBlueprint> addTempBuffer() {return addRegister(false,false);}
	    	
	    	void removeRegister(ID<RegisterBlueprint> id) {
	    		regIDMap.remove(id);
	    	}
	    	
	    	protected ID<RegisterBlueprint> getTrueVariable(String varName) {
	    		if(regIDMap.contains(varName))		    		
		    		return regIDMap.findID(varName);
	    		else {
	    			return addTrueVariable(varName);
	    		}
	    	}
	    	
	        
	        ID<RegisterBlueprint> parseValue(final String sb, byte type) {
	        	Log.println(Log.ID.PML, true, "parseValue("+sb+")");
	        	
	        	ID<RegisterBlueprint> out;

	        	if(type == P_NEW_OBJECT) {
	        		ID<RegisterBlueprint> output = addTempBuffer(), outputVar, name = addConstant(sb);

	        		if(str.nibble(".$")) {
		        		addTask("_create", output, name);

		        		StringBuilder s = new StringBuilder();
	                	do {
	                		s.append((char) c());
	                		str.munchChar();
	                	} while(Character.isAlphabetic(c()));
	                	
	                	ID<RegisterBlueprint> v = parseValue(s.toString(), P_VARIABLE);
	                	opo();
	                	
	                	out = v;
	        		}
	        		else if(str.nibble('(')) {
	    	        	Log.println(Log.ID.PML, true, "Parsing object arguments...");
		        			List<ID<RegisterBlueprint>> args = new ArrayList<ID<RegisterBlueprint>>();
		        			args.add(name);
		        			parseParameters(args);
		        			str.chomp(')');	
	    	        	Log.println(Log.ID.PML, false, "");

	        			ID<RegisterBlueprint>[] argsA = new ID[args.size()];
	        			args.toArray(argsA);
	        			
		        		addTask("_create", output, argsA);
		        		
		        		// Add Body
		        		str.munchSpace();
		        		if(str.nibble('{')) {
		    	        	Log.println(Log.ID.PML, true, "Parsing object substatements...");

	        				opu(output);
		        			parseStatements();
		        			opo();
		        			str.chomp('}');
		        			
		    	        	Log.println(Log.ID.PML, false, "");
		        			
		        			out = null;
		        		}
	        		}
	        		
	        		else {	
		        		addTask("_create", output, name);

	        			str.munchSpace();
	        			if(str.nibble('{')) {
	        				opu(output);
		        			parseStatements();
		        			opo();
		        			str.chomp('}');
		        			out = null;
		        		}
	        		}
	        		
	        		out = output;
	        	}
	        	else if(type == P_VARIABLE) {

	        		if(o == null)
	        			out = getTrueVariable(sb);
	        		else {
	        			ID<RegisterBlueprint> outputVar = addTempBuffer();
	        			addTask("_accessVar", outputVar, o, addConstant(sb));
	        			return outputVar;
	        		}
	        	}
	        	else if(type == P_NUMBER) {
        			out = addConstant(Double.parseDouble(sb));
	        	}
	        	else if(type == P_NEW_ACTION) {	        		
        			parseScript(sb);
        			
        			out = null;
	        	}
        		else if(type == P_ACTION) {
        			List<ID<RegisterBlueprint>> parameters = new ArrayList<ID<RegisterBlueprint>>();

        			str.chomp('(');
        				parseParameters(parameters);
        			str.chomp(')');
        			
        			ID<RegisterBlueprint>[] parArray = new ID[parameters.size()];
        			parameters.toArray(parArray);
        			parameters.clear();
        			
        			
        			// TODO: add proper checking for whether or not output is null
        			ID<RegisterBlueprint> output = addTempBuffer();
        			addTask(sb, output, parArray);
        			        			
        			out = output;
        		}
        		else if(type == P_STRING) {
        			out = addConstant(sb.replace("\\n", "\n")) ;
        		}
        		else if(type == P_CONSTANT) {        			
    				switch(sb) {
        				case "TRUE": 	out = addConstant(true);		break;
        				case "FALSE": 	out = addConstant(false);		break;
        				case "PI": 		out = addConstant(Math.PI);		break;
        				case "TAU": 	out = addConstant(2*Math.PI);	break;
           				case "E": 		out = addConstant(Math.E);	break;
        				default: 		out = null;
        			}
        		}
        		else
        			out = null;
	        	
		        Log.println(Log.ID.PML, false, "" + out);
		        
		        return out;
	        }
	    }

	    
	    Script s = new Parser().parse();	    

		Log.println(Log.ID.PML, false, "");

		return s;
	    /*System.out.println("Created new script: ");
	    System.out.println("\tTasks: " + sc.taskList.size());
	    	for(Task t : sc.taskList)
	    		System.out.println("\t\t" + t.getAction().getName());
	    System.out.println("\tRegisters: " + sc.tempVars.size());*/
	}
	
	public static void ini() {
		
	}
}
