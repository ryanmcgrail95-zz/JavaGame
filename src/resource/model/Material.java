package resource.model;

import functions.Math2D;
import gfx.GOGL;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;

import datatypes.vec3;
import datatypes.vec4;

public class Material {
	private float[] ambient, diffuse, specular;
	private Texture tex;
	private String name;
	
	public Material(String name) {
		this.name = name;
	}
	public Material(String name, vec4 ambient, vec4 diffuse, vec4 specular, Texture tex) {
		this.name = name;
		setAmbient(ambient);
		setDiffuse(diffuse);
		setSpecular(specular);
		setTexture(tex);
	}
	
	public void setAmbient(vec4 ambient) 	{this.ambient = ambient.getArray();}
	public void setDiffuse(vec4 diffuse) 	{this.diffuse = diffuse.getArray();}
	public void setSpecular(vec4 specular)	{this.specular = specular.getArray();}
	public void setTexture(Texture tex) 	{this.tex = tex;}
	
	public boolean checkName(String oName) {
		return name.equals(oName);
	}
	
	public void enable() {
		GL2 gl = GOGL.gl;
		float s = 50 + Math2D.calcLenX(50,GOGL.getTime());
		float[] emission = {0,0,0,1};
		
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, ambient, 0);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, diffuse, 0);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specular, 0);
		//gl.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 50);
		//gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_EMISSION, emission, 0);

		if(tex != null) {
			GOGL.bind(tex);
			GOGL.enableTextureRepeat();
		}
		else {
			GOGL.disableTextures();
			GOGL.enableBlending();
			GOGL.enableInterpolation();
		}
	}
	public void disable() {
		GL2 gl = GOGL.gl;
		GOGL.unbind();
	}
	public String getName() {
		return name;
	}
	public Texture getTexture() {
		return tex;
	}
}
