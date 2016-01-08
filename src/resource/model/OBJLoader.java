package resource.model;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import cont.ImageLoader;
import datatypes.StringExt;
import fl.FileExt;
import gfx.GL;
import gfx.RGBA;

public final class OBJLoader {
	public static void loadInto(String name, Model mod) {
		loadModel("Resources/Models/" + name, mod);
	}
	
	private static void loadMaterials(String fileName, List<Material> mats, Model mod) {
		Material curMaterial = null;		
		File f = FileExt.getFile(fileName);
		String curDirectory = fileName.replace(f.getName(),"");
				
		try {
			String line, type;
			StringExt lineExt = new StringExt();
			BufferedReader r = new BufferedReader(new InputStreamReader(FileExt.get(fileName)));
						
			while((line = r.readLine()) != null) {
				lineExt.set(line);
				
							
				type = lineExt.chompWord(); 

				if(type.equals("newmtl")) {
					String name = lineExt.get();
					curMaterial = new Material(name);
					mats.add(curMaterial);
				}
				else if(type.equals("Ka"))
					curMaterial.setAmbient(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber(),1);
				else if(type.equals("Ks"))
					curMaterial.setSpecular(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber(),1);
				else if(type.equals("Kd"))
					curMaterial.setDiffuse(lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber(),1);
				else if(type.equals("map_Kd")) {
					String n = lineExt.chompWord();
					
					System.out.println(n);
					BufferedImage i = ImageLoader.load(curDirectory+n);
					curMaterial.setTexture(GL.createTexture(i, false));
				}
			}
			
			r.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void loadModel(String fileName, Model mod) {

		List<Material> mats = new ArrayList<Material>();
		
		List<float[]> pointList = new ArrayList<float[]>();
		List<float[]> normalList = new ArrayList<float[]>();
		List<float[]> uvList = new ArrayList<float[]>();
		List<Integer> colorList = new ArrayList<Integer>();
		List<int[]> vertexList = new ArrayList<int[]>();
		
		List<Float> vertexNums = new ArrayList<Float>();
		
		boolean hasColor = false;
		
		String materialName = "";
		
		File f = FileExt.getFile(fileName);
		String curDirectory = fileName.replace(f.getName(),"");
		
		try {
			String line, type;
			StringExt lineExt = new StringExt();
			
			BufferedReader r = new BufferedReader(new InputStreamReader(FileExt.get(fileName)));
			Material[] matsArray = new Material[0];;
			
			while((line = r.readLine()) != null) {				
				lineExt.set(line);
				
				type = lineExt.chompWord(); 

				if(type.equals("mtllib")) {
					loadMaterials(curDirectory + lineExt.get(), mats, mod);
					
					matsArray = new Material[mats.size()];
					for(int i = 0; i < mats.size(); i++)
						matsArray[i] = mats.get(i);
				}
				else if(type.equals("usemtl")) {
					materialName = lineExt.get();
										
					int matPos = -1;
					for(int i = 0; i < matsArray.length; i++)
						if(matsArray[i].checkName(materialName)) {
							matPos = i;
							break;
						}
					
					vertexList.add(new int[] {-1,matPos,-1,-1});
				}
				else if(type.equals("v")) {					
					String n;
					while((n = lineExt.chompWord()) != "")
						vertexNums.add(Float.parseFloat(n));

					pointList.add( new float[] {vertexNums.get(0),vertexNums.get(1),vertexNums.get(2),1} );

					if(vertexNums.size() > 3) {
						hasColor = true;
						colorList.add( RGBA.convertRGBA2Int((int)(vertexNums.get(3)*255),(int)(vertexNums.get(4)*255),(int)(vertexNums.get(5)*255),255) );
					}
					
					vertexNums.clear();
				}
				else if(type.equals("vt"))
					uvList.add( new float[] {lineExt.chompNumber(),lineExt.chompNumber()} );
				else if(type.equals("vn"))
					normalList.add( new float[] {lineExt.chompNumber(),lineExt.chompNumber(),lineExt.chompNumber(),0} );
				else if(type.equals("f")) {
					StringExt curSection;
					String[] indices;
					int[] face;
					
					for(int i = 0; i < 3; i++) {
						curSection = new StringExt(lineExt.chompWord());
												
						indices = curSection.split('/');
						face = new int[] {0,0,0,-1};
						
						if(hasColor) {
							for(int k = 0; k < 3; k++)
								face[k] = Integer.parseInt(indices[k])-1;
							face[3] = face[0];
						}
						else
							for(int k = 0; k < 3; k++)
								face[k] = Integer.parseInt(indices[k])-1;
						
						vertexList.add(face);
					}
				}
			}
			
			mod.create(Model.TRIANGLES, pointList, normalList, uvList, colorList, vertexList);
			mod.attachMaterials(matsArray);

			mats.clear();
			r.close();
		} catch (IOException e) {
		}
	}
}
