package object.environment;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import functions.Math2D;
import gfx.GOGL;

import com.jogamp.opengl.util.texture.Texture;

import object.primitive.Drawable;
import object.primitive.Environmental;


public class Floor extends FloorBlock {
	private static List<Floor> floorList = new ArrayList<Floor>();
	
	public Floor(float x1, float y1, float x2, float y2, float z, Texture tex) {
		super(x1, y1, z, x2, y2, z, tex);
		
		floorList.add(this);
	}
	
	//PARENT FUNCTIONS
		public void draw() {
			GOGL.draw3DFloor(x1,y1,x2,y2,z1,null);
		}
		
		
	//GLOBAL FUNCTIONS
		public static float findGroundZ(float x, float y, float curZ) {
			//Finds Z value of floor directly below point.
			
			float groundZ = -100000;
			
			for(Floor f : floorList)
				if(Math2D.checkRectangle(x,y, f.x1,f.y1,f.x2,f.y2)) {
					if(groundZ < f.z1 && f.z1 <= curZ)
						groundZ = f.z1;
					
					if(groundZ == curZ)
						break;
				}
			
			return groundZ;
		}
}
