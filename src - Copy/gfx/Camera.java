package gfx;

import obj.prim.Drawable;
import obj.prim.Instantiable;

public class Camera {
	//CAMERA FOCUS
	private static final int CF_OBJECT = 1;
	private static int camFocusType;
	private static Instantiable camFocusObject;
	
	public static void focusObject(Drawable instance) {
		camFocusType = CF_OBJECT;
		camFocusObject = instance;
	}
}
