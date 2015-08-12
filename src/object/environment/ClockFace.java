package object.environment;

import gfx.GOGL;


public final class ClockFace {
	
	public static float getSeconds() {
		return (float) ((System.currentTimeMillis()/1000.) % 60);
	}
	public static float getSecondHandAngle() {
		return getSeconds() / 60f*360 - 90;
	}
	
	public static float getMinutes() {
		return (float) ((System.currentTimeMillis()/(1000.*60)) % 60);
	}
	public static float getMinuteHandAngle() {
		return getMinutes() / 60f*360 - 90;
	}
	
	public static float getHours() {
		return (float) (1 + (System.currentTimeMillis()/(1000.*60*60)) % 12);
	}
	public static float getHourHandAngle() {
		return getHours() / 12f*360 - 90;
	}
	
	
	/*Clock c = new Clock();
				GOGL.transformClear();
				GOGL.transformTranslation(200, 200, 0);
				
				GOGL.transformRotationZ(c.getSecondHandAngle());				
				GOGL.drawLine(0,0,32,0);
				GOGL.transformRotationZ(-c.getSecondHandAngle()+c.getMinuteHandAngle());				
				GOGL.drawLine(0,0,38,0);
				GOGL.transformRotationZ(-c.getMinuteHandAngle()+c.getHourHandAngle());				
				GOGL.drawLine(0,0,16,0);				
				GOGL.transformClear();*/
}
