package object.actor.body;

import ds.vec3;
import functions.MathExt;
import gfx.GOGL;

public class StandCycle extends Cycle {
	
	public StandCycle() {
		super();
		frameNum = 1;
		speed = .15f;

		iniBody();
		iniLegs();
		iniArms();
	}
	
	protected void iniBody() {
		float[] b = {
			-90
		};
		
		for(int i = 0; i < b.length; i++)
			b[i] += 90;

		bodyX = b; bodyY = b; bodyZ = b;
	}
	protected void iniArms() {
		float[] uA = {
			90
		},
		lA = {
			90
		};
		
		upperArmX = uA; upperArmY = uA; upperArmZ = uA;
		lowerArmX = lA; lowerArmY = lA; lowerArmZ = lA;
	}
	protected void iniLegs() {
		float[] uL = {
			90
		},
		lL = {
			90
		};
		
		upperLegX = lL; upperLegY = uL; upperLegZ = uL;
		lowerLegX = lL; lowerLegY = lL; lowerLegZ = lL;
	}
}


/*package object.actor;

import ds.vec3;
import functions.MathExt;
import gfx.GOGL;

public class WalkCycle extends Cycle {
	
	public WalkCycle() {
		super();
		frameNum = 24;

		iniBody();
		iniLegs();
		iniArms();
	}
	
	protected void iniBody() {
		float[] b = {
			-90,-90,-87,-85,-87,-85,-82,-83,-85,-88,-84,-80,-74,-80,-84,-88,-88,-88,-92,-91,-89,-87,-86,-84
		};
		
		for(int i = 0; i < b.length; i++)
			b[i] += 90;

		bodyX = b; bodyY = b; bodyZ = b;
	}
	protected void iniArms() {
		float[] uA = {
			120,136,138,138,136,104,101,84,84,80,84,90,84,60,50,56,78,90,93,90,110,110,140,130
		},
		lA = {
			90,106,111,110,106,90,90,80,78,73,70,50,45,46,47,65,80,90,83,90,72,60,98,90
		};
		
		upperArmX = uA; upperArmY = uA; upperArmZ = uA;
		lowerArmX = lA; lowerArmY = lA; lowerArmZ = lA;
	}
	protected void iniLegs() {
		float[] uL = {
			60,63,50,50,66,74,90,101,111,116,117,104,117,12,107,90,92,90,70,60,60,59,57,57
		},
		lL = {
			55,92,91,90,98,95,95,103,108,125,145,135,145,158,167,170,170,170,170,148,148,109,115,73
		};
		
		upperLegX = lL; upperLegY = uL; upperLegZ = uL;
		lowerLegX = lL; lowerLegY = lL; lowerLegZ = lL;
	}
}*/