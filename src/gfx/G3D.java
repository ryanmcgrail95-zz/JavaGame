package gfx;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import functions.Math3D;
import resource.model.Model;

public class G3D extends GL {
	private G3D() {}
	
	// Sphere-Drawing Functions
		public static void draw3DSphere(float x, float y, float z, float r, int numPts) {draw3DSphere(x,y,z,r,null,numPts);}
		public static void draw3DSphere(float x, float y, float z, float r, Texture tex, int numPts) {draw3DSphere(x,y,z,r,r,tex,numPts);}
		public static void draw3DSphere(float x, float y, float z, float hr, float vr, Texture tex, int numPts) {
			if(numPts < 3)
				return;
			
			bind(tex);
			
			float dir, zDir, xN, yN, zN;
			
			for(int d = 0; d < numPts-1; d++) {
				
				gl.glBegin(GL2.GL_TRIANGLE_STRIP);
				
				for(int i = 0; i < numPts; i++) {
					dir = 360.f*i/(numPts-1);
					
					for(int ii = 0; ii < 2; ii++) {
						zDir = 360.f*(d+ii)/(numPts-1);
						
						xN = Math3D.calcPolarX(dir,zDir);
						yN = Math3D.calcPolarY(dir,zDir);
						zN = Math3D.calcPolarZ(dir,zDir);
						
						gl.glTexCoord2d(i/(numPts-1), (d+ii)/(numPts-1));
							gl.glVertex3f(x+xN*hr,y+yN*hr,z+zN*vr);
					}
				}
				
		        gl.glEnd();
			}
			
			unbind();
		}
}
