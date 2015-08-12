package object.environment;

import datatypes.mat4;
import datatypes.vec3;
import datatypes.vec4;
import functions.Math2D;
import gfx.GOGL;
import object.actor.Player;

public class Planet extends Heightmap {

	private float xCenter, yCenter, zCenter, radius;
	
	public Planet(float x, float y, float z, float radius, float extraRadius, String fileName) {		
		super(1, 1f/255, fileName);
		
		this.radius = radius;
		this.xCenter = x;
		this.yCenter = y;
		this.zCenter = z;
	}
	
	public boolean draw() {	
				
		GOGL.enableLighting();
		GOGL.setLightColori(128, 135, 35);
		
		vec4 vN1, vN2;
		float vD, vZD1, vZD2;
		float vR1,vR2, vX1,vY1,vZ1, vX2,vY2,vZ2;
		
		for(int y = 0; y < height-1; y++) {
			
			vZD1 = 90 - 180*y/(height-1);
			vZD2 = 90 - 180*(y+1)/(height-1);
			GOGL.begin(GOGL.P_TRIANGLE_STRIP);
			
			for(int x = 0; x <= width; x++) {
				
				if(x != width) {
					vD = 360*x/width;
					vR1 = radius+get(x,y);
					vR2 = radius+get(x,y+1);
					vN1 = new vec4(getNormal(x,y),0);
					vN2 = new vec4(getNormal(x,y+1),0);
				}
				else {
					vD = 360*x/width;
					vR1 = radius+get(0,y);
					vR2 = radius+get(0,y+1);
					vN1 = new vec4(getNormal(0,y),0);
					vN2 = new vec4(getNormal(0,y+1),0);
				}
								
				vX1 = xCenter + Math2D.calcPolarX(vR1, vD, vZD1);
				vY1 = yCenter + Math2D.calcPolarY(vR1, vD, vZD1);
				vZ1 = zCenter + Math2D.calcPolarZ(vR1, vD, vZD1);
				vX2 = xCenter + Math2D.calcPolarX(vR2, vD, vZD2);
				vY2 = yCenter + Math2D.calcPolarY(vR2, vD, vZD2);
				vZ2 = zCenter + Math2D.calcPolarZ(vR2, vD, vZD2);
				
				vN1 = vN1.mult(mat4.createRotationMatrix(0,0,vD));
				vN2 = vN2.mult(mat4.createRotationMatrix(0,0,vD));
				
				GOGL.vertex(vX1,vY1,vZ1,0,0,vN1.get(0),vN1.get(1),vN1.get(2));
				GOGL.vertex(vX2,vY2,vZ2,0,0,vN2.get(0),vN2.get(1),vN2.get(2));
			}
			
  			GOGL.end();
		}
		GOGL.setColor(1,1,1);
		GOGL.disableLighting();
		return false;
	}
}
