package gfx;

import java.util.HashMap;
import java.util.Map;
import com.jogamp.opengl.GL2;

public class ShaderController {
	private static Map<String, Shader> shaderMap = new HashMap<String, Shader>();
	
	public static void ini(GL2 gl) {
		addShader(gl, "Galaxy");
		addShader(gl, "Gaussian");
		addShader(gl, "Grayscale");
		addShader(gl, "Rainbow");
		addShader(gl, "Diffuse");
		addShader(gl, "Outline");
		addShader(gl, "Invert");
		addShader(gl, "Mirror");
		addShader(gl, "Ripple");
		addShader(gl, "Water");
	}

	private static void addShader(GL2 gl, String name) {
		shaderMap.put(name, new Shader(gl, name));
	}
	
	public static Shader getShader(String name) {
		return shaderMap.get(name);
	}
}
