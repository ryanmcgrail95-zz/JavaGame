package resource.model;

import gfx.RGBA;

import java.io.File;
import java.io.IOException;
 
import java.util.ArrayList;
import java.util.List;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;
 
public class PLYLoader { 
	public static Model loadPLY(File file) throws IOException {		
		List<float[]> pointList = new ArrayList<float[]>();
		List<int[]> vertexList = new ArrayList<int[]>();
		List<Integer> colorList = new ArrayList<Integer>();
		
		PlyReader ply = new PlyReaderFile(file);
 
		int vertexCount = ply.getElementCount("vertex");
		int triangleCount = ply.getElementCount("face");
		
		ElementReader reader;
		
 
		/* Iterate to read elements */
		while ((reader = ply.nextElementReader()) != null) {
			String elementType = reader.getElementType().getName();
 
			if (elementType.equals("vertex")) {
				Element element;
				int x = 0;
				while ((element = reader.readElement()) != null) {
                                   /* manipulated array indexes to store  */
					pointList.add( new float[] {
							(float) element.getDouble("x"),
							(float) element.getDouble("y"),
							(float) element.getDouble("z")
						}
					);
					colorList.add( RGBA.convertRGBA2Int(
						(int) element.getDouble("red"),
						(int) element.getDouble("green"),
						(int) element.getDouble("blue"),
						255)
					);
					x++;
				} 
			} else if (elementType.equals("face")) {
				
				Element element;
				int x = 0;
				while ((element = reader.readElement()) != null) {
					int[] vertex_index = null;
 
					try {
						vertex_index = element.getIntList("vertex_indices");
					}
					
					catch (Exception e) {}
 
					if(vertex_index == null) {
						try {
							vertex_index = element.getIntList("vertex_index");
						}
						
						catch (Exception e) {}
					}
 
					if(vertex_index == null) {
						throw new IOException("Failed to read vertices");
					}

					vertexList.add( new int[] {
							vertex_index[0],
							vertex_index[1],
							vertex_index[2]
						}
					);
					x++;
				}
			}
 
			reader.close();
		}
 
		ply.close();
 
 
		/* created mesh object to include PLY object in JME 3 environment */
		return new Model(Model.TRIANGLES, pointList, new ArrayList<float[]>(), new ArrayList<float[]>(), colorList, vertexList);
	}	
}