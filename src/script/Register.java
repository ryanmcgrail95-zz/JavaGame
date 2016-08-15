package script;

import java.util.HashMap;
import java.util.Map;

import cont.Log;
import ds.lst.CleanList;
import gfx.GL;

public class Register extends PML {	
	private boolean isConstant, isTrueVariable;
	private byte type = V_NONE;
	private double numValue;
	private boolean boolValue;
	private String strValue;
	private Object objValue;
	
	private String name;
	

	public Register() {}
	public Register(String name, boolean isConstant, boolean isTrueVariable) {
		this.name = name;
		this.isConstant = isConstant;
		this.isTrueVariable = isTrueVariable;
	}
	public Register(RegisterBlueprint r) {
		Register val = r.get();
				
		Log.println(Log.ID.PML, true, "Register( " + r + " )");
		
		this.name = val.name;
		this.set(val);
		this.isConstant = val.isConstant;
		this.isTrueVariable = val.isTrueVariable;
		
		Log.println(Log.ID.PML, false, "");
	}
	
	public Register set(double newValue) {
		Log.println(Log.ID.PML, true, "Register.set("+newValue+")");
		if(type == V_POINTER)
			((Register) objValue).set(newValue);
		else {
			type = V_NUMBER;
			numValue = newValue;
		}
		Log.println(Log.ID.PML, false, "");

		return this;
	}
	public Register set(boolean newValue) {
		if(type == V_POINTER)
			((Register) objValue).set(newValue);
		else {
			type = V_BOOLEAN;
			boolValue = newValue;
		}
		return this;
	}
	public Register set(Object newValue) {
		if(type == V_POINTER)
			((Register) objValue).set(newValue);
		else {
			type = V_OBJECT;
			objValue = newValue;
		}
		return this;
	}
	public Register set(String newValue) {
		if(type == V_POINTER)
			((Register) objValue).set(newValue);
		else {
			type = V_STRING;
			strValue = newValue;
		}
		return this;
	}
	public Register set(Action newValue) {
		if(type == V_POINTER)
			((Register) objValue).set(newValue);
		else {
			type = V_ACTION;
			objValue = newValue;
		}
		return this;
	}
	public Register set(Register other) {		
		if(type == V_POINTER)
			((Register) objValue).set(other);
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
	
	public Register point(Register other) {
		if(other == null)
			throw new UnsupportedOperationException("Attempting to point to null register!");
		else if(other == this)
			throw new UnsupportedOperationException("Attempting to point to self!");

		type = V_POINTER;
		objValue = other;
		name = "Pointer to " + other.name;

		return this;
	}
	
	public void destroy() {
		objValue = null;
		strValue = null;
	}
	
		
	/*public Variable add(Variable other) {
		return copy().adde(other);
	}*/
	public Register adde(Register other) {
		switch(type) {
			case V_NUMBER: 	if(other.type == V_STRING)
								set("" + get() + other.get());
							else
								numValue += other.numValue;	
							break;
			case V_STRING: strValue += other.get();	
							break;
		}
		return this;
	}
	/*public Variable add(double other) {
		return copy().multe(other);
	}*/
	public Register adde(double other) {
		switch(type) {
			case V_NUMBER: numValue += other;		break;
			case V_STRING: strValue += other;	break;
		}
		return this;
	}
	
	/*public Variable sub(Variable other) {
		return copy().sube(other);
	}*/
	public Register sube(Register other) {
		switch(type) {
			case V_NUMBER: numValue -= other.numValue;	break;
		}
		return this;
	}
	/*public Variable sub(double other) {
		return copy().sube(other);
	}*/
	public Register sube(double other) {
		switch(type) {
			case V_NUMBER: numValue -= other;		break;
		}
		type = V_NUMBER;
		return this;
	}
	
	
	public Register dive(Register other) {
		switch(type) {
			case V_NUMBER: numValue /= other.numValue;	break;
		}
		return this;
	}
	public Register dive(double other) {
		switch(type) {
			case V_NUMBER: numValue /= other;		break;
		}
		type = V_NUMBER;
		return this;
	}
	
	
	public Register multe(Register other) {
		switch(type) {
			case V_NUMBER: numValue *= other.numValue;	break;
		}
		return this;
	}
	public Register multe(double other) {
		switch(type) {
			case V_NUMBER: numValue *= other;		break;
		}
		type = V_NUMBER;
		return this;
	}
	
	public Register powe(Register other) {
		switch(type) {
			case V_NUMBER: numValue = Math.pow(numValue, other.numValue);	break;
		}
		return this;
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

	private Object get() {
		switch(type) {
			case V_NUMBER:	return numValue;
			case V_BOOLEAN:	return boolValue;
			case V_STRING:	return strValue;
			case V_OBJECT:	return objValue;
			case V_POINTER:	return ((Register)objValue).get();
			default:		return null;
		}
	}
	
	public String toString() {
		String s = "[ Register["+type+"] @" + super.toString() + ": " + toPureString() + "]";
		
		return s;
	}
	
	public String toPureString() {		
		switch(type) {
			case V_NONE:	return "(Unset.)";
			case V_NUMBER:	return ""+numValue;
			case V_BOOLEAN:	return ""+boolValue;
			case V_STRING:	return ""+strValue.toString();
			case V_OBJECT:	return ""+objValue.toString();
			case V_POINTER:	return ""+"Pointer(" + ((Register)objValue).getName() + ")";
			default:
				return "";
		}
	}
	
	public boolean isConstant() {return isConstant;}
	public boolean isTrueVariable() {return isTrueVariable;}
	public boolean isTempBuffer() {return !isConstant() && !isTrueVariable();}

	public Object getObject() {
		if(!isObject())
			throw new UnsupportedOperationException("Variable \"" + this + "\" is not an object.");
		return objValue;
	}

	public String getString() {
		if(!isString())
			throw new UnsupportedOperationException("Variable \"" + this + "\" is not a string.");
		return strValue;
	}
	
	public boolean getBoolean() {
		if(!isBoolean())
			throw new UnsupportedOperationException("Variable \"" + this + "\" is not a boolean.");
		return boolValue;
	}

	public Register operation(String operation, Register other) {
		switch(operation) {
			case "+=":	return this.adde(other);
			case "-=":	return this.sube(other);
			case "*=":	return this.multe(other);
			case "/=":	return this.dive(other);
			
			default:	throw new RuntimeException("Operation does not exist.");
		}
	}
}
