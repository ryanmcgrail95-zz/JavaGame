package paper;

import io.Controller;
import io.IO;
import io.Keyboard;
import resource.model.Model;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;

public class PartnerPM extends ActorPM {
	private static PartnerPM instance;
	
	private PartnerPM(float x, float y, float z) {
		super("mario",x,y,z);
	}
	
	@Override
	protected void control() {
	}

	public static PartnerPM create() {
		if(instance == null)	
			instance = new PartnerPM(0,0,0);
		return instance;
	}
	public static PartnerPM create(float x, float y, float z) {
		create().setPos(x,y,z);
		return instance;
	}
	public static PartnerPM getInstance() {
		return instance;
	}
}
