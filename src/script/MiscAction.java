package script;

import obj.env.blk.AirBlock;
import obj.env.blk.GroundBlock;
import object.primitive.Positionable;
import paper.Boundary;
import paper.EnemyPM;
import paper.NPCPM;
import paper.PlayerPM;
import paper.SpinningEyes;
import paper.Switch;
import resource.model.Model;
import resource.sound.Sound;
import btl.BattleController;
import btl.BattleEnvironment;
import ds.nameid.ID;
import functions.Math2D;
import functions.MathExt;
import gfx.GL;
import gfx.ModelRenderer;
import gfx.OKPopup;

public class MiscAction extends Action {
	public MiscAction(PMLMemory mem, String name, int regNum, int[] argIndList, ConstantRegister[] constList, boolean hasOutput) {
		super(mem, name, regNum, argIndList, constList, hasOutput);
	}

	public static void ini(PMLMemory mem) {
		int[]	arg1 = {0},
				arg2 = {0, 1},
				arg4 = {0, 1, 2, 3};
		
		new MiscAction(mem, "println", 1, arg1, null, false);
		
		new MiscAction(mem, "popupOK", 1, arg1, null, false);

		new MiscAction(mem, "_get", 1, arg1, null, true);

		new MiscAction(mem, "_if", 1, arg1, null, true);
		new MiscAction(mem, "_move", 1, arg1, null, true);

		new MiscAction(mem, "_<", 2, arg2, null, true);
		new MiscAction(mem, "_<=", 2, arg2, null, true);
		new MiscAction(mem, "_>", 2, arg2, null, true);
		new MiscAction(mem, "_>=", 2, arg2, null, true);
		new MiscAction(mem, "_==", 2, arg2, null, true);
		new MiscAction(mem, "_!=", 2, arg2, null, true);

		new MiscAction(mem, "_+", 2, arg2, null, true);
		new MiscAction(mem, "_-", 2, arg2, null, true);
		new MiscAction(mem, "_*", 2, arg2, null, true);
		new MiscAction(mem, "_/", 2, arg2, null, true);
		new MiscAction(mem, "_^", 2, arg2, null, true);
		new MiscAction(mem, "_%", 2, arg2, null, true);
		new MiscAction(mem, "_&&", 2, arg2, null, true);
		new MiscAction(mem, "_||", 2, arg2, null, true);
		new MiscAction(mem, "_!", 1,	arg1, null, true);
		
		new MiscAction(mem, "_return", 1, arg1, null, true);
		
		new MiscAction(mem, "sinr", 1, arg1, null, true);
		new MiscAction(mem, "sind", 1, arg1, null, true);
		new MiscAction(mem, "cosr", 1, arg1, null, true);
		new MiscAction(mem, "cosd", 1, arg1, null, true);
		new MiscAction(mem, "tanr", 1, arg1, null, true);
		new MiscAction(mem, "tand", 1, arg1, null, true);
		
		new MiscAction(mem, "asinr", 1, arg1, null, true);
		new MiscAction(mem, "asind", 1, arg1, null, true);
		new MiscAction(mem, "acosr", 1, arg1, null, true);
		new MiscAction(mem, "acosd", 1, arg1, null, true);
		new MiscAction(mem, "atanr", 1, arg1, null, true);
		new MiscAction(mem, "atand", 1, arg1, null, true);

		new MiscAction(mem, "fact", 1, arg1, null, true);

		new MiscAction(mem, "nPr", 2, arg2, null, true);
		new MiscAction(mem, "nCr", 2, arg2, null, true);

		new MiscAction(mem, "sqrt", 1, arg1, null, true);
		
		new MiscAction(mem, "ln", 1, arg1, null, true);

		new MiscAction(mem, "mean", 1, arg1, null, true);
		new MiscAction(mem, "max", 1, arg1, null, true);
		new MiscAction(mem, "min", 1, arg1, null, true);
		
		new MiscAction(mem, "ptDis", 4, arg4, null, true);
		new MiscAction(mem, "ptDir", 4, arg4, null, true);

		new MiscAction(mem, "createBoundary", 1, arg1, null, true);
		new MiscAction(mem, "playMusic", 1, arg1, null, true);
		new MiscAction(mem, "_create", 1, arg1, null, true);
		new MiscAction(mem, "_accessVar", 1, arg1, null, true);		
	}

	
	
