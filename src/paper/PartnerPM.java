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
	
	private PartnerPM(String type, float x, float y, float z) {
		super(type,x,y,z);
		followActor = PlayerPM.getInstance();
	}
	
	@Override
	protected void control() {}

	public static PartnerPM create() {return create("luigi");}
	public static PartnerPM create(String type) {
		if(instance == null)	
			instance = new PartnerPM(type,0,0,0);
		return instance;
	}
	public static PartnerPM create(String type, float x, float y, float z) {
		create(type).setPos(x,y,z);
		return instance;
	}
	public static PartnerPM getInstance() {
		return instance;
	}
}
