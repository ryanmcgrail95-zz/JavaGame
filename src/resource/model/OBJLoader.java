package resource.model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import cont.ImageLoader;
import datatypes.StringExt;
import datatypes.vec2;
import datatypes.vec3;
import datatypes.vec4;
import fl.FileExt;
import gfx.GOGL;

public final class OBJLoader {
	
	public static Model load(String name) {
		Model mod = loadModel("Resources/Models/" + name + ".obj");
		Material mat = loadMaterials("Resources/Models/" + name + ".mtl");
		mod.attachMaterials(mat);
		
		return mod;
	}
	
	private static Material loadMaterials(String fileName) {
		Material curMaterial = new Material();
		vec4 ambient, diffuse, specular;
		
		try {
			String line, type;
			StringExt lineExt = new StringExt();
			BufferedReader r = new BufferedReader(new InputStreamReader(FileExt.get(fileName)));
			
			while((line = r.readLine()) != null) {
				lineExt.set(line);
				
				type = lineExt.chompWord(); 

				if(type.equals("newmtl"))
					curMaterial = new Material();
				else if(type.equals("Ka")) {
					ambient = new vec4(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber(),1);
					curMaterial.setAmbient(ambient);
				}
				else if(type.equals("Ks")) {
					specular = new vec4(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber(),1);
					curMaterial.setSpecular(specular);
				}
				else if(type.equals("Kd")) {
					diffuse = new vec4(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber(),1);
					curMaterial.setDiffuse(diffuse);
				}
				else if(type.equals("map_Kd")) {
					BufferedImage i = ImageLoader.load("Resources/Models/"+lineExt.chompWord());
					curMaterial.setTexture(GOGL.createTexture(i, false));
				}
			}
			
			return curMaterial;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private static Model loadModel(String fileName) {

		List<vec3> pointList = new ArrayList<vec3>();
		List<vec3> normalList = new ArrayList<vec3>();
		List<vec2> uvList = new ArrayList<vec2>();
		List<vec3> vertexList = new ArrayList<vec3>();
		
		try {
			String line, type;
			StringExt lineExt = new StringExt();
			BufferedReader r = new BufferedReader(new InputStreamReader(FileExt.get(fileName)));
			
			while((line = r.readLine()) != null) {				
				lineExt.set(line);
				
				type = lineExt.chompWord(); 

				if(type.equals("v"))
					pointList.add(new vec3(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber()));
				else if(type.equals("vt"))
					uvList.add(new vec2(lineExt.chompNumber(),lineExt.chompNumber()));
				else if(type.equals("vn"))
					normalList.add(new vec3(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber()));
				else if(type.equals("f")) {
					StringExt curSection;
					String[] indices;
					vec3 face;
					
					for(int i = 0; i < 3; i++) {
						curSection = new StringExt(lineExt.chompWord());
												
						indices = curSection.split('/');
						face = new vec3();
						
						for(int k = 0; k < 3; k++)
							face.set(k, Integer.parseInt(indices[k])-1);
						
						vertexList.add(face);
					}
				}
			}
			
			return new Model(pointList, normalList, uvList, vertexList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
