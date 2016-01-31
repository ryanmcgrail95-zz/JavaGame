package script;

import obj.env.blk.AirBlock;
import obj.env.blk.GroundBlock;
import object.primitive.Positionable;
import paper.Boundary;
import paper.PlayerPM;
import paper.SpinningEyes;
import paper.Switch;
import resource.model.Model;
import functions.FastMath;
import functions.MathExt;
import gfx.ModelRenderer;

public class MiscAction extends Action {
	private final static int M_PRINTLN = 0, M_RETURN = 1, M_MIN = 2, M_ADD = 3, M_SUB = 4, M_MULT = 5, M_DIV = 6, M_POW = 7, M_MAX = 8, M_SET = 9, M_SINR = 10, M_SIND = 11, M_COSR = 12, M_COSD = 13, M_MEAN = 14, M_TASK_MOVE = 15, M_CREATE = 16, M_ACCESSVAR = 17, M_IF = 18, M_COMPARE = 19, M_CREATEBOUNDARY = 20;
	private int miscType;
	
	public MiscAction(String name, boolean hasOutput, int type) {
		super(name, null, hasOutput);
		miscType = type;
	}

	public static void ini() {
		new MiscAction("println", false, M_PRINTLN);
		new MiscAction("min", true, M_MIN);
		new MiscAction("max", true, M_MAX);
		
		new MiscAction("taskMove", false, M_TASK_MOVE);

		new MiscAction("add", true, M_ADD);
		new MiscAction("sub", true, M_SUB);
		new MiscAction("mult", true, M_MULT);
		new MiscAction("div", true, M_DIV);
		new MiscAction("pow", true, M_POW);
		
		new MiscAction("set", true, M_SET);
		
		new MiscAction("return", true, M_RETURN);
		
		new MiscAction("sinr", true, M_SINR);
		new MiscAction("sind", true, M_SIND);
		new MiscAction("cosr", true, M_COSR);
		new MiscAction("cosd", true, M_COSD);
		
		new MiscAction("create", true, M_CREATE);
		
		new MiscAction("accessVar", true, M_ACCESSVAR);
		
		new MiscAction("if", true, M_IF);

		new MiscAction("createBoundary", true, M_CREATEBOUNDARY);
	}
	
	public boolean isReturn() {return miscType == M_RETURN;}
	public boolean isTaskMove() {return miscType == M_TASK_MOVE;}
	
	public static Task createAddTask(Variable dst, Variable a, Variable b) {
		return new Task(getAction("add"), new Variable[] {a, b}, dst);
	}
	public static Task createSubtractTask(Variable dst, Variable a, Variable b) {
		return new Task(getAction("sub"), new Variable[] {a, b}, dst);
	}
	public static Task createMultiplyTask(Variable dst, Variable a, Variable b) {
		return new Task(getAction("mult"), new Variable[] {a, b}, dst);
	}
	public static Task createDivideTask(Variable dst, Variable a, Variable b) {
		return new Task(getAction("div"), new Variable[] {a, b}, dst);
	}
	public static Task createExponentTask(Variable dst, Variable a, Variable b) {
		return new Task(getAction("pow"), new Variable[] {a, b}, dst);
	}
	public static Task createSetTask(Variable dst, Variable a) {
		return new Task(getAction("set"), new Variable[] {a}, dst);
	}

	public static Task createTaskMoveTask(Variable a) {
		return new Task(getAction("taskMove"), new Variable[] {a}, null);
	}
	public static Task createIfTask(Variable dst, Variable a) {
		return new Task(getAction("if"), new Variable[] {a}, dst);
	}
	
	public static Task createCreateTask(Variable dst, Variable type) {
		return new Task(getAction("create"), new Variable[] {type}, dst);
	}
	
	public static Task createAccessVarTask(Variable dst, Variable owner, Variable name) {
		return new Task(getAction("accessVar"), new Variable[] {owner,name}, dst);
	}
	
