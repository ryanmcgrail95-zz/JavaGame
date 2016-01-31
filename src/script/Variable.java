package script;

import java.util.HashMap;
import java.util.Map;

import datatypes.lists.CleanList;

public class Variable extends PML {
	private static CleanList<Variable> tempList = new CleanList<Variable>("TempVars");
	public static Map<Script, Map<String,Variable>> varMap = new HashMap<Script, Map<String,Variable>>();
	
	private boolean inVarMap, isConstant, isTrueVariable;
	private byte type;
	private double numValue;
	private boolean boolValue;
	private String strValue;
	private Object objValue;
	
	private String name;
	

	public Variable(String name, boolean isConstant, boolean isTrueVariable) {
		this.name = name;
		inVarMap = true;
		this.isConstant = isConstant;
		this.isTrueVariable = isTrueVariable;
	}
	
	public Variable set(double newValue) {
		if(type == V_POINTER)
			((Variable) objValue).set(newValue);
		else {
			type = V_NUMBER;
			numValue = newValue;
		}
		return this;
	}
	public Variable set(boolean newValue) {
		if(type == V_POINTER)
			((Variable) objValue).set(newValue);
		else {
			type = V_BOOLEAN;
			boolValue = newValue;
		}
		return this;
	}
	public Variable set(Object newValue) {
		if(type == V_POINTER)
			((Variable) objValue).set(newValue);
		else {
			type = V_OBJECT;
			objValue = newValue;
		}
		return this;
	}
	public Variable set(String newValue) {
		if(type == V_POINTER)
			((Variable) objValue).set(newValue);
		else {
			type = V_STRING;
			strValue = newValue;
		}
		return this;
	}
	public Variable set(Action newValue) {
		if(type == V_POINTER)
			((Variable) objValue).set(newValue);
		else {
			type = V_ACTION;
			objValue = newValue;
		}
		return this;
	}
	public Variable set(Variable other) {
		if(type == V_POINTER)
			((Variable) objValue).set(other);
		else
			if(other != null && other != this) {
				type = other.type;
				numValue = other.numValue;
				boolValue = other.boolValue;
				objValue = other.objValue;
				strValue = other.strValue;			
			}
		return this;
	}
	
	public Variable point(Variable other) {
		if(other != null && other != this) {
			type = V_POINTER;
			objValue = other;
			name = "Pointer to " + other.name;
		}
		return this;
	}
	
	public void destroy() {
		objValue = null;
		strValue = null;
	}
	
	
	// Whether or Not is True Variable, Can Use var = whatever
	public boolean canBeSet() {
		return inVarMap;
	}
		
	/*public Variable add(Variable other) {
		return copy().adde(other);
	}*/
	public Variable adde(Variable other) {
		switch(type) {
			case V_NUMBER: numValue += other.numValue;	break;
			case V_STRING: strValue += other.strValue;		break;
		}
		return this;
	}
	/*public Variable add(double other) {
		return copy().multe(other);
	}*/
	public Variable adde(double other) {
		switch(type) {
			case V_NUMBER: numValue += other;		break;
		}
		type = V_NUMBER;
		return this;
	}
	
	/*public Variable sub(Variable other) {
		return copy().sube(other);
	}*/
	public Variable sube(Variable other) {
		switch(type) {
			case V_NUMBER: numValue -= other.numValue;	break;
		}
		return this;
	}
	/*public Variable sub(double other) {
		return copy().sube(other);
	}*/
	public Variable sube(double other) {
		switch(type) {
			case V_NUMBER: numValue -= other;		break;
		}
		type = V_NUMBER;
		return this;
	}
	
	
	public Variable dive(Variable other) {
		switch(type) {
			case V_NUMBER: numValue /= other.numValue;	break;
		}
		return this;
	}
	public Variable dive(double other) {
		switch(type) {
			case V_NUMBER: numValue /= other;		break;
		}
		type = V_NUMBER;
		return this;
	}
	
	
	public Variable multe(Variable other) {
		switch(type) {
			case V_NUMBER: numValue *= other.numValue;	break;
		}
		return this;
	}
	public Variable multe(double other) {
		switch(type) {
			case V_NUMBER: numValue *= other;		break;
		}
		type = V_NUMBER;
		return this;
	}
	
	public Variable powe(Variable other) {
		switch(type) {
			case V_NUMBER: numValue = Math.pow(numValue, other.numValue);	break;
		}
		return this;
	}
	
	public static Variable get(Script sc, String name, boolean isConstant, boolean isTrueVariable) {
		Map<String,Variable> map;
		Variable var;
		
		if(varMap.containsKey(sc))
			map = varMap.get(sc);
		else {
			map = new HashMap<String,Variable>();
			varMap.put(sc, map);
		}
					
		if(map.containsKey(name))
			return map.get(name);
		else {
			var = new Variable(name, isConstant, isTrueVariable);
			map.put(name, var);
			return var;
		}
	}

	public String getName() {return name;}
	
	public double getNumber() {
		if(!isNumber())
			throw new UnsupportedOperationException("Variable \"" + name + "\" is not a number.");
		return numValue;
	}
	
	private boolean isNumber() {return type == V_NUMBER;}
	private boolean isBoolean() {return type == V_BOOLEAN;}
	private boolean isString() {return type == V_STRING;}
	private boolean isObject() {return type == V_OBJECT;}

	public String toString() {
		switch(type) {
			case V_NUMBER:	return "" + numValue;
			case V_BOOLEAN:	return ""+boolValue;
			case V_STRING:	return strValue.toString();
			case V_OBJECT:	return objValue.toString();
			case V_POINTER:	return "Pointer(" + ((Variable)objValue).getName() + ")";
			default:
				return "";
		}
	}
	
	public boolean isConstant() {return isConstant;}
	public boolean isTrueVariable() {return isTrueVariable;}
	public boolean isTempBuffer() {return !isConstant() && !isTrueVariable();}

	public Object getObject() {
		if(!isObject())
			throw new UnsupportedOperationException("Variable \"" + name + "\" is not an object.");
		return objValue;
	}

	public String getString() {
		if(!isString())
			throw new UnsupportedOperationException("Variable \"" + name + "\" is not a string.");
		return strValue;
	}
	
	public boolean getBoolean() {
		if(!isBoolean())
			throw new UnsupportedOperationException("Variable \"" + name + "\" is not a boolean.");
		return boolValue;
	}

	public Variable operation(String operation, Variable other) {
		switch(operation) {
			case "+=":	return this.adde(other);
			case "-=":	return this.sube(other);
			case "*=":	return this.multe(other);
			case "/=":	return this.dive(other);
			
			default:	throw new RuntimeException("Operation does not exist.");
		}
	}
}
