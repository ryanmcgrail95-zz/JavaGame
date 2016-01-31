package resource.model;

import functions.Math2D;
import gfx.GL;
import com.jogamp.opengl.util.texture.Texture;

public class Material {
	private float[] ambient, diffuse, specular;
	private Texture tex;
	private String name;
	
	public Material(String name) {
		this.name = name;
	}
	
	public void setAmbient(float r, float g, float b, float a)	{this.ambient = new float[] {r,g,b,a};}
	public void setDiffuse(float r, float g, float b, float a)	{this.diffuse = new float[] {r,g,b,a};}
	public void setSpecular(float r, float g, float b, float a)	{this.specular = new float[] {r,g,b,a};}
	public void setTexture(Texture tex) 	{this.tex = tex;}
	
	public boolean checkName(String oName) {
		return name.equals(oName);
	}
	
	public void enable() {
		//GL2 gl = GL.getGL2();
		float s = 50 + Math2D.calcLenX(50,GL.getTime());
		float[] emission = {0,0,0,1};
		
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient, 0);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse, 0);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular, 0);
		//gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 50);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, emission, 0);

		if(tex != null) {
			GL.bind(tex);
			GL.setTextureRepeat(true);
		}
		else {
			GL.disableTextures();
			GL.enableBlending();
			GL.enableInterpolation();
		}
	}
	public void disable() {GL.unbind();}
	public String getName() {return name;}
	public Texture getTexture() {return tex;}

	public void destroy() {
		ambient = diffuse = specular = null;
		name = null;
		if(tex != null) {
			tex.destroy(GL.getGL());
			tex = null;
		}
	}
}
