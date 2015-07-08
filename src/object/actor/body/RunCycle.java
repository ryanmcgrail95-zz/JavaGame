package object.actor.body;

import Datatypes.vec3;
import functions.MathExt;
import gfx.GOGL;

public class RunCycle extends Cycle {
	
	public RunCycle() {
		super();
		frameNum = 12;
		speed = .3f;

		iniBody();
		iniLegs();
		iniArms();
	}
	
	protected void iniBody() {
		float[] b = {
				0,
				
				5,
				8,
				8,
				5,
				
				0,
				
				0,
				
				5,
				8,
				8,
				5,
				
				0
		};

		bodyX = b; bodyY = b; bodyZ = b;
	}
	protected void iniArms() {
		float[] uA = {
			80,
			
			90,
			100,
			130,
			170,
			
			180,
			180,
			
			170,
			135,
			100,
			80,
			
			75
		},
		lA = {
			-45,
			
			-10,
			15,
			75,
			85,
			
			90,
			90,
			
			75,
			45,
			15,
			-10,
			
			-45
		};
		
		upperArmX = uA; upperArmY = uA; upperArmZ = uA;
		lowerArmX = lA; lowerArmY = lA; lowerArmZ = lA;
	}
	protected void iniLegs() {
		float[] uL = {
			15,
			
			45,
			60,
			75,
			
			90,
			
			105,
			110,
			105,
			
			85,
			50,
			20,
			
			10
		},
		lL = {
			90,
			
			85,
			
			90,
			95,
			120,
			
			135,
			170,
			190,
			
			210,
			210,
			150,
			
			80
		};
		
		upperLegX = lL; upperLegY = uL; upperLegZ = uL;
		lowerLegX = lL; lowerLegY = lL; lowerLegZ = lL;
	}
}