	public static Task createCompareTask(Variable dst, Variable a, Variable b, Variable type) {
		return new Task(getAction("compare"), new Variable[] {a,b,type}, dst);
	}
	
	
	@Override
	public Variable run(Variable output, Variable[] parameters) {
		double a, b;
		switch(miscType) {
			case M_PRINTLN:		for(Variable v : parameters)
									System.out.print(v + " ");
								System.out.println();
								return null;

			case M_MEAN:		if(parameters.length < 1)
									throw new UnsupportedOperationException("The function mean() requires at least one argument.");
								a = 0;
								b = parameters.length;
								for(Variable v : parameters)
									a += v.getNumber();
								return output.set(a/b);
								
			case M_MIN:			if(parameters.length < 1)
									throw new UnsupportedOperationException("The function min() requires at least one argument.");
								a = Double.MAX_VALUE;
								for(Variable v : parameters)
									a = ((b=v.getNumber()) < a) ? b : a;
								return output.set(a);
								
			case M_MAX:			if(parameters.length < 1)
									throw new UnsupportedOperationException("The function max() requires at least one argument.");
								a = Double.MIN_VALUE;
								for(Variable v : parameters)
									a = ((b=v.getNumber()) > a) ? b : a;
								return output.set(a);
								
			case M_ADD:			return output.set(parameters[0].getNumber() + parameters[1].getNumber());
			case M_SUB:			return output.set(parameters[0].getNumber() - parameters[1].getNumber());
			case M_MULT:		return output.set(parameters[0].getNumber() * parameters[1].getNumber());
			case M_DIV:			return output.set(parameters[0].getNumber() / parameters[1].getNumber());
			case M_POW:			return output.set(Math.pow(parameters[0].getNumber(),parameters[1].getNumber()));

			case M_SET:			if(parameters.length != 1)
									throw new UnsupportedOperationException("The function set() requires one argument.");
								return output.set(parameters[0]);
								
			case M_SINR:		if(parameters.length != 1)
									throw new UnsupportedOperationException("The function sinr() requires one argument.");
								return output.set(FastMath.sin(parameters[0].getNumber()));
			case M_SIND:		if(parameters.length != 1)
									throw new UnsupportedOperationException("The function sind() requires one argument.");
								return output.set(FastMath.sind(parameters[0].getNumber()));
			case M_COSR:		if(parameters.length != 1)
									throw new UnsupportedOperationException("The function cosr() requires one argument.");
								return output.set(FastMath.cos(parameters[0].getNumber()));
			case M_COSD:		if(parameters.length != 1)
									throw new UnsupportedOperationException("The function cosd() requires one argument.");
								return output.set(FastMath.cosd(parameters[0].getNumber()));
			
			case M_RETURN:		if(parameters.length != 1)
									throw new UnsupportedOperationException("The function return() requires one argument.");
								return parameters[0];
								
			case M_TASK_MOVE:	if(parameters.length != 1)
									throw new UnsupportedOperationException("The function taskMove() requires one argument.");
								return parameters[0];
								
			case M_IF:			if(parameters.length != 1)
									throw new UnsupportedOperationException("The function if() requires one argument.");
								return output.set(parameters[0].getBoolean() ? 1 : 0);
								
			case M_ACCESSVAR:	if(parameters.length != 2)
									throw new UnsupportedOperationException("The function accessVar() requires two arguments.");
								Variable v = output.point(((Positionable) parameters[0].getObject()).getVar(parameters[1].getString()));
								return v;

			case M_CREATE:		if(parameters.length < 1)
									throw new UnsupportedOperationException("The function create() requires at least one argument.");
								
								Object o;
								switch(parameters[0].getString()) {
									case "Player":
										int parLen = parameters.length;
										if(parLen == 1)
											o = PlayerPM.create();
										else
											o = PlayerPM.create((float) parameters[1].getNumber(), (float) parameters[2].getNumber(), (float) parameters[3].getNumber());
										break;
									case "SpinningEyes":	o = new SpinningEyes(0,0,0); 	break;
									case "Switch":			o = new Switch(0,0,0); 			break;
									case "GroundBlock":		o = new GroundBlock(0,0,0); 	break;
									case "AirBlock":		o = new AirBlock(0,0,0); 		break;
									case "ModelRenderer":	o = new ModelRenderer(parameters[1].getString());	break;
									default:	throw new UnsupportedOperationException("The function create() was passed an invalid object, " + parameters[0] + ".");
								}
			
								return output.set(o);

			case M_CREATEBOUNDARY:
								if(parameters.length % 2 != 0)
									throw new UnsupportedOperationException("The function createBoundary() requires sets of coordinates (x,y)--the last set is incomplete.");
								if(parameters.length < 4)
									throw new UnsupportedOperationException("The function createBoundary() requires more than 1 coordinate.");
								int len, numPts;
								len = parameters.length;
								numPts = parameters.length/2;
								float[][] pts = new float[numPts][2];
								
								for(int i = 0, k = 0; i < len; i += 2, k++) {
									pts[k][0]	= (float) parameters[i].getNumber();
									pts[k][1] = (float) parameters[i+1].getNumber();
								}
								
								return output.set(new Boundary(pts));

			case M_COMPARE:		if(parameters.length != 3)
									throw new UnsupportedOperationException("The function compare() requires one argument.");
								
								a = output.getNumber();
								b = output.getNumber();
								
								boolean out = false;
			
								switch((int) parameters[2].getNumber()) {
									case I_EQUAL:			out = a == b; break;
									case I_NOT_EQUAL:		out = a != b; break;
									case I_LESS:			out = a < b; break;
									case I_LESS_EQUAL:		out = a <= b; break;
									case I_GREATER:			out = a > b; break;
									case I_GREATER_EQUAL:	out = a >= b; break;
								}
			
								return output.set(out);
								
			default:
				throw new RuntimeException("The misc. action " + getName() + " was not defined.");
		}
	}

	public boolean isTaskIf() {
		return miscType == M_IF;
	}
}
