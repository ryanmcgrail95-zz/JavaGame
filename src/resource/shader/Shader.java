package resource.shader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL2;

import fl.FileExt;

public class Shader {
	
	private static Map<String, Shader> shaderMap = new HashMap<String, Shader>();
	private int vertexShader, fragmentShader, mProgram;
	
	public Shader(GL2 gl, String name) {
		
		vertexShader = loadShader(gl, gl.GL_VERTEX_SHADER, FileExt.readFile2String("Resources/Shaders/" + name + "/vertex.sh"));
		fragmentShader = loadShader(gl, gl.GL_FRAGMENT_SHADER, FileExt.readFile2String("Resources/Shaders/" + name + "/frag.sh"));

        mProgram = gl.glCreateProgram();

		gl.glAttachShader(mProgram, vertexShader);
        gl.glAttachShader(mProgram, fragmentShader);
        
        
        gl.glLinkProgram(mProgram);
        
        System.out.println(gl.glGetError() == gl.GL_NO_ERROR);

        
        gl.glValidateProgram(mProgram);
        
        
        
        IntBuffer intBuffer = IntBuffer.allocate(1);
        gl.glGetProgramiv(mProgram, gl.GL_LINK_STATUS,intBuffer);
        if (intBuffer.get(0)!=1){
           gl.glGetProgramiv(mProgram, gl.GL_INFO_LOG_LENGTH,intBuffer);
           int size = intBuffer.get(0);
           System.err.println("Program link error: ");
           if (size>0){
              ByteBuffer byteBuffer = ByteBuffer.allocate(size);
              gl.glGetProgramInfoLog(fragmentShader, size, intBuffer, byteBuffer);
              for (byte b:byteBuffer.array()){
                 System.err.print((char)b);
              }
           } else {
              System.out.println("Unknown");
           }
           System.exit(1);
        }
	}
	
		
	private int loadShader(GL2 gl, int type, String shaderCode) {
		// Create a shader of the correct type
        int shader = gl.glCreateShader(type);

        // Compile the shader from source code
        gl.glShaderSource(shader, 1, new String[] { shaderCode }, (int[]) null, 0);
        gl.glCompileShader(shader);
        
        return shader;
    }


	public int getVertexShader() {
		return vertexShader;
	}
	
	public int getFragmentShader() {
		return fragmentShader;
	}
	
	public int getProgram() {
		return mProgram;
	}
	
	
	
	// STATIC
	
	public static void ini(GL2 gl) {
		addShader(gl, "FireSprite");
		addShader(gl, "Fireball");
		addShader(gl, "FireballGaussian");
		addShader(gl, "Gaussian");
		addShader(gl, "Rainbow");
		addShader(gl, "Metal");
		addShader(gl, "Model");
		addShader(gl, "PageCurl");
		/*addShader(gl, "Galaxy");
		addShader(gl, "Grayscale");
		addShader(gl, "Rainbow");
		//addShader(gl, "Diffuse");
		addShader(gl, "Outline");
		addShader(gl, "Invert");
		addShader(gl, "Main");
		addShader(gl, "Mirror");
		//addShader(gl, "Ripple");
		addShader(gl, "Water");*/
	}
	private static void addShader(GL2 gl, String name) {
		shaderMap.put(name, new Shader(gl, name));
	}
	public static Shader getShader(String name) {
		return shaderMap.get(name);
	}
}
