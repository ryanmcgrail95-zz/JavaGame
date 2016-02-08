package paper;

import cont.Text;
import io.Controller;
import io.IO;
import io.Keyboard;
import resource.model.Model;
import gfx.GL;
import gfx.GT;
import gfx.RGBA;

public class PartnerPM extends ActorPM {
	private static PartnerPM instance;
	private boolean isActing = false;
	
	private static String[] partners = {"mario", "luigi", "watt"};
	private static int currentPartnerNum = 0;
	
	private PartnerPM(String type, float x, float y, float z) {
		super(type,x,y,z);
		followActor = PlayerPM.getInstance();
		
		//setSurviveTransition(true);
	}
	
	public void destroy() {
		super.destroy();
		instance = null;
	}
	
	public void update() {
		super.update();
		
		if(IO.checkPressed(IO.C_DOWN))
			Text.createTextDialog("HELLO!\nline2\nline3\nline4\nUhoh");
		if(IO.checkPressed(IO.C_RIGHT))
			switchCharacter(partners[currentPartnerNum = (currentPartnerNum+1) % partners.length]);
	}
	
	protected void startSwitching() {
		followActor.setCanControl(false);		
		followActor.stop();

		ActorPM.setCanSeeAll(false);
	}
	protected void endSwitching() {
		followActor.setCanControl(true);
		
		ActorPM.setCanSeeAll(true);
	}
	
	@Override
	protected void control() {}

	public static PartnerPM create() {return create(partners[currentPartnerNum]);}
	public static PartnerPM create(String type) {
		if(instance == null)	
			instance = new PartnerPM(type,0,0,0);
		return instance;
	}
	public static PartnerPM create(String type, float x, float y, float z) {
		create(type).setPos(x,y,z);
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