	@Override
	public void call(Register output, Register[] parameters, PMLMemory stack) {
		int len = parameters.length;
		
		double a, b;
		switch(name) {
			case "popupOK":		OKPopup.open(parameters[0].toPureString());
								return;
		
			case "println":		if(len > 0)
									for(Register r : parameters)
										System.out.print(r.toPureString() + " ");
								System.out.println();
								return;


			case "sqrt":		output.set(Math.sqrt(parameters[0].getNumber()));							return;

			case "rnd":			output.set(MathExt.rndf());
								return;
								
			case "mean":		a = 0;
								b = parameters.length;
								for(Register v : parameters)
									a += v.getNumber();
								output.set(a/b);
								return;
								
			case "min":			a = Double.MAX_VALUE;
								for(Register v : parameters)
									a = ((b=v.getNumber()) < a) ? b : a;
								output.set(a);
								return;
								
			case "max":			a = Double.MIN_VALUE;
								for(Register v : parameters)
									a = ((b=v.getNumber()) > a) ? b : a;
								output.set(a);
								return;
								
			
			case "_>":		output.set(parameters[0].getNumber() > parameters[1].getNumber());				return;
			case "_>=":		output.set(parameters[0].getNumber() >= parameters[1].getNumber());				return;
			case "_<":		output.set(parameters[0].getNumber() < parameters[1].getNumber());				return;
			case "_<=":		output.set(parameters[0].getNumber() <= parameters[1].getNumber());				return;
			case "_==":		output.set(parameters[0].getNumber() == parameters[1].getNumber());				return;
			case "_!=":		output.set(parameters[0].getNumber() != parameters[1].getNumber());				return;

			case "_||":		output.set(parameters[0].getBoolean() || parameters[1].getBoolean());			return;
			case "_&&":		output.set(parameters[0].getBoolean() && parameters[1].getBoolean());			return;
			case "_!":		output.set( !parameters[0].getBoolean() );										return;

			
			case "_+":		output.set(parameters[0]); 	output.adde(parameters[1]);							return;
			case "_-":		output.set(parameters[0].getNumber() - parameters[1].getNumber());				return;
			case "_*":		output.set(parameters[0].getNumber() * parameters[1].getNumber());				return;
			case "_/":		output.set(parameters[0].getNumber() / parameters[1].getNumber());				return;
			case "_^":		output.set(Math.pow(parameters[0].getNumber(),parameters[1].getNumber()));		return;
			case "_%":		output.set( parameters[0].getNumber() % parameters[1].getNumber() );			return;
											
			case "sinr":	output.set(Math2D.sinr(parameters[0].getNumber()));							return;
			case "sind":	output.set(Math2D.sind(parameters[0].getNumber()));							return;
			case "cosr":	output.set(Math2D.cosr(parameters[0].getNumber()));							return;
			case "cosd":	output.set(Math2D.cosd(parameters[0].getNumber()));							return;
			case "tanr":	output.set(Math.tan(parameters[0].getNumber()));								return;
			case "tand":	output.set(Math.tan(parameters[0].getNumber()/180*3.14159265));					return;
			
			case "ln":		output.set( Math.log(parameters[0].getNumber()) );	return;
			
			case "_if":		output.set(parameters[0].getBoolean() == true ? 1 : 0);	return;

			case "_return":
			case "_get":
			case "_move":	output.set(parameters[0]);	return;

			
			
			case "_accessVar":	if(parameters.length != 2)
									throw new UnsupportedOperationException("The function accessVar() requires two arguments.");
								Register v = output.point(((Positionable) parameters[0].getObject()).getVar(parameters[1].getString()));
								//output.set(v);
								return;

			case "_create":		if(parameters.length < 1)
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
									
									case "Enemy":
										o = new EnemyPM(parameters[1].getString(), (float) parameters[2].getNumber(), (float) parameters[3].getNumber(), (float) parameters[4].getNumber());
										break;

									case "NPC":
										o = new NPCPM(parameters[1].getString(), (float) parameters[2].getNumber(), (float) parameters[3].getNumber(), (float) parameters[4].getNumber());
										break;
										
									case "SpinningEyes":	o = new SpinningEyes(0,0,0); 	break;
									case "Switch":			o = new Switch(0,0,0); 			break;
									case "GroundBlock":		o = new GroundBlock(0,0,0); 	break;
									case "AirBlock":
										if(parameters.length == 4)
											o = new AirBlock((float) parameters[1].getNumber(), (float) parameters[2].getNumber(), (float) parameters[3].getNumber(), true);
										else
											o = new AirBlock((float) parameters[1].getNumber(), (float) parameters[2].getNumber(), (float) parameters[3].getNumber(), parameters[4].getBoolean());
										break;
									case "BattleEnvironment":	o = new BattleEnvironment(parameters[1].getString());	break;
									case "ModelRenderer":		o = new ModelRenderer(parameters[1].getString());	break;
									case "BattleController":	o = new BattleController();	break;
									default:	throw new UnsupportedOperationException("The function create() was passed an invalid object, " + parameters[0] + ".");
								}
			
								output.set(o);
								return;

			case "createBoundary":
								if(parameters.length % 2 != 0)
									throw new UnsupportedOperationException("The function createBoundary() requires sets of coordinates (x,y)--the last set is incomplete.");
								if(parameters.length < 4)
									throw new UnsupportedOperationException("The function createBoundary() requires more than 1 coordinate.");
								int numPts;
								len = parameters.length;
								numPts = parameters.length/2;
								float[][] pts = new float[numPts][2];
								
								for(int i = 0, k = 0; i < len; i += 2, k++) {
									pts[k][0]	= (float) parameters[i].getNumber();
									pts[k][1] = (float) parameters[i+1].getNumber();
								}
								
								output.set(new Boundary(pts));
								return;
								
			case "playMusic":
								int le = parameters.length;
								if(le < 1 || le > 5)
									throw new UnsupportedOperationException("The function playMusic() requires one, two, or three arguments.");
				
								switch(le) {
									/*case 1: Sound.playMusic(parameters[0].getString()); 
										break;
									case 2: Sound.playMusic(parameters[0].getString(), parameters[1].getString());	
										break;
									case 3: Sound.playMusic(parameters[0].getString(), parameters[1].getString(), (float) parameters[2].getNumber());
										break;
									case 4: Sound.playMusic(parameters[0].getString(), (float) parameters[1].getNumber(), parameters[2].getBoolean(), parameters[3].getBoolean());
										break;
									case 5: Sound.playMusic(parameters[0].getString(), parameters[1].getString(), (float) parameters[2].getNumber(), parameters[3].getBoolean(), parameters[4].getBoolean());
										break;*/
								}
								return;
								
			default:
				throw new RuntimeException("The misc. action " + getName() + " was not defined.");
		}
	}
}
